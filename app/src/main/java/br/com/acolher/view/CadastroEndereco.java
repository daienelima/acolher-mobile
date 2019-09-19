package br.com.acolher.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroEndereco extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String ERRO = "ERRO";
    private LocationManager locationManager;
    private Address address;
    private Location location;
    private double latitude;
    private double longitude;

    Spinner spinnerEstados;
    ArrayAdapter<CharSequence> adapterSpinnerEstados;
    GoogleApiClient googleApiClient;
    Button pesquisarEndereco;
    FusedLocationProviderClient fusedLocation;
    TextInputLayout inputRua;
    TextInputLayout inputCep;
    TextInputLayout inputNumero;
    TextInputLayout inputBairro;

    Button btnFinalizarCadastro;

    EnderecoController ec;
    private Endereco endereco = new Endereco();
    private Instituicao instituicao = new Instituicao();
    private RetrofitInit retrofitInit = new RetrofitInit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.cadastro_endereco_activity);

        spinnerEstados  = (Spinner) findViewById(R.id.listaEstados);
        adapterSpinnerEstados = ArrayAdapter.createFromResource(this, R.array.spinner_estados, android.R.layout.simple_spinner_item);
        adapterSpinnerEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstados.setAdapter(adapterSpinnerEstados);

        pesquisarEndereco = (Button) findViewById(R.id.btnSearchLocale);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        inputRua = (TextInputLayout) findViewById(R.id.inputRua);

        inputCep = (TextInputLayout) findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));

        btnFinalizarCadastro = (Button) findViewById(R.id.btnFinalizarCadastro);

        inputBairro = (TextInputLayout) findViewById(R.id.inputBairro);

        inputNumero = (TextInputLayout) findViewById(R.id.inputNumero);

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
                                        address = buscarEndereco(latitude, longitude);
                                        inputRua.getEditText().setText(address.getThoroughfare());
                                        inputCep.getEditText().setText(address.getPostalCode());
                                        inputBairro.getEditText().setText(address.getSubLocality());
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

                    endereco.setBairro(inputBairro.toString());
                    endereco.setCep(inputCep.toString());
                    //endereco.setCidade();
                    endereco.setEstado(spinnerEstados.getSelectedItem().toString());
                    endereco.setLatitude(Double.toString(latitude));
                    endereco.setLongitude(Double.toString(longitude));
                    endereco.setRua(inputRua.toString());
                    endereco.setNumero(inputNumero.toString());

                    Log.d(ERRO, endereco.toString());

                    cadastroEndereco(endereco);

                    Intent intent = getIntent();
                    instituicao.setAtivo(true);
                    instituicao.setNome(intent.getStringExtra("nomeInstituicao"));
                    instituicao.setCnpj(intent.getStringExtra("cnpjInstituicao"));
                    instituicao.setTelefone(intent.getStringExtra("telefoneInstituicao"));
                    instituicao.setEmail(intent.getStringExtra("emailInstituicao"));
                    instituicao.setSenha(intent.getStringExtra("passwordInstituicao"));

                    cadastroInstituicao(instituicao);

                }
            }
        });

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

    public Address buscarEndereco(Double latitude, Double longitude) throws IOException{

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if(addresses.size() > 0){
            address = addresses.get(0);
        }

        return  address;
    }

    public boolean validateForm(){

        String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
        String rua = inputRua.getEditText().getText().toString();
        String numero = inputNumero.getEditText().getText().toString();
        String bairro = inputBairro.getEditText().getText().toString();

        if(ec.validaCep(cep) != ""){
            inputCep.getEditText().setError(ec.validaCep(cep));
            return false;
        }

        if(ec.validaBairro(bairro) != ""){
            inputBairro.getEditText().setError(ec.validaBairro(bairro));
            return false;
        }

        if(ec.validaNumero(numero) != ""){
            inputNumero.getEditText().setError(ec.validaNumero(numero));
            return false;
        }

        if(ec.validaRua(rua) != ""){
            inputRua.getEditText().setError(ec.validaRua(rua));
            return false;
        }

        if(ec.validaEstado(spinnerEstados.getSelectedItem().toString()) != ""){
            ((TextView)spinnerEstados.getSelectedView()).setError(ec.validaEstado(spinnerEstados.getSelectedItem().toString()));
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
                    int status = response.code();
                    Log.d(ERRO, String.valueOf(status));
                    instituicao.setEndereco(response.body());
                } else {
                    Log.d(ERRO, "erro");
                    Log.d(ERRO, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.d(ERRO, t.getMessage());
            }
        });

    }

    private void cadastroInstituicao(Instituicao instituicao){
        Call<Instituicao> cadastroInstituicao = retrofitInit.getService().cadastroInstituicao(instituicao);
        cadastroInstituicao.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if (response.isSuccessful()) {
                    int status = response.code();
                    Log.d("testeInstituicao", String.valueOf(status));
                    Log.d("testeInstituicao", "erro");
                    Log.d("testeInstituicao", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

    }
}
