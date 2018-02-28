package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class DataListAdapter extends BaseAdapter {

    private List<PostalCode> mPostalCodeList;
    private OnItemClickListener mListener;

    public DataListAdapter(List<PostalCode> postalCodeList, OnItemClickListener listener) {
        mPostalCodeList = postalCodeList;
        mListener = listener;
    }

    public void setPostalCodeList(List<PostalCode> postalCodeList) {
        mPostalCodeList = postalCodeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPostalCodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPostalCodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_items_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PostalCode postalCode = (PostalCode) getItem(position);
        holder.txtPlaceName.setText(postalCode.getPlace());
        holder.txtPostalCode.setText("Postal code: " + postalCode.getPostalCode());
        holder.txtCountryCode.setText("Conutry: " + postalCode.getCountryCode());

        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(postalCode);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView txtPlaceName;
        private TextView txtPostalCode;
        private TextView txtCountryCode;
        private ImageButton btnSave;

        ViewHolder(View view) {
            txtPlaceName = view.findViewById(R.id.simple_item_place_name);
            txtPostalCode = view.findViewById(R.id.simple_item_postal_code);
            txtCountryCode = view.findViewById(R.id.simple_item_country_code);
            btnSave = view.findViewById(R.id.button_save);
        }
    }
}
