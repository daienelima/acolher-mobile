package br.com.acolher.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.BasicoController;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.controller.InstituicaoController;
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

public class MeusDadosInstituicao extends AppCompatActivity {

    public static final String CAMPO_OBRIGATORIO = "Campo Obrigatório!";
    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, email, telefone,cnpj,inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    private Button alterar,salvar,btnBuscaCep,cancelarEdicao;
    private Instituicao globalInstituicao;
    private String msgResposta;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meus_dados_instituicao);
        getSupportActionBar().hide();

        findById();

        //Buscar em SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("USERDATA", Context.MODE_PRIVATE);

        //Carregados dados
        carregarDados(sharedPreferences);
        habilitarEdicao(false);

        //Chamar Edição ao clicar alterar
        alterar.setOnClickListener(view -> habilitarEdicao(true));

        //Cancelar Edição
        cancelarEdicao.setOnClickListener(view -> habilitarEdicao(false) );

        btnBuscaCep.setOnClickListener(v -> {
            String cep = Validacoes.cleanCep(inputCep.getEditText().getText().toString());
            buscaCep(cep);
        });

        //Chamar Put ao clicar Salvar
        salvar.setOnClickListener(view -> {
            try {
                atualizarDados();
                if (validateForm()){
                    updateBD();
                    msgResposta = "Atualização realizada com sucesso!";
                    habilitarEdicao(false);
                }else{
                    msgResposta = "Campos Obrigatorio não devem ficar vazio";
                }
            } catch (Exception e) {
                msgResposta = "Falha na atualização!";
                e.printStackTrace();
            }
            msgTela(msgResposta);
        });
    }

    //incluir dados em campos
    private void meusdados (Instituicao dados){
        nomeCompleto.getEditText().setText(dados.getNome());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(MaskWatcher.addMask(dados.getTelefone(),"(##) #####-####"));
        telefone.getEditText().setTextColor(Color.BLACK);
        cnpj.getEditText().setText(MaskWatcher.addMask(dados.getCnpj(),"##.###.###/####-##"));
        cnpj.getEditText().setTextColor(Color.BLACK);

        inputRua.getEditText().setText(dados.getEndereco().getLogradouro());
        inputRua.getEditText().setTextColor(Color.BLACK);
        inputCep.getEditText().setText(MaskWatcher.addMask(dados.getEndereco().getCep(),"##.###-###"));
        inputCep.getEditText().setTextColor(Color.BLACK);
        inputBairro.getEditText().setText(dados.getEndereco().getBairro());
        inputBairro.getEditText().setTextColor(Color.BLACK);
        inputNumero.getEditText().setText(dados.getEndereco().getNumero());
        inputNumero.getEditText().setTextColor(Color.BLACK);
        inputUF.getEditText().setText(dados.getEndereco().getUf());
        inputUF.getEditText().setTextColor(Color.BLACK);
        inputCidade.getEditText().setText(dados.getEndereco().getCidade());
        inputCidade.getEditText().setTextColor(Color.BLACK);

        //Acrescentando mascaras
        telefone.getEditText().addTextChangedListener(MaskWatcher.buildFone());
        cnpj.getEditText().addTextChangedListener(MaskWatcher.buildCnpj());
        inputCep.getEditText().addTextChangedListener(MaskWatcher.buildCep());
    }

    private void habilitarEdicao (boolean opcao){
        if(opcao){
            nomeCompleto.setEnabled(true);
            email.setEnabled(true);
            telefone.setEnabled(true);
            cnpj.setEnabled(true);

            inputRua.setEnabled(true);
            inputCep.setEnabled(true);
            inputBairro.setEnabled(true);
            inputNumero.setEnabled(true);
            inputUF.setEnabled(true);
            inputCidade.setEnabled(true);

            btnBuscaCep.setEnabled(true);
            alterar.setEnabled(false);
            alterar.setActivated(false);
            alterar.setVisibility(View.GONE);
            cancelarEdicao.setEnabled(true);
            cancelarEdicao.setVisibility(View.VISIBLE);
            salvar.setEnabled(true);
            salvar.setVisibility(View.VISIBLE);
        }else{
            nomeCompleto.setEnabled(false);
            email.setEnabled(false);
            telefone.setEnabled(false);
            cnpj.setEnabled(false);

            inputRua.setEnabled(false);
            inputCep.setEnabled(false);
            inputBairro.setEnabled(false);
            inputNumero.setEnabled(false);
            inputUF.setEnabled(false);
            inputCidade.setEnabled(false);

            btnBuscaCep.setEnabled(false);
            alterar.setEnabled(true);
            alterar.setActivated(true);
            cancelarEdicao.setEnabled(false);
            cancelarEdicao.setVisibility(View.GONE);
            salvar.setEnabled(false);
            alterar.setVisibility(View.VISIBLE);
            salvar.setVisibility(View.GONE);
        }
    }

    //Realizar Busca em Fragment
    private void findById() {
        nomeCompleto = findViewById(R.id.inputNome);
        email =  findViewById(R.id.inputEmail);
        telefone =  findViewById(R.id.inputTelefone);
        cnpj =  findViewById(R.id.inputCnpj);
        alterar =  findViewById(R.id.buttonAlterar);
        salvar =  findViewById(R.id.buttonSalvar);
        cancelarEdicao = findViewById(R.id.buttonCancelarEdicao);

        // Para endereco
        btnBuscaCep = findViewById(R.id.btnBuscaCep);
        inputRua = findViewById(R.id.inputRua);
        inputCep =  findViewById(R.id.inputCep);
        inputBairro = findViewById(R.id.inputBairro);
        inputNumero = findViewById(R.id.inputNumero);
        inputUF = findViewById(R.id.inputUF);
        inputCidade = findViewById(R.id.inputCidade);
    }

    private void carregarDados (SharedPreferences sharedPreferences){

        Call<Instituicao> call = retrofitInit.getService().consultaInstituicao(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, String.valueOf(response.code()));
                    globalInstituicao = response.body();
                    meusdados(globalInstituicao);
                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });
    }

    private void atualizarDados (){
        Endereco endereco = globalInstituicao.getEndereco();
        // Para endereco
        endereco.setLogradouro(inputRua.getEditText().getText().toString());
        endereco.setCep(Validacoes.cleanCep(inputCep.getEditText().getText().toString()));
        endereco.setBairro(inputBairro.getEditText().getText().toString());
        endereco.setNumero(inputNumero.getEditText().getText().toString());
        endereco.setUf(inputUF.getEditText().getText().toString());
        endereco.setCidade(inputCidade.getEditText().getText().toString());

        // Para instituicao
        globalInstituicao.setNome(nomeCompleto.getEditText().getText().toString());
        globalInstituicao.setEmail(email.getEditText().getText().toString());
        globalInstituicao.setTelefone(Validacoes.cleanTelefone(telefone.getEditText().getText().toString()));
        globalInstituicao.setCnpj(Validacoes.cleanCNPJ(cnpj.getEditText().getText().toString()));

    }

    private void msgTela(String mensagem){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Atenção!");
        alertDialog.setMessage(mensagem);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        alertDialog.show();
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
            inputCep.setError(CAMPO_OBRIGATORIO);
        }
    }

    public boolean validateForm(){

        BasicoController bc = new BasicoController();
        InstituicaoController ic = new InstituicaoController();
        EnderecoController ec = new EnderecoController();


        if(bc.validarNome(globalInstituicao.getNome()) != ""){
            nomeCompleto.setError(ic.validarNome(globalInstituicao.getNome()));
            return false;
        }

        if(ic.validaCnpj(globalInstituicao.getCnpj()) != ""){
            cnpj.setError(ic.validaCnpj(globalInstituicao.getCnpj()));
            return false;
        }
        if(bc.validarEmail(globalInstituicao.getEmail()) != ""){
            email.setError(ic.validarEmail(globalInstituicao.getEmail()));
            return false;
        }

        if(bc.validarTelefone(globalInstituicao.getTelefone()) != ""){
            telefone.setError(ic.validarTelefone(globalInstituicao.getTelefone()));
            return false;
        }

        if(ec.validaCep(globalInstituicao.getEndereco().getCep()) != ""){
            inputCep.setError(ec.validaCep(globalInstituicao.getEndereco().getCep()));
            return false;
        }

        //Para endereço
        if(!EnderecoController.empty(globalInstituicao.getEndereco().getLogradouro())){
            inputRua.setError(CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(globalInstituicao.getEndereco().getNumero())){
            inputNumero.setError(CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(globalInstituicao.getEndereco().getBairro())){
            inputBairro.setError(CAMPO_OBRIGATORIO);
            return false;
        }

        if(!EnderecoController.empty(globalInstituicao.getEndereco().getCidade())){
            inputCidade.setError(CAMPO_OBRIGATORIO);
            return false;
        }

        if(EnderecoController.validaUF(globalInstituicao.getEndereco().getUf()) != ""){
            inputUF.setError(EnderecoController.validaUF(globalInstituicao.getEndereco().getUf()));
            return false;
        }

        return true;
    }
    private void updateBD(){
        updateBDEndereco();
        Call<Instituicao> call = retrofitInit.getService().atualizarInstituicao(globalInstituicao);
        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                Log.d(TAG, String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {
                Log.e(TAG, t.getMessage());

            }
        });
    }

    private void updateBDEndereco(){

        Call<Endereco> call = retrofitInit.getService().atualizarEndereco(globalInstituicao.getEndereco());
        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.e(TAG, t.getMessage());

            }
        });

    }
}
