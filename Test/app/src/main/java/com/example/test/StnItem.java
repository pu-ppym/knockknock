package com.example.test;

public class StnItem {

    private String citycode;
    private String nodeid;
    private String nodeno;
    private String nodenm;

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodeno() {
        return nodeno;
    }

    public String getNodenm() {
        return nodenm;
    }

    public StnItem(String citycode, String nodeid, String nodenm, String nodeno) {
        this.citycode = citycode;
        this.nodeid = nodeid;
        this.nodenm = nodenm;
        this.nodeno = nodeno;
    }

}
