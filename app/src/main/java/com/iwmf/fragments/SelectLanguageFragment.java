package com.iwmf.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwmf.LoginActivity;
import com.iwmf.R;
import com.iwmf.base.BaseFragment;
import com.iwmf.http.PARAMS;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Language_code;
import com.iwmf.utils.Utils;

/**
 * <p> Select language from the available language list. </p>
 */
public class SelectLanguageFragment extends BaseFragment implements OnClickListener {

    private TextView txtLanguage = null;
    private String[] aryLanguage = null;
    private int selectedLanguage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_language_selection, container, false);

        mappingWidgets(mView);

        return mView;
    }

    private void mappingWidgets(View v) {

        LinearLayout lnrSelectLng = (LinearLayout) v.findViewById(R.id.lnrSelectLng);
        TextView txtNext = (TextView) v.findViewById(R.id.txtNext);
        txtLanguage = (TextView) v.findViewById(R.id.txtLanguage);

        aryLanguage = getResources().getStringArray(R.array.Language);

        txtLanguage.setText(aryLanguage[selectedLanguage]);

        lnrSelectLng.setOnClickListener(this);
        txtNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lnrSelectLng:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_country_name_center, R.id.title);
                mArrayAdapter.addAll(aryLanguage);

                builder.setTitle(R.string.select_your_language).setAdapter(mArrayAdapter, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        selectedLanguage = which;

                        txtLanguage.setText(aryLanguage[which]);
                    }
                });

                AlertDialog mAlertDialog = builder.create();

                mAlertDialog.show();

                break;

            case R.id.txtNext:

                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("reportapref", Context.MODE_PRIVATE);

                sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE, aryLanguage[selectedLanguage]).putBoolean(PARAMS.KEY_IS_VERY_FIRST_TIME, false).apply();

                if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_english))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.EN.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_english));

                } else if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_spanish))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.ES.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.ES.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_spanish));

                } else if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_french))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.FR.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.FR.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_french));

                } else if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_turkish))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.TR.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.TR.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_turkish));

                } else if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_arabic))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.AR.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.AR.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_arabic));

                } else if (aryLanguage[selectedLanguage].equalsIgnoreCase(getString(R.string.lng_hebrew))) {

                    sharedpreferences.edit().putString(PARAMS.KEY_LANGUAGE_CODE, Language_code.IW.name()).apply();

                    ConstantData.LANGUAGE_CODE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE_CODE, Language_code.IW.name());
                    ConstantData.LANGUAGE = sharedpreferences.getString(PARAMS.KEY_LANGUAGE, getString(R.string.lng_hebrew));
                }

                Utils.changeLocale(ConstantData.LANGUAGE_CODE, getActivity());

                Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(mIntent);

                getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                getActivity().finish();

                break;
        }
    }
}
