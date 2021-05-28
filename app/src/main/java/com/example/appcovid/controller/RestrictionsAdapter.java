package com.example.appcovid.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcovid.R;
import com.example.appcovid.model.RestrictionsItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que construye la vista de cada restricción
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RecyclerView
 */
public class RestrictionsAdapter extends RecyclerView.Adapter<RestrictionsAdapter.ViewHolder>
{
    private static List<RestrictionsItems> mData;
    private static LayoutInflater mInflater;

    /**
     * Contructor de la clase
     * @param context contexto de la actividad
     */
    public RestrictionsAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }


    /**
     * Clase que almacena y recicla las vistas a medida que se desplazan fuera de la pantalla
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textView, textView2;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.restriction_title);
            textView2 = itemView.findViewById(R.id.restriction_text);
        }

        @Override
        public void onClick(View view) { }
    }


    /**
     * Método que infla el diseño de la fila xml cuando sea necesario
     * @param parent p
     * @param viewType v
     * @return new ViewHolder(view)
     */
    @NonNull
    @Override
    public RestrictionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.element_restrictions, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Método que une los datos TextView e ImageView en cada fila
     * @param holder h
     * @param position posición de la restricción
     */
    @Override
    public void onBindViewHolder(RestrictionsAdapter.ViewHolder holder, int position)
    {
        holder.textView.setText(mData.get(position).getmTitle());
        holder.textView2.setText(mData.get(position).getmDescription());
    }


    /**
     * Método que devuelve el número de restriciones
     * @return mData.size()
     */
    @Override
    public int getItemCount()
    {
        return mData.size();
    }


    /**
     * Método que añade todas las restricciones a los datos
     * @param items lista de restricciones
     */
    public void addData(List<RestrictionsItems> items)
    {
        mData.addAll(items);
        notifyDataSetChanged();
    }
}