package com.ealong.diversion.service;

import com.ealong.diversion.model.LinkModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

public class ParsePageUtils {
    public static void parse(Map<String, LinkModel> dataMap, String url) {
        try {
            System.out.println("当前请求是："+ url);
            Random r = new Random();
            Thread.sleep(r.nextInt(1000));
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.122 Safari/537.36")
                    .timeout(1000 * 60 * 5).get();
            Elements links = document.getElementsByTag("a");
            for (Element e : links) {
                String relHref = e.attr("href");
                String absHref = e.attr("abs:href");
                if (!relHref.startsWith("http")) {
                    if (dataMap.containsKey(absHref)) {
                        continue;
                    }
                    LinkModel _linkModel = new LinkModel();
                    _linkModel.setName(e.text().trim());
                    dataMap.put(absHref, _linkModel);
                    parse(dataMap, absHref);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
