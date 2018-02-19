package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.R;
import com.gmail.victorkusov.postcrossinghelper.model.PostalCode;

import java.util.List;

public class DataListAdapter extends ArrayAdapter<PostalCode> {


    private List<PostalCode> mCodes;


    public DataListAdapter(@NonNull Context context, int resource, @NonNull List<PostalCode> objects) {
        super(context, resource, objects);
        mCodes = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PostalCode postalCode = mCodes.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_items_list, parent, false);
        }

        TextView txtPlaceName = convertView.findViewById(R.id.simple_item_place_name);
        TextView txtPostalCode = convertView.findViewById(R.id.simple_item_postal_code);
        TextView txtCountryCode = convertView.findViewById(R.id.simple_item_country_code);

        txtPlaceName.setText(postalCode.getPlace());
        txtPostalCode.setText("Postal code: " + postalCode.getPostalCode());
        txtCountryCode.setText("Conutry: " + postalCode.getCountryCode());

        return convertView;
    }


    public List<PostalCode> getCodes() {
        return mCodes;
    }

    public void setCodes(List<PostalCode> codes) {
        mCodes = codes;
    }

}
