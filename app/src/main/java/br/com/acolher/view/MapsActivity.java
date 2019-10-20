package br.com.acolher.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.acolher.R;

public class MapsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private ActionBar actionBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(MapsActivity.this);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7a91ca")));

        loadFragment(new HomeMapFragment());

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        /*setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }

    private boolean loadFragment(Fragment fragment){

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_content, fragment)
                    .commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tool_sair:
                limparDadosUsuario();
                Intent login = new Intent(MapsActivity.this, Login.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                return true;

            case R.id.tool_ajuda:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void limparDadosUsuario() {
        sharedPreferences = this.getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("logado", false);
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Buscar Informação do sharedPreferences Inicio

        SharedPreferences sharedPreferences = getSharedPreferences("USERDATA", Context.MODE_PRIVATE);

        //Buscar Informação do sharedPreferences Fim


        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.home :
                fragment = new HomeMapFragment();
                break;
            case R.id.agenda :
                fragment = new ConsultasFragment();
                break;
            case R.id.chat :
                fragment = new ChatFragment();
                break;
            case R.id.conta :

                if (sharedPreferences.getString("TYPE", "").equals("INSTITUICAO")) {
                    fragment = new MeusDadosInstituicaoFragment();
                }else{
                    fragment = new MinhaContaFragment();
                }
                break;
            default:
                break;
        }

        return loadFragment(fragment);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
}
