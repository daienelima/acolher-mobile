package br.com.acolher.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.adapters.AdapterDisponibilidades;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.helper.Helper;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Integer codigoRecente;
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private Marker myMarker, markerConsulta;
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
    private Integer codeUser;
    private String typeUser;
    private ProgressDialog progressDialog;
    private HashMap<Integer, Consulta> mapConsulta;
    private FloatingActionButton btnAddConsulta, btnAddLastConsulta;
    private AlertDialog alerta;
    private Double latDisp;
    private Double longDisp;
    BottomNavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_maps, null);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findById(view);

        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        MapsInitializer.initialize(getContext());
        fusedLocation = LocationServices.getFusedLocationProviderClient(getContext());

        if(googleApiClient == null){

            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

        /**
         * Shared Preferences Mocado
         */

        //sharedPreferences = getContext().getSharedPreferences("USERDATA",Context.MODE_PRIVATE);

        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("USERCODE", 4);
        editor.putString("TYPE", "PACIENTE");
        editor.apply();*/


        latDisp = 0.0;
        longDisp = 0.0;
        codeUser = (Integer) Helper.getSharedPreferences("USERCODE",  0, 1, getContext());
        typeUser = (String) Helper.getSharedPreferences("TYPE", "", 2, getContext());
        codigoRecente = (Integer) Helper.getSharedPreferences("COD_END_RECENT", 0, 1, getContext());

        if(!typeUser.equals("PACIENTE")) {
            if(codigoRecente != 0){
                btnAddLastConsulta.show();
            }
        }




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

        if(typeUser.equals("PACIENTE")){

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
                                    generateMarkers(consultas,null);
                                }else {
                                    Toast.makeText(getContext(), "Não existem consultas disponíveis nessa região!", Toast.LENGTH_LONG).show();
                                }
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
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
        }else{
            btnAddConsulta.show();
            if(((Integer) Helper.getSharedPreferences("COD_END_RECENT", 0, 1, getContext())) != 0){
                btnAddLastConsulta.show();
            }
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker.equals(myMarker)){
                    return false;
                }

                if(consPorUser != null){
                    Intent intent = new Intent(getContext(), Consultas.class);
                    intent.putExtra("consulta",consPorUser);
                    startActivity(intent);
                    return false;
                }

                Integer id = Integer.parseInt(marker.getSnippet());

                if(verifyDuplicityLatLng(id)){
                    ArrayList<Consulta> consultasPorLocalz = consultasPorLocalizacao(id);

                    View view = getLayoutInflater().inflate(R.layout.modal_disponibilidades, null);
                    ListView listView = view.findViewById(R.id.listaDisponibilidades);
                    AdapterDisponibilidades adapterDisponibilidades = new AdapterDisponibilidades(consultasPorLocalz, getContext());
                    listView.setAdapter(adapterDisponibilidades);

                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    mBuilder.setView(view);
                    final AlertDialog dialog = mBuilder.create();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
                            openModal(Integer.parseInt(String.valueOf(id)));
                        }
                    });

                    dialog.show();
                }else {
                    openModal(id);
                }

                //ModalDisponibilidades.listaConsultas = consultas;

                /*ModalDisponibilidades.listaConsultas = consultas;
                Intent modal = new Intent(getContext(), ModalDisponibilidades.class);
                startActivity(modal);*/

                /*if(!marker.equals(myMarker)){
                    if(consPorUser != null){
                        openDetails(consPorUser);
                    }else if(consultas != null){
                        openModal(Integer.parseInt(marker.getSnippet()));
                    }
                }*/
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

        btnAddConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latDisp == 0.0 && longDisp == 0.0){
                    openGenericModal("Selecione o local", "Selecione no mapa o local que será realizada a consulta!", getContext() );
                }else{
                    callCadastroDisp(0);
                }
            }
        });

        btnAddLastConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latDisp = Double.parseDouble((String) Helper.getSharedPreferences("LAT", "0.0", 2, getContext()));
                longDisp = Double.parseDouble((String) Helper.getSharedPreferences("LON", "0.0", 2, getContext()));
                callCadastroDisp(codigoRecente);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(typeUser .equals("VOLUNTARIO") || typeUser .equals("INSTITUICAO")){
                    if(markerConsulta != null){
                        markerConsulta.remove();
                    }
                    latDisp = latLng.latitude;
                    longDisp = latLng.longitude;
                    markerConsulta = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_heart))
                            .draggable(true)
                    );
                }
            }
        });

    }

    public void findById(View view){
        progressDialog = new ProgressDialog(getContext());
        navigationView = getActivity().findViewById(R.id.bottom_navigation);
        btnAddConsulta = view.findViewById(R.id.btnAddConsulta);
        btnAddLastConsulta = view.findViewById(R.id.btnAddConsultaRecente);
        mMapView = (MapView) mView.findViewById(R.id.map);
    }

    public void callCadastroDisp(Integer codigo){
        Intent telaCadastroDisp = new Intent(getContext(), CadastroDisponibilidade.class);
        telaCadastroDisp.putExtra("lat", latDisp);
        telaCadastroDisp.putExtra("long", longDisp);
        telaCadastroDisp.putExtra("codigoRecente", codigo);
        progressDialog.setMessage("Redirecionando");
        progressDialog.setCancelable(false);
        progressDialog.show();
        latDisp = 0.0;
        longDisp = 0.0;
        startActivity(telaCadastroDisp);
    }

    public void generateMarkers(List<Consulta> consultas, Consulta consultaPorUsuario){
        if(consultas != null){
            List<LatLng> listLatLng = new ArrayList<LatLng>();
            for(int i=0; i<consultas.size(); i++){
                LatLng disponibilidade;
                try{
                     disponibilidade = new LatLng(Double.parseDouble(consultas.get(i).getEndereco().getLatitude()), Double.parseDouble(consultas.get(i).getEndereco().getLongitude()));
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
                if(!listLatLng.contains(disponibilidade)){
                    listLatLng.add(disponibilidade);
                    mMap.addMarker(new MarkerOptions()
                            .position(disponibilidade)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_heart))
                            .snippet(String.valueOf(consultas.get(i).getCodigo()))
                    );
                }
            }
        }else if(consultaPorUsuario != null){
            LatLng disponibilidade = new LatLng(Double.parseDouble(consultaPorUsuario.getEndereco().getLatitude()), Double.parseDouble(consultaPorUsuario.getEndereco().getLongitude()));
            mMap.addMarker(new MarkerOptions()
                    .position(disponibilidade)
                    .snippet(String.valueOf(consultaPorUsuario.getCodigo()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_heart))
            );
        }else{
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Não existem consultas disponíveis nesta localidade!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        if(mMap != null){
            onMapReady(mMap);
        }
        if(markerConsulta != null){
            markerConsulta.remove();
        }
        googleApiClient.connect();
        progressDialog.dismiss();
        if(typeUser.equals("PACIENTE")){
            //btnAddLastConsulta.show();
        }
        super.onResume();
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

    public void openModal(int codigo){

        Consulta consulta = getConsulta(codigo);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View viewDialog = getLayoutInflater().inflate(R.layout.custom_dialog_disponibilidade, null);
        mBuilder.setView(viewDialog);
        AlertDialog dialog = mBuilder.create();

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
                            if(response.isSuccessful()){
                            }
                        }

                        @Override
                        public void onFailure(Call<Consulta> call, Throwable t) {
                            //openGenericModal("Erro!", "Não foi possivel agendar a consulta, tente novamente mais tarde!", getContext());
                        }
                    });
                    navigationView.setSelectedItemId(navigationView.getSelectedItemId());
                    dialog.dismiss();
                }
            }
        });

        valueLogradouro.setText(consulta.getEndereco().getLogradouro());
        valueNumero.setText(consulta.getEndereco().getNumero());
        valueCep.setText(consulta.getEndereco().getCep());
        valueBairro.setText(consulta.getEndereco().getBairro());
        valueDataHora.setText(consulta.getData() + " | " + consulta.getHora());

        if(consulta.getInstituicao() == null && consulta.getProfissional() != null){
            labelVoluntario.setText("Profissional : ");
            valueVoluntario.setText(consulta.getProfissional().getNome_completo());
            tell = consulta.getProfissional().getTelefone();
        }else{
            labelVoluntario.setText("Instituição : ");
            valueVoluntario.setText(consulta.getInstituicao().getNome());
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
        telaConsulta.putExtra("consulta", c);
        startActivity(telaConsulta);
    }

    public void openGenericModal(String title, String text, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alerta = builder.create();
        alerta.show();
    }

    public Consulta getConsulta(Integer codigo){
        Consulta consulta = null;
        for(Consulta c : consultas){
            if(c.getCodigo() == codigo){
                consulta = c;
            }
        }
        return consulta;
    }

    public boolean verifyDuplicityLatLng(Integer codigo){

        Consulta consulta = getConsulta(codigo);
        LatLng localConsulta = new LatLng(Double.parseDouble(consulta.getEndereco().getLatitude()), Double.parseDouble(consulta.getEndereco().getLongitude()));

        for(Consulta c : consultas){
            if(c.getCodigo() != codigo){
                LatLng testeDuplicLocalz = new LatLng(Double.parseDouble(c.getEndereco().getLatitude()), Double.parseDouble(c.getEndereco().getLongitude()));
                if(testeDuplicLocalz.equals(localConsulta)){
                    return true;
                }
            }
        }

        return false;
    }

    public ArrayList<Consulta> consultasPorLocalizacao(Integer codigo){

        ArrayList<Consulta> consultasPorLocalz = new ArrayList<Consulta>();
        Consulta cons = getConsulta(codigo);
        LatLng testarLatLng = new LatLng(Double.parseDouble(cons.getEndereco().getLatitude()), Double.parseDouble(cons.getEndereco().getLongitude()));

        for(Consulta c : consultas){
            LatLng latLng = new LatLng(Double.parseDouble(c.getEndereco().getLatitude()), Double.parseDouble(c.getEndereco().getLongitude()));
            if(latLng.equals(testarLatLng)){
                consultasPorLocalz.add(c);
            }
        }

        return consultasPorLocalz;

    }
}
