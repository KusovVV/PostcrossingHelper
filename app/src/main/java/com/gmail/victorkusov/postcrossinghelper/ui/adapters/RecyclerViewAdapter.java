package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder> {

    private List<PostalCode> queryData;
    private OnItemClickListener mClickListener;

    public void setData(List<PostalCode> queryData) {
        this.queryData = queryData;

        notifyDataSetChanged();
    }

    public RecyclerViewAdapter(List<PostalCode> queryData, OnItemClickListener listener) {
        this.queryData = queryData;
        this.mClickListener = listener;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items_list, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, final int position) {
        final PostalCode postalCode = queryData.get(position);

        holder.mPlace.setText(postalCode.getPlace());
        holder.mPostalCode.setText("Postal code: " + postalCode.getPostalCode());
        holder.mCountry.setText("Country: " + postalCode.getCountryCode());

        holder.mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(postalCode);
            }
        });

    }


    @Override
    public int getItemCount() {
        return queryData.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        TextView mPlace;
        TextView mPostalCode;
        TextView mCountry;
        ImageButton mButtonSave;

        DataViewHolder(final View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.recycler_item_place_name);
            mPostalCode = itemView.findViewById(R.id.recycler_item_postal_code);
            mCountry = itemView.findViewById(R.id.recycler_item_country_code);
            mButtonSave = itemView.findViewById(R.id.button_save);
        }
    }
}
