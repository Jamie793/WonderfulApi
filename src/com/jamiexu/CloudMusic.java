package com.jamiexu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class CloudMusic {
    //last test time:2020-6-3 13:46
    private final int nums = 30;
    private int currentPos = 0;
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine;
    private ArrayList<CloudMusicBean> arrayList = new ArrayList<>();

    public CloudMusic() {
        execJs();
    }

    private String get(String url, String data, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            if (len != -1)
                httpURLConnection.setRequestProperty("Content-Length", len + "");
            httpURLConnection.setRequestProperty("Host", "music.163.com");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
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

    public CloudMusicBean preSong() {
        if (this.currentPos != 0)
            this.currentPos--;
        return this.arrayList.get(this.currentPos);
    }

    public CloudMusicBean nextSong() {
        if (this.currentPos != this.arrayList.size() - 1)
            this.currentPos++;
        return this.arrayList.get(this.currentPos);
    }

    public CloudMusicBean getCurrent() {
        return this.arrayList.get(this.currentPos);
    }

    public void loadSong(String keyWord) {
        String url = "https://music.163.com/weapi/cloudsearch/get/web?csrf_token=";
        String data = URLEncoder.encode(getSearch(keyWord), StandardCharsets.UTF_8).replace("params%3D", "params=")
                .replace("%26encSecKey%3D", "&encSecKey=");
        String response = get(url, data, data.length());
        try {
            JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String name = jsonObject1.getString("name");
                String id = jsonObject1.getString("id");
                this.arrayList.add(new CloudMusicBean(name, getPlayUrl(id)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPlayUrl(String id) {
        String data = URLEncoder.encode(getPlay(id), StandardCharsets.UTF_8).replace("params%3D", "params=")
                .replace("%26encSecKey%3D", "&encSecKey=");
        String response = get("https://music.163.com/weapi/song/enhance/player/url/v1?csrf_token=", data, data.length());
//        System.out.println(id);
//        System.out.println(data);
//        System.out.println(response);
//        System.out.println("=========================");
        try {
            return new JSONObject(response).getJSONArray("data").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void execJs() {
        try {
            if (engine != null)
                return;
            engine = manager.getEngineByName("javascript");
            engine.eval("window = this;\n" +
                    "var CryptoJS = CryptoJS || function(u, p) {\n" +
                    "        var d = {}, l = d.lib = {}, s = function() {}, t = l.Base = {\n" +
                    "            extend: function(a) {\n" +
                    "                s.prototype = this;\n" +
                    "                var c = new s;\n" +
                    "                a && c.mixIn(a);\n" +
                    "                c.hasOwnProperty(\"init\") || (c.init = function() {\n" +
                    "                    c.$super.init.apply(this, arguments)\n" +
                    "                });\n" +
                    "                c.init.prototype = c;\n" +
                    "                c.$super = this;\n" +
                    "                return c\n" +
                    "            },\n" +
                    "            create: function() {\n" +
                    "                var a = this.extend();\n" +
                    "                a.init.apply(a, arguments);\n" +
                    "                return a\n" +
                    "            },\n" +
                    "            init: function() {},\n" +
                    "            mixIn: function(a) {\n" +
                    "                for (var c in a)\n" +
                    "                a.hasOwnProperty(c) && (this[c] = a[c]);\n" +
                    "                a.hasOwnProperty(\"toString\") && (this.toString = a.toString)\n" +
                    "            },\n" +
                    "            clone: function() {\n" +
                    "                return this.init.prototype.extend(this)\n" +
                    "            }\n" +
                    "        }, r = l.WordArray = t.extend({\n" +
                    "            init: function(a, c) {\n" +
                    "                a = this.words = a || [];\n" +
                    "                this.sigBytes = c != p ? c : 4 * a.length\n" +
                    "            },\n" +
                    "            toString: function(a) {\n" +
                    "                return (a || v).stringify(this)\n" +
                    "            },\n" +
                    "            concat: function(a) {\n" +
                    "                var c = this.words,\n" +
                    "                    e = a.words,\n" +
                    "                    j = this.sigBytes;\n" +
                    "                a = a.sigBytes;\n" +
                    "                this.clamp();\n" +
                    "                if (j % 4) for (var k = 0; k < a; k++)\n" +
                    "                c[j + k >>> 2] |= (e[k >>> 2] >>> 24 - 8 * (k % 4) & 255) << 24 - 8 * ((j + k) % 4);\n" +
                    "                else if (65535 < e.length) for (k = 0; k < a; k += 4)\n" +
                    "                c[j + k >>> 2] = e[k >>> 2];\n" +
                    "                else c.push.apply(c, e);\n" +
                    "                this.sigBytes += a;\n" +
                    "                return this\n" +
                    "            },\n" +
                    "            clamp: function() {\n" +
                    "                var a = this.words,\n" +
                    "                    c = this.sigBytes;\n" +
                    "                a[c >>> 2] &= 4294967295 << 32 - 8 * (c % 4);\n" +
                    "                a.length = u.ceil(c / 4)\n" +
                    "            },\n" +
                    "            clone: function() {\n" +
                    "                var a = t.clone.call(this);\n" +
                    "                a.words = this.words.slice(0);\n" +
                    "                return a\n" +
                    "            },\n" +
                    "            random: function(a) {\n" +
                    "                for (var c = [], e = 0; e < a; e += 4)\n" +
                    "                c.push(4294967296 * u.random() | 0);\n" +
                    "                return new r.init(c, a)\n" +
                    "            }\n" +
                    "        }),\n" +
                    "            w = d.enc = {}, v = w.Hex = {\n" +
                    "                stringify: function(a) {\n" +
                    "                    var c = a.words;\n" +
                    "                    a = a.sigBytes;\n" +
                    "                    for (var e = [], j = 0; j < a; j++) {\n" +
                    "                        var k = c[j >>> 2] >>> 24 - 8 * (j % 4) & 255;\n" +
                    "                        e.push((k >>> 4).toString(16));\n" +
                    "                        e.push((k & 15).toString(16))\n" +
                    "                    }\n" +
                    "                    return e.join(\"\")\n" +
                    "                },\n" +
                    "                parse: function(a) {\n" +
                    "                    for (var c = a.length, e = [], j = 0; j < c; j += 2)\n" +
                    "                    e[j >>> 3] |= parseInt(a.substr(j, 2), 16) << 24 - 4 * (j % 8);\n" +
                    "                    return new r.init(e, c / 2)\n" +
                    "                }\n" +
                    "            }, b = w.Latin1 = {\n" +
                    "                stringify: function(a) {\n" +
                    "                    var c = a.words;\n" +
                    "                    a = a.sigBytes;\n" +
                    "                    for (var e = [], j = 0; j < a; j++)\n" +
                    "                    e.push(String.fromCharCode(c[j >>> 2] >>> 24 - 8 * (j % 4) & 255));\n" +
                    "                    return e.join(\"\")\n" +
                    "                },\n" +
                    "                parse: function(a) {\n" +
                    "                    for (var c = a.length, e = [], j = 0; j < c; j++)\n" +
                    "                    e[j >>> 2] |= (a.charCodeAt(j) & 255) << 24 - 8 * (j % 4);\n" +
                    "                    return new r.init(e, c)\n" +
                    "                }\n" +
                    "            }, x = w.Utf8 = {\n" +
                    "                stringify: function(a) {\n" +
                    "                    try {\n" +
                    "                        return decodeURIComponent(escape(b.stringify(a)))\n" +
                    "                    } catch (c) {\n" +
                    "                        throw Error(\"Malformed UTF-8 data\")\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                parse: function(a) {\n" +
                    "                    return b.parse(unescape(encodeURIComponent(a)))\n" +
                    "                }\n" +
                    "            }, q = l.BufferedBlockAlgorithm = t.extend({\n" +
                    "                reset: function() {\n" +
                    "                    this.i9b = new r.init;\n" +
                    "                    this.ty5D = 0\n" +
                    "                },\n" +
                    "                vb5g: function(a) {\n" +
                    "                    \"string\" == typeof a && (a = x.parse(a));\n" +
                    "                    this.i9b.concat(a);\n" +
                    "                    this.ty5D += a.sigBytes\n" +
                    "                },\n" +
                    "                kY3x: function(a) {\n" +
                    "                    var c = this.i9b,\n" +
                    "                        e = c.words,\n" +
                    "                        j = c.sigBytes,\n" +
                    "                        k = this.blockSize,\n" +
                    "                        b = j / (4 * k),\n" +
                    "                        b = a ? u.ceil(b) : u.max((b | 0) - this.JP0x, 0);\n" +
                    "                    a = b * k;\n" +
                    "                    j = u.min(4 * a, j);\n" +
                    "                    if (a) {\n" +
                    "                        for (var q = 0; q < a; q += k)\n" +
                    "                        this.qL4P(e, q);\n" +
                    "                        q = e.splice(0, a);\n" +
                    "                        c.sigBytes -= j\n" +
                    "                    }\n" +
                    "                    return new r.init(q, j)\n" +
                    "                },\n" +
                    "                clone: function() {\n" +
                    "                    var a = t.clone.call(this);\n" +
                    "                    a.i9b = this.i9b.clone();\n" +
                    "                    return a\n" +
                    "                },\n" +
                    "                JP0x: 0\n" +
                    "            });\n" +
                    "        l.Hasher = q.extend({\n" +
                    "            cfg: t.extend(),\n" +
                    "            init: function(a) {\n" +
                    "                this.cfg = this.cfg.extend(a);\n" +
                    "                this.reset()\n" +
                    "            },\n" +
                    "            reset: function() {\n" +
                    "                q.reset.call(this);\n" +
                    "                this.lt3x()\n" +
                    "            },\n" +
                    "            update: function(a) {\n" +
                    "                this.vb5g(a);\n" +
                    "                this.kY3x();\n" +
                    "                return this\n" +
                    "            },\n" +
                    "            finalize: function(a) {\n" +
                    "                a && this.vb5g(a);\n" +
                    "                return this.mA3x()\n" +
                    "            },\n" +
                    "            blockSize: 16,\n" +
                    "            lS3x: function(a) {\n" +
                    "                return function(b, e) {\n" +
                    "                    return (new a.init(e)).finalize(b)\n" +
                    "                }\n" +
                    "            },\n" +
                    "            vl5q: function(a) {\n" +
                    "                return function(b, e) {\n" +
                    "                    return (new n.HMAC.init(a, e)).finalize(b)\n" +
                    "                }\n" +
                    "            }\n" +
                    "        });\n" +
                    "        var n = d.algo = {};\n" +
                    "        return d\n" +
                    "    }(Math);\n" +
                    "(function() {\n" +
                    "    var u = CryptoJS,\n" +
                    "        p = u.lib.WordArray;\n" +
                    "    u.enc.Base64 = {\n" +
                    "        stringify: function(d) {\n" +
                    "            var l = d.words,\n" +
                    "                p = d.sigBytes,\n" +
                    "                t = this.bA0x;\n" +
                    "            d.clamp();\n" +
                    "            d = [];\n" +
                    "            for (var r = 0; r < p; r += 3)\n" +
                    "            for (var w = (l[r >>> 2] >>> 24 - 8 * (r % 4) & 255) << 16 | (l[r + 1 >>> 2] >>> 24 - 8 * ((r + 1) % 4) & 255) << 8 | l[r + 2 >>> 2] >>> 24 - 8 * ((r + 2) % 4) & 255, v = 0; 4 > v && r + .75 * v < p; v++)\n" +
                    "            d.push(t.charAt(w >>> 6 * (3 - v) & 63));\n" +
                    "            if (l = t.charAt(64)) for (; d.length % 4;)\n" +
                    "            d.push(l);\n" +
                    "            return d.join(\"\")\n" +
                    "        },\n" +
                    "        parse: function(d) {\n" +
                    "            var l = d.length,\n" +
                    "                s = this.bA0x,\n" +
                    "                t = s.charAt(64);\n" +
                    "            t && (t = d.indexOf(t), -1 != t && (l = t));\n" +
                    "            for (var t = [], r = 0, w = 0; w < l; w++)\n" +
                    "            if (w % 4) {\n" +
                    "                var v = s.indexOf(d.charAt(w - 1)) << 2 * (w % 4),\n" +
                    "                    b = s.indexOf(d.charAt(w)) >>> 6 - 2 * (w % 4);\n" +
                    "                t[r >>> 2] |= (v | b) << 24 - 8 * (r % 4);\n" +
                    "                r++\n" +
                    "            }\n" +
                    "            return p.create(t, r)\n" +
                    "        },\n" +
                    "        bA0x: \"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=\"\n" +
                    "    }\n" +
                    "})();\n" +
                    "(function(u) {\n" +
                    "    function p(b, n, a, c, e, j, k) {\n" +
                    "        b = b + (n & a | ~n & c) + e + k;\n" +
                    "        return (b << j | b >>> 32 - j) + n\n" +
                    "    }\n" +
                    "\n" +
                    "    function d(b, n, a, c, e, j, k) {\n" +
                    "        b = b + (n & c | a & ~c) + e + k;\n" +
                    "        return (b << j | b >>> 32 - j) + n\n" +
                    "    }\n" +
                    "\n" +
                    "    function l(b, n, a, c, e, j, k) {\n" +
                    "        b = b + (n ^ a ^ c) + e + k;\n" +
                    "        return (b << j | b >>> 32 - j) + n\n" +
                    "    }\n" +
                    "\n" +
                    "    function s(b, n, a, c, e, j, k) {\n" +
                    "        b = b + (a ^ (n | ~c)) + e + k;\n" +
                    "        return (b << j | b >>> 32 - j) + n\n" +
                    "    }\n" +
                    "    for (var t = CryptoJS, r = t.lib, w = r.WordArray, v = r.Hasher, r = t.algo, b = [], x = 0; 64 > x; x++)\n" +
                    "    b[x] = 4294967296 * u.abs(u.sin(x + 1)) | 0;\n" +
                    "    r = r.MD5 = v.extend({\n" +
                    "        lt3x: function() {\n" +
                    "            this.cN0x = new w.init([1732584193, 4023233417, 2562383102, 271733878])\n" +
                    "        },\n" +
                    "        qL4P: function(q, n) {\n" +
                    "            for (var a = 0; 16 > a; a++) {\n" +
                    "                var c = n + a,\n" +
                    "                    e = q[c];\n" +
                    "                q[c] = (e << 8 | e >>> 24) & 16711935 | (e << 24 | e >>> 8) & 4278255360\n" +
                    "            }\n" +
                    "            var a = this.cN0x.words,\n" +
                    "                c = q[n + 0],\n" +
                    "                e = q[n + 1],\n" +
                    "                j = q[n + 2],\n" +
                    "                k = q[n + 3],\n" +
                    "                z = q[n + 4],\n" +
                    "                r = q[n + 5],\n" +
                    "                t = q[n + 6],\n" +
                    "                w = q[n + 7],\n" +
                    "                v = q[n + 8],\n" +
                    "                A = q[n + 9],\n" +
                    "                B = q[n + 10],\n" +
                    "                C = q[n + 11],\n" +
                    "                u = q[n + 12],\n" +
                    "                D = q[n + 13],\n" +
                    "                E = q[n + 14],\n" +
                    "                x = q[n + 15],\n" +
                    "                f = a[0],\n" +
                    "                m = a[1],\n" +
                    "                g = a[2],\n" +
                    "                h = a[3],\n" +
                    "                f = p(f, m, g, h, c, 7, b[0]),\n" +
                    "                h = p(h, f, m, g, e, 12, b[1]),\n" +
                    "                g = p(g, h, f, m, j, 17, b[2]),\n" +
                    "                m = p(m, g, h, f, k, 22, b[3]),\n" +
                    "                f = p(f, m, g, h, z, 7, b[4]),\n" +
                    "                h = p(h, f, m, g, r, 12, b[5]),\n" +
                    "                g = p(g, h, f, m, t, 17, b[6]),\n" +
                    "                m = p(m, g, h, f, w, 22, b[7]),\n" +
                    "                f = p(f, m, g, h, v, 7, b[8]),\n" +
                    "                h = p(h, f, m, g, A, 12, b[9]),\n" +
                    "                g = p(g, h, f, m, B, 17, b[10]),\n" +
                    "                m = p(m, g, h, f, C, 22, b[11]),\n" +
                    "                f = p(f, m, g, h, u, 7, b[12]),\n" +
                    "                h = p(h, f, m, g, D, 12, b[13]),\n" +
                    "                g = p(g, h, f, m, E, 17, b[14]),\n" +
                    "                m = p(m, g, h, f, x, 22, b[15]),\n" +
                    "                f = d(f, m, g, h, e, 5, b[16]),\n" +
                    "                h = d(h, f, m, g, t, 9, b[17]),\n" +
                    "                g = d(g, h, f, m, C, 14, b[18]),\n" +
                    "                m = d(m, g, h, f, c, 20, b[19]),\n" +
                    "                f = d(f, m, g, h, r, 5, b[20]),\n" +
                    "                h = d(h, f, m, g, B, 9, b[21]),\n" +
                    "                g = d(g, h, f, m, x, 14, b[22]),\n" +
                    "                m = d(m, g, h, f, z, 20, b[23]),\n" +
                    "                f = d(f, m, g, h, A, 5, b[24]),\n" +
                    "                h = d(h, f, m, g, E, 9, b[25]),\n" +
                    "                g = d(g, h, f, m, k, 14, b[26]),\n" +
                    "                m = d(m, g, h, f, v, 20, b[27]),\n" +
                    "                f = d(f, m, g, h, D, 5, b[28]),\n" +
                    "                h = d(h, f, m, g, j, 9, b[29]),\n" +
                    "                g = d(g, h, f, m, w, 14, b[30]),\n" +
                    "                m = d(m, g, h, f, u, 20, b[31]),\n" +
                    "                f = l(f, m, g, h, r, 4, b[32]),\n" +
                    "                h = l(h, f, m, g, v, 11, b[33]),\n" +
                    "                g = l(g, h, f, m, C, 16, b[34]),\n" +
                    "                m = l(m, g, h, f, E, 23, b[35]),\n" +
                    "                f = l(f, m, g, h, e, 4, b[36]),\n" +
                    "                h = l(h, f, m, g, z, 11, b[37]),\n" +
                    "                g = l(g, h, f, m, w, 16, b[38]),\n" +
                    "                m = l(m, g, h, f, B, 23, b[39]),\n" +
                    "                f = l(f, m, g, h, D, 4, b[40]),\n" +
                    "                h = l(h, f, m, g, c, 11, b[41]),\n" +
                    "                g = l(g, h, f, m, k, 16, b[42]),\n" +
                    "                m = l(m, g, h, f, t, 23, b[43]),\n" +
                    "                f = l(f, m, g, h, A, 4, b[44]),\n" +
                    "                h = l(h, f, m, g, u, 11, b[45]),\n" +
                    "                g = l(g, h, f, m, x, 16, b[46]),\n" +
                    "                m = l(m, g, h, f, j, 23, b[47]),\n" +
                    "                f = s(f, m, g, h, c, 6, b[48]),\n" +
                    "                h = s(h, f, m, g, w, 10, b[49]),\n" +
                    "                g = s(g, h, f, m, E, 15, b[50]),\n" +
                    "                m = s(m, g, h, f, r, 21, b[51]),\n" +
                    "                f = s(f, m, g, h, u, 6, b[52]),\n" +
                    "                h = s(h, f, m, g, k, 10, b[53]),\n" +
                    "                g = s(g, h, f, m, B, 15, b[54]),\n" +
                    "                m = s(m, g, h, f, e, 21, b[55]),\n" +
                    "                f = s(f, m, g, h, v, 6, b[56]),\n" +
                    "                h = s(h, f, m, g, x, 10, b[57]),\n" +
                    "                g = s(g, h, f, m, t, 15, b[58]),\n" +
                    "                m = s(m, g, h, f, D, 21, b[59]),\n" +
                    "                f = s(f, m, g, h, z, 6, b[60]),\n" +
                    "                h = s(h, f, m, g, C, 10, b[61]),\n" +
                    "                g = s(g, h, f, m, j, 15, b[62]),\n" +
                    "                m = s(m, g, h, f, A, 21, b[63]);\n" +
                    "            a[0] = a[0] + f | 0;\n" +
                    "            a[1] = a[1] + m | 0;\n" +
                    "            a[2] = a[2] + g | 0;\n" +
                    "            a[3] = a[3] + h | 0\n" +
                    "        },\n" +
                    "        mA3x: function() {\n" +
                    "            var b = this.i9b,\n" +
                    "                n = b.words,\n" +
                    "                a = 8 * this.ty5D,\n" +
                    "                c = 8 * b.sigBytes;\n" +
                    "            n[c >>> 5] |= 128 << 24 - c % 32;\n" +
                    "            var e = u.floor(a / 4294967296);\n" +
                    "            n[(c + 64 >>> 9 << 4) + 15] = (e << 8 | e >>> 24) & 16711935 | (e << 24 | e >>> 8) & 4278255360;\n" +
                    "            n[(c + 64 >>> 9 << 4) + 14] = (a << 8 | a >>> 24) & 16711935 | (a << 24 | a >>> 8) & 4278255360;\n" +
                    "            b.sigBytes = 4 * (n.length + 1);\n" +
                    "            this.kY3x();\n" +
                    "            b = this.cN0x;\n" +
                    "            n = b.words;\n" +
                    "            for (a = 0; 4 > a; a++)\n" +
                    "            c = n[a],\n" +
                    "            n[a] = (c << 8 | c >>> 24) & 16711935 | (c << 24 | c >>> 8) & 4278255360;\n" +
                    "            return b\n" +
                    "        },\n" +
                    "        clone: function() {\n" +
                    "            var b = v.clone.call(this);\n" +
                    "            b.cN0x = this.cN0x.clone();\n" +
                    "            return b\n" +
                    "        }\n" +
                    "    });\n" +
                    "    t.MD5 = v.lS3x(r);\n" +
                    "    t.HmacMD5 = v.vl5q(r)\n" +
                    "})(Math);\n" +
                    "(function() {\n" +
                    "    var u = CryptoJS,\n" +
                    "        p = u.lib,\n" +
                    "        d = p.Base,\n" +
                    "        l = p.WordArray,\n" +
                    "        p = u.algo,\n" +
                    "        s = p.EvpKDF = d.extend({\n" +
                    "            cfg: d.extend({\n" +
                    "                keySize: 4,\n" +
                    "                hasher: p.MD5,\n" +
                    "                iterations: 1\n" +
                    "            }),\n" +
                    "            init: function(d) {\n" +
                    "                this.cfg = this.cfg.extend(d)\n" +
                    "            },\n" +
                    "            compute: function(d, r) {\n" +
                    "                for (var p = this.cfg, s = p.hasher.create(), b = l.create(), u = b.words, q = p.keySize, p = p.iterations; u.length < q;) {\n" +
                    "                    n && s.update(n);\n" +
                    "                    var n = s.update(d).finalize(r);\n" +
                    "                    s.reset();\n" +
                    "                    for (var a = 1; a < p; a++)\n" +
                    "                    n = s.finalize(n),\n" +
                    "                    s.reset();\n" +
                    "                    b.concat(n)\n" +
                    "                }\n" +
                    "                b.sigBytes = 4 * q;\n" +
                    "                return b\n" +
                    "            }\n" +
                    "        });\n" +
                    "    u.EvpKDF = function(d, l, p) {\n" +
                    "        return s.create(p).compute(d, l)\n" +
                    "    }\n" +
                    "})();\n" +
                    "CryptoJS.lib.Cipher || function(u) {\n" +
                    "    var p = CryptoJS,\n" +
                    "        d = p.lib,\n" +
                    "        l = d.Base,\n" +
                    "        s = d.WordArray,\n" +
                    "        t = d.BufferedBlockAlgorithm,\n" +
                    "        r = p.enc.Base64,\n" +
                    "        w = p.algo.EvpKDF,\n" +
                    "        v = d.Cipher = t.extend({\n" +
                    "            cfg: l.extend(),\n" +
                    "            createEncryptor: function(e, a) {\n" +
                    "                return this.create(this.JY0x, e, a)\n" +
                    "            },\n" +
                    "            createDecryptor: function(e, a) {\n" +
                    "                return this.create(this.bqV9M, e, a)\n" +
                    "            },\n" +
                    "            init: function(e, a, b) {\n" +
                    "                this.cfg = this.cfg.extend(b);\n" +
                    "                this.Qq2x = e;\n" +
                    "                this.L9C = a;\n" +
                    "                this.reset()\n" +
                    "            },\n" +
                    "            reset: function() {\n" +
                    "                t.reset.call(this);\n" +
                    "                this.lt3x()\n" +
                    "            },\n" +
                    "            process: function(e) {\n" +
                    "                this.vb5g(e);\n" +
                    "                return this.kY3x()\n" +
                    "            },\n" +
                    "            finalize: function(e) {\n" +
                    "                e && this.vb5g(e);\n" +
                    "                return this.mA3x()\n" +
                    "            },\n" +
                    "            keySize: 4,\n" +
                    "            ivSize: 4,\n" +
                    "            JY0x: 1,\n" +
                    "            bqV9M: 2,\n" +
                    "            lS3x: function(e) {\n" +
                    "                return {\n" +
                    "                    encrypt: function(b, k, d) {\n" +
                    "                        return (\"string\" == typeof k ? c : a).encrypt(e, b, k, d)\n" +
                    "                    },\n" +
                    "                    decrypt: function(b, k, d) {\n" +
                    "                        return (\"string\" == typeof k ? c : a).decrypt(e, b, k, d)\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        });\n" +
                    "    d.StreamCipher = v.extend({\n" +
                    "        mA3x: function() {\n" +
                    "            return this.kY3x(!0)\n" +
                    "        },\n" +
                    "        blockSize: 1\n" +
                    "    });\n" +
                    "    var b = p.mode = {}, x = function(e, a, b) {\n" +
                    "        var c = this.tw5B;\n" +
                    "        c ? this.tw5B = u : c = this.DB8t;\n" +
                    "        for (var d = 0; d < b; d++)\n" +
                    "        e[a + d] ^= c[d]\n" +
                    "    }, q = (d.BlockCipherMode = l.extend({\n" +
                    "        createEncryptor: function(e, a) {\n" +
                    "            return this.Encryptor.create(e, a)\n" +
                    "        },\n" +
                    "        createDecryptor: function(e, a) {\n" +
                    "            return this.Decryptor.create(e, a)\n" +
                    "        },\n" +
                    "        init: function(e, a) {\n" +
                    "            this.vw6q = e;\n" +
                    "            this.tw5B = a\n" +
                    "        }\n" +
                    "    })).extend();\n" +
                    "    q.Encryptor = q.extend({\n" +
                    "        processBlock: function(e, a) {\n" +
                    "            var b = this.vw6q,\n" +
                    "                c = b.blockSize;\n" +
                    "            x.call(this, e, a, c);\n" +
                    "            b.encryptBlock(e, a);\n" +
                    "            this.DB8t = e.slice(a, a + c)\n" +
                    "        }\n" +
                    "    });\n" +
                    "    q.Decryptor = q.extend({\n" +
                    "        processBlock: function(e, a) {\n" +
                    "            var b = this.vw6q,\n" +
                    "                c = b.blockSize,\n" +
                    "                d = e.slice(a, a + c);\n" +
                    "            b.decryptBlock(e, a);\n" +
                    "            x.call(this, e, a, c);\n" +
                    "            this.DB8t = d\n" +
                    "        }\n" +
                    "    });\n" +
                    "    b = b.CBC = q;\n" +
                    "    q = (p.pad = {}).Pkcs7 = {\n" +
                    "        pad: function(a, b) {\n" +
                    "            for (var c = 4 * b, c = c - a.sigBytes % c, d = c << 24 | c << 16 | c << 8 | c, l = [], n = 0; n < c; n += 4)\n" +
                    "            l.push(d);\n" +
                    "            c = s.create(l, c);\n" +
                    "            a.concat(c)\n" +
                    "        },\n" +
                    "        unpad: function(a) {\n" +
                    "            a.sigBytes -= a.words[a.sigBytes - 1 >>> 2] & 255\n" +
                    "        }\n" +
                    "    };\n" +
                    "    d.BlockCipher = v.extend({\n" +
                    "        cfg: v.cfg.extend({\n" +
                    "            mode: b,\n" +
                    "            padding: q\n" +
                    "        }),\n" +
                    "        reset: function() {\n" +
                    "            v.reset.call(this);\n" +
                    "            var a = this.cfg,\n" +
                    "                b = a.iv,\n" +
                    "                a = a.mode;\n" +
                    "            if (this.Qq2x == this.JY0x) var c = a.createEncryptor;\n" +
                    "            else c = a.createDecryptor,\n" +
                    "            this.JP0x = 1;\n" +
                    "            this.eT1x = c.call(a, this, b && b.words)\n" +
                    "        },\n" +
                    "        qL4P: function(a, b) {\n" +
                    "            this.eT1x.processBlock(a, b)\n" +
                    "        },\n" +
                    "        mA3x: function() {\n" +
                    "            var a = this.cfg.padding;\n" +
                    "            if (this.Qq2x == this.JY0x) {\n" +
                    "                a.pad(this.i9b, this.blockSize);\n" +
                    "                var b = this.kY3x(!0)\n" +
                    "            } else b = this.kY3x(!0),\n" +
                    "            a.unpad(b);\n" +
                    "            return b\n" +
                    "        },\n" +
                    "        blockSize: 4\n" +
                    "    });\n" +
                    "    var n = d.CipherParams = l.extend({\n" +
                    "        init: function(a) {\n" +
                    "            this.mixIn(a)\n" +
                    "        },\n" +
                    "        toString: function(a) {\n" +
                    "            return (a || this.formatter).stringify(this)\n" +
                    "        }\n" +
                    "    }),\n" +
                    "        b = (p.format = {}).OpenSSL = {\n" +
                    "            stringify: function(a) {\n" +
                    "                var b = a.ciphertext;\n" +
                    "                a = a.salt;\n" +
                    "                return (a ? s.create([1398893684, 1701076831]).concat(a).concat(b) : b).toString(r)\n" +
                    "            },\n" +
                    "            parse: function(a) {\n" +
                    "                a = r.parse(a);\n" +
                    "                var b = a.words;\n" +
                    "                if (1398893684 == b[0] && 1701076831 == b[1]) {\n" +
                    "                    var c = s.create(b.slice(2, 4));\n" +
                    "                    b.splice(0, 4);\n" +
                    "                    a.sigBytes -= 16\n" +
                    "                }\n" +
                    "                return n.create({\n" +
                    "                    ciphertext: a,\n" +
                    "                    salt: c\n" +
                    "                })\n" +
                    "            }\n" +
                    "        }, a = d.SerializableCipher = l.extend({\n" +
                    "            cfg: l.extend({\n" +
                    "                format: b\n" +
                    "            }),\n" +
                    "            encrypt: function(a, b, c, d) {\n" +
                    "                d = this.cfg.extend(d);\n" +
                    "                var l = a.createEncryptor(c, d);\n" +
                    "                b = l.finalize(b);\n" +
                    "                l = l.cfg;\n" +
                    "                return n.create({\n" +
                    "                    ciphertext: b,\n" +
                    "                    key: c,\n" +
                    "                    iv: l.iv,\n" +
                    "                    algorithm: a,\n" +
                    "                    mode: l.mode,\n" +
                    "                    padding: l.padding,\n" +
                    "                    blockSize: a.blockSize,\n" +
                    "                    formatter: d.format\n" +
                    "                })\n" +
                    "            },\n" +
                    "            decrypt: function(a, b, c, d) {\n" +
                    "                d = this.cfg.extend(d);\n" +
                    "                b = this.Hj9a(b, d.format);\n" +
                    "                return a.createDecryptor(c, d).finalize(b.ciphertext)\n" +
                    "            },\n" +
                    "            Hj9a: function(a, b) {\n" +
                    "                return \"string\" == typeof a ? b.parse(a, this) : a\n" +
                    "            }\n" +
                    "        }),\n" +
                    "        p = (p.kdf = {}).OpenSSL = {\n" +
                    "            execute: function(a, b, c, d) {\n" +
                    "                d || (d = s.random(8));\n" +
                    "                a = w.create({\n" +
                    "                    keySize: b + c\n" +
                    "                }).compute(a, d);\n" +
                    "                c = s.create(a.words.slice(b), 4 * c);\n" +
                    "                a.sigBytes = 4 * b;\n" +
                    "                return n.create({\n" +
                    "                    key: a,\n" +
                    "                    iv: c,\n" +
                    "                    salt: d\n" +
                    "                })\n" +
                    "            }\n" +
                    "        }, c = d.PasswordBasedCipher = a.extend({\n" +
                    "            cfg: a.cfg.extend({\n" +
                    "                kdf: p\n" +
                    "            }),\n" +
                    "            encrypt: function(b, c, d, l) {\n" +
                    "                l = this.cfg.extend(l);\n" +
                    "                d = l.kdf.execute(d, b.keySize, b.ivSize);\n" +
                    "                l.iv = d.iv;\n" +
                    "                b = a.encrypt.call(this, b, c, d.key, l);\n" +
                    "                b.mixIn(d);\n" +
                    "                return b\n" +
                    "            },\n" +
                    "            decrypt: function(b, c, d, l) {\n" +
                    "                l = this.cfg.extend(l);\n" +
                    "                c = this.Hj9a(c, l.format);\n" +
                    "                d = l.kdf.execute(d, b.keySize, b.ivSize, c.salt);\n" +
                    "                l.iv = d.iv;\n" +
                    "                return a.decrypt.call(this, b, c, d.key, l)\n" +
                    "            }\n" +
                    "        })\n" +
                    "}();\n" +
                    "(function() {\n" +
                    "    for (var u = CryptoJS, p = u.lib.BlockCipher, d = u.algo, l = [], s = [], t = [], r = [], w = [], v = [], b = [], x = [], q = [], n = [], a = [], c = 0; 256 > c; c++)\n" +
                    "    a[c] = 128 > c ? c << 1 : c << 1 ^ 283;\n" +
                    "    for (var e = 0, j = 0, c = 0; 256 > c; c++) {\n" +
                    "        var k = j ^ j << 1 ^ j << 2 ^ j << 3 ^ j << 4,\n" +
                    "            k = k >>> 8 ^ k & 255 ^ 99;\n" +
                    "        l[e] = k;\n" +
                    "        s[k] = e;\n" +
                    "        var z = a[e],\n" +
                    "            F = a[z],\n" +
                    "            G = a[F],\n" +
                    "            y = 257 * a[k] ^ 16843008 * k;\n" +
                    "        t[e] = y << 24 | y >>> 8;\n" +
                    "        r[e] = y << 16 | y >>> 16;\n" +
                    "        w[e] = y << 8 | y >>> 24;\n" +
                    "        v[e] = y;\n" +
                    "        y = 16843009 * G ^ 65537 * F ^ 257 * z ^ 16843008 * e;\n" +
                    "        b[k] = y << 24 | y >>> 8;\n" +
                    "        x[k] = y << 16 | y >>> 16;\n" +
                    "        q[k] = y << 8 | y >>> 24;\n" +
                    "        n[k] = y;\n" +
                    "        e ? (e = z ^ a[a[a[G ^ z]]],\n" +
                    "        j ^= a[a[j]]) : e = j = 1\n" +
                    "    }\n" +
                    "    var H = [0, 1, 2, 4, 8, 16, 32, 64, 128, 27, 54],\n" +
                    "        d = d.AES = p.extend({\n" +
                    "            lt3x: function() {\n" +
                    "                for (var a = this.L9C, c = a.words, d = a.sigBytes / 4, a = 4 * ((this.beT7M = d + 6) + 1), e = this.bqT9K = [], j = 0; j < a; j++)\n" +
                    "                if (j < d) e[j] = c[j];\n" +
                    "                else {\n" +
                    "                    var k = e[j - 1];\n" +
                    "                    j % d ? 6 < d && 4 == j % d && (k = l[k >>> 24] << 24 | l[k >>> 16 & 255] << 16 | l[k >>> 8 & 255] << 8 | l[k & 255]) : (k = k << 8 | k >>> 24,\n" +
                    "                    k = l[k >>> 24] << 24 | l[k >>> 16 & 255] << 16 | l[k >>> 8 & 255] << 8 | l[k & 255],\n" +
                    "                    k ^= H[j / d | 0] << 24);\n" +
                    "                    e[j] = e[j - d] ^ k\n" +
                    "                }\n" +
                    "                c = this.bqS9J = [];\n" +
                    "                for (d = 0; d < a; d++)\n" +
                    "                j = a - d,\n" +
                    "                k = d % 4 ? e[j] : e[j - 4],\n" +
                    "                c[d] = 4 > d || 4 >= j ? k : b[l[k >>> 24]] ^ x[l[k >>> 16 & 255]] ^ q[l[k >>> 8 & 255]] ^ n[l[k & 255]]\n" +
                    "            },\n" +
                    "            encryptBlock: function(a, b) {\n" +
                    "                this.DA8s(a, b, this.bqT9K, t, r, w, v, l)\n" +
                    "            },\n" +
                    "            decryptBlock: function(a, c) {\n" +
                    "                var d = a[c + 1];\n" +
                    "                a[c + 1] = a[c + 3];\n" +
                    "                a[c + 3] = d;\n" +
                    "                this.DA8s(a, c, this.bqS9J, b, x, q, n, s);\n" +
                    "                d = a[c + 1];\n" +
                    "                a[c + 1] = a[c + 3];\n" +
                    "                a[c + 3] = d\n" +
                    "            },\n" +
                    "            DA8s: function(a, b, c, d, e, j, l, f) {\n" +
                    "                for (var m = this.beT7M, g = a[b] ^ c[0], h = a[b + 1] ^ c[1], k = a[b + 2] ^ c[2], n = a[b + 3] ^ c[3], p = 4, r = 1; r < m; r++)\n" +
                    "                var q = d[g >>> 24] ^ e[h >>> 16 & 255] ^ j[k >>> 8 & 255] ^ l[n & 255] ^ c[p++],\n" +
                    "                    s = d[h >>> 24] ^ e[k >>> 16 & 255] ^ j[n >>> 8 & 255] ^ l[g & 255] ^ c[p++],\n" +
                    "                    t = d[k >>> 24] ^ e[n >>> 16 & 255] ^ j[g >>> 8 & 255] ^ l[h & 255] ^ c[p++],\n" +
                    "                    n = d[n >>> 24] ^ e[g >>> 16 & 255] ^ j[h >>> 8 & 255] ^ l[k & 255] ^ c[p++],\n" +
                    "                    g = q,\n" +
                    "                    h = s,\n" +
                    "                    k = t;\n" +
                    "                q = (f[g >>> 24] << 24 | f[h >>> 16 & 255] << 16 | f[k >>> 8 & 255] << 8 | f[n & 255]) ^ c[p++];\n" +
                    "                s = (f[h >>> 24] << 24 | f[k >>> 16 & 255] << 16 | f[n >>> 8 & 255] << 8 | f[g & 255]) ^ c[p++];\n" +
                    "                t = (f[k >>> 24] << 24 | f[n >>> 16 & 255] << 16 | f[g >>> 8 & 255] << 8 | f[h & 255]) ^ c[p++];\n" +
                    "                n = (f[n >>> 24] << 24 | f[g >>> 16 & 255] << 16 | f[h >>> 8 & 255] << 8 | f[k & 255]) ^ c[p++];\n" +
                    "                a[b] = q;\n" +
                    "                a[b + 1] = s;\n" +
                    "                a[b + 2] = t;\n" +
                    "                a[b + 3] = n\n" +
                    "            },\n" +
                    "            keySize: 8\n" +
                    "        });\n" +
                    "    u.AES = p.lS3x(d)\n" +
                    "})();\n" +
                    "\n" +
                    "function RSAKeyPair(a, b, c) {\n" +
                    "    this.e = biFromHex(a),\n" +
                    "    this.d = biFromHex(b),\n" +
                    "    this.m = biFromHex(c),\n" +
                    "    this.chunkSize = 2 * biHighIndex(this.m),\n" +
                    "    this.radix = 16,\n" +
                    "    this.barrett = new BarrettMu(this.m)\n" +
                    "}\n" +
                    "\n" +
                    "function twoDigit(a) {\n" +
                    "    return (10 > a ? \"0\" : \"\") + String(a)\n" +
                    "}\n" +
                    "\n" +
                    "function encryptedString(a, b) {\n" +
                    "    for (var f, g, h, i, j, k, l, c = new Array, d = b.length, e = 0; d > e;)\n" +
                    "    c[e] = b.charCodeAt(e),\n" +
                    "    e++;\n" +
                    "    for (; 0 != c.length % a.chunkSize;)\n" +
                    "    c[e++] = 0;\n" +
                    "    for (f = c.length,\n" +
                    "    g = \"\",\n" +
                    "    e = 0; f > e; e += a.chunkSize) {\n" +
                    "        for (j = new BigInt,\n" +
                    "        h = 0,\n" +
                    "        i = e; i < e + a.chunkSize; ++h)\n" +
                    "        j.digits[h] = c[i++],\n" +
                    "        j.digits[h] += c[i++] << 8;\n" +
                    "        k = a.barrett.powMod(j, a.e),\n" +
                    "        l = 16 == a.radix ? biToHex(k) : biToString(k, a.radix),\n" +
                    "        g += l + \" \"\n" +
                    "    }\n" +
                    "    return g.substring(0, g.length - 1)\n" +
                    "}\n" +
                    "\n" +
                    "function decryptedString(a, b) {\n" +
                    "    var e, f, g, h, c = b.split(\" \"),\n" +
                    "        d = \"\";\n" +
                    "    for (e = 0; e < c.length; ++e)\n" +
                    "    for (h = 16 == a.radix ? biFromHex(c[e]) : biFromString(c[e], a.radix),\n" +
                    "    g = a.barrett.powMod(h, a.d),\n" +
                    "    f = 0; f <= biHighIndex(g); ++f)\n" +
                    "    d += String.fromCharCode(255 & g.digits[f], g.digits[f] >> 8);\n" +
                    "    return 0 == d.charCodeAt(d.length - 1) && (d = d.substring(0, d.length - 1)),\n" +
                    "    d\n" +
                    "}\n" +
                    "\n" +
                    "function setMaxDigits(a) {\n" +
                    "    maxDigits = a,\n" +
                    "    ZERO_ARRAY = new Array(maxDigits);\n" +
                    "    for (var b = 0; b < ZERO_ARRAY.length; b++)\n" +
                    "    ZERO_ARRAY[b] = 0;\n" +
                    "    bigZero = new BigInt,\n" +
                    "    bigOne = new BigInt,\n" +
                    "    bigOne.digits[0] = 1\n" +
                    "}\n" +
                    "\n" +
                    "function BigInt(a) {\n" +
                    "    this.digits = \"boolean\" == typeof a && 1 == a ? null : ZERO_ARRAY.slice(0),\n" +
                    "    this.isNeg = !1\n" +
                    "}\n" +
                    "\n" +
                    "function biFromDecimal(a) {\n" +
                    "    for (var d, e, f, b = \"-\" == a.charAt(0), c = b ? 1 : 0; c < a.length && \"0\" == a.charAt(c);)++c;\n" +
                    "    if (c == a.length) d = new BigInt;\n" +
                    "    else {\n" +
                    "        for (e = a.length - c,\n" +
                    "        f = e % dpl10,\n" +
                    "        0 == f && (f = dpl10),\n" +
                    "        d = biFromNumber(Number(a.substr(c, f))),\n" +
                    "        c += f; c < a.length;)\n" +
                    "        d = biAdd(biMultiply(d, lr10), biFromNumber(Number(a.substr(c, dpl10)))),\n" +
                    "        c += dpl10;\n" +
                    "        d.isNeg = b\n" +
                    "    }\n" +
                    "    return d\n" +
                    "}\n" +
                    "\n" +
                    "function biCopy(a) {\n" +
                    "    var b = new BigInt(!0);\n" +
                    "    return b.digits = a.digits.slice(0),\n" +
                    "    b.isNeg = a.isNeg,\n" +
                    "    b\n" +
                    "}\n" +
                    "\n" +
                    "function biFromNumber(a) {\n" +
                    "    var c, b = new BigInt;\n" +
                    "    for (b.isNeg = 0 > a,\n" +
                    "    a = Math.abs(a),\n" +
                    "    c = 0; a > 0;)\n" +
                    "    b.digits[c++] = a & maxDigitVal,\n" +
                    "    a >>= biRadixBits;\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function reverseStr(a) {\n" +
                    "    var c, b = \"\";\n" +
                    "    for (c = a.length - 1; c > -1; --c)\n" +
                    "    b += a.charAt(c);\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function biToString(a, b) {\n" +
                    "    var d, e, c = new BigInt;\n" +
                    "    for (c.digits[0] = b,\n" +
                    "    d = biDivideModulo(a, c),\n" +
                    "    e = hexatrigesimalToChar[d[1].digits[0]]; 1 == biCompare(d[0], bigZero);)\n" +
                    "    d = biDivideModulo(d[0], c),\n" +
                    "    digit = d[1].digits[0],\n" +
                    "    e += hexatrigesimalToChar[d[1].digits[0]];\n" +
                    "    return (a.isNeg ? \"-\" : \"\") + reverseStr(e)\n" +
                    "}\n" +
                    "\n" +
                    "function biToDecimal(a) {\n" +
                    "    var c, d, b = new BigInt;\n" +
                    "    for (b.digits[0] = 10,\n" +
                    "    c = biDivideModulo(a, b),\n" +
                    "    d = String(c[1].digits[0]); 1 == biCompare(c[0], bigZero);)\n" +
                    "    c = biDivideModulo(c[0], b),\n" +
                    "    d += String(c[1].digits[0]);\n" +
                    "    return (a.isNeg ? \"-\" : \"\") + reverseStr(d)\n" +
                    "}\n" +
                    "\n" +
                    "function digitToHex(a) {\n" +
                    "    var b = 15,\n" +
                    "        c = \"\";\n" +
                    "    for (i = 0; 4 > i; ++i)\n" +
                    "    c += hexToChar[a & b],\n" +
                    "    a >>>= 4;\n" +
                    "    return reverseStr(c)\n" +
                    "}\n" +
                    "\n" +
                    "function biToHex(a) {\n" +
                    "    var d, b = \"\";\n" +
                    "    for (biHighIndex(a),\n" +
                    "    d = biHighIndex(a); d > -1; --d)\n" +
                    "    b += digitToHex(a.digits[d]);\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function charToHex(a) {\n" +
                    "    var h, b = 48,\n" +
                    "        c = b + 9,\n" +
                    "        d = 97,\n" +
                    "        e = d + 25,\n" +
                    "        f = 65,\n" +
                    "        g = 90;\n" +
                    "    return h = a >= b && c >= a ? a - b : a >= f && g >= a ? 10 + a - f : a >= d && e >= a ? 10 + a - d : 0\n" +
                    "}\n" +
                    "\n" +
                    "function hexToDigit(a) {\n" +
                    "    var d, b = 0,\n" +
                    "        c = Math.min(a.length, 4);\n" +
                    "    for (d = 0; c > d; ++d)\n" +
                    "    b <<= 4,\n" +
                    "    b |= charToHex(a.charCodeAt(d));\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function biFromHex(a) {\n" +
                    "    var d, e, b = new BigInt,\n" +
                    "        c = a.length;\n" +
                    "    for (d = c,\n" +
                    "    e = 0; d > 0; d -= 4, ++e)\n" +
                    "    b.digits[e] = hexToDigit(a.substr(Math.max(d - 4, 0), Math.min(d, 4)));\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function biFromString(a, b) {\n" +
                    "    var g, h, i, j, c = \"-\" == a.charAt(0),\n" +
                    "        d = c ? 1 : 0,\n" +
                    "        e = new BigInt,\n" +
                    "        f = new BigInt;\n" +
                    "    for (f.digits[0] = 1,\n" +
                    "    g = a.length - 1; g >= d; g--)\n" +
                    "    h = a.charCodeAt(g),\n" +
                    "    i = charToHex(h),\n" +
                    "    j = biMultiplyDigit(f, i),\n" +
                    "    e = biAdd(e, j),\n" +
                    "    f = biMultiplyDigit(f, b);\n" +
                    "    return e.isNeg = c,\n" +
                    "    e\n" +
                    "}\n" +
                    "\n" +
                    "function biDump(a) {\n" +
                    "    return (a.isNeg ? \"-\" : \"\") + a.digits.join(\" \")\n" +
                    "}\n" +
                    "\n" +
                    "function biAdd(a, b) {\n" +
                    "    var c, d, e, f;\n" +
                    "    if (a.isNeg != b.isNeg) b.isNeg = !b.isNeg,\n" +
                    "    c = biSubtract(a, b),\n" +
                    "    b.isNeg = !b.isNeg;\n" +
                    "    else {\n" +
                    "        for (c = new BigInt,\n" +
                    "        d = 0,\n" +
                    "        f = 0; f < a.digits.length; ++f)\n" +
                    "        e = a.digits[f] + b.digits[f] + d,\n" +
                    "        c.digits[f] = 65535 & e,\n" +
                    "        d = Number(e >= biRadix);\n" +
                    "        c.isNeg = a.isNeg\n" +
                    "    }\n" +
                    "    return c\n" +
                    "}\n" +
                    "\n" +
                    "function biSubtract(a, b) {\n" +
                    "    var c, d, e, f;\n" +
                    "    if (a.isNeg != b.isNeg) b.isNeg = !b.isNeg,\n" +
                    "    c = biAdd(a, b),\n" +
                    "    b.isNeg = !b.isNeg;\n" +
                    "    else {\n" +
                    "        for (c = new BigInt,\n" +
                    "        e = 0,\n" +
                    "        f = 0; f < a.digits.length; ++f)\n" +
                    "        d = a.digits[f] - b.digits[f] + e,\n" +
                    "        c.digits[f] = 65535 & d,\n" +
                    "        c.digits[f] < 0 && (c.digits[f] += biRadix),\n" +
                    "        e = 0 - Number(0 > d);\n" +
                    "        if (-1 == e) {\n" +
                    "            for (e = 0,\n" +
                    "            f = 0; f < a.digits.length; ++f)\n" +
                    "            d = 0 - c.digits[f] + e,\n" +
                    "            c.digits[f] = 65535 & d,\n" +
                    "            c.digits[f] < 0 && (c.digits[f] += biRadix),\n" +
                    "            e = 0 - Number(0 > d);\n" +
                    "            c.isNeg = !a.isNeg\n" +
                    "        } else c.isNeg = a.isNeg\n" +
                    "    }\n" +
                    "    return c\n" +
                    "}\n" +
                    "\n" +
                    "function biHighIndex(a) {\n" +
                    "    for (var b = a.digits.length - 1; b > 0 && 0 == a.digits[b];)--b;\n" +
                    "    return b\n" +
                    "}\n" +
                    "\n" +
                    "function biNumBits(a) {\n" +
                    "    var e, b = biHighIndex(a),\n" +
                    "        c = a.digits[b],\n" +
                    "        d = (b + 1) * bitsPerDigit;\n" +
                    "    for (e = d; e > d - bitsPerDigit && 0 == (32768 & c); --e)\n" +
                    "    c <<= 1;\n" +
                    "    return e\n" +
                    "}\n" +
                    "\n" +
                    "function biMultiply(a, b) {\n" +
                    "    var d, h, i, k, c = new BigInt,\n" +
                    "        e = biHighIndex(a),\n" +
                    "        f = biHighIndex(b);\n" +
                    "    for (k = 0; f >= k; ++k) {\n" +
                    "        for (d = 0,\n" +
                    "        i = k,\n" +
                    "        j = 0; e >= j; ++j, ++i)\n" +
                    "        h = c.digits[i] + a.digits[j] * b.digits[k] + d,\n" +
                    "        c.digits[i] = h & maxDigitVal,\n" +
                    "        d = h >>> biRadixBits;\n" +
                    "        c.digits[k + e + 1] = d\n" +
                    "    }\n" +
                    "    return c.isNeg = a.isNeg != b.isNeg,\n" +
                    "    c\n" +
                    "}\n" +
                    "\n" +
                    "function biMultiplyDigit(a, b) {\n" +
                    "    var c, d, e, f;\n" +
                    "    for (result = new BigInt,\n" +
                    "    c = biHighIndex(a),\n" +
                    "    d = 0,\n" +
                    "    f = 0; c >= f; ++f)\n" +
                    "    e = result.digits[f] + a.digits[f] * b + d,\n" +
                    "    result.digits[f] = e & maxDigitVal,\n" +
                    "    d = e >>> biRadixBits;\n" +
                    "    return result.digits[1 + c] = d,\n" +
                    "    result\n" +
                    "}\n" +
                    "\n" +
                    "function arrayCopy(a, b, c, d, e) {\n" +
                    "    var g, h, f = Math.min(b + e, a.length);\n" +
                    "    for (g = b,\n" +
                    "    h = d; f > g; ++g, ++h)\n" +
                    "    c[h] = a[g]\n" +
                    "}\n" +
                    "\n" +
                    "function biShiftLeft(a, b) {\n" +
                    "    var e, f, g, h, c = Math.floor(b / bitsPerDigit),\n" +
                    "        d = new BigInt;\n" +
                    "    for (arrayCopy(a.digits, 0, d.digits, c, d.digits.length - c),\n" +
                    "    e = b % bitsPerDigit,\n" +
                    "    f = bitsPerDigit - e,\n" +
                    "    g = d.digits.length - 1,\n" +
                    "    h = g - 1; g > 0; --g, --h)\n" +
                    "    d.digits[g] = d.digits[g] << e & maxDigitVal | (d.digits[h] & highBitMasks[e]) >>> f;\n" +
                    "    return d.digits[0] = d.digits[g] << e & maxDigitVal,\n" +
                    "    d.isNeg = a.isNeg,\n" +
                    "    d\n" +
                    "}\n" +
                    "\n" +
                    "function biShiftRight(a, b) {\n" +
                    "    var e, f, g, h, c = Math.floor(b / bitsPerDigit),\n" +
                    "        d = new BigInt;\n" +
                    "    for (arrayCopy(a.digits, c, d.digits, 0, a.digits.length - c),\n" +
                    "    e = b % bitsPerDigit,\n" +
                    "    f = bitsPerDigit - e,\n" +
                    "    g = 0,\n" +
                    "    h = g + 1; g < d.digits.length - 1; ++g, ++h)\n" +
                    "    d.digits[g] = d.digits[g] >>> e | (d.digits[h] & lowBitMasks[e]) << f;\n" +
                    "    return d.digits[d.digits.length - 1] >>>= e,\n" +
                    "    d.isNeg = a.isNeg,\n" +
                    "    d\n" +
                    "}\n" +
                    "\n" +
                    "function biMultiplyByRadixPower(a, b) {\n" +
                    "    var c = new BigInt;\n" +
                    "    return arrayCopy(a.digits, 0, c.digits, b, c.digits.length - b),\n" +
                    "    c\n" +
                    "}\n" +
                    "\n" +
                    "function biDivideByRadixPower(a, b) {\n" +
                    "    var c = new BigInt;\n" +
                    "    return arrayCopy(a.digits, b, c.digits, 0, c.digits.length - b),\n" +
                    "    c\n" +
                    "}\n" +
                    "\n" +
                    "function biModuloByRadixPower(a, b) {\n" +
                    "    var c = new BigInt;\n" +
                    "    return arrayCopy(a.digits, 0, c.digits, 0, b),\n" +
                    "    c\n" +
                    "}\n" +
                    "\n" +
                    "function biCompare(a, b) {\n" +
                    "    if (a.isNeg != b.isNeg) return 1 - 2 * Number(a.isNeg);\n" +
                    "    for (var c = a.digits.length - 1; c >= 0; --c)\n" +
                    "    if (a.digits[c] != b.digits[c]) return a.isNeg ? 1 - 2 * Number(a.digits[c] > b.digits[c]) : 1 - 2 * Number(a.digits[c] < b.digits[c]);\n" +
                    "    return 0\n" +
                    "}\n" +
                    "\n" +
                    "function biDivideModulo(a, b) {\n" +
                    "    var f, g, h, i, j, k, l, m, n, o, p, q, r, s, c = biNumBits(a),\n" +
                    "        d = biNumBits(b),\n" +
                    "        e = b.isNeg;\n" +
                    "    if (d > c) return a.isNeg ? (f = biCopy(bigOne),\n" +
                    "    f.isNeg = !b.isNeg,\n" +
                    "    a.isNeg = !1,\n" +
                    "    b.isNeg = !1,\n" +
                    "    g = biSubtract(b, a),\n" +
                    "    a.isNeg = !0,\n" +
                    "    b.isNeg = e) : (f = new BigInt,\n" +
                    "    g = biCopy(a)),\n" +
                    "    new Array(f, g);\n" +
                    "    for (f = new BigInt,\n" +
                    "    g = a,\n" +
                    "    h = Math.ceil(d / bitsPerDigit) - 1,\n" +
                    "    i = 0; b.digits[h] < biHalfRadix;)\n" +
                    "    b = biShiftLeft(b, 1), ++i, ++d,\n" +
                    "    h = Math.ceil(d / bitsPerDigit) - 1;\n" +
                    "    for (g = biShiftLeft(g, i),\n" +
                    "    c += i,\n" +
                    "    j = Math.ceil(c / bitsPerDigit) - 1,\n" +
                    "    k = biMultiplyByRadixPower(b, j - h); - 1 != biCompare(g, k);)++f.digits[j - h],\n" +
                    "    g = biSubtract(g, k);\n" +
                    "    for (l = j; l > h; --l) {\n" +
                    "        for (m = l >= g.digits.length ? 0 : g.digits[l],\n" +
                    "        n = l - 1 >= g.digits.length ? 0 : g.digits[l - 1],\n" +
                    "        o = l - 2 >= g.digits.length ? 0 : g.digits[l - 2],\n" +
                    "        p = h >= b.digits.length ? 0 : b.digits[h],\n" +
                    "        q = h - 1 >= b.digits.length ? 0 : b.digits[h - 1],\n" +
                    "        f.digits[l - h - 1] = m == p ? maxDigitVal : Math.floor((m * biRadix + n) / p),\n" +
                    "        r = f.digits[l - h - 1] * (p * biRadix + q),\n" +
                    "        s = m * biRadixSquared + (n * biRadix + o); r > s;)--f.digits[l - h - 1],\n" +
                    "        r = f.digits[l - h - 1] * (p * biRadix | q),\n" +
                    "        s = m * biRadix * biRadix + (n * biRadix + o);\n" +
                    "        k = biMultiplyByRadixPower(b, l - h - 1),\n" +
                    "        g = biSubtract(g, biMultiplyDigit(k, f.digits[l - h - 1])),\n" +
                    "        g.isNeg && (g = biAdd(g, k), --f.digits[l - h - 1])\n" +
                    "    }\n" +
                    "    return g = biShiftRight(g, i),\n" +
                    "    f.isNeg = a.isNeg != e,\n" +
                    "    a.isNeg && (f = e ? biAdd(f, bigOne) : biSubtract(f, bigOne),\n" +
                    "    b = biShiftRight(b, i),\n" +
                    "    g = biSubtract(b, g)),\n" +
                    "    0 == g.digits[0] && 0 == biHighIndex(g) && (g.isNeg = !1),\n" +
                    "    new Array(f, g)\n" +
                    "}\n" +
                    "\n" +
                    "function biDivide(a, b) {\n" +
                    "    return biDivideModulo(a, b)[0]\n" +
                    "}\n" +
                    "\n" +
                    "function biModulo(a, b) {\n" +
                    "    return biDivideModulo(a, b)[1]\n" +
                    "}\n" +
                    "\n" +
                    "function biMultiplyMod(a, b, c) {\n" +
                    "    return biModulo(biMultiply(a, b), c)\n" +
                    "}\n" +
                    "\n" +
                    "function biPow(a, b) {\n" +
                    "    for (var c = bigOne, d = a;;) {\n" +
                    "        if (0 != (1 & b) && (c = biMultiply(c, d)),\n" +
                    "        b >>= 1,\n" +
                    "        0 == b) break;\n" +
                    "        d = biMultiply(d, d)\n" +
                    "    }\n" +
                    "    return c\n" +
                    "}\n" +
                    "\n" +
                    "function biPowMod(a, b, c) {\n" +
                    "    for (var d = bigOne, e = a, f = b;;) {\n" +
                    "        if (0 != (1 & f.digits[0]) && (d = biMultiplyMod(d, e, c)),\n" +
                    "        f = biShiftRight(f, 1),\n" +
                    "        0 == f.digits[0] && 0 == biHighIndex(f)) break;\n" +
                    "        e = biMultiplyMod(e, e, c)\n" +
                    "    }\n" +
                    "    return d\n" +
                    "}\n" +
                    "\n" +
                    "function BarrettMu(a) {\n" +
                    "    this.modulus = biCopy(a),\n" +
                    "    this.k = biHighIndex(this.modulus) + 1;\n" +
                    "    var b = new BigInt;\n" +
                    "    b.digits[2 * this.k] = 1,\n" +
                    "    this.mu = biDivide(b, this.modulus),\n" +
                    "    this.bkplus1 = new BigInt,\n" +
                    "    this.bkplus1.digits[this.k + 1] = 1,\n" +
                    "    this.modulo = BarrettMu_modulo,\n" +
                    "    this.multiplyMod = BarrettMu_multiplyMod,\n" +
                    "    this.powMod = BarrettMu_powMod\n" +
                    "}\n" +
                    "\n" +
                    "function BarrettMu_modulo(a) {\n" +
                    "    var i, b = biDivideByRadixPower(a, this.k - 1),\n" +
                    "        c = biMultiply(b, this.mu),\n" +
                    "        d = biDivideByRadixPower(c, this.k + 1),\n" +
                    "        e = biModuloByRadixPower(a, this.k + 1),\n" +
                    "        f = biMultiply(d, this.modulus),\n" +
                    "        g = biModuloByRadixPower(f, this.k + 1),\n" +
                    "        h = biSubtract(e, g);\n" +
                    "    for (h.isNeg && (h = biAdd(h, this.bkplus1)),\n" +
                    "    i = biCompare(h, this.modulus) >= 0; i;)\n" +
                    "    h = biSubtract(h, this.modulus),\n" +
                    "    i = biCompare(h, this.modulus) >= 0;\n" +
                    "    return h\n" +
                    "}\n" +
                    "\n" +
                    "function BarrettMu_multiplyMod(a, b) {\n" +
                    "    var c = biMultiply(a, b);\n" +
                    "    return this.modulo(c)\n" +
                    "}\n" +
                    "\n" +
                    "function BarrettMu_powMod(a, b) {\n" +
                    "    var d, e, c = new BigInt;\n" +
                    "    for (c.digits[0] = 1,\n" +
                    "    d = a,\n" +
                    "    e = b;;) {\n" +
                    "        if (0 != (1 & e.digits[0]) && (c = this.multiplyMod(c, d)),\n" +
                    "        e = biShiftRight(e, 1),\n" +
                    "        0 == e.digits[0] && 0 == biHighIndex(e)) break;\n" +
                    "        d = this.multiplyMod(d, d)\n" +
                    "    }\n" +
                    "    return c\n" +
                    "}\n" +
                    "var maxDigits, ZERO_ARRAY, bigZero, bigOne, dpl10, lr10, hexatrigesimalToChar, hexToChar, highBitMasks, lowBitMasks, biRadixBase = 2,\n" +
                    "    biRadixBits = 16,\n" +
                    "    bitsPerDigit = biRadixBits,\n" +
                    "    biRadix = 65536,\n" +
                    "    biHalfRadix = biRadix >>> 1,\n" +
                    "    biRadixSquared = biRadix * biRadix,\n" +
                    "    maxDigitVal = biRadix - 1,\n" +
                    "    maxInteger = 9999999999999998;\n" +
                    "setMaxDigits(20),\n" +
                    "dpl10 = 15,\n" +
                    "lr10 = biFromNumber(1e15),\n" +
                    "hexatrigesimalToChar = new Array(\"0\", \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", \"h\", \"i\", \"j\", \"k\", \"l\", \"m\", \"n\", \"o\", \"p\", \"q\", \"r\", \"s\", \"t\", \"u\", \"v\", \"w\", \"x\", \"y\", \"z\"),\n" +
                    "hexToChar = new Array(\"0\", \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"a\", \"b\", \"c\", \"d\", \"e\", \"f\"),\n" +
                    "highBitMasks = new Array(0, 32768, 49152, 57344, 61440, 63488, 64512, 65024, 65280, 65408, 65472, 65504, 65520, 65528, 65532, 65534, 65535),\n" +
                    "lowBitMasks = new Array(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535);\n" +
                    "! function() {\n" +
                    "    function a(a) {\n" +
                    "        var d, e, b = \"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789\",\n" +
                    "            c = \"\";\n" +
                    "        for (d = 0; a > d; d += 1)\n" +
                    "        e = Math.random() * b.length,\n" +
                    "        e = Math.floor(e),\n" +
                    "        c += b.charAt(e);\n" +
                    "        return c\n" +
                    "    }\n" +
                    "\n" +
                    "    function b(a, b) {\n" +
                    "        var c = CryptoJS.enc.Utf8.parse(b),\n" +
                    "            d = CryptoJS.enc.Utf8.parse(\"0102030405060708\"),\n" +
                    "            e = CryptoJS.enc.Utf8.parse(a),\n" +
                    "            f = CryptoJS.AES.encrypt(e, c, {\n" +
                    "                iv: d,\n" +
                    "                mode: CryptoJS.mode.CBC\n" +
                    "            });\n" +
                    "        return f.toString()\n" +
                    "    }\n" +
                    "\n" +
                    "    function c(a, b, c) {\n" +
                    "        var d, e;\n" +
                    "        return setMaxDigits(131),\n" +
                    "        d = new RSAKeyPair(b, \"\", c),\n" +
                    "        e = encryptedString(d, a)\n" +
                    "    }\n" +
                    "\n" +
                    "    function d(d, e, f, g) {\n" +
                    "        var h = {}, i = a(16);\n" +
                    "        return h.encText = b(d, g),\n" +
                    "        h.encText = b(h.encText, i),\n" +
                    "        h.encSecKey = c(i, e, f),\n" +
                    "        h\n" +
                    "    }\n" +
                    "\n" +
                    "    function e(a, b, d, e) {\n" +
                    "        var f = {};\n" +
                    "        return f.encText = c(a + e, b, d),\n" +
                    "        f\n" +
                    "    }\n" +
                    "    window.asrsea = d,\n" +
                    "    window.ecnonasr = e\n" +
                    "}();\n" +
                    "\n" +
                    "function get(data) {\n" +
                    "    var bYf8X = window.asrsea(data, \"010001\", \"00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7\", \"0CoJUm6Qyw8W8jud\");\n" +
                    "\n" +
                    "    return \"params=\" + bYf8X.encText + \"&encSecKey=\" + bYf8X.encSecKey\n" +
                    "}\n" +
                    "\n" +
                    "function getSearch(keyWord) {\n" +
                    "    var data = {\n" +
                    "        hlpretag: \"<span class=\\\"s-fc7\\\">\",\n" +
                    "        hlposttag: \"</span>\",\n" +
                    "        s: keyWord,\n" +
                    "        type: \"1\",\n" +
                    "        offset: \"0\",\n" +
                    "        total: \"true\",\n" +
                    "        limit: \"30\",\n" +
                    "        csrf_token: \"\"\n" +
                    "    }\n" +
                    "    return get(JSON.stringify(data));\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "function getPlay(id) {\n" +
                    "    var data = {\n" +
                    "        ids: \"[\" + id + \"]\",\n" +
                    "        level: \"standard\",\n" +
                    "        encodeType: \"aac\",\n" +
                    "        csrf_token: \"\"\n" +
                    "    }\n" +
                    "    return get(JSON.stringify(data));\n" +
                    "}");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }


    public String getSearch(String keyWord) {
        try {
            return (String) engine.eval("getSearch('" + keyWord + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPlay(String id) {
        try {
            return (String) engine.eval("getPlay('" + id + "')");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }


    class CloudMusicBean {
        private String name;
        private String url;

        public CloudMusicBean(String name, String url) {
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
