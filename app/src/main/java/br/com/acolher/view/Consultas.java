package br.com.acolher.view;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.util.Strings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.helper.Helper;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Status;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class Consultas extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Marker myMarker;
    RetrofitInit retrofitInit = new RetrofitInit();
    Call<Consulta> call;
    Consulta c;
    String idDestinatario;
    private static final int REQUEST_PHONE_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.consulta_activity);

        c = (Consulta) getIntent().getSerializableExtra("consulta");

        MapView mMapView = findViewById(R.id.map);
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
        ImageButton cancelarConsulta = findViewById(R.id.buttonCancelarConsulta) ;
        ImageButton chat = findViewById(R.id.buttonChat);
        ImageButton ligar = findViewById(R.id.buttonLigar);

        switch (c.getStatusConsulta()){
            case CANCELADA:
                cancelarConsulta.setVisibility(View.INVISIBLE);
                chat.setVisibility(View.INVISIBLE);
                ligar.setVisibility(View.INVISIBLE);
                break;
            case REALIZADA:
                cancelarConsulta.setVisibility(View.GONE);
                break;
            case DISPONIVEL:
                chat.setVisibility(View.GONE);
                ligar.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(CONSTANTES.USERDATA, getApplicationContext().MODE_PRIVATE);
        String tipo = pref.getString(CONSTANTES.TYPE,"tipo não encontrado");

        if(tipo.equals(CONSTANTES.PACIENTE)){
            try {
                nomeLabel.setText("Nome do voluntário");
                nome.setText(c.getProfissional().getNome_completo());
                idDestinatario = c.getProfissional().getCodigo().toString();
            }catch(Exception e){
                nomeLabel.setText("Nome da instituicao");
                nome.setText(c.getInstituicao().getNome());
                idDestinatario = "i" + c.getInstituicao().getCodigo().toString();
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

        chat.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ConversaActivity.class );
            intent.putExtra("idDestinatario", idDestinatario);
            intent.putExtra("nomeDestinatario", nome.getText());
            startActivity(intent);
        });

        cancelarConsulta.setOnClickListener(v -> {
            if(tipo.equals(CONSTANTES.PACIENTE)){
                try {
                    call = retrofitInit.getService().cancelarConsultaPaciente(c);
                }catch (Exception e){
                    Helper.msgErroServidor(Consultas.this);
                }
            }else if(tipo.equals(CONSTANTES.VOLUNTARIO)){
                try {
                    call = retrofitInit.getService().cancelarConsulta(c);
                }catch (Exception e){
                    Helper.msgErroServidor(Consultas.this);
                }
            }else{
                try {
                    call = retrofitInit.getService().cancelarConsulta(c);
                    finish();
                }catch (Exception e){
                    Helper.msgErroServidor(Consultas.this);
                }

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
        });

        //Botão para ligar dentro da API
        ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MapsActivity maps = new MapsActivity();
                String tell = "";
                if(tipo.equals("PACIENTE")){
                    if(c.getInstituicao() != null){
                        tell = c.getInstituicao().getTelefone();
                    }else {
                        tell = c.getProfissional().getTelefone();
                    }
                }else if(tipo.equals("VOLUNTARIO")){
                    tell = c.getPaciente().getTelefone();
                }

                final Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tell));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Consultas.this, Manifest.permission.CALL_PHONE)) {

                        } else {
                            ActivityCompat.requestPermissions(Consultas.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                        }
                    }
                    else{
                        startActivity(intentCall);
                    }
                }else {
                    startActivity(intentCall);
                }
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