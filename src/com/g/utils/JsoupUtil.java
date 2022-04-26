package com.g.utils;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * HTTP請求工具類
 * 
 * 
 */
public class JsoupUtil {
	public static String httpGet(String url,String cookie) throws IOException{
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //请求头设置，特别是cookie设置
        con.header("Cookie", "_ga=GA1.1.319652816.1646637684; _ga_EKLPQL67GF=GS1.1.1647077203.10.1.1647077212.0");
        con.header("Accept", "text/html, application/xhtml+xml, */*"); 
        con.header("Content-Type", "application/json");
        con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0"); 
        con.header("report-to","{'endpoints':[{'url':'https://a.nel.cloudflare.com/report/v3?s=i3aifx54lHYvUw05M4hvEmM7JFRkzlVt2dQHYORtr1nC7fAZh9xMt802H47foFaBvoCcE7uIzhzC8QhVdlkdT9kU0h2LOagBPLk6h2ZeR%2BciywkC9MUmRFhmTkoTPwq0'}],'group':'cf-nel','max_age':604800}");
        con.header("cf-cache-status", "DYNAMIC");
        con.header("cf-ray", "6eabaa9388eb7c21-LAX");
        con.header("content-encoding", "br");
        con.header("authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NDc4Mzc5MzcsImp0aSI6IjB4ZWFkNzJmNmJiMzhhMjgwY2E1NmViYjkwYjQ4N2EzZTM0NWQ3NThlNyIsInN1YiI6IndlYiJ9.C49ZkLG7ZhEJECBZM41d9ySnqyboR7KtLAZlN4PbpsM");
        con.header("date", "Sat, 12 Mar 2022 09:57:33 GMT");
        con.header("nel", "{\"success_fraction\":0,\"report_to\":\"cf-nel\",\"max_age\":604800}");
        con.header("server", "cloudflare");
        con.header("vary", "Accept-Encoding");
        con.header("vary", "Origin");
        con.header("x-frame-options", "SAMEORIGIN");
        con.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        con.header("accept-encoding", "gzip, deflate, br");
        con.header("accept-language", "zh-CN,zh;q=0.9");
        con.header("cache-control", "max-age=0");
        con.header("x-frame-options", "SAMEORIGIN");
        con.header("upgrade-insecure-requests", "1");
        //解析请求结果
        Document doc=con.get(); 
        //获取标题
        System.out.println(doc.title());
        return doc.toString();
    }
	public static String httpPost(String url,Map<String,String> map,String cookie) throws IOException{
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //遍历生成参数
        if(map!=null){
            for (String key : map.keySet()) {
            	con.data(key, map.get(key));
            }
        }
        //插入cookie（头文件形式）
        con.header("Cookie", cookie);
        con.header("SESSIONID", "");
        Document doc = con.post();  
        System.out.println(doc);
        return doc.toString();
    }
}