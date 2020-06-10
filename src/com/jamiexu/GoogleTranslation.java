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
import java.util.HashMap;

/**
 * @author Jamiexu
 * time:2020-5-5 19:14
 * blog:blog.jamiexu.cn
 * This code is for learning only！！！
 * This code is for learning only！！！
 * This code is for learning only！！！
 */

public class GoogleTranslation {
    //last test time:2020-6-3 13:46
    //last test time:2020-6-10 12:14
    private static HashMap<String, String> hashMap_language;
    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

    static {
        try {
            scriptEngine.eval("function token(a) {\n" +
                    "    var k = \"\";\n" +
                    "    var b = 406644;\n" +
                    "    var b1 = 3293161072;\n" +
                    "\n" +
                    "    var jd = \".\";\n" +
                    "    var sb = \"+-a^+6\";\n" +
                    "    var Zb = \"+-3^+b+-f\";\n" +
                    "\n" +
                    "    for (var e = [], f = 0, g = 0; g < a.length; g++) {\n" +
                    "        var m = a.charCodeAt(g);\n" +
                    "        128 > m ? e[f++] = m : (2048 > m ? e[f++] = m >> 6 | 192 : (55296 == (m & 64512) && g + 1 < a.length && 56320 == (a.charCodeAt(g + 1) & 64512) ? (m = 65536 + ((m & 1023) << 10) + (a.charCodeAt(++g) & 1023),\n" +
                    "        e[f++] = m >> 18 | 240,\n" +
                    "        e[f++] = m >> 12 & 63 | 128) : e[f++] = m >> 12 | 224,\n" +
                    "        e[f++] = m >> 6 & 63 | 128),\n" +
                    "        e[f++] = m & 63 | 128)\n" +
                    "    }\n" +
                    "    a = b;\n" +
                    "    for (f = 0; f < e.length; f++)\n" +
                    "        a += e[f],\n" +
                    "        a = RL(a, sb);\n" +
                    "    a = RL(a, Zb);\n" +
                    "    a ^= b1 || 0;\n" +
                    "    0 > a && (a = (a & 2147483647) + 2147483648);\n" +
                    "    a %= 1E6;\n" +
                    "    return a.toString() + jd + (a ^ b)\n" +
                    "};\n" +
                    "function RL(a, b) {\n" +
                    "    var t = \"a\";\n" +
                    "    var Yb = \"+\";\n" +
                    "    for (var c = 0; c < b.length - 2; c += 3) {\n" +
                    "        var d = b.charAt(c + 2),\n" +
                    "        d = d >= t ? d.charCodeAt(0) - 87 : Number(d),\n" +
                    "        d = b.charAt(c + 1) == Yb ? a >>> d: a << d;\n" +
                    "        a = b.charAt(c) == Yb ? a + d & 4294967295 : a ^ d\n" +
                    "    }\n" +
                    "    return a\n" +
                    "}");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public GoogleTranslation() {
        try {
            if (hashMap_language == null) {
                hashMap_language = new HashMap<>();
                JSONObject jsonObject = new JSONObject("{lists:[{code:'auto',name:'检测语言'},{code:'sq',name:'阿尔巴尼亚语'},{code:'ar',name:'阿拉伯语'},{code:'am',name:'阿姆哈拉语'},{code:'az',name:'阿塞拜疆语'},{code:'ga',name:'爱尔兰语'},{code:'et',name:'爱沙尼亚语'},{code:'or',name:'奥里亚语(奥里亚文)'},{code:'eu',name:'巴斯克语'},{code:'be',name:'白俄罗斯语'},{code:'bg',name:'保加利亚语'},{code:'is',name:'冰岛语'},{code:'pl',name:'波兰语'},{code:'bs',name:'波斯尼亚语'},{code:'fa',name:'波斯语'},{code:'af',name:'布尔语(南非荷兰语)'},{code:'tt',name:'鞑靼语'},{code:'da',name:'丹麦语'},{code:'de',name:'德语'},{code:'ru',name:'俄语'},{code:'fr',name:'法语'},{code:'tl',name:'菲律宾语'},{code:'fi',name:'芬兰语'},{code:'fy',name:'弗里西语'},{code:'km',name:'高棉语'},{code:'ka',name:'格鲁吉亚语'},{code:'gu',name:'古吉拉特语'},{code:'kk',name:'哈萨克语'},{code:'ht',name:'海地克里奥尔语'},{code:'ko',name:'韩语'},{code:'ha',name:'豪萨语'},{code:'nl',name:'荷兰语'},{code:'ky',name:'吉尔吉斯语'},{code:'gl',name:'加利西亚语'},{code:'ca',name:'加泰罗尼亚语'},{code:'cs',name:'捷克语'},{code:'kn',name:'卡纳达语'},{code:'co',name:'科西嘉语'},{code:'hr',name:'克罗地亚语'},{code:'ku',name:'库尔德语'},{code:'la',name:'拉丁语'},{code:'lv',name:'拉脱维亚语'},{code:'lo',name:'老挝语'},{code:'lt',name:'立陶宛语'},{code:'lb',name:'卢森堡语'},{code:'rw',name:'卢旺达语'},{code:'ro',name:'罗马尼亚语'},{code:'mg',name:'马尔加什语'},{code:'mt',name:'马耳他语'},{code:'mr',name:'马拉地语'},{code:'ml',name:'马拉雅拉姆语'},{code:'ms',name:'马来语'},{code:'mk',name:'马其顿语'},{code:'mi',name:'毛利语'},{code:'mn',name:'蒙古语'},{code:'bn',name:'孟加拉语'},{code:'my',name:'缅甸语'},{code:'hmn',name:'苗语'},{code:'xh',name:'南非科萨语'},{code:'zu',name:'南非祖鲁语'},{code:'ne',name:'尼泊尔语'},{code:'no',name:'挪威语'},{code:'pa',name:'旁遮普语'},{code:'pt',name:'葡萄牙语'},{code:'ps',name:'普什图语'},{code:'ny',name:'齐切瓦语'},{code:'ja',name:'日语'},{code:'sv',name:'瑞典语'},{code:'sm',name:'萨摩亚语'},{code:'sr',name:'塞尔维亚语'},{code:'st',name:'塞索托语'},{code:'si',name:'僧伽罗语'},{code:'eo',name:'世界语'},{code:'sk',name:'斯洛伐克语'},{code:'sl',name:'斯洛文尼亚语'},{code:'sw',name:'斯瓦希里语'},{code:'gd',name:'苏格兰盖尔语'},{code:'ceb',name:'宿务语'},{code:'so',name:'索马里语'},{code:'tg',name:'塔吉克语'},{code:'te',name:'泰卢固语'},{code:'ta',name:'泰米尔语'},{code:'th',name:'泰语'},{code:'tr',name:'土耳其语'},{code:'tk',name:'土库曼语'},{code:'cy',name:'威尔士语'},{code:'ug',name:'维吾尔语'},{code:'ur',name:'乌尔都语'},{code:'uk',name:'乌克兰语'},{code:'uz',name:'乌兹别克语'},{code:'es',name:'西班牙语'},{code:'iw',name:'希伯来语'},{code:'el',name:'希腊语'},{code:'haw',name:'夏威夷语'},{code:'sd',name:'信德语'},{code:'hu',name:'匈牙利语'},{code:'sn',name:'修纳语'},{code:'hy',name:'亚美尼亚语'},{code:'ig',name:'伊博语'},{code:'it',name:'意大利语'},{code:'yi',name:'意第绪语'},{code:'hi',name:'印地语'},{code:'su',name:'印尼巽他语'},{code:'id',name:'印尼语'},{code:'jw',name:'印尼爪哇语'},{code:'en',name:'英语'},{code:'yo',name:'约鲁巴语'},{code:'vi',name:'越南语'},{code:'zh-CN',name:'中文'}],target_code_name:[{code:'sq',name:'阿尔巴尼亚语'},{code:'ar',name:'阿拉伯语'},{code:'am',name:'阿姆哈拉语'},{code:'az',name:'阿塞拜疆语'},{code:'ga',name:'爱尔兰语'},{code:'et',name:'爱沙尼亚语'},{code:'or',name:'奥里亚语(奥里亚文)'},{code:'eu',name:'巴斯克语'},{code:'be',name:'白俄罗斯语'},{code:'bg',name:'保加利亚语'},{code:'is',name:'冰岛语'},{code:'pl',name:'波兰语'},{code:'bs',name:'波斯尼亚语'},{code:'fa',name:'波斯语'},{code:'af',name:'布尔语(南非荷兰语)'},{code:'tt',name:'鞑靼语'},{code:'da',name:'丹麦语'},{code:'de',name:'德语'},{code:'ru',name:'俄语'},{code:'fr',name:'法语'},{code:'tl',name:'菲律宾语'},{code:'fi',name:'芬兰语'},{code:'fy',name:'弗里西语'},{code:'km',name:'高棉语'},{code:'ka',name:'格鲁吉亚语'},{code:'gu',name:'古吉拉特语'},{code:'kk',name:'哈萨克语'},{code:'ht',name:'海地克里奥尔语'},{code:'ko',name:'韩语'},{code:'ha',name:'豪萨语'},{code:'nl',name:'荷兰语'},{code:'ky',name:'吉尔吉斯语'},{code:'gl',name:'加利西亚语'},{code:'ca',name:'加泰罗尼亚语'},{code:'cs',name:'捷克语'},{code:'kn',name:'卡纳达语'},{code:'co',name:'科西嘉语'},{code:'hr',name:'克罗地亚语'},{code:'ku',name:'库尔德语'},{code:'la',name:'拉丁语'},{code:'lv',name:'拉脱维亚语'},{code:'lo',name:'老挝语'},{code:'lt',name:'立陶宛语'},{code:'lb',name:'卢森堡语'},{code:'rw',name:'卢旺达语'},{code:'ro',name:'罗马尼亚语'},{code:'mg',name:'马尔加什语'},{code:'mt',name:'马耳他语'},{code:'mr',name:'马拉地语'},{code:'ml',name:'马拉雅拉姆语'},{code:'ms',name:'马来语'},{code:'mk',name:'马其顿语'},{code:'mi',name:'毛利语'},{code:'mn',name:'蒙古语'},{code:'bn',name:'孟加拉语'},{code:'my',name:'缅甸语'},{code:'hmn',name:'苗语'},{code:'xh',name:'南非科萨语'},{code:'zu',name:'南非祖鲁语'},{code:'ne',name:'尼泊尔语'},{code:'no',name:'挪威语'},{code:'pa',name:'旁遮普语'},{code:'pt',name:'葡萄牙语'},{code:'ps',name:'普什图语'},{code:'ny',name:'齐切瓦语'},{code:'ja',name:'日语'},{code:'sv',name:'瑞典语'},{code:'sm',name:'萨摩亚语'},{code:'sr',name:'塞尔维亚语'},{code:'st',name:'塞索托语'},{code:'si',name:'僧伽罗语'},{code:'eo',name:'世界语'},{code:'sk',name:'斯洛伐克语'},{code:'sl',name:'斯洛文尼亚语'},{code:'sw',name:'斯瓦希里语'},{code:'gd',name:'苏格兰盖尔语'},{code:'ceb',name:'宿务语'},{code:'so',name:'索马里语'},{code:'tg',name:'塔吉克语'},{code:'te',name:'泰卢固语'},{code:'ta',name:'泰米尔语'},{code:'th',name:'泰语'},{code:'tr',name:'土耳其语'},{code:'tk',name:'土库曼语'},{code:'cy',name:'威尔士语'},{code:'ug',name:'维吾尔语'},{code:'ur',name:'乌尔都语'},{code:'uk',name:'乌克兰语'},{code:'uz',name:'乌兹别克语'},{code:'es',name:'西班牙语'},{code:'iw',name:'希伯来语'},{code:'el',name:'希腊语'},{code:'haw',name:'夏威夷语'},{code:'sd',name:'信德语'},{code:'hu',name:'匈牙利语'},{code:'sn',name:'修纳语'},{code:'hy',name:'亚美尼亚语'},{code:'ig',name:'伊博语'},{code:'it',name:'意大利语'},{code:'yi',name:'意第绪语'},{code:'hi',name:'印地语'},{code:'su',name:'印尼巽他语'},{code:'id',name:'印尼语'},{code:'jw',name:'印尼爪哇语'},{code:'en',name:'英语'},{code:'yo',name:'约鲁巴语'},{code:'vi',name:'越南语'},{code:'zh-TW',name:'中文(繁体)'},{code:'zh-CN',name:'中文(简体)'}]}");
                JSONArray jsonArray = jsonObject.getJSONArray("lists");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    hashMap_language.put(jsonObject1.getString("name"), jsonObject1.getString("code"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String get(String url) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.setRequestProperty("Host", "translate.google.cn");
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

    public String translateTo(String keyWord, String language) {
        String url = null;
        try {
            url = "https://translate.google.cn/translate_a/single?client=webapp&sl=auto&tl="
                    + language + "&hl=en&dt=at&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=sos&dt=ss&dt=t&ssel=0&tsel=3&xid=1791807%2C45625687%2C45626150&kc=0&tk=" +
                    getToken(keyWord) + "&q=" + URLEncoder.encode(keyWord, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = get(url);
        response = response.substring(4, response.indexOf("\",\"" + keyWord + "\""));
        return response;
    }

    public String getToken(String keyWord) {
        try {
            return (String) scriptEngine.eval("token('" + keyWord + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> getAllLanguage() {
        return this.hashMap_language;
    }


}
