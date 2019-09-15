package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;

public class Cadastro_Disponibilidade_Activity extends AppCompatActivity {
     //Declarando
     TextInputLayout inputCodigo;
    TextInputLayout inputNome;
    TextInputLayout inputData;
    TextInputLayout inputHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        //Buscar botões em layout
        inputCodigo = (TextInputLayout) findViewById(R.id.inputCodigo);
        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);
        inputData = (TextInputLayout) findViewById(R.id.inputDataNasc);
        inputHora = (TextInputLayout) findViewById(R.id.inputHora);


        //Mocando dados para futura integração
        inputCodigo.getEditText().setText("1");
        inputCodigo.setEnabled(false);;
        inputNome.getEditText().setText("Alysson Alves");
        inputNome.setEnabled(false);;
        inputData.getEditText().setText("18/10/1990");
        inputHora.getEditText().setText("08:00");
        }
    }

