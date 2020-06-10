package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author Jamiexu
 * time:2020-5-5 19:14
 * blog:blog.jamiexu.cn
 * This code is for learning only！！！
 * This code is for learning only！！！
 * This code is for learning only！！！
 */

public class KGMusic {
    //last test time:2020-6-3 13:46
    //last test time:2020-6-9 12:23
    private final int nums = 30;
    private int currentPos = 0;
    private ArrayList<KGBean> arrayList = new ArrayList<>();


    private String get(String url, String cookie) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.setRequestProperty("Cookie", cookie);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                String str = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
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
        loadSong(keyword, 1);
    }

    public void loadSong(String keyword, int page) {
        this.arrayList.clear();
        try {
            String format = "https://songsearch.kugou.com/song_search_v2?keyword=%s&page=1&pagesize=%d&userid=-1&clientver=&platform=WebFilter&tag=em&filter=2&iscorrection=1&privilege_filter=0&_=%d";
            String response = get(String.format(format, keyword, this.nums, System.currentTimeMillis()), "");
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("lists");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                String name = jsonObject1.getString("SongName").replace("</em>", "").replace("<em>", "");
                String singerName = jsonObject1.getString("SingerName");
                String url = getPlayUrl(jsonObject1.getString("FileHash"));
                int size = getSize(url);
                this.arrayList.add(new KGBean(name, url, singerName, size));
            }

        } catch (JSONException e) {
        }
    }

    private int getSize(String url) {
        if (url == null)
            return -1;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            return httpURLConnection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public KGBean preSong() {
        if (this.currentPos != 0)
            this.currentPos--;
        return this.arrayList.get(this.currentPos);
    }

    public KGBean nextSong() {
        if (this.currentPos != this.arrayList.size() - 1)
            this.currentPos++;
        return this.arrayList.get(this.currentPos);
    }

    public KGBean getCurrent() {
        return this.arrayList.get(this.currentPos);
    }

    public ArrayList<KGMusic.KGBean> getDataLists() {
        return this.arrayList;
    }

    private String getPlayUrl(String filehash) {
        try {
            String responese = get("https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=" + filehash, "kg_mid=21cd38b3d6b7cd06117d1c3154ab8fd4;");
            JSONObject jsonObject = new JSONObject(responese).getJSONObject("data");
            return jsonObject.getString("play_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    class KGBean {
        private String name;
        private String url;
        private String singerName;
        private int size;

        public KGBean(String name, String url, String singerName, int size) {
            this.name = name;
            this.url = url;
            this.singerName = singerName;
            this.size = size;
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

        public String getSingerName() {
            return singerName;
        }

        public void setSingerName(String singerName) {
            this.singerName = singerName;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

}