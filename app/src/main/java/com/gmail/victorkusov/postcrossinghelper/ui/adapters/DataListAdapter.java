package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class DataListAdapter extends BaseAdapter {

    private List<PostalCode> mPostalCodeList;

    public DataListAdapter(List<PostalCode> postalCodeList) {
        mPostalCodeList = postalCodeList;
    }

    public void setPostalCodeList(List<PostalCode> postalCodeList) {
        mPostalCodeList = postalCodeList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_items_list,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PostalCode postalCode = (PostalCode) getItem(position);
        holder.txtPlaceName.setText(postalCode.getPlace());
        holder.txtPostalCode.setText("Postal code: " + postalCode.getPostalCode());
        holder.txtCountryCode.setText("Conutry: " + postalCode.getCountryCode());

        return convertView;
    }


    class ViewHolder {
        private TextView txtPlaceName;
        private TextView txtPostalCode;
        private TextView txtCountryCode;

        ViewHolder(View view) {
            txtPlaceName = view.findViewById(R.id.simple_item_place_name);
            txtPostalCode = view.findViewById(R.id.simple_item_postal_code);
            txtCountryCode = view.findViewById(R.id.simple_item_country_code);
        }
    }

//    private List<PostalCode> mCodes;
//
//    public DataListAdapter(@NonNull Context context, int resource, @NonNull List<PostalCode> objects) {
//        super(context, resource, objects);
//        mCodes = objects;
//    }
//
//    @Override
//    public int getCount() {
//        return mCodes.size();
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        ViewHolder holder;
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_items_list, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        holder.bind(mCodes.get(position));
//        return convertView;
//    }
//
//    public void setCodes(List<PostalCode> codes) {
//        mCodes = codes;
//        notifyDataSetChanged();
//    }
//
//
//    class ViewHolder {
//        private TextView txtPlaceName;
//        private TextView txtPostalCode;
//        private TextView txtCountryCode;
//
//        ViewHolder(View view) {
//            txtPlaceName = view.findViewById(R.id.simple_item_place_name);
//            txtPostalCode = view.findViewById(R.id.simple_item_postal_code);
//            txtCountryCode = view.findViewById(R.id.simple_item_country_code);
//        }
//
//        void bind(PostalCode postalCode) {
//            txtPlaceName.setText(postalCode.getPlace());
//            txtPostalCode.setText("Postal code: " + postalCode.getPostalCode());
//            txtCountryCode.setText("Conutry: " + postalCode.getCountryCode());
//        }
//    }

}
