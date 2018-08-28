package com.proclivis.smartknock.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VisitorVerifyDone {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<VisitorVerifyDoneVo> data = null;

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

    public ArrayList<VisitorVerifyDoneVo> getData() {
        return data;
    }

    public void setData(ArrayList<VisitorVerifyDoneVo> data) {
        this.data = data;
    }

}
