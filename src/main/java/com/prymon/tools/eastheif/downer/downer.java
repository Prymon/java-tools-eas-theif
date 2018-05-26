package com.prymon.tools.eastheif.downer;

import com.alibaba.fastjson.JSONObject;
import com.prymon.tools.eastheif.secret.JSSecret;

import java.io.IOException;
import java.util.HashMap;

public class downer {
    public static void main(String[] args) throws IOException {
//        searchMusicId();
        getUrl("26620756");
    }

    private static void getUrl(String musicId) {
        JSONObject json = new JSONObject();
        json.put("ids", "[" + musicId + "]");
        json.put("br", "128000");
        json.put("csrf_token", "");
        HashMap<String, String> datas = JSSecret.getDatas(json.toJSONString());
        System.out.println(datas);
    }

}
