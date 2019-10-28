package br.com.acolher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.com.acolher.view.AlterarSenha;
import br.com.acolher.view.CadastroActivity;
import br.com.acolher.view.CadastroEndereco;
import br.com.acolher.view.CadastroInstituicao;
import br.com.acolher.view.CadastroDisponibilidade;

import br.com.acolher.view.Login;
import br.com.acolher.view.MeusDadosActivity;
import br.com.acolher.view.MeusDadosInstituicaoActivity;

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
                intent = new Intent(chamaTelas.this, Login.class);
                startActivity(intent);
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

            case R.id.meusDadosInstituicao:
                intent = new Intent(chamaTelas.this, MeusDadosInstituicaoActivity.class);
                startActivity(intent);

            default:
                return;
        }
    }
}
