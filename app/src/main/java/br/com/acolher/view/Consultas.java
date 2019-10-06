package br.com.acolher.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterConsultas;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Consultas extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker myMarker;
    private RetrofitInit retrofitInit = new RetrofitInit();
    Call<Consulta> call;
    Consulta c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Configurações da activity
        setContentView(R.layout.consulta_activity);

        c = (Consulta) getIntent().getSerializableExtra("consulta");

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
        Button cancelarConsulta = (Button) findViewById(R.id.buttonCancelarConsulta) ;
        TextView voltar = (TextView) findViewById(R.id.labelRetornarConsultas) ;

        if(c.getStatusConsulta().equals(Status.CANCELADA)){
            cancelarConsulta.setVisibility(View.INVISIBLE);
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("USERDATA", getApplicationContext().MODE_PRIVATE);
        String tipo = pref.getString("TIPO","tipo não encontrado");



        final Bundle bundle = getIntent().getExtras();
        if(tipo.equals("paciente")){
            nomeLabel.setText("Nome do voluntário");
            nome.setText(c.getProfissional().getNome_completo());
        }else if(tipo.equals("voluntario")){
            nomeLabel.setText("Nome do paciente");
            try {
                nome.setText(c.getPaciente().getNome_completo());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //tem q tratar
        }

        data.setText(c.getData());
        hora.setText(c.getHora());
        endereco.setText(c.getEndereco().getLogradouro()+",n° "+c.getEndereco().getNumero()+" "+c.getEndereco().getBairro());

        cancelarConsulta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(tipo.equals("paciente")){
                    call = retrofitInit.getService().cancelarConsultaPaciente(c);
                    finish();
                }else if(tipo.equals("voluntario")){
                    call = retrofitInit.getService().cancelarConsulta(c);
                    finish();
                }else{
                    //tem q tratar
                }
                call.enqueue(new Callback<Consulta>() {
                    @Override
                    public void onResponse(Call<Consulta> call, Response<Consulta> response) {                    }

                    @Override
                    public void onFailure(Call<Consulta> call, Throwable t) {}
                });
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
        LatLng sydney = new LatLng(Double.parseDouble(c.getEndereco().getLatitude()), Double.parseDouble(c.getEndereco().getLongitude()));
        mMap = googleMap;
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}