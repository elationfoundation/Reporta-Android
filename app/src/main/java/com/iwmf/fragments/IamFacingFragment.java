package com.iwmf.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.iwmf.R;
import com.iwmf.adapters.IamFacingListAdapter;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.FacingIssueData;
import com.iwmf.utils.Utils;

import java.util.ArrayList;

/**
 * <p> User can describe what he/she is facing in
 * terms of its situation. It shows a list of all situation. </p>
 */
public class IamFacingFragment extends BaseFragment implements OnClickListener {

    private ArrayList<FacingIssueData> listFacingIssue;
    private IamFacingListAdapter adapter;
    private ListView listview;
    private String facingIssue = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        facingIssue = getArguments().getString("facingIssue");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.activity_i_am_facing, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        listview = (ListView) v.findViewById(R.id.listView);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listFacingIssue = new ArrayList<>();

        fillData();

        if (!TextUtils.isEmpty(facingIssue)) {

            String[] strArray = facingIssue.split(",,");

            for (String aStrArray : strArray) {
                for (int k = 0; k < listFacingIssue.size(); k++) {
                    if (aStrArray.trim().equalsIgnoreCase(listFacingIssue.get(k).getName().trim())) {
                        listFacingIssue.get(k).setChecked(true);
                    }
                }
            }

        }

        adapter = new IamFacingListAdapter(getActivity(), listFacingIssue);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FacingIssueData data = (FacingIssueData) parent.getItemAtPosition(position);
                data.setChecked(!data.isChecked());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fillData() {

        String[] strArray = getResources().getStringArray(R.array.imfacing);

        for (String aStrArray : strArray) {
            listFacingIssue.add(new FacingIssueData(aStrArray, false));
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.txtBack:
                getActivity().finish();
                break;

            case R.id.txtDone:
                onDoneClick();
                break;
        }
    }

    public void onDoneClick() {

        Intent intent = new Intent();
        String sTemp = "";

        for (int i = 0; i < adapter.getSelectedItem().size(); i++) {
            if (adapter.getSelectedItem().get(i).isChecked()) {

                if (Utils.isEmpty(sTemp)) {
                    sTemp = adapter.getSelectedItem().get(i).getName();
                } else {
                    sTemp += ",, " + adapter.getSelectedItem().get(i).getName();
                }
            }
        }

        intent.putExtra("facingIssue", sTemp);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }

}
