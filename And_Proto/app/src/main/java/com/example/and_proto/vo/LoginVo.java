package com.example.and_proto.vo;

public class LoginVo {
    String id;
    String pw;

    public LoginVo(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }


}
