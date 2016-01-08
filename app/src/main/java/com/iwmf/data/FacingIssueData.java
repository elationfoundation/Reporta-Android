package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store data about the issue user is facing. </p>
 */
@SuppressWarnings("ALL")
public class FacingIssueData implements Serializable {

    private static final long serialVersionUID = -370969461027867060L;
    private String name = "";
    private boolean isChecked = false;

    public FacingIssueData(String name, boolean checked) {

        super();
        this.name = name;
        this.isChecked = checked;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public boolean isChecked() {

        return isChecked;
    }

    public void setChecked(boolean checked) {

        this.isChecked = checked;
    }

}
