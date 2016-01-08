package com.iwmf;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.iwmf.base.BaseAppCompatActivity;
import com.iwmf.data.PendingMediaData;
import com.iwmf.fragments.CaptureImageFragment;
import com.iwmf.fragments.VideoFragment;
import com.iwmf.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * <p> Main activity to handle all media types like Image capture, Audio recording and Video recording. </p>
 */

@SuppressWarnings("ALL")
public class MediaActivity extends BaseAppCompatActivity implements OnClickListener {

    private static final int FRAGMENT_CONTAINER = R.id.fragment_media_container;
    private static final int CAMERA_CODE = 3, VIDEO_CODE = 1, AUDIO_CODE = 2;
    private Uri mediaUri = null;
    private File file = null;
    private Button btnDone = null;
    private int type = 0;
    private MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        File file = new File(Environment.getExternalStorageDirectory().getPath(), ".IWMF");
        if (!file.exists()) {
            file.mkdirs();
        }

        File mImageNoMedia = new File(file, ".nomedia");
        if (!mImageNoMedia.exists()) {
            try {
                mImageNoMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File mImageDirectory = new File(file, ".Images");
        if (!mImageDirectory.exists()) {
            mImageDirectory.mkdirs();
        }
        mImageNoMedia = new File(mImageDirectory, ".nomedia");
        if (!mImageNoMedia.exists()) {
            try {
                mImageNoMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File mVideoDirectory = new File(file, ".Videos");
        if (!mVideoDirectory.exists()) {
            mVideoDirectory.mkdirs();
        }

        mImageNoMedia = new File(mVideoDirectory, ".nomedia");
        if (!mImageNoMedia.exists()) {
            try {
                mImageNoMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageButton iBtnCamera = (ImageButton) findViewById(R.id.iBtnCamera);
        ImageButton iBtnVideo = (ImageButton) findViewById(R.id.iBtnVideo);
        ImageButton iBtnAudio = (ImageButton) findViewById(R.id.iBtnAudio);
        btnDone = (Button) findViewById(R.id.btnDone);

        iBtnCamera.setOnClickListener(this);
        iBtnVideo.setOnClickListener(this);
        iBtnAudio.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    public void addFragment(Fragment f) {

        getSupportFragmentManager().beginTransaction().replace(FRAGMENT_CONTAINER, f).commit();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iBtnCamera:

                takeImageUsingCamera();

                break;

            case R.id.iBtnVideo:

                takeVideoUsingCamera();

                break;

            case R.id.iBtnAudio:

                startActivityForResult(new Intent(getApplicationContext(), CheckinAddAudioActivity.class), AUDIO_CODE);

                break;
            case R.id.btnDone:

                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        mediaPlayer = null;
                    }
                }

                PendingMediaData data = new PendingMediaData();
                data.setMediaType(type);
                data.setFilePath(file.getAbsolutePath());
                btnDone.setVisibility(View.GONE);
                Intent intent = new Intent();
                intent.putExtra("data", data);
                setResult(RESULT_OK, intent);

                finish();

                break;

        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.pop_exit);
    }

    private void takeImageUsingCamera() {

        file = new File(Environment.getExternalStorageDirectory().getPath(), ".IWMF/.Images/" + System.currentTimeMillis() + ".jpg");

        mediaUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    private void takeVideoUsingCamera() {

        file = new File(Environment.getExternalStorageDirectory().getPath(), ".IWMF/.Videos/" + System.currentTimeMillis() + ".mp4");

        mediaUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);

        startActivityForResult(intent, VIDEO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            type = requestCode;

            btnDone.setVisibility(View.VISIBLE);

            if (requestCode == CAMERA_CODE) {

                CaptureImageFragment fragment = new CaptureImageFragment();
                mediaUri = getImageContentUri(file);
                Bundle bundle = new Bundle();
                bundle.putString("uri", file.getAbsolutePath());
                fragment.setArguments(bundle);
                addFragment(fragment);

            } else if (requestCode == VIDEO_CODE) {

                VideoFragment fragment = new VideoFragment();

                mediaUri = getVideoContentUri(file);
                Bundle bundle = new Bundle();
                bundle.putString("uri", mediaUri.toString());
                fragment.setArguments(bundle);
                addFragment(fragment);

            } else if (requestCode == AUDIO_CODE) {
                try {
                    file = new File(data.getExtras().getString("path"));

                    String aes = Utils.getMediaAES(file);
                    Utils.writeToFile(file, aes, this);
                    mediaUri = getAudioContentUri(file);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Uri getImageContentUri(File imageFile) {

        return Uri.fromFile(imageFile);
    }

    private Uri getVideoContentUri(File videoFile) {

        Uri uri = null;
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.VideoColumns._ID}, MediaStore.Video.VideoColumns.DATA + "=? ", new String[]{filePath}, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "" + id);
            } else {
                if (videoFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DATA, filePath);
                    uri = this.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    uri = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri;
    }

    private Uri getAudioContentUri(File audioFile) {

        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.AudioColumns._ID}, MediaStore.Audio.AudioColumns.DATA + "=? ", new String[]{filePath}, null);

        Uri url = null;

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                url = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id);
            } else {
                if (audioFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Audio.Media.DATA, filePath);
                    url = this.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    url = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return url;
    }
}
