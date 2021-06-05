package com.example.appcovid.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appcovid.R;
import com.example.appcovid.model.RestrictionsItems;

import java.util.List;

/**
 * Clase que construye la vista de cada restricción
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see ArrayAdapter
 * @see RestrictionsItems
 */
public class RestrictionsAdapter extends ArrayAdapter<RestrictionsItems>
{
    private List<RestrictionsItems> mRestrictionsItems;
    private Context mContext;
    private int mResourceLayout;

    /**
     * Contructor de la clase
     * @param context contexto de la aplicación
     * @param resource r
     * @param objects lista de restricciones
     */
    public RestrictionsAdapter(@NonNull Context context, int resource, List<RestrictionsItems> objects)
    {
        super(context, resource);
        this.mContext = context;
        this.mResourceLayout = resource;
        this.mRestrictionsItems = objects;
    }


    /**
     * Método que devuelve el número de restricciones
     * @return mRestrictionsItems.size()
     */
    @Override
    public int getCount() {
        return mRestrictionsItems.size();
    }


    /**
     * Método que devulve la restricción según su posición
     * @param position posición de la restricción
     * @return mRestrictionsItems.get(position)
     */
    @Nullable @Override
    public RestrictionsItems getItem(int position) {
        return mRestrictionsItems.get(position);
    }


    /**
     * Método que devulve la vista
     * @param position p
     * @param convertView cv
     * @param parent p
     * @return view
     */
    @SuppressLint("InflateParams") @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.element_restrictions, null);
        }

        RestrictionsItems item = mRestrictionsItems.get(position);

        TextView textTitle = view.findViewById(R.id.restriction_title);
        textTitle.setText(item.getmTitle());

        TextView textDescription = view.findViewById(R.id.restriction_text);
        textDescription.setText(item.getmDescription());

        return view;
    }
}