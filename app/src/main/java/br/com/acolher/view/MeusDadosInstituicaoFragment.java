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
import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosInstituicaoFragment extends Fragment {

    //Declarações
    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, email, telefone,cnpj;
    private Button alterar,salvar;
    private View mView;

    //Levantando Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_meus_dados_instituicao, null);

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

        return mView;
    }
    //incluir dados em campos
    private void meusdados (Instituicao dados){
        nomeCompleto.getEditText().setText(dados.getNome());
        nomeCompleto.getEditText().setTextColor(Color.BLACK);
        email.getEditText().setText(dados.getEmail());
        email.getEditText().setTextColor(Color.BLACK);
        telefone.getEditText().setText(dados.getTelefone());
        telefone.getEditText().setTextColor(Color.BLACK);
        cnpj.getEditText().setText(dados.getCnpj());
        cnpj.getEditText().setTextColor(Color.BLACK);

        //Desabilitar cammpos
        nomeCompleto.setEnabled(false);
        email.setEnabled(false);
        telefone.setEnabled(false);
        cnpj.setEnabled(false);
    }
        //Realizar Busca em Fragment
    private void findById() {
        nomeCompleto = mView.findViewById(R.id.inputNome);
        email =  mView.findViewById(R.id.inputEmail);
        telefone =  mView.findViewById(R.id.inputTelefone);
        cnpj =  mView.findViewById(R.id.inputCnpj);
        alterar =  mView.findViewById(R.id.buttonAlterar);
        salvar =  mView.findViewById(R.id.buttonSalvar);
    }

    //Chamar Edição ao clicar
    /*
       alterar.OnClickListener){
        alterar.setEnabled(false);
    });*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
