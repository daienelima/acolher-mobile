package br.com.acolher.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConsultas;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Usuario;

public class ConsultasFragment extends Fragment {

    View mView;
    private List<Consulta> consultas;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_consultas, null);
        consultas = new ArrayList<>();

        Usuario u = new Usuario();
        u.setNomeCompleto("Pedro Paulo");
        u.setCpf("10436594471");

        Usuario d = new Usuario();
        d.setNomeCompleto("Ricardo Nobrega");
        d.setCrm_crp("23123123123");

        Endereco e = new Endereco();
        e.setLogradouro("Rua Tocantinópolis");
        e.setNumero("100");
        e.setBairro("Ibura");

        Consulta c = new Consulta();
        c.setCodigo(1);
        c.setPaciente(u);
        c.setProfissional(d);
        c.setHora("14:00");
        c.setEndereco(e);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.setData(LocalDateTime.of(2018,01,01,01,01));
        }
        consultas.add(c);


        Consulta cc = new Consulta();
        cc.setCodigo(2);
        cc.setPaciente(d);
        cc.setProfissional(d);
        cc.setHora("19:00");
        cc.setEndereco(e);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cc.setData(LocalDateTime.of(2019,01,01,01,01));
        }
        consultas.add(cc);

        ListView listaDeConsultas = (ListView) mView.findViewById(R.id.listaConsultas);

        AdapterConsultas adapter = new AdapterConsultas(consultas, getActivity());
        listaDeConsultas.setAdapter(adapter);


        listaDeConsultas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                String nome = (String) ((TextView)view.findViewById(R.id.nome)).getText();
                String data = (String) ((TextView)view.findViewById(R.id.data)).getText();
                String hora = (String) ((TextView)view.findViewById(R.id.hora)).getText();
                String endereco = (String) ((TextView)view.findViewById(R.id.endereco)).getText();
                String cod = (String) ((TextView)view.findViewById(R.id.cod)).getText();

                Intent intent = new Intent(view.getContext(), Consultas.class);
                intent.putExtra("nome",nome);
                intent.putExtra("data",data);
                intent.putExtra("hora",hora);
                intent.putExtra("endereco",endereco);
                intent.putExtra("cod",cod);
                startActivity(intent);
            }
        });


        return mView;
    }
}
