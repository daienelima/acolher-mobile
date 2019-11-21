package br.com.acolher.helper;

import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import br.com.acolher.model.Notificacao;
import br.com.acolher.model.NotificacaoResponse;
import br.com.acolher.model.SendNotificationModel;
import br.com.acolher.service.ServiceApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FireStore {

    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String TAG = "FIRESTORE";
    public static String hashAtual;
    static CollectionReference users = db.collection("users");

    public static void insertUserId(Integer codigo,String hash){
        Map<String, Object> user = new HashMap<>();
        user.put("codigo", codigo);
        user.put("hash", hash);
        users.document(String.valueOf(codigo)).set(user);
    }

    public static void updateToken(Integer codigo, String hash){
        DocumentReference documentReference = users.document(String.valueOf(codigo));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        documentReference.update("hash", hash);
                    }else{
                        insertUserId(codigo, hash);
                    }
                }
            }
        });
        /*if(cr != null){
            users.document(String.valueOf(codigo)).update("hash", hash);
        }else{
            insertUserId(codigo, hash);
        }*/
    }

    public static String getUserId(Integer codigo){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.get("codigo") == codigo){
                                    hashAtual = (String) document.get("hash");
                                }
                            }
                        }else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return hashAtual;

    }

    public static void enviarNotificacaoVoluntario(String hash){

        Retrofit retrofit = null;

        SendNotificationModel sendNotificationModel = new SendNotificationModel("test", "test");
        Notificacao notificacao = new Notificacao();
        notificacao.setSendNotificationModel(sendNotificationModel);
        notificacao.setToken(hash);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceApi api = retrofit.create(ServiceApi.class);

        Call<ResponseBody> responseBodyCall = api.enviarNotificacao(notificacao);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("notfic","success");
                }else {
                    Log.d("notfic","fail");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        /*OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder request = chain.request().newBuilder().addHeader("Authorization", "");
                return null;
            }
        });

        JSONObject jGcmData = new JSONObject();
        JSONObject jData = new JSONObject();
        jData.put("title", "askdjaosijdoiasjd");
        jData.put("body", "foi visse, firma...");
        jGcmData.put("to", hash);
        jGcmData.put("notification", jData);

        Retrofit retrofit = null;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServiceApi api = retrofit.create(ServiceApi.class);

        Call<NotificacaoResponse> retorno = api.enviarNotificacao(jGcmData);
        retorno.enqueue(new Callback<NotificacaoResponse>() {
            @Override
            public void onResponse(Call<NotificacaoResponse> call, Response<NotificacaoResponse> response) {
                if(response.isSuccessful()){
                    Log.d("notif", "pessoas atingidas -" + response.body().getSuccess());
                }else {
                    Log.d("notif", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<NotificacaoResponse> call, Throwable t) {
                Log.d("notif","erro");
            }
        });*;

        /*try{
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("title", "askdjaosijdoiasjd");
            jData.put("body", "foi visse, firma...");
            jGcmData.put("to", hash);
            jGcmData.put("notification", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key="+CONSTANTES.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            outputStream.write(jGcmData.toString().getBytes());

            InputStream inputStream = conn.getInputStream();

            Log.d("not", inputStream.toString());
            Log.d("not", "Enviou, visse firma...");

        }catch (MalformedURLException ex){
            ex.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException json){
            json.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

}
