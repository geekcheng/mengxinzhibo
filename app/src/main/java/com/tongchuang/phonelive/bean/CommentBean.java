package com.tongchuang.phonelive.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class CommentBean {
    private String id;
    private String uid;
    private String touid;
    private String parentid;
    private String content;
    private String likes;
    private String addtime;
    private String datetime;
    private String islike;
    private UserInfo userInfo;
    private UserInfo toUserInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "userinfo")
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @JSONField(name = "userinfo")
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @JSONField(name = "touserinfo")
    public UserInfo getToUserInfo() {
        return toUserInfo;
    }

    @JSONField(name = "touserinfo")
    public void setToUserInfo(UserInfo toUserInfo) {
        this.toUserInfo = toUserInfo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTouid() {
        return touid;
    }

    public void setTouid(String touid) {
        this.touid = touid;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }

    public class UserInfo {
        private String id;
        private String user_nicename;
        private String avatar;
        private String avatar_thumb;
        private String sex;
        private String signature;
        private String coin;
        private String consumption;
        private String votestotal;
        private String province;
        private String city;
        private String birthday;
        private String user_status;
        private String issuper;
        private String level;
        private String level_anchor;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_nicename() {
            return user_nicename;
        }

        public void setUser_nicename(String user_nicename) {
            this.user_nicename = user_nicename;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAvatar_thumb() {
            return avatar_thumb;
        }

        public void setAvatar_thumb(String avatar_thumb) {
            this.avatar_thumb = avatar_thumb;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        public String getConsumption() {
            return consumption;
        }

        public void setConsumption(String consumption) {
            this.consumption = consumption;
        }

        public String getVotestotal() {
            return votestotal;
        }

        public void setVotestotal(String votestotal) {
            this.votestotal = votestotal;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        public String getIssuper() {
            return issuper;
        }

        public void setIssuper(String issuper) {
            this.issuper = issuper;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getLevel_anchor() {
            return level_anchor;
        }

        public void setLevel_anchor(String level_anchor) {
            this.level_anchor = level_anchor;
        }
    }
}
