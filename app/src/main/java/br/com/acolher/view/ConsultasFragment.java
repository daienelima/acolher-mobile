package br.com.acolher.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConsultas;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultasFragment extends Fragment implements Serializable{

    View mView;
    private List<Consulta> consultas;
    Call<List<Consulta>> call;
    private RetrofitInit retrofitInit = new RetrofitInit();
    private SharedPreferences pref;
    private int id;
    private ListView listaDeConsultas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_consultas, null);
        consultas = new ArrayList<>();
        listaDeConsultas = (ListView) mView.findViewById(R.id.listaConsultas);

        pref = getActivity().getApplicationContext().getSharedPreferences("USERDATA", getActivity().getApplicationContext().MODE_PRIVATE);
        id = pref.getInt("USERCODE",0);

        loadLista();

        listaDeConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
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
            call = retrofitInit.getService().getConsultasPorPaciente(id);
        }else if(tipo.equals("VOLUNTARIO")){
            call = retrofitInit.getService().getConsultasPorVoluntario(id);
        }else {
            //tem q tratar
        }


        call.enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                if(response.isSuccessful()){
                    consultas = response.body();
                    if(consultas == null){
                        consultas = new ArrayList<Consulta>();
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
