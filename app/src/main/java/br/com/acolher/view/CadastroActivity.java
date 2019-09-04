package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import br.com.acolher.helper.MaskWatcher;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;

public class CadastroActivity extends AppCompatActivity{

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    TextInputLayout inputDataNasc;
    ImageButton btnCalendar;
    Button continuarCadastro;
    TextInputLayout inputPassword;
    TextInputLayout inputTelefone;
    TextInputLayout inputCpf;
    TextInputLayout textInputNome;
    EditText inputNome;

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
        inputTelefone = (TextInputLayout) findViewById(R.id.inputTelefone);
        inputTelefone.getEditText().addTextChangedListener(new MaskWatcher("(##) #####-####"));

        textInputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);
        inputNome = (EditText) findViewById(R.id.labelNomeCompleto);


        inputCpf = (TextInputLayout) findViewById(R.id.inputCPF);
        inputCpf.getEditText().addTextChangedListener(new MaskWatcher("###.###.###-##"));


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
                validateForm();
                //Intent intentEndereco = new Intent(CadastroActivity.this, CadastroEndereco.class);
                //startActivity(intentEndereco);
            }
        });

    }

    public void openCalendar(){
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
        if(inputNome.getText().toString().isEmpty()){
            //textInputNome.setErrorTextAppearance();
            inputNome.setError(getString(R.string.error_nome_completo));
            return false;
        }else{
            textInputNome.setErrorEnabled(false);
        }
        return true;
    }

}