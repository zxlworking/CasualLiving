package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKElementList extends ResponseBaseBean {

    /**
     "current_page":0,
     "code":0,
     "result":Array[1],
     "total_page":6,
     "desc":"success"
     */
    public int current_page = 0;
    public int total_page = 0;
    public List<QSBKElement> result = new ArrayList<>();

    @Override
    public String toString() {
        return "QSBKElementList{" +
                "current_page =" + current_page +
                ", total_page =" + total_page +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}
