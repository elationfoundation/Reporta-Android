package com.iwmf.data;

import com.iwmf.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p> Model class to store data about Contacts.
 * It also store associated circle of each contact. </p>
 */
@SuppressWarnings("ALL")
public class Contacts implements Serializable {

    private static final long serialVersionUID = -1677430579249463077L;
    private String contact_id = "";
    private String firstname = "";
    private String lastname = "";
    private String mobile = "";
    private String emails = "";
    private String sos_enabled = "";
    private String associated_id = "";
    private String contact_type = "";
    private String contact_exist = "";

    private ArrayList<Associated_circles> associated_circles = null;

    public String getContact_id() {

        return contact_id;
    }

    public void setContact_id(String contact_id) {

        this.contact_id = contact_id;
    }

    public String getContact_exist() {
        return contact_exist;
    }

    public void setContact_exist(String contact_exist) {
        this.contact_exist = contact_exist;
    }

    public String getContact_type() {
        return contact_type;
    }

    public void setContact_type(String contact_type) {
        this.contact_type = contact_type;
    }

    public String getFirstname() {

        return Utils.capitalize(firstname);
    }

    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }

    public String getLastname() {

        return Utils.capitalize(lastname);
    }

    public void setLastname(String lastname) {

        this.lastname = lastname;
    }

    public String getMobile() {

        return mobile;
    }

    public void setMobile(String mobile) {

        this.mobile = mobile;
    }

    public String getEmails() {

        return emails;
    }

    public void setEmails(String emails) {

        this.emails = emails;
    }

    public String getSos_enabled() {

        return sos_enabled;
    }

    public void setSos_enabled(String sos_enabled) {

        this.sos_enabled = sos_enabled;
    }

    public String getAssociated_id() {

        return associated_id;
    }

    public void setAssociated_id(String associated_id) {

        this.associated_id = associated_id;
    }

    public ArrayList<Associated_circles> getAssociated_circles() {

        return associated_circles;
    }

    public void setAssociated_circles(ArrayList<Associated_circles> associated_circles) {

        this.associated_circles = associated_circles;
    }

}
