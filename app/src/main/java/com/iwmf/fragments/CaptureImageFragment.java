package com.iwmf.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iwmf.R;
import com.iwmf.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p> Capture image from camera. </p>
 */
@SuppressWarnings("ALL")
public class CaptureImageFragment extends Fragment {

    private static final String TAG = CaptureImageFragment.class.getSimpleName();
    private ImageView imgFull;
    private String uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            uri = savedInstanceState.getString("uri");
        } else {
            uri = getArguments().getString("uri");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri", uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_image, container, false);
        mappingWidgets(mView);
        return mView;
    }

    private void mappingWidgets(View v) {

        imgFull = (ImageView) v.findViewById(R.id.imgFull);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        setImageFromFile();
    }

    private void setImageFromFile() {

        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {

                Bitmap bmp = null;
                Bitmap scaledBitmap = null;

                try {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(uri, options); // TODO Why decoding here if not saving file ?

                    int actualHeight = options.outHeight;
                    int actualWidth = options.outWidth;

                    float maxWidth = 1280.0f;
                    float maxHeight = 1920.0f;


                    float imgRatio = actualWidth / actualHeight;
                    float maxRatio = maxWidth / maxHeight;

                    if (actualHeight > maxHeight || actualWidth > maxWidth) {
                        if (imgRatio < maxRatio) {
                            imgRatio = maxHeight / actualHeight;
                            actualWidth = (int) (imgRatio * actualWidth);
                            actualHeight = (int) maxHeight;
                        } else if (imgRatio > maxRatio) {
                            imgRatio = maxWidth / actualWidth;
                            actualHeight = (int) (imgRatio * actualHeight);
                            actualWidth = (int) maxWidth;
                        } else {
                            actualHeight = (int) maxHeight;
                            actualWidth = (int) maxWidth;
                        }
                    }

                    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
                    options.inJustDecodeBounds = false;
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inTempStorage = new byte[16 * 1024];

                    try {
                        bmp = BitmapFactory.decodeFile(uri, options);
                    } catch (OutOfMemoryError exception) {
                        exception.printStackTrace();
                    }

                    try {
                        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                    } catch (OutOfMemoryError exception) {
                        exception.printStackTrace();
                    }

                    float ratioX = actualWidth / (float) options.outWidth;
                    float ratioY = actualHeight / (float) options.outHeight;
                    float middleX = actualWidth / 2.0f;
                    float middleY = actualHeight / 2.0f;

                    Matrix scaleMatrix = new Matrix();
                    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

                    Canvas canvas = new Canvas(scaledBitmap);
                    canvas.setMatrix(scaleMatrix);
                    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

                    ExifInterface exif;
                    try {
                        exif = new ExifInterface(uri);

                        // ***< remove meta tags >******

                        String[] exifTag = new String[]{ExifInterface.TAG_APERTURE, ExifInterface.TAG_DATETIME, ExifInterface.TAG_FLASH, ExifInterface.TAG_FOCAL_LENGTH, ExifInterface.TAG_EXPOSURE_TIME, ExifInterface.TAG_GPS_ALTITUDE, ExifInterface.TAG_GPS_ALTITUDE_REF, ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF, ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF, ExifInterface.TAG_GPS_DATESTAMP, ExifInterface.TAG_GPS_TIMESTAMP, ExifInterface.TAG_GPS_PROCESSING_METHOD, ExifInterface.TAG_ISO, ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL};

                        for (String anExifTag : exifTag) {
                            try {
                                exif.setAttribute(anExifTag, "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, "0/0,0/0,0/0");
                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, "0/0,0/0,0/0");
                            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, "0");
                            exif.setAttribute(ExifInterface.TAG_ISO, "0");
                            exif.setAttribute(ExifInterface.TAG_FLASH, "0");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            exif.saveAttributes();
                        }

                        // *********

                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                        Matrix matrix = new Matrix();
                        if (orientation == 6) {
                            matrix.postRotate(90);
                        } else if (orientation == 3) {
                            matrix.postRotate(180);
                        } else if (orientation == 8) {
                            matrix.postRotate(270);
                        }
                        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    scaledBitmap = saveBitmap2URI(scaledBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return scaledBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {

                super.onPostExecute(result);
                if (result != null) {

                    imgFull.setImageBitmap(result);
                }
            }
        }.execute(uri);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private Bitmap saveBitmap2URI(Bitmap bmp) {

        FileOutputStream out = null;

        try {

            File filename = new File(uri);

            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, out);

            String aes = Utils.getMediaAES(filename);

            Utils.writeToFile(filename, aes, getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bmp;
    }

}
