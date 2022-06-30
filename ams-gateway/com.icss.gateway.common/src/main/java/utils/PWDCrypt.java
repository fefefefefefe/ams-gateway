package utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PWDCrypt {
    public static final String Unix = "Unix";
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";
    public static final String SHA384 = "SHA-384";
    public static final String SHA512 = "SHA-512";
    private static Map KeyMap = new HashMap();
    private static Map SeedMap;
    private static List DigestAlgorithms;
    private String key;

    public PWDCrypt(String key) {
        this.key = key;
    }

    public PWDCrypt() {
        this.key = ConstantUtil.Default_PWD_Algorithms;
    }

    public final String crypt(String input) {
        if (input != null && !input.equals("")) {
            this.key = this.key == null ? ConstantUtil.Default_PWD_Algorithms : this.key;
            String secret = null;
            if (DigestAlgorithms.contains(this.key)) {
                secret = this.messageDigest(this.key, input, (String) null);
                secret = SeedMap.get(this.key).toString() + secret;
            }

            return secret;
        } else {
            return input;
        }
    }

    public static final boolean validate(String orginal, String secret) {
        if (orginal == null && secret == null) {
            return orginal == null && secret == null;
        } else {
            String key = getAlgorithm(secret);
            key = key == null ? ConstantUtil.Default_PWD_Algorithms : key;
            if (key != null) {
                PWDCrypt crypt = new PWDCrypt(key);
                return secret.equals(crypt.crypt(orginal));
            } else {
                return false;
            }
        }
    }

    public static final String getAlgorithm(String secret) {
        if (secret != null && secret.length() >= 3) {
            String seed = secret.substring(0, 2);
            if (seed.equals("RD")) {
                return "RD";
            } else {
                return KeyMap.containsKey(seed) ? (String) KeyMap.get(seed) : null;
            }
        } else {
            return null;
        }
    }

    private String messageDigest(String key, String input, String encode) {
        byte[] data = null;

        try {
            data = input.getBytes("UTF8");
            MessageDigest md = MessageDigest.getInstance(key);
            md.update(data);
            data = md.digest();
        } catch (Exception var7) {
        }

        if (!StringUtil.isEmpty(encode) && encode.equals("rone")) {
            BigInteger bigInteger = new BigInteger(data);
            return bigInteger.toString(16);
        } else {
            StringBuffer encodeBuf = new StringBuffer();

            for (int i = 0; i < data.length; ++i) {
                if (Integer.toHexString(255 & data[i]).length() == 1) {
                    encodeBuf.append("0").append(Integer.toHexString(255 & data[i]));
                } else {
                    encodeBuf.append(Integer.toHexString(255 & data[i]));
                }
            }

            return encodeBuf.toString();
        }
    }

    static {
        KeyMap.put("RO", "Unix");
        KeyMap.put("MD", "MD5");
        KeyMap.put("RS", "SHA-1");
        KeyMap.put("RH", "SHA-256");
        KeyMap.put("RA", "SHA-384");
        KeyMap.put("R5", "SHA-512");
        SeedMap = new HashMap();
        SeedMap.put("Unix", "RO");
        SeedMap.put("MD5", "MD");
        SeedMap.put("SHA-1", "RS");
        SeedMap.put("SHA-256", "RH");
        SeedMap.put("SHA-384", "RA");
        SeedMap.put("SHA-512", "R5");
        DigestAlgorithms = new ArrayList();
        DigestAlgorithms.add("MD5");
        DigestAlgorithms.add("SHA-1");
        DigestAlgorithms.add("SHA-256");
        DigestAlgorithms.add("SHA-384");
        DigestAlgorithms.add("SHA-512");
    }
}

