package com.example.appcovid.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
public class RestrictionsAdapter extends ArrayAdapter<RestrictionsItems>
{
    private List<RestrictionsItems> mRestrictionsItems;
    private Context mContext;
    private int mResourceLayout;

    public RestrictionsAdapter(@NonNull Context context, int resource, List<RestrictionsItems> objects) {
        super(context, resource);
        this.mContext = context;
        this.mResourceLayout = resource;
        this.mRestrictionsItems = objects;
    }

    @Override
    public int getCount() {
        return mRestrictionsItems.size();
    }

    @Nullable
    @Override
    public RestrictionsItems getItem(int position) {
        return mRestrictionsItems.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        Log.d("Holaaaa", "posfds");

        if(view == null) {
            Log.d("Holaaaa2", "posfds");
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