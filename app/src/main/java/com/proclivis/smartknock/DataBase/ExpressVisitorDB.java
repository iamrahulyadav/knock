package com.proclivis.smartknock.DataBase;

public class ExpressVisitorDB {

    public static final String TABLE_NAME = "AllExpressVisitorDB";

    public static final String COLUMN_MOBILE_NO = "mobileNo";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FROM = "coming_from";
    public static final String COLUMN_PURPOSE = "purpose";
    public static final String COLUMN_MEMBER_NAME = "member_name";

    private String mobileNo;
    private String name;
    private String coming_from;
    private String purpose;
    private String member_name;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_MOBILE_NO + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_FROM + " TEXT,"
                    + COLUMN_PURPOSE + " TEXT,"
                    + COLUMN_MEMBER_NAME + " TEXT"
                    + ")";

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComing_from() {
        return coming_from;
    }

    public void setComing_from(String coming_from) {
        this.coming_from = coming_from;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
}