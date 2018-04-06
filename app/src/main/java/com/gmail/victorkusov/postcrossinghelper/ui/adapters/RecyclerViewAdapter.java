package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.Place;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder> {

    private List<Place> queryData;
    private OnItemClickListener mClickListener;

    public void setData(List<Place> queryData, OnItemClickListener listener) {
        this.queryData = queryData;
        mClickListener = listener;
        notifyDataSetChanged();
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items_list, parent, false);
        return new DataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DataViewHolder holder, int position) {
        final Place postalCode = queryData.get(position);

        holder.mPlace.setText(postalCode.getPlaceName());
        holder.mPostalCode.setText("Postal code: " + postalCode.getPostalCode());
        holder.mCountry.setText("Country: " + postalCode.getCountryCode());

        holder.mProgressBar.setVisibility(View.GONE);
        holder.mButtonSave.setVisibility(View.VISIBLE);

        holder.mButtonSave.setImageDrawable(postalCode.isSavedToFirebase() ?
                ContextCompat.getDrawable(holder.mButtonSave.getContext(), R.drawable.ic_delete_24dp) :
                ContextCompat.getDrawable(holder.mButtonSave.getContext(), R.drawable.ic_save_blue_24dp));

        holder.mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return queryData.size();
    }

    public Place getItem(int position) {
        return queryData.get(position);
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        private TextView mPlace;
        private TextView mPostalCode;
        private TextView mCountry;
        private ImageButton mButtonSave;
        private ProgressBar mProgressBar;

        DataViewHolder(final View itemView) {
            super(itemView);

            mPlace = itemView.findViewById(R.id.recycler_item_place_name);
            mPostalCode = itemView.findViewById(R.id.recycler_item_postal_code);
            mCountry = itemView.findViewById(R.id.recycler_item_country_code);
            mButtonSave = itemView.findViewById(R.id.button_save);
            mProgressBar = itemView.findViewById(R.id.button_save_loading);
        }
    }
}
