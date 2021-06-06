/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appcovid.controller;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

/**
 * Clase que gestiona las notificaciones en primer plano provenientes del servidor
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 03/06/2021/A
 * @see FirebaseMessagingService
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    /**
     * Método que lanza una notificación cuando se recibe mensaje desde el servidor
     * @param remoteMessage mensaje
     */
    @RequiresApi(api = Build.VERSION_CODES.O) @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        if (remoteMessage.getNotification() != null)
        {
            launchNotification(remoteMessage);
        }
    }

    /**
     * Método que actualiza el token de registro si se da alguno de los siguientes casos:
     * 1 - La app se restablece en un dispositivo nuevo.
     * 2 - El usuario desinstala y vuelve a instalar la app.
     * 3 - El usuario borra los datos de la app.
     * @param token token
     */
    @Override
    public void onNewToken(@NonNull String token)
    {
        if (!BaseActivity.Mac.equals(""))
        {
            BaseActivity.getmRef().child(BaseActivity.Mac).child("FCM_token").setValue(token);
        }
    }


    /**
     * Método que lanza la notificación
     * @param message mensaje
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launchNotification(RemoteMessage message)
    {
        Intent intent = new Intent(this, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_stat_ic_icon_covid_foreground);
        builder.setContentTitle(Objects.requireNonNull(message.getNotification()).getTitle());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }
}