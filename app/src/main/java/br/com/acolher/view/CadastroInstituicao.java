package br.com.acolher.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.InstituicaoController;
import br.com.acolher.helper.MaskWatcher;
import br.com.acolher.helper.Validacoes;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Instituicao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroInstituicao extends AppCompatActivity{

    Button continuarCadastro;
    TextInputLayout inputPassword;
    TextInputLayout inputTelefone;
    TextInputLayout inputCnpj;
    TextInputLayout inputNome;
    TextInputLayout inputEmail;
    InstituicaoController ic;
    public static final String TAG = "API";
    private Instituicao instituicao = new Instituicao();
    private RetrofitInit retrofitInit = new RetrofitInit();
    private String nome;
    private String email;
    private String password;
    private String cnpj;
    private String telefone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Configurações da activity
        setContentView(R.layout.activity_cadastro_instituicao);

        continuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);

        inputCnpj = (TextInputLayout) findViewById(R.id.inputCnpj);
        inputCnpj.getEditText().addTextChangedListener(MaskWatcher.buildCnpj());

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));
        inputNome = (TextInputLayout) findViewById(R.id.inputNome);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

        continuarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ic = new InstituicaoController();
                if(validateForm()){
                    Intent intent = getIntent();
                    Endereco endereco = new Endereco();
                    endereco.setCodigo(intent.getIntExtra("codigoEndereco", 0));

                    instituicao.setEndereco(endereco);
                    instituicao.setNome(nome);
                    instituicao.setCnpj(cnpj);
                    instituicao.setTelefone(telefone);
                    instituicao.setEmail(email);
                    instituicao.setSenha(password);

                    cadastroInstituicao(instituicao);
                }
            }
        });

    }

    public boolean validateForm(){

        nome = inputNome.getEditText().getText().toString();
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        cnpj = Validacoes.cleanCNPJ(inputCnpj.getEditText().getText().toString());
        telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());

        if(ic.validarNome(nome) != ""){
            inputNome.getEditText().setError(ic.validarNome(nome));
            return false;
        }

        if(ic.validaCnpj(cnpj) != ""){
            inputCnpj.getEditText().setError(ic.validaCnpj(cnpj));
            return false;
        }
        if(ic.validarEmail(email) != ""){
            inputEmail.getEditText().setError(ic.validarEmail(email));
            return false;
        }

        if(ic.validaPassword(password) != ""){
            inputPassword.getEditText().setError(ic.validaPassword(password));
            return false;
        }

        if(ic.validarTelefone(telefone) != ""){
            inputTelefone.getEditText().setError(ic.validarTelefone(telefone));
            return false;
        }
        return true;

    }

    private void cadastroInstituicao(Instituicao instituicao){

        Call<Instituicao> cadastroInstituicao = retrofitInit.getService().cadastroInstituicao(instituicao);
        cadastroInstituicao.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Log.d(TAG, response.body().toString());

                    salvarDadosInstituicao(response.body().getCodigo());

                    Intent home = new Intent(CadastroInstituicao.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, String.valueOf(response.code()));
                    if(response.code() == 403){
                        if(response.errorBody().contentLength() == 19) {
                            msgJaCadastrado("CNPJ");
                        }
                        if(response.errorBody().contentLength() == 21) {
                            msgJaCadastrado("E-mail");
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {

            }
        });

    }

    public void msgJaCadastrado(String campo){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastroInstituicao.this);
        alertDialog.setTitle("Atenção");
        alertDialog.setMessage(campo + " " + "já cadastrado.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }

    public void salvarDadosInstituicao(Integer codigoUsuario) {
        sharedPreferences = this.getSharedPreferences("USERDATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("USERCODE", codigoUsuario);
        editor.putString("TYPE", "INSTITUICAO");
        editor.apply();
    }
}