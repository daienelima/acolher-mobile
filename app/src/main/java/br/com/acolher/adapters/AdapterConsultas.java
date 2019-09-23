package br.com.acolher.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.model.Consulta;

public class AdapterConsultas extends BaseAdapter {
    private final List<Consulta> consultas;
    private final Activity act;

    public AdapterConsultas(List<Consulta> consultas, Activity act) {
        this.consultas = consultas;
        this.act = act;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = act.getLayoutInflater().inflate(R.layout.listview_consultas, parent, false);

        TextView nome = view.findViewById(R.id.nome);
        TextView data = view.findViewById(R.id.data);
        TextView hora = view.findViewById(R.id.hora);
        TextView endereco = view.findViewById(R.id.endereco);
        TextView cod =  view.findViewById(R.id.cod);

        String tipo = "pacientee";

        Consulta consulta = consultas.get(position);


        if(tipo.equals("paciente")) {
            nome.setText(consulta.getProfissional().getNome_completo());
        }else{
            nome.setText(consulta.getPaciente().getNome_completo());
        }
        hora.setText(consulta.getHora());
        endereco.setText(consulta.getEndereco().getLogradouro()+ ",n° " + consulta.getEndereco().getNumero() + " "+consulta.getEndereco().getBairro());
        cod.setText(""+consulta.getCodigo().toString());
        return view;
    }
}
