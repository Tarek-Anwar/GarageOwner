package com.homegarage.garageowner.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.homegarage.garageowner.R;

import java.util.Map;
import java.util.Random;

public class FirebaseInstanceService extends FirebaseMessagingService {
/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        else
            showNotification(remoteMessage.getData());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(Map<String, String> date){

        String title = date.get("title");
        String  body = date.get("body");

        NotificationManager NotiManeger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String Noti_Channel_ID = "homegarage.garageowner.service";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notiChan1 = new NotificationChannel(Noti_Channel_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notiChan1.setDescription("Noti-fication");
            notiChan1.enableLights(true);
            notiChan1.setLightColor(Color.BLUE);
            notiChan1.enableLights(true);
            NotiManeger.createNotificationChannel(notiChan1);
        }
        NotificationCompat.Builder notiBuild = new NotificationCompat.Builder(this, Noti_Channel_ID);
        notiBuild.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        NotiManeger.notify(new Random().nextInt(), notiBuild.build());
    }

    private void showNotification(String title, String body){

        NotificationManager NotiManeger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String Noti_Channel_ID = "homegarage.garageowner.service";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notiChan1 = new NotificationChannel(Noti_Channel_ID,"Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notiChan1.setDescription("Noti-fication");
            notiChan1.enableLights(true);
            notiChan1.setLightColor(Color.BLUE);
            notiChan1.enableLights(true);
            NotiManeger.createNotificationChannel(notiChan1);
        }
        NotificationCompat.Builder notiBuild = new NotificationCompat.Builder(this, Noti_Channel_ID);
        notiBuild.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        NotiManeger.notify(new Random().nextInt(), notiBuild.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE", s);
    }*/
}

