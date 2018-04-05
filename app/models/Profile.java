package models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Profile extends com.avaje.ebean.Model {

    @Id
    @Column(columnDefinition = "INT(11) NOT NULL")
    @JsonIgnore
    private int id;

    @OneToOne
    @JoinColumn(name = "id")
    @JsonIgnore
    private User user;

    @Column(columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    private String researchAreas;

    @Column(columnDefinition = "VARCHAR(255)")
    private String firstName;

    @Column(columnDefinition = "VARCHAR(255)")
    private String lastName;

    @Column(columnDefinition = "VARCHAR(255)")
    private String position;

    @Column(columnDefinition = "VARCHAR(255)")
    private String affiliation;

    @Column(columnDefinition = "VARCHAR(255)")
    private String email;

    @Column(columnDefinition = "VARCHAR(255)")
    private String phone;

    @Column(columnDefinition = "VARCHAR(255)")
    private String fax;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(columnDefinition = "VARCHAR(255)")
    private String country;

    @Column(columnDefinition = "VARCHAR(255)")
    private String zipCode;

    @Column(columnDefinition = "TEXT")
    private String comments;

    public static Finder<Integer, Profile> find = new Finder<Integer, Profile>(Profile.class);


    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResearchAreas() {
        return researchAreas;
    }

    public void setResearchAreas(String researchAreas) {
        this.researchAreas = researchAreas;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
