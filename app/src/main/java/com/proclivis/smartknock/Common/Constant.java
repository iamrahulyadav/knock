package com.proclivis.smartknock.Common;

import android.net.Uri;

import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.DataBase.MemberDB;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.Model.GetMemberVisitorVo;
import com.proclivis.smartknock.Model.MemberDetail;

import java.util.ArrayList;


public class Constant {

    //public static String URL = "http://knockknock.proclivistech.com/kk/APIs_1";
    //public static String URL = "http://knockknock.proclivistech.com/kk/APIs";
    //public static String URL = "http://smartknock.proclivistech.com/kk/APIs";
    public static String URL = "http://web-medico.com/web1/proclivis/APIs";

    public static final String SHARE_PREF = "_preferences";

    //Device Info
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_NAME = "DEVICE_NAME";

    //Login Customer SharePar
    public static final String USER_ID = "USER_ID";
    public static final String USER_INSTALLATION_ID = "USER_INSTALLATION_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_MOBILE_NO = "USER_MOBILE_NO";
    public static final String USER_ADDRESS1 = "USER_ADDRESS1";
    public static final String USER_ADDRESS2 = "USER_ADDRESS2";
    public static final String USER_ADDRESS3 = "USER_ADDRESS3";
    public static final String USER_CITY = "USER_CITY";
    public static final String USER_STATE = "USER_STATE";
    public static final String USER_PINCODE = "USER_PINCODE";
    public static final String USER_ENABLED = "USER_ENABLED";
    public static final String USER_CREATION_DATE= "USER_CREATION_DAT";
    public static final String USER_LAST_UODATE_DATE = "USER_LAST_UODATE_DATE";
    public static final String USER_SCROLLING_MESSEGE = "USER_SCROLLING_MESSEGE";
    public static final String USER_PRO = "USER_PRO";

    //login member sharepar

    public static final String MEMBER_ID = "MEMBER_ID ";
    public static final String MEMBER_CUSTOMER_ID = "MEMBER_CUSTOMER_ID ";
    public static final String MEMBER_INSTALLATION_ID= "MEMBER_INSTALLATION_ID";
    public static final String MEMBER_NAME = "MEMBER_NAME ";
    public static final String MEMBER_MOBILE_NO = "MEMBER_MOBILE_NO ";
    public static final String MEMBER_MOBILE_UNIQUE_ID = "MEMBER_MOBILE_UNIQUE_ID ";
    public static final String MEMBER_EMAIL = "MEMBER_EMAIL ";
    public static final String MEMBER_EMAIL_FLAG = "MEMBER_EMAIL_FLAG ";
    public static final String MEMBER_FLAT_NO = "MEMBER_FLAT_NO ";
    public static final String MEMBER_ENABLED = "MEMBER_ENABLED ";
    public static final String MEMBER_CREATION_DATE = "MEMBER_CREATION_DATE ";
    public static final String MEMBER_LAST_UODATE_DATE = "MEMBER_LAST_UODATE_DATE ";
    public static final String MEMBER_PROFILE_IMAGE = "MEMBER_PROFILE_IMAGE ";
    public static final String MEMBER_HELP = "MEMBER_HELP";

    public static final String FCM_ID = "FCM_ID ";

    public static final String IMAGE_PROFILE_1 = "IMAGE_PROFILE_1";
    public static final String IMAGE_PROFILE_2 = "IMAGE_PROFILE_2";
    public static final String IMAGE_UPLOADED_1 = "IMAGE_UPLOADED_1";
    public static final String IMAGE_UPLOADED_2 = "IMAGE_UPLOADED_2";

    public static final String IS_MEMBER_FIRST_TIME = "true";
    public static final String MEMBER_API_CALL_DATE = "00-00-0000";

    public static final String IS_VISITOR_FIRST_TIME = "yes";
    public static final String VISITOR_API_CALL_DATE = "00-00-0000";

    public static final String IS_EXPRESS_FIRST_TIME = "0";
    public static final String EXPRESS_API_CALL_DATE = "00-00-0000";

    public static final String IS_MEMBER_DATABASE_FIRST_TIME = "IS_MEMBER_DATABASE_FIRST_TIME";

    public static String MOBILE_NO = "";
    public static String NAME = "";
    public static String COMING_FROM = "";
    public static String PURPOSE = "";
    public static int COUNT = 0;

    public static int VISITOR_UPLOADED = 0;

    public static int help_count = 0;

    public static int Notifications=0;

    public static ArrayList<MemberDB> selectedMember = new ArrayList<>();
    public static ArrayList<VisitorOfflineDb> getVisitorVo = new ArrayList<>();
    public static ExpressVisitorOfflineDb expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();
    public static ArrayList<GetMemberVisitorVo> getMemberVisitorVo = new ArrayList<>();
    public static ArrayList<VisitorOfflineDb> waiting = new ArrayList<>();
    public static ArrayList<MemberDetail> memberDetails = new ArrayList<>();
    public static Uri USER_IMAGE_PATH=null;

    public static String VISITOR_POS=null;
    public static int NOTIFICATION_COUNT = 0;
    public static int MemberNameLength = 0;
    public static int UserNotifyId = 0;
    public static  boolean faceDetect = false;

    public static final String ALWAYS_ASK = "Always Ask";
    public static final String ALWAYS_ACCEPT = "Always Accept";
    public static final String ALWAYS_REJECT = "Always Reject";

    public static final String REJECT = "Reject";
    public static final String ACCEPT = "Accept";
    public static final String GUARD = "Guard";

    public static final String DRAWER_COUNT = "DRAWER_COUNT";

    public static final String REASON1 = "I am currently not at home";
    public static final String REASON2 = "I am busy. Please come later";
    public static final String REASON3 = "Please deliver parcel to security";
    public static final String REASON4 = "Type your own message";

    public static String DRAWER_m_name = "m_name";
    public static String DRAWER_mobile_no = "mobile_no";
    public static String DRAWER_coming_from = "coming_from";
    public static String DRAWER_purpose = "purpose";
    public static String DRAWER_visitor_count = "visitor_count";
    public static String DRAWER_date_time_in = "date_time_in";
    public static String DRAWER_reason = "reason";
    public static String DRAWER_status = "status";

}