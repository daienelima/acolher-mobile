package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import android.content.Intent;

import br.com.acolher.R;
import br.com.acolher.helper.Validacoes;

public class MeusDadosActivity extends AppCompatActivity {

    public static final String CAMPO_OBRIGATORIO = "Campo Obrigatório";
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TextInputLayout inputDataNasc;
    ImageButton btnCalendar;
    Button continuarCadastro;
    TextInputLayout inputPassword, inputTelefone, inputCpf, inputCodigo, inputNome, inputEmail;
    UsuarioController uc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_meus_dados);

        findById();

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });

        inputDataNasc.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputDataNasc.getEditText().clearFocus();
                    openCalendar();
                }
            }
        });

        continuarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uc = new UsuarioController();
                if(validateForm()) {

                    Intent intentEndereco = new Intent(MeusDadosActivity.this, CadastroEndereco.class);
                    startActivity(intentEndereco);
                }
            }
        });

    }

    private void findById() {
        inputDataNasc = findViewById(R.id.inputDataNasc);
        btnCalendar = findViewById(R.id.btnCalendar);
        continuarCadastro = findViewById(R.id.buttonContinuarCadastro);
        inputPassword =  findViewById(R.id.inputPassword);
        inputCpf = findViewById(R.id.inputCPF);
        inputCpf.getEditText().addTextChangedListener(MaskWatcher.buildCpf());
        inputTelefone = findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));
        inputNome = findViewById(R.id.inputNomeCompleto);
        inputEmail = findViewById(R.id.inputEmail);
    }

    public void openCalendar(){
        inputDataNasc.setErrorEnabled(false);
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(MeusDadosActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputDataNasc.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    public boolean validateForm(){
        String nome = inputNome.getEditText().getText().toString();
        String data = inputDataNasc.getEditText().getText().toString();
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String cpf = Validacoes.cleanCPF(inputCpf.getEditText().getText().toString());
        String telefone = Validacoes.cleanTelefone(inputTelefone.getEditText().getText().toString());

        if(uc.validarNome(nome) != ""){
            inputNome.getEditText().setError(uc.validarNome(nome));
            return false;
        }

        if(!UsuarioController.empty(data)){
            inputDataNasc.setError(CAMPO_OBRIGATORIO);
            return false;
        }

        if(UsuarioController.validaEmail(email)){
            inputEmail.setError("E-mail Invalido!");
            return false;
        }

        if(uc.validaPassword(password) != ""){
            inputPassword.setError(uc.validaPassword(password));
            return false;
        }

        if(uc.validarTelefone(telefone) != ""){
            inputTelefone.setError(uc.validarTelefone(telefone));
            return false;
        }

        if(uc.validaCpf(cpf) != ""){
            inputCpf.setError(uc.validaCpf(cpf));
            return false;
        }

        return true;

    }

}
