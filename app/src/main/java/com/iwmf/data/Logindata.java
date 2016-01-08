package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store login time data of user. </p>
 */
@SuppressWarnings("ALL")
public class Logindata implements Serializable {

    private static final long serialVersionUID = -2102492212014711521L;
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String language;
    private String phone;
    private String jobtitle;
    private String affiliation_id;
    private String freelancer;
    private String origin_country;
    private String working_country;
    private String gender;
    private String gender_type;
    private String headertoken;

    public Logindata() {

    }

    public String getHeadertoken() {
        return headertoken;
    }

    public void setHeadertoken(String headertoken) {
        this.headertoken = headertoken;
    }


    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }

    public String getGender_type() {

        return gender_type;
    }

    public void setGender_type(String gender_type) {

        this.gender_type = gender_type;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getFirstname() {

        return firstname;
    }

    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }

    public String getLastname() {

        return lastname;
    }

    public void setLastname(String lastname) {

        this.lastname = lastname;
    }

    public String getLanguage() {

        return language;
    }

    public void setLanguage(String language) {

        this.language = language;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public String getJobtitle() {

        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {

        this.jobtitle = jobtitle;
    }

    public String getAffiliation_id() {

        return affiliation_id;
    }

    public void setAffiliation_id(String affiliation_id) {

        this.affiliation_id = affiliation_id;
    }

    public String getFreelancer() {

        return freelancer;
    }

    public void setFreelancer(String freelancer) {

        this.freelancer = freelancer;
    }

    public String getOrigin_country() {

        return origin_country;
    }

    public void setOrigin_country(String origin_country) {

        this.origin_country = origin_country;
    }

    public String getWorking_country() {

        return working_country;
    }

    public void setWorking_country(String working_country) {

        this.working_country = working_country;
    }

}
