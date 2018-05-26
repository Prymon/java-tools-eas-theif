package com.prymon.tools.eastheif.secret;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class JSSecret {
    private static Invocable inv;
    public static final String encText = "encText";
    public static final String encSecKey = "encSecKey";

    /**
     * 从本地加载修改后的 js 文件到 scriptEngine
     */
    static {
        try {
            System.out.println("start init core.js");
            InputStream is = JSSecret.class.getClassLoader().getResourceAsStream("core.js");
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = is.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            String js = new String(swapStream.toByteArray());
            System.out.println("core.js: " + js);
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            engine.eval(js);
            inv = (Invocable) engine;
            System.out.println("Init completed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(123);
    }

    public static ScriptObjectMirror get_params(String paras) throws Exception {
        ScriptObjectMirror so = (ScriptObjectMirror) inv.invokeFunction("myFunc", paras);
        return so;
    }

    public static HashMap<String, String> getDatas(String paras) {
        try {
            ScriptObjectMirror so = (ScriptObjectMirror) inv.invokeFunction("myFunc", paras);

//            Set<Map.Entry<String, Object>> entries = so.entrySet();
//            for (Map.Entry<String,Object> map: entries) {
//                System.out.println("key:"+map.getKey());
//                System.out.println("value:"+map.getValue());
//            }
//

            HashMap<String, String> datas = new HashMap<>();
            datas.put("params", so.get(JSSecret.encText).toString());
            datas.put("encSecKey", so.get(JSSecret.encSecKey).toString());
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
