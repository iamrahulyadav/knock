package com.proclivis.smartknock.Model;

import java.util.ArrayList;

/**
 * Created by archirayan on 12/3/18.
 */

public class SignUp {

    private String status;
    private String message;
    private ArrayList<SignUpVo> data;

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

    public ArrayList<SignUpVo> getData() {
        return data;
    }

    public void setData(ArrayList<SignUpVo> data) {
        this.data = data;
    }

}
