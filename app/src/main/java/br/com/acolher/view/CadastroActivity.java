package br.com.acolher.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import br.com.acolher.R;

public class CadastroActivity extends AppCompatActivity{

    Calendar calendar;
    DatePickerDialog datePickerDialog;

    TextInputLayout inputDataNasc;
    Button btnContinuarCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //Configurações da activity
        setContentView(R.layout.cadastro_basico_activity);

        inputDataNasc = (TextInputLayout) findViewById(R.id.inputDataNasc);
        btnContinuarCadastro = (Button) findViewById(R.id.buttonContinuarCadastro);

        btnContinuarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(CadastroActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        inputDataNasc.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, day, month, year);
                datePickerDialog.show();
            }
        });
    }

}