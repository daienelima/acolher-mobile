package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import br.com.acolher.controller.UsuarioController;
import br.com.acolher.helper.MaskWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.helper.Validacoes;

public class CadastroActivity extends AppCompatActivity{

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    TextInputLayout inputDataNasc;
    ImageButton btnCalendar;
    Button continuarCadastro;
    TextInputLayout inputPassword;
    TextInputLayout inputTelefone;
    TextInputLayout inputCpf;
    TextInputLayout inputNome;
    TextInputLayout inputEmail;
    UsuarioController uc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Configurações da activity
        setContentView(R.layout.cadastro_basico_activity);

        inputDataNasc = (TextInputLayout) findViewById(R.id.inputDataNasc);

        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);

        continuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);

        inputCpf = (TextInputLayout) findViewById(R.id.inputCPF);
        inputCpf.getEditText().addTextChangedListener(MaskWatcher.buildCpf());

        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));

        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);

        inputEmail = (TextInputLayout) findViewById(R.id.inputEmail);

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
                if(validateForm()){
                    Intent intentEndereco = new Intent(CadastroActivity.this, CadastroEndereco.class);
                    startActivity(intentEndereco);
                }
            }
        });

    }

    public void openCalendar(){

        inputDataNasc.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CadastroActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        if(uc.validarDataNasc(data) != ""){
            inputDataNasc.getEditText().setError(uc.validarDataNasc(data));
            return false;
        }

        if(uc.validarEmail(email) != ""){
            inputEmail.getEditText().setError(uc.validarEmail(email));
            return false;
        }

        if(uc.validaPassword(password) != ""){
            inputPassword.getEditText().setError(uc.validaPassword(password));
            return false;
        }

        if(uc.validarTelefone(telefone) != ""){
            inputTelefone.getEditText().setError(uc.validarTelefone(telefone));
            return false;
        }

        if(uc.validaCpf(cpf) != ""){
            inputCpf.getEditText().setError(uc.validaCpf(cpf));
            return false;
        }

        /*if(inputNome.getText().toString().isEmpty()){
            //textInputNome.setErrorTextAppearance();
            inputNome.setError(getString(R.string.error_nome_completo));
            return false;
        }else{
            textInputNome.setErrorEnabled(false);
        }*/

        return true;

    }

}