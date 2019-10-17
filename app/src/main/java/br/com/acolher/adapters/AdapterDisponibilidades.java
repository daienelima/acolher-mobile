package br.com.acolher.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.model.Consulta;

public class AdapterDisponibilidades extends BaseAdapter {

    private final List<Consulta> consultas;
    private final Context context;

    public AdapterDisponibilidades(List<Consulta> consultas, Context context){
        this.consultas = consultas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return consultas.size();
    }

    @Override
    public Object getItem(int position) {
        return consultas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return consultas.get(position).getCodigo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_disponibilidades, parent, false);
        Consulta consulta = consultas.get(position);

        TextView provedorDisp = view.findViewById(R.id.labelProvedorDisp);
        TextView provedorValue = view.findViewById(R.id.valueProvedor);
        TextView data = view.findViewById(R.id.valueData);
        TextView hora = view.findViewById(R.id.valueHora);


        if(consulta.getProfissional() != null){
            provedorDisp.setText("Profissional: ");
            provedorValue.setText(consulta.getProfissional().getNome_completo());
        }else if(consulta.getInstituicao() != null){
            provedorDisp.setText("Instituição: ");
            provedorValue.setText(consulta.getInstituicao().getNome());
        }

        data.setText(consulta.getData());
        hora.setText(consulta.getHora());

        return view;
    }
}
