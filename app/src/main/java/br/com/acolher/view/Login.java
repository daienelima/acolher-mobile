package br.com.acolher.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputLayout inputEmail, inputSenha;
    private Button login;
    private TextView cadastro;
    public static final String TAG = "API";
    private RetrofitInit retrofitInit = new RetrofitInit();
    private ProgressDialog progressDialogLogin;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialogLogin = new ProgressDialog(Login.this);
        setContentView(R.layout.activity_login);
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7a91ca")));

        findById();
        usuarioLogado();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateLogin()){
                    String email = inputEmail.getEditText().getText().toString();
                    String senha = inputSenha.getEditText().getText().toString();
                    progressDialogLogin.setMessage("Entrando...");
                    progressDialogLogin.setCancelable(false);
                    progressDialogLogin.show();
                    br.com.acolher.dto.Login login = new br.com.acolher.dto.Login();
                    login.setEmail(email);
                    login.setSenha(senha);

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
                        String perfil = "instituicao";
                        intent.putExtra("perfil", perfil);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });
    }

    private boolean validateLogin(){

        String email = inputEmail.getEditText().getText().toString();
        String senha = inputSenha.getEditText().getText().toString();

        if(!UsuarioController.empty(email)){
            inputEmail.setError("E-mail inválido!");
            return false;
        }
        if(!UsuarioController.empty(senha)) {
            inputSenha.setError("Senha Inválida!");
            return  false;
        }
        return true;
    }

    private void findById() {
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        login = findViewById(R.id.buttonLogin);
        cadastro = findViewById(R.id.cadastro);
    }

    private void validarLoginUsuario(br.com.acolher.dto.Login login){

        Call<Usuario> validarLoginUsuario = retrofitInit.getService().validarLoginUsuario(login);
        validarLoginUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());
                    String tipoUsuario = "";
                    if(response.body().getCrm_crp().isEmpty()){
                        tipoUsuario = "PACIENTE";
                    }else{
                        tipoUsuario = "VOLUNTARIO";
                    }
                    salvarDadosUsuario(response.body().getCodigo(), tipoUsuario);
                    Intent home = new Intent(Login.this, MapsActivity.class);
                    if(progressDialogLogin.isShowing()){
                        progressDialogLogin.dismiss();
                    }
                    startActivity(home);
                    finish();
                } else {
                    if(progressDialogLogin.isShowing()){
                        progressDialogLogin.dismiss();
                    }
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

    private void validarLoginInstituicao(br.com.acolher.dto.Login login){

        Call<Instituicao> validarLoginInstituicao = retrofitInit.getService().validarLoginInstituicao(login);
        validarLoginInstituicao.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());
                    salvarDadosInstituicao(response.body().getCodigo());
                    Intent home = new Intent(Login.this, MapsActivity.class);
                    if(progressDialogLogin.isShowing()){
                        progressDialogLogin.dismiss();
                    }
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
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
        if(progressDialogLogin.isShowing()){
            progressDialogLogin.dismiss();
        }
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

    public void salvarDadosUsuario(Integer codigoUsuario, String tipoUsuario) {
        sharedPreferences = this.getSharedPreferences("USERDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USERCODE", codigoUsuario);
        editor.putString("TYPE", tipoUsuario);
        editor.apply();
    }

    public void salvarDadosInstituicao(Integer codigoUsuario) {
        sharedPreferences = this.getSharedPreferences("USERDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USERCODE", codigoUsuario);
        editor.putString("TYPE", "INSTITUICAO");
        editor.apply();
    }
    /**
     * Adicionar e-mail ao SharedPreferences
     *
     * @param email email a ser adicionado no SharedPreferences
     */
    public void salvarLogin(String email) {
        sharedPreferences = this.getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("logado", true);
        editor.putString("email", email.toLowerCase());
        editor.apply();
    }

    /**
     * Verificar se usuário está logado
     * Caso esteja: levar usário para a tela principal
     * Caso não esteja: recuperar último e-mail logado, se houver
     */
    private void usuarioLogado() {
        sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
         if (sharedPreferences.getBoolean("logado", false)) {
             Intent home = new Intent(this, MapsActivity.class);
             startActivity(home);
             finish();
         } else {
            inputEmail.getEditText().setText(sharedPreferences.getString("email", null));
         }
    }
}