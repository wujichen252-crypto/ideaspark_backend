package com.ideaspark.project.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserUpdateRequest {
    private String avatar;
    private String username;
    private String email;

    @JsonProperty("password_hash")
    private String password;

    private String position;
    private String bio;
    private String address;

    @JsonProperty("per_website")
    private String perWebsite;

    private String phone;

    @JsonProperty("is_hide")
    private Boolean isHide;

    @JsonProperty("is_notifisys")
    private Boolean isNotifSys;

    @JsonProperty("is_notiftrends")
    private Boolean isNotifTrends;

    @JsonProperty("is_notifipost")
    private Boolean isNotifPost;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerWebsite() {
        return perWebsite;
    }

    public void setPerWebsite(String perWebsite) {
        this.perWebsite = perWebsite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(Boolean isHide) {
        this.isHide = isHide;
    }

    public Boolean getIsNotifSys() {
        return isNotifSys;
    }

    public void setIsNotifSys(Boolean isNotifSys) {
        this.isNotifSys = isNotifSys;
    }

    public Boolean getIsNotifTrends() {
        return isNotifTrends;
    }

    public void setIsNotifTrends(Boolean isNotifTrends) {
        this.isNotifTrends = isNotifTrends;
    }

    public Boolean getIsNotifPost() {
        return isNotifPost;
    }

    public void setIsNotifPost(Boolean isNotifPost) {
        this.isNotifPost = isNotifPost;
    }
}
