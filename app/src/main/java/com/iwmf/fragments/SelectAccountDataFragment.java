package com.iwmf.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iwmf.R;
import com.iwmf.adapters.SelectLanguageListAdapter;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.SelectLanguageData;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p> User can select account data. And fioll other details. </p>
 */
@SuppressWarnings("ALL")
public class SelectAccountDataFragment extends BaseFragment implements OnClickListener {

    private final static int INTENT_SELECT_LANGUAGE = 1;
    private final static int INTENT_SELECT_JOB_TITLE = 2;
    private final static int INTENT_SELECT_COUNTRY_ORIGIN = 3;
    private final static int INTENT_SELECT_COUNTRY_WORKING = 4;

    private ArrayList<SelectLanguageData> listSelectlanguage;
    private SelectLanguageListAdapter adapter;
    private ListView listview;
    private EditText edtOther;

    private String selectdata = "";
    private String titleid = "";
    private String selectjobtitle = "";
    private String selectcountryorigin = "";
    private String selectcountryworking = "";

    private EditText edt_search = null;

    private InputStream mInputStream = null;

    public static SelectAccountDataFragment getInstance(String titleid) {

        SelectAccountDataFragment mFragment = new SelectAccountDataFragment();

        mFragment.titleid = titleid;

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        selectdata = getArguments().getString("selectdata");
        titleid = getArguments().getString("titleid");
        selectjobtitle = getArguments().getString("selectjobtitle");
        selectcountryorigin = getArguments().getString("selectcountryorigin");
        selectcountryworking = getArguments().getString("selectcountryworking");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.activity_select_language, container, false);
        mappingWidgets(mView);

        TextView textheader = (TextView) mView.findViewById(R.id.txtHeaderSelectlanguage);
        edtOther = (EditText) mView.findViewById(R.id.edtOther);

        edt_search = (EditText) mView.findViewById(R.id.edt_search);

        if (titleid.equals(getString(R.string.selectlanguage))) {

            edt_search.setVisibility(View.GONE);
            textheader.setText(getString(R.string.selectlanguage));

        } else if (titleid.equals(getString(R.string.selectjobtitle))) {

            edt_search.setVisibility(View.GONE);
            textheader.setText(getString(R.string.selectjobtitle));

        } else if (titleid.equals(getString(R.string.selectcountryorigin))) {

            edt_search.setVisibility(View.VISIBLE);
            textheader.setText(getString(R.string.selectcountryorigin));

        } else if (titleid.equals(getString(R.string.selectcountryworking))) {

            edt_search.setVisibility(View.VISIBLE);
            textheader.setText(getString(R.string.selectcountryworking));
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listSelectlanguage = new ArrayList<>();

        String[] strArray;
        if (titleid.equals(getString(R.string.selectlanguage))) {
            strArray = getResources().getStringArray(R.array.Language);

            for (String aStrArray : strArray) {
                listSelectlanguage.add(new SelectLanguageData(aStrArray, false));
            }

        } else if (titleid.equals(getString(R.string.selectjobtitle))) {

            strArray = getResources().getStringArray(R.array.JobTitle);

            for (String aStrArray : strArray) {
                listSelectlanguage.add(new SelectLanguageData(aStrArray, false));
            }

            edtOther.setVisibility(View.VISIBLE);


        } else if (titleid.equals(getString(R.string.selectcountryorigin))) {

            // ******

            mInputStream = getInputStream(mInputStream);

            JSONArray mJsonArray = null;

            try {

                String str = IOUtils.toString(mInputStream, "utf-8");
                mJsonArray = new JSONArray(str);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            // ******

            if (mJsonArray == null) {
                mJsonArray = new JSONArray();
            }

            for (int i = 0; i < mJsonArray.length(); i++) {
                try {
                    if (!Utils.isEmpty(mJsonArray.getJSONObject(i).getString("Name"))) {
                        listSelectlanguage.add(new SelectLanguageData(mJsonArray.getJSONObject(i).getString("Name"), mJsonArray.getJSONObject(i).getString("Code"), false));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(listSelectlanguage, new Comparator<SelectLanguageData>() {

                @Override
                public int compare(SelectLanguageData o1, SelectLanguageData o2) {

                    return o1.getName().compareTo(o2.getName());
                }
            });

        } else if (titleid.equals(getString(R.string.selectcountryworking))) {

            // ******

            mInputStream = getInputStream(mInputStream);

            JSONArray mJsonArray = null;
            try {
                String str = IOUtils.toString(mInputStream, "utf-8");
                mJsonArray = new JSONArray(str);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            // ******

            if (mJsonArray == null) {
                mJsonArray = new JSONArray();
            }

            for (int i = 0; i < mJsonArray.length(); i++) {
                try {
                    if (!Utils.isEmpty(mJsonArray.getJSONObject(i).getString("Name"))) {
                        listSelectlanguage.add(new SelectLanguageData(mJsonArray.getJSONObject(i).getString("Name"), mJsonArray.getJSONObject(i).getString("Code"), false));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(listSelectlanguage, new Comparator<SelectLanguageData>() {

                @Override
                public int compare(SelectLanguageData o1, SelectLanguageData o2) {

                    return o1.getName().compareTo(o2.getName());
                }
            });

        }

        if (listSelectlanguage == null) {
            listSelectlanguage = new ArrayList<>();
        }

        if (titleid.equals(getString(R.string.selectlanguage))) {

            if (!TextUtils.isEmpty(selectdata)) {
                for (int i = 0; i < listSelectlanguage.size(); i++) {
                    if (listSelectlanguage.get(i).getName().equals(selectdata)) {
                        listSelectlanguage.get(i).setChecked(true);
                        break;
                    }
                }
            }
        } else if (titleid.equals(getString(R.string.selectjobtitle))) {
            if (!TextUtils.isEmpty(selectjobtitle)) {
                boolean isChecked = false;
                for (int i = 0; i < listSelectlanguage.size(); i++) {
                    if (listSelectlanguage.get(i).getName().equals(selectjobtitle)) {
                        listSelectlanguage.get(i).setChecked(true);
                        isChecked = true;
                        if (i == 7) {
                            edtOther.setText(selectjobtitle);
                            edtOther.setEnabled(true);
                        }
                        break;
                    }
                }
                if (!isChecked) {
                    listSelectlanguage.get(7).setChecked(true);
                    edtOther.setText(selectjobtitle);
                    edtOther.setEnabled(true);
                }
            }
        } else if (titleid.equals(getString(R.string.selectcountryorigin))) {

            if (!TextUtils.isEmpty(selectcountryorigin)) {
                for (int i = 0; i < listSelectlanguage.size(); i++) {
                    if (listSelectlanguage.get(i).getName().equals(selectcountryorigin)) {
                        listSelectlanguage.get(i).setChecked(true);
                        break;
                    }
                }
            }

        } else if (titleid.equals(getString(R.string.selectcountryworking))) {

            if (!TextUtils.isEmpty(selectcountryworking)) {
                for (int i = 0; i < listSelectlanguage.size(); i++) {
                    if (listSelectlanguage.get(i).getName().equals(selectcountryworking)) {
                        listSelectlanguage.get(i).setChecked(true);
                        break;
                    }
                }
            }
        }

        adapter = new SelectLanguageListAdapter(getActivity(), listSelectlanguage);
        listview.setAdapter(adapter);

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                String text = edt_search.getText().toString().toLowerCase(Locale.getDefault());

                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Utils.hideKeyboard(getActivity(), edt_search);

                int count = listSelectlanguage.size();
                for (int i = 0; i < count; i++) {
                    listSelectlanguage.get(i).setChecked(false);
                }
                adapter.getSelectedItem().get(position).setChecked(true);
                adapter.notifyDataSetChanged();

                performDone();
            }
        });

    }

    private InputStream getInputStream(InputStream mInputStream) {

        if (ConstantData.LANGUAGE_CODE.equals(Language_code.EN.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_en);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.AR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_ar);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.FR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_fr);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.IW.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_iw);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.ES.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_es);

        } else if (ConstantData.LANGUAGE_CODE.equals(Language_code.TR.name())) {

            return getResources().openRawResource(R.raw.countrylist_json_new_tr);

        } else {

            return getResources().openRawResource(R.raw.countrylist_json_new_en);
        }
    }

    private void mappingWidgets(View v) {

        listview = (ListView) v.findViewById(R.id.listView);

    }

    @Override
    public void onClick(View v) {

        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        switch (v.getId()) {

            case R.id.txtBack:

                getActivity().finish();

                break;

            case R.id.txtDone:

                performDone();

                break;

        }
    }

    private void performDone() {

        if (titleid.equals(getString(R.string.selectlanguage))) {

            String sTemp = "";
            ArrayList<SelectLanguageData> mData = adapter.getSelectedItem();
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isChecked()) {
                    if (Utils.isEmpty(sTemp)) {
                        sTemp = mData.get(i).getName();
                    }
                }
            }

            getActivity().getIntent().putExtra("selectlanguage", sTemp);

            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(INTENT_SELECT_LANGUAGE, Activity.RESULT_OK, getActivity().getIntent());
                getActivity().onBackPressed();
            }

        } else if (titleid.equals(getString(R.string.selectjobtitle))) {

            String sTemp = "";
            ArrayList<SelectLanguageData> mData = adapter.getSelectedItem();
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isChecked()) {
                    if (Utils.isEmpty(sTemp)) {

                        if (listSelectlanguage.get(i).getName().equals("other")) {
                            sTemp = edtOther.getText().toString().trim();
                        } else {
                            sTemp = mData.get(i).getName();
                        }

                    }
                }
            }
            getActivity().getIntent().putExtra("selectjobtitle", sTemp);

            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(INTENT_SELECT_JOB_TITLE, Activity.RESULT_OK, getActivity().getIntent());
                getActivity().onBackPressed();
            }

        } else if (titleid.equals(getString(R.string.selectcountryorigin))) {

            String sTemp = "", sTempCode = "";
            ArrayList<SelectLanguageData> mData = adapter.getSelectedItem();
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isChecked()) {
                    if (Utils.isEmpty(sTemp)) {
                        sTemp = mData.get(i).getName();
                        sTempCode = mData.get(i).getCode();
                    }
                }
            }
            getActivity().getIntent().putExtra("selectcountryorigin", sTemp);
            getActivity().getIntent().putExtra("selectcountryorigin_code", sTempCode);

            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(INTENT_SELECT_COUNTRY_ORIGIN, Activity.RESULT_OK, getActivity().getIntent());
                getActivity().onBackPressed();
            }

        } else if (titleid.equals(getString(R.string.selectcountryworking))) {

            String sTemp = "", sTempCode = "";
            ArrayList<SelectLanguageData> mData = adapter.getSelectedItem();
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).isChecked()) {
                    if (Utils.isEmpty(sTemp)) {
                        sTemp = mData.get(i).getName();
                        sTempCode = mData.get(i).getCode();
                    }
                }
            }
            getActivity().getIntent().putExtra("selectcountryworking", sTemp);
            getActivity().getIntent().putExtra("selectcountryworking_code", sTempCode);

            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(INTENT_SELECT_COUNTRY_WORKING, Activity.RESULT_OK, getActivity().getIntent());
                getActivity().onBackPressed();
            }

        }

    }

    // ****

    private String[] getListOfCountriesInNativeLanguage() {

        Map<String, String> languagesMap = new TreeMap<>();
        Locale[] locales = Locale.getAvailableLocales();
        String[] countries = Locale.getISOCountries();
        String[] countriesInNativeLanguage;
        int supportedLocale = 0, nonSupportedLocale = 0;

        JSONArray jsonArray = new JSONArray();

        for (Locale obj : locales) {

            if ((obj.getDisplayCountry() != null) && (!"".equals(obj.getDisplayCountry()))) {
                languagesMap.put(obj.getCountry(), obj.getLanguage());
            }
        }

        countriesInNativeLanguage = new String[countries.length];

        for (String countryCode : countries) {

            Locale obj;
            if (languagesMap.get(countryCode) == null) {

                obj = new Locale(Locale.ENGLISH.getDisplayLanguage(), countryCode);
                nonSupportedLocale++;

            } else {

                // create a Locale with own country's languages
                obj = new Locale(languagesMap.get(countryCode), countryCode);
                supportedLocale++;

            }

            countriesInNativeLanguage[((nonSupportedLocale + supportedLocale) - 1)] = obj.getDisplayCountry(obj);

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("country", obj.getDisplayCountry(obj));
                jsonObject.put("country_code", obj.getCountry());
                jsonObject.put("language", obj.getDisplayLanguage());
                jsonObject.put("language_code", obj.getLanguage());

                jsonArray.put(jsonObject);

                File root = new File(Environment.getExternalStorageDirectory(), "iwmfNNNN");
                if (!root.exists()) {
                    root.mkdirs();
                }

                File mfile = new File(root, "countrylist_json");

                FileWriter writer = new FileWriter(mfile);
                writer.append(jsonArray.toString());
                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return countriesInNativeLanguage;
    }

    // ***

}
