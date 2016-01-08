package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store data about contact from address book. </p>
 */
@SuppressWarnings("ALL")
public class AddressBookContactData implements Serializable {

    private static final long serialVersionUID = 5547919417065460437L;
    private String id = "";
    private String name = "";
    private String number = "";
    private String email = "";

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = (name == null ? "" : name);
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {

        this.number = number;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

}
