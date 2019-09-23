package br.com.acolher.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.acolher.R;

public class Consultas extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker myMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Configurações da activity
        setContentView(R.layout.consulta_activity);

        MapView mMapView = (MapView) findViewById(R.id.map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        TextView nome = (TextView) findViewById(R.id.nome);
        TextView data = (TextView) findViewById(R.id.data);
        TextView hora = (TextView) findViewById(R.id.hora);
        TextView endereco = (TextView) findViewById(R.id.endereco);
        TextView nomeLabel = (TextView) findViewById(R.id.nomeLabel);
        TextView cancelarConsulta = (TextView) findViewById(R.id.buttonCancelarConsulta) ;
        TextView voltar = (TextView) findViewById(R.id.labelRetornarConsultas) ;

        String tipo = "paciente";

        if(tipo.equals("paciente")){
            nomeLabel.setText("Nome do voluntário");
        }else{
            nomeLabel.setText("Nome do paciente");
        }

        final Bundle bundle = getIntent().getExtras();
        nome.setText(bundle.getString("nome"));
        data.setText(bundle.getString("data"));
        hora.setText(bundle.getString("hora"));
        endereco.setText(bundle.getString("endereco"));

        cancelarConsulta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Codigo " + bundle.getString("cod"), Toast.LENGTH_SHORT).show();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-34, 151);
        mMap = googleMap;
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}