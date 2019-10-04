package br.com.acolher.viacep;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.com.acolher.model.ViaCep;


public class HttpService extends AsyncTask<Void, Void, ViaCep> {

    private final String myUrl;
    private final String cep;

    public HttpService(String cep) {
        this.cep = cep;
        myUrl = "https://viacep.com.br/ws/" + cep + "/json/";
    }

    @Override
    protected ViaCep doInBackground(Void... voids) {
        StringBuilder resposta = new StringBuilder();

        if(cep != null && cep.length() == 8){
            try {
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.connect();

                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    resposta.append(scanner.next());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Gson().fromJson(resposta.toString(), ViaCep.class);
    }
}
