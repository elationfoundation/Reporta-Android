package com.iwmf.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.iwmf.R;
import com.iwmf.adapters.PendingMediaAdapter;
import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.base.BaseFragment;
import com.iwmf.data.PendingMediaData;
import com.iwmf.http.AsyncWebServiceLoader;
import com.iwmf.http.RequestBuilder;
import com.iwmf.http.RequestMethod;
import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * <p> Shows the list of pending media files to upload on server. </p>
 */
@SuppressWarnings("ALL")
public class PendingMediaFragment extends BaseFragment implements OnClickListener {

    private ImageView imgSlideUp;
    private ListView lstPendingMedia;
    private ArrayList<PendingMediaData> listPendingMedia = null;
    private PendingMediaAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_pending_media, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        lstPendingMedia = (ListView) v.findViewById(R.id.lstPendingMedia);
        imgSlideUp = (ImageView) v.findViewById(R.id.imgSlideUp);
        lstPendingMedia.setEmptyView(v.findViewById(R.id.txtEmptyMessage));

        imgSlideUp.setOnClickListener(this);

        lstPendingMedia.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.select_action));
                builder.setItems(new CharSequence[]{getString(R.string.menu_resend), getString(R.string.menu_delete)}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:
                                // resend media
                                uploadMedia(position);
                                break;

                            case 1:
                                // delete media
                                if (ConstantData.DB.deleteMedia(listPendingMedia.get(position).getId())) {
                                    listPendingMedia.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                                break;
                        }
                    }
                });

                builder.show();

                return true;
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        listPendingMedia = new ArrayList<>();
        listPendingMedia = ConstantData.DB.getPendingMediaList();
        adapter = new PendingMediaAdapter(getActivity(), listPendingMedia);
        lstPendingMedia.setAdapter(adapter);
    }

    private void uploadMedia(final int index) {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_ADD_MEDIA, RequestMethod.POST, RequestBuilder.getMediaRequest(listPendingMedia.get(index)));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    try {

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.get("status").toString().equals("1")) {

                            Toast.displayMessage(getActivity(), R.string.media_upload_success);
                            File file = new File(listPendingMedia.get(index).getFilePath());
                            if (file.exists()) {
                                file.delete();
                            }

                            listPendingMedia.remove(index);
                            ConstantData.DB.deleteMedia(listPendingMedia.get(index).getId());

                            int i = ConstantData.DB.getPendingMediaCountForRefid(listPendingMedia.get(0).getRef_id() + "");

                            if (i == 0) {
                                sendMailAttachment(listPendingMedia.get(0).getRef_id());
                            }

                            adapter.notifyDataSetChanged();

                        } else if (jsonObject.get("status").toString().equals("3")) {
                            ((BaseAppCompatActivity) getActivity()).showSessionExpired(jsonObject.get("message").toString());
                        } else {
                            Toast.displayMessage(getActivity(), R.string.media_upload_fail);
                            ConstantData.DB.updateMediaAttemptCount(listPendingMedia.get(index).getId());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.displayMessage(getActivity(), R.string.media_upload_fail);
                        ConstantData.DB.updateMediaAttemptCount(listPendingMedia.get(index).getId());
                    } finally {
                        dismissProgressDialog();
                    }
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                    Toast.displayMessage(getActivity(), R.string.media_upload_fail);
                    ConstantData.DB.updateMediaAttemptCount(listPendingMedia.get(index).getId());
                }
            });
            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMailAttachment(final int Ref_id) {

        try {

            displayProgressDialog();

            AsyncWebServiceLoader mAsyncWebServiceLoader = new AsyncWebServiceLoader(RequestBuilder.WS_SEND_MAIL_MEDIA, RequestMethod.POST, RequestBuilder.getSendMailWithMediaInRequest(Ref_id, ConstantData.ALERT_TYPE_ALERT));

            mAsyncWebServiceLoader.setCallback(new Callbacks() {

                @Override
                public void onResponse(String result) {

                    dismissProgressDialog();
                }

                @Override
                public void onError(String error) {

                    dismissProgressDialog();
                }
            });
            mAsyncWebServiceLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
            dismissProgressDialog();
        }
    }

    @Override
    public void onClick(View v) {

        if (v == imgSlideUp) {

            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.no_anim, R.anim.abc_slide_out_top);
        }
    }

}
