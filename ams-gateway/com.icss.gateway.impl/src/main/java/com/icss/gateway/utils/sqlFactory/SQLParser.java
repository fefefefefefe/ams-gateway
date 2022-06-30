package com.icss.gateway.utils.sqlFactory;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class SQLParser {

    private SchemaStatVisitor schemaStatVisitor;

    private DbType dbType;


    public SQLParser(SchemaStatVisitor schemaStatVisitor, DbType dbType) {
        this.schemaStatVisitor = schemaStatVisitor;
        this.dbType = dbType;
    }

    //定义SQL类型
    public static class SQL_TYPE {
        //数据定义语言DDL：create、drop、alter
        //数据操纵语言DML：insert、update、delete、truncate
        //数据查询语言DQL：select
        //数据控制功能DCL：grant、revoke、commit、rollback
        //数据存储过程语言：Loop、while、if、open
        public static String SELECT = "SELECT";
        public static String INSERT = "INSERT";
        public static String UPDATE = "UPDATE";
        public static String DELETE = "DELETE";
        public static String ALTER = "ALTER";
        public static String CREATE_TABLE = "CREATE_TABLE";
        public static String CREATE_TABLE_AS_SELECT = "CREATE_TABLE_AS_SELECT";
        public static String DROP_TABLE = "DROP_TABLE";
        public static String CREATE_INDEX = "CREATE_INDEX";
        public static String DROP_INDEX = "DROP_INDEX";
        public static String TRUNCATE = "TRUNCATE";
        public static String ALTER_TABLE_ADD_COLUMN = "ALTER_TABLE_ADD_COLUMN";
        public static String ALTER_TABLE_DROP_COLUMN = "ALTER_TABLE_DROP_COLUMN";
        public static String ALTER_TABLE_ALTER_COLUMN = "ALTER_TABLE_ALTER_COLUMN";
        public static String ALTER_TABLE_RENAME_COLUMN = "ALTER_TABLE_RENAME_COLUMN";
        public static String ALTER_TABLE_RENAME = "ALTER_TABLE_RENAME";
        public static String COMMIT = "COMMIT";
        public static String DECLARE = "DECLARE";
        public static String EXPLAIN = "EXPLAIN";
        public static String EXPR = "EXPR";
        public static String FETCH = "FETCH";
        public static String GRANT = "GRANT";
        public static String IF = "IF";
        public static String LOOP = "LOOP";
        public static String MERGE = "MERGE";
        public static String OPEN = "OPEN";
        public static String REPLACE = "REPLACE";
        public static String REVOKE = "REVOKE";
        public static String ROLLBACK = "ROLLBACK";
        public static String SET = "SET";
        public static String WHILE = "WHILE";
        public static String USE = "USE";
    }

    //获取SQL类型
    public static String sqlType(SQLStatement stmt) {
        if (stmt instanceof SQLSelectStatement) {
            return SQL_TYPE.SELECT;
        } else if (stmt instanceof SQLInsertStatement) {
            return SQL_TYPE.INSERT;
        } else if (stmt instanceof SQLUpdateStatement) {
            return SQL_TYPE.UPDATE;
        } else if (stmt instanceof SQLDeleteStatement) {
            return SQL_TYPE.DELETE;
        } else if (stmt instanceof SQLCreateTableStatement) {
            return SQL_TYPE.CREATE_TABLE;
        } else if (stmt instanceof SQLDropTableStatement) {
            return SQL_TYPE.DROP_TABLE;
        } else if (stmt instanceof SQLAlterStatement) {
            return SQL_TYPE.ALTER;
        } else if (stmt instanceof SQLCommitStatement) {
            return SQL_TYPE.COMMIT;
        } else if (stmt instanceof SQLDeclareStatement) {
            return SQL_TYPE.DECLARE;
        } else if (stmt instanceof SQLExplainStatement) {
            return SQL_TYPE.EXPLAIN;
        } else if (stmt instanceof SQLExprStatement) {
            return SQL_TYPE.EXPR;
        } else if (stmt instanceof SQLFetchStatement) {
            return SQL_TYPE.FETCH;
        } else if (stmt instanceof SQLGrantStatement) {
            return SQL_TYPE.GRANT;
        } else if (stmt instanceof SQLIfStatement) {
            return SQL_TYPE.IF;
        } else if (stmt instanceof SQLLoopStatement) {
            return SQL_TYPE.LOOP;
        } else if (stmt instanceof SQLMergeStatement) {
            return SQL_TYPE.MERGE;
        } else if (stmt instanceof SQLOpenStatement) {
            return SQL_TYPE.OPEN;
        } else if (stmt instanceof SQLReplaceStatement) {
            return SQL_TYPE.REPLACE;
        } else if (stmt instanceof SQLRevokeStatement) {
            return SQL_TYPE.REVOKE;
        } else if (stmt instanceof SQLRollbackStatement) {
            return SQL_TYPE.ROLLBACK;
        } else if (stmt instanceof SQLSetStatement) {
            return SQL_TYPE.SET;
        } else if (stmt instanceof SQLWhileStatement) {
            return SQL_TYPE.WHILE;
        } else if (stmt instanceof SQLUseStatement) {
            return SQL_TYPE.USE;
        }
        return "OTHER_TYPE";
    }

    //格式化SQL语句
    public String formatSql(String sql) {
        return SQLUtils.format(sql, dbType);
    }

    //获取SQL拆解后的集合
    public List<SQLStatement> getSqlStmtList(String sql) {
        return SQLUtils.parseStatements(sql, dbType);
    }

    public List<String> getSqlList(String sql) {
        List<SQLStatement> statementList = getSqlStmtList(sql);
        List<String> sqlList = new ArrayList<String>();
        for (SQLStatement stmt : statementList) {  //修改
            stmt.setAfterSemi(false);  //不要分号
            String stmtStr = SQLUtils.toSQLString(Arrays.asList(stmt), dbType, null,
                    new SQLUtils.FormatOption(true, false), null);
            sqlList.add(stmtStr);
        }
        return sqlList;
    }

    //验证SQL的合法性
    public boolean verifySQL(String sql) {
        boolean verifySQL = true;
        try {
            SQLUtils.parseStatements(sql, dbType);
        } catch (Exception e) {
            log.error("出现异常", e);
            verifySQL = false;
        }
        return verifySQL;
    }

     /*
     *  获取SQL语句的类型
    返回值枚举  "CreateView", "Create", "DropView", "Drop", "Insert", "InsertSelect", "Select"  "Alter"
     * */
    public String getSqlType(String sql) {
        // 特殊处理sqlServer的select into
        if (dbType.equals(DbType.sqlserver) && sql.trim().startsWith("SELECT * INTO") && (sql.endsWith(") a ") || sql.endsWith(") a"))) {
            return "Create";
        }
        String[] order = new String[]{"CreateView", "Create", "Insert", "InsertSelect"};
        Map<String, String> map = this.getTablesMap(sql);
        /*按优先级返回*/
        for (String s : order) {
            for (String s1 : map.keySet()) {
                if(s.equalsIgnoreCase(map.get(s1))) return s;
            }
        }
        /*直接返回*/
        for (String s : map.keySet()) {
            return map.get(s);
        }
        return null;
    }

    private String getTableName(String fullName){
        // sqlServer特殊处理一个系统表(此表用于判断视图是否存在，不存在则删除)
        if (dbType.equals(DbType.sqlserver) && "INFORMATION_SCHEMA.VIEWS".equals(fullName)) {
            return fullName;
        }
        String[] names = fullName.split("\\.");
        return names.length > 1 ? names[1] : names[0];
    }

    //获取SQL语句中的表名称
    @SuppressWarnings("rawtypes")
    public List<Map<String, Object>> getTables(String sql) {
        //先清空
        /*List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
        List<SQLStatement> statementList = getSqlStmtList(sql);
        for (SQLStatement stmt : statementList) {
            stmt.accept(schemaStatVisitor);
            Map<Name, TableStat> tabmap = schemaStatVisitor.getTables();
            for (Iterator iterator = tabmap.keySet().iterator(); iterator.hasNext(); ) {
                Map<String, Object> map = new HashMap<String, Object>();
                Name name = (Name) iterator.next();
                map.put("tableName", getTableName(name.toString()));
                map.put("tableType", getTypeByTbStat(tabmap.get(name)));
                tableList.add(map);
            }
            break;
        }
        return tableList;*/
        List<Map<String, Object>> mList = new ArrayList<>();
        Map<String, String> tbMap = this.getTablesMap(sql);
        tbMap.keySet().forEach(key->{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("tableName", key);
            map.put("tableType", tbMap.get(key));
            mList.add(map);
        });
        return mList;
    }

    private String getTypeByTbStat(TableStat tableStat){
        if(tableStat.getCreateCount() >= 10){
            return "CreateView";
        }else if(tableStat.getDropCount() >= 10){
            return "DropView";
        }else{
            return tableStat.toString();
        }
    }

    public LinkedHashMap<String, String> getTablesMap(String sql) {
        LinkedHashMap<String, String> extMap;
        if((extMap=this.getTbMapExt(sql)) != null){
            return extMap;
        }
        LinkedHashMap<String, String> tables = new LinkedHashMap<>();
        List<SQLStatement> statementList = getSqlStmtList(sql);
        for (SQLStatement stmt : statementList) {
            stmt.accept(schemaStatVisitor);
            Map<TableStat.Name, TableStat> tbs = schemaStatVisitor.getTables();
            for (TableStat.Name name : tbs.keySet()) {
                tables.put(getTableName(name.getName()), getTypeByTbStat(tbs.get(name)));
            }
            break;
        }
        return tables;
    }

    /* 完整逻辑： 将 sample_proc(a,b,c) 视为 create c as select .... from a 处理) */
    public static final Pattern pattern = Pattern.compile("CALL sample_proc\\('(\\S+)', '(\\S+)', '(\\S+)'\\)");

    private LinkedHashMap<String, String> getTbMapExt(String sql) {
        Matcher matcher = pattern.matcher(sql);
        if(matcher.find() && matcher.groupCount()==3){
            LinkedHashMap<String, String> extMap = new LinkedHashMap<>();
            extMap.put(getTableName(matcher.group(1)), "Select");
            extMap.put(getTableName(matcher.group(3)), "Create");
            return extMap;
        }else{
            return null;
        }
    }
    private String replaceExt(String sql, Map<String, String> map) {
        Matcher matcher = pattern.matcher(sql);
        if(matcher.find() && matcher.groupCount()==3){
            return sql.replaceAll("'"+matcher.group(1)+"'", "'"+map.get(matcher.group(1))+"'")
                    .replaceAll("'"+matcher.group(3)+"'", "'"+map.get(matcher.group(3))+"'");
        }else {
            return null;
        }
    }


    public String replaceTables(String sql, Map<String, String> map) {
        String result;
        if((result=this.replaceExt(sql, map)) != null){
            return result;
        }
        List<SQLStatement> statementList = getSqlStmtList(sql);
        return SQLUtils.toSQLString(statementList, this.dbType, null,
                new SQLUtils.FormatOption(true, false), map);
    }

    //获取SQL语句中的字段
    public List<String> getColumns(String sql) {
        List<String> columnList = new ArrayList<String>();
        for (SQLStatement stmt : this.getSqlStmtList(sql)) {
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
            SQLSelectQuery sqlSelectQuery = sqlSelectStatement.getSelect().getQuery();
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            // 获取字段列表
            List<SQLSelectItem> selectItems = sqlSelectQueryBlock.getSelectList();
            for (SQLSelectItem si : selectItems) {
                String column = "";
                if (si.getAlias().equals("")) {
                    column = si.getAlias();
                } else {
                    column = si.getExpr().toString();
                }
                if (column.equals("")) {
                    String[] arr = column.split("\\.");
                    column = arr.length > 1 ? arr[1] : column;
                }
                columnList.add(column);
            }
            break;
        }
        return columnList;
    }

    //获取表名
    public List<String> sqlSplit(String sql) {
        List<String> sqlList = new ArrayList();
        for (SQLStatement sqlStatement : this.getSqlStmtList(sql)) {
            String thisSQL = sqlStatement.toString();
            if (!"".equals(thisSQL)) {
                thisSQL = thisSQL.replace("\n", " ");
            }
            if (thisSQL.endsWith(";")) {
                thisSQL = thisSQL.substring(0, thisSQL.length() - 1);
            }
            sqlList.add(thisSQL);
        }
        return sqlList;
    }

}

