package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

public class QQMusic {
    //last test time:2020-6-3 13:46
    //last test time:2020-6-8 18:56
    private final ArrayList<QQMusicBean> arrayList = new ArrayList<>();
    private final static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    private final int NUM = 30;
    private final String UIN = "774461324";
    private int currentPos = 0;

    static {
        initJs();
    }

    public QQMusicBean preSong() {
        if (this.currentPos != 0)
            this.currentPos--;
        return this.arrayList.get(this.currentPos);
    }

    public QQMusicBean nextSong() {
        if (this.currentPos != this.arrayList.size() - 1)
            this.currentPos++;
        return this.arrayList.get(this.currentPos);
    }

    public QQMusicBean getCurrent() {
        return this.arrayList.get(this.currentPos);
    }

    public ArrayList<QQMusicBean> getDataLists() {
        return this.arrayList;
    }


    private String get(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
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


    public void loadSong(String keyWord) {
        loadSong(keyWord, 1);
    }


    public void loadSong(String keyWord, int page) {
        this.arrayList.clear();
        String url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&searchid=42217694605196790&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p="
                + page + "&n="
                + this.NUM + "&w="
                + keyWord + "&g_tk_new_20200303=5381&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0";

        String response = get(url);
        try {
            JSONObject jsonObject = new JSONObject(response).getJSONObject("data").getJSONObject("song");
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String title = jsonObject1.getJSONObject("album").getString("name");
                JSONArray singer = jsonObject1.getJSONArray("singer");
                String singerName = "";
                for (int j = 0; j < singer.length(); j++) {
                    singerName += singer.getJSONObject(j).getString("title") + "/";
                }
                singerName = singerName.substring(0, singerName.length() - 1);
                int size = jsonObject1.getJSONObject("file").getInt("size_128mp3");
                String mid = jsonObject1.getString("mid");
                String purl = "https://isure.stream.qqmusic.qq.com/" + getVkey(mid);
                this.arrayList.add(new QQMusicBean(title, purl, singerName, size));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getData(String songmid) {
        String data = "{\"req\":{\"module\":\"CDN.SrfCdnDispatchServer\",\"method\":\"GetCdnDispatch\",\"param\":{\"guid\":\"1190859168\",\"calltype\":0,\"userip\":\"\"}},\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"1190859168\",\"songmid\":[\"" + songmid + "\"],\"songtype\":[0],\"uin\":\"" + this.UIN + "\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":" + this.UIN + ",\"format\":\"json\",\"ct\":24,\"cv\":0}}";
        return data;
    }

    public String getVkey(String songmid) {
        String data = getData(songmid);
        String sign = getSign(data);
        String url = null;
        try {
            url = "https://u.y.qq.com/cgi-bin/musics.fcg?-=getplaysongvkey946142466285981&g_tk=480671689&sign=" + sign + "&loginUin=" + this.UIN + "&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&data=" + URLEncoder.encode(data, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        System.out.println(get(url));
        try {
            JSONArray jsonArray = new JSONObject(get(url)).getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo");
            return jsonArray.getJSONObject(0).getString("purl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initJs() {
        try {
            scriptEngine.eval("window=this;var n = window;\n" +
                    "var getSecurity = function() {\n" +
                    "\n" +
                    "    n.__sign_hash_20200305 = function(n, t) {\n" +
                    "        function f(n, t) {\n" +
                    "            return n << t | n >>> 32 - t\n" +
                    "        }\n" +
                    "        function h(n, t) {\n" +
                    "            var o, e, u, p, r;\n" +
                    "            return u = 2147483648 & n,\n" +
                    "            p = 2147483648 & t,\n" +
                    "            r = (1073741823 & n) + (1073741823 & t),\n" +
                    "            (o = 1073741824 & n) & (e = 1073741824 & t) ? 2147483648 ^ r ^ u ^ p : o | e ? 1073741824 & r ? 3221225472 ^ r ^ u ^ p : 1073741824 ^ r ^ u ^ p : r ^ u ^ p\n" +
                    "        }\n" +
                    "        function o(n, t, o, e, u, p, r) {\n" +
                    "            var i;\n" +
                    "            return n = h(n, h(h((i = t) & o | ~i & e, u), r)),\n" +
                    "            h(f(n, p), t)\n" +
                    "        }\n" +
                    "        function e(n, t, o, e, u, p, r) {\n" +
                    "            var i;\n" +
                    "            return n = h(n, h(h(t & (i = e) | o & ~i, u), r)),\n" +
                    "            h(f(n, p), t)\n" +
                    "        }\n" +
                    "        function u(n, t, o, e, u, p, r) {\n" +
                    "            return n = h(n, h(h(t ^ o ^ e, u), r)),\n" +
                    "            h(f(n, p), t)\n" +
                    "        }\n" +
                    "        function p(n, t, o, e, u, p, r) {\n" +
                    "            return n = h(n, h(h(o ^ (t | ~e), u), r)),\n" +
                    "            h(f(n, p), t)\n" +
                    "        }\n" +
                    "        function r(n) {\n" +
                    "            var t, o = \"\", e = \"\";\n" +
                    "            for (t = 0; t <= 3; t++)\n" +
                    "                o += (e = \"0\" + (n >>> 8 * t & 255).toString(16)).substr(e.length - 2, 2);\n" +
                    "            return o\n" +
                    "        }\n" +
                    "        var i, l, c, g, a, s, v, d, y, b;\n" +
                    "        for (t = t || 32,\n" +
                    "        i = function(n) {\n" +
                    "            for (var t, o = n.length, e = o + 8, u = 16 * (1 + (e - e % 64) / 64), p = Array(u - 1), r = 0, i = 0; i < o; )\n" +
                    "                r = i % 4 * 8,\n" +
                    "                p[t = (i - i % 4) / 4] = p[t] | n.charCodeAt(i) << r,\n" +
                    "                i++;\n" +
                    "            return r = i % 4 * 8,\n" +
                    "            p[t = (i - i % 4) / 4] = p[t] | 128 << r,\n" +
                    "            p[u - 2] = o << 3,\n" +
                    "            p[u - 1] = o >>> 29,\n" +
                    "            p\n" +
                    "        }(n = function(n) {\n" +
                    "            n = n.replace(/\\r\\n/g, \"\\n\");\n" +
                    "            for (var t = \"\", o = 0; o < n.length; o++) {\n" +
                    "                var e = n.charCodeAt(o);\n" +
                    "                e < 128 ? t += String.fromCharCode(e) : (127 < e && e < 2048 ? t += String.fromCharCode(e >> 6 | 192) : (t += String.fromCharCode(e >> 12 | 224),\n" +
                    "                t += String.fromCharCode(e >> 6 & 63 | 128)),\n" +
                    "                t += String.fromCharCode(63 & e | 128))\n" +
                    "            }\n" +
                    "            return t\n" +
                    "        }(n)),\n" +
                    "        v = 1732584193,\n" +
                    "        d = 4023233417,\n" +
                    "        y = 2562383102,\n" +
                    "        b = 271733878,\n" +
                    "        l = 0; l < i.length; l += 16)\n" +
                    "            v = o(c = v, g = d, a = y, s = b, i[l + 0], 7, 3614090360),\n" +
                    "            b = o(b, v, d, y, i[l + 1], 12, 3905402710),\n" +
                    "            y = o(y, b, v, d, i[l + 2], 17, 606105819),\n" +
                    "            d = o(d, y, b, v, i[l + 3], 22, 3250441966),\n" +
                    "            v = o(v, d, y, b, i[l + 4], 7, 4118548399),\n" +
                    "            b = o(b, v, d, y, i[l + 5], 12, 1200080426),\n" +
                    "            y = o(y, b, v, d, i[l + 6], 17, 2821735955),\n" +
                    "            d = o(d, y, b, v, i[l + 7], 22, 4249261313),\n" +
                    "            v = o(v, d, y, b, i[l + 8], 7, 1770035416),\n" +
                    "            b = o(b, v, d, y, i[l + 9], 12, 2336552879),\n" +
                    "            y = o(y, b, v, d, i[l + 10], 17, 4294925233),\n" +
                    "            d = o(d, y, b, v, i[l + 11], 22, 2304563134),\n" +
                    "            v = o(v, d, y, b, i[l + 12], 7, 1804603682),\n" +
                    "            b = o(b, v, d, y, i[l + 13], 12, 4254626195),\n" +
                    "            y = o(y, b, v, d, i[l + 14], 17, 2792965006),\n" +
                    "            v = e(v, d = o(d, y, b, v, i[l + 15], 22, 1236535329), y, b, i[l + 1], 5, 4129170786),\n" +
                    "            b = e(b, v, d, y, i[l + 6], 9, 3225465664),\n" +
                    "            y = e(y, b, v, d, i[l + 11], 14, 643717713),\n" +
                    "            d = e(d, y, b, v, i[l + 0], 20, 3921069994),\n" +
                    "            v = e(v, d, y, b, i[l + 5], 5, 3593408605),\n" +
                    "            b = e(b, v, d, y, i[l + 10], 9, 38016083),\n" +
                    "            y = e(y, b, v, d, i[l + 15], 14, 3634488961),\n" +
                    "            d = e(d, y, b, v, i[l + 4], 20, 3889429448),\n" +
                    "            v = e(v, d, y, b, i[l + 9], 5, 568446438),\n" +
                    "            b = e(b, v, d, y, i[l + 14], 9, 3275163606),\n" +
                    "            y = e(y, b, v, d, i[l + 3], 14, 4107603335),\n" +
                    "            d = e(d, y, b, v, i[l + 8], 20, 1163531501),\n" +
                    "            v = e(v, d, y, b, i[l + 13], 5, 2850285829),\n" +
                    "            b = e(b, v, d, y, i[l + 2], 9, 4243563512),\n" +
                    "            y = e(y, b, v, d, i[l + 7], 14, 1735328473),\n" +
                    "            v = u(v, d = e(d, y, b, v, i[l + 12], 20, 2368359562), y, b, i[l + 5], 4, 4294588738),\n" +
                    "            b = u(b, v, d, y, i[l + 8], 11, 2272392833),\n" +
                    "            y = u(y, b, v, d, i[l + 11], 16, 1839030562),\n" +
                    "            d = u(d, y, b, v, i[l + 14], 23, 4259657740),\n" +
                    "            v = u(v, d, y, b, i[l + 1], 4, 2763975236),\n" +
                    "            b = u(b, v, d, y, i[l + 4], 11, 1272893353),\n" +
                    "            y = u(y, b, v, d, i[l + 7], 16, 4139469664),\n" +
                    "            d = u(d, y, b, v, i[l + 10], 23, 3200236656),\n" +
                    "            v = u(v, d, y, b, i[l + 13], 4, 681279174),\n" +
                    "            b = u(b, v, d, y, i[l + 0], 11, 3936430074),\n" +
                    "            y = u(y, b, v, d, i[l + 3], 16, 3572445317),\n" +
                    "            d = u(d, y, b, v, i[l + 6], 23, 76029189),\n" +
                    "            v = u(v, d, y, b, i[l + 9], 4, 3654602809),\n" +
                    "            b = u(b, v, d, y, i[l + 12], 11, 3873151461),\n" +
                    "            y = u(y, b, v, d, i[l + 15], 16, 530742520),\n" +
                    "            v = p(v, d = u(d, y, b, v, i[l + 2], 23, 3299628645), y, b, i[l + 0], 6, 4096336452),\n" +
                    "            b = p(b, v, d, y, i[l + 7], 10, 1126891415),\n" +
                    "            y = p(y, b, v, d, i[l + 14], 15, 2878612391),\n" +
                    "            d = p(d, y, b, v, i[l + 5], 21, 4237533241),\n" +
                    "            v = p(v, d, y, b, i[l + 12], 6, 1700485571),\n" +
                    "            b = p(b, v, d, y, i[l + 3], 10, 2399980690),\n" +
                    "            y = p(y, b, v, d, i[l + 10], 15, 4293915773),\n" +
                    "            d = p(d, y, b, v, i[l + 1], 21, 2240044497),\n" +
                    "            v = p(v, d, y, b, i[l + 8], 6, 1873313359),\n" +
                    "            b = p(b, v, d, y, i[l + 15], 10, 4264355552),\n" +
                    "            y = p(y, b, v, d, i[l + 6], 15, 2734768916),\n" +
                    "            d = p(d, y, b, v, i[l + 13], 21, 1309151649),\n" +
                    "            v = p(v, d, y, b, i[l + 4], 6, 4149444226),\n" +
                    "            b = p(b, v, d, y, i[l + 11], 10, 3174756917),\n" +
                    "            y = p(y, b, v, d, i[l + 2], 15, 718787259),\n" +
                    "            d = p(d, y, b, v, i[l + 9], 21, 3951481745),\n" +
                    "            v = h(v, c),\n" +
                    "            d = h(d, g),\n" +
                    "            y = h(y, a),\n" +
                    "            b = h(b, s);\n" +
                    "        return 32 == t ? r(v) + r(d) + r(y) + r(b) : r(d) + r(y)\n" +
                    "    }\n" +
                    "    ,\n" +
                    "    function i(f, h, l, c, g) {\n" +
                    "        g = g || [[this], [{}]];\n" +
                    "        for (var t = [], o = null, n = [function() {\n" +
                    "            return !0\n" +
                    "        }\n" +
                    "        , function() {}\n" +
                    "        , function() {\n" +
                    "            g.length = l[h++]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(l[h++])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++]\n" +
                    "              , t = g[g.length - 2 - n];\n" +
                    "            g[g.length - 2 - n] = g.pop(),\n" +
                    "            g.push(t)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(g[g.length - 1])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push([g.pop(), g.pop()].reverse())\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push([c, g.pop()])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push([g.pop()])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop();\n" +
                    "            g.push(n[0][n[1]])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(g[g.pop()[0]][0])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g[g.length - 2];\n" +
                    "            n[0][n[1]] = g[g.length - 1]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g[g.length - 2][0]][0] = g[g.length - 1]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop()\n" +
                    "              , t = g.pop();\n" +
                    "            g.push([t[0][t[1]], n])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop();\n" +
                    "            g.push([g[g.pop()][0], n])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop();\n" +
                    "            g.push(delete n[0][n[1]])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = [];\n" +
                    "            for (var t in g.pop())\n" +
                    "                n.push(t);\n" +
                    "            g.push(n)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 1].length ? g.push(g[g.length - 1].shift(), !0) : g.push(void 0, !1)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g[g.length - 2]\n" +
                    "              , t = Object.getOwnPropertyDescriptor(n[0], n[1]) || {\n" +
                    "                configurable: !0,\n" +
                    "                enumerable: !0\n" +
                    "            };\n" +
                    "            t.get = g[g.length - 1],\n" +
                    "            Object.defineProperty(n[0], n[1], t)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g[g.length - 2]\n" +
                    "              , t = Object.getOwnPropertyDescriptor(n[0], n[1]) || {\n" +
                    "                configurable: !0,\n" +
                    "                enumerable: !0\n" +
                    "            };\n" +
                    "            t.set = g[g.length - 1],\n" +
                    "            Object.defineProperty(n[0], n[1], t)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            h = l[h++]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++];\n" +
                    "            g[g.length - 1] && (h = n)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            throw g[g.length - 1]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++]\n" +
                    "              , t = n ? g.slice(-n) : [];\n" +
                    "            g.length -= n,\n" +
                    "            g.push(g.pop().apply(c, t))\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++]\n" +
                    "              , t = n ? g.slice(-n) : [];\n" +
                    "            g.length -= n;\n" +
                    "            var o = g.pop();\n" +
                    "            g.push(o[0][o[1]].apply(o[0], t))\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++]\n" +
                    "              , t = n ? g.slice(-n) : [];\n" +
                    "            g.length -= n,\n" +
                    "            t.unshift(null),\n" +
                    "            g.push(new (Function.prototype.bind.apply(g.pop(), t)))\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = l[h++]\n" +
                    "              , t = n ? g.slice(-n) : [];\n" +
                    "            g.length -= n,\n" +
                    "            t.unshift(null);\n" +
                    "            var o = g.pop();\n" +
                    "            g.push(new (Function.prototype.bind.apply(o[0][o[1]], t)))\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(!g.pop())\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(~g.pop())\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(typeof g.pop())\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] == g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] === g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] > g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] >= g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] << g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] >> g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] >>> g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] + g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] - g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] * g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] / g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] % g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] | g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] & g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] ^ g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2]in g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2]instanceof g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g[g.length - 1][0]] = void 0 === g[g[g.length - 1][0]] ? [] : g[g[g.length - 1][0]]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            for (var e = l[h++], u = [], n = l[h++], t = l[h++], p = [], o = 0; o < n; o++)\n" +
                    "                u[l[h++]] = g[l[h++]];\n" +
                    "            for (var r = 0; r < t; r++)\n" +
                    "                p[r] = l[h++];\n" +
                    "            g.push(function n() {\n" +
                    "                var t = u.slice(0);\n" +
                    "                t[0] = [this],\n" +
                    "                t[1] = [arguments],\n" +
                    "                t[2] = [n];\n" +
                    "                for (var o = 0; o < p.length && o < arguments.length; o++)\n" +
                    "                    0 < p[o] && (t[p[o]] = [arguments[o]]);\n" +
                    "                return i(f, e, l, c, t)\n" +
                    "            })\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            t.push([l[h++], g.length, l[h++]])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            t.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            return !!o\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            o = null\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 1] += String.fromCharCode(l[h++])\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(\"\")\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(void 0)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(null)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(!0)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.push(!1)\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g.length -= l[h++]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 1] = l[h++]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop()\n" +
                    "              , t = g[g.length - 1];\n" +
                    "            t[0][t[1]] = g[n[0]][0]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop()\n" +
                    "              , t = g[g.length - 1];\n" +
                    "            t[0][t[1]] = n[0][n[1]]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop()\n" +
                    "              , t = g[g.length - 1];\n" +
                    "            g[t[0]][0] = g[n[0]][0]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            var n = g.pop()\n" +
                    "              , t = g[g.length - 1];\n" +
                    "            g[t[0]][0] = n[0][n[1]]\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] < g.pop()\n" +
                    "        }\n" +
                    "        , function() {\n" +
                    "            g[g.length - 2] = g[g.length - 2] <= g.pop()\n" +
                    "        }\n" +
                    "        ]; ; )\n" +
                    "            try {\n" +
                    "                for (; !n[l[h++]](); )\n" +
                    "                    ;\n" +
                    "                if (o)\n" +
                    "                    throw o;\n" +
                    "                return g.pop()\n" +
                    "            } catch (n) {\n" +
                    "                var e = t.pop();\n" +
                    "                if (void 0 === e)\n" +
                    "                    throw n;\n" +
                    "                o = n,\n" +
                    "                h = e[0],\n" +
                    "                g.length = e[1],\n" +
                    "                e[2] && (g[e[2]][0] = o)\n" +
                    "            }\n" +
                    "    }(120731, 0, [21, 34, 50, 100, 57, 50, 102, 50, 98, 99, 101, 52, 54, 97, 52, 99, 55, 56, 52, 49, 57, 54, 57, 49, 56, 98, 102, 100, 100, 48, 48, 55, 55, 102, 2, 10, 3, 2, 9, 48, 61, 3, 9, 48, 61, 4, 9, 48, 61, 5, 9, 48, 61, 6, 9, 48, 61, 7, 9, 48, 61, 8, 9, 48, 61, 9, 9, 48, 4, 21, 427, 54, 2, 15, 3, 2, 9, 48, 61, 3, 9, 48, 61, 4, 9, 48, 61, 5, 9, 48, 61, 6, 9, 48, 61, 7, 9, 48, 61, 8, 9, 48, 61, 9, 9, 48, 61, 10, 9, 48, 61, 11, 9, 48, 61, 12, 9, 48, 61, 13, 9, 48, 61, 14, 9, 48, 61, 10, 9, 55, 54, 97, 54, 98, 54, 99, 54, 100, 54, 101, 54, 102, 54, 103, 54, 104, 54, 105, 54, 106, 54, 107, 54, 108, 54, 109, 54, 110, 54, 111, 54, 112, 54, 113, 54, 114, 54, 115, 54, 116, 54, 117, 54, 118, 54, 119, 54, 120, 54, 121, 54, 122, 54, 48, 54, 49, 54, 50, 54, 51, 54, 52, 54, 53, 54, 54, 54, 55, 54, 56, 54, 57, 13, 4, 61, 11, 9, 55, 54, 77, 54, 97, 54, 116, 54, 104, 8, 55, 54, 102, 54, 108, 54, 111, 54, 111, 54, 114, 14, 55, 54, 77, 54, 97, 54, 116, 54, 104, 8, 55, 54, 114, 54, 97, 54, 110, 54, 100, 54, 111, 54, 109, 14, 25, 0, 3, 4, 9, 11, 3, 3, 9, 11, 39, 3, 1, 38, 40, 3, 3, 9, 11, 38, 25, 1, 13, 4, 61, 12, 9, 55, 13, 4, 61, 13, 9, 3, 0, 13, 4, 4, 3, 13, 9, 11, 3, 11, 9, 11, 66, 22, 306, 4, 21, 422, 24, 4, 3, 14, 9, 55, 54, 77, 54, 97, 54, 116, 54, 104, 8, 55, 54, 102, 54, 108, 54, 111, 54, 111, 54, 114, 14, 55, 54, 77, 54, 97, 54, 116, 54, 104, 8, 55, 54, 114, 54, 97, 54, 110, 54, 100, 54, 111, 54, 109, 14, 25, 0, 3, 10, 9, 55, 54, 108, 54, 101, 54, 110, 54, 103, 54, 116, 54, 104, 15, 10, 40, 25, 1, 13, 4, 61, 12, 9, 6, 11, 3, 10, 9, 3, 14, 9, 11, 15, 10, 38, 13, 4, 61, 13, 9, 6, 11, 6, 5, 1, 5, 0, 3, 1, 38, 13, 4, 61, 0, 5, 0, 43, 4, 21, 291, 61, 3, 12, 9, 11, 0, 3, 9, 9, 49, 72, 0, 2, 3, 4, 13, 4, 61, 8, 9, 21, 721, 3, 2, 8, 3, 2, 9, 48, 61, 3, 9, 48, 61, 4, 9, 48, 61, 5, 9, 48, 61, 6, 9, 48, 61, 7, 9, 48, 4, 55, 54, 115, 54, 101, 54, 108, 54, 102, 8, 10, 30, 55, 54, 117, 54, 110, 54, 100, 54, 101, 54, 102, 54, 105, 54, 110, 54, 101, 54, 100, 32, 28, 22, 510, 4, 21, 523, 22, 4, 55, 54, 115, 54, 101, 54, 108, 54, 102, 8, 10, 0, 55, 54, 119, 54, 105, 54, 110, 54, 100, 54, 111, 54, 119, 8, 10, 30, 55, 54, 117, 54, 110, 54, 100, 54, 101, 54, 102, 54, 105, 54, 110, 54, 101, 54, 100, 32, 28, 22, 566, 4, 21, 583, 3, 4, 55, 54, 119, 54, 105, 54, 110, 54, 100, 54, 111, 54, 119, 8, 10, 0, 55, 54, 103, 54, 108, 54, 111, 54, 98, 54, 97, 54, 108, 8, 10, 30, 55, 54, 117, 54, 110, 54, 100, 54, 101, 54, 102, 54, 105, 54, 110, 54, 101, 54, 100, 32, 28, 22, 626, 4, 21, 643, 25, 4, 55, 54, 103, 54, 108, 54, 111, 54, 98, 54, 97, 54, 108, 8, 10, 0, 55, 54, 69, 54, 114, 54, 114, 54, 111, 54, 114, 8, 55, 54, 117, 54, 110, 54, 97, 54, 98, 54, 108, 54, 101, 54, 32, 54, 116, 54, 111, 54, 32, 54, 108, 54, 111, 54, 99, 54, 97, 54, 116, 54, 101, 54, 32, 54, 103, 54, 108, 54, 111, 54, 98, 54, 97, 54, 108, 54, 32, 54, 111, 54, 98, 54, 106, 54, 101, 54, 99, 54, 116, 27, 1, 23, 56, 0, 49, 444, 0, 0, 24, 0, 13, 4, 61, 8, 9, 55, 54, 95, 54, 95, 54, 103, 54, 101, 54, 116, 54, 83, 54, 101, 54, 99, 54, 117, 54, 114, 54, 105, 54, 116, 54, 121, 54, 83, 54, 105, 54, 103, 54, 110, 15, 21, 1126, 49, 2, 14, 3, 2, 9, 48, 61, 3, 9, 48, 61, 4, 9, 48, 61, 5, 9, 48, 61, 6, 9, 48, 61, 7, 9, 48, 61, 8, 9, 48, 61, 9, 9, 48, 61, 10, 9, 48, 61, 11, 9, 48, 61, 9, 9, 55, 54, 108, 54, 111, 54, 99, 54, 97, 54, 116, 54, 105, 54, 111, 54, 110, 8, 10, 30, 55, 54, 117, 54, 110, 54, 100, 54, 101, 54, 102, 54, 105, 54, 110, 54, 101, 54, 100, 32, 28, 22, 862, 21, 932, 21, 4, 55, 54, 108, 54, 111, 54, 99, 54, 97, 54, 116, 54, 105, 54, 111, 54, 110, 8, 55, 54, 104, 54, 111, 54, 115, 54, 116, 14, 55, 54, 105, 54, 110, 54, 100, 54, 101, 54, 120, 54, 79, 54, 102, 14, 55, 54, 121, 54, 46, 54, 113, 54, 113, 54, 46, 54, 99, 54, 111, 54, 109, 25, 1, 3, 0, 3, 1, 39, 32, 22, 963, 4, 55, 54, 67, 54, 74, 54, 66, 54, 80, 54, 65, 54, 67, 54, 114, 54, 82, 54, 117, 54, 78, 54, 121, 54, 55, 21, 974, 50, 4, 3, 12, 9, 11, 3, 8, 3, 10, 24, 2, 13, 4, 61, 10, 9, 3, 13, 9, 55, 54, 95, 54, 95, 54, 115, 54, 105, 54, 103, 54, 110, 54, 95, 54, 104, 54, 97, 54, 115, 54, 104, 54, 95, 54, 50, 54, 48, 54, 50, 54, 48, 54, 48, 54, 51, 54, 48, 54, 53, 15, 10, 22, 1030, 21, 1087, 22, 4, 3, 13, 9, 55, 54, 95, 54, 95, 54, 115, 54, 105, 54, 103, 54, 110, 54, 95, 54, 104, 54, 97, 54, 115, 54, 104, 54, 95, 54, 50, 54, 48, 54, 50, 54, 48, 54, 48, 54, 51, 54, 48, 54, 53, 15, 3, 9, 9, 11, 3, 3, 9, 11, 38, 25, 1, 13, 4, 61, 11, 9, 3, 12, 9, 11, 3, 10, 3, 53, 3, 37, 39, 24, 2, 13, 4, 4, 55, 54, 122, 54, 122, 54, 97, 3, 11, 9, 11, 38, 3, 10, 9, 11, 38, 0, 49, 771, 2, 1, 12, 9, 13, 8, 3, 12, 4, 4, 56, 0], n);\n" +
                    "    var t = n.__getSecuritySign;\n" +
                    "    return delete n.__getSecuritySign,\n" +
                    "    t\n" +
                    "}\n" +
                    "\n" +
                    "function getSign(id){\n" +
                    "    return getSecurity()(id)\n" +
                    "}\n");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    public String getSign(String data) {
        try {

            return (String) scriptEngine.eval("getSign('" + data + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }

    class QQMusicBean {
        private String name;
        private String url;
        private String singerName;
        private int size;

        public QQMusicBean(String name, String url, String singerNamem, int size) {
            this.name = name;
            this.url = url;
            this.singerName = singerNamem;
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


