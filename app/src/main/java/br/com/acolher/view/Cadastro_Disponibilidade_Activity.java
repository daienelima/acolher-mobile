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
     Calendar calendar;
     DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

     TextInputLayout inputCodigo;
    TextInputLayout inputNome;
    TextInputLayout inputData;
    ImageButton btnCalendar;
    TextInputLayout inputHora;
    int currentHour;
    int currentMinute;
    String amPm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        //Buscar botões em layout
        inputCodigo = (TextInputLayout) findViewById(R.id.inputCodigo);
        inputNome = (TextInputLayout) findViewById(R.id.inputNomeCompleto);
        inputData = (TextInputLayout) findViewById(R.id.inputDataNasc);
        btnCalendar = (ImageButton) findViewById(R.id.btnCalendar);
        inputHora = (TextInputLayout) findViewById(R.id.inputHora);


        //Mocando dados para futura integração
        inputCodigo.getEditText().setText("1");
        inputCodigo.setEnabled(false);;
        inputNome.getEditText().setText("Alysson Alves");
        inputNome.setEnabled(false);;
        inputData.getEditText().setText("18/10/1990");
        inputHora.getEditText().setText("08:00");


        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTime();
                openCalendar();
            }
        });

        inputData.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputData.getEditText().clearFocus();
                    openCalendar();
                }
            }
        });
        inputHora.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputHora.getEditText().clearFocus();
                    openTime();
                }
            }
        });

        }

    //Metodos a serem chamados:

    public void openCalendar(){

        inputData.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(Cadastro_Disponibilidade_Activity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputData.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void openTime(){
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(Cadastro_Disponibilidade_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                // Formatar para Am Pm caso nescessario
                /*if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }*/
                inputHora.getEditText().setText(String.format("%02d:%02d", hourOfDay, minutes));
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }


    }

