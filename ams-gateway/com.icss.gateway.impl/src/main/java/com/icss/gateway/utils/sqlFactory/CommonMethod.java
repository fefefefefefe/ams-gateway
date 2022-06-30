package com.icss.gateway.utils.sqlFactory;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import org.apache.commons.lang.StringUtils;
import utils.SQLParser;

import java.util.ArrayList;
import java.util.List;

public class CommonMethod {

    /**
     * 处理当前SQL语句中被注释的语句段，替换为空串
     * @param sql 待处理的SQL语句
     * @return sql
     */
    public static String dealNoteSql(String sql){
        String noteSql = "";//存储被注释掉的SQL段
        //由于SQLParser解析后的单个语句，注释内容都位于可执行语句的句首，若原语句的句尾有注释，则解析时就已经被处理掉了，故只对句首注释内容做处理
        int endInd = sql.lastIndexOf("*/");
        if(endInd > -1){
            noteSql = sql.substring(0, endInd + 1);
            sql = sql.substring(endInd + 2, sql.length());
        }
        return sql;
    }

    /**
     * 解析SQL节点中最后一条SELECT语句，拼接参数SQL，提高执行效率
     * @param oldSql 原SQL语句
     * @param replaceParamSql 参数语句
     * @throws Exception
     */
    public static String getResolvingSql(String oldSql, String replaceParamSql) throws Exception{
        SQLParser sp = new SQLParser(oldSql);
        StringBuilder newSql = new StringBuilder();
        SQLSelectStatement stm = sp.getSelectStatement();
        SQLSelectQuery sqlSelectQuery = stm.getSelect().getQuery();
        List<String> selectOptionList = new ArrayList<String>();
        boolean hasSign = false;//SELECT语句的输出列是否含有【*】
        if(sqlSelectQuery instanceof SQLSelectQueryBlock){//普通查询语句
            //第一种类型语句：SELECT A,B AS B1,COUNT(A) AS A1 FROM C WHERE A='1' GROUP BY A,B HAVING COUNT(A)=3 ORDER BY B DESC
            //第二种语句：SELECT C.A, C.B AS B1,COUNT(D.ID) FROM C LEFT JOIN D ON C.ID = D.ID LEFT JOIN E ON D.NAME = E.NAME WHERE C.A='4' ORDER BY C.A DESC
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            List<SQLSelectItem> selectOptions = sqlSelectQueryBlock.getSelectList();
            String selectStr = "";
            String alias = "";
            for(SQLSelectItem item : selectOptions){
                alias = item.getAlias();
                //普通的SELECT项（SELECT A,B ……或者 SELECT T.A,T.B ……或者 SELECT *，……）
                if (item.getExpr() instanceof SQLIdentifierExpr || item.getExpr() instanceof SQLPropertyExpr || item.getExpr() instanceof SQLAllColumnExpr) {
                    selectStr = item.getExpr().toString();
                    if(selectStr.contains("*")){
                        hasSign = true;
                        break;
                    }
                    if(StringUtils.isNotEmpty(alias)){
                        selectStr += " AS " + alias;
                    }
                }else if(item.getExpr() instanceof SQLCaseExpr){//CASE WHEN THEN项
                    SQLCaseExpr expr = (SQLCaseExpr) item.getExpr();
                    selectStr = expr.getParent().toString();
                }else if(item.getExpr() instanceof SQLMethodInvokeExpr){//其他类型都默认暂定为函数项
                    SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) item.getExpr();
                    if(expr.getMethodName().equalsIgnoreCase("DECODE")){//ORACLE中与CASE WHEN THEN一样的函数
                        selectStr = expr.getParent().toString();
                    }else{
                        if (StringUtils.isEmpty(alias)) {
                            throw new Exception("未使用列别名定义函数：【" + item.getExpr() + "】");
                        } else {
                            hasSign = true;
                            break;
                        }
                    }
                }else{
                    if (StringUtils.isEmpty(alias)) {
                        throw new Exception("未使用列别名定义函数：【" + item.getExpr() + "】");
                    } else {
                        hasSign = true;
                        break;
                    }
                }
                selectOptionList.add(selectStr);
            }
            if(hasSign){//使用结果集输出字段进行设参
                if(StringUtils.isNotEmpty(replaceParamSql)){
                    oldSql = "SELECT * FROM (" + oldSql + ") TEMP_TAB WHERE " + replaceParamSql;
                }
                newSql.append(oldSql);
            }else{//继续解析SQL语句使用原字段进行设参
                newSql.append("SELECT " + StringUtils.join(selectOptionList,","));
                newSql.append(" FROM " + sqlSelectQueryBlock.getFrom());
                SQLSelectGroupByClause groupByClause = sqlSelectQueryBlock.getGroupBy();
                SQLExpr whereStr = sqlSelectQueryBlock.getWhere();
                SQLOrderBy orderBy = sqlSelectQueryBlock.getOrderBy();
                if(groupByClause != null){
                    if(whereStr != null){
                        newSql.append(" WHERE " + whereStr.toString());
                    }
                    newSql.append(" " + groupByClause.toString());
                    SQLExpr having =  groupByClause.getHaving();
                    if(StringUtils.isNotEmpty(replaceParamSql)){
                        if(having != null){
                            newSql.append(" AND " + replaceParamSql);
                        }else{
                            newSql.append(" HAVING " + replaceParamSql);
                        }
                    }
                }else{
                    if(whereStr != null){
                        newSql.append(" WHERE " + whereStr.toString());
                        if(StringUtils.isNotEmpty(replaceParamSql)){
                            newSql.append(" AND " + replaceParamSql);
                        }
                    }else{
                        if(StringUtils.isNotEmpty(replaceParamSql)){
                            newSql.append(" WHERE " + replaceParamSql);
                        }
                    }
                }
                if(orderBy != null){
                    newSql.append(" " + orderBy.toString());
                }
            }
        }else if(sqlSelectQuery instanceof SQLUnionQuery){//union语句和其他语句
            //第三种语句：SELECT A, B AS B1,NUM FROM C UNION ALL SELECT A2 AS A,B4 AS B1,COUNT(NAME) AS NUM FROM D WHERE ID='1' GROUP BY A2,B4 HAVING A2='2'
            SQLUnionQuery unionQuery = (SQLUnionQuery) sqlSelectQuery;
            SQLSelectQuery selectQuery = unionQuery.getLeft();
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) selectQuery;
            List<SQLSelectItem> selectOptions = sqlSelectQueryBlock.getSelectList();
            String selectStr = "";
            String alias = "";
            for(SQLSelectItem item : selectOptions){
                alias = item.getAlias();
                //普通的SELECT项（SELECT A,B ……或者 SELECT T.A,T.B ……或者 SELECT *，……）
                if (item.getExpr() instanceof SQLIdentifierExpr || item.getExpr() instanceof SQLPropertyExpr || item.getExpr() instanceof SQLAllColumnExpr) {
                    selectStr = item.getExpr().toString();
                    if(selectStr.contains("*")){
                        hasSign = true;
                        break;
                    }
                    if(StringUtils.isNotEmpty(alias)){
                        selectStr = alias;
                    }
                }else if(item.getExpr() instanceof SQLCaseExpr){//CASE WHEN THEN项
                    SQLCaseExpr expr = (SQLCaseExpr) item.getExpr();
                    if(StringUtils.isNotEmpty(alias)){
                        selectStr = alias;
                    }else{
                        selectStr = expr.getValueExpr().toString();
                    }
                }else if(item.getExpr() instanceof SQLMethodInvokeExpr){//其他类型都默认暂定为函数项
                    SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) item.getExpr();
                    if(expr.getMethodName().equalsIgnoreCase("DECODE")){//ORACLE中与CASE WHEN THEN一样的函数
                        if (StringUtils.isEmpty(alias)) {
                            List<SQLExpr> arguments = expr.getArguments();
                            for(SQLExpr argument : arguments){
                                if(argument instanceof SQLIdentifierExpr){//decode(A,'1','2',……) as A1
                                    SQLIdentifierExpr expr1 = (SQLIdentifierExpr) argument;
                                    selectStr = expr1.getName();//A
                                    break;
                                }else if(argument instanceof SQLPropertyExpr){//decode(T.A,'1','2',……) as A1
                                    SQLPropertyExpr expr1 = (SQLPropertyExpr) argument;
                                    selectStr = expr1.getName();//A
                                    break;
                                }else{
                                    throw new Exception("未正确解析语句【" + argument.getParent() + "】中DECODE函数的输出字段信息");
                                }
                            }
                        }else{
                            selectStr = alias;
                        }
                    }else{
                        if (StringUtils.isEmpty(alias)) {
                            throw new Exception("未使用列别名定义函数：【" + item.getExpr() + "】");
                        } else {
                            selectStr = alias;
                        }
                    }
                }else{//其他类型都默认暂定为函数项
                    if (StringUtils.isEmpty(alias)) {
                        throw new Exception("未使用列别名定义函数：【" + item.getExpr() + "】");
                    } else {
                        selectStr = alias;
                    }
                }
                selectOptionList.add(selectStr);
            }
            if(hasSign){
                if(StringUtils.isNotEmpty(replaceParamSql)){
                    oldSql = "SELECT * FROM (" + oldSql + ") TEMP_TAB WHERE " + replaceParamSql;
                }
            }else {
                oldSql = "SELECT " + StringUtils.join(selectOptionList,",") + " FROM (" + oldSql + ") TEMP_TAB";
                if (StringUtils.isNotEmpty(replaceParamSql)) {
                    oldSql += " WHERE " + replaceParamSql;
                }
            }
            newSql.append(oldSql);
        }else{
            //第四种语句：其他语句
            if(StringUtils.isNotEmpty(replaceParamSql)){
                oldSql = "SELECT * FROM (" + oldSql + ") TEMP_TAB WHERE " + replaceParamSql;
            }
            newSql.append(oldSql);
        }
        return newSql.toString();
    }

    /**
     * 判断SQL语句的SELECT项中是否含有函数公式或*号(只应用于[SELECT A,B …… FROM C]语句)
     * @param sql
     * @return
     */
    public static boolean selectOptionMethod(String sql) throws Exception {
        boolean flag = false;
        SQLParser sqlParser = new SQLParser(sql);
        SQLSelectStatement statement = sqlParser.getSelectStatement();
        SQLSelectQuery selectQuery = statement.getSelect().getQuery();
        SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) selectQuery;
        List<SQLSelectItem> selectItemList = sqlSelectQueryBlock.getSelectList();
        for (SQLSelectItem item : selectItemList) {
            SQLExpr selectStr = item.getExpr();
            String alias = item.getAlias();
            //普通的SELECT项（SELECT A,B ……或者 SELECT T.A,T.B ……或者 SELECT *，……）
            if (selectStr instanceof SQLIdentifierExpr || selectStr instanceof SQLPropertyExpr || selectStr instanceof SQLAllColumnExpr) {
                if(selectStr.toString().contains("*")){
                    flag = true;
                    break;
                }
            }else if(!(selectStr instanceof SQLCaseExpr)){//除了CASE WHEN THEN项，其他类型都默认暂定为函数项
                if(selectStr instanceof SQLMethodInvokeExpr){
                    SQLMethodInvokeExpr expr = (SQLMethodInvokeExpr) selectStr;
                    if(!expr.getMethodName().equalsIgnoreCase("DECODE")){//ORACLE中与CASE WHEN THEN一样的函数
                        if (StringUtils.isEmpty(alias)) {
                            throw new Exception("未使用列别名定义函数：【" + selectStr + "】");
                        } else {
                            flag = true;
                            break;
                        }
                    }
                }else{
                    if (StringUtils.isEmpty(alias)) {
                        throw new Exception("未使用列别名定义函数：【" + selectStr + "】");
                    } else {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }
}
