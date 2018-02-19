package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder>{

    List<PostalCode> data;

    public void setData(List<PostalCode> data) {
        this.data = data;
    }

    public RecyclerViewAdapter(List<PostalCode> data) {
        this.data = data;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items_list,parent,false);
        DataViewHolder holder = new DataViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder{

        TextView mPlace, mPostalCode, mCountry;

        public DataViewHolder(View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.recycler_item_place_name);
            mPostalCode = itemView.findViewById(R.id.recycler_item_postal_code);
            mCountry = itemView.findViewById(R.id.recycler_item_country_code);
        }

        public void bind(PostalCode postalCode) {
            mPlace.setText(postalCode.getPlace());
            mPostalCode.setText( "Postal code: " +  postalCode.getPostalCode());
            mCountry.setText("Country: " + postalCode.getCountryCode());
        }
    }
}
