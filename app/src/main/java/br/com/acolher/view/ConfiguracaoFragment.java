package br.com.acolher.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConfiguracoes;

import static android.content.Context.MODE_PRIVATE;

public class ConfiguracaoFragment extends Fragment {

    View mView;
    private ArrayList<String> opces = new ArrayList<>();
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_listview_opcoes, null);
        listView = mView.findViewById(R.id.menu_item_id);

        sharedPreferences = getContext().getSharedPreferences("USERDATA",MODE_PRIVATE);
        opces = MontarMenu();
        AdapterConfiguracoes adapter = new AdapterConfiguracoes(opces, getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
            }
        });
        return mView;
    }

    /**
     * Montar itens do menu na lista
     * @return
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

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
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

}
