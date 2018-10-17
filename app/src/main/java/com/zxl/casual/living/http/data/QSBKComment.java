package com.zxl.casual.living.http.data;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKComment {
    public String user_name = "";
    public String comment_content = "";
    public String user_head_img = "";
    public String user_id = "";
    public int user_sex = 0;
    public int user_age = 0;
    public int comment_report = 0;

    @Override
    public String toString() {
        return "QSBKComment{" +
                "user_name='" + user_name + '\'' +
                ", comment_content='" + comment_content + '\'' +
                ", user_head_img='" + user_head_img + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_sex=" + user_sex +
                ", user_age=" + user_age +
                ", comment_report=" + comment_report +
                '}';
    }
}
