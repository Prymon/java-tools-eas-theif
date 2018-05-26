package com.prymon.tools.eastheif.downer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prymon.tools.eastheif.netease.Api;
import com.prymon.tools.eastheif.netease.UrlParamPair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import com.prymon.tools.eastheif.secret.JSSecret;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetRequest {

    public static String getMusicUrl(String musicId) {

        try {
            JSONObject json = new JSONObject();
            json.put("ids", "[" + musicId + "]");
            json.put("br", "128000");
            json.put("csrf_token", "");
            Connection.Response response = Jsoup.connect(" http://music.163.com/weapi/song/enhance/player/url?csrf_token=")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
                    .header("Accept", "*/*")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive")
                    .header("Host", "music.163.com")
                    .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
                    .header("DNT", "1")
                    .header("Pragma", "no-cache")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Referer", "http://music.163.com/")
                    .data(JSSecret.getDatas(json.toJSONString()))
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .execute();
            String list = response.body();
            String url = JSON.parseObject(list).getJSONArray("data").getJSONObject(0).getString("url");
            return url;
        } catch (IOException e) {
            return null;
        }
    }

    public static Map<String, String> searchMusicId(String musicName) {
        try {
            UrlParamPair upp = Api.SearchMusicList(musicName, "1");
            String req_str = upp.getParas().toJSONString();
            System.out.println("req_str:" + req_str);
            Connection.Response
                    response = Jsoup.connect("http://music.163.com/weapi/cloudsearch/get/web?csrf_token=")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
                    .header("Accept", "*/*")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive")
                    .header("Host", "music.163.com")
                    .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
                    .header("DNT", "1")
                    .header("Pragma", "no-cache")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data(JSSecret.getDatas(req_str))
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .execute();
            String list = response.body();
            HashMap<String, String> resultMap = new HashMap<>();
            if (JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").size() >= 1) {
                String id_1 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(0).getString("id");
                String name_1 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(0).getString("name") + "_01_" + JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(0).getJSONArray("ar").getJSONObject(0).getString("name");
                resultMap.put(id_1, name_1);
            }
            if (JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").size() >= 2) {
                String id_2 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(1).getString("id");
                String name_2 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(1).getString("name") + "_01_" + JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(1).getJSONArray("ar").getJSONObject(0).getString("name");
                resultMap.put(id_2, name_2);
            }
            if (JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").size() >= 3) {
                String id_3 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(2).getString("id");
                String name_3 = JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(2).getString("name") + "_01_" + JSON.parseObject(list).getJSONObject("result").getJSONArray("songs").getJSONObject(2).getJSONArray("ar").getJSONObject(0).getString("name");
                resultMap.put(id_3, name_3);
            }
            if (resultMap.isEmpty()) {
                return null;
            }
            return resultMap;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean downloadMusicByName(String musicName, String outputDir) {

        Map<String, String> musicIds = searchMusicId(musicName);
        if (musicIds == null) {
            return false;
        }
        boolean result = false;
        for (Map.Entry<String, String> entry : musicIds.entrySet()) {
            try {
                String musicUrl = getMusicUrl(entry.getKey());
                String fileName = entry.getValue() + ".mp3";
                FileDownloader.downLoadFromUrl(musicUrl, fileName, "music/");
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        downloadMusicByName("就是爱你", "music/");
    }
}