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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @author Jamiexu
 * time:2020-5-5 19:14
 * blog:blog.jamiexu.cn
 * The code is for learning only！！！
 * The code is for learning only！！！
 * The code is for learning only！！！
 * <p>
 * The time of the second update: 2020-11-27
 */

public class KGMusic {
    //last test time:2020-6-3 13:46
    //last test time:2020-6-9 12:23
    //last test time:2020-11-27 19:36
    private final int pageSize = 30;
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

    public String getMD5(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(content.getBytes());
            for (int i = 0; i < bytes.length; i++) {
                String hex = String.format("%02x", bytes[i] & 0XFF);
                stringBuilder.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().toUpperCase();
    }

    public String getSignature(ArrayList<String> strings) {
        strings.sort(String::compareTo);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt");
        for (String s : strings) {
            stringBuilder.append(s);
        }
        stringBuilder.append("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt");
        return getMD5(stringBuilder.toString());
    }

    public void loadSong(String keyword, int page) {
        this.arrayList.clear();
        try {

            ArrayList<String> strings = new ArrayList<>();
            strings.add("bitrate=0");
//            strings.add("callback=callback345");
            strings.add("clienttime=1606477446577");
            strings.add("clientver=2000");
            strings.add("dfid=-");
            strings.add("inputtype=0");
            strings.add("iscorrection=1");
            strings.add("isfuzzy=0");
            strings.add("keyword=559");
            strings.add("mid=1606477446577");
            strings.add("page=" + page);
            strings.add("pagesize=" + pageSize);
            strings.add("platform=WebFilter");
            strings.add("privilege_filter=0");
            strings.add("srcappid=2919");
            strings.add("tag=em");
            strings.add("userid=-1");
            strings.add("uuid=1606477446577");
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : strings) {
                stringBuilder.append(s).append("&");
            }
            stringBuilder.append("signature=" + getSignature(strings));


            String response = get("https://complexsearch.kugou.com/v2/search/song?" + stringBuilder.toString(), "");
            System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("lists");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
//                System.out.println(jsonObject1);
                String name = jsonObject1.getString("SongName").replace("</em>", "")
                        .replace("<em>", "");

                String singerName = jsonObject1.getString("SingerName");
                String albumID = jsonObject1.getString("AlbumID");
                String url = getPlayUrl(jsonObject1.getString("FileHash"), albumID);
                int size = Integer.parseInt(jsonObject1.getString("FileSize"));
                this.arrayList.add(new KGBean(name, url, albumID, singerName, size));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private String getPlayUrl(String filehash, String albumID) {
        try {
            String responese = get("https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=" + filehash +
                    "&album_id=" + albumID + "&mid=fd25635b451cd15bdc5562f9c4cf0e67&_=1606480130947", "");
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
        private String albumID;
        private String singerName;
        private int size;

        public KGBean(String name, String url, String albumID, String singerName, int size) {
            this.name = name;
            this.url = url;
            this.albumID = albumID;
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

        public String getAlbumID() {
            return albumID;
        }

        public void setAlbumID(String album) {
            this.albumID = album;
        }
    }

}