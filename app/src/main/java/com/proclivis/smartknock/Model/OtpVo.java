package com.proclivis.smartknock.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpVo implements Parcelable {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Details")
    @Expose
    private String details;

    public OtpVo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.details);
    }

    protected OtpVo(Parcel in) {
        this.status = in.readString();
        this.details = in.readString();
    }

    public static final Creator<OtpVo> CREATOR = new Creator<OtpVo>() {
        @Override
        public OtpVo createFromParcel(Parcel source) {
            return new OtpVo(source);
        }

        @Override
        public OtpVo[] newArray(int size) {
            return new OtpVo[size];
        }
    };
}