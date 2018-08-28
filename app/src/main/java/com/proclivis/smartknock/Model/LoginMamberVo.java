package com.proclivis.smartknock.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by archirayan on 15/3/18.
 */

public class LoginMamberVo {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("installation_id")
    @Expose
    private String installationId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("member_profile_image")
    @Expose
    private String memberProfileImage;
    @SerializedName("mobile_unique_id")
    @Expose
    private String mobileUniqueId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_flag")
    @Expose
    private String emailFlag;
    @SerializedName("flat_no")
    @Expose
    private String flatNo;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("fcm_id_list")
    @Expose
    private String fcmIdList;
    @SerializedName("enabled")
    @Expose
    private String enabled;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("last_updated_date")
    @Expose
    private String lastUpdatedDate;
    @SerializedName("status")
    @Expose
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getMemberProfileImage() {
        return memberProfileImage;
    }

    public void setMemberProfileImage(String memberProfileImage) {
        this.memberProfileImage = memberProfileImage;
    }

    public String getMobileUniqueId() {
        return mobileUniqueId;
    }

    public void setMobileUniqueId(String mobileUniqueId) {
        this.mobileUniqueId = mobileUniqueId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailFlag() {
        return emailFlag;
    }

    public void setEmailFlag(String emailFlag) {
        this.emailFlag = emailFlag;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getFcmIdList() {
        return fcmIdList;
    }

    public void setFcmIdList(String fcmIdList) {
        this.fcmIdList = fcmIdList;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
