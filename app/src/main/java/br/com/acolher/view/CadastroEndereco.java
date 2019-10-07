package br.com.acolher.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import br.com.acolher.model.ViaCep;
import br.com.acolher.service.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroEndereco extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "API";
    //private LocationManager locationManager;
    private Address address;
    //private Location location;
    private double latitude;
    private double longitude;

    private GoogleApiClient googleApiClient;
    private Button pesquisarEndereco, btnFinalizarCadastro, btnBuscaCep;
    private FusedLocationProviderClient fusedLocation;
    private TextInputLayout inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    private EnderecoController ec;
    private RetrofitInit retrofitInit = new RetrofitInit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_endereco);

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        findBydId();

        if(googleApiClient == null){

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        pesquisarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GetLocalization(CadastroEndereco.this)) {
                    if (ActivityCompat.checkSelfPermission(CadastroEndereco.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(CadastroEndereco.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }else{
                        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        fusedLocation.getLastLocation().addOnSuccessListener(CadastroEndereco.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                    try {
                                        address = Validacoes.buscarEndereco(latitude, longitude, getApplicationContext());
                                        inputRua.getEditText().setText(address.getThoroughfare());
                                        inputCep.getEditText().setText(address.getPostalCode());
                                        inputBairro.getEditText().setText(address.getSubLocality());
                                        inputUF.getEditText().setText(address.getLocality());
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                    /*if(location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.i("getCoordenadas", latitude + "-" + longitude);
                    }
                    try{
                        address = buscarEndereco(latitude, longitude);
                        if(address != null){
                          rua.getEditText().setText(address.getThoroughfare());
                          cep.getEditText().setText(address.getPostalCode());
                          bairro.getEditText().setText(address.getSubLocality());
                        }
                        //Toast.makeText(CadastroEndereco.this, address.getLocality(), Toast.LENGTH_LONG).show();
                    }catch (IOException e){
                        e.printStackTrace();
                    }*/

                    /*fusedLocation.getLastLocation().addOnSuccessListener(CadastroEndereco.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Toast.makeText(CadastroEndereco.this, location.getLatitude() + " - " + location.getLongitude(), Toast.LENGTH_LONG).show();
                        }
                    });*/
                }
            }
        });

        btnFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ec = new EnderecoController();
                if (validateForm()){
                    Endereco endereco = new Endereco();

                    endereco.setCep(Validacoes.cleanCep(inputCep.getEditText().getText().toString()));
                    endereco.setLogradouro(inputRua.getEditText().getText().toString());
                    endereco.setNumero(inputNumero.getEditText().getText().toString());
                    endereco.setBairro(inputBairro.getEditText().getText().toString());
                    endereco.setCidade(inputCidade.getEditText().getText().toString());
                    endereco.setUf(inputUF.getEditText().getText().toString());

                    cadastroEndereco(endereco);
                }
            }
        });

        btnBuscaCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
                buscaCep(cep);
            }
        });

    }

    private void buscaCep(String cep) {
        if(EnderecoController.empty(cep)){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://viacep.com.br/ws/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ServiceApi api = retrofit.create(ServiceApi.class);

            Call<ViaCep> retorno = api.buscarCEP(cep);
            retorno.enqueue(new Callback<ViaCep>() {
                @Override
                public void onResponse(Call<ViaCep> call, Response<ViaCep> response) {
                    Log.d(TAG, String.valueOf(response.code()));
                    ViaCep endereco = response.body();
                    inputRua.getEditText().setText(endereco.getLogradouro());
                    inputBairro.getEditText().setText(endereco.getBairro());
                    inputUF.getEditText().setText(endereco.getUf());
                    inputCidade.getEditText().setText(endereco.getLocalidade());
                }

                @Override
                public void onFailure(Call<ViaCep> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }else{
            inputCep.setError("Campo Obrigatorio");
        }
    }

    private void findBydId() {
        pesquisarEndereco =  findViewById(R.id.btnSearchLocale);
        btnBuscaCep = findViewById(R.id.btnBuscaCep);
        inputRua = findViewById(R.id.inputRua);
        inputCep =  findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));
        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);
        inputBairro = findViewById(R.id.inputBairro);
        inputNumero = findViewById(R.id.inputNumero);
        inputUF = findViewById(R.id.inputUF);
        inputCidade = findViewById(R.id.inputCidade);
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("location", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("location", "ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("location", "ConnectionFailed");
    }

    public boolean GetLocalization(Context context){
        int REQUEST_PERMISSION_LOCALIZATION = 221;
        boolean res=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                res = false;
                ActivityCompat.requestPermissions((Activity) context, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCALIZATION);

            }
        }
        return res;
    }

    public void showSettingsAlert(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastroEndereco.this);
        alertDialog.setTitle("GPS");
        alertDialog.setMessage("GPS não está habilitado. Deseja configurar?");
        alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                CadastroEndereco.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

    /*public Address buscarEndereco(Double latitude, Double longitude) throws IOException{

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if(addresses.size() > 0){
            address = addresses.get(0);
        }

        return  address;
    }*/

    public boolean validateForm(){

        String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
        String rua = inputRua.getEditText().getText().toString();
        String numero = inputNumero.getEditText().getText().toString();
        String bairro = inputBairro.getEditText().getText().toString();
        String uf = inputUF.getEditText().getText().toString();
        String cidade = inputCidade.getEditText().getText().toString();

        if(ec.validaCep(cep) != ""){
            inputCep.setError(ec.validaCep(cep));
            return false;
        }

        if(!EnderecoController.empty(rua)){
            inputRua.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(numero)){
            inputNumero.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(bairro)){
            inputBairro.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(cidade)){
            inputCidade.setError("Campo Obrigatorio");
            return false;
        }

        if(EnderecoController.validaUF(uf) != ""){
            inputUF.setError(EnderecoController.validaUF(uf));
            return false;
        }

        return true;
    }

    private void cadastroEndereco(Endereco endereco){
        Call<Endereco> cadastroEndereco = retrofitInit.getService().cadastroEndereco(endereco);
        cadastroEndereco.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    chamarProximaTela(response.body().getCodigo());
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                }

            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void chamarProximaTela(Integer codigoEndereco) {
        Intent intent = getIntent();
        String perfil = intent.getStringExtra("perfil");

        if(perfil.equals("instituicao")){
            Intent intentCadastroInstituicao = new Intent(CadastroEndereco.this, CadastroInstituicao.class);
            intentCadastroInstituicao.putExtra("codigoEndereco", codigoEndereco);
            startActivity(intentCadastroInstituicao);
        }else{
            Intent intentCadastroUsuario = new Intent(CadastroEndereco.this, CadastroActivity.class);
            intentCadastroUsuario.putExtra("perfil", perfil);
            intentCadastroUsuario.putExtra("codigoEndereco", codigoEndereco);
            startActivity(intentCadastroUsuario);
        }
    }
}