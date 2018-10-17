package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/9/14.
 */

public class TaoBaoAnchorListResponseBean extends ResponseBaseBean {
    /**
     "taobao_anchor_list":Array[20],
     "code":0,
     "desc":"success"
     */
    public int current_page = 0;
    public List<TaoBaoAnchor> taobao_anchor_list = new ArrayList<>();

    @Override
    public String toString() {
        return "CityInfoListResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", taobao_anchor_list=" + taobao_anchor_list +
                '}';
    }
}
