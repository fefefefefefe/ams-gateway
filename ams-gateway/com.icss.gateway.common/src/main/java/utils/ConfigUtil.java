package utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Properties;

public class ConfigUtil {
    private static ConfigUtil configUtil = new ConfigUtil();

    public static String getPropValue(String filePath, String key) throws Exception {
        InputStreamReader ips = null;
        try {
            Properties props = new Properties();
            ips = new InputStreamReader(configUtil.getClass().getResourceAsStream(filePath), "UTF-8");
            props.load(ips);
            String value = props.getProperty(key);
            return value;
        } finally {
            if (ips != null) ips.close();
        }
    }

    public static String getPropertiesValue(String filePath, String key) throws Exception{
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean(); //可以加载多个yml文件
        yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
        Properties properties = yamlMapFactoryBean.getObject(); //获取yml里的参数
//        String active = properties.getProperty("oracle.url");
//        System.out.println("active:" + active);
		InputStreamReader ips=null;
		try{
			ips = new InputStreamReader(configUtil.getClass().getResourceAsStream(filePath),"UTF-8");
            properties.load(ips);
			String value = properties.getProperty(key);
			return value;
		}finally{
			if(ips!=null)ips.close();
		}
    }


    /**
     * 修改或添加键值对 如果key存在，修改 反之，添加。
     *
     * @param key
     * @param value
     */
    public static void writeData(String filePath, String key, String value) throws Exception {
        OutputStream fos = null;
        InputStream fis = null;
        try {
            Properties prop = new Properties();
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();//一定要在修改值之前关闭fis
            fos = new FileOutputStream(filePath);
            prop.setProperty(key, value);
            prop.store(fos, "Update '" + key + "' value");
        } finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }

    }

//	public static void writePropertyData(String filePath,String key, String value) throws Exception{
//		Properties prop = new Properties();
//		// 读取属性文件a.properties
//		InputStream in = new BufferedInputStream(new FileInputStream(ConstantUtil.BASIC_PATH));
//		prop.load(in); /// 加载属性列表
//		Iterator<String> it = prop.stringPropertyNames().iterator();
//		while (it.hasNext()) {
//			String key1 = it.next();
//			System.out.println("打印== "+key1 + ":" + prop.getProperty(key1));
//		}
//		in.close();
//		
//		Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE); // 要匹配的字段内容，正则表达式   
//		Matcher matcher = pattern.matcher("");  
//		List<String> lines = Files.readAllLines(Paths.get(ConstantUtil.BASIC_PATH)); // 读取文本文件   
//	
//		for (int i = 0; i < lines.size(); i++) {
//			System.out.println(lines.get(i));
//			matcher.reset(lines.get(i));
//			if (matcher.find()) { // 匹配正则表达式
//				lines.remove(i);
//				lines.add(i, value);
//			}
//		}
//		
//		Files.write(Paths.get(ConstantUtil.BASIC_PATH), lines);  
//		
//	}
//	

}
