package com.iwmf;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iwmf.base.BaseAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p> This is used to record audio for checkin activity.
 * User can upload audio to notify its cirlce.  </p>
 */
@SuppressWarnings("ALL")
public class CheckinAddAudioActivity extends BaseAppCompatActivity implements OnClickListener {

    private MediaRecorder mrec = null;
    private String filename = "";
    private String sdCardPath = "";
    private CheckBox chkCapture;
    private ImageButton iBtnClose;
    private TextView txtDuration;
    private int timerVal;
    private TimerTask timerTask;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_checkin_add_audio);

        File file = new File(Environment.getExternalStorageDirectory().getPath(), ".IWMF");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, ".Audio");
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

        sdCardPath = Environment.getExternalStorageDirectory().getPath();

        chkCapture = (CheckBox) findViewById(R.id.chkCapture);

        iBtnClose = (ImageButton) findViewById(R.id.iBtnClose);
        txtDuration = (TextView) findViewById(R.id.txtDuration);

        iBtnClose.setOnClickListener(this);

        chkCapture.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    startRecording();
                    timerVal = 0;
                    updateTimer();

                } else {
                    stopRecording();
                    goBack();
                }
            }

        });

    }

    private void updateTimer() {

        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {

                timerVal += 1;
                runOnUiThread(new Runnable() {

                    public void run() {

                        if (timerVal < 10) {
                            txtDuration.setText("00:0" + timerVal);
                        } else {
                            txtDuration.setText("00:" + timerVal);
                        }
                    }
                });

            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void goBack() {

        Intent intent = new Intent();
        intent.putExtra("path", sdCardPath + filename);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v == iBtnClose) {
            finish();
        }
    }

    private void startRecording() {

        try {

            if (mrec != null) {
                mrec.stop();
                mrec.release();
                mrec = null;
            }

            filename = "/.IWMF/.Audio/Native_" + System.currentTimeMillis() + ".3gp";

            mrec = new MediaRecorder();
            mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
            mrec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mrec.setOutputFile(sdCardPath + filename);
            mrec.setMaxDuration(60000);
            mrec.prepare();
            mrec.start();
            mrec.setOnInfoListener(new MediaRecorder.OnInfoListener() {

                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {

                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        timer.purge();
                        timer.cancel();
                        goBack();
                    }
                }

            });
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {

        try {
            if (mrec != null) {
                mrec.stop();
                mrec.release();
                mrec = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {


        try {
            if (mrec != null) {
                mrec.stop();
                mrec.release();
                mrec = null;
            }
        } catch (Exception ignored) {

        }

        super.onDestroy();
    }
}
