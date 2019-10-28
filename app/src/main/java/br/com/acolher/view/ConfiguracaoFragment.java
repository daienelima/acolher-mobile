package br.com.acolher.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConfiguracoes;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.Helper;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ConfiguracaoFragment extends Fragment {

    private final static String TAG = "API";
    private View mView;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RetrofitInit retrofitInit = new RetrofitInit();
    Usuario usuarioLogado;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_listview_opcoes, null);
        listView = mView.findViewById(R.id.menu_item_id);

        sharedPreferences = getContext().getSharedPreferences("USERDATA",MODE_PRIVATE);
        ArrayList<String> opces = MontarMenu();
        AdapterConfiguracoes adapter = new AdapterConfiguracoes(opces, getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0: meusDados();
                    break;
                case 1: alterarSenha();
                    break;
                case 2: desativarConta();
                    break;
                case 3: sair();
                    break;
            }
        });
        return mView;
    }

    /**
     * Montar itens do menu na lista
     * @return menu de opcoes
     */
    private ArrayList<String> MontarMenu() {
        ArrayList<String> opces = new ArrayList<>();
        String meusDados = "Meus Dados";
        String alterar_senha = "Alterar Senha";
        String desativarConta = "Desativar Conta";
        String sair = "Sair";
        opces.add(meusDados);
        opces.add(alterar_senha);
        opces.add(desativarConta);
        opces.add(sair);
        return opces;
    }

    private void meusDados(){
        if (sharedPreferences.getString("TYPE", "").equals("INSTITUICAO")) {
            Intent intent = new Intent(getContext(), MeusDadosInstituicao.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getContext(), MeusDadosUsuario.class);
            startActivity(intent);
        }
    }

    private void alterarSenha(){
        Intent intent = new Intent(getContext(), AlterarSenha.class);
        startActivity(intent);
    }

    private void desativarConta(){
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Desativar Conta");
        builder.setMessage("Tem certeza que deseja desativar a conta ?");

        builder.setPositiveButton("Sim", (dialogInterface, i) -> {
            if (sharedPreferences.getString("TYPE", "").equals("INSTITUICAO")) {
                desativarContaInstituicao();
                this.sair();
            }else{
                desativarContaUsuario();
                this.sair();
            }

        });
        builder.setNegativeButton("Não", (dialogInterface, i) -> {

        });

        alertDialog = builder.create();
        alertDialog.show();
    }
    private void sair(){
        sharedPreferences = this.getContext().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("logado", false);
        editor.apply();
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void desativarContaUsuario(){
        Integer codeUser = (Integer) Helper.getSharedPreferences("USERCODE",  0, 1, getContext());
        Call<Usuario> call = retrofitInit.getService().desativarUsuario(codeUser);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {

            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void desativarContaInstituicao(){
        Call<Instituicao> call = retrofitInit.getService().desativarInstituicao(sharedPreferences.getInt("USERCODE", 1));
        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {

            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
