package com.gmail.victorkusov.postcrossinghelper.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.victorkusov.postcrossinghelper.model.InputData;
import com.gmail.victorkusov.postcrossinghelper.R;

import java.util.List;

public class TrackCodeAdapter extends BaseAdapter{

    private List<InputData> mDataList;

    public void setDataList(List<InputData> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public List<InputData> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final TrackHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_dialog_list,parent,false);
            holder = new TrackHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TrackHolder) convertView.getTag();
        }

        InputData data = mDataList.get(position);

        holder.mDateTime.setText(data.getDateTime());
        holder.mStatus.setText(data.getEvent());
        holder.mPlaceName.setText(data.getPlace());

        return convertView;
    }

    class TrackHolder{
        private TextView mDateTime;
        private TextView mStatus;
        private TextView mPlaceName;

        TrackHolder(View view) {
            mDateTime = view.findViewById(R.id.dialog_list_item_date_time);
            mStatus = view.findViewById(R.id.dialog_list_item_status);
            mPlaceName= view.findViewById(R.id.dialog_list_item_place_name);
        }
    }
}
