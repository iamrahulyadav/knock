package com.proclivis.smartknock.DataBase;

/**
 * Created by archirayan on 20/3/18.
 */

public class VisitorDB {

    public static final String TABLE_NAME = "visitor_records";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MOBILE_NO = "mobileNo";
    public static final String COLUMN_COMING_FROM = "comingFrom";
    public static final String COLUMN_PURPOSE = "purpose";

    private String id;
    private String name;
    private String mobileNo;
    private String comingFrom;
    private String purpose;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_MOBILE_NO + " TEXT,"
                    + COLUMN_COMING_FROM + " TEXT,"
                    + COLUMN_PURPOSE + " TEXT"
                    + ")";

    VisitorDB() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
