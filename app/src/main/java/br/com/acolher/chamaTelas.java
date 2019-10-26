package br.com.acolher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import br.com.acolher.helper.Helper;
import br.com.acolher.view.AlterarSenha;
import br.com.acolher.view.CadastroActivity;
import br.com.acolher.view.CadastroEndereco;
import br.com.acolher.view.CadastroInstituicao;
import br.com.acolher.view.CadastroDisponibilidade;

import br.com.acolher.view.MeusDadosActivity;

public class chamaTelas extends AppCompatActivity {

    private Button loginB, cadastroB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chama_telas);

        loginB = findViewById(R.id.telaLogin);
        cadastroB = findViewById(R.id.telaCadastro);

    }

    public void trocarTela(View v){

        Intent intent;

        switch (v.getId()) {
            case R.id.telaLogin:
                //intent = new Intent(chamaTelas.this, Login.class);
                //startActivity(intent);
                Geocoder coder = new Geocoder(chamaTelas.this);
                List<Address> addresses;
                LatLng coordinates = null;

                try {
                    addresses = coder.getFromLocationName("Pernambuco, Cajueiro seco", 5);
                    if(addresses == null){
                    }
                    Address location = addresses.get(0);
                    coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng latTest = Helper.openModalMap(chamaTelas.this, coordinates);
                    Toast.makeText(chamaTelas.this, String.valueOf(latTest.latitude), Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.telaCadastro:
                intent = new Intent(chamaTelas.this, CadastroActivity.class);
                startActivity(intent);
                break;
            case R.id.telaEndereco:
                intent = new Intent(chamaTelas.this, CadastroEndereco.class);
                startActivity(intent);
                break;
            case R.id.telaInstituicao:
                intent = new Intent(chamaTelas.this, CadastroInstituicao.class);
                startActivity(intent);
                break;
            case R.id.telaMeusDados:
                intent = new Intent(chamaTelas.this, MeusDadosActivity.class);
                startActivity(intent);
                break;
            case R.id.alterarSenha:
                intent = new Intent(chamaTelas.this, AlterarSenha.class);
                startActivity(intent);
                break;

            case R.id.cadastrarDisponibilidade:
                intent = new Intent(chamaTelas.this, CadastroDisponibilidade.class);
                startActivity(intent);
                break;

            default:
                return;
        }
    }
}
