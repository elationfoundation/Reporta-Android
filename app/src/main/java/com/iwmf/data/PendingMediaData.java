package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store information of pending media. </p>
 */
public class PendingMediaData implements Serializable {

    private static final long serialVersionUID = 2719078692893904221L;
    private int id = -1;
    private int ref_id = 0;
    private String filePath = "";
    private int mediaType = -1; // 1 for Audio, 2 for Video, 3 for Picture.
    private long createdDate = 0;
    private int tableId = 0; // 1 for checking and 2 for Alerts.
    private double longitude = 0;
    private double latitude = 0;
    private int attemptCount = 0;

    public int getTableId() {

        return tableId;
    }

    public void setTableId(int tableId) {

        this.tableId = tableId;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public int getAttemptCount() {

        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {

        this.attemptCount = attemptCount;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getRef_id() {

        return ref_id;
    }

    public void setRef_id(int ref_id) {

        this.ref_id = ref_id;
    }

    public String getFilePath() {

        return filePath;
    }

    public void setFilePath(String filePath) {

        this.filePath = filePath;
    }

    public int getMediaType() {

        return mediaType;
    }

    public void setMediaType(int mediaType) {

        this.mediaType = mediaType;
    }

    public long getCreatedDate() {

        return createdDate;
    }

    public void setCreatedDate(long createdDate) {

        this.createdDate = createdDate;
    }

}
