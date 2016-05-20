package com.yinrong.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by yinrong on 2016/4/24.
 */
public class ResourcebundleUtil {
   static ResourceBundle resourceBundle=ResourceBundle.getBundle("common",  new Locale("zh", "CN"));

    public ResourcebundleUtil(Locale locale,String filePath ) {
        resourceBundle=ResourceBundle.getBundle(filePath, locale);
    }

    public ResourcebundleUtil(String filePath ) {
        resourceBundle=ResourceBundle.getBundle(filePath, new Locale("zh", "CN"));
    }

    public static String  getBundleString(String key){
        try {
            return  new String(resourceBundle.getString(key).getBytes("ISO-8859-1"),"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            return null;
    }

}
