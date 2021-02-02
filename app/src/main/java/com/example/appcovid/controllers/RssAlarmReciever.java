package com.example.appcovid.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RssAlarmReciever extends BroadcastReceiver {
    public static final int CODIGO_PEDIDO = 1;
    public static final String ACCION = "com.example.appcovid.RssService";

    //Método activado por la alarma periódicamente (cada 24 horas)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RssService.class);
        //Si hace falta, se le pueden pasar datos a través del Intent
        context.startService(i);
    }
}
