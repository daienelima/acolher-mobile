package br.com.acolher.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.helper.Helper;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.ViaCep;
import br.com.acolher.service.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CadastroInstituicao extends AppCompatActivity{

    private Address address;
    private InstituicaoController ic;
    private boolean hasCnpj;
    private EnderecoController ec;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    public static final String TAG = "API";
    private double latitude, longitude = 0;
    private Endereco endereco = new Endereco();
    private FusedLocationProviderClient fusedLocation;
    private Instituicao instituicao = new Instituicao();
    private RetrofitInit retrofitInit = new RetrofitInit();
    Button btnFinalizarCadastro, btnBuscaCep, pesquisarEndereco, continuarCadastro;
    private String nome,email,password,cnpj,cep, rua, bairro, cidade, uf, numero,telefone;
    private TextInputLayout inputPassword,inputCnpj,inputTelefone,inputNome,inputEmail,inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        hasCnpj = false;
        //Configurações da activity
        setContentView(R.layout.activity_cadastro_instituicao);

        findById();

        Intent intent = getIntent();
        if(intent.getStringExtra(CONSTANTES.PERFIL) != null){
            if(intent.getStringExtra(CONSTANTES.PERFIL).equals(CONSTANTES.INSTITUICAO)) {
                hasCnpj = true;
                inputCnpj.setVisibility(View.VISIBLE);
            }
        }

        pesquisarEndereco.setOnClickListener(view -> {
            if (GetLocalization(CadastroInstituicao.this)) {
                if (ActivityCompat.checkSelfPermission(CadastroInstituicao.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(CadastroInstituicao.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }else{
                    fusedLocation.getLastLocation().addOnSuccessListener(CadastroInstituicao.this, location -> {
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

        btnBuscaCep.setOnClickListener(v -> {
            String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
            if(cep.length() == 8){
                buscaCep(cep);
            }else{
                inputCep.setError("CEP Invalido");
            }
        });

        btnFinalizarCadastro.setOnClickListener(view -> {
            ic = new InstituicaoController();
            ec = new EnderecoController();
            if(validateForm()) {

                //Montar Endereço
                endereco.setCep(Validacoes.cleanCep(inputCep.getEditText().getText().toString()));
                endereco.setLogradouro(inputRua.getEditText().getText().toString());
                endereco.setNumero(inputNumero.getEditText().getText().toString());
                endereco.setBairro(inputBairro.getEditText().getText().toString());
                endereco.setCidade(inputCidade.getEditText().getText().toString());
                endereco.setUf(inputUF.getEditText().getText().toString());

                //Montar Instituição
                instituicao.setEndereco(endereco);
                instituicao.setNome(nome);
                instituicao.setCnpj(cnpj);
                instituicao.setTelefone(telefone);
                instituicao.setEmail(email);
                instituicao.setSenha(password);

                if (hasCnpj) {
                    instituicao.setCnpj(cnpj);
                } else {
                    instituicao.setCnpj(CONSTANTES.VAZIO);
                }

                geAddressByParameters(endereco);
            }
        });

    }

    public void geAddressByParameters(Endereco address){
        Call<Endereco> getByParameters = retrofitInit.getService().getAddressByParameters(address);
        getByParameters.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if(response.isSuccessful()){
                    instituicao.setEndereco(response.body());
                    cadastroInstituicao(instituicao);
                }else {
                    if(endereco.getLatitude() == null){
                        if(Helper.getSharedPreferences(CONSTANTES.LAT_END, "", 2, CadastroInstituicao.this) != CONSTANTES.VAZIO){

                            endereco.setLatitude((String)Helper.getSharedPreferences(CONSTANTES.LAT_END, CONSTANTES.VAZIO, 2, CadastroInstituicao.this));
                            endereco.setLongitude((String)Helper.getSharedPreferences(CONSTANTES.LON_END, CONSTANTES.VAZIO, 2, CadastroInstituicao.this));
                            Helper.removeSharedPreferences(CONSTANTES.LAT_END, CadastroInstituicao.this);
                            Helper.removeSharedPreferences(CONSTANTES.LON_END, CadastroInstituicao.this);

                        }else {
                            String locationName = Validacoes.deParaUf(inputUF.getEditText().getText().toString()) + ", " + inputBairro.getEditText().getText().toString();
                            LatLng focoMap = Helper.getAddressForLocationName(locationName, CadastroInstituicao.this);
                            try {

                                Helper.openProgressDialog("Validando", CadastroInstituicao.this);

                                Helper.openModalMap(CadastroInstituicao.this, focoMap);

                                Helper.closeProgressDialog();


                                return;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    cadastroEndereco(endereco);
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Toast.makeText(CadastroInstituicao.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        cnpj = Validacoes.cleanCNPJ(inputCnpj.getEditText().getText().toString());
        telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());
        cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
        rua = inputRua.getEditText().getText().toString();
        numero = inputNumero.getEditText().getText().toString();
        bairro = inputBairro.getEditText().getText().toString();
        uf = inputUF.getEditText().getText().toString();
        cidade = inputCidade.getEditText().getText().toString();

        if(ic.validarNome(nome) != ""){
            inputNome.setError(ic.validarNome(nome));
            return false;
        }else{
            inputNome.setError(null);
            inputNome.clearFocus();
        }


        if(ic.validaCnpj(cnpj) != ""){
            inputCnpj.setError(ic.validaCnpj(cnpj));
            return false;
        }else{
            inputCnpj.setError(null);
            inputCnpj.clearFocus();
        }
        if(ic.validarEmail(email) != ""){
            inputEmail.setError(ic.validarEmail(email));
            return false;
        }else{
            inputEmail.setError(null);
            inputEmail.clearFocus();
        }

        if(ic.validaPassword(password) != ""){
            inputPassword.setError(ic.validaPassword(password));
            return false;
        }else{
            inputPassword.setError(null);
            inputPassword.clearFocus();
        }

        if(ic.validarTelefone(telefone) != ""){
            inputTelefone.setError(ic.validarTelefone(telefone));
            return false;
        }else{
            inputTelefone.setError(null);
            inputTelefone.clearFocus();
        }

        if(ec.validaCep(cep) != CONSTANTES.VAZIO){
            inputCep.setError(ec.validaCep(cep));
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

    private void cadastroEndereco(Endereco endereco){
        Call<Endereco> cadastroEndereco = retrofitInit.getService().cadastroEndereco(endereco);
        cadastroEndereco.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    instituicao.setEndereco(response.body());
                    cadastroInstituicao(instituicao);
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


    private void cadastroInstituicao(Instituicao instituicao){

        Call<Instituicao> cadastroInstituicao = retrofitInit.getService().cadastroInstituicao(instituicao);
        cadastroInstituicao.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());

                    salvarDadosInstituicao(response.body().getCodigo());

                    Intent home = new Intent(CadastroInstituicao.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        if(response.errorBody().contentLength() == 19) {
                            msgJaCadastrado("CNPJ");
                        }
                        if(response.errorBody().contentLength() == 21) {
                            msgJaCadastrado("E-mail");
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

    }

    public void msgJaCadastrado(String campo){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastroInstituicao.this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage(campo + " " + "já cadastrado.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

    public void salvarDadosInstituicao(Integer codigoUsuario) {
        sharedPreferences = this.getSharedPreferences("USERDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USERCODE", codigoUsuario);
        editor.putString("TYPE", "INSTITUICAO");
        editor.apply();
    }

    private void findById() {
        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);
        inputCnpj = (TextInputLayout) findViewById(R.id.inputCnpj);
        inputCnpj.getEditText().addTextChangedListener(MaskWatcher.buildCnpj());
        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));
        inputNome = (TextInputLayout) findViewById(R.id.inputNome);
        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);
        pesquisarEndereco =  findViewById(R.id.btnSearchLocale);
        btnBuscaCep = findViewById(R.id.btnBuscaCep);
        inputRua = findViewById(R.id.inputRua);
        inputCep =  findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));
        btnFinalizarCadastro = findViewById(R.id.buttonContinuarCadastro);
        inputBairro = findViewById(R.id.inputBairro);
        inputNumero = findViewById(R.id.inputNumero);
        inputUF = findViewById(R.id.inputUF);
        inputCidade = findViewById(R.id.inputCidade);

    }

    private void buscaCep(String cep) {
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
}