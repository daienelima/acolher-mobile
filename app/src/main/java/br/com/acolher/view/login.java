package br.com.acolher.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;

public class login extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout senha;
    private Button login;
    private TextView cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.inputEmail);
        senha = findViewById(R.id.inputSenha);
        login = findViewById(R.id.buttonLogin);
        cadastro = findViewById(R.id.cadastro);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validaEmail(email.getEditText().getText().toString())){
                    email.setErrorTextColor(ColorStateList.valueOf(Color.GREEN));
                    email.setError("Funk you!");
                }
            }
        });

        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(login.getContext());
                View viewDialog = getLayoutInflater().inflate(R.layout.custom_dialog_user_type, null);
                mBuilder.setView(viewDialog);
                final AlertDialog dialog = mBuilder.create();

                TextView btnClose = viewDialog.findViewById(R.id.closeDialogDisp);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                TextView labelDescricao = viewDialog.findViewById(R.id.label_perfil);

                labelDescricao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder dialogInfos = new AlertDialog.Builder(login.getContext());
                        View viewInfos = getLayoutInflater().inflate(R.layout.custom_dialog_info_perfil, null);
                        dialogInfos.setView(viewInfos);
                        final  AlertDialog alertInfos = dialogInfos.create();

                        alertInfos.show();

                    }
                });

                final Button btnPaciente = viewDialog.findViewById(R.id.btnPaciente);
                final Button btnProfissional = viewDialog.findViewById(R.id.btnProfissional);
                final Button btnInstituicao = viewDialog.findViewById(R.id.btnInstituicao);
                Intent intent = new Intent(login.getContext(), CadastroActivity.class);

                btnPaciente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String perfil = "paciente";
                        intent.putExtra("perfil", perfil);
                        startActivity(intent);
                    }
                });

                btnProfissional.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String perfil = "profissional";
                        intent.putExtra("perfil", perfil);
                        startActivity(intent);
                    }
                });

                btnInstituicao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(login.getContext(), CadastroInstituicao.class);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });

    }

    public boolean validaEmail(String emailValida) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailValida).matches();
    }
}
