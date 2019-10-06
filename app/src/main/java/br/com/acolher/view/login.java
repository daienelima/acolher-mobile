package br.com.acolher.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.dto.Login;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    private TextInputLayout email;
    private TextInputLayout senha;
    private Button login;
    private TextView cadastro;
    public static final String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.inputEmail);
        senha = findViewById(R.id.inputSenha);
        login = findViewById(R.id.buttonLogin);
        cadastro = findViewById(R.id.cadastro);

        //Verificar se o usuário está logado
        usuarioLogado();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validaEmail(email.getEditText().getText().toString())){
                    email.setErrorTextColor(ColorStateList.valueOf(Color.GREEN));
                    email.setError("E-mail inválido!");
                }else{
                    Login login = new Login();
                    login.setEmail(email.getEditText().getText().toString());
                    login.setSenha(senha.getEditText().getText().toString());

                    salvarLogin(login.getEmail());
                    validarLoginUsuario(login);
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
                Intent intent = new Intent(login.getContext(), CadastroEndereco.class);



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
                        String perfil = "instituicao";
                        intent.putExtra("perfil", perfil);
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

    private void validarLoginUsuario(Login login){

        Call<Usuario> validarLoginUsuario = retrofitInit.getService().validarLoginUsuario(login);
        validarLoginUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());
                    Intent home = new Intent(login.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        validarLoginInstituicao(login);
                    }

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });

    }

    private void validarLoginInstituicao(Login login){

        Call<Instituicao> validarLoginInstituicao = retrofitInit.getService().validarLoginInstituicao(login);
        validarLoginInstituicao.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());
                    Intent home = new Intent(login.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        msgLoginInvalido();
                    }
                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

    }

    public void msgLoginInvalido(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(login.this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage("Login inválido!");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

    /**
     * Adicionar e-mail ao SharedPreferences
     *
     * @param email email a ser adicionado no SharedPreferences
     */
    public void salvarLogin(String email) {
        sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //editor.putBoolean("logado", true);
        editor.putString("email", email.toLowerCase());
        editor.apply();
    }

    /**
     * Verificar se usuário está logado
     * Caso esteja: levar usário para a tela principal
     * Caso não esteja: recuperar último e-mail logado, se houver
     */
    private void usuarioLogado() {
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
       // if (sharedPreferences.getBoolean("logado", false)) {
       //     Intent home = new Intent(this, MapsActivity.class);
       //     startActivity(home);
       //     finish();
       // } else {
            email.getEditText().setText(sharedPreferences.getString("email", null));
       // }
    }
}