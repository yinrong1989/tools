package com.yinrong;

import com.yinrong.util.ResourcebundleUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by yinrong on 2016/4/24.
 */
public class AppTest {
    @Test
    public void resourcebundleTest(){
       String value= ResourcebundleUtil.getBundleString("name");
        String[] argstr=value.split(",");
        for (String str:argstr){
            System.out.println("argstr:"+str);
        }

     //   System.out.println("test name is "+value);

        String urls=ResourcebundleUtil.getBundleString("url");
        String[]urlsarr=urls.split("，");
        for(String url:urlsarr){
            DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        try {
            HttpResponse response =httpclient.execute(httpost);
            System.out.println("HttpResponse"+response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }



}
