package br.com.acolher.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    String idDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.consulta_activity);

        c = (Consulta) getIntent().getSerializableExtra("consulta");

        MapView mMapView = (MapView) findViewById(R.id.map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        TextView nome =  findViewById(R.id.nome);
        TextView data =  findViewById(R.id.data);
        TextView hora =  findViewById(R.id.hora);
        TextView endereco =  findViewById(R.id.endereco);
        TextView nomeLabel =  findViewById(R.id.nomeLabel);
        Button cancelarConsulta = findViewById(R.id.buttonCancelarConsulta) ;
        Button chat = findViewById(R.id.buttonChat);
        TextView voltar =  findViewById(R.id.labelRetornarConsultas) ;

        if(c.getStatusConsulta().equals(Status.CANCELADA)){
            cancelarConsulta.setVisibility(View.INVISIBLE);
            chat.setVisibility(View.INVISIBLE);
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("USERDATA", getApplicationContext().MODE_PRIVATE);
        String tipo = pref.getString("TYPE","tipo não encontrado");

        final Bundle bundle = getIntent().getExtras();
        if(tipo.equals("PACIENTE")){
            try {
                nomeLabel.setText("Nome do voluntário");
                nome.setText(c.getProfissional().getNome_completo());
                idDestinatario = c.getProfissional().getCodigo().toString();
            }catch(Exception e){
                nomeLabel.setText("Nome da instituicao");
                nome.setText(c.getInstituicao().getNome());
                idDestinatario = "i" + c.getInstituicao().getCodigo().toString();
            }
        }else if(tipo.equals("VOLUNTARIO")){
            nomeLabel.setText("Nome do paciente");
            try {
                nome.setText(c.getPaciente().getNome_completo());
                idDestinatario = c.getPaciente().getCodigo().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            nomeLabel.setText("Nome do paciente");
            try {
                nome.setText(c.getPaciente().getNome_completo());
                idDestinatario = c.getPaciente().getCodigo().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        data.setText(c.getData());
        hora.setText(c.getHora());
        endereco.setText(c.getEndereco().getLogradouro()+", n° "+c.getEndereco().getNumero()+" "+c.getEndereco().getBairro());

        chat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConversaActivity.class );
                intent.putExtra("idDestinatario", idDestinatario);
                intent.putExtra("nomeDestinatario", nome.getText());
                startActivity(intent);
            }
        });

        cancelarConsulta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(tipo.equals("PACIENTE")){
                    call = retrofitInit.getService().cancelarConsultaPaciente(c);
                }else if(tipo.equals("VOLUNTARIO")){
                    call = retrofitInit.getService().cancelarConsulta(c);
                }else{
                    call = retrofitInit.getService().cancelarConsulta(c);
                    finish();
                }
                call.enqueue(new Callback<Consulta>() {
                    @Override
                    public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Consulta> call, Throwable t) {
                        finish();
                    }
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
        LatLng local = new LatLng(Double.parseDouble(c.getEndereco().getLatitude()), Double.parseDouble(c.getEndereco().getLongitude()));
        mMap = googleMap;
        myMarker = mMap.addMarker(new MarkerOptions()
                .position(local)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local,15.0f ));
    }
}