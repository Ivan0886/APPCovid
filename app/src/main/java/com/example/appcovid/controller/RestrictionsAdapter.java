package com.example.appcovid.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appcovid.R;
import com.example.appcovid.model.RestrictionsItems;

import java.util.ArrayList;
import java.util.List;

public class RestrictionsAdapter extends RecyclerView.Adapter<com.example.appcovid.controller.RestrictionsAdapter.ViewHolder> {

        private static List<RestrictionsItems> mData;
        private static LayoutInflater mInflater;

        public RestrictionsAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mData = new ArrayList<RestrictionsItems>();
        }


        // Almacena y recicla las vistas a medida que se desplazan fuera de la pantalla
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView textView, textView2;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.restriction_title);
                textView2 = itemView.findViewById(R.id.restriction_text);
            }


            @Override
            public void onClick(View view) {
            }
        }


        // Infla el diseño de la fila xml cuando sea necesario
        @Override
        public com.example.appcovid.controller.RestrictionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.element_restrictions, parent, false);
            return new com.example.appcovid.controller.RestrictionsAdapter.ViewHolder(view);
        }


        // Une los datos TextView e ImageView en cada fila
        @Override
        public void onBindViewHolder(com.example.appcovid.controller.RestrictionsAdapter.ViewHolder holder, int position) {
            holder.textView.setText(mData.get(position).getmTitle());
            holder.textView2.setText(mData.get(position).getmDescription());
        }


        // Numero de RestrictionsItem
        @Override
        public int getItemCount() {
            return mData.size();
        }


        public void addData(List<RestrictionsItems> items) {
            mData.addAll(items);
            notifyDataSetChanged();
        }


        // Devuelve el RestrictionsItem del array
        public RestrictionsItems getItem(int id) {
            return mData.get(id);
        }

}
