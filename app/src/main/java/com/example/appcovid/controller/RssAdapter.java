package com.example.appcovid.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appcovid.R;
import com.example.appcovid.model.RssItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que construye la vista de cada noticia
 * @author Iván Moriche Damas
 * @author Rodrigo Garcia
 * @author Iustin Mocanu
 * @version 28/05/2021/A
 * @see RecyclerView
 */
public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ViewHolder>
{
    private static List<RssItem> mData;
    private static LayoutInflater mInflater;
    private static ItemClickListener mClickListener;

    /**
     * Contructor de la clase
     * @param context contexto de la actividad
     */
    public RssAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }


    /**
     * Clase que almacena y recicla las vistas a medida que se desplazan fuera de la pantalla
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textView;
        ImageView image;

        public ViewHolder(View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.new_title);
            image = itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view)
        {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    /**
     * Método que infla el diseño de la fila xml cuando sea necesario
     * @param parent p
     * @param viewType v
     * @return new ViewHolder(view)
     */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.element_news, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Método que une los datos TextView e ImageView en cada fila
     * @param holder h
     * @param position posición de la noticia
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.textView.setText(mData.get(position).getmTitle());
        Picasso.get().
                load(mData.get(position).
                        getmImage().
                        getmUrl()).
                fit().
                centerCrop().
                error(R.drawable.ic_img_test).
                into(holder.image);
    }


    /**
     * Método que devuelve el número de noticias
     * @return mData.size()
     */
    @Override
    public int getItemCount()
    {
        return mData.size();
    }


    /**
     * Método que añade todas las noticias a los datos
     * @param items lista de restricciones
     */
    public void addData(List<RssItem> items)
    {
        mData.addAll(items);
        notifyDataSetChanged();
    }


    /**
     * Método que devuelve la noticia del array
     * @param id noticia
     * @return mData.get(id)
     */
    public RssItem getItem(int id)
    {
        return mData.get(id);
    }


    /**
     * Método que permite capturar eventos de clics
     * @param itemClickListener escuchador
     */
    public void setClickListener(ItemClickListener itemClickListener)
    {
        mClickListener = itemClickListener;
    }


    /**
     * Interface que la actividad principal implementará para responder a eventos de clic
     */
    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }
}