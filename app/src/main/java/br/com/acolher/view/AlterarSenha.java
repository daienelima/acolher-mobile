package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import br.com.acolher.R;

public class AlterarSenha extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);
        getSupportActionBar().hide();

    }

}
