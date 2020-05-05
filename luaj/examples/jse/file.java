import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.LuajavaLib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by nirenr on 2020/1/16.
 */
public class file extends TwoArgFunction {
    public static String readAll(String path) {
        try {
            return new String(LuaUtil.readAll(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String[] list(String path) {
        return new File(path).list();
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean save(String path, String text) {
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(path));
            buf.write(text);
            buf.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable file = new LuaTable();
        file.set("readall", new readall());
        file.set("list", new list());
        file.set("exists", new exists());
        file.set("save", new save());
        env.set("file", file);
        if (!env.get("package").isnil()) env.get("package").get("loaded").set("file", file);
        return NIL;
    }

    private class readall extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            try {
                return LuaString.valueOf(LuaUtil.readAll(arg.tojstring()));
            } catch (Exception e) {
                return NIL;
            }
        }
    }

    private class list extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return LuajavaLib.asTable(list(arg.tojstring()));
        }
    }

    private class exists extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            try {
                return LuaValue.valueOf(exists(arg.tojstring()));
            } catch (Exception e) {
                return NIL;
            }
        }
    }

    private class save extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            return LuaValue.valueOf(save(arg1.tojstring(), arg2.tojstring()));
        }
    }
}
