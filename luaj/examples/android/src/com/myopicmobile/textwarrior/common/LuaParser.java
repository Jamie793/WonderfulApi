package com.myopicmobile.textwarrior.common;

import android.text.TextUtils;

import org.luaj.vm2.LocVars;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.Upvaldesc;
import org.luaj.vm2.compiler.LexState;
import org.luaj.vm2.compiler.LuaC;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nirenr on 2019/11/23.
 */

public class LuaParser {
    private static HashMap<String, ArrayList<Pair>> localMap = new HashMap<>();
    private static ArrayList<Var> varList = new ArrayList<>();
    private static ArrayList<LuaString> globalist = new ArrayList<>();

    public static class Var {
        public String name;
        public String type;
        public int startidx;
        public int endidx;

        public Var(String n, String t, int s, int e) {
            name = n;
            type = t;
            startidx = s;
            endidx = e;
        }

        @Override
        public String toString() {
            return String.format("Var (%s %s %s-%s)",name,type,startidx,endidx);
        }
    }

    public static class CharInputSteam extends InputStream {

        private final CharSequence mSrc;
        private final int mLen;
        private int idx = 0;

        public CharInputSteam(CharSequence src) {
            mSrc = src;
            mLen = src.length();
        }

        @Override
        public int read() throws IOException {
            idx++;
            if(idx>mLen)
                return -1;
            return mSrc.charAt(idx-1);
        }
    }

    public static boolean lexer(CharSequence src) {
        try {
            //Prototype lex = LuaC.lexer(new CharInputSteam(src), "luaj");
            Prototype lex = LuaC.lexer(src, "luaj");
            localMap.clear();
            varList.clear();
            lexer(lex);
            if(LexState.erroridx<0)
            globalist = new ArrayList<>(LexState.globals);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    public static HashMap<String, ArrayList<Pair>> getLocalMap() {
        return localMap;
    }


    private static void lexer(Prototype p) {
        if (p == null)
            return;
        LocVars[] ls = p.locvars;
        int np = p.numparams;
        Upvaldesc[] us = p.upvalues;
        for (int i = 0; i < us.length; i++) {
            Upvaldesc l = us[i];
            String n = l.name.tojstring();
            varList.add(new Var(n, " :upval", p.startidx, p.endidx));
        }
        for (int i = 0; i < ls.length; i++) {
            LocVars l = ls[i];
            String n = l.varname.tojstring();
            if (i < np) {
                varList.add(new Var(n, " :arg", l.startidx, l.endidx));
            } else {
                varList.add(new Var(n, " :local", l.startidx, l.endidx));
            }
            ArrayList<Pair> a = localMap.get(n);
            if (a == null) {
                a = new ArrayList<>();
                localMap.put(n, a);
            }
            a.add(new Pair(l.startidx, l.endidx));
        }

        Prototype[] ps = p.p;
        for (Prototype l : ps) {
            lexer(l);
        }
    }

    private static ArrayList<String> userWord = new ArrayList<>();

    public static void clearUserWord() {
        userWord.clear();
    }

    public static ArrayList<String> getUserWord() {
        return userWord;
    }

    public static void addUserWord(String s) {
        userWord.add(s);
    }

    public static ArrayList<String> filterLocal(String name, int idx) {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> ca = new ArrayList<>();

        for (int i = varList.size() - 1; i >= 0; i--) {
            Var var = varList.get(i);
            if (var.startidx <= idx && var.endidx >= idx) {
                String n = var.name;
                if (ca.contains(n))
                    continue;
                ca.add(n);
                if (n.toLowerCase().startsWith(name))
                    ret.add(n + var.type);
                String p = getSpells(n);
                if (TextUtils.isEmpty(p))
                    continue;
                if (p.startsWith(name))
                    ret.add(n);
            }
        }
        ArrayList<LuaString> ks = globalist;
        for (LuaValue k : ks) {
            String n = k.tojstring();
            if (ca.contains(n))
                continue;
            ca.add(n);
            if (n.toLowerCase().startsWith(name))
                ret.add(n+ " :global");
            String p = getSpells(n);
            if (TextUtils.isEmpty(p))
                continue;
            if (p.startsWith(name))
                ret.add(n);
        }
        return ret;
    }


    private static final int GB_SP_DIFF = 160;
    private static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    private static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};

    private static String getSpells(String characters) {
        try {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < characters.length(); i++) {
                char ch = characters.charAt(i);
                if (i == 0 && ch < 128) {
                    return null;
                }
                if ((ch >> 7) == 0) {
                    buffer.append(ch);
                } else {
                    char spell = getFirstLetter(ch);
                    if (spell == 0) {
                        continue;
                    }
                    buffer.append(String.valueOf(spell));
                }
            }
            return buffer.toString();
        } catch (Exception e){
            return null;
        }
    }

    private static char getFirstLetter(char ch) {
        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            return 0;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) {
            return 0;
        } else {
            return convert(uniCode);
        }
    }

    public static char convert(byte[] bytes) {
        char result = 0;
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }


}
