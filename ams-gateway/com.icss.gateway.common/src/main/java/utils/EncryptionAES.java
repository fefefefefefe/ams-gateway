package utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Json数据的加密解密(AES)
 *
 * @author zhaomiao
 */
public class EncryptionAES {
    /**
     * 加密字符串
     *
     * @param str 要加密的串
     * @return 返回加密后的字符串
     * @throws Exception
     */
    public static String encryptionAES(String str, String key) throws Exception {

        String encryptionStr = "";
        try {
            // 加密密匙
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            keyGenerator.init(128, random);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyGenerator.generateKey().getEncoded(), "AES"));
            encryptionStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        } catch (Exception e) {
            throw e;
        }
        return encryptionStr;
    }

    /**
     * AES解密
     *
     * @param rulesJson 解密内容
     * @param key       密钥
     * @return 解密完成的内容
     * @throws Exception
     */
    public static String decryptAES(String rulesJson, String key) throws Exception {
        // 解码
        byte[] bytes = Base64.decodeBase64(rulesJson);
        // AES解密
        try {
            byte[] decryptBytes = new byte[0];
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            kgen.init(128, random);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            decryptBytes = cipher.doFinal(bytes);
            return new String(decryptBytes, "UTF-8");
        } catch (Exception e) {
            throw new Exception("文件内容解密异常！");
        }
    }
}
