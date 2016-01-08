package com.iwmf.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Associated_circles;
import com.iwmf.data.ContactListData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.Toast;
import com.iwmf.views.stickylistheaders.StickyListHeadersAdapter;
import com.iwmf.views.stickylistheaders.StickyListHeadersListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p> Display all circle list. </p>
 */
public class CircleListFragment extends BaseFragment {

    private static ArrayList<ContactListData> mContactListDatas = null;
    private static String contact_id = "";
    private static String associated_id = "";
    private StickyListHeadersListView listView = null;
    private CustomAdapter mCustomAdapter = null;
    private EditText edt_search = null;

    public static CircleListFragment getInstance(String contact_id, String associated_id) {

        CircleListFragment mFragment = new CircleListFragment();

        CircleListFragment.contact_id = contact_id;
        CircleListFragment.associated_id = associated_id;

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_all_contacts, container, false);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        displayProgressDialog();

        listView = (StickyListHeadersListView) v.findViewById(R.id.listView);

        TextView txtSave = (TextView) v.findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) v.findViewById(R.id.txtCancel);

        listView.setEmptyView(v.findViewById(R.id.txtNoDataFound));
        edt_search = (EditText) v.findViewById(R.id.edt_search);

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        getContactList();

        if (mContactListDatas == null) {
            mContactListDatas = new ArrayList<>();
        }

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());

                mCustomAdapter.filter(text);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                boolean is = associated_id.contains(mContactListDatas.get(position).getContactlist_id());

                if (!is) {
                    if (associated_id.trim().equals("")) {
                        associated_id = mContactListDatas.get(position).getContactlist_id();
                    } else {
                        associated_id += ("," + mContactListDatas.get(position).getContactlist_id());
                    }
                } else {
                    associated_id = associated_id.replace(mContactListDatas.get(position).getContactlist_id(), "").replace(",,", ",");
                }

                mContactListDatas.get(position).setIs_associated(is ? "0" : "1");

                ContactAddManualFragment.mContacts.setAssociated_circles(new ArrayList<Associated_circles>());

                Associated_circles mAssociated_circles;

                ArrayList<Associated_circles> mArrayList_ = new ArrayList<>();

                for (int i = 0; i < mContactListDatas.size(); i++) {

                    if (mContactListDatas.get(i).getIs_associated().equals("1")) {

                        mAssociated_circles = new Associated_circles();

                        mAssociated_circles.setCircle(mContactListDatas.get(i).getCircle());
                        mAssociated_circles.setContactlist_id(mContactListDatas.get(i).getContactlist_id());
                        mAssociated_circles.setListname(mContactListDatas.get(i).getListname());

                        mArrayList_.add(mAssociated_circles);
                    }
                }

                ContactAddManualFragment.mContacts.setAssociated_circles(mArrayList_);

                mCustomAdapter.notifyDataSetChanged();

                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                }

            }
        });

    }

    private void getContactList() {

        try {

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ALLCIRCLEWITHSTATUS, RequestMethod.POST, RequestBuilder.getAllCircleWithStatusRequest(contact_id));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {
                            if (jsonObject.get("status").toString().equals("1")) {

                                mContactListDatas = new Gson().fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<ContactListData>>() {
                                }.getType());

                                if (mContactListDatas == null) {
                                    mContactListDatas = new ArrayList<>();
                                }

                                mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());
                                listView.setAdapter(mCustomAdapter);

                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
                            }
                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissProgressDialog();
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private static class ViewHolder {

        public TextView txtName = null, txtCircle = null;
        public CheckBox chkItem = null;
    }

    private static class HeaderViewHolder {

        TextView text;
    }

    public static class CustomAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private final int[] mSectionIndices;
        private final Character[] mSectionLetters;
        private final LayoutInflater vi;
        private ArrayList<ContactListData> mArrayList = null;
        private ArrayList<ContactListData> mArrayListTemp = null;
        private Context mContext = null;

        public CustomAdapter(ArrayList<ContactListData> mContacts, Context mContext) {

            this.mArrayList = mContacts;

            this.mArrayListTemp = new ArrayList<>();
            this.mArrayListTemp.addAll(mArrayList);
            this.mContext = mContext;

            mSectionIndices = getSectionIndices();
            mSectionLetters = getSectionLetters();

            this.vi = LayoutInflater.from(mContext);
        }

        private int[] getSectionIndices() {

            ArrayList<Integer> sectionIndices = new ArrayList<>();

            int[] sections = null;

            if (mArrayList != null && mArrayList.size() > 0) {

                char lastFirstChar = mArrayList.get(0).getListname().charAt(0);
                sectionIndices.add(0);
                for (int i = 1; i < mArrayList.size(); i++) {
                    if (mArrayList.get(i).getListname().charAt(0) != lastFirstChar) {
                        lastFirstChar = mArrayList.get(i).getListname().charAt(0);
                        sectionIndices.add(i);
                    }
                }
                sections = new int[sectionIndices.size()];
                for (int i = 0; i < sectionIndices.size(); i++) {
                    sections[i] = sectionIndices.get(i);
                }
            }
            return sections;
        }

        private Character[] getSectionLetters() {

            Character[] letters = new Character[mSectionIndices.length];
            for (int i = 0; i < mSectionIndices.length; i++) {
                letters[i] = mArrayList.get(mSectionIndices[i]).getListname().toUpperCase(Locale.getDefault()).charAt(0);
            }
            return letters;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {

                convertView = vi.inflate(R.layout.row_contact_list_names_with_arrow, parent, false);
                holder = new ViewHolder();

                holder.txtCircle = (TextView) convertView.findViewById(R.id.txtCircle);
                holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
                holder.chkItem = (CheckBox) convertView.findViewById(R.id.chkItem);
                holder.chkItem.setFocusable(false);
                holder.chkItem.setClickable(false);
                holder.chkItem.setFocusableInTouchMode(false);
                holder.chkItem.setEnabled(false);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (mArrayList.get(position) != null) {

                try {

                    holder.txtName.setText(mArrayList.get(position).getListname());
                    holder.txtCircle.setText(mArrayList.get(position).getCircle().equals("1") ? mContext.getString(R.string.private_) : mContext.getString(R.string.public_));

                    holder.chkItem.setChecked(associated_id.contains(mArrayList.get(position).getContactlist_id()));

                    mArrayList.get(position).setIs_associated(holder.chkItem.isChecked() ? "1" : "0");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return convertView;
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

        public void filter(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());
            mArrayList.clear();
            if (charText.length() == 0) {
                mArrayList.addAll(mArrayListTemp);
            } else {
                for (ContactListData mData : mArrayListTemp) {
                    String search = mData.getListname();
                    if (search.toLowerCase(Locale.getDefault()).contains(charText)) {
                        mArrayList.add(mData);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public Object[] getSections() {

            return mSectionLetters;
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
        public View getHeaderView(int position, View convertView, ViewGroup parent) {

            HeaderViewHolder holder;

            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = vi.inflate(R.layout.row_section_header, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.txtAlphabate);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            // set header text as first char in name
            CharSequence headerChar = mArrayList.get(position).getListname().subSequence(0, 1);
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
            return mArrayList.get(position).getListname().subSequence(0, 1).charAt(0);
        }
    }

}
