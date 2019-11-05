package br.com.acolher.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Status;
import br.com.acolher.view.ConsultasFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterConsultas extends BaseAdapter {

    private RetrofitInit retrofitInit = new RetrofitInit();
    private final List<Consulta> consultas;
    private final Activity context;
    public AdapterConsultas(List<Consulta> consultas, Activity act) {
        this.consultas = consultas;
        this.context = act;
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
        final View view = context.getLayoutInflater().inflate(R.layout.listview_consultas, parent, false);

        TextView confirmacao = view.findViewById(R.id.confirmacao);
        TextView nome = view.findViewById(R.id.nome);
        TextView data = view.findViewById(R.id.data);
        TextView hora = view.findViewById(R.id.hora);
        TextView endereco = view.findViewById(R.id.endereco);
        TextView cod =  view.findViewById(R.id.cod);
        TextView status =  view.findViewById(R.id.status);
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(CONSTANTES.USERDATA, context.getApplicationContext().MODE_PRIVATE);
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
        cod.setText(CONSTANTES.VAZIO + consulta.getCodigo().toString());

        LocalDateTime dataHoraAtual = LocalDateTime.now();

        String dia = String.valueOf(dataHoraAtual.getDayOfMonth());
        if(dia.length() == 1){
            dia = "0" + dia;
        }
        String mes = String.valueOf(dataHoraAtual.getMonthValue());
        if(mes.length() == 1){
            mes = "0" + mes;
        }
        String ano = String.valueOf(dataHoraAtual.getYear());
        String horaAtual = String.valueOf(dataHoraAtual.getHour());
        if(horaAtual.length() == 1){
            horaAtual = "0" + horaAtual;
        }
        String minuto = String.valueOf(dataHoraAtual.getMinute());
        if(minuto.length()==1){
            minuto =  "0" + minuto;
        }
        String horaAtualAxu = dia + "/" + mes + "/" + ano + " " + horaAtual + ":" + minuto;

        //Conveter consulta
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataUxi = consulta.getData() + " " +  consulta.getHora();
        LocalDateTime dataConsultaFormatada = LocalDateTime.parse(dataUxi, formatter);
        LocalDateTime dataAtualFormatada = LocalDateTime.parse(horaAtualAxu, formatter);

        if(dataConsultaFormatada.isBefore(dataAtualFormatada)){
            if(consulta.getStatusConsulta().equals(Status.DISPONIVEL)){
                consulta.setStatusConsulta(Status.CANCELADA);
                cancelarConsulta(consulta);
            }else if(consulta.getStatusConsulta().equals(Status.CONFIRMADA)){
                confirmacao.setVisibility(View.VISIBLE);
            }
        }

        status.setText(consulta.getStatusConsulta().toString());
        if(consulta.getStatusConsulta().equals(Status.CANCELADA)){
            status.setTextColor(Color.RED);
        }else if(consulta.getStatusConsulta().equals(Status.REALIZADA)){
            status.setTextColor(Color.GRAY);
        }else if(consulta.getStatusConsulta().equals(Status.CONFIRMADA)){
            status.setTextColor(Color.BLUE);
        }else{
            status.setTextColor(Color.GREEN);
        }

        confirmacao.setOnClickListener(view1 -> {
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            View viewDialog = context.getLayoutInflater().inflate(R.layout.custom_dialog_confirmar_consulta, null);
            mBuilder.setView(viewDialog);
            final AlertDialog dialog = mBuilder.create();

            Button sim = viewDialog.findViewById(R.id.sim);
            Button nao = viewDialog.findViewById(R.id.nao);

            Consulta c = new Consulta();
            String codigo = (String) ((TextView) view.findViewById(R.id.cod)).getText();
            c.setCodigo(Integer.parseInt(codigo));
            c.setPaciente(consulta.getPaciente());
            c.setInstituicao(consulta.getInstituicao());
            c.setProfissional(consulta.getProfissional());
            c.setData(consulta.getData());
            c.setHora(consulta.getHora());
            c.setEndereco(consulta.getEndereco());
            sim.setOnClickListener(view2 -> {
                confirmarRealizacaoConsulta(c);
                dialog.dismiss();
            });
            nao.setOnClickListener(view22 -> {
                cancelarConsulta(c);
                dialog.dismiss();
            });
            dialog.show();
        });
        return view;

    }

    private void confirmarRealizacaoConsulta(Consulta consulta){
        Call<Consulta> atualizarParaRealizada = retrofitInit.getService().confirmarRealizacaoConsulta(consulta);
        atualizarParaRealizada.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
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
                Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
    }
}