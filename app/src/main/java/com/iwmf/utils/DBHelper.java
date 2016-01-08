package com.iwmf.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iwmf.data.PendingMediaData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * <p> Database to store media files taken for checkin. </p>
 */
@SuppressWarnings("ALL")
public class DBHelper {

    // Path of device database where application's list is stored.
    private static String DB_PATH = "";
    private static String DATABASE_NAME = "IWMF.sqlite";
    private static String TABLE_MEDIA = "tbl_media";
    private DatabaseHelper dataHelper;
    private SQLiteDatabase db;
    private boolean isOpen;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

    public DBHelper(Context ctx) {

        Context context = ctx;

        try {

            File dbFile = context.getDatabasePath(DATABASE_NAME);
            DB_PATH = dbFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            DB_PATH = ctx.getFilesDir().getPath() + context.getPackageName() + "/databases/" + DATABASE_NAME;
        }

        dataHelper = new DatabaseHelper(context);
    }

    public DBHelper open() throws SQLException {

        try {
            boolean isExist = dataHelper.checkDataBase();

            if (!isExist) {
                db = dataHelper.getWritableDatabase();
                dataHelper.copyDataBase();
                if (db.isOpen()) {
                    db.close();
                }
            }

            db = dataHelper.getWritableDatabase();
            isOpen = true;
        } catch (Exception e) {
            isOpen = false;
        }
        return this;
    }

    public boolean isOpen() {

        return isOpen;
    }

    public boolean isDBExist() {

        return dataHelper.checkDataBase();
    }

    public void close() {

        if (db != null) {
            db.close();
        }

        db = null;
        isOpen = false;
    }

    public void insertPendingMedia(int refId, String filePath, int mediaType, long createdDate, int tableId, double latitude, double longitude, int attemptCount) {

        try {

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(createdDate);

            ContentValues initialValues = new ContentValues();

            initialValues.put("ref_id", refId);
            initialValues.put("filePath", filePath);
            initialValues.put("mediaType", mediaType);
            initialValues.put("createdDate", dateFormat.format(cal.getTime()));
            initialValues.put("tableId", tableId);
            initialValues.put("latitude", latitude);
            initialValues.put("longitude", longitude);
            initialValues.put("attemptCount", attemptCount);

            db.insert(TABLE_MEDIA, null, initialValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateMediaAttemptCount(int id) {

        try {
            Cursor cursor = null;
            int attemptCount = 0;
            try {
                cursor = db.query(TABLE_MEDIA, new String[]{"attemptCount"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    attemptCount = cursor.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            attemptCount++;

            ContentValues initialValues = new ContentValues();
            initialValues.put("attemptCount", attemptCount);

            db.update(TABLE_MEDIA, initialValues, "id=?", new String[]{String.valueOf(id)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PendingMediaData> getPendingMediaList() {

        ArrayList<PendingMediaData> list = new ArrayList<>();
        Cursor cursor = null;

        try {

            cursor = db.query(TABLE_MEDIA, null, null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {

                    cursor.moveToPosition(i);

                    PendingMediaData data = new PendingMediaData();

                    data.setId(cursor.getInt(0));
                    data.setRef_id(cursor.getInt(1));
                    data.setFilePath(cursor.getString(2));
                    data.setMediaType(cursor.getInt(3));

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateFormat.parse(cursor.getString(4)));
                    data.setCreatedDate(cal.getTimeInMillis());

                    data.setTableId(cursor.getInt(5));
                    data.setLatitude(cursor.getDouble(6));
                    data.setLongitude(cursor.getDouble(7));
                    data.setAttemptCount(cursor.getInt(8));

                    list.add(data);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public boolean deleteMedia(int id) {

        try {
            int count = db.delete(TABLE_MEDIA, "id = ?", new String[]{String.valueOf(id)});
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getPendingMediaCount() {

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_MEDIA, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        return cursor.getInt(0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    public int getPendingMediaCountForRefid(String ref_id) {

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT count(*) FROM  WHERE ref_id=?" + TABLE_MEDIA, new String[]{ref_id});

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        return cursor.getInt(0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        Context myContext = null;

        DatabaseHelper(Context context) {

            //  int DATABASE_VERSION = 1;
            super(context, DATABASE_NAME, null, 1);
            this.myContext = context;
        }

        private boolean checkDataBase() {

            File f = new File(DB_PATH);
            return f.exists();
        }

        private void copyDataBase() throws IOException {

            try {
                InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
                String outFileName = DB_PATH;
                OutputStream myOutput = new FileOutputStream(outFileName);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (Exception ignored) {
            }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
