package selling.sunshine.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by sunshine on 5/27/16.
 */
public class Configuration {
    private String nonceStr;
    private String timestamp;
    private String signature;
    private String url;
    private String shareLink;

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}