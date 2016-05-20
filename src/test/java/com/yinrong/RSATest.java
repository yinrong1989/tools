package com.yinrong;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by yinrong on 2016/5/5.
 */
public class RSATest {
    public static final String  SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String ALGORITHM       = "RSA";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;


    public static String encrypt(String content,String charset, String aliPubKey) {
        try {

            byte[] keyBytes = Base64.decode(aliPubKey);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key pubKey = keyFactory.generatePublic(x509KeySpec);

            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] data = content.getBytes(charset);
            byte[] splitData = splitData(data, cipher,MAX_ENCRYPT_BLOCK);
            return Base64.encode(splitData);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }


    public static String decrypt(String content,String charset, String myPriKey){

        try {

            byte[] keyBytes = Base64.decode(myPriKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            byte[] data = Base64.decode(content);
            byte[] splitData = splitData(data, cipher,MAX_DECRYPT_BLOCK);
            return new String(splitData,charset);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }


    private static byte[] splitData(byte[] data, Cipher cipher,int maxLen) throws IllegalBlockSizeException,
            BadPaddingException, IOException {
        int totalLen = data.length;
        int offSet = 0 ;
        int index = 0;
        byte[] cache;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 对数据分段解密
        while (totalLen - offSet > 0) {
            if (totalLen - offSet > maxLen) {
                cache = cipher.doFinal(data, offSet, maxLen);
            } else {
                cache = cipher.doFinal(data, offSet, totalLen - offSet);
            }
            out.write(cache, 0, cache.length);
            index++;
            offSet = index * maxLen;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;

    }


    public static String sign(String content, String charset, String myPriKey) {

        try {

            byte[] decodePubKey = Base64.decode(myPriKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodePubKey);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(privateKey);
            signature.update(content.getBytes(charset));

            byte[] signed = signature.sign();

            return Base64.encode(signed);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }


    public static boolean verify(String content, String charset, String aliPubKey,String aliSign) {
        try {

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] decodePubKey = Base64.decode(aliPubKey);

            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodePubKey));

            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(content.getBytes(charset));

            return signature.verify(Base64.decode(aliSign));

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args){

     /*   String priKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAPKZrs4MTh0QjSWYQ8bkpIo8TsPLWtbA+PLdScMmz6GMMiWr4KYREAM6eTdzJG+NK/aK+ImihxOrJV/VJNvoSscdQxP7brvjbEQbFs+PKovMWEO7xJmBm3U27+9HOH9NFprSWCog9cfbtQXAXwTeGFyXybOgUtVsL17kcIJabYd3AgMBAAECgYA8kzsen9vxTeywcnCZ/QVIrv5LzT8FWHHQ0ohUfiBiCguLdHtHfAMviy4xNkLmx60uhkzAsSBhPN68KxBlCH+C88j3OiLtibCHTEiyTtjuT6C+hmh0UNY6HI5kcKbGqalGp1wvtf6FQ9GT6c5t8FdsT5vDua75VdSqkOg7ETu+gQJBAPxrjhQ6Lq5wb7aiCFnDkr6rD4quQRrYwEanjRv/p/3J8Udntmfq67Q2z1gWvHsj8tvdq9Ufn9FjPb8D7m1B58cCQQD2CnnHAd6r+L96q74EWrCUyzT0t4/i71cX0rlqT7Exo6N2frPkX3mPU0oQ45nxW3dUC0Q4SoDPFHYpsrnzMkLRAkAMpQqqArwh6un9uSI39O8d9A3EQbRrNt5y66PK+kSdFweqLwzZKdCC55f9bq4kcQmScAlSlNH5uEH4lqbT/FAzAkAwBTVlYPkLyX3dvF1Wzjh+ofaQ+K6tlRcDgE5N8IXd8Vk7DFmh7fx0u8XN7A4krDxq+9PxxKDFSfISC179l0axAkABIMCJqK+fE2MASJgliVkrwc/JvJM9BhL1RwcfIDvqvL00exyBlPMbwxEoJe//2F53/h1Kh/rN0bCGU21/njrv";
                priKey= "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN0yqPkLXlnhM+2H/57aHsYHaHXazr9pFQun907TMvmbR04wHChVsKVgGUF1hC0FN9hfeYT5v2SXg1WJSg2tSgk7F29SpsF0I36oSLCIszxdu7ClO7c22mxEVuCjmYpJdqb6XweAZzv4Is661jXP4PdrCTHRdVTU5zR9xUByiLSVAgMBAAECgYEAhznORRonHylm9oKaygEsqQGkYdBXbnsOS6busLi6xA+iovEUdbAVIrTCG9t854z2HAgaISoRUKyztJoOtJfI1wJaQU+XL+U3JIh4jmNx/k5UzJijfvfpT7Cv3ueMtqyAGBJrkLvXjiS7O5ylaCGuB0Qz711bWGkRrVoosPM3N6ECQQD8hVQUgnHEVHZYtvFqfcoq2g/onPbSqyjdrRu35a7PvgDAZx69Mr/XggGNTgT3jJn7+2XmiGkHM1fd1Ob/3uAdAkEA4D7aE3ZgXG/PQqlm3VbE/+4MvNl8xhjqOkByBOY2ZFfWKhlRziLEPSSAh16xEJ79WgY9iti+guLRAMravGrs2QJBAOmKWYeaWKNNxiIoF7/4VDgrcpkcSf3uRB44UjFSn8kLnWBUPo6WV+x1FQBdjqRviZ4NFGIP+KqrJnFHzNgJhVUCQFzCAukMDV4PLfeQJSmna8PFz2UKva8fvTutTryyEYu+PauaX5laDjyQbc4RIEMU0Q29CRX3BA8WDYg7YPGRdTkCQQCG+pjU2FB17ZLuKRlKEdtXNV6zQFTmFc1TKhlsDTtCkWs/xwkoCfZKstuV3Uc5J4BNJDkQOGm38pDRPcUDUh2/";
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDyma7ODE4dEI0lmEPG5KSKPE7Dy1rWwPjy3UnDJs+hjDIlq+CmERADOnk3cyRvjSv2iviJoocTqyVf1STb6ErHHUMT+26742xEGxbPjyqLzFhDu8SZgZt1Nu/vRzh/TRaa0lgqIPXH27UFwF8E3hhcl8mzoFLVbC9e5HCCWm2HdwIDAQAB";
                pubKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQWiDVZ7XYxa4CQsZoB3n7bfxLDkeGKjyQPt2FUtm4TWX9OYrd523iw6UUqnQ+Evfw88JgRnhyXadp+vnPKP7unormYQAfsM/CxzrfMoVdtwSiGtIJB4pfyRXjA+KL8nIa2hdQy5nLfgPVGZN4WidfUY/QpkddCVXnZ4bAUaQjXQIDAQAB";
     */
        String priKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAPKZrs4MTh0QjSWYQ8bkpIo8TsPLWtbA+PLdScMmz6GMMiWr4KYREAM6eTdzJG+NK/aK+ImihxOrJV/VJNvoSscdQxP7brvjbEQbFs+PKovMWEO7xJmBm3U27+9HOH9NFprSWCog9cfbtQXAXwTeGFyXybOgUtVsL17kcIJabYd3AgMBAAECgYA8kzsen9vxTeywcnCZ/QVIrv5LzT8FWHHQ0ohUfiBiCguLdHtHfAMviy4xNkLmx60uhkzAsSBhPN68KxBlCH+C88j3OiLtibCHTEiyTtjuT6C+hmh0UNY6HI5kcKbGqalGp1wvtf6FQ9GT6c5t8FdsT5vDua75VdSqkOg7ETu+gQJBAPxrjhQ6Lq5wb7aiCFnDkr6rD4quQRrYwEanjRv/p/3J8Udntmfq67Q2z1gWvHsj8tvdq9Ufn9FjPb8D7m1B58cCQQD2CnnHAd6r+L96q74EWrCUyzT0t4/i71cX0rlqT7Exo6N2frPkX3mPU0oQ45nxW3dUC0Q4SoDPFHYpsrnzMkLRAkAMpQqqArwh6un9uSI39O8d9A3EQbRrNt5y66PK+kSdFweqLwzZKdCC55f9bq4kcQmScAlSlNH5uEH4lqbT/FAzAkAwBTVlYPkLyX3dvF1Wzjh+ofaQ+K6tlRcDgE5N8IXd8Vk7DFmh7fx0u8XN7A4krDxq+9PxxKDFSfISC179l0axAkABIMCJqK+fE2MASJgliVkrwc/JvJM9BhL1RwcfIDvqvL00exyBlPMbwxEoJe//2F53/h1Kh/rN0bCGU21/njrv";
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDyma7ODE4dEI0lmEPG5KSKPE7Dy1rWwPjy3UnDJs+hjDIlq+CmERADOnk3cyRvjSv2iviJoocTqyVf1STb6ErHHUMT+26742xEGxbPjyqLzFhDu8SZgZt1Nu/vRzh/TRaa0lgqIPXH27UFwF8E3hhcl8mzoFLVbC9e5HCCWm2HdwIDAQAB";



        String charset = "utf-8";
        String content = "RSA公钥加密算法是1977年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard ";

        String sign = sign(content,charset,priKey);
        boolean result = verify(content,charset,pubKey,sign);

        String encrptyContent = encrypt(content,charset,pubKey);
        String realContent = decrypt(encrptyContent,charset,priKey);

        System.out.println(String.format("sign=%s result=%s realContent=%s",sign,result,realContent));
    }

}
