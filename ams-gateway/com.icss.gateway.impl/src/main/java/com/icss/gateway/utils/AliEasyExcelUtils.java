package com.icss.gateway.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * ALiEasyExcel 工具类
 *
 * @author cuigf 2022-03-02
 */
public class AliEasyExcelUtils {


    /**
     * 根据inputStream文件流读取excel文件，返回List<Object>结果集
     *
     * @inputStream  文件Instream流
     * @author cuigf 2022-03-02
     */
    public static List<Object> readExcelDataList(InputStream inputStream) {
        return EasyExcel.read(inputStream).doReadAllSync();
    }

    /**
     * 根据每行excel的row对象和cell下标返回cell的值
     *
     * @excelRowData  每行excel的row对象
     * @index  cell的下标
     * @author cuigf 2022-03-02
     */
    private static Object getValueCommon(Object excelRowData, int index) {
        LinkedHashMap<Integer, Object> linkedHashMap = (LinkedHashMap<Integer, Object>)excelRowData;
        if (null != linkedHashMap && linkedHashMap.size() > 0) {
            return linkedHashMap.get(index);
        }
        return null;
    }

    /**
     * 根据每行excel的row对象和cell下标返回cell的值(String类型的值)
     *
     * @excelRowData  每行excel的row对象
     * @index  cell的下标
     * @author cuigf 2022-03-02
     */
    public static String getExcelCellValueString(Object excelRowData, int index) {
        Object value = getValueCommon(excelRowData, index);
        if (null != value) {
            return value.toString();
        }
        return null;
    }

    /**
     * 根据每行excel的row对象和cell下标返回cell的值(BigDecimal类型的值)
     *
     * @excelRowData  每行excel的row对象
     * @index  cell的下标
     * @author cuigf 2022-03-02
     */
    public static BigDecimal getExcelCellValueBigDecimal(Object excelRowData, int index) {
        Object value = getValueCommon(excelRowData, index);
        if (null != value) {
           BigDecimal bigDecimal = new BigDecimal(value.toString());
           return bigDecimal;
        }
        return null;
    }


    /**
     * 根据每行excel的row对象和cell下标返回cell的值(java.sql.Date类型的值)
     *
     * @excelRowData  每行excel的row对象
     * @index  cell的下标
     * @author cuigf 2022-03-02
     */
    public static java.sql.Date getExcelCellValueDate(Object excelRowData, int index) {
        Object value = getValueCommon(excelRowData, index);
        if (null != value) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                return new java.sql.Date(date.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    /**
     * 写入excel公共方法
     *
     * @inputStream  文件Instream流
     * @author cuigf 2022-03-02
     */
    public static <T> void write2Excel(String fileNameAndPostfix, String sheetName, Class<T> clazz, List<T> dataList, HttpServletResponse response) throws Exception {
        String[] fileNameAndPostfixArr = fileNameAndPostfix.split("\\.");
        String fileName = fileNameAndPostfixArr[0];
        String filePostfix = fileNameAndPostfixArr[1];
        File tempFile = File.createTempFile(fileName, "." + fileNameAndPostfixArr);
        ExcelWriter excelWriter = null;
        try {
            String tempFilePath = tempFile.getAbsolutePath();
            EasyExcel.write(tempFilePath, clazz).sheet(sheetName).doWrite(dataList);
            exportLow(response, fileName, filePostfix, tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (null != excelWriter) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 根据每行excel的row对象和cell下标返回cell的值(java.sql.Date类型的值)
     *
     * @response  HttpServletResponse对象
     * @fileName  文件名
     * @filePostfix 文件后缀
     * @fileAbsolutePath 文件绝对路径
     * @author  cuigf 2022-03-05
     */
    public static void exportLow(HttpServletResponse response, String fileName, String filePostfix, String fileAbsolutePath) throws Exception {

        InputStream inputStream = null;
        OutputStream fileOut = null;
        try {
            String fileNameAndPostfix = fileName + "." + filePostfix;
            response.reset();
            fileOut = response.getOutputStream();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;application/msword;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" +  new String(fileNameAndPostfix.getBytes("UTF-8"), "ISO-8859-1") );//
            inputStream = new FileInputStream(new File(fileAbsolutePath));
            int count;
            byte[] bytes=new byte[1024];
            while ((count = inputStream.read(bytes)) != -1) {
                fileOut.write(bytes, 0, count);//将缓冲区的数据输出到浏览器
            }
            fileOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != fileOut){
                    fileOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Map<Integer, Object> map = new LinkedHashMap<Integer, Object>();
        map.put(0, "2022-03-03");
        Date date = getExcelCellValueDate((Object)map, 0);
        System.out.println(date);
    }


}
