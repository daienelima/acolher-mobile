package br.com.acolher.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;

public class login extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout senha;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.inputEmail);
        senha = findViewById(R.id.inputSenha);
        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validaEmail(email.getEditText().getText().toString())){
                    email.setErrorTextColor(ColorStateList.valueOf(Color.GREEN));
                    email.setError("Funk you!");
                }
            }
        });

    }

    public boolean validaEmail(String emailValida) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailValida).matches();
    }
}
