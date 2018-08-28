package com.proclivis.smartknock.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by archirayan on 27/3/18.
 */

public class GetMemberVisitor {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<GetMemberVisitorVo> data = null;
    @SerializedName("customer_unique_details")
    @Expose
    private ArrayList<MemberDetail> memberDetails = null;


    public GetMemberVisitor() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<GetMemberVisitorVo> getData() {
        return data;
    }

    public void setData(ArrayList<GetMemberVisitorVo> data) {
        this.data = data;
    }

    public ArrayList<MemberDetail> getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(ArrayList<MemberDetail> customerUniqueDetails) {
        this.memberDetails = customerUniqueDetails;
    }

}


