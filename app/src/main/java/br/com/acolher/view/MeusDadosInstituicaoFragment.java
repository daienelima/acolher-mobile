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
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosInstituicaoFragment extends Fragment implements View.OnClickListener {

    //Declarações
    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, email, telefone,cnpj,inputRua ,inputCep, inputNumero, inputBairro, inputUF, inputCidade;;
    private Button alterar,salvar;
    private View mView;
    private String enderecoCompleto;

    //Levantando Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_meus_dados_instituicao, container,false);

        //Buscar em Fragments
        findById();

        //Buscar em SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);


        Call<Instituicao> call = retrofitInit.getService().consultaInstituicao(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, String.valueOf(response.code()));
                    meusdados(response.body());
                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

        habilitarEdicao(false);

        //Chamar Edição ao clicar alterar
        alterar.setOnClickListener(view -> {

            habilitarEdicao(true);
        });

        //Chamar Put ao clicar Salvar
        salvar.setOnClickListener(view -> {

            habilitarEdicao(false);
        });

        return mView;
    }
    //incluir dados em campos
    private void meusdados (Instituicao dados){

        enderecoCompleto = dados.getEndereco().getLogradouro() + "," + dados.getEndereco().getNumero()+ "," + dados.getEndereco().getCidade()+ "-" + dados.getEndereco().getUf()+ "," + dados.getEndereco().getCep();

        nomeCompleto.getEditText().setText(dados.getNome());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(dados.getTelefone());
        telefone.getEditText().setTextColor(Color.BLACK);
        cnpj.getEditText().setText(dados.getCnpj());
        cnpj.getEditText().setTextColor(Color.BLACK);

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
        cnpj.setEnabled(true);

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

    }else{

        //Desabilitar campos
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
        cnpj =  mView.findViewById(R.id.inputCnpj);
        alterar =  mView.findViewById(R.id.buttonAlterar);
        salvar =  mView.findViewById(R.id.buttonSalvar);

        // Para endereco

        //pesquisarEndereco =  findViewById(R.id.btnSearchLocale);
        //btnBuscaCep = findViewById(R.id.btnBuscaCep);
        inputRua = mView.findViewById(R.id.inputRua);
        inputCep =  mView.findViewById(R.id.inputCep);
        inputCep.getEditText().addTextChangedListener(new MaskWatcher("##.###-###"));
        inputBairro = mView.findViewById(R.id.inputBairro);
        inputNumero = mView.findViewById(R.id.inputNumero);
        inputUF = mView.findViewById(R.id.inputUF);
        inputCidade = mView.findViewById(R.id.inputCidade);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
