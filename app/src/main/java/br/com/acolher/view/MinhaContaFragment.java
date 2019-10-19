package br.com.acolher.view;

import android.content.Context;
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
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.ViaCep;
import br.com.acolher.service.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import br.com.acolher.model.Usuario;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MinhaContaFragment extends Fragment {

    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, cpf, dataNascimento, email, crm, telefone, inputRua, inputCep, inputNumero, inputBairro, inputUF, inputCidade;
    private View mView;
    private Button alterar, salvar;
    private String enderecoCompleto;
    private Usuario globalUsuario;
    private Boolean globalStatus;
    private String msgResposta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_minha_conta, null);

        findById();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("TYPE", "").equals("voluntario")) {
            crm.setVisibility(View.VISIBLE);
        }

        Call<Usuario> call = retrofitInit.getService().getUsuario(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    meusdados(response.body());
                    pegarEnderecoUsuario(response.body());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

        return mView;
    }

    private void meusdados(Usuario dados) {


        nomeCompleto.getEditText().setText(dados.getNome_completo());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        cpf.getEditText().setText(dados.getCpf());
        cpf.getEditText().setTextColor(Color.BLACK);
        dataNascimento.getEditText().setText(dados.getData_nascimento());
        dataNascimento.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(dados.getTelefone());
        telefone.getEditText().setTextColor(Color.BLACK);
        crm.getEditText().setText(dados.getCrm_crp());
        crm.getEditText().setTextColor(Color.BLACK);

        nomeCompleto.setEnabled(false);
        cpf.setEnabled(false);
        email.setEnabled(false);
        dataNascimento.setEnabled(false);
        telefone.setEnabled(false);
        crm.setEnabled(false);

    }

    private void pegarEnderecoUsuario(Usuario dados) {

        enderecoCompleto = dados.getEndereco().getLogradouro() + "," + dados.getEndereco().getNumero() + "," + dados.getEndereco().getCidade() + "-" + dados.getEndereco().getUf() + "," + dados.getEndereco().getCep();

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

    private void habilitarEdicao(boolean opcao) {
        if (opcao) {

            nomeCompleto.setEnabled(true);
            email.setEnabled(true);
            telefone.setEnabled(true);
            cpf.setEnabled(true);
            dataNascimento.setEnabled(true);
            crm.setEnabled(true);

            inputRua.setEnabled(true);
            inputCep.setEnabled(true);
            inputBairro.setEnabled(true);
            inputNumero.setEnabled(true);
            inputUF.setEnabled(true);
            inputCidade.setEnabled(true);

            alterar.setEnabled(false);
            alterar.setActivated(false);
            alterar.setVisibility(View.INVISIBLE);
            salvar.setEnabled(true);
            salvar.setVisibility(View.VISIBLE);

        } else {

            nomeCompleto.setEnabled(false);
            email.setEnabled(false);
            telefone.setEnabled(false);
            cpf.setEnabled(false);
            dataNascimento.setEnabled(false);
            crm.setEnabled(false);

            inputRua.setEnabled(false);
            inputCep.setEnabled(false);
            inputBairro.setEnabled(false);
            inputNumero.setEnabled(false);
            inputUF.setEnabled(false);
            inputCidade.setEnabled(false);

            alterar.setEnabled(true);
            alterar.setActivated(true);
            salvar.setEnabled(false);
            alterar.setVisibility(View.VISIBLE);
            salvar.setVisibility(View.INVISIBLE);
        }
    }


    private void findById() {
        nomeCompleto = mView.findViewById(R.id.inputNome);
        cpf = mView.findViewById(R.id.inputCPF);
        dataNascimento = mView.findViewById(R.id.inputDataNasc);
        email = mView.findViewById(R.id.inputEmail);
        telefone = mView.findViewById(R.id.inputTelefone);
        crm = mView.findViewById(R.id.inputCRM);
        crm.setVisibility(View.GONE);

        inputRua = mView.findViewById(R.id.inputRua);
        inputCep = mView.findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));
        inputBairro = mView.findViewById(R.id.inputBairro);
        inputNumero = mView.findViewById(R.id.inputNumero);
        inputUF = mView.findViewById(R.id.inputUF);
        inputCidade = mView.findViewById(R.id.inputCidade);
    }

    private void carregarDados(SharedPreferences sharedPreferences) {

        Call<Usuario> call = retrofitInit.getService().getUsuario(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
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

    private void atualizarDados() {

        Endereco endereco = globalUsuario.getEndereco();

        // Para endereco

        endereco.setLogradouro(inputRua.getEditText().getText().toString());
        endereco.setCep(inputCep.getEditText().getText().toString());
        endereco.setBairro(inputBairro.getEditText().getText().toString());
        endereco.setNumero(inputNumero.getEditText().getText().toString());
        endereco.setUf(inputUF.getEditText().getText().toString());
        endereco.setCidade(inputCidade.getEditText().getText().toString());

        // Para usuario

        globalUsuario.setNome_completo(nomeCompleto.getEditText().getText().toString());
        globalUsuario.setEmail(email.getEditText().getText().toString());
        globalUsuario.setTelefone(telefone.getEditText().getText().toString());
        globalUsuario.setCpf(cpf.getEditText().getText().toString());
        globalUsuario.setEndereco(endereco);
        globalUsuario.setCrm_crp(crm.getEditText().toString());
        globalUsuario.setData_nascimento(dataNascimento.getEditText().toString());


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
                Log.e("UsuarioService   ", "Erro ao atualizar o usuario:" + t.getMessage());

            }
        });
    }


    //@Override
    public void onClick(View v) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void msgTela(String mensagem) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
        alertDialog.setTitle("Atenção!");
        alertDialog.setMessage(mensagem);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());

        // visualizacao do dialogo
        alertDialog.show();
    }

    private void buscaCep(String cep) {
        if (EnderecoController.empty(cep)) {
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
        } else {
            inputCep.setError("Campo Obrigatorio");
        }
    }
}