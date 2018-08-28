package com.proclivis.smartknock.DataBase;

/**
 * Created by ravi on 20/02/18.
 */

public class MemberDB {
    public static final String TABLE_NAME = "member_records";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FLAT_NO = "flat_no";

    private String id;
    private String name;
    private String flat_no;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_FLAT_NO + " TEXT"
                    + ")";

    MemberDB() {
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

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    @Override
    public String toString() {
        return getName() + "|" + getFlat_no();
    }
}
