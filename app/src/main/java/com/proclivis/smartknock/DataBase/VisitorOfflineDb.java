package com.proclivis.smartknock.DataBase;

import android.os.Parcel;
import android.os.Parcelable;

public class VisitorOfflineDb implements Parcelable {

    public static final String TABLE_NAME = "VisitorOfflineDb";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VISITOR_RECORDS_ID = "COLUMN_VISITOR_RECORDS_ID";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MEMBER_ID = "member";
    public static final String COLUMN_MEMBER_NAME = "memberName";
    public static final String COLUMN_MOBILE_NO = "mobileNo";
    public static final String COLUMN_COMING_FROM = "comingFrom";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_DATE_TIME_IN = "date_time_in";
    public static final String COLUMN_DATE_TIME_OUT = "date_time_out";
    public static final String COLUMN_IS_SYNC = "is_sync";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_REASON = "reason";

    private int id;
    private String visitor_record_id;
    private String name;
    private String member;
    private String member_name;
    private String mobileNo;
    private String comingFrom;
    private String purpose;
    private String image;
    private String count;
    private String date_time_in;
    private String date_time_out;
    private String is_sync;
    private String status;
    private String reason;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + COLUMN_VISITOR_RECORDS_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_MEMBER_ID + " TEXT,"
                    + COLUMN_MEMBER_NAME + " TEXT,"
                    + COLUMN_MOBILE_NO + " TEXT,"
                    + COLUMN_COMING_FROM + " TEXT,"
                    + COLUMN_PURPOSE + " TEXT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_COUNT + " TEXT,"
                    + COLUMN_DATE_TIME_IN + " DATETIME,"
                    + COLUMN_DATE_TIME_OUT + " DATETIME,"
                    + COLUMN_IS_SYNC + " Text,"
                    + COLUMN_STATUS + " Text,"
                    + COLUMN_REASON + " Text"
                    + ")";

    public VisitorOfflineDb() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVisitor_record_id() {
        return visitor_record_id;
    }

    public void setVisitor_record_id(String visitor_record_id) {
        this.visitor_record_id = visitor_record_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDate_time_in() {
        return date_time_in;
    }

    public void setDate_time_in(String date_time_in) {
        this.date_time_in = date_time_in;
    }

    public String getDate_time_out() {
        return date_time_out;
    }

    public void setDate_time_out(String date_time_out) {
        this.date_time_out = date_time_out;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.visitor_record_id);
        dest.writeString(this.name);
        dest.writeString(this.member);
        dest.writeString(this.member_name);
        dest.writeString(this.mobileNo);
        dest.writeString(this.comingFrom);
        dest.writeString(this.purpose);
        dest.writeString(this.image);
        dest.writeString(this.count);
        dest.writeString(this.date_time_in);
        dest.writeString(this.date_time_out);
        dest.writeString(this.is_sync);
        dest.writeString(this.status);
        dest.writeString(this.reason);
    }

    protected VisitorOfflineDb(Parcel in) {
        this.id = in.readInt();
        this.visitor_record_id = in.readString();
        this.name = in.readString();
        this.member = in.readString();
        this.member_name = in.readString();
        this.mobileNo = in.readString();
        this.comingFrom = in.readString();
        this.purpose = in.readString();
        this.image = in.readString();
        this.count = in.readString();
        this.date_time_in = in.readString();
        this.date_time_out = in.readString();
        this.is_sync = in.readString();
        this.status = in.readString();
        this.reason = in.readString();
    }

    public static final Creator<VisitorOfflineDb> CREATOR = new Creator<VisitorOfflineDb>() {
        @Override
        public VisitorOfflineDb createFromParcel(Parcel source) {
            return new VisitorOfflineDb(source);
        }

        @Override
        public VisitorOfflineDb[] newArray(int size) {
            return new VisitorOfflineDb[size];
        }
    };
}