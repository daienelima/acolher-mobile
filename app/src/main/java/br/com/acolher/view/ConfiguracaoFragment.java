package br.com.acolher.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConfiguracoes;

public class ConfiguracaoFragment extends Fragment {

    View mView;
    private ArrayList<String> opces = new ArrayList<>();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_listview_opcoes, null);
        listView = mView.findViewById(R.id.menu_item_id);

        String meusDados = "Meus Dados";
        String alterar_senha = "Alterar Senha";
        String desativarConta = "Desativar Conta";
        String sair = "Sair";
        opces.add(meusDados);
        opces.add(alterar_senha);
        opces.add(desativarConta);
        opces.add(sair);

        AdapterConfiguracoes adapter = new AdapterConfiguracoes(opces, getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: teste();
                        break;
                }
            }
        });

        return mView;
    }
    
    private void teste(){
        
    }

}
