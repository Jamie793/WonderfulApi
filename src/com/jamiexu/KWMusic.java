package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Jamiexu
 * time:2020-6-3 13:31
 * blog:blog.jamiexu.cn
 * This code is for learning only！！！
 * This code is for learning only！！！
 * This code is for learning only！！！
 */

public class KWMusic {
    private final String SEARCH_URL = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=1&rn=%d&httpsStatus=1";
    private final String PLAY_URL = "http://www.kuwo.cn/url?httpsStatus=1&format=mp3&rid=%s&response=url&type=convert_url3&br=128kmp3";
    private final int NUM = 40;
    private int currentPos = 0;
    private ArrayList<KWBean> arrayList = new ArrayList<>();

    private String get(String url, String cookie) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.setRequestProperty("Cookie", cookie);
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
            httpURLConnection.setRequestProperty("Referer", "http://www.kuwo.cn/search/list");
            httpURLConnection.setRequestProperty("Host", "www.kuwo.cn");
            httpURLConnection.setRequestProperty("csrf", "WVTS7C1S50");
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                String str = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str).append("\n");
                }
                bufferedReader.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public void loadSong(String keyword) {
//        System.out.println(String.format(SEARCH_URL, keyword,NUM));
        String response = get(String.format(SEARCH_URL, keyword,NUM), "kw_token=WVTS7C1S50");
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String url = getPlayUrl(jsonObject.getString("rid"));
                this.arrayList.add(new KWBean(name, url));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public KWBean preSong() {
        if (this.currentPos != 0)
            this.currentPos--;
        return this.arrayList.get(this.currentPos);
    }

    public KWBean nextSong() {
        if (this.currentPos != this.arrayList.size() - 1)
            this.currentPos++;
        return this.arrayList.get(this.currentPos);
    }

    public KWBean getCurrent() {
        return this.arrayList.get(this.currentPos);
    }


    private String getPlayUrl(String rid) {
        String response = get(String.format(PLAY_URL, rid), "");
        try {
            return new JSONObject(response).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    class KWBean {
        private String name;
        private String url;

        public KWBean(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
