package br.com.acolher.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Status;
import br.com.acolher.model.Usuario;
import br.com.acolher.service.ServiceApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Integer consultaSelecionada;
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private Marker myMarker;
    private GoogleApiClient googleApiClient;
    private Double latitude;
    private Double longitude;
    private FusedLocationProviderClient fusedLocation;
    private List<Consulta> consultas;
    private Call<List<Consulta>> callGetConsultas;
    private Call<Consulta> callPutConsulta;
    private Call<Consulta> consultaPorPaciente;
    private Consulta consPorUser;
    private RetrofitInit retrofitInit = new RetrofitInit();
    private static final int REQUEST_PHONE_CALL = 1;
    private SharedPreferences sharedPreferences;
    private Integer codeUser;
    private ProgressDialog progressDialog;
    private HashMap<Integer, Consulta> mapConsulta;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_maps, null);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Shared Preferences Mocado
         */
        sharedPreferences = getContext().getSharedPreferences("USERDATA",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("USERCODE", 4);
        editor.apply();

        MapsInitializer.initialize(getContext());
        fusedLocation = LocationServices.getFusedLocationProviderClient(getContext());

        mMapView = (MapView) mView.findViewById(R.id.map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        if(googleApiClient == null){

            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        /*enderecos = new ArrayList<Endereco>();

        Endereco end1 = new Endereco();
        end1.setLatitude("-8.152672205776259");
        end1.setLongitude("-34.916444420814514");

        Endereco end2 = new Endereco();
        end2.setLatitude("-8.149791409918464");
        end2.setLongitude("-34.91507716476917");

        Endereco end3 = new Endereco();
        end3.setLatitude("-8.153644635638697");
        end3.setLongitude("-34.913859106600285");

        Endereco end4 = new Endereco();
        end4.setLatitude("-8.156542993620057");
        end4.setLongitude("-34.916751869022846");

        Endereco end5 = new Endereco();
        end5.setLatitude("-8.160599929350232");
        end5.setLongitude("-34.9206243082881");

        enderecos.add(end1);
        enderecos.add(end2);
        enderecos.add(end3);
        enderecos.add(end4);
        enderecos.add(end5);*/
        codeUser = sharedPreferences.getInt("USERCODE", 0);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (GetLocalization(getContext())) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            } else {
                fusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            LatLng myLocal = new LatLng(latitude, longitude);
                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(myLocal)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocal, 13.0f));

                            Toast.makeText(getContext(), "LAT E LONG" + latitude + " - " + longitude, Toast.LENGTH_LONG).show();
                        }else{
                            latitude = -8.160599929350232;
                            longitude = -34.9206243082881;

                            LatLng myLocal = new LatLng(latitude, longitude);
                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(myLocal)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocal, 13.0f));
                        }
                    }
                });
            }
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Carregando");
        progressDialog.setCancelable(false);
        progressDialog.show();

        consultaPorPaciente = retrofitInit.getService().getConsultasPorPaciente(codeUser);

        consultaPorPaciente.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                consPorUser = response.body();
                if(consPorUser == null){
                    callGetConsultas = retrofitInit.getService().getConsultas();
                    callGetConsultas.enqueue(new Callback<List<Consulta>>() {
                        @Override
                        public void onResponse(Call<List<Consulta>> call, Response<List<Consulta>> response) {
                            consultas = response.body();
                            if(consultas != null && consultas.size() > 0){
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                generateMarkers(consultas,null);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Consulta>> call, Throwable t) {
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                        }
                    });
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    generateMarkers(null, consPorUser);
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.equals(myMarker)){
                    if(consPorUser != null){
                        openDetails(consPorUser);
                    }else if(consultas != null){
                        openModal(Integer.parseInt(marker.getSnippet()));
                    }
                }
                /*if(marker.equals(myMarker)){

                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    View viewDialog = getLayoutInflater().inflate(R.layout.custom_dialog_disponibilidade, null);
                    mBuilder.setView(viewDialog);
                    final AlertDialog dialog = mBuilder.create();

                    TextView btnClose = (TextView) viewDialog.findViewById(R.id.closeDialogDisp);

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }*/
                return false;
            }
        });

    }

    public void generateMarkers(List<Consulta> consultas, Consulta consultaPorUsuario){
        if(consultas != null){
            for(int i=0; i<consultas.size(); i++){
                LatLng disponibilidade = new LatLng(Double.parseDouble(consultas.get(i).getEndereco().getLatitude()), Double.parseDouble(consultas.get(i).getEndereco().getLongitude()));
                mMap.addMarker(new MarkerOptions()
                        .position(disponibilidade)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_heart))
                        .snippet(String.valueOf(i))
                );
            }
        }else if(consultaPorUsuario != null){
            LatLng disponibilidade = new LatLng(Double.parseDouble(consultaPorUsuario.getEndereco().getLatitude()), Double.parseDouble(consultaPorUsuario.getEndereco().getLongitude()));
            mMap.addMarker(new MarkerOptions()
                    .position(disponibilidade)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_heart))
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("location", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("location", "ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("location", "ConnectionFailed");
    }

    public boolean GetLocalization(Context context){
        int REQUEST_PERMISSION_LOCALIZATION = 221;
        boolean res=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                res = false;
                ActivityCompat.requestPermissions((Activity) context, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_LOCALIZATION);

            }
        }
        return res;
    }

    public void openModal(int index){

        Consulta consulta = consultas.get(index);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View viewDialog = getLayoutInflater().inflate(R.layout.custom_dialog_disponibilidade, null);
        mBuilder.setView(viewDialog);
        final AlertDialog dialog = mBuilder.create();

        TextView labelVoluntario = viewDialog.findViewById(R.id.tvVoluntario);
        TextView valueVoluntario = viewDialog.findViewById(R.id.vlVoluntario);
        TextView valueLogradouro = viewDialog.findViewById(R.id.vlEndereco);
        TextView valueNumero = viewDialog.findViewById(R.id.vlNumero);
        TextView valueCep = viewDialog.findViewById(R.id.vlCep);
        TextView valueBairro = viewDialog.findViewById(R.id.vlBairro);
        TextView valueDataHora = viewDialog.findViewById(R.id.vlDataHora);
        TextView btnClose = viewDialog.findViewById(R.id.closeDialogDisp);
        Button btnCall = viewDialog.findViewById(R.id.btnCall);
        Button btnAgendar = viewDialog.findViewById(R.id.btnAgendar);
        String tell;

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeUser != 0){
                    Usuario paciente = new Usuario();
                    paciente.setCodigo(codeUser);
                    consulta.setPaciente(paciente);
                    callPutConsulta = retrofitInit.getService().confirmarConsulta(consulta);
                    callPutConsulta.enqueue(new Callback<Consulta>() {
                        @Override
                        public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                            if(response.body() != null || response.body().getStatusConsulta().equals("CONFIRMADA")){
                                Toast.makeText(getContext(), "Consulta agendada!", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Consulta> call, Throwable t) {

                        }
                    });
                }
            }
        });

        if(consulta.getInstituicao() == null && consulta.getProfissional() != null){
            labelVoluntario.setText("Profissional : ");
            valueVoluntario.setText(consulta.getProfissional().getNome_completo());
            valueLogradouro.setText(consulta.getProfissional().getEndereco().getLogradouro());
            valueNumero.setText(consulta.getProfissional().getEndereco().getNumero());
            valueCep.setText(consulta.getProfissional().getEndereco().getCep());
            valueBairro.setText(consulta.getProfissional().getEndereco().getBairro());
            valueDataHora.setText(consulta.getData() + " | " + consulta.getHora());
            tell = consulta.getProfissional().getTelefone();
        }else{
            labelVoluntario.setText("Instituição : ");
            valueVoluntario.setText(consulta.getInstituicao().getNome());
            valueLogradouro.setText(consulta.getInstituicao().getEndereco().getLogradouro());
            valueNumero.setText(consulta.getInstituicao().getEndereco().getNumero());
            valueCep.setText(consulta.getInstituicao().getEndereco().getCep());
            valueBairro.setText(consulta.getInstituicao().getEndereco().getBairro());
            valueDataHora.setText(consulta.getData() + " | " + consulta.getHora());
            tell = consulta.getInstituicao().getTelefone();
        }

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tell));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {

                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
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

        dialog.show();
    }

    public void openDetails(Consulta c){
        Intent telaConsulta = new Intent(getContext(), Consultas.class);
        String nome = c.getPaciente() != null ? c.getPaciente().getNome_completo() : c.getInstituicao().getNome();
        String data = c.getData();
        String hora = c.getHora();
        String endereco = c.getEndereco().getLogradouro();
        String cod = c.getCodigo().toString();

        telaConsulta.putExtra("nome",nome);
        telaConsulta.putExtra("data",data);
        telaConsulta.putExtra("hora",hora);
        telaConsulta.putExtra("endereco",endereco);
        telaConsulta.putExtra("cod",cod);
        startActivity(telaConsulta);
    }
}
