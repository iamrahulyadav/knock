package com.proclivis.smartknock.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class VisitorVerifyDoneVo implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("installation_id")
    @Expose
    private String installationId;
    @SerializedName("member_id")
    @Expose
    private String memberId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("coming_from")
    @Expose
    private String comingFrom;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("visitor_image")
    @Expose
    private String visitorImage;
    @SerializedName("date_time_in")
    @Expose
    private String dateTimeIn;
    @SerializedName("date_time_out")
    @Expose
    private String dateTimeOut;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("last_updated_date")
    @Expose
    private String lastUpdatedDate;
    @SerializedName("visitor_type")
    @Expose
    private String visitorType;
    @SerializedName("member_name")
    @Expose
    private String memberName;
    @SerializedName("visitor_count")
    @Expose
    private String visitorCount;

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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getVisitorImage() {
        return visitorImage;
    }

    public void setVisitorImage(String visitorImage) {
        this.visitorImage = visitorImage;
    }

    public String getDateTimeIn() {
        return dateTimeIn;
    }

    public void setDateTimeIn(String dateTimeIn) {
        this.dateTimeIn = dateTimeIn;
    }

    public String getDateTimeOut() {
        return dateTimeOut;
    }

    public void setDateTimeOut(String dateTimeOut) {
        this.dateTimeOut = dateTimeOut;
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

    public String getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(String visitorType) {
        this.visitorType = visitorType;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(String visitorCount) {
        this.visitorCount = visitorCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.installationId);
        dest.writeString(this.memberId);
        dest.writeString(this.name);
        dest.writeString(this.mobileNo);
        dest.writeString(this.comingFrom);
        dest.writeString(this.purpose);
        dest.writeString(this.visitorImage);
        dest.writeString(this.dateTimeIn);
        dest.writeString(this.dateTimeOut);
        dest.writeString(this.creationDate);
        dest.writeString(this.lastUpdatedDate);
        dest.writeString(this.visitorType);
        dest.writeString(this.memberName);
        dest.writeString(this.visitorCount);
    }

    public VisitorVerifyDoneVo() {
    }

    protected VisitorVerifyDoneVo(Parcel in) {
        this.id = in.readString();
        this.installationId = in.readString();
        this.memberId = in.readString();
        this.name = in.readString();
        this.mobileNo = in.readString();
        this.comingFrom = in.readString();
        this.purpose = in.readString();
        this.visitorImage = in.readString();
        this.dateTimeIn = in.readString();
        this.dateTimeOut = in.readString();
        this.creationDate = in.readString();
        this.lastUpdatedDate = in.readString();
        this.visitorType = in.readString();
        this.memberName = in.readString();
        this.visitorCount = in.readString();
    }

    public static final Creator<VisitorVerifyDoneVo> CREATOR = new Creator<VisitorVerifyDoneVo>() {
        @Override
        public VisitorVerifyDoneVo createFromParcel(Parcel source) {
            return new VisitorVerifyDoneVo(source);
        }

        @Override
        public VisitorVerifyDoneVo[] newArray(int size) {
            return new VisitorVerifyDoneVo[size];
        }
    };
}
