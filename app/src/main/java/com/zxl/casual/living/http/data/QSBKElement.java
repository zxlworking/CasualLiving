package com.zxl.casual.living.http.data;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKElement {
    public static final int SEX_MAN = 0;
    public static final int SEX_FEMALE = 1;

    public static final int QSBK_COLLECT_OPERATOR_COLLECT = 0;
    public static final int QSBK_COLLECT_OPERATOR_CANCEL = 1;
    public static final int QSBK_COLLECT_OPERATOR_QUERY_ALL = 2;

    public String author_id = "";
    public int author_sex = SEX_MAN;
    public String thumb ="";
    public int author_age = 0;
    public int has_thumb = 0;
    public int comment_number = 0;
    public String author_name = "";
    public String content ="";
    public String author_head_img = "";
    public int is_anonymity = 0;
    public int vote_number = 0;
    public boolean is_collect = false;

    public boolean hasThumb(){
        if(has_thumb == 1){
            return true;
        }
        return false;
    }

    public boolean isAnonymity(){
        if(is_anonymity == 1){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "QSBKElement{" +
                "author_head_id ='" + author_id  + '\'' +
                ", author_sex='" + author_sex + '\'' +
                ", thumb='" + thumb + '\'' +
                ", author_age=" + author_age +
                ", has_thumb=" + has_thumb +
                ", comment_number=" + comment_number +
                ", author_name='" + author_name + '\'' +
                ", content='" + content + '\'' +
                ", author_head_img='" + author_head_img + '\'' +
                ", is_anonymity=" + is_anonymity +
                ", vote_number=" + vote_number +
                ", is_collect=" + is_collect +
                '}';
    }
}
