package com.proclivis.smartknock.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
 * Created by archirayan on 27/3/18.
 */

public class GetMemberVisitorVo implements Parcelable {

    @SerializedName("pro")
    @Expose
    private String pro;
    @SerializedName("customer_installation_id")
    @Expose
    private String customerInstallationId;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("customer_message")
    @Expose
    private String customerMessage;
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("member_name")
    @Expose
    private String memberName;
    @SerializedName("vistor_record_id")
    @Expose
    private String vistorRecordId;
    @SerializedName("visitor_name")
    @Expose
    private String visitorName;
    @SerializedName("visitor_mobile_no")
    @Expose
    private String visitorMobileNo;
    @SerializedName("visitor_coming_from")
    @Expose
    private String visitorComingFrom;
    @SerializedName("visitor_purpose")
    @Expose
    private String visitorPurpose;
    @SerializedName("visitor_image")
    @Expose
    private String visitorImage;
    @SerializedName("visitor_date_time_in")
    @Expose
    private String visitorDateTimeIn;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("visitor_type")
    @Expose
    private String visitorType;
    @SerializedName("visitor_date_time_out")
    @Expose
    private String visitorDateTimeOut;
    @SerializedName("visitor_creation_date")
    @Expose
    private String visitorCreationDate;
    @SerializedName("visitor_last_updated_date")
    @Expose
    private String visitorLastUpdatedDate;
    @SerializedName("visitor_count")
    @Expose
    private String visitorCount;
    @SerializedName("date_in")
    @Expose
    private String dateIn;
    @SerializedName("date_out")
    @Expose
    private String dateOut;

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getCustomerInstallationId() {
        return customerInstallationId;
    }

    public void setCustomerInstallationId(String customerInstallationId) {
        this.customerInstallationId = customerInstallationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMessage() {
        return customerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        this.customerMessage = customerMessage;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getVistorRecordId() {
        return vistorRecordId;
    }

    public void setVistorRecordId(String vistorRecordId) {
        this.vistorRecordId = vistorRecordId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorMobileNo() {
        return visitorMobileNo;
    }

    public void setVisitorMobileNo(String visitorMobileNo) {
        this.visitorMobileNo = visitorMobileNo;
    }

    public String getVisitorComingFrom() {
        return visitorComingFrom;
    }

    public void setVisitorComingFrom(String visitorComingFrom) {
        this.visitorComingFrom = visitorComingFrom;
    }

    public String getVisitorPurpose() {
        return visitorPurpose;
    }

    public void setVisitorPurpose(String visitorPurpose) {
        this.visitorPurpose = visitorPurpose;
    }

    public String getVisitorImage() {
        return visitorImage;
    }

    public void setVisitorImage(String visitorImage) {
        this.visitorImage = visitorImage;
    }

    public String getVisitorDateTimeIn() {
        return visitorDateTimeIn;
    }

    public void setVisitorDateTimeIn(String visitorDateTimeIn) {
        this.visitorDateTimeIn = visitorDateTimeIn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(String visitorType) {
        this.visitorType = visitorType;
    }

    public String getVisitorDateTimeOut() {
        return visitorDateTimeOut;
    }

    public void setVisitorDateTimeOut(String visitorDateTimeOut) {
        this.visitorDateTimeOut = visitorDateTimeOut;
    }

    public String getVisitorCreationDate() {
        return visitorCreationDate;
    }

    public void setVisitorCreationDate(String visitorCreationDate) {
        this.visitorCreationDate = visitorCreationDate;
    }

    public String getVisitorLastUpdatedDate() {
        return visitorLastUpdatedDate;
    }

    public void setVisitorLastUpdatedDate(String visitorLastUpdatedDate) {
        this.visitorLastUpdatedDate = visitorLastUpdatedDate;
    }

    public String getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(String visitorCount) {
        this.visitorCount = visitorCount;
    }

    public String getDateIn() {
        return dateIn;
    }

    public void setDateIn(String dateIn) {
        this.dateIn = dateIn;
    }

    public String getDateOut() {
        return dateOut;
    }

    public void setDateOut(String dateOut) {
        this.dateOut = dateOut;
    }

    public static final String COLOM_pro = "pro";
    public static final String COLOM_customerInstallationId = "customerInstallationId";
    public static final String COLOM_customerName = "customerName";
    public static final String COLOM_customerMessage = "customerMessage";
    public static final String COLOM_memberId = "memberId";
    public static final String COLOM_memberName = "memberName";
    public static final String COLOM_vistorRecordId = "vistorRecordId";
    public static final String COLOM_visitorName = "visitorName";
    public static final String COLOM_visitorMobileNo = "visitorMobileNo";
    public static final String COLOM_visitorComingFrom = "visitorComingFrom";
    public static final String COLOM_visitorPurpose = "visitorPurpose";
    public static final String COLOM_visitorImage = "visitorImage";
    public static final String COLOM_visitorDateTimeIn = "visitorDateTimeIn";
    public static final String COLOM_reson = "reason";
    public static final String COLOM_status = "status";
    public static final String COLOM_visitorType = "visitorType";
    public static final String COLOM_visitorDateTimeOut = "visitorDateTimeOut";
    public static final String COLOM_visitorCreationDate = "visitorCreationDate";
    public static final String COLOM_visitorLastUpdatedDate = "visitorLastUpdatedDate";
    public static final String COLOM_visitorCount = "visitorCount";

    public static final String TABLE_NAME = "member_visitor_records";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLOM_pro + " TEXT,"
                    + COLOM_customerInstallationId + " TEXT,"
                    + COLOM_customerName + " TEXT,"
                    + COLOM_customerMessage + " TEXT,"
                    + COLOM_memberId + " TEXT,"
                    + COLOM_memberName + " TEXT,"
                    + COLOM_vistorRecordId + " TEXT,"
                    + COLOM_visitorName + " TEXT,"
                    + COLOM_visitorMobileNo + " TEXT,"
                    + COLOM_visitorComingFrom + " TEXT,"
                    + COLOM_visitorPurpose + " TEXT,"
                    + COLOM_visitorImage + " TEXT,"
                    + COLOM_visitorDateTimeIn + " TEXT,"
                    + COLOM_reson + " TEXT,"
                    + COLOM_status + " TEXT,"
                    + COLOM_visitorType + " TEXT,"
                    + COLOM_visitorDateTimeOut + " TEXT,"
                    + COLOM_visitorCreationDate + " TEXT,"
                    + COLOM_visitorLastUpdatedDate + " TEXT,"
                    + COLOM_visitorCount + " TEXT"
                    + ")";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pro);
        dest.writeString(this.customerInstallationId);
        dest.writeString(this.customerName);
        dest.writeString(this.customerMessage);
        dest.writeString(this.memberId);
        dest.writeString(this.memberName);
        dest.writeString(this.vistorRecordId);
        dest.writeString(this.visitorName);
        dest.writeString(this.visitorMobileNo);
        dest.writeString(this.visitorComingFrom);
        dest.writeString(this.visitorPurpose);
        dest.writeString(this.visitorImage);
        dest.writeString(this.visitorDateTimeIn);
        dest.writeString(this.reason);
        dest.writeString(this.status);
        dest.writeString(this.visitorType);
        dest.writeString(this.visitorDateTimeOut);
        dest.writeString(this.visitorCreationDate);
        dest.writeString(this.visitorLastUpdatedDate);
        dest.writeString(this.visitorCount);
        dest.writeString(this.dateIn);
        dest.writeString(this.dateOut);
    }

    public GetMemberVisitorVo() {
    }

    private GetMemberVisitorVo(Parcel in) {
        this.pro = in.readString();
        this.customerInstallationId = in.readString();
        this.customerName = in.readString();
        this.customerMessage = in.readString();
        this.memberId = in.readString();
        this.memberName = in.readString();
        this.vistorRecordId = in.readString();
        this.visitorName = in.readString();
        this.visitorMobileNo = in.readString();
        this.visitorComingFrom = in.readString();
        this.visitorPurpose = in.readString();
        this.visitorImage = in.readString();
        this.visitorDateTimeIn = in.readString();
        this.reason = in.readString();
        this.status = in.readString();
        this.visitorType = in.readString();
        this.visitorDateTimeOut = in.readString();
        this.visitorCreationDate = in.readString();
        this.visitorLastUpdatedDate = in.readString();
        this.visitorCount = in.readString();
        this.dateIn = in.readString();
        this.dateOut = in.readString();
    }

    public static final Creator<GetMemberVisitorVo> CREATOR = new Creator<GetMemberVisitorVo>() {
        @Override
        public GetMemberVisitorVo createFromParcel(Parcel source) {
            return new GetMemberVisitorVo(source);
        }

        @Override
        public GetMemberVisitorVo[] newArray(int size) {
            return new GetMemberVisitorVo[size];
        }
    };
}