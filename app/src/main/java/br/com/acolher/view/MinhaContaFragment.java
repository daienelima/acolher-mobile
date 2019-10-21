package br.com.acolher.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.EnderecoController;
import br.com.acolher.controller.UsuarioController;
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

public class MinhaContaFragment extends Fragment implements View.OnClickListener {

    //Declarações
    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, email, telefone,cpf,crm,dataNasc,inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    private Button alterar,salvar,btnBuscaCep;
    private View mView;
    private String enderecoCompleto;
    private Usuario globalUsuario;
    private Boolean globalStatus;
    private String msgResposta;

    //Levantando Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_minha_conta, container,false);

        //Buscar em Fragments
        findById();

        //Buscar em SharedPreferences

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("TYPE", "").equals("VOLUNTARIO")) {
            crm.setVisibility(View.VISIBLE);
        }

        //Carregados dados
        carregarDados(sharedPreferences);

        habilitarEdicao(false);

        //Chamar Edição ao clicar alterar
        alterar.setOnClickListener(view -> habilitarEdicao(true));

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
                    msgResposta = "Favor preencha todos campos obrigatorios!";
                }
            } catch (Exception e) {
                msgResposta = "Falha na atualização!";
                e.printStackTrace();
            }

            msgTela(msgResposta);
        });

        return mView;
    }
    //incluir dados em campos
    private void meusdados (Usuario dados){


        enderecoCompleto = dados.getEndereco().getLogradouro() + "," + dados.getEndereco().getNumero()+ "," + dados.getEndereco().getCidade()+ "-" + dados.getEndereco().getUf()+ "," + dados.getEndereco().getCep();

        nomeCompleto.getEditText().setText(dados.getNome_completo());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(dados.getTelefone());
        telefone.getEditText().setTextColor(Color.BLACK);
        cpf.getEditText().setText(dados.getCpf());
        cpf.getEditText().setTextColor(Color.BLACK);
        crm.getEditText().setText(dados.getCrm_crp());
        crm.getEditText().setTextColor(Color.BLACK);
        dataNasc.getEditText().setText(dados.getData_nascimento());
        dataNasc.getEditText().setTextColor(Color.BLACK);

        inputRua.getEditText().setText(dados.getEndereco().getLogradouro());
        inputRua.getEditText().setTextColor(Color.BLACK);
        inputCep.getEditText().setText(dados.getEndereco().getCep());
        inputCep.getEditText().setTextColor(Color.BLACK);
        inputBairro.getEditText().setText(dados.getEndereco().getBairro());
        inputBairro.getEditText().setTextColor(Color.BLACK);
        inputNumero.getEditText().setText(dados.getEndereco().getNumero());
        inputNumero.getEditText().setTextColor(Color.BLACK);
        inputUF.getEditText().setText(dados.getEndereco().getUf());
        inputUF.getEditText().setTextColor(Color.BLACK);
        inputCidade.getEditText().setText(dados.getEndereco().getCidade());
        inputCidade.getEditText().setTextColor(Color.BLACK);
    }

    //incluir dados em campos
    private void capturarDadosCampos (Usuario dados){

        enderecoCompleto = dados.getEndereco().getLogradouro() + "," + dados.getEndereco().getNumero()+ "," + dados.getEndereco().getCidade()+ "-" + dados.getEndereco().getUf()+ "," + dados.getEndereco().getCep();

        nomeCompleto.getEditText().setText(dados.getNome_completo());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(dados.getTelefone());
        telefone.getEditText().setTextColor(Color.BLACK);
        cpf.getEditText().setText(dados.getCpf());
        cpf.getEditText().setTextColor(Color.BLACK);

        inputRua.getEditText().setText(dados.getEndereco().getLogradouro());
        inputRua.getEditText().setTextColor(Color.BLACK);
        inputCep.getEditText().setText(dados.getEndereco().getCep());
        inputCep.getEditText().setTextColor(Color.BLACK);
        inputBairro.getEditText().setText(dados.getEndereco().getBairro());
        inputBairro.getEditText().setTextColor(Color.BLACK);
        inputNumero.getEditText().setText(dados.getEndereco().getNumero());
        inputNumero.getEditText().setTextColor(Color.BLACK);
        inputUF.getEditText().setText(dados.getEndereco().getUf());
        inputUF.getEditText().setTextColor(Color.BLACK);
        inputCidade.getEditText().setText(dados.getEndereco().getCidade());
        inputCidade.getEditText().setTextColor(Color.BLACK);
    }

    private void habilitarEdicao (boolean opcao){
        if(opcao){
            //Habilitar campos
            nomeCompleto.setEnabled(true);
            email.setEnabled(true);
            telefone.setEnabled(true);
            cpf.setEnabled(true);
            crm.setEnabled(true);
            dataNasc.setEnabled(true);

            inputRua.setEnabled(true);
            inputCep.setEnabled(true);
            inputBairro.setEnabled(true);
            inputNumero.setEnabled(true);
            inputUF.setEnabled(true);
            inputCidade.setEnabled(true);

            btnBuscaCep.setEnabled(true);
            alterar.setEnabled(false);
            alterar.setActivated(false);
            alterar.setVisibility(View.INVISIBLE);
            salvar.setEnabled(true);
            salvar.setVisibility(View.VISIBLE);

        }else{

            //Desabilitar crm
            crm.setVisibility(View.GONE);

            //Desabilitar campos
            nomeCompleto.setEnabled(false);
            email.setEnabled(false);
            telefone.setEnabled(false);
            cpf.setEnabled(false);
            crm.setEnabled(false);
            dataNasc.setEnabled(false);

            inputRua.setEnabled(false);
            inputCep.setEnabled(false);
            inputBairro.setEnabled(false);
            inputNumero.setEnabled(false);
            inputUF.setEnabled(false);
            inputCidade.setEnabled(false);

            btnBuscaCep.setEnabled(false);
            alterar.setEnabled(true);
            alterar.setActivated(true);
            salvar.setEnabled(false);
            alterar.setVisibility(View.VISIBLE);
            salvar.setVisibility(View.INVISIBLE);
        }

    }
    //Realizar Busca em Fragment
    private void findById() {
        nomeCompleto = mView.findViewById(R.id.inputNome);
        email =  mView.findViewById(R.id.inputEmail);
        telefone =  mView.findViewById(R.id.inputTelefone);
        cpf =  mView.findViewById(R.id.inputCpf);
        crm = mView.findViewById(R.id.inputCRM);
        dataNasc = mView.findViewById(R.id.inputDataNasc);
        alterar =  mView.findViewById(R.id.buttonAlterar);
        salvar =  mView.findViewById(R.id.buttonSalvar);

        // Para endereco

        //pesquisarEndereco =  findViewById(R.id.btnSearchLocale);
        btnBuscaCep = mView.findViewById(R.id.btnBuscaCep);
        inputRua = mView.findViewById(R.id.inputRua);
        inputCep =  mView.findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));
        inputBairro = mView.findViewById(R.id.inputBairro);
        inputNumero = mView.findViewById(R.id.inputNumero);
        inputUF = mView.findViewById(R.id.inputUF);
        inputCidade = mView.findViewById(R.id.inputCidade);
    }

    private void carregarDados (SharedPreferences sharedPreferences){

        Call<Usuario> call = retrofitInit.getService().getUsuario(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, String.valueOf(response.code()));
                    globalUsuario = response.body();
                    meusdados(globalUsuario);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void atualizarDados (){

        Endereco endereco = globalUsuario.getEndereco();

        // Para endereco

        endereco.setLogradouro(inputRua.getEditText().getText().toString());
        endereco.setCep(inputCep.getEditText().getText().toString());
        endereco.setBairro(inputBairro.getEditText().getText().toString());
        endereco.setNumero(inputNumero.getEditText().getText().toString());
        endereco.setUf(inputUF.getEditText().getText().toString());
        endereco.setCidade(inputCidade.getEditText().getText().toString());

        // Para Usuario

        globalUsuario.setNome_completo(nomeCompleto.getEditText().getText().toString());
        globalUsuario.setEmail(email.getEditText().getText().toString());
        globalUsuario.setTelefone(telefone.getEditText().getText().toString());
        globalUsuario.setCpf(cpf.getEditText().getText().toString());
        globalUsuario.setCrm_crp(crm.getEditText().getText().toString());
        globalUsuario.setData_nascimento(dataNasc.getEditText().getText().toString());
        globalUsuario.setEndereco(endereco);



    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void msgTela(String mensagem){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        alertDialog.setTitle("Atenção!");
        alertDialog.setMessage(mensagem);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());

        // visualizacao do dialogo
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
            inputCep.setError("Campo Obrigatorio");
        }
    }

    public boolean validateForm(){

        UsuarioController ic = new UsuarioController();

        EnderecoController ec = new EnderecoController();


        if(ic.validarNome(globalUsuario.getNome_completo()) != ""){
            nomeCompleto.setError(ic.validarNome(globalUsuario.getNome_completo()));
            return false;
        }

        if(ic.validaCpf(globalUsuario.getCpf()) != ""){
            cpf.setError(ic.validaCpf(globalUsuario.getCpf()));
            return false;
        }

        if((globalUsuario.getData_nascimento()) == null || globalUsuario.getData_nascimento().trim().isEmpty() || globalUsuario.getData_nascimento() == ""){
            dataNasc.setError("Campo Obrigatorio");
            return false;
        }

        if(ic.validarEmail(globalUsuario.getEmail()) != ""){
            email.setError(ic.validarEmail(globalUsuario.getEmail()));
            return false;
        }


        if(ic.validarTelefone(globalUsuario.getTelefone()) != ""){
            telefone.setError(ic.validarTelefone(globalUsuario.getTelefone()));
            return false;
        }

        if(ec.validaCep(globalUsuario.getEndereco().getCep()) != ""){
            inputCep.setError(ec.validaCep(globalUsuario.getEndereco().getCep()));
            return false;
        }

        //Para endereço

        if(!EnderecoController.empty(globalUsuario.getEndereco().getLogradouro())){
            inputRua.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(globalUsuario.getEndereco().getNumero())){
            inputNumero.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(globalUsuario.getEndereco().getBairro())){
            inputBairro.setError("Campo Obrigatorio");
            return false;
        }

        if(!EnderecoController.empty(globalUsuario.getEndereco().getCidade())){
            inputCidade.setError("Campo Obrigatorio");
            return false;
        }

        if(EnderecoController.validaUF(globalUsuario.getEndereco().getUf()) != ""){
            inputUF.setError(EnderecoController.validaUF(globalUsuario.getEndereco().getUf()));
            return false;
        }



        return true;

    }
    private void updateBD(){

        updateBDEndereco();

        Call<Usuario> call = retrofitInit.getService().alterarUsuario(globalUsuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("UsuarioService   ", "Erro ao atualizar a usuario:" + t.getMessage());

            }
        });
    }

    private void updateBDEndereco(){

        Call<Endereco> call = retrofitInit.getService().atualizarEndereco(globalUsuario.getEndereco());
        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Log.e("UsuarioService   ", "Erro ao atualizar endereco usuario:" + t.getMessage());

            }
        });

    }


}
