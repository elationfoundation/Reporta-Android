package com.iwmf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.data.FacingIssueData;

import java.util.ArrayList;

/**
 * <p> Display situations for user facing activity. Inflate layout row_single_choice_item_line to display situation.
 * <p/>
 * This adapter is called from IamFacingFragment. </p>
 */
public class IamFacingListAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private ArrayList<FacingIssueData> list = null;

    public IamFacingListAdapter(Context context, ArrayList<FacingIssueData> list) {

        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public ArrayList<FacingIssueData> getSelectedItem() {

        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder mHolder;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.row_single_choice_item_line, parent, false);

            mHolder = new ViewHolder();

            mHolder.txtItem = (TextView) convertView.findViewById(R.id.txtItem);
            mHolder.chkItem = (CheckBox) convertView.findViewById(R.id.chkItem);
            mHolder.chkItem.setFocusable(false);
            mHolder.chkItem.setClickable(false);
            mHolder.chkItem.setFocusableInTouchMode(false);
            mHolder.chkItem.setEnabled(false);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        FacingIssueData data = list.get(position);

        mHolder.txtItem.setText(data.getName());

        mHolder.chkItem.setChecked(data.isChecked());

        return convertView;
    }

    private static class ViewHolder {

        CheckBox chkItem = null;
        TextView txtItem = null;
    }
}
