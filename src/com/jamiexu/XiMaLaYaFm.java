package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @author Jamiexu
 * time:2020-5-7 19:10
 * blog:blog.jamiexu.cn
 * This code is for learning only！！！
 * This code is for learning only！！！
 * This code is for learning only！！！
 */

public class XiMaLaYaFm {
    private final int nums = 20;
    private final ArrayList<AlbumBean> album_arrayList = new ArrayList<>();
    private final String SEARCH_URL = "https://www.ximalaya.com/revision/search/main?core=album&kw=%s&page=1&spellchecker=true&rows=20&condition=relation&device=iPhone&fq=&paidFilter=false";
    private final String ALBUM_URL = "https://www.ximalaya.com/revision/album?albumId=%s";
    private final String AUDIO_URL = "https://www.ximalaya.com/revision/play/v1/audio?id=%s&ptype=1";

    private String get(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("xm-sign", generateXmSign());
            httpURLConnection.setConnectTimeout(5 * 1000);
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

    public void loadAlbum(String keyWord) {
        keyWord = URLEncoder.encode(keyWord, StandardCharsets.UTF_8);
        String response = get(String.format(SEARCH_URL, keyWord));
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("data").getJSONObject("album").getJSONArray("docs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("customTitle");
                String id = jsonObject.getString("albumId");
//                System.out.println(name);
                String response1 = get(String.format(ALBUM_URL, id));
                ArrayList<AudioBean> audioBeans = getAudioBean(response1);
                if (audioBeans.size() != 0)
                    this.album_arrayList.add(new AlbumBean(name, audioBeans));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public AlbumBean getAlbum(int pos) {
        return this.album_arrayList.get(pos);
    }

    public int size() {
        return this.album_arrayList.size();
    }

    private ArrayList<AudioBean> getAudioBean(String data) {
        ArrayList<AudioBean> audioBeans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONObject(data).getJSONObject("data").getJSONObject("tracksInfo").getJSONArray("tracks");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("title");
                String id = jsonObject.getString("trackId");
                String response = get(String.format(AUDIO_URL, id));
                JSONObject jsonObject1 = new JSONObject(response);
                String url = jsonObject1.getJSONObject("data").getString("src");
                if (!url.equals("null"))
                    audioBeans.add(new AudioBean(name, url));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return audioBeans;
    }

    private String generateXmSign() {
        String time = System.currentTimeMillis() + "000";
        return getMd5(("himalaya-" + time).getBytes(), false) + "(80)" + time + "(75)" + time;
    }

    private String getMd5(byte[] bytes, boolean upper) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return bytesToHex(messageDigest.digest(bytes), upper);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String bytesToHex(byte[] bytes, boolean upper) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b & 0xFF));
        }
        if (upper)
            return stringBuilder.toString().toUpperCase();
        return stringBuilder.toString();
    }

    class AlbumBean {
        private String title;
        private ArrayList<AudioBean> audioBean;

        public AlbumBean(String title, ArrayList<AudioBean> audioBean) {
            this.title = title;
            this.audioBean = audioBean;
        }

        public AudioBean getAudio(int pos) {
            if (pos < audioBean.size())
                return this.audioBean.get(pos);
            return new AudioBean(null, null);
        }

        public int size() {
            return this.audioBean.size();
        }

    }

    class AudioBean {
        private String title;
        private String url;

        public AudioBean(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }

}
