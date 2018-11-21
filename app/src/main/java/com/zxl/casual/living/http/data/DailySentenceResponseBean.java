package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/21.
 */

public class DailySentenceResponseBean extends ResponseBaseBean{
    /**
     {
     "sid":"3195",
     "tts":"http://news.iciba.com/admin/tts/2018-11-21-day.mp3",
     "content":"Sometimes people think they lose things and they didn't really lose them. It just gets moved.",
     "note":"人们有时以为失去了什么，其实没有，只是被移开了。",
     "love":"942",
     "translation":"小编的话：读完《第六感》里的这句台词大家有什么感受呢？人生本来就是变幻多姿的，你觉得它是什么，那么它就是什么。不要把生活看成一场交易，有些东西也固然不会失去。",
     "picture":"http://cdn.iciba.com/news/word/20181121.jpg",
     "picture2":"http://cdn.iciba.com/news/word/big_20181121b.jpg",
     "caption":"词霸每日一句",
     "dateline":"2018-11-21",
     "s_pv":"0",
     "sp_pv":"0",
     "tags":[
     {
     "id":null,
     "name":null
     }
     ],
     "fenxiang_img":"http://cdn.iciba.com/web/news/longweibo/imag/2018-11-21.jpg"
     }
     */

    public String sid = "";
    public String tts = "";
    public String content = "";
    public String note = "";
    public String love = "";
    public String translation = "";
    public String picture = "";
    public String picture2 = "";
    public String caption = "";
    public String dateline = "";
    public String s_pv = "";
    public String sp_pv = "";
    public List<Tag> tags = new ArrayList<>();
    public String fenxiang_img = "";

    class Tag{
        public String id = "";
        public String name = "";

        @Override
        public String toString() {
            return "Tag{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DailySentenceResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", sid='" + sid + '\'' +
                ", tts='" + tts + '\'' +
                ", content='" + content + '\'' +
                ", note='" + note + '\'' +
                ", love='" + love + '\'' +
                ", translation='" + translation + '\'' +
                ", picture='" + picture + '\'' +
                ", picture2='" + picture2 + '\'' +
                ", caption='" + caption + '\'' +
                ", dateline='" + dateline + '\'' +
                ", s_pv='" + s_pv + '\'' +
                ", sp_pv='" + sp_pv + '\'' +
                ", tags=" + tags +
                ", fenxiang_img='" + fenxiang_img + '\'' +
                '}';
    }
}
