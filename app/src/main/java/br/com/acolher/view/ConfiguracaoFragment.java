package br.com.acolher.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConfiguracoes;

import static android.content.Context.MODE_PRIVATE;

public class ConfiguracaoFragment extends Fragment {

    View mView;
    private ArrayList<String> opces = new ArrayList<>();
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
                    case 0: teste();
                        break;
                    case 1: teste1();
                        break;
                    case 2: teste2();
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

    private void teste(){
        
    }
    private void teste1(){

    }
    private void teste2(){

    }
    private void sair(){
        sharedPreferences = getContext().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("logado", false);
        editor.apply();
    }

}
