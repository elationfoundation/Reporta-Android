package com.iwmf.listeners;

/**
 * <p> Callback interface for server response. </p>
 */
public interface Callbacks {

    void onResponse(String result);

    void onError(String error);

}
