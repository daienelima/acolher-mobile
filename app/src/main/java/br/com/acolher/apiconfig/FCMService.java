package br.com.acolher.apiconfig;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.acolher.R;
import br.com.acolher.view.HomeMapFragment;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("candido", remoteMessage.getMessageId());
        System.out.println("Candido log " + remoteMessage.getMessageId());

        Map<String, String> data = remoteMessage.getData();

        if(data == null || data.get("sender") == null) return;

        final Intent ii = new Intent(this, HomeMapFragment.class);

        ii.putExtra("nomeDestinatario",data.get("remetente"));
        ii.putExtra("idDestinatario",data.get("idUsuario"));

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii,0);

        NotificationManager n = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationChannelId = "my_channel_id_01";

        NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId,"My Notification", IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Channel description");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);

        n.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),notificationChannelId);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.heart_pin);
        builder.setContentTitle(data.get("title"));
        builder.setContentText(data.get("body"));
        builder.setContentIntent(pendingIntent);

        n.notify(1,builder.build());
    }
}
