package br.com.acolher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinhaContaFragment extends Fragment {


    private Usuario usuario = new Usuario();

    private RetrofitInit retrofitInit = new RetrofitInit();
    Call<Usuario> call;
    View mView;
    private String enderecoCompleto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_minha_conta, null);

        TextInputEditText nomeCompleto = (TextInputEditText) mView.findViewById(R.id.labelNomeCompleto);
        TextInputEditText cpf = (TextInputEditText) mView.findViewById(R.id.labelCPF);
        TextInputEditText dataNascimento = (TextInputEditText) mView.findViewById(R.id.labelDataNasc);
        TextInputEditText email = (TextInputEditText) mView.findViewById(R.id.labelEmail);
        TextInputEditText telefone = (TextInputEditText) mView.findViewById(R.id.labelTelefone);
        TextInputEditText endereco =  mView.findViewById(R.id.labelEndereco);
        TextInputEditText crm = (TextInputEditText) mView.findViewById(R.id.labelCRM);
        Button alterar = mView.findViewById(R.id.buttonAlterar);



        call = retrofitInit.getService().getUsuario(1);



        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                usuario = response.body();
                enderecoCompleto = usuario.getEndereco().getLogradouro() + "," + usuario.getEndereco().getNumero()+ "," + usuario.getEndereco().getCidade()+ "-" + usuario.getEndereco().getUf()+ "," + usuario.getEndereco().getCep();

                //System.out.println("Teste pegando nome " + nomeCompleto);
                //System.out.println("Teste pegando CPF " + cpf);

                nomeCompleto.setText(usuario.getNome_completo());
                cpf.setText(usuario.getCpf());
                dataNascimento.setText(usuario.getData_nascimento());
                email.setText(usuario.getEmail());
                telefone.setText(usuario.getTelefone());
                endereco.setText(enderecoCompleto);
                crm.setText(usuario.getCrm_crp());

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

        alterar.setOnClickListener(view -> {

            Intent intentMeusDados = new Intent(getContext(), MeusDadosActivity.class);
            startActivity(intentMeusDados);

        });

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
