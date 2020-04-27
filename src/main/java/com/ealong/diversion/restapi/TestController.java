package com.ealong.diversion.restapi;

import com.ealong.diversion.model.LinkModel;
import com.ealong.diversion.service.DiversionPage;
import com.ealong.diversion.service.ParsePageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController{
    private DiversionPage diversionPage;
    @Autowired
    public void setDiversionPage(DiversionPage diversionPage) {
        this.diversionPage = diversionPage;
    }

    @RequestMapping("app")
    public String app(){
        diversionPage.myPrint("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html");
        return "hello ok";
    }
    @GetMapping("getLink")
    public String getLink(){
        try {
            Map<String, LinkModel> dataMap = new LinkedHashMap<>();
            LinkModel model = new LinkModel();
            //String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/index.html";
            String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/44.html";
            dataMap.put(url, model);
            ParsePageUtils.parse(dataMap, url);
            for(Map.Entry<String, LinkModel> entry: dataMap.entrySet()){
                System.out.println("获取的连接："+entry.getKey()+", 名称："+ entry.getValue().getName()+", 编码："+ entry.getValue().getCode());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "el";
    }
}
