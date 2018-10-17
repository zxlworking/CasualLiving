package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/21.
 */

public class UserInfoResponseBean extends ResponseBaseBean {
    public static final String USER_SEX_MAN = "0";
    public static final String USER_SEX_FEMALE = "1";

    public static final String USER_OPERATOR_CREATE = "0";
    public static final String USER_OPERATOR_LOGIN = "1";
    public static final String USER_OPERATOR_LOGOUT = "2";

    public static final String USER_STATE_NONE = "-1";
    public static final String USER_STATE_INIT = "0";
    public static final String USER_STATE_LOGIN = "1";
    public static final String USER_STATE_LOGOUT = "2";

    public String user_id = "";
    public String user_name = "";
    public String pass_word = "";
    public String phone_number = "";
    public String nick_name = "";
    public String sex = "";
    public String birthday = "";
}
