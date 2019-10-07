package br.com.acolher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
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
        TextView status =  view.findViewById(R.id.status);

        SharedPreferences pref = act.getApplicationContext().getSharedPreferences("USERDATA", act.getApplicationContext().MODE_PRIVATE);
        String tipo = pref.getString("TYPE","tipo não encontrado");

        Consulta consulta = consultas.get(position);

        if(tipo.equals("PACIENTE")) {
            nome.setText(consulta.getProfissional().getNome_completo());
        }else if (tipo.equals("VOLUNTARIO")){
            try {
                nome.setText(consulta.getPaciente().getNome_completo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //tem q tratar
        }
        hora.setText(consulta.getHora());
        data.setText(consulta.getData());
        endereco.setText(consulta.getEndereco().getLogradouro()+ ",n° " + consulta.getEndereco().getNumero() + " "+consulta.getEndereco().getBairro());
        cod.setText(""+consulta.getCodigo().toString());
        status.setText(consulta.getStatusConsulta().toString());
        if(consulta.getStatusConsulta().toString().equals("CANCELADA")){
            status.setTextColor(Color.RED);
        }else{
            status.setTextColor(Color.GREEN);
        }
        return view;
    }
}
