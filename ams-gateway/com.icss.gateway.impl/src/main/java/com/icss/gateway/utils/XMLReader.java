package com.icss.gateway.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class XMLReader {
	
	@SuppressWarnings("unchecked")
	public static String readSQL(String id, Map<String, String> params) {
		String result = null;
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(XMLReader.class.getClassLoader().getResource("SQL.xml"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element elem =  doc.getRootElement();
		List<Element> elems=  elem.elements("sql");
		for (Element element : elems) {
			if(element.attribute("id").getValue().equals(id)) {
				result = element.getText();
			}
		}
		if(params != null) {
			Set<String> set = params.keySet();
			for (String key : set) {
				result = result.replace("#"+key+"#", params.get(key));
			}
		}
		return result.replaceAll("\n", " ");
	}

}
