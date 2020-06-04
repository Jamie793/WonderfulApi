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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmMusic {
    private final int NUM = 30;
    private String COOKIE = "xmgid=6746be65-95e1-4dde-b0e2-37c9751aeb5a; _uab_collina=159116350127451706464992; cna=FbPCFvbhDCwCAd9KYChS4CAo; gid=159118246958928; _xiamitoken=463afe1357c094c33be7cef50ced10ad; _unsign_token=04da81c5bd144837eae04cf110ebf13c; xm_sg_tk=6e876135d5ad813916a9f509d6a866a6_"
            + System.currentTimeMillis() + "; xm_sg_tk.sig=ONX71eT2-VT97hBaTmg0XTlmr8D-wgqTvrXXUEQQFsE; xm_traceid=0b0f6f3615912441967388803ee941; xm_oauth_state=301740c5c290a01ffbc06e0b038cff2f; _xm_umtoken=T365F6596C20FD4A374A0F642C92D8A7B3BDC2421FA9F5E9F35F0663018; x5sec=7b22617365727665723b32223a223536643133386232633337633964313338383362326537313233626461623630434d6e3134665946454b2f79754d436b362f57506851453d227d; _xm_cf_=mS88y0ezDKYu4rr-Wl6XKYLa; l=eBjaAYNqQW7RYB06BO5anurza779iCRX1sPzaNbMiInca1JFgEvDwNQDBif2zdtjgtCXqHtP5TNQiRnyWe4dgG8NNLEL2oN-DxvO.; isg=BBwcpCtRrveJJloknGKRKXGK7TrOlcC_hhX-s_YZqof6QbfLDqdJT4u3oam5SfgX";

    private String get(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.setRequestProperty("Cookie", this.COOKIE);
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

    public String getToken() {
        Pattern pattern = Pattern.compile("xm_sg_tk=([^;]*)");
        Matcher matcher = pattern.matcher(this.COOKIE);
        while (matcher.find()) {
            return matcher.group().replace("xm_sg_tk=", "").split("_")[0];
        }
        return null;
    }

    public String getCookie() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://www.xiami.com/").openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                Map<String, List<String>> headers = httpURLConnection.getHeaderFields();
                for (Map.Entry<String, List<String>> h : headers.entrySet()) {

                    if (h.getKey() != null && h.getKey().contains("set-cookie")) {
                        String cookie = h.getValue() + "";
                        Pattern pattern = Pattern.compile("[xm_sg_tk|xm_sg_tk\\.sig]+=([^;]*)");
                        Matcher matcher = pattern.matcher(cookie);
                        String c = "";
                        while (matcher.find()) {
                            String d = matcher.group();
                            if (d.contains("xm_sg_tk.sig=") || d.contains("xm_sg_tk="))
                                c += d + ";";
                        }
                        c+="x5sec=7b22617365727665723b32223a223764353365313037333936353937646233376539626664363365333165353633434f614b3476594645496a7167357246304a533755513d3d227d;";
                        return c;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadSong(String keyword) {
        this.COOKIE = getCookie();
        String token = getToken();
        String url = "https://www.xiami.com/api/search/searchSongs?_q=";
        String data = "{\"key\":\"" + keyword + "\",\"pagingVO\":{\"page\":1,\"pageSize\":" + NUM + "}}";
        String encode = getEncode(token, "/api/search/searchSongs", data);
        url += URLEncoder.encode(data, StandardCharsets.UTF_8) + "&_s=" + encode;
        String response = get(url);
        System.out.println(response);

        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("result").getJSONObject("data").getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("songId");
                String name = jsonObject.getString("songName");
                System.out.println(getPlayUrl(id));
                System.out.println(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPlayUrl(String id) {
        String token = getToken();
        String url = "https://www.xiami.com/api/song/getPlayInfo?_q=";
        String data = "{\"songIds\":[" + id + "]}";
        String encode = getEncode(token, "/api/song/getPlayInfo", data);
        url += URLEncoder.encode(data, StandardCharsets.UTF_8) + "&_s=" + encode;
        String response = get(url);
        System.out.println(response);
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("result").getJSONObject("data")
                    .getJSONArray("songPlayInfos").getJSONObject(0).getJSONArray("playInfos");
            if (jsonArray.length() == 0)
                return null;
            String link = jsonArray.getJSONObject(0).getString("listenFile");
            if (link == null || link.equals("") || link.length() == 0)
                link = jsonArray.getJSONObject(1).getString("listenFile");
            return link;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getEncode(String token, String url, String data) {
        String d = token + "_xmMain_" + url + "_" + data;
        return getMd5(d.getBytes(), false);
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

}
