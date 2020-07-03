package com.adoisstudio.helper;


public class Request {

    private String params = "";


    public void addString(String name, String val) {
        params += name + "=" + val + "&";
    }

    public void addInt(String name, int val) {
        params += name + "=" + val + "&";
    }

    public String getParams() {
        if (params.endsWith("&") && params.length() > 0)
            params = params.substring(0, params.length() - 1);
        return "?" + params;
    }

}
