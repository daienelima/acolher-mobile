package br.com.acolher.apiconfig;

import br.com.acolher.service.ServiceApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInit {

    private ServiceApi serviceApi;

    public RetrofitInit() {

        String BASE_URL = "https://acolher.herokuapp.com/api/";
        //String BASE_URL = "http://10.0.2.2:8080/api/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceApi = retrofit.create(ServiceApi.class);
    }

    public ServiceApi getService(){
        return serviceApi;
    }
}
