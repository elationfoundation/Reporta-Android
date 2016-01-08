package com.iwmf.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.data.SelectLanguageData;

import java.util.ArrayList;
import java.util.Locale;

/**
 * <p> Give list of all available languages to the user to choose from. </p>
 */
@SuppressWarnings("ALL")
public class SelectLanguageListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final ArrayList<SelectLanguageData> list;
    private final ArrayList<SelectLanguageData> listTemp;
    private int prePos = 0;

    public SelectLanguageListAdapter(Context context, ArrayList<SelectLanguageData> list) {

        this.list = list;

        this.listTemp = new ArrayList<>();
        this.listTemp.addAll(list);

        mInflater = LayoutInflater.from(context);
    }

    public int getPrePos() {

        return prePos;
    }

    public void setPrePos(int prePos) {

        this.prePos = prePos;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public SelectLanguageData getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public ArrayList<SelectLanguageData> getSelectedItem() {

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

        SelectLanguageData data = list.get(position);

        mHolder.txtItem.setText(Html.fromHtml(data.getName()));

        mHolder.chkItem.setChecked(data.isChecked());

        return convertView;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(listTemp);
        } else {
            for (SelectLanguageData mData : listTemp) {

                String search = mData.getName() + " " + mData.getCode();

                if (search.toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(mData);
                }

            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {

        CheckBox chkItem;
        TextView txtItem = null;
    }
}
