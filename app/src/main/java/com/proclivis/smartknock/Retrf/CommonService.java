package com.proclivis.smartknock.Retrf;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public interface CommonService {

    @FormUrlEncoded
    @POST("/get_members_list_by_customer_id1.php")
    void get_members_list_by_customer_id1(
            @Field("customer_id") String customer_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/login_customer_or_member1.php")
    void login_customer_or_member1(
            @Field("mobile_no") String mobile_no,
            @Field("device_id") String device_id,
            @Field("device_name") String device_name,
            @Field("fcm_id") String fcm_id,
            Callback<Response> user);

    @Multipart
    @POST("/add_visitor_while_IN_without_OUT_validation.php")
    void add_visitor_while_IN_without_OUT_validation(
            @Part("device_id") TypedString device_id,
            @Part("member_id") TypedString member_id,
            @Part("name") TypedString name,
            @Part("mobile_no") TypedString mobile_no,
            @Part("coming_from") TypedString coming_from,
            @Part("purpose") TypedString purpose,
            @Part("vistor_count") TypedString vistor_count,
            @Part("visitor_image") TypedFile visitor_image,
            @Part("customer_mobile_no") TypedString customer_mobile_no,
            @Part("status") TypedString status,
            Callback<Response> user);
    @Multipart
    @POST("/add_visitor_while_IN_without_OUT_validation_date.php")
    void add_visitor_while_IN_without_OUT_validation_date(
            @Part("device_id") TypedString device_id,
            @Part("member_id") TypedString member_id,
            @Part("name") TypedString name,
            @Part("mobile_no") TypedString mobile_no,
            @Part("coming_from") TypedString coming_from,
            @Part("purpose") TypedString purpose,
            @Part("vistor_count") TypedString vistor_count,
            @Part("date") TypedString date,
            @Part("customer_mobile_no") TypedString customer_mobile_no,
            @Part("visitor_image") TypedFile visitor_image,
            Callback<Response> user);

    @Multipart
    @POST("/update_member_profile_image1.php")
    void update_member_profile_image1(
            @Part("mobile_no") TypedString mobile_no,
            @Part("member_profile_image") TypedFile member_profile_image,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/update_visitor_while_OUT_by_id.php")
    void update_visitor_while_OUT_by_id(
            @Field("mobile_no") String mobile_no,
            @Field("installation_id") String installation_id,
            @Field("customer_mobile_no") String customer_mobile_no,
            @Field("out_from") String out_from,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/get_visitor_detail_by_mobileno.php")
    void get_visitor_detail_by_mobileno(
            @Field("mobile_no") String mobile_no,
            @Field("installation_id") String installation_id,
            @Field("customer_mobile_no") String customer_mobile_no,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/get_visitors_customer_wise1.php")
    void get_visitors_customer_wise1(
            @Field("customer_id") String customer_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/get_visitors_records_by_member_number1.php")
    void get_visitors_records_by_member_number1(
            @Field("mobile_no") String mobile_no,
            @Field("flag") String flag,
            Callback<Response> user);



    @FormUrlEncoded
    @POST("/get_customer_or_member_detail.php?")
    void get_member_name_email(
            @Field("mobile_no") String mobile_no,
            @Field("device_id") String device_id,
            Callback<Response> user);



    @FormUrlEncoded
    @POST("/update_member_name_email1.php")
    void update_member_name_email1(
            @Field("mobile_no") String mobile_no,
            @Field("member_name") String member_name,
            @Field("member_email") String member_email,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/set_status_member.php")
    void set_status_member(
            @Field("member_id") String member_id,
            @Field("status") String status,
            @Field("reason") String reason,
            @Field("installation_id") String installation_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/update_fcm_id_and_apple_id1.php")
    void update_fcm_id_and_apple_id1(
            @Field("mobile_no") String mobile_no,
            @Field("fcm_id") String fcm_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/logout.php")
    void logout(
            @Field("mobile_no") String mobile_no,
            @Field("fcm_id") String fcm_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/update_email_flag_of_member1.php")
    void update_email_flag_of_member1(
            @Field("mobile_no") String mobile_no,
            @Field("email_flag") String email_flag,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/update_customer_name_message1.php")
    void update_customer_name_message1(
            @Field("customer_id") String customer_id,
            @Field("customer_name") String customer_name,
            @Field("customer_message") String customer_message,
            Callback<Response> user);

    @Multipart
    @POST("/add_daily_visitor_while_IN_without_OUT_validation.php")
    void add_daily_visitor_while_IN_without_OUT_validation(
            @Part("mobile_no") TypedString mobile_no,
            @Part("installation_id_passing") TypedString installation_id_passing,
            @Part("customer_mobile_no") TypedString customer_mobile_no,
            @Part("date") TypedString date,
            @Part("visitor_image") TypedFile visitor_image,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/get_daily_visitor_customer_ins_id.php")
    void get_daily_visitor_customer_ins_id(
            @Field("customer_installation_id") String customer_installation_id,
            Callback<Response> user);

    @Multipart
    @POST("/add_daily_visitor_while_IN_without_OUT_validation_date.php")
    void add_daily_visitor_while_IN_without_OUT_validation_date(
            @Part("mobile_no") TypedString mobile_no,
            @Part("installation_id_passing") TypedString installation_id_passing,
            @Part("customer_mobile_no") TypedString customer_mobile_no,
            @Part("date") TypedString date,
            @Part("visitor_image") TypedFile visitor_image,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/update_visitor_while_OUT_by_id_date.php")
    void update_visitor_while_OUT_by_id_date(
            @Field("mobile_no") String mobile_no,
            @Field("installation_id") String installation_id,
            @Field("customer_mobile_no") String customer_mobile_no,
            @Field("date") String date,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/get_visitors_record_by_Installation_id.php")
    void get_visitors_record_by_Installation_id(
            @Field("installation_id") String installation_id,
            Callback<Response> user);

    @FormUrlEncoded
    @POST("/member_accept_reject_visitor.php")
    void member_accept_reject_visitor(
            @Field("member_id") String member_id,
            @Field("visitor_record_id") String visitor_record_id,
            @Field("type") String type,
            @Field("reason") String reason,
            Callback<Response> user);
}