package com.proclivis.smartknock.Model;

import java.util.ArrayList;

/**
 * Created by archirayan on 12/3/18.
 */

public class Login {

    private String status;
    private String message;
    private ArrayList<LoginVo> data;

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

    public ArrayList<LoginVo> getData() {
        return data;
    }

    public void setData(ArrayList<LoginVo> data) {
        this.data = data;
    }

}
