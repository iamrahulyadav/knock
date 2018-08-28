package com.proclivis.smartknock.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by archirayan on 16/3/18.
 */

public class VisitorConfirmVo implements Parcelable {

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
    @SerializedName("coming_from")
    @Expose
    private String comingFrom;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("last_updated_date")
    @Expose
    private String lastUpdatedDate;

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.installationId);
        dest.writeString(this.name);
        dest.writeString(this.mobileNo);
        dest.writeString(this.comingFrom);
        dest.writeString(this.purpose);
        dest.writeString(this.creationDate);
        dest.writeString(this.lastUpdatedDate);
    }

    public VisitorConfirmVo() {
    }

    protected VisitorConfirmVo(Parcel in) {
        this.id = in.readString();
        this.installationId = in.readString();
        this.name = in.readString();
        this.mobileNo = in.readString();
        this.comingFrom = in.readString();
        this.purpose = in.readString();
        this.creationDate = in.readString();
        this.lastUpdatedDate = in.readString();
    }

    public static final Creator<VisitorConfirmVo> CREATOR = new Creator<VisitorConfirmVo>() {
        @Override
        public VisitorConfirmVo createFromParcel(Parcel source) {
            return new VisitorConfirmVo(source);
        }

        @Override
        public VisitorConfirmVo[] newArray(int size) {
            return new VisitorConfirmVo[size];
        }
    };
}
