package com.iwmf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.data.Contacts;
import com.iwmf.views.stickylistheaders.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * <p> Display all contacts. Inflate layout row_contact_names_with_arrow to display contact.
 * <p/>
 * This adapter is called from AllContactsFragment. </p>
 */
public class AllContactsAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    private final int[] mSectionIndices;
    private final Character[] mSectionLetters;
    private final LayoutInflater mInflater;
    private ArrayList<Contacts> mArrayList = null;
    private ArrayList<Contacts> mArrayListTemp = null;

    public AllContactsAdapter(ArrayList<Contacts> mContacts, Context context) {

        mInflater = LayoutInflater.from(context);
        mArrayList = mContacts;
        mArrayListTemp = new ArrayList<>();
        mArrayListTemp.addAll(mArrayList);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {

        ArrayList<Integer> sectionIndices = new ArrayList<>();
        char lastFirstChar = mArrayList.get(0).getFirstname().charAt(0);
        sectionIndices.add(0);
        for (int i = 1; i < mArrayList.size(); i++) {
            char tempChar = mArrayList.get(i).getFirstname().charAt(0);
            if (tempChar != lastFirstChar) {
                lastFirstChar = tempChar;
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {

        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = mArrayList.get(mSectionIndices[i]).getFirstname().toUpperCase(Locale.getDefault()).charAt(0);
        }
        return letters;
    }

    @Override
    public int getCount() {

        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {

        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.row_contact_names_with_arrow, parent, false);
            holder = new ViewHolder();

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.imgvLock = (ImageView) convertView.findViewById(R.id.imgvLock);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mArrayList.get(position) != null) {

            try {

                holder.txtName.setText(mArrayList.get(position).getFirstname() + " " + mArrayList.get(position).getLastname());

                if (mArrayList.get(position).getContact_type().equals("2")) {

                    holder.imgvLock.setImageResource(0);

                } else if (mArrayList.get(position).getSos_enabled().equals("1")) {

                    holder.imgvLock.setImageResource(R.drawable.lock);

                } else if (mArrayList.get(position).getSos_enabled().equals("2")) {

                    holder.imgvLock.setImageResource(R.drawable.lock_pending);

                } else {

                    holder.imgvLock.setImageResource(0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.row_section_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.txtAlphabate);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        CharSequence headerChar = mArrayList.get(position).getLastname().subSequence(0, 1);
        holder.text.setText(headerChar.toString().toUpperCase(Locale.getDefault()));

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {

        // return the first character of the country as ID because this is what
        // headers are based upon
        return mArrayList.get(position).getLastname().subSequence(0, 1).charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {

        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {

        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {

        return mSectionLetters;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mArrayList.clear();
        if (charText.length() == 0) {
            mArrayList.addAll(mArrayListTemp);
        } else {
            for (Contacts mData : mArrayListTemp) {
                if ((mData.getFirstname() + " " + mData.getLastname()).toLowerCase(Locale.getDefault()).contains(charText)) {
                    mArrayList.add(mData);
                }
            }
        }
        notifyDataSetChanged();
    }

    class HeaderViewHolder {

        TextView text;
    }

    class ViewHolder {

        public TextView txtName = null;
        public ImageView imgvLock = null;
    }
}
