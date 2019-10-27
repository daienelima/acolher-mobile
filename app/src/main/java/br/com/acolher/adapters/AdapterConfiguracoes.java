package br.com.acolher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.acolher.R;

public class AdapterConfiguracoes extends BaseAdapter {

    private final List<String> opcoes;
    private final Context context;

    public AdapterConfiguracoes(List<String> opcoes, Context context) {
        this.opcoes = opcoes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return opcoes.size();
    }

    @Override
    public Object getItem(int i) {
        return opcoes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.menu_layout, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.opcao);
        textView.setText(opcoes.get(i));
        return view;
    }
}
