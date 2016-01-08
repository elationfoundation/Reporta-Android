package com.iwmf.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iwmf.R;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.ContactListData;
import com.iwmf.data.Contacts;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p> Add contacts to circle.
 * Save updated circle to server. </p>
 */
@SuppressWarnings("ALL")
public class AddCircleFragment extends BaseFragment implements OnClickListener {

    private final static int REQUEST_CODE_PRIVATE = 110;
    private final static int REQUEST_CODE_PUBLIC = 111;
    public static ArrayList<ContactListData> mContactListDatas = null;
    private TextView lblCircle = null;
    private TextView lblActiveCircle = null;
    private RelativeLayout rltAddCircle = null;
    private RelativeLayout rltActiveCircle = null;
    private boolean isPrivate = false;

    private ListView listView = null;
    private CustomAdapter mCustomAdapter = null;

    private SelectedScreen mSelectedScreen = null;

    private boolean isFromContacts = false;

    private String ids = "";

    public static AddCircleFragment getInstance(SelectedScreen mScreen, boolean isFromContacts, String ids) {

        AddCircleFragment mFragment = new AddCircleFragment();

        try {


            mFragment.mSelectedScreen = mScreen;
            mFragment.isFromContacts = isFromContacts;

            mFragment.ids = ids;

            mFragment.isPrivate = (SelectedScreen.PRIVATE == mScreen || SelectedScreen.REGISTER == mScreen);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_add_circle, container, false);

        mappingWidgets(mView);

        if (Utils.isOnline(getActivity()) && SelectedScreen.REGISTER != mSelectedScreen) {

            getContactList();

        } else {

            lblCircle.setText(getString(R.string.createprivatecircle));
            rltAddCircle.setVisibility(View.VISIBLE);
        }

        return mView;
    }

    private void mappingWidgets(View v) {

        listView = (ListView) v.findViewById(R.id.listView);

        TextView txtNoDataFound = (TextView) v.findViewById(R.id.txtNoDataFound);
        TextView txtAddCircle = (TextView) v.findViewById(R.id.txtAddCircle);
        TextView lbl_add_circle = (TextView) v.findViewById(R.id.lbl_add_circle);
        lblCircle = (TextView) v.findViewById(R.id.lblCircle);

        TextView txtActiveCircle = (TextView) v.findViewById(R.id.txtActiveCircle);
        lblActiveCircle = (TextView) v.findViewById(R.id.lblActiveCircle);

        listView.setEmptyView(txtNoDataFound);

        rltAddCircle = (RelativeLayout) v.findViewById(R.id.rltAddCircle);
        rltAddCircle.setOnClickListener(this);

        rltActiveCircle = (RelativeLayout) v.findViewById(R.id.rltActiveCircle);
        rltActiveCircle.setOnClickListener(this);

        RelativeLayout rltSavedCircles = (RelativeLayout) v.findViewById(R.id.rltSavedCircles);
        rltSavedCircles.setOnClickListener(this);

        if (isPrivate) {

            lblCircle.setText(getString(R.string.private_circle));
            txtNoDataFound.setText(getString(R.string.no_saved_private_circle));
        } else {

            lblCircle.setText(getString(R.string.public_circle));
            txtNoDataFound.setText(getString(R.string.no_saved_pulic_circle));
        }

        mContactListDatas = null;
        mContactListDatas = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());
        listView.setAdapter(mCustomAdapter);

        rltActiveCircle.setVisibility(View.GONE);
        rltAddCircle.setVisibility(View.VISIBLE);

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                if (mContactListDatas.size() > 1 && !mContactListDatas.get(position).getDefaultstatus().equals("1")) {

                    final int pos = position;

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.delete_circle_msg));
                    dialog.setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            performDeleteContactList(mContactListDatas.get(pos).getContactlist_id(), pos);

                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.no), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

                return true;
            }
        });

    }

    private void performDeleteContactList(String contactlist_id, final int pos) {

        displayProgressDialog();

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_DELETECIRCLE, RequestMethod.POST, RequestBuilder.getDeleteContactlistRequest(contactlist_id));
        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                try {
                    dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("status") && jsonObject.has("message")) {

                        if (jsonObject.get("status").toString().equals("1")) {

                            mContactListDatas.remove(pos);
                            mCustomAdapter.notifyDataSetChanged();

                        } else if (jsonObject.get("status").toString().equals("3")) {

                            ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                        } else {

                            showMessage("", jsonObject.get("message").toString());
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
    }

    private void getContactList() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ALLCIRCLEWITHCONTACTS, RequestMethod.POST, RequestBuilder.getCircleByCircleIdRequest((isPrivate ? "1" : "2")));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {
                            if (jsonObject.get("status").toString().equals("1")) {

                                mContactListDatas = null;

                                mContactListDatas = new Gson().fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<ContactListData>>() {
                                }.getType());

                                if (mContactListDatas == null) {
                                    mContactListDatas = new ArrayList<>();
                                }

                                mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());
                                listView.setAdapter(mCustomAdapter);

                                boolean isAnyActiveCircle = false;

                                if (mContactListDatas.size() > 0) {

                                    for (int i = 0; i < mContactListDatas.size(); i++) {

                                        if (isFromContacts) {

                                            if (mContactListDatas.get(i).getDefaultstatus().equals("1")) {
                                                rltActiveCircle.setTag(i);
                                                lblActiveCircle.setText(mContactListDatas.get(i).getListname());
                                                isAnyActiveCircle = true;
                                                break;
                                            }
                                        } else {

                                            mContactListDatas.get(i).setDefaultstatus("0");

                                            if (mContactListDatas.get(i).getContactlist_id().equals(ids)) {
                                                mContactListDatas.get(i).setDefaultstatus("1");
                                                isAnyActiveCircle = true;
                                                rltActiveCircle.setTag(i);
                                                lblActiveCircle.setText(mContactListDatas.get(i).getListname());
                                            }
                                        }
                                    }

                                    int pos = 0;
                                    boolean is = false;

                                    for (int j = 0; j < mContactListDatas.size(); j++) {

                                        if (mContactListDatas.get(j).getContacts() == null) {
                                            mContactListDatas.get(j).setContacts(new ArrayList<Contacts>());
                                        }

                                        if (mContactListDatas.get(j).getContacts().size() == 0) {
                                            pos = j;
                                            is = true;
                                            break;
                                        }
                                    }

                                    if (is) {
                                        final int uPos = pos;
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                        dialog.setCancelable(false);
                                        dialog.setMessage(mContactListDatas.get(uPos).getListname() + getString(R.string.this_circle_has_no_contact));
                                        dialog.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Fragment mFragment = AddContactToCircleFragment.getInstance(mSelectedScreen, mContactListDatas.get(uPos));
                                                mFragment.setTargetFragment(AddCircleFragment.this, 1);

                                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddContactToCircleFragment.class.getSimpleName()).commit();

                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }

                                    if (isAnyActiveCircle) {
                                        rltActiveCircle.setVisibility(View.VISIBLE);
                                        rltAddCircle.setVisibility(View.GONE);
                                    } else {
                                        rltActiveCircle.setVisibility(View.GONE);
                                        rltAddCircle.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    rltActiveCircle.setVisibility(View.GONE);
                                    rltAddCircle.setVisibility(View.VISIBLE);
                                }

                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                rltActiveCircle.setVisibility(View.GONE);
                                rltAddCircle.setVisibility(View.VISIBLE);

                                mContactListDatas = null;
                                mContactListDatas = new ArrayList<>();

                                mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());
                                listView.setAdapter(mCustomAdapter);
                            }
                        } else {
                            dismissProgressDialog();
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

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.rltActiveCircle:


                int pos = 0;
                if (rltActiveCircle.getTag() != null) {

                    pos = (Integer) rltActiveCircle.getTag();
                }

                Fragment mFragment = AddContactToCircleFragment.getInstance(mSelectedScreen, mContactListDatas.get(pos));
                mFragment.setTargetFragment(AddCircleFragment.this, 1);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddContactToCircleFragment.class.getSimpleName()).commit();

                break;

            case R.id.rltAddCircle:


                openCreateCircleDialog();
                break;
            case R.id.rltSavedCircles:


                openCreateCircleDialog();
                break;
        }
    }

    private void openCreateCircleDialog() {

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.dialog_create_circle, null, false);

        ((TextView) view.findViewById(R.id.txtMsg)).setText((isPrivate ? getString(R.string.create_a_new_private) : getString(R.string.create_a_new_public)));

        final EditText edt = (EditText) view.findViewById(R.id.edt);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).setCancelable(true).setPositiveButton(R.string.save, null).setNegativeButton(R.string.cancel, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button buttonOK = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String edtText = edt.getText().toString().trim();

                        Utils.hideKeyboard(getActivity(), edt);

                        if (!Utils.isEmpty(edtText)) {
                            performCreateCircleWs(edtText);
                            dialog.dismiss();
                        }
                    }
                });

                Button buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Utils.hideKeyboard(getActivity(), edt);

                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void performCreateCircleWs(String circleName) {

        if (SelectedScreen.REGISTER != mSelectedScreen) {

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATECONTACTCIRCLE, RequestMethod.POST, RequestBuilder.getCreateCircleRequest("", (isPrivate ? "1" : "2"), circleName));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    dismissProgressDialog();

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {
                            if (jsonObject.get("status").toString().equals("1")) {

                                JSONObject jObjectData = jsonObject.getJSONObject("data");

                                ContactListData mContactListData = new ContactListData();

                                mContactListData.setCircle(isPrivate ? "1" : "2");
                                mContactListData.setContactlist_id(jObjectData.getString("circle_id"));
                                mContactListData.setContacts(new ArrayList<Contacts>());
                                mContactListData.setListname(jObjectData.getString("listname"));

                                Fragment mFragment = AddContactToCircleFragment.getInstance(mSelectedScreen, mContactListData);
                                mFragment.setTargetFragment(AddCircleFragment.this, 1);

                                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddContactToCircleFragment.class.getSimpleName()).commit();

                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                showMessage("", jsonObject.get("message").toString());
                            }
                        } else {
                            Toast.displayError(getActivity(), getString(R.string.try_later));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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

        } else {

            ContactListData mContactListData = new ContactListData();

            mContactListData.setCircle("1");
            mContactListData.setContactlist_id("");
            mContactListData.setContacts(new ArrayList<Contacts>());
            mContactListData.setListname(circleName);

            Fragment mFragment = AddContactToCircleFragment.getInstance(mSelectedScreen, mContactListData);
            mFragment.setTargetFragment(AddCircleFragment.this, 1);

            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(AddContactToCircleFragment.class.getSimpleName()).commit();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            getContactList();
        }
    }

    private static class ViewHolder {

        public TextView txtName = null;
        public CheckBox switchActive = null;
    }

    public class CustomAdapter extends BaseAdapter {

        private ArrayList<ContactListData> mArrayList = null;
        private ArrayList<ContactListData> mArrayListTemp = null;
        private LayoutInflater vi;
        private Context mContext = null;

        public CustomAdapter(ArrayList<ContactListData> mContactListDatas, Context mContext) {

            this.mArrayList = mContactListDatas;

            this.mContext = mContext;

            this.mArrayListTemp = new ArrayList<>();
            this.mArrayListTemp.addAll(mArrayList);

            this.vi = LayoutInflater.from(mContext);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            final ViewHolder holder;

            if (v == null) {

                v = vi.inflate(R.layout.row_circle_list_with_switch, parent, false);
                holder = new ViewHolder();

                holder.txtName = (TextView) v.findViewById(R.id.txtName);
                holder.switchActive = (CheckBox) v.findViewById(R.id.switchActive);

                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            // final ContactListData mContactListData = mArrayList.get(position);
            if (mArrayList.get(position) != null) {

                try {

                    holder.txtName.setText(mArrayList.get(position).getListname());
                    holder.switchActive.setTag(position);

                    if (mArrayList.get(position).getDefaultstatus().equals("1")) {
                        holder.switchActive.setChecked(true);
                    }

                    holder.switchActive.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            final int pos = Integer.parseInt(holder.switchActive.getTag().toString());

                            rltActiveCircle.setTag(pos);

                            if (getTargetFragment() != null) {

                                if (isPrivate) {

                                    getTargetFragment().onActivityResult(REQUEST_CODE_PRIVATE, Activity.RESULT_OK, getActivity().getIntent().putExtra("private_contactlist_id", mArrayList.get(pos).getContactlist_id()));

                                } else {

                                    getTargetFragment().onActivityResult(REQUEST_CODE_PUBLIC, Activity.RESULT_OK, getActivity().getIntent().putExtra("public_contactlist_id", holder.switchActive.isChecked() ? mArrayList.get(pos).getContactlist_id() : ""));

                                }
                            }

                            if (!holder.switchActive.isChecked()) {

                                holder.switchActive.setChecked(true);

                                if (isPrivate) {

                                    showMessage("", getString(R.string.please_select_private));

                                } else {


                                    holder.switchActive.setChecked(false);

                                    if (isFromContacts) {

                                        setDefaultList(mArrayList.get(pos).getContactlist_id(), "0");

                                    } else {

                                        for (int i = 0; i < mContactListDatas.size(); i++) {
                                            mContactListDatas.get(i).setDefaultstatus("0");
                                        }

                                        lblActiveCircle.setText(mContactListDatas.get(pos).getListname());

                                        mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());

                                        listView.setAdapter(mCustomAdapter);

                                    }
                                }

                            } else {

                                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage(isPrivate ? R.string.do_you_want_update_private : R.string.do_you_want_update_public).setCancelable(true).setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.ok, null).create();

                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {

                                        Button buttonOK = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                        buttonOK.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                dialog.dismiss();

                                                holder.switchActive.setChecked(true);

                                                if (isFromContacts) {

                                                    setDefaultList(mArrayList.get(pos).getContactlist_id(), "1");

                                                } else {

                                                    for (int i = 0; i < mContactListDatas.size(); i++) {
                                                        mContactListDatas.get(i).setDefaultstatus("0");
                                                    }

                                                    mContactListDatas.get(pos).setDefaultstatus("1");

                                                    lblActiveCircle.setText(mContactListDatas.get(pos).getListname());

                                                    mCustomAdapter = new CustomAdapter(mContactListDatas, getActivity());

                                                    listView.setAdapter(mCustomAdapter);

                                                }
                                            }
                                        });

                                        buttonCancel.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View view) {

                                                dialog.dismiss();

                                                holder.switchActive.setChecked(false);

                                            }
                                        });

                                    }
                                });
                                dialog.show();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return v;
        }

        private void setDefaultList(final String list_id, final String defaultstatus) {

            ((BaseAppCompatActivity) mContext).displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CHANGEDEFAULTSTATUS, RequestMethod.POST, RequestBuilder.getChangeDefaultStatusRequest(list_id, (isPrivate ? "1" : "2"), defaultstatus));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    ((BaseAppCompatActivity) mContext).dismissProgressDialog();

                    try {

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                for (int i = 0; i < mContactListDatas.size(); i++) {
                                    if (mContactListDatas.get(i).getContactlist_id().equals(list_id)) {
                                        lblActiveCircle.setText(mContactListDatas.get(i).getListname());
                                        mContactListDatas.get(i).setDefaultstatus(defaultstatus);
                                    } else {
                                        mContactListDatas.get(i).setDefaultstatus("0");
                                    }
                                }

                                if (defaultstatus.equals("0")) {
                                    rltActiveCircle.setVisibility(View.GONE);
                                    rltAddCircle.setVisibility(View.VISIBLE);
                                } else {
                                    rltActiveCircle.setVisibility(View.VISIBLE);
                                    rltAddCircle.setVisibility(View.GONE);
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
                        Toast.displayError(getActivity(), getString(R.string.try_later));
                    }
                }

                @Override
                public void onError(String error) {

                    ((BaseAppCompatActivity) mContext).dismissProgressDialog();
                    Toast.displayError(getActivity(), getString(R.string.try_later));
                }
            });

            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    if (mData.getListname().toLowerCase(Locale.getDefault()).contains(charText)) {
                        mArrayList.add(mData);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

}
