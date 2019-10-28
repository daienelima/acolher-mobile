package br.com.acolher.view;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.helper.Helper;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Usuario;
import br.com.acolher.model.ViaCep;
import br.com.acolher.service.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CadastroActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private Address address;
    private double latitude, longitude = 0;
    private FusedLocationProviderClient fusedLocation;
    private GoogleApiClient googleApiClient;
    private ImageButton btnCalendar;
    private Button pesquisarEndereco, btnFinalizarCadastro, btnBuscaCep;
    private TextInputLayout inputDataNasc, inputPassword, inputTelefone, inputCpf, inputCRM_CRP, inputNome, inputEmail;
    private TextInputLayout inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    private UsuarioController uc;
    private EnderecoController ec;
    private String nome, data, email, password, cpf, telefone, crpCrm;
    private String cep, rua, bairro, cidade, uf, numero;
    private boolean hasCrpCrm;
    private RetrofitInit retrofitInit = new RetrofitInit();
    private Usuario usuario = new Usuario();
    private Endereco endereco = new Endereco();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        if(googleApiClient == null){

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(CadastroActivity.this)
                    .addOnConnectionFailedListener(CadastroActivity.this)
                    .addApi(LocationServices.API)
                    .build();
        }

        hasCrpCrm = false;
        //Configurações da activity
        setContentView(R.layout.activity_cadastro_basico);
        findById();

        Intent intent = getIntent();
        if(intent.getStringExtra(CONSTANTES.PERFIL) != null){
            if(intent.getStringExtra(CONSTANTES.PERFIL).equals(CONSTANTES.PROFISSIONAL)) {
                hasCrpCrm = true;
                inputCRM_CRP.setVisibility(View.VISIBLE);
            }
        }

        btnCalendar.setOnClickListener(view -> openCalendar());

        inputDataNasc.getEditText().setOnFocusChangeListener((view, b) -> {
            if (b){
                inputDataNasc.getEditText().clearFocus();
                openCalendar();
            }
        });

        pesquisarEndereco.setOnClickListener(view -> {
            if (GetLocalization(CadastroActivity.this)) {
                if (ActivityCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(CadastroActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }else{
                    fusedLocation.getLastLocation().addOnSuccessListener(CadastroActivity.this, location -> {
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            try {
                                address = Validacoes.buscarEndereco(latitude, longitude, getApplicationContext());
                                inputRua.getEditText().setText(address.getThoroughfare());
                                inputCep.getEditText().setText(address.getPostalCode());
                                inputBairro.getEditText().setText(address.getSubLocality());
                                inputCidade.getEditText().setText(address.getSubAdminArea());
                                inputUF.getEditText().setText(Validacoes.deParaEstados(address.getAdminArea()));
                            }catch (IOException e){
                                Log.d(CONSTANTES.TAG, e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        btnFinalizarCadastro.setOnClickListener(view -> {
            uc = new UsuarioController();
            ec = new EnderecoController();
            if(validateForm()) {
                if(Helper.getSharedPreferences(CONSTANTES.LAT_END, "", 2, CadastroActivity.this) != ""){
                    endereco.setLatitude((String)Helper.getSharedPreferences(CONSTANTES.LAT_END, "", 2, CadastroActivity.this));
                    endereco.setLongitude((String)Helper.getSharedPreferences(CONSTANTES.LON_END, "", 2, CadastroActivity.this));
                    Helper.removeSharedPreferences(CONSTANTES.LAT_END, CadastroActivity.this);
                    Helper.removeSharedPreferences(CONSTANTES.LON_END, CadastroActivity.this);
                    //Montar Endereço
                    endereco.setCep(Validacoes.cleanCep(inputCep.getEditText().getText().toString()));
                    endereco.setLogradouro(inputRua.getEditText().getText().toString());
                    endereco.setNumero(inputNumero.getEditText().getText().toString());
                    endereco.setBairro(inputBairro.getEditText().getText().toString());
                    endereco.setCidade(inputCidade.getEditText().getText().toString());
                    endereco.setUf(inputUF.getEditText().getText().toString());

                    //Montar Usuario
                    usuario.setNome_completo(nome);
                    usuario.setData_nascimento(data);
                    usuario.setCpf(cpf);
                    usuario.setTelefone(telefone);
                    usuario.setEmail(email);
                    usuario.setPassword(password);

                    if (hasCrpCrm) {
                        usuario.setCrm_crp(crpCrm);
                    } else {
                        usuario.setCrm_crp(CONSTANTES.VAZIO);
                    }

                    cadastroEndereco(endereco);
                }else {
                    String locationName = Validacoes.deParaUf(inputUF.getEditText().getText().toString()) + ", " + inputBairro.getEditText().getText().toString();
                    LatLng focoMap = Helper.getAddressForLocationName(locationName, CadastroActivity.this);
                    try {
                        Helper.openModalMap(CadastroActivity.this, focoMap);
                        return;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnBuscaCep.setOnClickListener(v -> {
            String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
            buscaCep(cep);
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

    private void findById() {
        inputDataNasc = findViewById(R.id.inputDataNasc);
        btnCalendar = findViewById(R.id.btnCalendar);
        inputPassword = findViewById(R.id.inputPassword);
        inputCpf = findViewById(R.id.inputCPFCad);
        inputCpf.getEditText().addTextChangedListener(MaskWatcher.buildCpf());
        inputCRM_CRP = findViewById(R.id.inputCRM);
        inputCRM_CRP.setVisibility(View.GONE);
        inputTelefone = findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));
        inputNome = findViewById(R.id.inputNomeCompleto);
        inputEmail = findViewById(R.id.inputEmail);
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
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    inputRua.getEditText().setText(response.body().getLogradouro());
                    inputBairro.getEditText().setText(response.body().getBairro());
                    inputUF.getEditText().setText(response.body().getUf());
                    inputCidade.getEditText().setText(response.body().getLocalidade());
                }

                @Override
                public void onFailure(Call<ViaCep> call, Throwable t) {
                    Log.d(CONSTANTES.TAG, t.getMessage());
                }
            });
        }else{
            inputCep.setError(CONSTANTES.CAMPO_OBRIGATORIO);
        }
    }

    private void cadastroEndereco(Endereco endereco){
        Call<Endereco> cadastroEndereco = retrofitInit.getService().cadastroEndereco(endereco);
        cadastroEndereco.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    usuario.setEndereco(response.body());
                    cadastroUsuario(usuario);
                } else {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
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

    public void openCalendar(){

        inputDataNasc.setErrorEnabled(false);
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CadastroActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputDataNasc.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        data = inputDataNasc.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        cpf = Validacoes.cleanCPF(inputCpf.getEditText().getText().toString());
        telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());
        crpCrm = inputCRM_CRP.getEditText().getText().toString();
        cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
        rua = inputRua.getEditText().getText().toString();
        numero = inputNumero.getEditText().getText().toString();
        bairro = inputBairro.getEditText().getText().toString();
        uf = inputUF.getEditText().getText().toString();
        cidade = inputCidade.getEditText().getText().toString();

        if(uc.validarNome(nome) != CONSTANTES.VAZIO){
            inputNome.setError(uc.validarNome(nome));
            return false;
        }

        if(!UsuarioController.empty(data)){
            inputDataNasc.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }

        if(!UsuarioController.validaEmail(email)){
            inputEmail.setError(CONSTANTES.EMAIL_INVALIDO);
            return false;
        }

        if(uc.validaPassword(password) != CONSTANTES.VAZIO){
            inputPassword.setError(uc.validaPassword(password));
            return false;
        }

        if(uc.validarTelefone(telefone) != CONSTANTES.VAZIO){
            inputTelefone.setError(uc.validarTelefone(telefone));
            return false;
        }

        if(uc.validaCpf(cpf) != CONSTANTES.VAZIO){
            inputCpf.setError(uc.validaCpf(cpf));
            return false;
        }

        if(hasCrpCrm){
            if(UsuarioController.empty(crpCrm)){
                inputCRM_CRP.setError(CONSTANTES.CAMPO_OBRIGATORIO);
                return false;
            }
        }

        if(ec.validaCep(cep) != CONSTANTES.VAZIO){
            inputCep.setError(ec.validaCep(cep));
            return false;
        }

        if(!EnderecoController.empty(rua)){
            inputRua.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(numero)){
            inputNumero.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(bairro)){
            inputBairro.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(cidade)){
            inputCidade.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }

        if(EnderecoController.validaUF(uf) != CONSTANTES.VAZIO){
            inputUF.setError(EnderecoController.validaUF(uf));
            return false;
        }

        return true;
    }

    private void cadastroUsuario(Usuario usuario){
        Call<Usuario> cadastroUsuario = retrofitInit.getService().cadastroUsuario(usuario);
        cadastroUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));

                    String tipoUsuario = "";
                    if(response.body().getCrm_crp().isEmpty()){
                        tipoUsuario = CONSTANTES.PACIENTE;
                    }else{
                        tipoUsuario = CONSTANTES.VOLUNTARIO;
                    }
                    salvarDadosUsuario(response.body().getCodigo(), tipoUsuario);

                    Intent home = new Intent(CadastroActivity.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        if(response.errorBody().contentLength() == 18){
                            msgJaCadastrado("CPF");
                        }
                        if(response.errorBody().contentLength() == 21){
                            msgJaCadastrado("E-mail");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    public void msgJaCadastrado(String campo){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastroActivity.this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage(campo + " " + "já cadastrado.");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());

        // visualizacao do dialogo
        alertDialog.show();
    }

    public void salvarDadosUsuario(Integer codigoUsuario, String tipoUsuario) {
        sharedPreferences = this.getSharedPreferences(CONSTANTES.USERDATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(CONSTANTES.USERCODE, codigoUsuario);
        editor.putString(CONSTANTES.TYPE, tipoUsuario);
        editor.apply();
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
}