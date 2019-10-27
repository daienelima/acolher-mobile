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
            }else{
                desativarContaUsuario();
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
        getUsuer(sharedPreferences.getInt("USERCODE", 1));
        Call<Usuario> call = retrofitInit.getService().desativarUsuario(usuarioLogado);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(getContext(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                Log.d(TAG,String.valueOf(response.code()));
                if(response.isSuccessful()){
                    finalizaApp();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void getUsuer(Integer codigo){
        Call<Usuario> call = retrofitInit.getService().getUsuario(codigo);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                usuarioLogado = response.body();
                pegaUser(usuarioLogado);
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    private void desativarContaInstituicao(){
    }

    private void finalizaApp(){
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sucesso");
        builder.setMessage("Conta Desativada Com Sucesso");
        builder.setPositiveButton("Sim", (dialogInterface, i) -> this.sair());
    }

    private void pegaUser(Usuario u){
        usuarioLogado = new Usuario();
        usuarioLogado.setCodigo(u.getCodigo());
        usuarioLogado.setAtivo(u.isAtivo());
        usuarioLogado.setCpf(u.getCpf());
        usuarioLogado.setCrm_crp(u.getCrm_crp());
        usuarioLogado.setData_nascimento(u.getData_nascimento());
        usuarioLogado.setEndereco(u.getEndereco());
        usuarioLogado.setEmail(u.getEmail());
        usuarioLogado.setPassword(u.getPassword());
        usuarioLogado.setTelefone(u.getTelefone());
    }

}
