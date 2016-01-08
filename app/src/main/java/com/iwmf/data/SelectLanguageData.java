package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to save the language selection by user. </p>
 */
@SuppressWarnings("ALL")
public class SelectLanguageData implements Serializable {

    private static final long serialVersionUID = 194729216058857783L;

    private String Name = "";
    private String Code = "";

    private boolean checked = false;

    public SelectLanguageData(String Name, String Code, boolean checked) {

        super();

        this.Name = Name;
        this.Code = Code;
        this.checked = checked;
    }

    public SelectLanguageData(String Title, boolean checked) {

        super();

        this.Name = Title;
        this.checked = checked;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public boolean isChecked() {

        return checked;
    }

    public void setChecked(boolean checked) {

        this.checked = checked;
    }
}
