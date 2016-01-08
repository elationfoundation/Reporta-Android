package com.iwmf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.data.LocationData;

import java.util.ArrayList;

/**
 * <p> Displays available nearby locations to the user. </p>
 */
public class LocationListAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private ArrayList<LocationData> listData = null;

    public LocationListAdapter(Context context, ArrayList<LocationData> list) {

        mInflater = LayoutInflater.from(context);
        this.listData = list;
    }

    @Override
    public int getCount() {

        return listData.size();
    }

    @Override
    public Object getItem(int position) {

        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            final ViewHolder mHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_location_list, parent, false);
                mHolder = new ViewHolder();
                mHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
                mHolder.txtDistance = (TextView) convertView.findViewById(R.id.txtDistance);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }

            LocationData data = listData.get(position);

            mHolder.txtName.setText(data.getName());
            mHolder.txtDistance.setText(data.getDistanceString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ViewHolder {

        TextView txtName = null, txtDistance = null;
    }
}
