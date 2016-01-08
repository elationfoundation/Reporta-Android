package com.iwmf.fragments;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iwmf.R;
import com.iwmf.utils.Utils;

import java.io.File;

public class VideoFragment extends Fragment {

    private ImageView imgFull;
    private Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uri = Uri.parse(savedInstanceState.getString("uri"));
        } else {
            uri = Uri.parse(getArguments().getString("uri"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("uri", uri.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_video, container, false);
        imgFull = (ImageView) mView.findViewById(R.id.imgFull);
        return mView;
    }

    private String getAbsolutePath(Uri uri) {

        String path = "";
        try {
            Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
            assert c != null;
            c.moveToNext();
            path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getAbsolutePath(uri), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        imgFull.setImageBitmap(thumb);

        File filename = new File(getAbsolutePath(uri));

        String aes = Utils.getMediaAES(filename);
        Utils.writeToFile(filename, aes, getActivity());
    }
}
