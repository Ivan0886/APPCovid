package com.example.appcovid.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.appcovid.R;
import com.example.appcovid.model.BaseActivity;

/**
 * Clase que contiene la pantalla inicial de la App
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see BaseActivity
 */
public class MainActivity extends BaseActivity
{
    /**
     * Método que se ejecuta al arrancar la App.
     * @param savedInstanceState instancia de la actividad.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * Método que lanza una actividad distinta dependiendo del boton pulsado
     * @param v botón
     */
    @SuppressLint("NonConstantResourceId")
    public void onClickLanzarActivity(View v)
    {
        int id = v.getId();

        switch(id)
        {
            case R.id.button_news:
                startActivity(new Intent(this, NewsActivity.class));
                break;
            case R.id.button_restriction:
                startActivity(new Intent(this, RestrictionsActivity.class));
                break;
            default:
                startActivity(new Intent(this, StateActivity.class));
        }
    }
}