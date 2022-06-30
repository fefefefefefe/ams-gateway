package com.icss.gateway.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.core.env.Environment;
import utils.DbHelper;
import utils.httpinterface.Pagination;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelperUtils {

    /**
     * 创建DbHelper实例
     * @env 环境对象
     * @author cuigf 2022-03-08
     */
    public static DbHelper createDbHelperInstance(Environment env) throws Exception {
        String jdbcUrl = env.getProperty("spring.datasource.url");
        String jdbcUserName = env.getProperty("spring.datasource.username");
        String jdbcPassword = env.getProperty("spring.datasource.password");
        return new DbHelper( "oracle.jdbc.driver.OracleDriver", jdbcUrl, jdbcUserName, jdbcPassword);
    }


    /**
     * 将ResultSet转为ListMap
     * @param rs 要转换的结果集
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> resultSetToListMap(ResultSet rs) throws SQLException {
        Map<String, Object> resultlist = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String key = "";
        Object value = null;
        List<String> Columnslist = new ArrayList<String>();
        List<Map<String, Object>> columnsInfoList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();
        while (rs.next()) {
            map = new HashMap<String, Object>();
            for (int k = 1; k <= count; k++) {
                key = rsmd.getColumnName(k).toLowerCase();
                String columnType = rsmd.getColumnTypeName(k);
                value = rs.getObject(k);
                if(columnType.equals("TIMESTAMP")){
                    value += "";
                }
                if(!key.equals("rownrs")){
                    map.put(key, null == value ? "" : value);
                }
            }
            list.add(map);
        }
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if(!columnName.equals("rownrs")){
                Columnslist.add(columnName);
                columnsMap.put("columnName", columnName);
                columnsMap.put("columnType", columnType);
                columnsMap.put("columnLen", columnLen);
                columnsInfoList.add(columnsMap);
            }
        }
        mapList.put("columnList", columnsInfoList);
        resultlist.put("columns", Columnslist);
        resultlist.put("result", list);
        resultlist.put("columnInfo", mapList);
        return resultlist;
    }

    /**
     * 根据pageNo和pageSize获取IPage<Object>通用接口
     *
     * @pageNo 当前页号
     * @pageSize 页大小
     * @sql 查询的sql语句
     * @dbHelper DbHelper对象
     * @author cuigf 2022-03-10
     */

    public static IPage<Object> getIPageList(int pageNo, int pageSize, String sql, DbHelper dbHelper) throws Exception {
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNo);
        pagination.setPageSize(pageSize);
        ResultSet rs = dbHelper.executeQueryByPageDMOra(pagination, sql);
        Map<String, Object> ResultMap = resultSetToListMap(rs);
        return composeCommonPage(ResultMap, pagination);
    }

    /**
     * 根据resultMap和pagination组装分页对象IPage<Object>
     *
     * @param resultMap 查询sql返回结果对象(类型Map<String, Object>)
     * @param pagination Pagination分页对象
     * @return String
     * @author cuigf 2022-03-04
     */
    public static IPage<Object> composeCommonPage(Map<String, Object> resultMap, Pagination pagination) {
        IPage<Object> resultPage = new Page<>();
        List<Object> recordList = (List<Object>) resultMap.get("result");
        resultPage.setRecords(recordList);
        resultPage.setTotal(pagination.getDataCount());
        resultPage.setCurrent(pagination.getPageNum());
        resultPage.setSize(pagination.getPageSize());
        resultPage.setPages(pagination.getPageCount());
        return resultPage;
    }




}
