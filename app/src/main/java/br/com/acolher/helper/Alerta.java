package br.com.acolher.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;

import br.com.acolher.view.MapsActivity;

public class Alerta {

    MapsActivity maps = new MapsActivity();

    public void caixaAlerta(){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(maps);

        msgBox.setTitle("Confirmação.");
        msgBox.setCancelable(true);

        msgBox.setMessage("Deseja realmente sair do aplicativo?");
        msgBox.setPositiveButton("Sim?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                maps.finish();
            }
        });

        msgBox.setNegativeButton("Não?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alerta = msgBox.create();
        alerta.show();
    }

}

