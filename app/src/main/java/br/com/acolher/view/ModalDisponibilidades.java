package br.com.acolher.view;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterDisponibilidades;
import br.com.acolher.model.Consulta;

public class ModalDisponibilidades extends AppCompatActivity {

    public static List<Consulta> listaConsultas;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modal_disponibilidades);

        lista = findViewById(R.id.listaDisponibilidades);
        AdapterDisponibilidades adapter = new AdapterDisponibilidades(listaConsultas, this);
        lista.setAdapter(adapter);

    }
}
