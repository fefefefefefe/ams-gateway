package utils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PoiPrint {
	/**
	 * poi 通过读取word模板 写入
	 * @param request
	 * @param obj
	 * @param response
	 * @param filename
	 * @param outname
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void poiPintnWord(HttpServletRequest request, Object obj,
                             HttpServletResponse response, String filename, String outname) throws IntrospectionException, IllegalAccessException, InvocationTargetException{
		String ss = request.getSession().getServletContext().getRealPath("");
		Map map = new HashMap();
		if (obj != null) {
			map = BeanToMapUtil.convertBean(obj);
		}
		readwriteWord(ss +File.separator+"template"+File.separatorChar + filename, map, response, outname);
	}
	
	
	/**
	 * poi 通过读取word模板 写入
	 * @param request
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException 
	 */
	public static InputStream poiAllPintnWord(HttpServletRequest request, Map<String, Object> params, String templateFileName) throws Exception {
		String ss = request.getSession().getServletContext().getRealPath("");
		Map paramMap = new HashMap();
		Map<String, List> paramListMap = new HashMap<String, List>();
		for (Entry<String, Object> entry : params.entrySet()) {
			if("java.util.ArrayList".equalsIgnoreCase(entry.getValue().getClass().getName())){
				List paramList = (List) entry.getValue();
				paramListMap.put(entry.getKey(), paramList);
			} else if("java.util.Map".equalsIgnoreCase(entry.getValue().getClass().getName())) {
				paramMap.putAll((Map)entry.getValue());
			} else {
				Map map = BeanToMapUtil.convertBean(entry.getValue());
				paramMap.putAll(map);
			}
		}
		
		String filePath = ss +File.separator+"template"+File.separatorChar + templateFileName;
		Map<String, Object> map = paramMap;
		XWPFDocument doc = new XWPFDocument(new FileInputStream(new File(filePath)));
		//XWPFDocument doc = null;
		Iterator<XWPFTable> iterator = doc.getTablesIterator();
		while (iterator.hasNext()) {
			XWPFTable table = iterator.next();
			int rownums = table.getRows().size();
			List styleCellList = new ArrayList();
			List colMarket=new ArrayList();
			String listName = "";
			int configRowNum = -1;
			for (int i = 0; i < rownums; i++) {//循 环行
				XWPFTableRow row = table.getRows().get(i);
				String cellText = row.getCell(0).getText();
				if(cellText.indexOf("$list{")>=0){
					for(int j = 0 ; j < row.getTableCells().size() ; j++){
						listName = cellText.substring(cellText.indexOf("{")+1, cellText.indexOf(".")).trim();
						String col1Text = row.getCell(j).getText();
						styleCellList.add(row.getCell(j));
						if(StringUtil.isNotEmpty(col1Text)){
							col1Text = col1Text.substring(col1Text.indexOf(".")+1, col1Text.indexOf("}")).trim();
						}
						colMarket.add(col1Text);
					}
					configRowNum = i;
				}								
			}
			
			if(configRowNum!=-1){
				List paramList = paramListMap.get(listName);
				for (int k = 0; k < paramList.size(); k++) {
					Map paramMapTemp = BeanToMapUtil.convertBean(paramList.get(k));
						XWPFTableRow row = table.createRow();
						int rowsize = row.getTableCells().size();
						for(int x=0; x<(colMarket.size() - rowsize ) ;x++){
							row.addNewTableCell();
						}
						for(int j=0;j<colMarket.size();j++){
							String cellName = (String) colMarket.get(j);
							if(cellName!=null){
								XWPFTableCell cell0 = row.getCell(j);
								Object value = paramMapTemp.get(cellName);
								//昆山沿革模块 因为时间数据设置为timestamp 需要特定手动截取
								String strValue=value.toString();
								if(cellName.indexOf("Time")>=0){
									strValue=strValue.substring(0,10);
								}
								setCellText(cell0, value==null?"":strValue,(XWPFTableCell)styleCellList.get(j),null,null);
							}
						}
				}
			} else {
				boolean hasParam = false;
				for (int i = 0; i < rownums; i++) {//循 环行
					XWPFTableRow row = table.getRows().get(i);
					for(int j = 0 ; j < row.getTableCells().size() ; j++){
							Matcher matcher = null;
							XWPFTableCell cell = row.getCell(j);
							for (int k = 0; k < cell.getParagraphs().size(); k++) {
								XWPFParagraph Para = cell.getParagraphs().get(k);
								if(!Para.getRuns().isEmpty()){
									XWPFRun styleRun = Para.getRuns().get(0);
									String ParaText = Para.getText();
									boolean flog = true;
									while ((matcher = matcher(ParaText)).find()) {
					                	String str = matcher.group();
					                	str = str.substring(2,str.length()-1);
					                	String strAll = "\\$\\{"+str+"\\}";
					                	Object marketValue = (Object) paramMap.get(str);
					                	ParaText = ParaText.replaceAll( strAll, StringUtil.isEmpty(marketValue==null?"":marketValue+"")?"":marketValue+"");
					                	flog = false;
					                }
									if(flog){
										continue;
									}
									XWPFRun run1 = Para.createRun();
								    run1.setText(ParaText);
									run1.setFontFamily(styleRun.getFontFamily());
									run1.setFontSize(styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize());
									run1.setBold(styleRun.isBold());
									run1.setTextPosition(styleRun.getTextPosition());
									Para.setAlignment(Para.getAlignment());
									Para.setVerticalAlignment(Para.getVerticalAlignment());
									
									List runs = Para.getRuns();
									int size  = runs.size();
								 	for(int l=0;l<size-1;l++){
								 		Para.removeRun(0);
								 	}
								}
								
							}
							/*if(hasParam){
								updCellText(row.getCell(j), col1Text,row.getCell(j));
							}*/
					}								
				}
			}
			
			for (int i = 0; i < rownums; i++) {//循 环行
				XWPFTableRow row = table.getRows().get(i);
				if(i==configRowNum){//删除配置行
					table.removeRow(i);
				}
			}
		}
		
		ByteArrayOutputStream on= new ByteArrayOutputStream();
		doc.write(on);
		ByteArrayInputStream in =new ByteArrayInputStream(on.toByteArray());
		return in;
	}
	
	
	/**
	 * poi 通过读取word模板 写入
	 * @param request
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException 
	 */
	public static InputStream poiAllParagraphPintnWord(HttpServletRequest request, Map<String, Object> params, String templateFileName) throws Exception {
		String ss = request.getSession().getServletContext().getRealPath("");
		Map paramMap = new HashMap();
		Map<String, List> paramListMap = new HashMap<String, List>();
		for (Entry<String, Object> entry : params.entrySet()) {
			if("java.util.ArrayList".equalsIgnoreCase(entry.getValue().getClass().getName())){
				List paramList = (List) entry.getValue();
				paramListMap.put(entry.getKey(), paramList);
			} else if("java.util.Map".equalsIgnoreCase(entry.getValue().getClass().getName())) {
				paramMap.putAll((Map)entry.getValue());
			} else {
				Map map = BeanToMapUtil.convertBean(entry.getValue());
				paramMap.putAll(map);
			}
		}
		
		String filePath = ss +File.separator+"template"+File.separatorChar + templateFileName;
		Map<String, Object> map = paramMap;
//		// 读取word模板
		
		XWPFDocument doc = new XWPFDocument(new FileInputStream(new File(filePath)));
		//XWPFDocument doc = null;
		List<XWPFParagraph> paragraphList = doc.getParagraphs();
		Matcher matcher = null;
		for (XWPFParagraph Para : paragraphList) {
			if(!Para.getRuns().isEmpty()){
				XWPFRun styleRun = Para.getRuns().get(0);
				String ParaText = Para.getText();
				while ((matcher = matcher(ParaText)).find()) {
                	String str = matcher.group();
                	str = str.substring(2,str.length()-1);
                	String strAll = "\\$\\{"+str+"\\}";
                	Object marketValue = (Object) paramMap.get(str);
                	ParaText = ParaText.replaceAll( strAll, StringUtil.isEmpty(marketValue+"")?"":marketValue+"");
                }
				XWPFRun run1 = Para.createRun();
			    run1.setText(ParaText);
				run1.setFontFamily(styleRun.getFontFamily());
				run1.setFontSize(styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize());
				run1.setBold(styleRun.isBold());
				run1.setTextPosition(styleRun.getTextPosition());
				Para.setAlignment(Para.getAlignment());
				Para.setVerticalAlignment(Para.getVerticalAlignment());
				
				List runs = Para.getRuns();
				int size  = runs.size();
			 	for(int l=0;l<size-1;l++){
			 		Para.removeRun(0);
			 	}
			}
		}
		
		ByteArrayOutputStream on= new ByteArrayOutputStream();
		doc.write(on);
		ByteArrayInputStream in =new ByteArrayInputStream(on.toByteArray());
		return in;
	}
	

	 public static  void updCellText(XWPFTableCell cell, String text, XWPFTableCell styleCell) {
		 	if(text == null){
		 		text = "";
		 	}
		 	if(styleCell.getParagraphs()!=null &&
		 			styleCell.getParagraphs().get(0) != null && 
		 			styleCell.getParagraphs().get(0).getRuns() !=null &&
		 			styleCell.getParagraphs().get(0).getRuns().size()>0 && 
		 			styleCell.getParagraphs().get(0).getRuns().get(0) !=null){
		 		XWPFRun styleRun = styleCell.getParagraphs().get(0).getRuns().get(0);
		 		for (int j = 0; j < styleCell.getParagraphs().size(); j++) {
				    XWPFParagraph para1 = cell.getParagraphs().get(j);
				    XWPFRun run1 = para1.createRun();
			 		run1.setText(text);
					run1.setFontFamily(styleRun.getFontFamily());
					run1.setFontSize(styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize());
					run1.setBold(styleRun.isBold());
					run1.setTextPosition(styleRun.getTextPosition());
					
					para1.setAlignment(styleCell.getParagraphs().get(0).getAlignment());
					para1.setVerticalAlignment(styleCell.getParagraphs().get(0).getVerticalAlignment());
					
					List runs = cell.getParagraphs().get(0).getRuns();
					int size  = runs.size();
				 	for(int i=0;i<size-1;i++){
				 		cell.getParagraphs().get(0).removeRun(0);
				 	}
				}
		 	}else{
		 		cell.setText(text);
		 	}
	 }
	
	public static  void setCellText(XWPFTableCell cell, String text, XWPFTableCell styleCell, Integer fontSize, ParagraphAlignment align) {
	
	if(text == null){
		text = "";
	}
	
	List list = styleCell.getParagraphs().get(0).getRuns();
	if(!list.isEmpty()){
		XWPFRun styleRun = (XWPFRun) list.get(0);
		
		String []s = text.split("\n");//按回车符分割字符   
		if (s.length==1) {  
			XWPFParagraph para1 = cell.getParagraphs().get(0);
		    XWPFRun run1 = para1.createRun();
			run1.setText(text);
			run1.setFontFamily(styleRun.getFontFamily());
			if(fontSize==null){
				fontSize = styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize();
			}
			if(align==null){
				align = styleCell.getParagraphs().get(0).getAlignment();
			}
			run1.setFontSize(fontSize);
			run1.setBold(styleRun.isBold());
			run1.setTextPosition(styleRun.getTextPosition());
			para1.setAlignment(align);
			para1.setVerticalAlignment(styleCell.getParagraphs().get(0).getVerticalAlignment()); 
		}else{
			XWPFParagraph para1 = cell.getParagraphs().get(0);
		    XWPFRun run1 = para1.createRun();
			run1.setText(s[0]);
			run1.setFontFamily(styleRun.getFontFamily());
			if(fontSize==null){
				fontSize = styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize();
			}
			if(align==null){
				align = styleCell.getParagraphs().get(0).getAlignment();
			}
			run1.setFontSize(fontSize);
			run1.setBold(styleRun.isBold());
			run1.setTextPosition(styleRun.getTextPosition());
			para1.setAlignment(align);
			para1.setVerticalAlignment(styleCell.getParagraphs().get(0).getVerticalAlignment()); 
			for (int i = 1; i < s.length; i++) {
				XWPFParagraph p = cell.addParagraph();//添加新段落
			    XWPFRun run = p.createRun();
			    run.setText(s[i]);
			    run.setFontFamily(styleRun.getFontFamily());
				if(fontSize==null){
					fontSize = styleRun.getFontSize()==-1?new Integer(10):styleRun.getFontSize();
				}
				if(align==null){
					align = styleCell.getParagraphs().get(0).getAlignment();
				}
				run.setFontSize(fontSize);
				run.setBold(styleRun.isBold());
				run.setTextPosition(styleRun.getTextPosition());
				p.setAlignment(align);
				p.setVerticalAlignment(styleCell.getParagraphs().get(0).getVerticalAlignment());
			}
		}
	}
 }
	
	 /**
	    * 正则匹配字符串
	    * @param str
	    * @return
	    */
	   private static Matcher matcher(String str) {
	      Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
	      Matcher matcher = pattern.matcher(str);
	      return matcher;
	   }
	
	
	/**
	 * 实现对word2003读取和修改操作
	 *
	 * @param filePath
	 *            word模板路径和名称
	 * @param map
	 *            待填充的数据，从数据库读取
	 */
	public static void readwriteWord(String filePath, Map<String, String> map, HttpServletResponse response, String outname) {
		// 读取word模板
		FileInputStream in = null;
		HWPFDocument hdt = null;
		try {
			in = new FileInputStream(new File(filePath));
			hdt = new HWPFDocument(in);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 读取word文本内容
		Range range = hdt.getRange();
	    System.out.println(range.text());
		// 替换文本内容
		for (Entry<String, String> entry : map.entrySet()) {
			try{
				if(!"orderByList".equals(entry.getKey())&&!"modifiedProperties".equals(entry.getKey())){
					range.replaceText("${"+entry.getKey()+"}", entry.getValue().toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 输出文件
		String name = outname;// "接收资料回执";
		response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.ms-excel;application/msword;charset=UTF-8");
       // fileName = URLEncoder.encode(fileName, "UTF-8");
		try {
			response.setHeader("Content-Disposition", "attachment;filename="+ new String((name + ".doc").getBytes("UTF-8"), "UTF-8"));//iso-8859-1
			OutputStream ostream = response.getOutputStream();
			// //输出到本地文件的话，new一个文件流
			// FileOutputStream ostream = new
			// FileOutputStream("D:/test/4444444.doc");
			hdt.write(ostream);
			ostream.close();
		} catch (Exception e) {
		}
	}
	/**
	 * 2007读取写入模板
	 * @param srcPath
	 * @param destPath
	 * @param map
	 */
	public static void searchAndReplace(String srcPath, String destPath,
                                        Map<String, String> map, HttpServletResponse response, String outname) {
		try {

			XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(srcPath));
			Iterator it = document.getTablesIterator();
			while (it.hasNext()) {
				XWPFTable table = (XWPFTable) it.next();
				int rcount = table.getNumberOfRows();
				for (int i = 0; i < rcount; i++) {
					XWPFTableRow row = table.getRow(i);
					List<XWPFTableCell> cells = row.getTableCells();
					for (XWPFTableCell cell : cells) {
						for (Entry<String, String> e : map.entrySet()) {
							if (cell.getText().equals("${"+e.getKey()+"}")) {
								cell.removeParagraph(0);
								cell.setText(e.getValue());
							}
						}
					}
				}
			}
			
			// 输出文件
			String name = outname;// "接收资料回执";
			response.reset();
			response.setHeader("Content-Disposition", "attachment;filename="+ new String((name + ".doc").getBytes("UTF-8"), "UTF-8"));//  "iso-8859-1"
			response.setContentType("application/msword");
			OutputStream ostream = response.getOutputStream();
			// //输出到本地文件的话，new一个文件流

			// FileOutputStream ostream = new
			// FileOutputStream("D:/4444444.doc");
			document.write(ostream);
			ostream.close();
//			FileOutputStream outStream = null;
//			outStream = new FileOutputStream(destPath);
//			document.write(outStream);
//			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出到读取txt word
	 * 
	 * @param request
	 *            request对象
	 * @param obj
	 *            JAVA对象实体类
	 * @param response
	 *            response对象
	 * @param filename
	 *            模板名称
	 * @param outname
	 *            输出文件名称
	 * @throws Exception
	 */
	public void exportWord(HttpServletRequest request, Object obj, HttpServletResponse response, String filename, String outname)
			throws Exception {

		try {
			/***
			 * 查询数据库获得数据
			 **/
			String ss = request.getSession().getServletContext().getRealPath("");
			// word内容
			String content = readTxtFile(ss + "/template/" + filename);// 拼接注意加上<html>
			Map map = new HashMap();
			if (obj != null) {
				map = BeanToMapUtil.convertBean(obj);
			}
			// html拼接出word内容
			Iterator ite = map.entrySet().iterator();
			while (ite.hasNext()) {
				Entry<Object, Object> entry = (Entry<Object, Object>) ite.next();
				Object key = entry.getKey();// map中的key
				Object value = entry.getValue();// 上面key对应的value
				content = content.replace("${" + key.toString() + "}",value.toString());
			}
			byte b[] = content.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			POIFSFileSystem poifs = new POIFSFileSystem();
			DirectoryEntry directory = poifs.getRoot();
			DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
			// 输出文件
			String name = outname;// "接收资料回执";
			response.reset();
			response.setCharacterEncoding("UTF-8");
	        response.setContentType("application/vnd.ms-excel;application/msword;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename="+ new String((name + ".doc").getBytes("UTF-8"),"UTF-8"));//"iso-8859-1"
			//response.setContentType("application/msword");
			OutputStream ostream = response.getOutputStream();
			// //输出到本地文件的话，new一个文件流

			// FileOutputStream ostream = new
			// FileOutputStream("D:/test/4444444.doc");
			poifs.writeFilesystem(ostream);
			bais.close();
			ostream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
	 * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
	 * 
	 * @param filePath
	 */
	public static String readTxtFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			//System.out.println(file.getAbsoluteFile());
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getWordString(InputStream in){
		String str = "";
		try {
			HWPFDocument document = new HWPFDocument(in);
			str = document.getText()+"";
		} catch (Exception e) {
			try {
				WordExtractor ex = new WordExtractor(in);   
				str = ex.getText();
			} catch (IOException e1) {
				e1.printStackTrace();
			}   
		}
		return str;
		
	}
	
	@SuppressWarnings("finally")
	public static SXSSFWorkbook exportDataIntoExcel(ResultSet resultSet, List<String> listString) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount=0;
		//每个工作薄显示50000条数据
		int perPageNum = 50000;
		String title = "模型预览数据";
		Sheet sh = null;
		Row row = null;//创建一行
    	Cell cell = null;
    	CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			// 创建工作表对象
			int rowNumber = 0;
	        ResultSetMetaData rsmd = resultSet.getMetaData();
	        while (resultSet.next()) {
	        	if (rowNumber%perPageNum == 0) {
	        		bodyRowCount = 0;
	        		sh = wb.createSheet(title + rowNumber/perPageNum);
	        		sh = wb.getSheetAt(rowNumber/perPageNum);
	        		row = sh.createRow(bodyRowCount);
	        		for (int j = 0; j < rsmd.getColumnCount(); j++) {
	        			String colName = rsmd.getColumnLabel(j+1);
        				cell = row.createCell(j);
						cell.setCellValue(colName);
	        		}
	        	}
	        	rowNumber++;
	        	bodyRowCount++;		//正文内容行号自增
	        	row = sh.createRow(bodyRowCount);
	        	for (int j = 0; j < rsmd.getColumnCount(); j++) {
	        		String c = resultSet.getString(j+1);
	        		String colName = rsmd.getColumnLabel(j+1);
	        		if(listString.contains(colName) ) {
	        			row.createCell(j).setCellValue(StringUtil.getEncryptColu(c));
	        		}else {
	        			row.createCell(j).setCellValue(c);
	        		}
        		}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return wb;
		}
	}
	
	
	public static void export(HttpServletResponse response, SXSSFWorkbook workBook, String fileName) throws Exception {
		OutputStream fileOut = null; 
		try {
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/vnd.ms-excel;application/msword;charset=UTF-8");
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes("UTF-8"), "UTF-8") );//

			fileOut = response.getOutputStream();
			workBook.write(fileOut);
			fileOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileOut){
					fileOut.close();
					//outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 导出数据到xlsx
	 * doubtfulDataList：要导出的结果集
	 * clNames：
	 * clTypes：
	 * clLengths：
	 * @return
	 */
	public static SXSSFWorkbook exportExcel(List<Map<String, Object>> doubtfulDataList, String clNames, String clTypes,
                                            String clLengths, String cNamess) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clNameStr = cNamess.split(",");
			//String[] oldClType = clTypes.split(",");
			
			if(doubtfulDataList == null) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				bodyRowCount++;// 正文内容行号递增1
				
			}else {
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					if (rowNumber % perPageNum == 0) {
						sh = wb.createSheet("标题" + rowNumber / perPageNum);
						sh = wb.getSheetAt(rowNumber / perPageNum);
						bodyRowCount = 0;
					}
					rowNumber++;
					row = sh.createRow(bodyRowCount);
					Map<String, Object> map = doubtfulDataList.get(i);
					if(map != null) {//jsy 10-12 add
						for (int j = 0; j < clName.length; j++) {
							if (bodyRowCount == 0) {
								cell = row.createCell(j);
								cell.setCellValue(clNameStr[j]);
							}
							if (bodyRowCount == 0 && j == clName.length - 1) {
								bodyRowCount++;// 正文内容行号递增1
								row = sh.createRow(bodyRowCount);
							}
						}
						for (int j = 0; j < clName.length; j++) {
							if (map.get(clName[j]) == null) {
								row.createCell(j).setCellValue("");
								continue;
							}
							if (bodyRowCount != 0) {
								if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
									if(map.get(clName[j]).toString().indexOf(" CST") != -1) {
										row.createCell(j).setCellValue(StringUtil.databaseDate(map.get(clName[j]).toString()));
									}else {
										row.createCell(j).setCellValue(map.get(clName[j]).toString());
									}
								} else {
									row.createCell(j).setCellValue("");
								}
							}
						}
						bodyRowCount++;// 正文内容行号递增1
					}
					
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}

	/**
	 * 导出数据到xlsx 支持到处多个sheet  ZhangSiWeiG
	 * doubtfulDataList：要导出的结果集列表
	 * clNames：列名
	 * clTypes：列类型
	 * clLengths：列长度信息
	 * @return
	 */
	public static HSSFWorkbook exportExcelNSheets(List<List<Map<String, Object>>> doubtfulDataList, List<String> clNames, List<String> pageNames) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		HSSFSheet sh = null;
		Row row = null;// 创建一行
		CellStyle style = wb.createCellStyle();
		Cell cell = null;
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		style.setBorderBottom(BorderStyle.THIN); //下边框
		style.setBorderLeft(BorderStyle.THIN);//左边框
		style.setBorderTop(BorderStyle.THIN);//上边框
		style.setBorderRight(BorderStyle.THIN);//右边框
		try {
			int rowNumber = 0;
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					String[] clName = clNames.get(i).split(",");
					String sheetName = pageNames.get(i);
					sh = wb.createSheet(sheetName);//创建sheet页
					sh = wb.getSheetAt(rowNumber);
					//sh.setRandomAccessWindowSize(-1);
					bodyRowCount = 0;
					rowNumber++;
					if (bodyRowCount == 0) {
						Font headerFont = wb.createFont();
				        headerFont.setFontHeightInPoints((short) 20);//设置大小
				        headerFont.setColor(IndexedColors.BLACK.getIndex());//设置字体颜色
						row = sh.createRow(bodyRowCount);//创建行
						CellStyle newCellStyle = getCellStyleColumn(wb);
						for (int j = 0; j < clName.length; j++) {
							sh.setColumnWidth(j, 5000);
							cell = row.createCell(j);//创建格子
							cell.setCellValue(clName[j]);//设置格子的值
							//newCellStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
							newCellStyle.setFont(headerFont);
							cell.setCellStyle(newCellStyle);
						}
						bodyRowCount ++;
					}
					List<Map<String, Object>> dataList = doubtfulDataList.get(i);
					for(int rowData = 0; rowData < dataList.size(); rowData++) {
						row = sh.createRow(bodyRowCount);
						Map<String, Object> map = dataList.get(rowData);
						if(map != null) {
							for (int j = 0; j < clName.length; j++) {
								if (map.get(clName[j]) == null) {
									row.createCell(j).setCellValue("");
									continue;
								}
								if (bodyRowCount != 0) {
									if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
										cell = row.createCell(j);
										cell.setCellValue(map.get(clName[j]).toString());
										cell.setCellStyle(style);
									} else {
										cell = row.createCell(j);
										cell.setCellValue("");
										cell.setCellStyle(style);
										
									}
								}
							}
							bodyRowCount++;// 正文内容行号递增1
						}
					}
				}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}
	
	private static CellStyle getCellStyleColumn(HSSFWorkbook wb) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		style.setBorderBottom(BorderStyle.THIN); //下边框
		style.setBorderLeft(BorderStyle.THIN);//左边框
		style.setBorderTop(BorderStyle.THIN);//上边框
		style.setBorderRight(BorderStyle.THIN);//右边框
		style.setFillBackgroundColor(Short.parseShort("25"));
		return style;
	}
	
	public static SXSSFWorkbook exportExcel(List<Map<String, Object>> doubtfulDataList, String[] clNames, String[] clTypes,
                                            String[] clLengths, List list) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames;
			//String[] oldClType = clTypes;
			if(doubtfulDataList == null) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				Map<String, Object> map = null;
				for (int j = 0; j < clName.length; j++) {
					if (map.get(clName[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount != 0) {
						if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
				
							row.createCell(j).setCellValue(map.get(clName[j]).toString());
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
				
			}

			for (int i = 0; i < doubtfulDataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				Map<String, Object> map = doubtfulDataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clName.length; j++) {
					if (map.get(clName[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount != 0) {
						if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
							/*String oldtype = oldClType==null?"-1":oldClType[j];
							String value = CommonMethod.getColumnValue(map.get(clName[j]).toString(), oldtype);
							value = value.replace("'", "");
							if(list.contains(clName[j])) { //如果列名在LIST中 则进行脱敏
								value = StringUtil.getEncryptColu(value); //脱敏	
							}*/
							row.createCell(j).setCellValue(map.get(clName[j]).toString());
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}

	/**
	 * 导出数据到xls
	 * doubtfulDataList：要导出的结果集
	 * clNames：
	 * clTypes：
	 * clLengths：
	 * @return
	 */
	public static HSSFWorkbook exportExcelLow(List<Map<String, Object>> doubtfulDataList , String clNames, String clTypes, String clLengths, String cNamess) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		//fuhang，2021-08-18 添加表头自动换行
        style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        //垂直对齐
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        //下边框
        style.setBorderBottom(BorderStyle.THIN);
        //左边框
        style.setBorderLeft(BorderStyle.THIN);
        //右边框
        style.setBorderRight(BorderStyle.THIN);
        //上边框
        style.setBorderTop(BorderStyle.THIN);
        //fh 2021.11.02设置前景颜色 修复wps展示正常，office展示变黑问题
        style.setFillForegroundColor(IndexedColors.WHITE.index);
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clNamestr = cNamess.split(",");
			//String[] oldClType = clTypes.split(",");

			if(doubtfulDataList == null || doubtfulDataList.size() == 0) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
                        cell.setCellStyle(style);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
                        cell.setCellStyle(style);
					}
				}
				bodyRowCount++;// 正文内容行号递增1

			}else {
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					if (rowNumber % perPageNum == 0) {
						sh = wb.createSheet("标题" + rowNumber / perPageNum);
						sh = wb.getSheetAt(rowNumber / perPageNum);
						bodyRowCount = 0;
					}
					rowNumber++;
					row = sh.createRow(bodyRowCount);
					Map<String, Object> map = doubtfulDataList.get(i);
					for (int j = 0; j < clName.length; j++) {
						if (bodyRowCount == 0) {
							cell = row.createCell(j);
							cell.setCellValue(clNamestr[j]);//clName[j]
                            cell.setCellStyle(style);
						}
						if (bodyRowCount == 0 && j == clName.length - 1) {
							bodyRowCount++;// 正文内容行号递增1
							row = sh.createRow(bodyRowCount);
						}
					}
					for (int j = 0; j < clName.length; j++) {
						if (bodyRowCount == 0) {
							cell = row.createCell(j);
							cell.setCellValue(clNamestr[j]);//clName[j]
                            cell.setCellStyle(style);
						}
						if (bodyRowCount == 0 && j == clName.length - 1) {
							bodyRowCount++;// 正文内容行号递增1
							row = sh.createRow(bodyRowCount);
						}
					}
					for (int j = 0; j < clName.length; j++) {
						if (map.get(clName[j]) == null) {
							row.createCell(j).setCellValue("");
							continue;
						}
						if (bodyRowCount != 0) {
							if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
								if(map.get(clName[j]).toString().indexOf(" CST") != -1) {
									row.createCell(j).setCellValue(StringUtil.databaseDate(map.get(clName[j]).toString()));
								}else {
									row.createCell(j).setCellValue(map.get(clName[j]).toString());
								}
							} else {
								row.createCell(j).setCellValue("");
							}
						}
					}
					bodyRowCount++;// 正文内容行号递增1
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}

	/**
	 * 导出数据到xls ZhangSiWei2021年7月21日 09:55:30新增  原因：列名中包含逗号无法识别
	 * doubtfulDataList：要导出的结果集
	 * clNames：
	 * clTypes：
	 * clLengths：
	 * @return
	 */
	public static HSSFWorkbook exportExcelLow(List<Map<String, Object>> doubtfulDataList , String[] clNames, String clTypes, String clLengths, String[] cNamess) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames;
			String[] clNamestr = cNamess;
			//String[] oldClType = clTypes.split(",");
			
			if(doubtfulDataList == null || doubtfulDataList.size() == 0) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				bodyRowCount++;// 正文内容行号递增1
				
			}else {
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					if (rowNumber % perPageNum == 0) {
						sh = wb.createSheet("标题" + rowNumber / perPageNum);
						sh = wb.getSheetAt(rowNumber / perPageNum);
						bodyRowCount = 0;
					}
					rowNumber++;
					row = sh.createRow(bodyRowCount);
					Map<String, Object> map = doubtfulDataList.get(i);
					for (int j = 0; j < clName.length; j++) {
						if (bodyRowCount == 0) {
							cell = row.createCell(j);
							cell.setCellValue(clNamestr[j]);//clName[j]
							
						}
						if (bodyRowCount == 0 && j == clName.length - 1) {
							bodyRowCount++;// 正文内容行号递增1
							row = sh.createRow(bodyRowCount);
						}
					}
					for (int j = 0; j < clName.length; j++) {
						if (bodyRowCount == 0) {
							cell = row.createCell(j);
							cell.setCellValue(clNamestr[j]);//clName[j]
						}
						if (bodyRowCount == 0 && j == clName.length - 1) {
							bodyRowCount++;// 正文内容行号递增1
							row = sh.createRow(bodyRowCount);
						}
					}
					for (int j = 0; j < clName.length; j++) {
						if (map.get(clName[j]) == null) {
							row.createCell(j).setCellValue("");
							continue;
						}
						if (bodyRowCount != 0) {
							if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
								if(map.get(clName[j]).toString().indexOf(" CST") != -1) {
									row.createCell(j).setCellValue(StringUtil.databaseDate(map.get(clName[j]).toString()));
								}else {
									row.createCell(j).setCellValue(map.get(clName[j]).toString());
								}
							} else {
								row.createCell(j).setCellValue("");
							}
						}
					}
					bodyRowCount++;// 正文内容行号递增1
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}
	
	public static HSSFWorkbook exportExcelLowBak(List<Map<String, Object>> doubtfulDataList, String clNames, String clTypes, String clLengths) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] oldClType = clTypes.split(",");

			for (int i = 0; i < doubtfulDataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				Map<String, Object> map = doubtfulDataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clName.length; j++) {
					if (map.get(clName[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount != 0) {
						if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
							String value = CommonMethod.getColumnValue(map.get(clName[j]).toString(), oldClType[j]);
							value = value.replace("'", "");
							row.createCell(j).setCellValue(value);
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}
	
	/**
	 * 导出数据到xls
	 * dataList：要导出的列表
	 * clNames：excel 列表头
	 * clTypes：excel 列类型 
	 * clColumns：excel 列字段名
	 * @return
	 */
	@SuppressWarnings("finally")
	public static HSSFWorkbook normalLessLevelExportExcel(List<Map<String, Object>> dataList, String clNames, String clTypes,
                                                          String clColumns) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clColumn = clColumns.split(",");
			String[] oldClType = clTypes.split(",");

			for (int i = 0; i < dataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				Map<String, Object> map = dataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clColumn.length; j++) {
					if (map.get(clColumn[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount != 0) {
						if (StringUtil.isNotEmpty(map.get(clColumn[j]).toString())) {
							String value = CommonMethod.getColumnValue(map.get(clColumn[j]).toString(), oldClType[j]);
							value = value.replace("'", "");
							row.createCell(j).setCellValue(value);
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return wb;
		}
	}
	
	
	/**
	 * 导出数据到xls 特别设置表头样式的
	 * dataList：要导出的列表
	 * clNames：excel 列表头
	 * clTypes：excel 列类型 
	 * clColumns：excel 列字段名
	 * @return
	 */
	@SuppressWarnings("finally")
	public static HSSFWorkbook normalXlsLevelExportExcel(List<Map<String, Object>> dataList, String clNames, String clTypes,
                                                         String clColumns) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clColumn = clColumns.split(",");
			String[] oldClType = clTypes.split(",");

			HSSFCellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			Font ztFont = wb.createFont();
			ztFont.setFontHeightInPoints((short)22);
			ztFont.setFontName("微软简仿宋");
			titleStyle.setFont(ztFont);
			
			HSSFCellStyle titleStyle1 = wb.createCellStyle();
			titleStyle1.setAlignment(HorizontalAlignment.CENTER);
			Font ztFont1 = wb.createFont();
			ztFont1.setFontHeightInPoints((short)12);
			ztFont1.setFontName("微软简仿宋");
			ztFont1.setBold(true);
			titleStyle1.setFont(ztFont1);
			
			for (int i = 0; i < dataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					//设置列宽
					sh.setColumnWidth(0, 252*11+323);
					sh.setColumnWidth(1, 252*18+323);
					sh.setColumnWidth(2, 252*30+323);
					sh.setColumnWidth(3, 252*30+323);
					sh.setColumnWidth(4, 252*22+323);
					sh.autoSizeColumn(5, true);
					bodyRowCount = 1;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				if (bodyRowCount == 1) {
					cell = row.createCell(0);
					cell.setCellValue("汉化表");
					cell.setCellStyle(titleStyle);
					sh.addMergedRegion(new CellRangeAddress(1,1,0,clColumn.length));
					bodyRowCount++;
					row = sh.createRow(bodyRowCount);
				}
				Map<String, Object> map = dataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 2) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
						cell.setCellStyle(titleStyle1);
					}
					if (bodyRowCount == 2 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clColumn.length; j++) {
					if (map.get(clColumn[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount > 2) {
						if (StringUtil.isNotEmpty(map.get(clColumn[j]).toString())) {
							String value = CommonMethod.getColumnValue(map.get(clColumn[j]).toString(), oldClType[j]);
							value = value.replace("'", "");
							row.createCell(j).setCellValue(value);
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return wb;
		}
	}
	
	/**
	 * 导出数据到xlsx 特别设置表头样式
	 * dataList：要导出的结果集
	 * clNames：
	 * clTypes：
	 * clColumns：
	 * @return
	 */
	@SuppressWarnings("finally")
	public static SXSSFWorkbook commonXlsxLevelExportExcel(List<Map<String, Object>> dataList, String clNames, String clTypes, String clColumns) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clColumn = clColumns.split(",");
			String[] oldClType = clTypes.split(",");

			CellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			Font ztFont = wb.createFont();
			ztFont.setFontHeightInPoints((short)24);
			ztFont.setFontName("微软简仿宋");
			titleStyle.setFont(ztFont);
			
			CellStyle titleStyle1 = wb.createCellStyle();
			titleStyle1.setAlignment(HorizontalAlignment.CENTER);
			Font ztFont1 = wb.createFont();
			ztFont1.setFontHeightInPoints((short)12);
			ztFont1.setFontName("微软简仿宋");
			ztFont1.setBold(true);
			titleStyle1.setFont(ztFont1);
			
			for (int i = 0; i < dataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					//设置列宽
					sh.setColumnWidth(0, 252*11+323);
					sh.setColumnWidth(1, 252*18+323);
					sh.setColumnWidth(2, 252*30+323);
					sh.setColumnWidth(3, 252*30+323);
					sh.setColumnWidth(4, 252*22+323);
					bodyRowCount = 1;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				
				if (bodyRowCount == 1) {
					cell = row.createCell(0);
					cell.setCellValue("汉化表");
					cell.setCellStyle(titleStyle);
					sh.addMergedRegion(new CellRangeAddress(1,1,0,clColumn.length));
					bodyRowCount++;
					row = sh.createRow(bodyRowCount);
				}
				
				Map<String, Object> map = dataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 2) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
						cell.setCellStyle(titleStyle1);
					}
					if (bodyRowCount == 2 && j == clColumn.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clColumn.length; j++) {
					if (map.get(clColumn[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount > 2) {
						if (StringUtil.isNotEmpty(map.get(clColumn[j]).toString())) {
							String value = CommonMethod.getColumnValue(map.get(clColumn[j]).toString(), oldClType[j]);
							value = value.replace("'", "");
							row.createCell(j).setCellValue(value);
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return wb;
		}
	}
	
	/**
	 * 导出数据到xlsx
	 * dataList：要导出的结果集
	 * clNames：
	 * clTypes：
	 * clColumns：
	 * @return
	 */
	@SuppressWarnings("finally")
	public static SXSSFWorkbook commonHighLevelExportExcel(List<Map<String, Object>> dataList, String clNames, String clTypes,
                                                           String clColumns) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clColumn = clColumns.split(",");
			String[] oldClType = clTypes.split(",");

			for (int i = 0; i < dataList.size(); i++) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				rowNumber++;
				row = sh.createRow(bodyRowCount);
				Map<String, Object> map = dataList.get(i);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						cell.setCellValue(clName[j]);
					}
					if (bodyRowCount == 0 && j == clColumn.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				for (int j = 0; j < clColumn.length; j++) {
					if (map.get(clColumn[j]) == null) {
						row.createCell(j).setCellValue("");
						continue;
					}
					if (bodyRowCount != 0) {
						if (StringUtil.isNotEmpty(map.get(clColumn[j]).toString())) {
							String value = CommonMethod.getColumnValue(map.get(clColumn[j]).toString(), oldClType[j]);
							value = value.replace("'", "");
							row.createCell(j).setCellValue(value);
						} else {
							row.createCell(j).setCellValue("");
						}
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			return wb;
		}
	}
	
	public static void exportLow(HttpServletResponse response, HSSFWorkbook workBook, String fileName) throws Exception {
		OutputStream fileOut = null; 
		try {
			response.reset();
			response.setCharacterEncoding("UTF-8");
	        response.setContentType("application/vnd.ms-excel;application/msword;charset=UTF-8");
	        fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileName.getBytes("UTF-8"), "UTF-8") );//
			fileOut = response.getOutputStream();
			workBook.write(fileOut);
			fileOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileOut){
					fileOut.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * @Description: 解决下载文件中表头中文乱码的问题
	 * @param doubtfulDataList
	 * @param clNames
	 * @param clTypes
	 * @param clLengths
	 * @param cNamess
	 * @return
	 * @author MiJiaHui 2019年11月19日上午9:28:11
	 */
	public static HSSFWorkbook exportHSSFExcelLinux(List<Map<String, Object>> doubtfulDataList , String clNames, String clTypes, String clLengths, String cNamess) {
		// 创建工作簿对象
		HSSFWorkbook wb = new HSSFWorkbook();
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		String clNameStr ="";
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clNamestr = cNamess.split(",");
			//String[] oldClType = clTypes.split(",");
			
			if(doubtfulDataList == null) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
						//clNameStr = new String(clNamestr[j].getBytes("UTF-8"), "UTF-8");//clNamestr[j].getBytes("UTF-8"), "UTF-8"
						cell.setCellValue(clNamestr[j]);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				bodyRowCount++;// 正文内容行号递增1
			}else {
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					if (rowNumber % perPageNum == 0) {
						sh = wb.createSheet("标题" + rowNumber / perPageNum);
						sh = wb.getSheetAt(rowNumber / perPageNum);
						bodyRowCount = 0;
					}
					rowNumber++;
					row = sh.createRow(bodyRowCount);
					Map<String, Object> map = doubtfulDataList.get(i);
					for (int j = 0; j < clName.length; j++) {
						if (bodyRowCount == 0) {
							cell = row.createCell(j);
							//clNameStr = new String(clNamestr[j].getBytes("UTF-8"), "UTF-8");
							cell.setCellValue(clName[j]);//clName[j]
						}
						if (bodyRowCount == 0 && j == clName.length - 1) {
							bodyRowCount++;// 正文内容行号递增1
							row = sh.createRow(bodyRowCount);
						}
					}
					for (int j = 0; j < clName.length; j++) {
						if (map.get(clName[j]) == null) {
							row.createCell(j).setCellValue("");
							continue;
						}
						if (bodyRowCount != 0) {
							if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
								if(map.get(clName[j]).toString().indexOf(" CST") != -1) {
									row.createCell(j).setCellValue(StringUtil.databaseDate(map.get(clName[j]).toString()));
								}else {
									row.createCell(j).setCellValue(map.get(clName[j]).toString());
								}
							} else {
								row.createCell(j).setCellValue("");
							}
						}
					}
					bodyRowCount++;// 正文内容行号递增1
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @Description: 解决下载文件中表头中文乱码的问题
	 * @param doubtfulDataList
	 * @param clNames
	 * @param clTypes
	 * @param clLengths
	 * @param cNamess
	 * @return
	 * @author MiJiaHui 2019年11月19日上午9:28:11
	 */
	public static SXSSFWorkbook exportSXSSExcelLinux(List<Map<String, Object>> doubtfulDataList, String clNames, String clTypes,
                                                     String clLengths, String cNamess) {
		// 创建工作簿对象
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		int bodyRowCount = 0;
		// 每个工作薄显示50000条数据
		int perPageNum = 50000;
		Sheet sh = null;
		Row row = null;// 创建一行
		Cell cell = null;
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		String clNameStr = "";
		try {
			int rowNumber = 0;
			String[] clName = clNames.split(",");
			String[] clNamesStr = cNamess.split(",");
			//String[] oldClType = clTypes.split(",");
			
			if(doubtfulDataList == null) {
				if (rowNumber % perPageNum == 0) {
					sh = wb.createSheet("标题" + rowNumber / perPageNum);
					sh = wb.getSheetAt(rowNumber / perPageNum);
					bodyRowCount = 0;
				}
				row = sh.createRow(bodyRowCount);
				for (int j = 0; j < clName.length; j++) {
					if (bodyRowCount == 0) {
						cell = row.createCell(j);
//						clNameStr = new String(clNamesStr[j].getBytes("UTF-8"), "UTF-8");
						cell.setCellValue(clNameStr);
					}
					if (bodyRowCount == 0 && j == clName.length - 1) {
						bodyRowCount++;// 正文内容行号递增1
						row = sh.createRow(bodyRowCount);
					}
				}
				bodyRowCount++;// 正文内容行号递增1
				
			}else {
				for (int i = 0; i < doubtfulDataList.size(); i++) {
					if (rowNumber % perPageNum == 0) {
						sh = wb.createSheet("标题" + rowNumber / perPageNum);
						sh = wb.getSheetAt(rowNumber / perPageNum);
						bodyRowCount = 0;
					}
					rowNumber++;
					row = sh.createRow(bodyRowCount);
					Map<String, Object> map = doubtfulDataList.get(i);
					if(map != null) {//jsy 10-12 add
						for (int j = 0; j < clName.length; j++) {
							if (bodyRowCount == 0) {
								cell = row.createCell(j);
								//clNameStr = new String(clNamesStr[j].getBytes("UTF-8"), "UTF-8");
								cell.setCellValue(clNamesStr[j]);
							}
							if (bodyRowCount == 0 && j == clName.length - 1) {
								bodyRowCount++;// 正文内容行号递增1
								row = sh.createRow(bodyRowCount);
							}
						}
						for (int j = 0; j < clName.length; j++) {
							if (map.get(clName[j]) == null) {
								row.createCell(j).setCellValue("");
								continue;
							}
							if (bodyRowCount != 0) {
								if (StringUtil.isNotEmpty(map.get(clName[j]).toString())) {
									if(map.get(clName[j]).toString().indexOf(" CST") != -1) {
										row.createCell(j).setCellValue(StringUtil.databaseDate(map.get(clName[j]).toString()));
									}else {
										row.createCell(j).setCellValue(map.get(clName[j]).toString());
									}
								} else {
									row.createCell(j).setCellValue("");
								}
							}
						}
						bodyRowCount++;// 正文内容行号递增1
					}
					
				}
			}
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} /*finally {
			return wb;
		}*/
	}
}
