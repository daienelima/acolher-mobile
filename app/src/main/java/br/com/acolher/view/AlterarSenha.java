package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import br.com.acolher.R;
import br.com.acolher.controller.UsuarioController;

public class AlterarSenha extends AppCompatActivity {

    public static final String CAMPO_OBRIGATORIO = "Campo Obrigatório!";
    private TextInputLayout inputSenhaAtual, inputNovaSenha, inputNovaSenhaConfirmacao;
    private Button altearSenha;
    String senha, novaSenha, senhaConfimacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);
        getSupportActionBar().hide();

        findById();

        senha = inputSenhaAtual.getEditText().getText().toString();
        novaSenha = inputNovaSenha.getEditText().getText().toString();
        senhaConfimacao = inputNovaSenhaConfirmacao.getEditText().getText().toString();

        altearSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm(senha, novaSenha, senhaConfimacao)){

                }
            }
        });

    }

    private void findById(){
        inputSenhaAtual = findViewById(R.id.inputSenhaAtual);
        inputNovaSenha = findViewById(R.id.inputNovaSenha);
        inputNovaSenhaConfirmacao = findViewById(R.id.inputConfirmarNovaSenha);
        altearSenha = findViewById(R.id.buttonContinuarCadastro);
    }

    private boolean validateForm(String sennha, String novaSenha, String SenhaConfirmacao){

        if(!UsuarioController.empty(sennha)){
            inputSenhaAtual.setError(CAMPO_OBRIGATORIO);
            return  false;
        }

        if(!UsuarioController.empty(novaSenha)){
            inputNovaSenha.setError(CAMPO_OBRIGATORIO);
            return  false;
        }

        if(!UsuarioController.empty(SenhaConfirmacao)){
            inputNovaSenhaConfirmacao.setError(CAMPO_OBRIGATORIO);
            return  false;
        }

        return true;
    }

}
