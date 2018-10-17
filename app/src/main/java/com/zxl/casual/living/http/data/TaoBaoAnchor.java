package com.zxl.casual.living.http.data;

/**
 * Created by zxl on 2018/9/20.
 */

public class TaoBaoAnchor {
    /**
     {
     "anchor_img":"https://img.alicdn.com/imgextra/i4/369453264/TB2o_juaTlYBeNjSszcXXbwhFXa_!!369453264-0-beehive-scenes.jpg_310x310.jpg",
     "anchor_name":"淘女郎demi",
     "anchor-vflag":"https://img.alicdn.com/tfs/TB1y3.KbbGYBuNjy0FoXXciBFXa-48-48.png",
     "fans-count":"139.51万"
     },
     */

    public String anchor_name = "";
    public String anchor_img = "";
    public String anchor_vflag = "";
    public String fans_count = "";

    @Override
    public String toString() {
        return "TaoBaoAnchor{" +
                "anchor_name='" + anchor_name + '\'' +
                ", anchor_img='" + anchor_img + '\'' +
                ", anchor_vflag='" + anchor_vflag + '\'' +
                ", fans_count='" + fans_count + '\'' +
                '}';
    }
}
