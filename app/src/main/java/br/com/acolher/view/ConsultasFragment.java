package br.com.acolher.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConsultas;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.model.Consulta;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultasFragment extends Fragment implements Serializable{

    View mView;
    private List<Consulta> consultas;
    Call<List<Consulta>> call;
    private RetrofitInit retrofitInit = new RetrofitInit();
    private SharedPreferences sharedPreferences;
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
        listaDeConsultas = mView.findViewById(R.id.listaConsultas);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(CONSTANTES.USERDATA, getActivity().getApplicationContext().MODE_PRIVATE);
        codigo = sharedPreferences.getInt(CONSTANTES.USERCODE,0);

        loadLista();

        listaDeConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemDoubleClick(View view, int position, long l) {
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                long currTime = System.currentTimeMillis();
                if (currTime - mLastClickTime < 1000) {
                    onItemDoubleClick(view, position, id);
                    return;
                }
                mLastClickTime = currTime;
                String cod = (String) ((TextView)view.findViewById(R.id.cod)).getText();

                Consulta consulta = new Consulta();
                consulta.setCodigo(Integer.parseInt(cod));
                for(Consulta con : consultas){
                    if(con.getCodigo().equals(consulta.getCodigo())){
                        consulta = con;
                    }
                }
                Intent intent = new Intent(view.getContext(), Consultas.class);
                intent.putExtra("consulta", consulta);
                startActivity(intent);
            }
        });


        return mView;
    }

    public void loadLista(){
        String tipo = sharedPreferences.getString(CONSTANTES.TYPE,"tipo não encontrado");
        if(tipo.equals(CONSTANTES.PACIENTE)) {
            call = retrofitInit.getService().getConsultasPorPacientes(codigo);
        }else if(tipo.equals(CONSTANTES.VOLUNTARIO)){
            call = retrofitInit.getService().getConsultasPorVoluntario(codigo);
        }else if(tipo.equals(CONSTANTES.INSTITUICAO)){
            call = retrofitInit.getService().getConsultasPorInstituicao(codigo);
        }


        call.enqueue(new Callback<List<Consulta>>() {
            @Override
            public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                Log.d(CONSTANTES.TAG, String.valueOf(response.code()));
                if(response.isSuccessful()){
                    consultas = response.body();
                    if(consultas == null){
                        consultas = new ArrayList<>();
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
