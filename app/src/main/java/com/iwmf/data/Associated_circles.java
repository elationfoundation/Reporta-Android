package com.iwmf.data;

import com.iwmf.utils.Utils;

import java.io.Serializable;

/**
 * <p> Model class to store data about Associated_circles. </p>
 */
public class Associated_circles implements Serializable {

    private static final long serialVersionUID = 7460071520028335184L;
    private String contactlist_id = "";
    private String listname = "";
    private String circle = "";

    public String getContactlist_id() {

        return contactlist_id;
    }

    public void setContactlist_id(String contactlist_id) {

        this.contactlist_id = contactlist_id;
    }

    public String getListname() {

        return Utils.capitalize(listname);
    }

    public void setListname(String listname) {

        this.listname = listname;
    }

    public String getCircle() {

        return circle;
    }

    public void setCircle(String circle) {

        this.circle = circle;
    }

}
