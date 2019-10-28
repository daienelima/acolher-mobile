package br.com.acolher.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.CONSTANTES;
import br.com.acolher.model.Instituicao;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlterarSenha extends AppCompatActivity {

    private TextInputLayout inputSenhaAtual, inputNovaSenha, inputNovaSenhaConfirmacao;
    private Button altearSenha;
    br.com.acolher.dto.AlterarSenha alterarSenha = new br.com.acolher.dto.AlterarSenha();
    private RetrofitInit retrofitInit = new RetrofitInit();
    String senha, novaSenha, senhaConfimacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);
        getSupportActionBar().hide();

        findById();

        SharedPreferences sharedPreferences = getSharedPreferences(CONSTANTES.USERDATA, Context.MODE_PRIVATE);
        altearSenha.setOnClickListener(view -> {
            senha = inputSenhaAtual.getEditText().getText().toString();
            novaSenha = inputNovaSenha.getEditText().getText().toString();
            senhaConfimacao = inputNovaSenhaConfirmacao.getEditText().getText().toString();

            if(validateForm(senha, novaSenha, senhaConfimacao)){
                alterarSenha.setCodigo(sharedPreferences.getInt(CONSTANTES.USERCODE, 1));
                alterarSenha.setNovaSenha(novaSenha);
                alterarSenha.setSenhaAntiga(senha);

                if (sharedPreferences.getString(CONSTANTES.TYPE, CONSTANTES.VAZIO).equals(CONSTANTES.INSTITUICAO)) {
                    alterarSenhaInstituicao();
                    msgSucesso();
                }else{
                    alterarSenhaUsuario();
                    msgSucesso();
                }
            }
        });

    }

    private void alterarSenhaInstituicao(){
        Call<Instituicao> call = retrofitInit.getService().alterarSenhaInstituicao(alterarSenha);
        call.enqueue(new Callback<Instituicao>() {
            @Override
            public void onResponse(Call<Instituicao> call, Response<Instituicao> response) {
                Log.d(CONSTANTES.TAG,String.valueOf(response.code()));
                if(response.code() == 403){
                    msgErro("A senha atual está incorreta");
                }
            }

            @Override
            public void onFailure(Call<Instituicao> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
    }

    private void alterarSenhaUsuario(){
        Call<Usuario> call = retrofitInit.getService().alterarSenhaUsuario(alterarSenha);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Log.d(CONSTANTES.TAG,String.valueOf(response.code()));
                if(response.code() == 403){
                    msgErro("A senha atual está incorreta");
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.d(CONSTANTES.TAG, t.getMessage());
            }
        });
    }

    private void msgSucesso(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Alteraração de Senha");
        alertDialog.setMessage("Senha atualizada com sucesso");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            Intent intent = new Intent(AlterarSenha.this, MapsActivity.class);
            startActivity(intent);
        });
        alertDialog.show();
    }

    private void msgErro(String msg){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle("ERRO");
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }
    private void findById(){
        inputSenhaAtual = findViewById(R.id.inputSenhaAtual);
        inputNovaSenha = findViewById(R.id.inputNovaSenha);
        inputNovaSenhaConfirmacao = findViewById(R.id.inputConfirmarNovaSenha);
        altearSenha = findViewById(R.id.buttonContinuarCadastro);
    }

    private boolean validateForm(String sennha, String novaSenha, String SenhaConfirmacao){
        UsuarioController uc = new UsuarioController();

        if(!UsuarioController.empty(sennha)){
            inputSenhaAtual.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return  false;
        }

        if(!UsuarioController.empty(novaSenha)){
            inputNovaSenha.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return  false;
        }

        if(!UsuarioController.empty(SenhaConfirmacao)){
            inputNovaSenhaConfirmacao.setError(CONSTANTES.CAMPO_OBRIGATORIO);
            return  false;
        }

        if(uc.validaPassword(novaSenha) != CONSTANTES.VAZIO){
            inputNovaSenha.setError(uc.validaPassword(novaSenha));
            return false;
        }

        if(!novaSenha.equals(senhaConfimacao)){
            inputNovaSenhaConfirmacao.setError("Senha não confere");
            return false;
        }
        return true;
    }

}
