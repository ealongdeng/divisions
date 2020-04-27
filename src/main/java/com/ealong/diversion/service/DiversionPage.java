package com.ealong.diversion.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DiversionPage {

    public void myPrint(String baseUrl) {
        Map<String, Boolean> oldMap = new LinkedHashMap<String, Boolean>();
        // 存储链接-是否被遍历
        // 键值对
        String oldLinkHost = ""; // host
        Pattern p = Pattern.compile("(https?://)?[^/\\s]*");
        // 比如：http://www.zifangsky.cn
        Matcher m = p.matcher(baseUrl);
        if (m.find()) {
            oldLinkHost = m.group() + "/tjsj/tjbz/tjyqhdmhcxhfdm/2019";
        }
        oldMap.put(baseUrl, false);
        oldMap = crawlLinks(oldLinkHost, oldMap);
        for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
            System.out.println("链接：" + mapping.getKey());
        }
    }
    private Map<String, Boolean> crawlLinks(String oldLinkHost, Map<String, Boolean> oldMap) {
        Map<String, Boolean> newMap = new LinkedHashMap<String, Boolean>();
        String oldLink = "";
        for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
            //System.out.println("link:" + mapping.getKey() + "--------check:" + mapping.getValue());
            // 如果没有被遍历过
            if (!mapping.getValue()) {
                oldLink = mapping.getKey();
                // 发起GET请求
                try {
                    Pattern pattern = Pattern.compile("<a.*?href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)</a>");

                    /*
                    HttpGet httpGet = new HttpGet(oldLink);
                    CloseableHttpClient client = HttpClients.createDefault();
                    CloseableHttpResponse response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    if(entity != null){
                        String content = EntityUtils.toString(entity, "gb2312");
                        Matcher matcher = pattern.matcher(content);
                        if(matcher.find()){
                            String link = matcher.group(1).trim();
                        }
                    }*/


                    URL url = new URL(oldLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    //connection.setConnectTimeout(20000);
                    //connection.setReadTimeout(20000);
                    if (connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "gb2312"));
                        String line = "";

                        Matcher matcher = null;
                        while ((line = reader.readLine()) != null) {
                            //line = new String(line.getBytes(StandardCharsets.UTF_8), "gb2312");
                            matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                String newLink = matcher.group(1).trim();
                                // 链接
                                // String title = matcher.group(3).trim(); //标题
                                // 判断获取到的链接是否以http开头
                                if (!newLink.startsWith("http")) {
                                    if (newLink.startsWith("/"))
                                        newLink = oldLinkHost + newLink;
                                    else
                                        newLink = oldLinkHost + "/" + newLink;
                                }
                                // 去除链接末尾的 /
                                if (newLink.endsWith("/"))
                                    newLink = newLink.substring(0, newLink.length() - 1);
                                // 去重，并且丢弃其他网站的链接
                                if (!oldMap.containsKey(newLink) && !newMap.containsKey(newLink)
                                        && newLink.startsWith(oldLinkHost)) {
                                    // System.out.println("temp2: " + newLink);
                                    newMap.put(newLink, false);
                                }
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                oldMap.put(oldLink, true);
            }
        }
        // 有新链接，继续遍历
        if (!newMap.isEmpty()) {
            oldMap.putAll(newMap);
            oldMap.putAll(crawlLinks(oldLinkHost, oldMap));
            // 由于Map的特性，不会导致出现重复的键值对
        }
        return oldMap;
    }
}
