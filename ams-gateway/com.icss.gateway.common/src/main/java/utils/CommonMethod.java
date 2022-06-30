package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 公共方法
 *
 * @author ZhangSiWeiG
 */
public class CommonMethod {

    /**
     * 将ResultSet转为ListMap
     *
     * @param rs 要转换的结果集
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> batchAnalysisResultSetToListMap(ResultSet rs) throws SQLException {
        Map<String, Object> resultlist = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String key = null;
        List<String> Columnslist = new ArrayList<String>();
        List<Map<String, Object>> columnsInfoList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();
        //处理时间类型的值
        Set setTimestamp = new HashSet();
        Set setDate = new HashSet();
        SimpleDateFormat simpleTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if (!columnName.equals("batch") && !columnName.equals("rownr")
                    && !columnName.equals("rownrs")) {
                Columnslist.add(columnName);
                columnsMap.put("columnName", columnName);
                columnsMap.put("columnType", columnType);
                columnsMap.put("columnLen", columnLen);
//                if (columnType.equals("timestamp".toUpperCase())) {
//                    setTimestamp.add(columnName);
//                } else if (columnType.equals("date".toUpperCase())) {
//                    setDate.add(columnName);
//                }
                columnsInfoList.add(columnsMap);
            }
        }

        //执行拼接后的sql语句


        while (rs.next()) {
            long start = System.currentTimeMillis();
            map = new HashMap<String, Object>();
            for (int i = 1; i <= count; i++) {

                key = rsmd.getColumnName(i).toLowerCase();
                if (!key.equals("batch") && !key.equals("rownr") && !key.equals("rownrs")) // 去掉批次字段
                {
                    if (setDate.size() > 0 && setDate.contains(key)) {
                        map.put(key, simpleDateFormat.format(rs.getObject(i)));
                    } else if (setTimestamp.size() > 0 && setTimestamp.contains(key)) {
                        map.put(key, simpleTimestampFormat.format(rs.getObject(i)));
                    } else {
                        map.put(key, rs.getObject(i));
                    }
                }
            }
            list.add(map);
            long end = System.currentTimeMillis();
            //System.out.println((end-start)+"毫秒");
        }

        mapList.put("columnList", columnsInfoList);
        resultlist.put("columns", Columnslist);
        resultlist.put("result", list);
        resultlist.put("columnInfo", mapList);
        return resultlist;
    }

    /**
     * 将ResultSet转为ListMap
     * 查询前50条
     *
     * @param rs 要转换的结果集
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> resultSetToListMapOracle(ResultSet rs) throws SQLException {
        Map<String, Object> resultlist = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String key = null;
        List<String> Columnslist = new ArrayList<String>();
        List<Map<String, Object>> columnsInfoList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();
        //处理时间类型的值
        Set setTimestamp = new HashSet();
        Set setDate = new HashSet();
        SimpleDateFormat simpleTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if (!columnName.equals("batch") && !columnName.equals("rownr")
                    && !columnName.equals("rownrs")) {
                Columnslist.add(columnName);
                columnsMap.put("columnName", columnName);
                columnsMap.put("columnType", columnType);
                columnsMap.put("columnLen", columnLen);
//                if (columnType.equals("timestamp".toUpperCase())) {
//                    setTimestamp.add(columnName);
//                } else if (columnType.equals("date".toUpperCase())) {
//                    setDate.add(columnName);
//                }
                columnsInfoList.add(columnsMap);
            }
        }
        int j = 0;
        while (rs.next()) {
            j++;
            if (j > 50) {
                break;
            }
            map = new HashMap<String, Object>();
            for (int i = 1; i <= count; i++) {
                key = rsmd.getColumnName(i).toLowerCase();
                if (!key.equals("batch") && !key.equals("rownr") && !key.equals("rownrs")) // 去掉批次字段
                {
                    if (setDate.size() > 0 && setDate.contains(key)) {
                        map.put(key, simpleDateFormat.format(rs.getObject(i)));
                    } else if (setTimestamp.size() > 0 && setTimestamp.contains(key)) {
                        map.put(key, simpleTimestampFormat.format(rs.getObject(i)));
                    } else {
                        map.put(key, rs.getObject(i));
                    }
                }
            }
            list.add(map);
        }
        mapList.put("columnList", columnsInfoList);
        resultlist.put("columns", Columnslist);
        resultlist.put("result", list);
        resultlist.put("columnInfo", mapList);
        return resultlist;
    }

    /**
     * 解析待执行SQL语句中的含有【for—loop】语法的SQL语句
     *
     * @param nativeSql 待执行的SQL语句（原生SQL语句）
     * @return resolvingMap
     * @author JL 2020-07-14
     */
    public static Map<String, Object> resolvingSql(String nativeSql) {
        Map<String, Object> resolvingMap = new HashMap<String, Object>();
        boolean isError = false;
        String message = "";
        Map<String, String> replaceContentSqlMap = new HashMap<String, String>();
        StringBuffer newSql = new StringBuffer();
        try {
            if (nativeSql.contains(ConstantUtil.FOR_LOOP_CONTENT_START) || nativeSql.contains(ConstantUtil.FOR_LOOP_CONTENT_END)) {
                if (nativeSql.contains(ConstantUtil.FOR_LOOP_CONTENT_START) && nativeSql.contains(ConstantUtil.FOR_LOOP_CONTENT_END)) {
                    //开始截取【FOR—LOOP】语句
                    String[] beforePart = nativeSql.split(ConstantUtil.FOR_LOOP_CONTENT_START);
                    if (beforePart.length <= 1) {//以开始标志做结尾，说明循环SQL为空或写法不符合规范
                        isError = true;
                        message = "存在【FOR—LOOP】语句不符合规范，不能以开始标志做结尾";
                    } else {
                        newSql.append(beforePart[0]);
                        for (int i = 1; i < beforePart.length; i++) {//从第一个下标开始寻找匹配的结束标志
                            if (beforePart[i].contains(ConstantUtil.FOR_LOOP_CONTENT_END)) {
                                String[] afterPart = beforePart[i].split(ConstantUtil.FOR_LOOP_CONTENT_END);
                                if (afterPart.length == 0 || (afterPart.length > 1 && com.alibaba.druid.util.StringUtils.isEmpty(afterPart[0]))) {
                                    isError = true;
                                    message = "存在【FOR—LOOP】语句为空";
                                } else {
                                    newSql.append(ConstantUtil.FOR_LOOP_CONTENT_SQL + "###" + (i - 1) + "###");
                                    replaceContentSqlMap.put((i - 1) + "", afterPart[0]);
                                    if (afterPart.length > 1) {//
                                        newSql.append(afterPart[1]);
                                    }
                                }
                            } else {
                                isError = true;
                                message = "存在【FOR—LOOP】语句不符合规范，未找到结束标志";
                            }
                        }
                    }
                } else {
                    isError = true;
                    message = "未找到【FOR—LOOP】语句正确的开始或结束标志";
                }
            } else {
                newSql.append(nativeSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isError = true;
            message = e.getMessage();
        } finally {
            resolvingMap.put("isError", isError);
            resolvingMap.put("message", message);
            resolvingMap.put("replaceContentSqlMap", replaceContentSqlMap);
            resolvingMap.put("returnSql", newSql.toString());
        }
        return resolvingMap;
    }

    /**
     * 处理当前SQL语句中被注释的语句段，替换为空串
     *
     * @param sql 待处理的SQL语句
     * @return sql
     */
    public static String dealNoteSql(String sql) {
        String noteSql = "";//存储被注释掉的SQL段
        //由于SQLParser解析后的单个语句，注释内容都位于可执行语句的句首，若原语句的句尾有注释，则解析时就已经被处理掉了，故只对句首注释内容做处理
        int endInd = sql.lastIndexOf("*/");
        if (endInd > -1) {
            noteSql = sql.substring(0, endInd + 1);
            sql = sql.substring(endInd + 2, sql.length());
        }
        return sql;
    }

    /**
     * 处理含有【FOR-LOOP】循环的主循环体SQL语句
     *
     * @param dealSql           待处理的SQL语句
     * @param replaceMainSqlMap 主循环体内部分SQL语句与其占位替换语句的关系Map对象
     * @return dealSqlMap
     */
    public static Map<String, Object> dealForLoopSql(String dealSql, Map<String, String> replaceMainSqlMap) {
        Map<String, Object> dealSqlMap = new HashMap<String, Object>();
        boolean isError = false;
        String message = "";
        StringBuffer loopSql = new StringBuffer();
        try {
            if (!com.alibaba.druid.util.StringUtils.isEmpty(dealSql) && (dealSql.contains(ConstantUtil.FOR_LOOP_MAIN_START) || dealSql.contains(ConstantUtil.FOR_LOOP_MAIN_END))) {
                if (dealSql.contains(ConstantUtil.FOR_LOOP_MAIN_START) && dealSql.contains(ConstantUtil.FOR_LOOP_MAIN_END)) {
                    //开始拆分【FOR—LOOP】语句
                    int firstInd = dealSql.indexOf(ConstantUtil.FOR_LOOP_MAIN_START);//找出主循环体开始标志在语句中开始出现的位置
                    String beforePartSql = dealSql.substring(0, firstInd);
                    String afterPartSql = dealSql.substring(firstInd + ConstantUtil.FOR_LOOP_MAIN_START.length(), dealSql.length());
                    loopSql.append(beforePartSql);
                    if (StringUtils.isNotEmpty(afterPartSql)) {
                        int endInd = afterPartSql.lastIndexOf(ConstantUtil.FOR_LOOP_MAIN_END);////找出主循环体结束标志在语句中最后出现的位置
                        String mainSql = afterPartSql.substring(0, endInd);//获取到当期主循环体内的SQL代码段
                        String leftSql = afterPartSql.substring(endInd + ConstantUtil.FOR_LOOP_MAIN_END.length(), afterPartSql.length());//剩余部分的SQL代码段
                        if (StringUtils.isNotEmpty(mainSql)) {
                            Map<String, Object> resMap = new HashMap<String, Object>();
                            //如果截取后的主循环提SQL代码段内仍然含有FOR循环，则需递归处理
                            if (mainSql.contains(ConstantUtil.FOR_LOOP_MAIN_START) || mainSql.contains(ConstantUtil.FOR_LOOP_MAIN_END)) {
                                resMap = dealForLoopSql(mainSql, replaceMainSqlMap);
                                isError = (boolean) resMap.get("isError");
                                if (isError) {
                                    message = resMap.get("message").toString();
                                } else {//
                                    resMap = dealForLoopMainSql(resMap.get("returnSql").toString(), (Map<String, String>) resMap.get("replaceMainSqlMap"));
                                    isError = (boolean) resMap.get("isError");
                                    if (isError) {
                                        message = resMap.get("message").toString();
                                    } else {
                                        loopSql.append(resMap.get("returnSql"));
                                        replaceMainSqlMap = (Map<String, String>) resMap.get("replaceMainSqlMap");
                                    }
                                }
                            } else {//SQL代码段不包含FOR循环时，解析并替换操作数据表的SQL语句，同事创建其关系Map对象
                                resMap = dealForLoopMainSql(mainSql, replaceMainSqlMap);
                                isError = (boolean) resMap.get("isError");
                                if (isError) {
                                    message = resMap.get("message").toString();
                                } else {
                                    loopSql.append(resMap.get("returnSql"));
                                    replaceMainSqlMap = (Map<String, String>) resMap.get("replaceMainSqlMap");
                                }
                            }
                            loopSql.append(leftSql);
                        } else {
                            isError = true;
                            message = "存在【FOR—LOOP】主循环体语句为空";
                        }
                    } else {
                        isError = true;
                        message = "存在【FOR—LOOP】主循环体语句不符合规范，不能以开始标志做结尾";
                    }
                } else {
                    isError = true;
                    message = "未找到【FOR—LOOP】主循环体语句正确的开始或结束标志";
                }
            } else {
                loopSql.append(dealSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isError = true;
            message = e.getMessage();
        } finally {
            dealSqlMap.put("isError", isError);
            dealSqlMap.put("message", message);
            dealSqlMap.put("replaceMainSqlMap", replaceMainSqlMap);
            dealSqlMap.put("returnSql", loopSql.toString());
        }
        return dealSqlMap;
    }

    /**
     * 处理主循环体SQL代码段
     *
     * @param loopMainSql SQL语句
     * @return dealSqlMap
     */
    private static Map<String, Object> dealForLoopMainSql(String loopMainSql, Map<String, String> replaceMainSqlMap) {
        Map<String, Object> dealSqlMap = new HashMap<String, Object>();
        boolean isError = false;
        String message = "";
        StringBuffer loopSql = new StringBuffer();
        try {
            //对主循环体语句按分号进行拆分
            SQLParser mainSp = new SQLParser(loopMainSql);
            List<String> mainSqlArr = mainSp.sqlSplit();
            //枚举主循环体中可支持的SQL语句类型
            String[] sqlTypeArr = {"SELECT", "INSERT", "UPDATE", "DELETE"};
            int ind = replaceMainSqlMap.keySet().size();
            for (int i = 0; i < mainSqlArr.size(); i++) {
                if (StringUtil.isNotEmpty(mainSqlArr.get(i).trim())) {
                    String sql = mainSqlArr.get(i).replaceAll("\t", "");//此处替换是解决SQLParser的sqlSplit()方法会多生成“\t”字符
                    //使用SQLParser逐句解析
                    String sqlType = "";
                    String curSql = "";
                    try {
                        SQLParser sp = new SQLParser(sql);
                        sqlType = sp.getSqlType();
                        //能解析出来且SQL语句类型符合或者能解析出来但类型不确定的其他可执行的SQL语句，则用自定义语句段占位替换
                        if (Arrays.asList(sqlTypeArr).contains(sqlType) || StringUtil.isEmpty(sqlType)) {
                            if (sqlType.equals("SELECT") && sql.contains(ConstantUtil.FOR_LOOP_MAIN_SQL)) {//占位SQL语句时跳过
                                curSql = sql;
                            } else {//其他类型SQL使用占位SQL替换
                                curSql = ConstantUtil.FOR_LOOP_MAIN_SQL + ind;
                                replaceMainSqlMap.put(ConstantUtil.FOR_LOOP_MAIN_SQL + ind + "`" + sqlType, sql);
                                ind++;
                            }
                        } else {
                            isError = true;
                            message = "主循环体的SQL语句含有不支持的语句类型";
                        }
                    } catch (Exception e) {
                        curSql = sql;//解析出错或SQL语句类型不符合，则不做处理
                    }
                    if (isError) {
                        break;
                    } else {
                        loopSql.append(curSql + ";");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isError = true;
            message = e.getMessage();
        } finally {
            dealSqlMap.put("isError", isError);
            dealSqlMap.put("message", message);
            dealSqlMap.put("replaceMainSqlMap", replaceMainSqlMap);
            dealSqlMap.put("returnSql", loopSql.toString());
        }
        return dealSqlMap;
    }

    /**
     * 获取SQL语句集合中最后一个SELECT语句的下标
     *
     * @param sqls 当前SQL语句集合
     * @author JL
     */
    public static int getLastSelectIndex(List<String> sqls) {
        String sqlType = "";
        for (int i = (sqls.size() - 1); i >= 0; i--) {
            String curSql = dealNoteSql(sqls.get(i));
            SQLParser sp = new SQLParser(curSql);
            sqlType = sp.getSqlType();
            //找出非【FOR-LOOP】循环语句的占位SELECT类型的语句（【FOR-LOOP】循环语句用“SELECT * FROM DUAL”语句进行了占位替换）
            if ("SELECT".equals(sqlType) && !curSql.contains(ConstantUtil.FOR_LOOP_CONTENT_SQL_KEYWORDS) && !curSql.contains(ConstantUtil.FOR_LOOP_MAIN_SQL_KEYWORDS)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将ResultSet转为ListMap
     *
     * @param rs 要转换的结果集
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> resultSetToListMapNoData(ResultSet rs) throws SQLException {
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
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if (!columnName.equals("rownrs")) {
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
     * 根据分页将ResultSet转为ListMap
     *
     * @param rs       要转换的结果集
     * @param startNum 要转换的数据条数起始行数
     * @param endNum   要转换的数据条数结束行数
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> resultSetToListMapByPage(ResultSet rs, int startNum, int endNum) throws SQLException {
        Map<String, Object> resultlist = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String key = "";
        Object value = null;
        List<String> Columnslist = new ArrayList<String>();//字段名称（存在重复字段）
        List<String> keylist = new ArrayList<String>();//字段名称（若存在重复字段名称，则替换）
        List<Map<String, Object>> columnsInfoList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if (!columnName.equals("rownrs")) {
                Columnslist.add(Columnslist.size(), columnName);
                if (keylist.contains(columnName)) {
                    columnName = columnName + "#" + "TEMP#COL" + i;
                }
                keylist.add(i - 1, columnName);
                columnsMap.put("columnName", columnName);
                columnsMap.put("columnType", columnType);
                columnsMap.put("columnLen", columnLen);
                columnsInfoList.add(i - 1, columnsMap);
            } else {
                keylist.add(i - 1, "");
                columnsInfoList.add(i - 1, null);
            }
        }
        int curInd = 1;
        while (rs.next()) {
            if (curInd >= startNum && curInd <= endNum) {
                map = new HashMap<String, Object>();
                for (int k = 1; k <= count; k++) {
                    key = keylist.get(k - 1);
                    if (StringUtils.isNotEmpty(key)) {
                        value = rs.getObject(k);
                        if (columnsInfoList.get(k - 1).get("columnType").equals("TIMESTAMP")) {
                            value += "";
                        }
                        map.put(key, null == value ? "" : value);
                    }
                }
                list.add(map);
                if (curInd == endNum) {
                    break;
                }
                curInd++;
            } else {
                curInd++;
            }
        }
        //单独处理keylist、columnsInfoList
        Iterator iterator = keylist.iterator();
        while (iterator.hasNext()) {
            if (StringUtils.isEmpty(iterator.next().toString())) {
                iterator.remove();
            }
        }
        iterator = columnsInfoList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
        mapList.put("columnList", columnsInfoList);
        resultlist.put("columns", Columnslist);
        resultlist.put("keys", keylist);
        resultlist.put("result", list);
        resultlist.put("columnInfo", mapList);
        return resultlist;
    }


    /**
     * 根据分页将ResultSet转为ListMap
     *
     * @param rs 要转换的结果集
     * @return 返回ListMap
     * @throws SQLException Sql异常
     */
    public static Map<String, Object> resultSetToListMap(ResultSet rs, long dataNum) throws SQLException {
        Map<String, Object> resultlist = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        String key = "";
        Object value = null;
        List<String> Columnslist = new ArrayList<String>();//字段名称（存在重复字段）
        List<String> keylist = new ArrayList<String>();//字段名称（若存在重复字段名称，则替换）
        List<Map<String, Object>> columnsInfoList = new ArrayList<Map<String, Object>>();
        Map<String, List<Map<String, Object>>> mapList = new HashMap<String, List<Map<String, Object>>>();
        for (int i = 1; i <= count; i++) {
            Map<String, Object> columnsMap = new HashMap<String, Object>();
            String columnType = rsmd.getColumnTypeName(i);
            String columnName = rsmd.getColumnName(i).toLowerCase();
            String columnLen = rsmd.getColumnDisplaySize(i) + "";
            if (!columnName.equals("rownrs")) {
                Columnslist.add(Columnslist.size(), columnName);
                if (keylist.contains(columnName)) {
                    columnName = columnName + "#" + "TEMP#COL" + i;
                }
                keylist.add(i - 1, columnName);
                columnsMap.put("columnName", columnName);
                columnsMap.put("columnType", columnType);
                columnsMap.put("columnLen", columnLen);
                columnsInfoList.add(i - 1, columnsMap);
            } else {
                keylist.add(i - 1, "");
                columnsInfoList.add(i - 1, null);
            }
        }
        int curInd = 1;
        while (rs.next()) {
            if (curInd <= dataNum) {
                map = new HashMap<String, Object>();
                for (int k = 1; k <= count; k++) {
                    key = keylist.get(k - 1);
                    if (StringUtils.isNotEmpty(key)) {
                        value = rs.getObject(k);
                        if (columnsInfoList.get(k - 1).get("columnType").equals("TIMESTAMP")) {
                            value += "";
                        }
                        map.put(key, null == value ? "" : value);
                    }
                }
                list.add(map);
                if (curInd == dataNum) {
                    break;
                }
                curInd++;
            } else {
                curInd++;
            }
        }
        //单独处理keylist、columnsInfoList
        Iterator iterator = keylist.iterator();
        while (iterator.hasNext()) {
            if (StringUtils.isEmpty(iterator.next().toString())) {
                iterator.remove();
            }
        }
        iterator = columnsInfoList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
        resultlist.put("columnList", columnsInfoList);
        resultlist.put("columns", Columnslist);
        resultlist.put("keys", keylist);
        resultlist.put("result", list);
//        resultlist.put("columnInfo", mapList);
        return resultlist;
    }

    /**
     * 获取列值
     *
     * @param value 直
     * @param type  类型
     * @return 返回类型
     */
    public static String getColumnValue(String value, String type) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("-1", -1);
        // SqlServer数据类型
        map.put("datetime2", 0);
        map.put("datetimeoffset", 0);
        map.put("decimal", 1);
        map.put("binary", 2);
        map.put("nchar", 3);
        map.put("numeric", 1);
        map.put("nvarchar", 4);
        map.put("time", 5);
        map.put("varbinary", 2);
        map.put("varchar", 20);
        map.put("varchar2", 20);
        map.put("int", 12);
        map.put("smalldatetime", 7);
        map.put("image", 8);
        map.put("smallint", 6);
        map.put("float", 9);
        map.put("bigint", 10);
        map.put("bit", 11);
        map.put("datetime", 7);
        map.put("geography", 20);
        map.put("geometry", 20);
        map.put("hierarchyid", 20);
        map.put("money", 13);
        map.put("ntext", 14);
        map.put("real", 15);
        map.put("smallmoney", 13);
        map.put("sql_variant", 8);
        map.put("text", 16);
        map.put("tinyint", 17);
        map.put("xml", 20);
        map.put("uniqueidentifier", 8);
        map.put("char", 18);
        map.put("date", 7);
        map.put("timestamp", 19);

        switch (map.get(type.toLowerCase()).intValue()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
                return "'" + value.replace("'", "''") + "'";
            case 8:
            case 16:
                //return "0x" + value + "";
                return "" + value + "";
            default:
                //return "N'" + value.replace("'", "''") + "'";
                return "'" + value.replace("'", "''") + "'";
        }
    }

    /**
     * 获取创建表语句（疑点提交专用）
     *
     * @param tableName 表名
     * @return 返回创建表语句
     * @throws Exception
     */
    public static String getCreateTableSql(List<Map<String, Object>> columnList, String tableName) throws Exception {
        // ID列
        StringBuilder createSql = new StringBuilder("");
        createSql.append("create table " + tableName + "(\r\nID VARCHAR2(64) NOT NULL primary key,");

        // 其他数据列，匹配列数据类型
        for (Map<String, Object> map : columnList) {
            String newClTypes = getColumnType(map.get("columnType").toString(), map.get("columnLen").toString());
            createSql.append("\r\n" + "" + map.get("columnName") + "" + " " + newClTypes + ",");
        }
        createSql.delete(createSql.length() - 1, createSql.length());
        createSql.append(")");
        String createSql1 = String.valueOf(createSql);
        return createSql1;
    }

    /**
     * 获取列类型
     *
     * @param oldColumnType 旧列类型
     * @param length        长度
     * @return 返回类型串
     * @throws Exception
     */
    public static String getColumnType(String oldColumnType, String length) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < oldColumnType.length(); i++) {
            char c = oldColumnType.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        oldColumnType = sb.toString();
        String newColumnType = "";
        if (StringUtil.isEmpty(length)) {
            length = "";
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("-1", -1);
        // Oracle11g数据类型
        map.put("binary_double", 0);
        map.put("binary_float", 1);
        map.put("blob", 2);
        map.put("clob", 3);
        map.put("char", 4);
        map.put("date", 5);
        map.put("interval day to second", 6);
        map.put("interval year to month", 7);
        map.put("long", 8);
        map.put("long raw", 9);
        map.put("nclob", 10);
        map.put("number", 11);
        map.put("nvarchar2", 12);
        map.put("raw", 13);
        map.put("timestamp", 14);
        map.put("timestamp with local time zone", 15);
        map.put("timestamp with time zone", 16);
        map.put("varchar2", 17);
        map.put("varchar", 18);
        if (map.get(length) != null) {
            switch (map.get(length).intValue()) {
                case -1:
                    length = "MAX";
                    break;
            }
        }

        if (map.get(oldColumnType.toLowerCase()) == null) {
            System.out.println("oldColumnType为空：" + oldColumnType);
        }
        switch (map.get(oldColumnType.toLowerCase()).intValue()) {
            case 0:
                newColumnType += "double";
                break;
            case 1:
                newColumnType += "float";
                break;
            case 2:
                newColumnType += "blob";
                break;
            case 3:
                newColumnType += "clob";
                break;
            case 4:
                newColumnType += "char(" + length + ")";
                break;
            case 5:
                newColumnType += "timestamp";
                break;
            case 6:
                newColumnType += "interval day to second";
                break;
            case 7:
                newColumnType += "interval year to month";
                break;
            case 8:
                newColumnType += "text";
                break;
            case 9:
                newColumnType += "longvarbinary";
                break;
            case 10:
                newColumnType += "clob";
                break;
            case 11:
                newColumnType += "number(20,4)";
                break;
            case 12:
                int lenint = Integer.parseInt(length);//判断格式是否大于4000
                if ((lenint * 2) <= 4000) {
                    newColumnType += "varchar2(" + (lenint * 2) + ")";
                } else {
                    newColumnType += "text";
                }
                break;
            case 13:
                newColumnType += "varbinary";
                break;
            case 14:
                newColumnType += "timestamp";
                break;
            case 15:
                newColumnType += "timestamp";
                break;
            case 16:
                newColumnType += "timestamp";
                break;
            case 17:
                int lenint2 = Integer.parseInt(length);//判断格式是否大于4000
                if (lenint2 <= 4000) {
                    newColumnType += "varchar2(" + length + ")";
                } else {
                    newColumnType += "text";
                }
                break;
            case 18:
                int lenint3 = Integer.parseInt(length);//判断格式是否大于4000
                if (lenint3 <= 4000) {
                    newColumnType += "varchar(" + length + ")";
                } else {
                    newColumnType += "text";
                }
                break;
        }
        return newColumnType;
    }


    /**
     * 组织insert语句
     *
     * @param dataList 数据List
     * @return 返回Insert语句
     * @throws Exception
     */
    public static List<String> insertSqlByOracle(List<Map<String, Object>> dataList, List<Map<String, Object>> columnList, String tableName) throws Exception {
        List<String> list = new ArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < dataList.size(); i++) {
            StringBuilder insertSql = new StringBuilder("");
            Map<String, Object> map = dataList.get(i);
            // 拼接字段名称字符串(初始化自动生成字段)
            StringBuilder columnNameStr = new StringBuilder("");
            StringBuilder valueStr = new StringBuilder("");
            columnNameStr.append("ID");
            valueStr.append("'" + UUID.randomUUID().toString() + "'");

            for (Map<String, Object> colMap : columnList) {
                columnNameStr.append("," + "" + colMap.get("columnName") + "");

                if (colMap.get("columnName") != null && StringUtil.isNotEmpty(colMap.get("columnName").toString())) {
                    if (colMap.get("columnType").equals("timestamp")) {
                        valueStr.append("," + getColumnValue(simpleDateFormat.format(simpleDateFormat.parse(map.get(colMap.get("columnName").toString()).toString())), colMap.get("columnType").toString()));
                    } else if (colMap.get("columnType").equals("date")) {
                        valueStr.append("," + getColumnValue(dateFormat.format(dateFormat.parse(map.get(colMap.get("columnName").toString()).toString())), colMap.get("columnType").toString()));
                    } else {
                        valueStr.append("," + getColumnValue(map.get(colMap.get("columnName").toString()).toString(), colMap.get("columnType").toString()));
                    }
                } else {
                    valueStr.append("," + null);
                }
            }
            // 拼接字段值字符串(初始化自动生成字段的值)
//            columnNameStr.delete(0, 1);
//            valueStr.delete(0, 1);
            insertSql.append("\ninsert into " + tableName + "(" + columnNameStr + ") values(" + valueStr + ")");
            list.add(insertSql.toString());
        }
        return list;
    }

    /**

     - listToTree
     - <p>方法说明<p>
     - 将JSONArray数组转为树状结构
     - @param arr 需要转化的数据
     - @param id 数据唯一的标识键值
     - @param pid 父id唯一标识键值
     - @param child 子节点键值
     - @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child){
        JSONArray r = new JSONArray();
        JSONObject hash = new JSONObject();
        //将数组转为Object的形式，key为数组中的id
        for(int i=0;i<arr.size();i++){
            JSONObject json = (JSONObject) arr.get(i);
            hash.put(json.getString(id), json);
        }
        //遍历结果集
        for(int j=0;j<arr.size();j++){
            //单条记录
            JSONObject aVal = (JSONObject) arr.get(j);
            //在hash中取出key为单条记录中pid的值
            JSONObject hashVP = (JSONObject) hash.get(aVal.get(pid).toString());
            //如果记录的pid存在，则说明它有父节点，将她添加到孩子节点的集合中
            if(hashVP!=null){
                //检查是否有child属性
                if(hashVP.get(child)!=null){
                    JSONArray ch = (JSONArray) hashVP.get(child);
                    ch.add(aVal);
                    hashVP.put(child, ch);
                }else{
                    JSONArray ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(child, ch);
                }
            }else{
                r.add(aVal);
            }
        }
        return r;
    }
}
