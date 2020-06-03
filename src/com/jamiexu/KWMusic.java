package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class KWMusic {
    private final String SEARCH_URL = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=%s&pn=1&rn=30&httpsStatus=1";
    private final String PLAY_URL = "http://www.kuwo.cn/url?httpsStatus=1&format=mp3&rid=%s&response=url&type=convert_url3&br=128kmp3";

    private String get(String url, String cookie) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.setRequestProperty("Cookie", cookie);
            httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
            httpURLConnection.setRequestProperty("Referer","http://www.kuwo.cn/search/list");
            httpURLConnection.setRequestProperty("Host","www.kuwo.cn");
            httpURLConnection.setRequestProperty("csrf","WVTS7C1S50");
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
        String response = get(String.format(SEARCH_URL, keyword),"kw_token=WVTS7C1S50");
        try {
            JSONArray jsonArray = new JSONObject(response).getJSONObject("data").getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String rid = jsonObject.getString("rid");
                System.out.println(rid);
                System.out.println(jsonObject.getString("name"));
                System.out.println(jsonObject.getString("artist"));
                System.out.println(getPlayUrl(rid));
                System.out.println();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getPlayUrl(String rid){
        String response = get(String.format(PLAY_URL,rid),"");
        return response;
    }
}
