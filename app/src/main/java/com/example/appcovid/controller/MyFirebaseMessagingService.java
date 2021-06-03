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

import android.app.Notification;
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
import com.example.appcovid.views.MainActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * NOTA: Solo puede haber un servicio en cada aplicación que reciba mensajes FCM. Si es múltiple
 * se declaran en el Manifest, luego se elegirá el primero.
 *
 * Para que esta muestra de Java sea funcional, debe eliminar lo siguiente de la mensajería de Kotlin
 * servicio en el AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */

/**
 * Clase que gestiona las notificaciones provenientes del servidor
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see FirebaseMessagingService
 */
import com.example.appcovid.model.BaseActivity;
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            launchNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(String token) {
        BaseActivity.getmDatabase().getReference().child(BaseActivity.Mac).child("FCM_token").setValue(token);
        Log.d("TOKEN", "Refreshed token: " + token);
    }

    public void launchNotification(RemoteMessage message)
    {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                NotificationChannel.DEFAULT_CHANNEL_ID);

        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
        builder.setContentTitle(message.getNotification().getTitle());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getNotification().getBody()));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, builder.build());
    }

}


