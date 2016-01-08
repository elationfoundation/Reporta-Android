package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store details about user job.
 * NOT USED. </p>
 */
@SuppressWarnings("ALL")
public class SelectJobTitleData implements Serializable {

    private static final long serialVersionUID = 3942763039386241346L;
    private String name = "";
    private boolean checked = false;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public boolean isChecked() {

        return checked;
    }

    public void setChecked(boolean checked) {

        this.checked = checked;
    }
}
