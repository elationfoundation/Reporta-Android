package com.iwmf.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iwmf.R;
import com.iwmf.adapters.AllContactsAdapter;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.Contacts;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.SelectedScreen;
import com.iwmf.utils.Toast;
import com.iwmf.utils.Utils;
import com.iwmf.views.stickylistheaders.StickyListHeadersListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * <p> Display all contacts to user to choose from. </p>
 */
public class AllContactsFragment extends BaseFragment {

    private static ArrayList<Contacts> mContacts = null;

    private StickyListHeadersListView listView = null;
    private AllContactsAdapter mAllContactsAdapter = null;
    private EditText edt_search = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_all_contacts, container, false);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        listView = (StickyListHeadersListView) v.findViewById(R.id.listView);

        RelativeLayout rltCancel = (RelativeLayout) v.findViewById(R.id.rltCancel);
        rltCancel.setVisibility(View.GONE);

        listView.setEmptyView(v.findViewById(R.id.txtNoDataFound));
        edt_search = (EditText) v.findViewById(R.id.edt_search);

        if (Utils.isOnline(getActivity())) {
            getContactList();
        }

        if (mContacts == null) {
            mContacts = new ArrayList<>();
        }

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());

                mAllContactsAdapter.filter(text);
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

                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_container, ContactAddManualFragment.getInstance(mContacts.get(position), SelectedScreen.PRIVATE, -1)).addToBackStack(ContactAddManualFragment.class.getSimpleName()).commit();
            }
        });
    }

    private void getContactList() {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ALLCONTACTS, RequestMethod.POST, RequestBuilder.getAllContactListRequest());

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {
                        dismissProgressDialog();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("status") && jsonObject.has("message")) {

                            if (jsonObject.get("status").toString().equals("1")) {

                                mContacts = new Gson().fromJson(jsonObject.getJSONArray("data").toString(), new TypeToken<List<Contacts>>() {
                                }.getType());

                                if (mContacts == null) {
                                    mContacts = new ArrayList<>();
                                }

                                Collections.sort(mContacts, new Comparator<Contacts>() {

                                    @Override
                                    public int compare(Contacts o1, Contacts o2) {

                                        return o1.getLastname().compareTo(o2.getLastname());
                                    }
                                });

                                mAllContactsAdapter = new AllContactsAdapter(mContacts, getActivity());

                                listView.setAdapter(mAllContactsAdapter);

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
}
