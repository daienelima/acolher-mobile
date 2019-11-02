package br.com.acolher.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterConsultas extends BaseAdapter {

    private RetrofitInit retrofitInit = new RetrofitInit();
    private final List<Consulta> consultas;
    private final Activity act;
    private TextView labelPergunta, labelSim, labelNao;

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
        return position;
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
        labelPergunta = view.findViewById(R.id.labelPergunta);
        labelSim = view.findViewById(R.id.sim);
        labelNao = view.findViewById(R.id.nao);

        SharedPreferences pref = act.getApplicationContext().getSharedPreferences(CONSTANTES.USERDATA, act.getApplicationContext().MODE_PRIVATE);
        String tipo = pref.getString(CONSTANTES.TYPE,"tipo não encontrado");

        Consulta consulta = consultas.get(position);

        if(tipo.equals(CONSTANTES.PACIENTE)) {
            try {
                nome.setText(consulta.getProfissional().getNome_completo());
            } catch (Exception e) {
                nome.setText(consulta.getInstituicao().getNome());
            }
        }else if (tipo.equals(CONSTANTES.VOLUNTARIO)){
            try {
                nome.setText(consulta.getPaciente().getNome_completo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (tipo.equals(CONSTANTES.INSTITUICAO)){
            try {
                nome.setText(consulta.getPaciente().getNome_completo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        hora.setText(consulta.getHora());
        data.setText(consulta.getData());
        endereco.setText(consulta.getEndereco().getLogradouro()+ ",n° " + consulta.getEndereco().getNumero() + " "+consulta.getEndereco().getBairro());
        cod.setText(""+consulta.getCodigo().toString());

        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");

        Date dataConsulta = null;
        try {
            dataConsulta = new SimpleDateFormat("dd/MM/yyyy").parse(consulta.getData());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dataAtual = new Date();
        int horaAtual = Integer.valueOf(sdfH.format(gc.getTime()).replace(":", ""));
        int horaConsulta = Integer.valueOf(consulta.getHora().replace(":", ""));

        String horaExa = String.valueOf(horaAtual).substring(0,2);
        String horaConsultaExa = String.valueOf(horaConsulta).length() != 3 ?
                String.valueOf(horaConsulta).substring(0,2) : String.valueOf(horaConsulta).substring(0,1);

        //Toast.makeText(act, consulta.getStatusConsulta().toString(), Toast.LENGTH_SHORT).show();
        if(dataConsulta.before(dataAtual) && Integer.parseInt(horaExa) > Integer.parseInt(horaConsultaExa)){
            if(consulta.getStatusConsulta().equals(Status.DISPONIVEL)){
                consulta.setStatusConsulta(Status.CANCELADA);
                cancelarConsulta(consulta);
            }else if(consulta.getStatusConsulta().equals(Status.CONFIRMADA)){
                labelPergunta.setVisibility(View.VISIBLE);
                labelSim.setVisibility(View.VISIBLE);
                labelNao.setVisibility(View.VISIBLE);
            }
        } else if(consulta.getData().equals(sdf.format(gc.getTime()))){

            if (horaConsulta <= horaAtual){
                labelPergunta.setVisibility(View.VISIBLE);
                labelSim.setVisibility(View.VISIBLE);
                labelNao.setVisibility(View.VISIBLE);
            }
        }

        status.setText(consulta.getStatusConsulta().toString());

        if(consulta.getStatusConsulta().equals(Status.CANCELADA)){
            status.setTextColor(Color.RED);
        }else{
            status.setTextColor(Color.GRAY);
        }


        labelSim.setOnClickListener(v -> {
            String cod1 = (String) ((TextView) view.findViewById(R.id.cod)).getText();

            Consulta c = new Consulta();
            c.setCodigo(Integer.parseInt(cod1));
            confirmarRealizacaoConsulta(c);
        });

        labelNao.setOnClickListener(v -> {
            String cod12 = (String) ((TextView) view.findViewById(R.id.cod)).getText();

            Consulta c = new Consulta();
            c.setCodigo(Integer.parseInt(cod12));
            confirmarRealizacaoConsulta(c);
            cancelarConsulta(c);
        });
        return view;
    }

    private void confirmarRealizacaoConsulta(Consulta consulta){
        Call<Consulta> atualizarParaRealizada = retrofitInit.getService().confirmarRealizacaoConsulta(consulta);
        atualizarParaRealizada.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    labelPergunta.setVisibility(View.GONE);
                    labelSim.setVisibility(View.GONE);
                    labelNao.setVisibility(View.GONE);
                } else {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
    }

    private void cancelarConsulta(Consulta consulta) {
        Call<Consulta> atualizarParaCancelada = retrofitInit.getService().cancelarConsulta(consulta);
        atualizarParaCancelada.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                    labelPergunta.setVisibility(View.GONE);
                    labelSim.setVisibility(View.GONE);
                    labelNao.setVisibility(View.GONE);
                } else {
                    Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
    }
}
