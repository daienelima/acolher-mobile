package br.com.acolher.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinhaContaFragment extends Fragment {

    private String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private TextInputLayout nomeCompleto, cpf, dataNascimento, email ,crm, telefone;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_minha_conta, null);

        findById();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("USERDATA", Context.MODE_PRIVATE);

        if(sharedPreferences.getString("TYPE", "").equals("voluntario")){
            crm.setVisibility(View.VISIBLE);
        }

        Call<Usuario> call = retrofitInit.getService().getUsuario(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, String.valueOf(response.code()));
                    meusdados(response.body());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

        return mView;
    }

    private void meusdados (Usuario dados){
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

    private void findById() {
        nomeCompleto = mView.findViewById(R.id.inputNome);
        cpf =  mView.findViewById(R.id.inputCPF);
        dataNascimento =  mView.findViewById(R.id.inputDataNasc);
        email =  mView.findViewById(R.id.inputEmail);
        telefone =  mView.findViewById(R.id.inputTelefone);
        crm =  mView.findViewById(R.id.inputCRM);
        crm.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
