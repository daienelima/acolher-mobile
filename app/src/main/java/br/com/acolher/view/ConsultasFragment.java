package br.com.acolher.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConsultas;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;

public class ConsultasFragment extends Fragment implements Serializable{

    View mView;
    private List<Consulta> consultas;
    Call<List<Consulta>> call;
    private SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
    Date data = new Date();
    private RetrofitInit retrofitInit = new RetrofitInit();
    private SharedPreferences pref;
    private Integer codigo;
    private ListView listaDeConsultas;
    private TextView labelNenhumaConsulta;
    long mLastClickTime;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_consultas, null);
        labelNenhumaConsulta = mView.findViewById(R.id.labelNenhumaConsulta);
        consultas = new ArrayList<>();
        listaDeConsultas = (ListView) mView.findViewById(R.id.listaConsultas);
        pref = getActivity().getApplicationContext().getSharedPreferences("USERDATA", getActivity().getApplicationContext().MODE_PRIVATE);
        codigo = pref.getInt("USERCODE",0);


        loadLista();



        listaDeConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemDoubleClick(AdapterView<?> adapterView, View view, int position, long l) {
                // DOUBLE CLICK
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                long currTime = System.currentTimeMillis();
                if (currTime - mLastClickTime < 1000) {
                    onItemDoubleClick(parent, view, position, id);
                    return;
                }
                mLastClickTime = currTime;

                String nome = (String) ((TextView)view.findViewById(R.id.nome)).getText();
                String data = (String) ((TextView)view.findViewById(R.id.data)).getText();
                String hora = (String) ((TextView)view.findViewById(R.id.hora)).getText();
                String endereco = (String) ((TextView)view.findViewById(R.id.endereco)).getText();
                String cod = (String) ((TextView)view.findViewById(R.id.cod)).getText();

                Consulta c = new Consulta();
                c.setCodigo(Integer.parseInt(cod));
                for(Consulta con : consultas){
                    if(con.getCodigo().equals(c.getCodigo())){
                        c = con;
                    }
                }
                Intent intent = new Intent(view.getContext(), Consultas.class);
                intent.putExtra("consulta",c);
                startActivity(intent);
            }
        });


        return mView;
    }

    public void loadLista(){
        String tipo = pref.getString("TYPE","erro");
        if(tipo.equals("PACIENTE")) {
            call = retrofitInit.getService().getConsultasPorPacientes(codigo);
        }else if(tipo.equals("VOLUNTARIO")){
            call = retrofitInit.getService().getConsultasPorVoluntario(codigo);
        }else if(tipo.equals("INSTITUICAO")){
            call = retrofitInit.getService().getConsultasPorInstituicao(codigo);
        }else {
            //tem q tratar
        }


        call.enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if(response.isSuccessful()){
                    consultas = response.body();
/*
                    //Verificar consulta, caso não vigente mudar status para CANCELADA
                    for(Consulta con : consultas){
                        try {
                            Date dataConsulta=new SimpleDateFormat("dd/MM/yyyy").parse(con.getData());
                            Date horaConsulta = new SimpleDateFormat("HH:mm").parse(con.getHora());

                            if(dataConsulta.before(data) || (dataConsulta.equals(data) && horaConsulta.before(data))){
                            }
                            if(!con.getStatusConsulta().equals("REALIZADA") && (dataConsulta.before(data))){
                                con.setStatusConsulta(Status.CANCELADA);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }*/

                    if(consultas == null){
                        consultas = new ArrayList<Consulta>();
                    }else if(consultas.size() == 0){
                        labelNenhumaConsulta.setVisibility(View.VISIBLE);
                    }
                    AdapterConsultas adapter = new AdapterConsultas(consultas, getActivity());
                    listaDeConsultas.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Consulta>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onResume(){
        loadLista();
        super.onResume();
    }
}
