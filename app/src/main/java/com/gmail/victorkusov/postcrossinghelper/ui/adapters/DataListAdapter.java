package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class DataListAdapter extends BaseAdapter {

    private List<PostalCode> mPostalCodeList;
    private OnItemClickListener mClickListener;

    public void setPostalCodeList(List<PostalCode> postalCodeList, OnItemClickListener clickListener) {
        mPostalCodeList = postalCodeList;
        mClickListener = clickListener;
        notifyDataSetChanged();
    }

    public List<PostalCode> getPostalCodeList() {
        return mPostalCodeList;
    }

    @Override
    public int getCount() {
        return mPostalCodeList.size();
    }

    @Override
    public PostalCode getItem(int position) {
        return mPostalCodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_items_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PostalCode postalCode = getItem(position);
        String postalCodeText = "Postal code: " + postalCode.getPostalCode();
        String countryText = "Conutry: " + postalCode.getCountryCode();

        holder.txtPlaceName.setText(postalCode.getPlace());
        holder.txtPostalCode.setText(postalCodeText);
        holder.txtCountryCode.setText(countryText);
        holder.mButtonSave.setTag(position);

        if(postalCode.isSavedToFirebase() != null) {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mButtonSave.setVisibility(View.VISIBLE);
            holder.mButtonSave.setImageDrawable(postalCode.isSavedToFirebase() ?
                    ContextCompat.getDrawable(holder.mButtonSave.getContext(), R.drawable.ic_delete_24dp) :
                    ContextCompat.getDrawable(holder.mButtonSave.getContext(), R.drawable.ic_save_blue_24dp));
        } else {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mButtonSave.setVisibility(View.GONE);
        }

        holder.mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(position);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView txtPlaceName;
        private TextView txtPostalCode;
        private TextView txtCountryCode;
        private ImageButton mButtonSave;
        private ProgressBar mProgressBar;

        ViewHolder(View view) {
            txtPlaceName = view.findViewById(R.id.simple_item_place_name);
            txtPostalCode = view.findViewById(R.id.simple_item_postal_code);
            txtCountryCode = view.findViewById(R.id.simple_item_country_code);
            mButtonSave = view.findViewById(R.id.button_save);
            mProgressBar = view.findViewById(R.id.button_save_loading);
        }
    }
}
