package com.example.appcovid.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appcovid.R;
import com.example.appcovid.model.RssItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ViewHolder> {

    private static List<RssItem> mData;
    private static LayoutInflater mInflater;
    private static ItemClickListener mClickListener;

    public RssAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<RssItem>();
    }


    // Almacena y recicla las vistas a medida que se desplazan fuera de la pantalla
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.new_title);
            image = itemView.findViewById(R.id.image_new);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // Infla el diseño de la fila xml cuando sea necesario
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.element_news, parent, false);
        return new ViewHolder(view);
    }


    // Une los datos TextView e ImageView en cada fila
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mData.get(position).getTitle());
        Picasso.get().
                load(mData.get(position).
                        getImage().
                        getUrl()).
                fit().
                centerCrop().
                error(R.drawable.img_prueba).
                into(holder.image);
    }


    // Numero de RssItem
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void addData(List<RssItem> items) {
        mData.addAll(items);
        notifyDataSetChanged();
    }


    // Devuelve el RssItem del array
    public RssItem getItem(int id) {
        return mData.get(id);
    }


    // Permite capturar eventos de clics
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    // La actividad principal implementará este método para responder a eventos de clic
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}