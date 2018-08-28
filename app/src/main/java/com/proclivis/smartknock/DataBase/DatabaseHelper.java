package com.proclivis.smartknock.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.proclivis.smartknock.Model.ExpressVisitorVo;
import com.proclivis.smartknock.Model.GetMemberVisitorVo;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.Model.ToMeetVo;
import com.proclivis.smartknock.Model.VisitorConfirmVo;

import java.util.ArrayList;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KNOCK_KNOCK_DB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(MemberDB.CREATE_TABLE);
        db.execSQL(VisitorDB.CREATE_TABLE);
        db.execSQL(VisitorOfflineDb.CREATE_TABLE);
        db.execSQL(ExpressVisitorDB.CREATE_TABLE);
        db.execSQL(ExpressVisitorOfflineDb.CREATE_TABLE);
        db.execSQL(MemberDetail.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MemberDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VisitorDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VisitorOfflineDb.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpressVisitorDB.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExpressVisitorOfflineDb.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MemberDetail.TABLE_NAME);
        onCreate(db);
    }

    public void insertMemberDB(ArrayList<ToMeetVo> toMeetVos) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MemberDB.TABLE_NAME);
        db.execSQL(MemberDB.CREATE_TABLE);

        ContentValues values = new ContentValues();

        for (int i = 0; i < toMeetVos.size(); i++) {
            values.put(MemberDB.COLUMN_ID, toMeetVos.get(i).getId());
            values.put(MemberDB.COLUMN_NAME, toMeetVos.get(i).getName());
            values.put(MemberDB.COLUMN_FLAT_NO, toMeetVos.get(i).getFlatNo());

            db.insert(MemberDB.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void insertVisitorsDB(ArrayList<VisitorConfirmVo> visitorConfirmVos) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + VisitorDB.TABLE_NAME);
        db.execSQL(VisitorDB.CREATE_TABLE);

        ContentValues values = new ContentValues();

        for (int i = 0; i < visitorConfirmVos.size(); i++) {
            values.put(VisitorDB.COLUMN_ID, visitorConfirmVos.get(i).getId());
            values.put(VisitorDB.COLUMN_NAME, visitorConfirmVos.get(i).getName());
            values.put(VisitorDB.COLUMN_MOBILE_NO, visitorConfirmVos.get(i).getMobileNo());
            values.put(VisitorDB.COLUMN_COMING_FROM, visitorConfirmVos.get(i).getComingFrom());
            values.put(VisitorDB.COLUMN_PURPOSE, visitorConfirmVos.get(i).getPurpose());

            db.insert(VisitorDB.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void insertVisitor(String name, String mobile_no, String coming_from, String purpose) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(VisitorDB.COLUMN_ID, "");
        values.put(VisitorDB.COLUMN_NAME, name);
        values.put(VisitorDB.COLUMN_MOBILE_NO, mobile_no);
        values.put(VisitorDB.COLUMN_COMING_FROM, coming_from);
        values.put(VisitorDB.COLUMN_PURPOSE, purpose);

        // insert row
        db.insert(VisitorDB.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id

    }

    public void updateVisitor(String name, String mobile_no, String coming_from, String purpose) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(VisitorDB.COLUMN_ID, "");
        values.put(VisitorDB.COLUMN_NAME, name);
        values.put(VisitorDB.COLUMN_MOBILE_NO, mobile_no);
        values.put(VisitorDB.COLUMN_COMING_FROM, coming_from);
        values.put(VisitorDB.COLUMN_PURPOSE, purpose);

        db.update(VisitorDB.TABLE_NAME, values, VisitorDB.COLUMN_MOBILE_NO + " = ?", new String[]{String.valueOf(mobile_no)});

        db.close();

    }

    public ArrayList<MemberDB> getAllMember() {
        ArrayList<MemberDB> memberDBs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + MemberDB.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MemberDB memberDB = new MemberDB();
                memberDB.setId(cursor.getString(cursor.getColumnIndex(MemberDB.COLUMN_ID)));
                memberDB.setName(cursor.getString(cursor.getColumnIndex(MemberDB.COLUMN_NAME)));
                memberDB.setFlat_no(cursor.getString(cursor.getColumnIndex(MemberDB.COLUMN_FLAT_NO)));

                memberDBs.add(memberDB);
            } while (cursor.moveToNext());
        }
        db.close();
        return memberDBs;
    }

    public ArrayList<VisitorDB> getAllVisitor() {
        ArrayList<VisitorDB> visitorDBs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + VisitorDB.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                VisitorDB visitorDB = new VisitorDB();
                visitorDB.setId(cursor.getString(cursor.getColumnIndex(VisitorDB.COLUMN_ID)));
                visitorDB.setName(cursor.getString(cursor.getColumnIndex(VisitorDB.COLUMN_NAME)));
                visitorDB.setMobileNo(cursor.getString(cursor.getColumnIndex(VisitorDB.COLUMN_MOBILE_NO)));
                visitorDB.setComingFrom(cursor.getString(cursor.getColumnIndex(VisitorDB.COLUMN_COMING_FROM)));
                visitorDB.setPurpose(cursor.getString(cursor.getColumnIndex(VisitorDB.COLUMN_PURPOSE)));

                visitorDBs.add(visitorDB);
            } while (cursor.moveToNext());
        }
        db.close();
        return visitorDBs;
    }

    public void insertVisitorOffline(VisitorOfflineDb visitorOfflineDb) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(VisitorOfflineDb.COLUMN_VISITOR_RECORDS_ID, visitorOfflineDb.getVisitor_record_id());
            values.put(VisitorOfflineDb.COLUMN_NAME, visitorOfflineDb.getName());
            values.put(VisitorOfflineDb.COLUMN_MEMBER_ID, visitorOfflineDb.getMember());
            values.put(VisitorOfflineDb.COLUMN_MEMBER_NAME, visitorOfflineDb.getMember_name());
            values.put(VisitorOfflineDb.COLUMN_MOBILE_NO, visitorOfflineDb.getMobileNo());
            values.put(VisitorOfflineDb.COLUMN_COMING_FROM, visitorOfflineDb.getComingFrom());
            values.put(VisitorOfflineDb.COLUMN_PURPOSE, visitorOfflineDb.getPurpose());
            values.put(VisitorOfflineDb.COLUMN_IMAGE, visitorOfflineDb.getImage());
            values.put(VisitorOfflineDb.COLUMN_COUNT, visitorOfflineDb.getCount());
            values.put(VisitorOfflineDb.COLUMN_DATE_TIME_IN, visitorOfflineDb.getDate_time_in());
            values.put(VisitorOfflineDb.COLUMN_DATE_TIME_OUT, visitorOfflineDb.getDate_time_out());
            values.put(VisitorOfflineDb.COLUMN_IS_SYNC, visitorOfflineDb.getIs_sync());
            values.put(VisitorOfflineDb.COLUMN_STATUS, visitorOfflineDb.getStatus());
            values.put(VisitorOfflineDb.COLUMN_REASON, visitorOfflineDb.getReason());

            db.insert(VisitorOfflineDb.TABLE_NAME, null, values);

            db.close();
        } catch (Exception e) {
            Log.e("insert", e.toString());
        }
    }

    public ArrayList<VisitorOfflineDb> getAllOfflineVisitorByMobile(String mobile) {
        ArrayList<VisitorOfflineDb> visitorOfflineDbs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + VisitorOfflineDb.TABLE_NAME + " WHERE mobileNo = '" + mobile + "' ORDER BY date_time_in DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();
                visitorOfflineDb.setId(cursor.getInt(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_ID)));
                visitorOfflineDb.setVisitor_record_id(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_VISITOR_RECORDS_ID)));
                visitorOfflineDb.setName(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_NAME)));
                visitorOfflineDb.setMember(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MEMBER_ID)));
                visitorOfflineDb.setMember_name(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MEMBER_NAME)));
                visitorOfflineDb.setMobileNo(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MOBILE_NO)));
                visitorOfflineDb.setComingFrom(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_COMING_FROM)));
                visitorOfflineDb.setPurpose(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_PURPOSE)));
                visitorOfflineDb.setImage(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_IMAGE)));
                visitorOfflineDb.setCount(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_COUNT)));
                visitorOfflineDb.setDate_time_in(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_DATE_TIME_IN)));
                visitorOfflineDb.setDate_time_out(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_DATE_TIME_OUT)));
                visitorOfflineDb.setIs_sync(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_IS_SYNC)));
                visitorOfflineDb.setStatus(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_STATUS)));
                visitorOfflineDb.setReason(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_REASON)));

                visitorOfflineDbs.add(visitorOfflineDb);

            } while (cursor.moveToNext());
        }
        db.close();
        return visitorOfflineDbs;
    }

    public void updateVisitorOffline(int id, String mobile, String date_time_out) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(VisitorOfflineDb.COLUMN_DATE_TIME_OUT, date_time_out);

        db.update(VisitorOfflineDb.TABLE_NAME, values, VisitorOfflineDb.COLUMN_ID + " = ? AND " + VisitorOfflineDb.COLUMN_MOBILE_NO + " = ?", new String[]{String.valueOf(id), String.valueOf(mobile)});

        db.close();

    }

    public void updateVisitorOfflineStatus(int id, String status, String reason) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(VisitorOfflineDb.COLUMN_STATUS, status);
        values.put(VisitorOfflineDb.COLUMN_REASON, reason);

        db.update(VisitorOfflineDb.TABLE_NAME, values, VisitorOfflineDb.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

    }

    public void updateIsSyncVisitorOffline(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(VisitorOfflineDb.COLUMN_IS_SYNC, "true");

        db.update(VisitorOfflineDb.TABLE_NAME, values, VisitorOfflineDb.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

    }

    public void updateStatusReasonVisitorOffline(int id , String status, String reason) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(VisitorOfflineDb.COLUMN_STATUS, status);
        values.put(VisitorOfflineDb.COLUMN_REASON, reason);

        db.update(VisitorOfflineDb.TABLE_NAME, values, VisitorOfflineDb.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

    }
    public ArrayList<VisitorOfflineDb> getAllOfflineVisitor() {
        ArrayList<VisitorOfflineDb> visitorOfflineDbs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + VisitorOfflineDb.TABLE_NAME + " ORDER BY date_time_in";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();
                visitorOfflineDb.setId(cursor.getInt(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_ID)));
                visitorOfflineDb.setVisitor_record_id(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_VISITOR_RECORDS_ID)));
                visitorOfflineDb.setName(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_NAME)));
                visitorOfflineDb.setMember(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MEMBER_ID)));
                visitorOfflineDb.setMember_name(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MEMBER_NAME)));
                visitorOfflineDb.setMobileNo(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_MOBILE_NO)));
                visitorOfflineDb.setComingFrom(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_COMING_FROM)));
                visitorOfflineDb.setPurpose(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_PURPOSE)));
                visitorOfflineDb.setImage(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_IMAGE)));
                visitorOfflineDb.setCount(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_COUNT)));
                visitorOfflineDb.setDate_time_in(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_DATE_TIME_IN)));
                visitorOfflineDb.setDate_time_out(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_DATE_TIME_OUT)));
                visitorOfflineDb.setIs_sync(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_IS_SYNC)));
                visitorOfflineDb.setStatus(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_STATUS)));
                visitorOfflineDb.setReason(cursor.getString(cursor.getColumnIndex(VisitorOfflineDb.COLUMN_REASON)));

                visitorOfflineDbs.add(visitorOfflineDb);
            } while (cursor.moveToNext());
        }
        db.close();
        return visitorOfflineDbs;
    }

    public void deleteOfflineVisitor(VisitorOfflineDb visitorOfflineDbs) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(VisitorOfflineDb.TABLE_NAME, VisitorOfflineDb.COLUMN_ID + "= ?", new String[]{String.valueOf(visitorOfflineDbs.getId())});

        db.close();
    }

    public void deleteOfflineVisitorByMobile(VisitorOfflineDb visitorOfflineDbs) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(VisitorOfflineDb.TABLE_NAME, VisitorOfflineDb.COLUMN_MOBILE_NO + "= ?", new String[]{String.valueOf(visitorOfflineDbs.getMobileNo())});

        db.close();
    }

    public void deleteOfflineVisitorById(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(VisitorOfflineDb.TABLE_NAME, VisitorOfflineDb.COLUMN_ID + "= ?", new String[]{String.valueOf(id)});

        db.close();
    }


    public void insertAllExpressVisitorDB(ArrayList<ExpressVisitorVo> expressVisitorDBS) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + ExpressVisitorDB.TABLE_NAME);
        db.execSQL(ExpressVisitorDB.CREATE_TABLE);

        ContentValues values = new ContentValues();

        for (int i = 0; i < expressVisitorDBS.size(); i++) {
            values.put(ExpressVisitorDB.COLUMN_MOBILE_NO, expressVisitorDBS.get(i).getMobileNo());
            values.put(ExpressVisitorDB.COLUMN_NAME, expressVisitorDBS.get(i).getName());
            values.put(ExpressVisitorDB.COLUMN_FROM, expressVisitorDBS.get(i).getComingFrom());
            values.put(ExpressVisitorDB.COLUMN_PURPOSE, expressVisitorDBS.get(i).getPurpose());
            values.put(ExpressVisitorDB.COLUMN_MEMBER_NAME, expressVisitorDBS.get(i).getMName());

            db.insert(ExpressVisitorDB.TABLE_NAME, null, values);
        }
        db.close();
    }

    public ArrayList<ExpressVisitorDB> getAllExpressVisitorDB() {
        ArrayList<ExpressVisitorDB> memberDBs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ExpressVisitorDB.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ExpressVisitorDB memberDB = new ExpressVisitorDB();
                memberDB.setMobileNo(cursor.getString(cursor.getColumnIndex(ExpressVisitorDB.COLUMN_MOBILE_NO)));
                memberDB.setName(cursor.getString(cursor.getColumnIndex(ExpressVisitorDB.COLUMN_NAME)));
                memberDB.setComing_from(cursor.getString(cursor.getColumnIndex(ExpressVisitorDB.COLUMN_FROM)));
                memberDB.setPurpose(cursor.getString(cursor.getColumnIndex(ExpressVisitorDB.COLUMN_PURPOSE)));
                memberDB.setMember_name(cursor.getString(cursor.getColumnIndex(ExpressVisitorDB.COLUMN_MEMBER_NAME)));

                memberDBs.add(memberDB);
            } while (cursor.moveToNext());
        }
        db.close();
        return memberDBs;
    }

    public void insertExpressVisitorOffline(ExpressVisitorOfflineDb expressVisitorOfflineDb) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(ExpressVisitorOfflineDb.COLUMN_MOBILE_NO, expressVisitorOfflineDb.getMobileNo());
            values.put(ExpressVisitorOfflineDb.COLUMN_IMAGE, expressVisitorOfflineDb.getImage());
            values.put(ExpressVisitorOfflineDb.COLUMN_NAME, expressVisitorOfflineDb.getName());
            values.put(ExpressVisitorOfflineDb.COLUMN_MEMBER, expressVisitorOfflineDb.getMember());
            values.put(ExpressVisitorOfflineDb.COLUMN_MEMBER_NAME, expressVisitorOfflineDb.getMemberName());
            values.put(ExpressVisitorOfflineDb.COLUMN_COMING_FROM, expressVisitorOfflineDb.getComingFrom());
            values.put(ExpressVisitorOfflineDb.COLUMN_PURPOSE, expressVisitorOfflineDb.getPurpose());
            values.put(ExpressVisitorOfflineDb.COLUMN_COUNT, expressVisitorOfflineDb.getCount());
            values.put(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_IN, expressVisitorOfflineDb.getDate_time_in());
            values.put(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_OUT, expressVisitorOfflineDb.getDate_time_out());
            values.put(ExpressVisitorOfflineDb.COLUMN_IS_SYNC, expressVisitorOfflineDb.getIs_sync());
            values.put(ExpressVisitorOfflineDb.COLUMN_STATUS, expressVisitorOfflineDb.getStatus());
            values.put(ExpressVisitorOfflineDb.COLUMN_REASON, expressVisitorOfflineDb.getReason());

            db.insert(ExpressVisitorOfflineDb.TABLE_NAME, null, values);

            db.close();
        } catch (Exception e) {
            Log.e("insert", e.toString());
        }
    }

    public ArrayList<ExpressVisitorOfflineDb> getAllOfflineExpressVisitorByMobile(String mobile) {
        ArrayList<ExpressVisitorOfflineDb> expressVisitorOfflineDbs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ExpressVisitorOfflineDb.TABLE_NAME + " WHERE mobileNo = '" + mobile + "' ORDER BY date_time_in";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ExpressVisitorOfflineDb expressVisitorOfflineDb = new ExpressVisitorOfflineDb();
                expressVisitorOfflineDb.setId(cursor.getInt(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_ID)));
                expressVisitorOfflineDb.setMobileNo(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MOBILE_NO)));
                expressVisitorOfflineDb.setName(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_NAME)));
                expressVisitorOfflineDb.setComingFrom(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_COMING_FROM)));
                expressVisitorOfflineDb.setPurpose(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_PURPOSE)));
                expressVisitorOfflineDb.setCount(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_COUNT)));
                expressVisitorOfflineDb.setMember(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MEMBER)));
                expressVisitorOfflineDb.setMemberName(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MEMBER_NAME)));
                expressVisitorOfflineDb.setImage(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_IMAGE)));
                expressVisitorOfflineDb.setDate_time_in(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_IN)));
                expressVisitorOfflineDb.setDate_time_out(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_OUT)));
                expressVisitorOfflineDb.setIs_sync(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_IS_SYNC)));
                expressVisitorOfflineDb.setStatus(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_STATUS)));
                expressVisitorOfflineDb.setReason(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_REASON)));

                expressVisitorOfflineDbs.add(expressVisitorOfflineDb);
            } while (cursor.moveToNext());
        }
        db.close();
        return expressVisitorOfflineDbs;
    }

    public void updateExpressVisitorOffline(int id, String mobile, String date_time_out) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_OUT, date_time_out);

        db.update(ExpressVisitorOfflineDb.TABLE_NAME, values, ExpressVisitorOfflineDb.COLUMN_ID + " = ? AND " + ExpressVisitorOfflineDb.COLUMN_MOBILE_NO + " = ?", new String[]{String.valueOf(id), String.valueOf(mobile)});

        db.close();

    }

    public ArrayList<ExpressVisitorOfflineDb> getAllOfflineExpressVisitor() {
        ArrayList<ExpressVisitorOfflineDb> visitorOfflineDbs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + ExpressVisitorOfflineDb.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                ExpressVisitorOfflineDb visitorOfflineDb = new ExpressVisitorOfflineDb();
                visitorOfflineDb.setId(cursor.getInt(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_ID)));
                visitorOfflineDb.setMobileNo(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MOBILE_NO)));
                visitorOfflineDb.setImage(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_IMAGE)));
                visitorOfflineDb.setDate_time_in(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_IN)));
                visitorOfflineDb.setDate_time_out(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_DATE_TIME_OUT)));
                visitorOfflineDb.setName(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_NAME)));
                visitorOfflineDb.setIs_sync(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_IS_SYNC)));
                visitorOfflineDb.setMember(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MEMBER)));
                visitorOfflineDb.setMemberName(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_MEMBER_NAME)));
                visitorOfflineDb.setCount(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_COUNT)));
                visitorOfflineDb.setComingFrom(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_COMING_FROM)));
                visitorOfflineDb.setPurpose(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_PURPOSE)));
                visitorOfflineDb.setStatus(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_STATUS)));
                visitorOfflineDb.setReason(cursor.getString(cursor.getColumnIndex(ExpressVisitorOfflineDb.COLUMN_REASON)));

                visitorOfflineDbs.add(visitorOfflineDb);
            } while (cursor.moveToNext());
        }
        db.close();
        return visitorOfflineDbs;
    }

    public void updateIsSyncExpressVisitorOffline(String mobile) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpressVisitorOfflineDb.COLUMN_IS_SYNC, "true");
        db.update(ExpressVisitorOfflineDb.TABLE_NAME, values, ExpressVisitorOfflineDb.COLUMN_MOBILE_NO + " = ?", new String[]{String.valueOf(mobile)});
        db.close();

    }

    public void deleteOfflineExpressVisitor(ExpressVisitorOfflineDb visitorOfflineDbs) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpressVisitorOfflineDb.TABLE_NAME, ExpressVisitorOfflineDb.COLUMN_MOBILE_NO + "= ?", new String[]{String.valueOf(visitorOfflineDbs.getMobileNo())});
        db.close();
    }

    public void addAllMemberVisitor(ArrayList<GetMemberVisitorVo> getMemberVisitorVos) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + GetMemberVisitorVo.TABLE_NAME);
            db.execSQL(GetMemberVisitorVo.CREATE_TABLE);

            ContentValues values = new ContentValues();

            for (int i=0 ; i<getMemberVisitorVos.size(); i++){

                values.put(GetMemberVisitorVo.COLOM_pro, getMemberVisitorVos.get(i).getPro());
                values.put(GetMemberVisitorVo.COLOM_customerInstallationId, getMemberVisitorVos.get(i).getCustomerInstallationId());
                values.put(GetMemberVisitorVo.COLOM_customerName, getMemberVisitorVos.get(i).getCustomerName());
                values.put(GetMemberVisitorVo.COLOM_customerMessage, getMemberVisitorVos.get(i).getCustomerMessage());
                values.put(GetMemberVisitorVo.COLOM_memberId, getMemberVisitorVos.get(i).getMemberId());
                values.put(GetMemberVisitorVo.COLOM_memberName, getMemberVisitorVos.get(i).getMemberName());
                values.put(GetMemberVisitorVo.COLOM_vistorRecordId, getMemberVisitorVos.get(i).getVistorRecordId());
                values.put(GetMemberVisitorVo.COLOM_visitorName, getMemberVisitorVos.get(i).getVisitorName());
                values.put(GetMemberVisitorVo.COLOM_visitorMobileNo, getMemberVisitorVos.get(i).getVisitorMobileNo());
                values.put(GetMemberVisitorVo.COLOM_visitorComingFrom, getMemberVisitorVos.get(i).getVisitorComingFrom());
                values.put(GetMemberVisitorVo.COLOM_visitorPurpose, getMemberVisitorVos.get(i).getVisitorPurpose());
                values.put(GetMemberVisitorVo.COLOM_visitorImage, getMemberVisitorVos.get(i).getVisitorImage());
                values.put(GetMemberVisitorVo.COLOM_visitorDateTimeIn, getMemberVisitorVos.get(i).getVisitorDateTimeIn());
                values.put(GetMemberVisitorVo.COLOM_reson, getMemberVisitorVos.get(i).getReason());
                values.put(GetMemberVisitorVo.COLOM_status, getMemberVisitorVos.get(i).getStatus());
                values.put(GetMemberVisitorVo.COLOM_visitorType, getMemberVisitorVos.get(i).getVisitorType());
                values.put(GetMemberVisitorVo.COLOM_visitorDateTimeOut, getMemberVisitorVos.get(i).getVisitorDateTimeOut());
                values.put(GetMemberVisitorVo.COLOM_visitorCreationDate, getMemberVisitorVos.get(i).getVisitorCreationDate());
                values.put(GetMemberVisitorVo.COLOM_visitorLastUpdatedDate, getMemberVisitorVos.get(i).getVisitorLastUpdatedDate());
                values.put(GetMemberVisitorVo.COLOM_visitorCount, getMemberVisitorVos.get(i).getVisitorCount());

                db.insert(GetMemberVisitorVo.TABLE_NAME, null, values);

            }


            db.close();
        } catch (Exception e) {
            Log.e("insert", e.toString());
        }
    }

    public void addNewMemberVisitor(ArrayList<GetMemberVisitorVo> getMemberVisitorVos, ArrayList<GetMemberVisitorVo> data) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();


            for (int i=0 ; i<getMemberVisitorVos.size(); i++){

                boolean exist = false;
                for (int j=0; j<data.size(); j++){
                    if (getMemberVisitorVos.get(i).getVistorRecordId().equals(data.get(j).getVistorRecordId())){
                        exist = true;

                        ContentValues value = new ContentValues();
                        value.put(GetMemberVisitorVo.COLOM_visitorDateTimeOut, getMemberVisitorVos.get(i).getVisitorDateTimeOut());
                        db.update(GetMemberVisitorVo.TABLE_NAME, value, GetMemberVisitorVo.COLOM_vistorRecordId + " = ?", new String[]{String.valueOf(getMemberVisitorVos.get(i).getVistorRecordId())});

                        break;
                    }
                }

                if (!exist){
                    ContentValues values = new ContentValues();
                    values.put(GetMemberVisitorVo.COLOM_pro, getMemberVisitorVos.get(i).getPro());
                    values.put(GetMemberVisitorVo.COLOM_customerInstallationId, getMemberVisitorVos.get(i).getCustomerInstallationId());
                    values.put(GetMemberVisitorVo.COLOM_customerName, getMemberVisitorVos.get(i).getCustomerName());
                    values.put(GetMemberVisitorVo.COLOM_customerMessage, getMemberVisitorVos.get(i).getCustomerMessage());
                    values.put(GetMemberVisitorVo.COLOM_memberId, getMemberVisitorVos.get(i).getMemberId());
                    values.put(GetMemberVisitorVo.COLOM_memberName, getMemberVisitorVos.get(i).getMemberName());
                    values.put(GetMemberVisitorVo.COLOM_vistorRecordId, getMemberVisitorVos.get(i).getVistorRecordId());
                    values.put(GetMemberVisitorVo.COLOM_visitorName, getMemberVisitorVos.get(i).getVisitorName());
                    values.put(GetMemberVisitorVo.COLOM_visitorMobileNo, getMemberVisitorVos.get(i).getVisitorMobileNo());
                    values.put(GetMemberVisitorVo.COLOM_visitorComingFrom, getMemberVisitorVos.get(i).getVisitorComingFrom());
                    values.put(GetMemberVisitorVo.COLOM_visitorPurpose, getMemberVisitorVos.get(i).getVisitorPurpose());
                    values.put(GetMemberVisitorVo.COLOM_visitorImage, getMemberVisitorVos.get(i).getVisitorImage());
                    values.put(GetMemberVisitorVo.COLOM_visitorDateTimeIn, getMemberVisitorVos.get(i).getVisitorDateTimeIn());
                    values.put(GetMemberVisitorVo.COLOM_reson, getMemberVisitorVos.get(i).getReason());
                    values.put(GetMemberVisitorVo.COLOM_status, getMemberVisitorVos.get(i).getStatus());
                    values.put(GetMemberVisitorVo.COLOM_visitorType, getMemberVisitorVos.get(i).getVisitorType());
                    values.put(GetMemberVisitorVo.COLOM_visitorDateTimeOut, getMemberVisitorVos.get(i).getVisitorDateTimeOut());
                    values.put(GetMemberVisitorVo.COLOM_visitorCreationDate, getMemberVisitorVos.get(i).getVisitorCreationDate());
                    values.put(GetMemberVisitorVo.COLOM_visitorLastUpdatedDate, getMemberVisitorVos.get(i).getVisitorLastUpdatedDate());
                    values.put(GetMemberVisitorVo.COLOM_visitorCount, getMemberVisitorVos.get(i).getVisitorCount());

                    db.insert(GetMemberVisitorVo.TABLE_NAME, null, values);
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("insert", e.toString());
        }
    }

    public ArrayList<GetMemberVisitorVo> getMemberVisitor() {
        ArrayList<GetMemberVisitorVo> getMemberVisitorVos = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + GetMemberVisitorVo.TABLE_NAME + " ORDER BY visitorDateTimeIn DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                GetMemberVisitorVo getMemberVisitorVo = new GetMemberVisitorVo();

                getMemberVisitorVo.setPro(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_pro)));
                getMemberVisitorVo.setCustomerInstallationId(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_customerInstallationId)));
                getMemberVisitorVo.setCustomerName(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_customerName)));
                getMemberVisitorVo.setCustomerMessage(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_customerMessage)));
                getMemberVisitorVo.setMemberId(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_memberId)));
                getMemberVisitorVo.setMemberName(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_memberName)));
                getMemberVisitorVo.setVistorRecordId(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_vistorRecordId)));
                getMemberVisitorVo.setVisitorName(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorName)));
                getMemberVisitorVo.setVisitorMobileNo(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorMobileNo)));
                getMemberVisitorVo.setVisitorComingFrom(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorComingFrom)));
                getMemberVisitorVo.setVisitorPurpose(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorPurpose)));
                getMemberVisitorVo.setVisitorImage(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorImage)));
                getMemberVisitorVo.setVisitorDateTimeIn(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorDateTimeIn)));
                getMemberVisitorVo.setReason(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_reson)));
                getMemberVisitorVo.setStatus(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_status)));
                getMemberVisitorVo.setVisitorType(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorType)));
                getMemberVisitorVo.setVisitorDateTimeOut(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorDateTimeOut)));
                getMemberVisitorVo.setVisitorCreationDate(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorCreationDate)));
                getMemberVisitorVo.setVisitorLastUpdatedDate(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorLastUpdatedDate)));
                getMemberVisitorVo.setVisitorCount(cursor.getString(cursor.getColumnIndex(GetMemberVisitorVo.COLOM_visitorCount)));

                getMemberVisitorVos.add(getMemberVisitorVo);
            } while (cursor.moveToNext());
        }
        db.close();
        return getMemberVisitorVos;
    }

    public void updateMemberVisitor(String id , String reason , String status){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GetMemberVisitorVo.COLOM_status, status);
        values.put(GetMemberVisitorVo.COLOM_reson, reason);
        db.update(GetMemberVisitorVo.TABLE_NAME, values, GetMemberVisitorVo.COLOM_vistorRecordId + " = ?", new String[]{String.valueOf(id)});
        db.close();

    }

    public void addAllMemberSoc(ArrayList<MemberDetail> memberDetails) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + MemberDetail.TABLE_NAME);
            db.execSQL(MemberDetail.CREATE_TABLE);

            ContentValues values = new ContentValues();

            for (int i=0 ; i<memberDetails.size(); i++){

                values.put(MemberDetail.COLUMN_id, memberDetails.get(i).getId());
                values.put(MemberDetail.COLUMN_installationId, memberDetails.get(i).getInstallationId());
                values.put(MemberDetail.COLUMN_name, memberDetails.get(i).getName());
                values.put(MemberDetail.COLUMN_mobileNo, memberDetails.get(i).getMobileNo());
                values.put(MemberDetail.COLUMN_address1, memberDetails.get(i).getAddress1());
                values.put(MemberDetail.COLUMN_address2, memberDetails.get(i).getAddress2());
                values.put(MemberDetail.COLUMN_address3, memberDetails.get(i).getAddress3());
                values.put(MemberDetail.COLUMN_city, memberDetails.get(i).getCity());
                values.put(MemberDetail.COLUMN_state, memberDetails.get(i).getState());
                values.put(MemberDetail.COLUMN_message, memberDetails.get(i).getMessage());
                values.put(MemberDetail.COLUMN_pincode, memberDetails.get(i).getPincode());
                values.put(MemberDetail.COLUMN_enabled, memberDetails.get(i).getEnabled());
                values.put(MemberDetail.COLUMN_pro, memberDetails.get(i).getPro());
                values.put(MemberDetail.COLUMN_creationDate, memberDetails.get(i).getCreationDate());
                values.put(MemberDetail.COLUMN_lastUpdatedDate, memberDetails.get(i).getLastUpdatedDate());
                values.put(MemberDetail.COLUMN_mId, memberDetails.get(i).getMId());
                values.put(MemberDetail.COLUMN_flatNo, memberDetails.get(i).getFlatNo());
                values.put(MemberDetail.COLUMN_status, memberDetails.get(i).getStatus());
                values.put(MemberDetail.COLUMN_reason, memberDetails.get(i).getReason());


                db.insert(MemberDetail.TABLE_NAME, null, values);

            }


            db.close();
        } catch (Exception e) {
            Log.e("insert", e.toString());
        }
    }

    public ArrayList<MemberDetail> getMemberSoc() {
        ArrayList<MemberDetail> memberDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + MemberDetail.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                MemberDetail memberDetail = new MemberDetail();

                memberDetail.setId(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_id)));
                memberDetail.setInstallationId(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_installationId)));
                memberDetail.setName(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_name)));
                memberDetail.setMobileNo(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_mobileNo)));
                memberDetail.setAddress1(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_address1)));
                memberDetail.setAddress2(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_address2)));
                memberDetail.setAddress3(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_address3)));
                memberDetail.setCity(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_city)));
                memberDetail.setState(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_state)));
                memberDetail.setMessage(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_message)));
                memberDetail.setPincode(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_pincode)));
                memberDetail.setEnabled(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_enabled)));
                memberDetail.setPro(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_pro)));
                memberDetail.setCreationDate(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_creationDate)));
                memberDetail.setLastUpdatedDate(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_lastUpdatedDate)));
                memberDetail.setMId(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_mId)));
                memberDetail.setFlatNo(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_flatNo)));
                memberDetail.setStatus(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_status)));
                memberDetail.setReason(cursor.getString(cursor.getColumnIndex(MemberDetail.COLUMN_reason)));

                memberDetails.add(memberDetail);
            } while (cursor.moveToNext());
        }
        db.close();
        return memberDetails;
    }

    //Amit 19.06.18. Added below function to get Offline Record count which is not in sync
    public int getVisitorOfflineCount(){
        int countOfflineRecords = 0;
        String selectVisitorQuery = "SELECT COUNT(1) FROM "+ VisitorOfflineDb.TABLE_NAME + " WHERE " + VisitorOfflineDb.COLUMN_IS_SYNC + " = 'false' OR " + VisitorOfflineDb.COLUMN_DATE_TIME_OUT + "<> ''";
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectVisitorQuery, null);
        if (cursor.moveToFirst())
            countOfflineRecords = cursor.getInt(0);
        return countOfflineRecords;
    }
    //Amit 19.06.18. Added below function to get Offline Record count which is not in sync
    public int getExpressVisitorOfflineCount(){
        int countOfflineRecords = 0;
        String selectExpressVisitorQuery = "SELECT COUNT(1) FROM "+ ExpressVisitorOfflineDb.TABLE_NAME + " WHERE " + ExpressVisitorOfflineDb.COLUMN_IS_SYNC + " = 'false' OR "+ ExpressVisitorOfflineDb.COLUMN_DATE_TIME_OUT + "<> ''";
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectExpressVisitorQuery, null);
        if (cursor.moveToFirst())
            countOfflineRecords = countOfflineRecords + cursor.getInt(0);
        return countOfflineRecords;

    }

}