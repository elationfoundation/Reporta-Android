package com.iwmf.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.iwmf.R;
import com.iwmf.adapters.AddContactsToCircleAdapter;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Associated_circles;
import com.iwmf.data.ContactListData;
import com.iwmf.data.Contacts;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.views.stickylistheaders.StickyListHeadersListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * <p> User can add new contact to his existing circle. </p>
 */
public class AddContactToCircleFragment extends BaseFragment implements OnClickListener {

    public static boolean isActive = false;
    private ArrayList<Contacts> mContacts = null;
    private StickyListHeadersListView listView = null;
    private TextView txtEdit = null;
    private TextView txtContactTotal = null;
    private TextView txtNoDataFound = null;
    private TextView txtMsg = null;
    private EditText edt_search = null, edtCircleName = null;
    private Button btnBuildThisCircle = null;
    private Button btnCancel;
    private View line = null;
    private SelectedScreen mSelectedScreen = null;
    private String circle_id = null, circle_name = null;
    private ContactListData mContactListData = null;

    private AddContactsToCircleAdapter mAddContactsToCircleAdapter = null;

    public static AddContactToCircleFragment getInstance(SelectedScreen mSelectedScreen, ContactListData mContactListData) {

        AddContactToCircleFragment mFragment = new AddContactToCircleFragment();

        mFragment.mSelectedScreen = mSelectedScreen;
        mFragment.circle_id = mContactListData.getContactlist_id();
        mFragment.circle_name = mContactListData.getListname();
        mFragment.mContactListData = mContactListData;

        mFragment.mContacts = mContactListData.getContacts();

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_add_contact_to_circle, container, false);

        mappingWidgets(mView);

        isActive = mContacts.size() == 0;

        return mView;
    }

    @Override
    public void onResume() {

        super.onResume();
        isActive = mContacts.size() == 0;
    }

    @Override
    public void onPause() {

        super.onPause();
        isActive = mContacts.size() == 0;
    }

    private void mappingWidgets(View v) {

        listView = (StickyListHeadersListView) v.findViewById(R.id.listView);

        edt_search = (EditText) v.findViewById(R.id.edt_search);
        edtCircleName = (EditText) v.findViewById(R.id.edtCircleName);

        btnBuildThisCircle = (Button) v.findViewById(R.id.btnBuildThisCircle);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);

        line = v.findViewById(R.id.line);

        Button btnDone = (Button) v.findViewById(R.id.btnDone);

        if (SelectedScreen.REGISTER == mSelectedScreen) {
            btnDone.setVisibility(View.VISIBLE);
        } else {
            btnDone.setVisibility(View.GONE);
        }

        txtMsg = (TextView) v.findViewById(R.id.txtMsg);
        txtEdit = (TextView) v.findViewById(R.id.txtEdit);
        txtContactTotal = (TextView) v.findViewById(R.id.txtContactTotal);
        TextView txtAdd = (TextView) v.findViewById(R.id.txtAdd);
        txtNoDataFound = (TextView) v.findViewById(R.id.txtNoDataFound);

        txtEdit.setOnClickListener(this);
        txtAdd.setOnClickListener(this);
        btnBuildThisCircle.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        listView.setEmptyView(txtNoDataFound);

        edtCircleName.setText(circle_name);

        if (SelectedScreen.PRIVATE == mSelectedScreen || SelectedScreen.REGISTER == mSelectedScreen) {
            txtMsg.setText(getString(R.string.your_private));
        } else {
            txtMsg.setText(getString(R.string.your_public));
        }

        if (mContacts == null) {
            mContacts = new ArrayList<>();
        }

        if (mContacts.size() > 0) {

            txtContactTotal.setVisibility(View.VISIBLE);
            txtContactTotal.setText("(" + mContacts.size() + ")");

            edt_search.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);

            txtMsg.setVisibility(View.GONE);
            btnBuildThisCircle.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            Collections.sort(mContacts, new Comparator<Contacts>() {

                @Override
                public int compare(Contacts o1, Contacts o2) {

                    return o1.getLastname().compareTo(o2.getLastname());
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {

                    mAddContactsToCircleAdapter = new AddContactsToCircleAdapter(mContacts, getActivity());
                    isActive = mContacts.size() == 0;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(mAddContactsToCircleAdapter);
                        }
                    });
                }
            }).start();


        } else {

            txtContactTotal.setVisibility(View.GONE);

            edt_search.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

            txtNoDataFound.setVisibility(View.GONE);
            txtMsg.setVisibility(View.VISIBLE);
            btnBuildThisCircle.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        }

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());

                mAddContactsToCircleAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (mContacts.size() > 1) {

                    final int pos = position;

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.delete_contact_msg));
                    dialog.setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            performDeleteContact(mContacts.get(pos).getContact_id(), pos);

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

                } else {

                    // final int pos = position;

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(getString(R.string.delete_this_contact));
                    dialog.setPositiveButton(getString(R.string.delete_now), new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            performDeleteContactList(circle_id);

                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {

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

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contacts mContacts_ = (Contacts) mAddContactsToCircleAdapter.getItem(position);

                if (SelectedScreen.REGISTER != mSelectedScreen) {
                    if (getTargetFragment() != null) {
                        getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                    }
                }

                if (mContacts_.getAssociated_circles() == null) {

                    ArrayList<Associated_circles> mAssociated_circles = new ArrayList<>();

                    Associated_circles mAss = new Associated_circles();

                    mAss.setContactlist_id(mContactListData.getContactlist_id());
                    mAss.setListname(mContactListData.getListname());

                    mAssociated_circles.add(mAss);

                    mContacts_.setSos_enabled("0");
                    mContacts_.setAssociated_circles(mAssociated_circles);

                }

                Fragment mFragment = ContactAddManualFragment.getInstance(mContacts_, mSelectedScreen, position);
                mFragment.setTargetFragment(AddContactToCircleFragment.this, mSelectedScreen == SelectedScreen.REGISTER ? 2 : 1);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();

            }
        });
    }

    private void performDeleteContact(String contact_id, final int pos) {

        displayProgressDialog();

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_DELETECONTACT, RequestMethod.POST, RequestBuilder.getDeleteContactRequest(contact_id));
        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                try {
                    dismissProgressDialog();

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("message") && jsonObject.has("status")) {
                        if (jsonObject.get("status").toString().equals("1")) {

                            if (SelectedScreen.REGISTER != mSelectedScreen) {
                                if (getTargetFragment() != null) {
                                    getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                                }
                            }

                            mContacts.remove(pos);
                            mAddContactsToCircleAdapter.notifyDataSetChanged();
                            if (mContacts.size() > 0) {
                                txtContactTotal.setVisibility(View.VISIBLE);
                                txtContactTotal.setText("(" + mContacts.size() + ")");
                            } else {
                                txtContactTotal.setVisibility(View.GONE);
                            }

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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtEdit:

                if (txtEdit.getText().equals(getString(R.string.edit))) {
                    edtCircleName.setTextColor(getResources().getColor(R.color.active_circle_text));
                    edtCircleName.setEnabled(true);
                    edtCircleName.requestFocus();
                    edtCircleName.setSelection(edtCircleName.length());
                    txtEdit.setText(getString(R.string.save));
                } else {

                    if (!circle_name.equals(edtCircleName.getText().toString().trim())) {
                        performUpdateCircleNameWs(edtCircleName.getText().toString().trim());
                    } else {
                        edtCircleName.setTextColor(Color.BLACK);
                        txtEdit.setText(getString(R.string.edit));
                        edtCircleName.setEnabled(false);
                    }
                }

                break;
            case R.id.txtAdd:
                openListdialogForAddContact();

                break;
            case R.id.btnBuildThisCircle:
                openListdialogForAddContact();

                break;
            case R.id.btnDone:
                if (mContacts != null && mContacts.size() > 0) {
                    performNext();
                } else {
                    Toast.displayError(getActivity(), getString(R.string.add_atleast_one_contact));
                }
                break;
            case R.id.btnCancel:
                // Delete empty circle
                performDeleteContactList(circle_id);
                break;
        }
    }

    private void performDeleteContactList(String contactlist_id) {

        displayProgressDialog();

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_DELETECIRCLE, RequestMethod.POST, RequestBuilder.getDeleteContactlistRequest(contactlist_id));
        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                try {
                    dismissProgressDialog();

                    if (!isAdded()) return;

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("message") && jsonObject.has("status")) {

                        if (jsonObject.get("status").toString().equals("1")) {

                            getActivity().getSupportFragmentManager().popBackStackImmediate(AddCircleFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

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
    }

    private void performNext() {

        if (AccountCreationFragment.jsonRegister != null) {

            try {

                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LIST_ID, null);
                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CIRCLE, "1");
                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_LISTNAME, edtCircleName.getText().toString());
                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DELETE_CONTACT, "");
                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_DEFAULTSTATUS, "1");

                JsonArray j = new GsonBuilder().create().toJsonTree(mContacts).getAsJsonArray();

                JSONArray js = new JSONArray(j.toString());

                AccountCreationFragment.jsonRegister.put(PARAMS.TAG_CONTACTS, js);

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, new AccountTermAndConditionFragment()).addToBackStack(AccountTermAndConditionFragment.class.getSimpleName()).commit();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getContactList() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ALLCIRCLEWITHCONTACTS, RequestMethod.POST, RequestBuilder.getCircleByCircleIdRequest(((SelectedScreen.PRIVATE == mSelectedScreen || SelectedScreen.REGISTER == mSelectedScreen) ? "1" : "2")));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                ArrayList<ContactListData> mContactListDatas;

                                mContactListDatas = new Gson().fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<ContactListData>>() {
                                }.getType());

                                if (mContactListDatas == null) {
                                    mContactListDatas = new ArrayList<>();
                                }

                                if (mContactListDatas.size() > 0) {

                                    for (int i = 0; i < mContactListDatas.size(); i++) {
                                        if (mContactListDatas.get(i).getContactlist_id().equals(circle_id)) {

                                            mContactListData = mContactListDatas.get(i);
                                            mContacts = mContactListDatas.get(i).getContacts();

                                            if (mContacts == null) {
                                                mContacts = new ArrayList<>();
                                            }


                                            if (mContacts.size() > 0) {
                                                txtContactTotal.setVisibility(View.VISIBLE);
                                                txtContactTotal.setText("(" + mContacts.size() + ")");

                                                edt_search.setVisibility(View.VISIBLE);
                                                line.setVisibility(View.VISIBLE);

                                                txtMsg.setVisibility(View.GONE);
                                                btnBuildThisCircle.setVisibility(View.GONE);
                                                btnCancel.setVisibility(View.GONE);

                                                Collections.sort(mContacts, new Comparator<Contacts>() {

                                                    @Override
                                                    public int compare(Contacts o1, Contacts o2) {

                                                        return o1.getLastname().compareTo(o2.getLastname());
                                                    }
                                                });

                                                mAddContactsToCircleAdapter = new AddContactsToCircleAdapter(mContacts, getActivity());

                                                isActive = mContacts.size() == 0;
                                                listView.setAdapter(mAddContactsToCircleAdapter);

                                            } else {

                                                txtContactTotal.setVisibility(View.GONE);

                                                edt_search.setVisibility(View.GONE);
                                                line.setVisibility(View.GONE);

                                                txtNoDataFound.setVisibility(View.GONE);
                                                txtMsg.setVisibility(View.VISIBLE);
                                                btnBuildThisCircle.setVisibility(View.VISIBLE);
                                                btnCancel.setVisibility(View.VISIBLE);
                                            }


                                            break;
                                        }
                                    }
                                }
                            } else if (jsonObject.get("status").toString().equals("3")) {

                                ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());

                            } else {

                                Toast.displayMessage(getActivity(), jsonObject.get("message").toString());
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

    private void performUpdateCircleNameWs(String circleName) {

        displayProgressDialog();

        AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_CREATECONTACTCIRCLE, RequestMethod.POST, RequestBuilder.getCreateCircleRequest(circle_id, ((SelectedScreen.PRIVATE == mSelectedScreen || SelectedScreen.REGISTER == mSelectedScreen) ? "1" : "2"), circleName));

        mAsyncWebServiceLoader.setCallback(new Callbacks() {

            @Override
            public void onResponse(String result) {

                dismissProgressDialog();


                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("status") && jsonObject.has("message")) {
                        if (jsonObject.get("status").toString().equals("1")) {

                            JSONObject jObjectData = jsonObject.getJSONObject("data");

                            txtEdit.setText(getString(R.string.edit));

                            circle_id = jObjectData.getString("circle_id");
                            circle_name = jObjectData.getString("listname");

                            mContactListData.setListname(circle_name);
                            mContactListData.setContactlist_id(circle_id);

                            edtCircleName.setTextColor(Color.BLACK);
                            edtCircleName.setText(circle_name);
                            txtEdit.setText(getString(R.string.edit));
                            edtCircleName.setEnabled(false);

                            if (SelectedScreen.REGISTER != mSelectedScreen) {
                                if (getTargetFragment() != null) {
                                    getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                                }
                            }

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

                dismissProgressDialog();
                Toast.displayError(getActivity(), getString(R.string.try_later));
            }
        });

        mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void openListdialogForAddContact() {

        String[] items = {getString(R.string.add_from_addressbook), getString(R.string.add_from_reporta_contacts), getString(R.string.add_manually)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {


                Contacts mContactsTemp = new Contacts();

                ArrayList<Associated_circles> mAssociated_circles = new ArrayList<>();

                Associated_circles mAss = new Associated_circles();

                mAss.setCircle(mContactListData.getCircle());

                mAss.setContactlist_id(mContactListData.getContactlist_id());
                mAss.setListname(mContactListData.getListname());

                mAssociated_circles.add(mAss);

                mContactsTemp.setSos_enabled("0");
                mContactsTemp.setAssociated_circles(mAssociated_circles);

                if (which == 0) {

                    Fragment mFragment = ContactAddressBookFragment.getInstance(mSelectedScreen, mContactsTemp);
                    mFragment.setTargetFragment(AddContactToCircleFragment.this, SelectedScreen.REGISTER == mSelectedScreen ? 2 : 1);

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddressBookFragment.class.getSimpleName()).commit();


                } else if (which == 1) {


                    Fragment mFragment = AddExistingContactsListFragment.getInstance(circle_id);
                    mFragment.setTargetFragment(AddContactToCircleFragment.this, SelectedScreen.REGISTER == mSelectedScreen ? 2 : 1);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(CircleListFragment.class.getSimpleName()).commit();

                } else if (which == 2) {

                    Fragment mFragment = ContactAddManualFragment.getInstance(mContactsTemp, mSelectedScreen, -1);
                    mFragment.setTargetFragment(AddContactToCircleFragment.this, SelectedScreen.REGISTER == mSelectedScreen ? 2 : 1);

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).add(R.id.fragment_container, mFragment).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();
                }
            }
        }).create();

        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        isActive = mContacts.size() == 0;

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            getContactList();

            if (SelectedScreen.REGISTER != mSelectedScreen) {
                if (getTargetFragment() != null) {
                    getTargetFragment().onActivityResult(1, Activity.RESULT_OK, null);
                }
            }

        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {


            Collections.sort(mContacts, new Comparator<Contacts>() {

                @Override
                public int compare(Contacts o1, Contacts o2) {

                    return o1.getLastname().compareTo(o2.getLastname());
                }
            });

            mAddContactsToCircleAdapter = new AddContactsToCircleAdapter(mContacts, getActivity());

            if (mContacts.size() > 0) {
                txtContactTotal.setVisibility(View.VISIBLE);
                txtContactTotal.setText("(" + mContacts.size() + ")");

                edt_search.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);

                txtMsg.setVisibility(View.GONE);
                btnBuildThisCircle.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

            } else {

                txtContactTotal.setVisibility(View.GONE);

                edt_search.setVisibility(View.GONE);
                line.setVisibility(View.GONE);

                txtNoDataFound.setVisibility(View.GONE);
                txtMsg.setVisibility(View.VISIBLE);
                btnBuildThisCircle.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            }

            isActive = mContacts.size() == 0;
            listView.setAdapter(mAddContactsToCircleAdapter);
        }
    }
}