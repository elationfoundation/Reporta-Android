package com.iwmf.data;

import com.iwmf.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p> Model class to store data about ContactList. </p>
 */
@SuppressWarnings("ALL")
public class ContactListData implements Serializable {

    private static final long serialVersionUID = 2795019774863136029L;
    private String contactlist_id = "";
    private String user_id = "";
    private String circle = "";
    private String listname = "";
    private String defaultstatus = "0";
    private String is_associated = "";

    private ArrayList<Contacts> Contacts = null;

    public String getUser_id() {

        return user_id;
    }

    public void setUser_id(String user_id) {

        this.user_id = user_id;
    }

    public String getContactlist_id() {

        return contactlist_id;
    }

    public void setContactlist_id(String contactlist_id) {

        this.contactlist_id = contactlist_id;
    }

    public String getCircle() {

        return circle;
    }

    public void setCircle(String circle) {

        this.circle = circle;
    }

    public String getListname() {

        return Utils.capitalize(listname);
    }

    public void setListname(String listname) {

        this.listname = listname;
    }

    public String getDefaultstatus() {

        return defaultstatus;
    }

    public void setDefaultstatus(String defaultstatus) {

        this.defaultstatus = defaultstatus;
    }

    public ArrayList<Contacts> getContacts() {

        return Contacts;
    }

    public void setContacts(ArrayList<Contacts> contacts) {

        Contacts = contacts;
    }

    public String getIs_associated() {

        return is_associated;
    }

    public void setIs_associated(String is_associated) {

        this.is_associated = is_associated;
    }

}
