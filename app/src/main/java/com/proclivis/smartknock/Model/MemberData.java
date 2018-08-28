package com.proclivis.smartknock.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemberData {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;

    @SerializedName("member_profile_image")
    @Expose
    private String memberprofileimage;

    @SerializedName("email_flag")
    @Expose
    private String emailflag;

    @SerializedName("enabled")
    @Expose
    private String enabled;
    @SerializedName("last_updated_date")
    @Expose
    private String lastUpdatedDate;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("type")
    @Expose
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMemberprofileimage() {
        return memberprofileimage;
    }

    public void setMemberprofileimage(String memberprofileimage) {
        this.memberprofileimage = memberprofileimage;
    }

    public String getEmailflag() {
        return emailflag;
    }

    public void setEmailflag(String emailflag) {
        this.emailflag = emailflag;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }


    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
