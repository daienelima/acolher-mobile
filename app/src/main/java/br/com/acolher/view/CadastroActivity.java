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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.helper.FireStore;
import br.com.acolher.helper.Helper;
import br.com.acolher.helper.MaskEditUtil;
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

    Calendar calendar;
    DatePickerDialog datePickerDialog;
    Address address;
    double latitude, longitude = 0;
    FusedLocationProviderClient fusedLocation;
    GoogleApiClient googleApiClient;
    ImageButton btnCalendar;
    Button pesquisarEndereco, btnFinalizarCadastro, btnBuscaCep;
    TextInputLayout inputDataNasc, inputPassword, inputTelefone, inputCpf, inputCRM_CRP, inputNome, inputEmail;
    TextInputLayout inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    String nome, data, email, password, cpf, telefone, crpCrm;
    String cep, rua, bairro, cidade, uf, numero;
    boolean hasCrpCrm;
    RetrofitInit retrofitInit = new RetrofitInit();
    Usuario usuario = new Usuario();
    Endereco endereco = new Endereco();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    UsuarioController usuarioController;
    EnderecoController enderecoController;
    TextView voltarPagina;

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
        setContentView(R.layout.activity_cadastro_basico);
        findById();

        Intent intent = getIntent();
        if(intent.getStringExtra(CONSTANTES.PERFIL).equals(CONSTANTES.PROFISSIONAL)) {
            hasCrpCrm = true;
            inputCRM_CRP.setVisibility(View.VISIBLE);
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
                                endereco.setLongitude(String.valueOf(longitude));
                                endereco.setLatitude(String.valueOf(latitude));
                            }catch (IOException e){
                                Log.d(CONSTANTES.TAG, e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        btnFinalizarCadastro.setOnClickListener(view -> {
            if(validateForm()) {
                endereco.setCep(Validacoes.cleanCep(inputCep.getEditText().getText().toString()));
                endereco.setLogradouro(inputRua.getEditText().getText().toString());
                endereco.setNumero(inputNumero.getEditText().getText().toString());
                endereco.setBairro(inputBairro.getEditText().getText().toString());
                endereco.setCidade(inputCidade.getEditText().getText().toString());
                endereco.setUf(inputUF.getEditText().getText().toString());

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
                getAddressByParameters(endereco);
            }
        });

        btnBuscaCep.setOnClickListener(v -> {
            String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
            if(cep.length() == 8){
                buscaCep(cep);
            }else{
                inputCep.setError("CEP Invalido");
            }
        });
        voltarPagina.setOnClickListener(view -> {
            Intent paginaLogin = new Intent(CadastroActivity.this, Login.class);
            startActivity(paginaLogin);
            finish();
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
        inputCpf.getEditText().addTextChangedListener(MaskEditUtil.mask(inputCpf.getEditText(), MaskEditUtil.FORMAT_CPF));
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
        inputCep.getEditText().addTextChangedListener(MaskWatcher.buildCep());
        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);
        inputBairro = findViewById(R.id.inputBairro);
        inputNumero = findViewById(R.id.inputNumero);
        inputUF = findViewById(R.id.inputUF);
        inputCidade = findViewById(R.id.inputCidade);
        voltarPagina = findViewById(R.id.labelRetornarLogin);
    }

    private void buscaCep(String cep) {
        Toast.makeText(CadastroActivity.this, cep, Toast.LENGTH_LONG).show();
        if(!cep.isEmpty() && !cep.equals(null)){
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
        datePickerDialog = new DatePickerDialog(CadastroActivity.this, (datePicker, mYear, mMonth, mDay) -> inputDataNasc.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear), year, month, day);
        datePickerDialog.show();
    }

    public boolean validateForm(){
        usuarioController = new UsuarioController();
        enderecoController = new EnderecoController();
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

        if(usuarioController.validarNome(nome) != CONSTANTES.VAZIO){
            inputNome.setError(usuarioController.validarNome(nome));
            return false;
        }else{
            inputNome.setError(null);
            inputNome.clearFocus();
        }

        if(!UsuarioController.empty(data)){
            inputDataNasc.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }else{
            inputDataNasc.setError(null);
            inputDataNasc.clearFocus();
        }

        if(!UsuarioController.validaEmail(email)){
            inputEmail.setError(CONSTANTES.EMAIL_INVALIDO);
            return false;
        }else{
            inputEmail.setError(null);
            inputEmail.clearFocus();
        }

        if(usuarioController.validaPassword(password) != CONSTANTES.VAZIO){
            inputPassword.setError(usuarioController.validaPassword(password));
            return false;
        }else{
            inputPassword.setError(null);
            inputPassword.clearFocus();
        }

        if(usuarioController.validarTelefone(telefone) != CONSTANTES.VAZIO){
            inputTelefone.setError(usuarioController.validarTelefone(telefone));
            return false;
        }else{
            inputTelefone.setError(null);
            inputTelefone.clearFocus();
        }

        if(usuarioController.validaCpf(cpf) != CONSTANTES.VAZIO){
            inputCpf.setError(usuarioController.validaCpf(cpf));
            return false;
        }else{
            inputCpf.setError(null);
            inputCpf.clearFocus();
        }

        if(hasCrpCrm){
            if(!UsuarioController.empty(crpCrm)){
                inputCRM_CRP.setError(CONSTANTES.CAMPO_OBRIGATORIO);
                return false;
            }else{
                inputCRM_CRP.setError(null);
                inputCRM_CRP.clearFocus();
            }
        }

        if(enderecoController.validaCep(cep) != CONSTANTES.VAZIO){
            inputCep.setError(enderecoController.validaCep(cep));
            return false;
        }else{
            inputCep.setError(null);
            inputCep.clearFocus();
        }

        if(!EnderecoController.empty(rua)){
            inputRua.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }else{
            inputRua.setError(null);
            inputRua.clearFocus();
        }

        if(!EnderecoController.empty(numero)){
            inputNumero.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }else{
            inputNumero.setError(null);
            inputNumero.clearFocus();
        }

        if(!EnderecoController.empty(bairro)){
            inputBairro.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }else{
            inputBairro.setError(null);
            inputBairro.clearFocus();
        }

        if(!EnderecoController.empty(cidade)){
            inputCidade.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return false;
        }else{
            inputCidade.setError(null);
            inputCidade.clearFocus();
        }

        if(EnderecoController.validaUF(uf) != CONSTANTES.VAZIO){
            inputUF.setError(EnderecoController.validaUF(uf));
            return false;
        }else{
            inputUF.setError(null);
            inputUF.clearFocus();
        }
        return true;
    }

    private void getAddressByParameters(Endereco address){
        Call<Endereco> getByParameters = retrofitInit.getService().getAddressByParameters(address);
        getByParameters.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if(response.isSuccessful()){
                    usuario.setEndereco(response.body());
                    cadastroUsuario(usuario);
                }else {
                    if(endereco.getLatitude() == null){
                        if(Helper.getSharedPreferences(CONSTANTES.LAT_END, "", 2, CadastroActivity.this) != CONSTANTES.VAZIO){
                            endereco.setLatitude((String)Helper.getSharedPreferences(CONSTANTES.LAT_END, CONSTANTES.VAZIO, 2, CadastroActivity.this));
                            endereco.setLongitude((String)Helper.getSharedPreferences(CONSTANTES.LON_END, CONSTANTES.VAZIO, 2, CadastroActivity.this));
                            Helper.removeSharedPreferences(CONSTANTES.LAT_END, CadastroActivity.this);
                            Helper.removeSharedPreferences(CONSTANTES.LON_END, CadastroActivity.this);
                        }else {
                            String locationName = Validacoes.deParaUf(inputUF.getEditText().getText().toString()) + ", " + inputBairro.getEditText().getText().toString();
                            LatLng focoMap = Helper.getAddressForLocationName(locationName, CadastroActivity.this);
                            try {
                                Helper.openProgressDialog("Validando", CadastroActivity.this);
                                Helper.openModalMap(CadastroActivity.this, focoMap);
                                Helper.closeProgressDialog();
                                return;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Helper.closeProgressDialog();
                            }
                        }
                    }
                    cadastroEndereco(endereco);
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {

            }
        });
    }

    private void cadastroUsuario(Usuario usuario){
        Call<Usuario> cadastroUsuario = retrofitInit.getService().cadastroUsuario(usuario);
        cadastroUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));

                    String tipoUsuario;
                    if(response.body().getCrm_crp().isEmpty()){
                        tipoUsuario = CONSTANTES.PACIENTE;
                    }else{
                        tipoUsuario = CONSTANTES.VOLUNTARIO;
                    }
                    salvarDadosUsuario(response.body().getCodigo(), tipoUsuario);

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if(!task.isSuccessful()){
                                        Log.w("CODE", "getInstangeId Failed", task.getException());
                                        return;
                                    }
                                    String token = task.getResult().getToken();
                                    FireStore.insertUserId(response.body().getCodigo(), token);
                                    String msg = getString(R.string.msg_token_fmt, token);
                                    Log.d("CODE", msg);
                                }
                            });

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