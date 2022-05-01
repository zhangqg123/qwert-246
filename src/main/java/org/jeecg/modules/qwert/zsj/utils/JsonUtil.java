package org.jeecg.modules.qwert.zsj.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

/**
 * Json读取工具类
 */
public class JsonUtil {

    private static final Logger logger = LogManager.getLogger(JsonUtil.class);

    /**
     * 读取json文件
     * @param fileName json文件名
     * @return 返回json字符串
     */
    public static String readJsonFile(File jsonFile) {
        String jsonStr = "";
        logger.info("————开始读取" + jsonFile.getPath() + "文件————");
        try {
            //File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            logger.info("————读取" + jsonFile.getPath() + "文件结束!————");
            return jsonStr;
        } catch (Exception e) {
            logger.info("————读取" + jsonFile.getPath() + "文件出现异常，读取失败!————");
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
    //    JsonUtil jsonUtil = new JsonUtil();
        String jsonStr = JsonUtil.readJsonFile(new File("d:/response.json"));
        Map jsonMap = (Map) JSON.parse(jsonStr);
        JSONArray rows = (JSONArray) jsonMap.get("rows");
        JSONObject jsonObject = rows.getJSONObject(0);
        for(Map.Entry<String,Object> entry : jsonObject.entrySet()){
            String key = entry.getKey();
            if(!key.equals("zhaobiaocontent")&&!key.equals("zhongbiaocontent")) {
                Object value = entry.getValue();
                System.out.println("key:::" + key);
                System.out.println("value:::"+value.toString());
            }
        }
    }
}