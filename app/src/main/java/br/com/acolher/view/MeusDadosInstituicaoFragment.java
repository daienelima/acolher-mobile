package br.com.acolher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeusDadosInstituicaoFragment extends Fragment {

    private Instituicao instituicao = new Instituicao();


    private RetrofitInit retrofitInit = new RetrofitInit();
    private Call<Instituicao> call;
    private View mView;
    private String enderecoCompleto;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_meus_dados_instituicao, null);

        //buscando itens de layout\
        TextInputEditText nome =  mView.findViewById(R.id.labelNome);
        TextInputEditText cnpj =  mView.findViewById(R.id.labelCnpj);
        TextInputEditText email =  mView.findViewById(R.id.labelEmail);
        TextInputEditText telefone =  mView.findViewById(R.id.labelTelefone);
        TextInputEditText endereco =  mView.findViewById(R.id.labelEndereco);
        Button alterar = mView.findViewById(R.id.buttonAlterar);

        //buscando do banco
        call = retrofitInit.getService().consultaInstituicao(1);

        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                instituicao = response.body();

                enderecoCompleto = instituicao.getEndereco().getLogradouro() + "," + instituicao.getEndereco().getNumero()+ "," + instituicao.getEndereco().getCidade()+ "-" + instituicao.getEndereco().getUf()+ "," + instituicao.getEndereco().getCep();

                //incluindo dados em tela
                nome.setText(instituicao.getNome());
                cnpj.setText(instituicao.getCnpj());
                email.setText(instituicao.getEmail());
                telefone.setText(instituicao.getTelefone());
                endereco.setText(enderecoCompleto);
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });
        //Chamar Edição ao clicar
        alterar.setOnClickListener(view -> {

            Intent intentMeusDadosInstituicao = new Intent(getContext(), MeusDadosInstituicaoActivity.class);
            startActivity(intentMeusDadosInstituicao);

        });

        return mView;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



}