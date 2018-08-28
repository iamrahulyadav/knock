package com.proclivis.smartknock.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by archirayan on 27/3/18.
 */

public class MemberDetail {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("installation_id")
    @Expose
    private String installationId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("address3")
    @Expose
    private String address3;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("enabled")
    @Expose
    private String enabled;
    @SerializedName("pro")
    @Expose
    private String pro;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("last_updated_date")
    @Expose
    private String lastUpdatedDate;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("m_id")
    @Expose
    private String mId;
    @SerializedName("flat_no")
    @Expose
    private String flatNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("reason")
    @Expose
    private String reason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
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

    public String getMId() {
        return mId;
    }

    public void setMId(String mId) {
        this.mId = mId;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public static final String COLUMN_id = "id";
    public static final String COLUMN_installationId = "installationId";
    public static final String COLUMN_name = "name";
    public static final String COLUMN_mobileNo = "mobileNo";
    public static final String COLUMN_address1 = "address1";
    public static final String COLUMN_address2 = "address2";
    public static final String COLUMN_address3 = "address3";
    public static final String COLUMN_city = "city";
    public static final String COLUMN_state = "state";
    public static final String COLUMN_message = "message";
    public static final String COLUMN_pincode = "pincode";
    public static final String COLUMN_enabled = "enabled";
    public static final String COLUMN_pro = "pro";
    public static final String COLUMN_creationDate = "creationDate";
    public static final String COLUMN_lastUpdatedDate = "lastUpdatedDate";
    public static final String COLUMN_mId = "mId";
    public static final String COLUMN_flatNo = "flatNo";
    public static final String COLUMN_status = "status";
    public static final String COLUMN_reason = "reason";

    public static final String TABLE_NAME = "member_soc_records";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_id + " TEXT,"
                    + COLUMN_installationId + " TEXT,"
                    + COLUMN_name + " TEXT,"
                    + COLUMN_mobileNo + " TEXT,"
                    + COLUMN_address1 + " TEXT,"
                    + COLUMN_address2 + " TEXT,"
                    + COLUMN_address3 + " TEXT,"
                    + COLUMN_city + " TEXT,"
                    + COLUMN_state + " TEXT,"
                    + COLUMN_message + " TEXT,"
                    + COLUMN_pincode + " TEXT,"
                    + COLUMN_enabled + " TEXT,"
                    + COLUMN_pro + " TEXT,"
                    + COLUMN_creationDate + " TEXT,"
                    + COLUMN_lastUpdatedDate + " TEXT,"
                    + COLUMN_mId + " TEXT,"
                    + COLUMN_flatNo + " TEXT,"
                    + COLUMN_status + " TEXT,"
                    + COLUMN_reason + " TEXT"
                    + ")";


}
