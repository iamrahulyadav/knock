package com.proclivis.smartknock.Model;

import java.util.ArrayList;

/**
 * Created by archirayan on 13/3/18.
 */

public class ToMeet {
    private String status;
    private String message;
    private ArrayList<ToMeetVo> data;

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

    public ArrayList<ToMeetVo> getData() {
        return data;
    }

    public void setData(ArrayList<ToMeetVo> data) {
        this.data = data;
    }
}
