package com.iwmf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.data.PendingMediaData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * <p> Display all pending media files in a list. </p>
 */
@SuppressWarnings("ALL")
public class PendingMediaAdapter extends BaseAdapter {

    private final ArrayList<PendingMediaData> listPendingMedia;
    private final LayoutInflater inflater;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd hh:mm", Locale.ENGLISH);

    public PendingMediaAdapter(Context context, ArrayList<PendingMediaData> listPendingMedia) {

        this.listPendingMedia = listPendingMedia;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return listPendingMedia.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_pending_media, parent, false);
            mHolder = new ViewHolder();
            mHolder.txtCreated = (TextView) convertView.findViewById(R.id.txtCreated);
            mHolder.imgMedia = (ImageView) convertView.findViewById(R.id.imgMedia);

            convertView.setTag(mHolder);

        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        PendingMediaData data = listPendingMedia.get(position);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(data.getCreatedDate());

        mHolder.txtCreated.setText(dateFormat.format(cal.getTime()));

        if (data.getMediaType() == 1 || data.getMediaType() == 2) {
            mHolder.imgMedia.setImageResource(R.drawable.icon_media_video);
        } else {
            mHolder.imgMedia.setImageResource(R.drawable.icon_media_pic);
        }

        return convertView;
    }

    private static class ViewHolder {

        TextView txtCreated;
        ImageView imgMedia;
    }
}
