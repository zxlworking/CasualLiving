package com.zxl.casual.living.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxl on 2018/11/30.
 */

public class MusicComment {
    /**
     "isMusician":false,
     "code":200,
     "userId":-1,
     "hotComments":[
     {
     "commentLocationType":0,
     "liked":false,
     "expressionUrl":null,
     "commentId":257112364,
     "beReplied":[

     ],
     "content":"费玉清是个神秘的男人。翻唱自成一派，无数经典荤段子却从未被打死，直男却终身未婚，面目清秀却骚得飞起，骚得飞起却少有负面新闻。抬头唱歌圣如佛，低头嘿嘿淫如魔。",
     "user":{
     "remarkName":null,
     "expertTags":null,
     "avatarUrl":"https://p1.music.126.net/4p_SCtvRKtyt-N82ZANG-A==/6062707115568426.jpg",
     "userId":7751992,
     "locationInfo":null,
     "vipRights":null,
     "userType":0,
     "experts":null,
     "authStatus":0,
     "nickname":"毛富贵",
     "vipType":0
     },
     "likedCount":13974,
     "time":1480571506111,
     "pendantData":null,
     "showFloorComment":null,
     "parentCommentId":0
     }
     ],
     "comments":[
     {
     "commentLocationType":0,
     "liked":false,
     "expressionUrl":null,
     "commentId":1312767005,
     "isRemoveHotComment":false,
     "beReplied":[
     {
     "content":"费玉清犹如酒肉和尚，酒肉穿肠过，佛祖心中留。",
     "status":0,
     "beRepliedCommentId":246430786,
     "user":{
     "remarkName":null,
     "expertTags":null,
     "avatarUrl":"https://p2.music.126.net/YoR8qyiZgfYVKKCwl5dnsw==/109951163594346794.jpg",
     "userId":30332218,
     "locationInfo":null,
     "vipRights":null,
     "userType":0,
     "experts":null,
     "authStatus":0,
     "nickname":"弱风轻拂柳",
     "vipType":10
     },
     "expressionUrl":null
     }
     ],
     "content":"费玉清就是现实生活中的济公活佛",
     "user":{
     "remarkName":null,
     "expertTags":null,
     "avatarUrl":"https://p2.music.126.net/9yq7VsEJYbvX10v45ywYrQ==/18716986441346963.jpg",
     "userId":398278692,
     "locationInfo":null,
     "vipRights":null,
     "userType":0,
     "experts":null,
     "authStatus":0,
     "nickname":"谜天大圣",
     "vipType":0
     },
     "likedCount":0,
     "time":1543499646195,
     "pendantData":null,
     "showFloorComment":null,
     "parentCommentId":0
     }
     ],
     "more":true,
     "topComments":[

     ],
     "total":3151,
     "moreHot":true
     */

    public boolean isMusician = false;
    public int code = 0;
    public int userId = 0;
    public List<HotComment> hotComments = new ArrayList<>();
    public List<Comment> comments = new ArrayList<>();
    public boolean more = false;
    public int total = 0;
    public boolean moreHot = false;

    public class HotComment{
        public int commentLocationType = 0;
        public boolean liked = false;
        public long commentId = 0;
        public List<BeReplied> beReplied = new ArrayList<>();
        public String content = "";
        public User user = null;
        public int likedCount = 0;
        public long time = 0;
        public long parentCommentId = 0;

        @Override
        public String toString() {
            return "HotComment{" +
                    "commentLocationType=" + commentLocationType +
                    ", liked=" + liked +
                    ", commentId=" + commentId +
                    ", beReplied=" + beReplied +
                    ", content='" + content + '\'' +
                    ", user=" + user +
                    ", likedCount=" + likedCount +
                    ", time=" + time +
                    ", parentCommentId=" + parentCommentId +
                    '}';
        }
    }

    public class User{
        public String avatarUrl = "";
        public long userId = 0;
        //"locationInfo":null,
        //"vipRights":null,
        public int userType = 0;
        public int authStatus = 0;
        public String nickname = "";
        public int vipType = 0;

        @Override
        public String toString() {
            return "User{" +
                    "avatarUrl='" + avatarUrl + '\'' +
                    ", userId=" + userId +
                    ", userType=" + userType +
                    ", authStatus=" + authStatus +
                    ", nickname='" + nickname + '\'' +
                    ", vipType=" + vipType +
                    '}';
        }
    }

    public class Comment{
        public int commentLocationType = 0;
        public boolean liked = false;
        public long commentId = 0;
        public boolean isRemoveHotComment = false;
        public List<BeReplied> beReplied = new ArrayList<>();
        public String content = "";
        public User user = null;
        public int likedCount = 0;
        public long time = 0;
        public long parentCommentId = 0;

        @Override
        public String toString() {
            return "Comment{" +
                    "commentLocationType=" + commentLocationType +
                    ", liked=" + liked +
                    ", commentId=" + commentId +
                    ", isRemoveHotComment=" + isRemoveHotComment +
                    ", beReplied=" + beReplied +
                    ", content='" + content + '\'' +
                    ", user=" + user +
                    ", likedCount=" + likedCount +
                    ", time=" + time +
                    ", parentCommentId=" + parentCommentId +
                    '}';
        }
    }

    public class BeReplied {
        public String content = "";
        public int status = 0;
        public long beRepliedCommentId = 0;
        public User user = null;

        @Override
        public String toString() {
            return "BeReplied{" +
                    "content='" + content + '\'' +
                    ", status=" + status +
                    ", beRepliedCommentId=" + beRepliedCommentId +
                    ", user=" + user +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MusicComment{" +
                "isMusician=" + isMusician +
                ", code=" + code +
                ", userId=" + userId +
                ", hotComments=" + hotComments +
                ", comments=" + comments +
                ", more=" + more +
                ", total=" + total +
                ", moreHot=" + moreHot +
                '}';
    }
}
