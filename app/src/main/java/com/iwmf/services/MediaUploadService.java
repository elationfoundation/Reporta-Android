package com.iwmf.services;

import android.app.IntentService;
import android.content.Intent;

import com.iwmf.data.PendingMediaData;
import com.iwmf.http.PARAMS;
import com.iwmf.http.RequestBuilder;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.DBHelper;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * <p> MediaUploadService handles media uploading task in background.
 * Broadcast ConstantData.ACTION_UPLOAD_DONE intent on completion. </p>
 */
@SuppressWarnings("ALL")
public class MediaUploadService extends IntentService {

    private static final String TAG = MediaUploadService.class.getSimpleName();

    public static boolean MEDIA_IS_UPLOADING = false;

    public MediaUploadService() {

        super("MediaUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            if (ConstantData.DB == null || !ConstantData.DB.isOpen()) {
                ConstantData.DB = new DBHelper(this);
                ConstantData.DB.open();
            }

            ArrayList<PendingMediaData> list = ConstantData.DB.getPendingMediaList();

            if (list != null && list.size() > 0) {

                MEDIA_IS_UPLOADING = true;


                for (int i = 0; i < list.size(); i++) {
                    PendingMediaData mPendingMediaData = list.get(i);
                    try {
                        String requestParams = RequestBuilder.getMediaRequest(mPendingMediaData);

                        OkHttpClient client = new OkHttpClient();
                        client.setConnectTimeout(15, TimeUnit.SECONDS);
                        RequestBody body = new FormEncodingBuilder().add(PARAMS.TAG_BULKDATA, requestParams).build();
                        Request request = new Request.Builder().url(RequestBuilder.WS_ADD_MEDIA).post(body).build();
                        Response response = client.newCall(request).execute();
                        String result = response.body().string();

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.getInt("status") == 1) {
                            ConstantData.DB.deleteMedia(mPendingMediaData.getId());
                            File mFile = new File(mPendingMediaData.getFilePath());
                            if (mFile.exists()) {
                                mFile.delete();
                            }

                        } else {
                            ConstantData.DB.updateMediaAttemptCount(mPendingMediaData.getId());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ConstantData.DB.updateMediaAttemptCount(mPendingMediaData.getId());
                    }
                }

                MEDIA_IS_UPLOADING = false;

            }

            Intent intentBroadcast = new Intent(ConstantData.ACTION_UPLOAD_DONE);
            sendBroadcast(intentBroadcast);

        } catch (Exception e) {
            e.printStackTrace();
            Intent intentBroadcast = new Intent(ConstantData.ACTION_UPLOAD_DONE);
            sendBroadcast(intentBroadcast);
        }
    }

    @Override
    public void onDestroy() {

        MEDIA_IS_UPLOADING = false;
        super.onDestroy();
    }
}
