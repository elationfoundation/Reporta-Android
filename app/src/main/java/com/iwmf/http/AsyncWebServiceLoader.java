package com.iwmf.http;

import android.os.AsyncTask;

import com.iwmf.listeners.Callbacks;
import com.iwmf.utils.AESCrypt;
import com.iwmf.utils.ConstantData;
import com.iwmf.utils.Utils;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

/**
 * <p> Handles network communication asynchronously.
 * Notify the calling method of success or failure.
 * Also takes care of Encryption/Decryption before communication. </p>
 */
public class AsyncWebServiceLoader extends AsyncTask<String, Void, String> {

    private Callbacks callback = null;
    private boolean isSuccess = false;
    private String url = "";
    private RequestMethod requestMethod = null;
    private String requestParams = null;

    public AsyncWebServiceLoader(String url, RequestMethod requestMethod, String requestParams) {

        super();

        this.url = url;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;

    }

    public Callbacks getCallback() {

        return callback;
    }

    public void setCallback(Callbacks callback) {

        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            isSuccess = false;

            String result;

            final OkHttpClient client = new OkHttpClient();

            client.setConnectTimeout(60, TimeUnit.SECONDS);
            client.setWriteTimeout(60, TimeUnit.SECONDS);
            client.setReadTimeout(60, TimeUnit.SECONDS);

            String key = "sha";

            client.setCertificatePinner(new CertificatePinner.Builder().add("<domain>", key).build());

            Request request;
            Response response;

            switch (requestMethod) {
                case GET:
                    try {

                        if (requestParams != null && requestParams.length() > 0) {
                            url = url + "?" + PARAMS.TAG_BULKDATA + "=" + Utils.setServerString(requestParams);
                        }

                        request = new Request.Builder().url(url).build();

                        response = client.newCall(request).execute();

                        isSuccess = response.isSuccessful();

                        if (isSuccess) {

                            result = response.body().string();

                        } else {

                            result = response.toString();
                        }

                    } catch (Exception e) {
                        result = "";
                        e.printStackTrace();
                    }

                    break;
                case POST:

                    if (requestParams != null && requestParams.length() > 0) {

                        try {

                            requestParams = AESCrypt.encrypt(requestParams, false);

                            RequestBody body = new FormEncodingBuilder().add(PARAMS.TAG_BULKDATA, requestParams).build();

                            request = new Request.Builder().url(url).post(body).header(PARAMS.TAG_HEADERTOKEN, ConstantData.HEADERTOKEN).addHeader(PARAMS.TAG_DEVICETOKEN, AESCrypt.encrypt(ConstantData.REGISTRATIONID, false)).addHeader(PARAMS.TAG_LANGUAGE_CODE, AESCrypt.encrypt(ConstantData.LANGUAGE_CODE, false)).build();

                            response = client.newCall(request).execute();

                            isSuccess = response.isSuccessful();

                            if (isSuccess) {

                                result = response.body().string();

                                if (!Utils.isEmpty(result)) {

                                    result = AESCrypt.decrypt(result);
                                }

                            } else {

                                result = response.toString();
                            }

                        } catch (Exception e) {
                            result = "";
                            e.printStackTrace();
                        }
                    } else {
                        result = "";
                    }
                    break;

                default:
                    result = "";
                    break;
            }

            if (Utils.isEmpty(result)) {
                isSuccess = false;
            }

            return result;

        } catch (OutOfMemoryError e) {

            e.printStackTrace();
            isSuccess = false;

            return e.toString();

        } catch (Exception e) {

            e.printStackTrace();
            isSuccess = false;

            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);

        if (callback != null) {

            if (isSuccess) {

                callback.onResponse(result);

            } else {

                callback.onError(result);
            }
        }
    }
}
