package com.neoniou.tool.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Neo.Zzj
 * @date 2021/4/21
 */
public class ConvertUtil {

    private static final Map<String, String> CHARACTER_MAP = new HashMap<>();

    static {
        init();
    }

    private static void init() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("s2t.json");) {
            assert is != null;
            String jsonStr = IoUtil.read(is, StandardCharsets.UTF_8);
            JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                CHARACTER_MAP.put(jo.getStr("t"), jo.getStr("s"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String singleConvert(Character character) {
        String s = Character.toString(character);
        return CHARACTER_MAP.getOrDefault(s, s);
    }

    public static String convert(String content) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            sb.append(singleConvert(content.charAt(i)));
        }
        return sb.toString();
    }
}
