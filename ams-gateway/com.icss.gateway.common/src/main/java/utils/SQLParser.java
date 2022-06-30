package utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;

import java.util.*;

/**
 * SQL解析工具类
 *
 * @author ZhangSiWei
 * @date 2022/3/3 17:08
 */
public class SQLParser {

    /**
     * 数据库类型
     */
    private DbType dbType = JdbcConstants.ORACLE;

    /**
     * sql列表
     */
    private List<SQLStatement> statementList = new ArrayList<SQLStatement>();

    /**
     * sql
     */
    private String sql;

    /**
     * 校验sql
     */
    private boolean verifySQL;

    /**
     * 构造函数
     * @param sql 传入对应的sql
     * @throws Exception 异常信息
     */
    public SQLParser(String sql) {
        this.sql = sql;
        OracleStatementParser osp = new OracleStatementParser(this.sql);
        try {
            this.statementList = osp.parseStatementList();
            this.verifySQL = true;
        }catch(Exception e) {
            this.verifySQL = false;
            throw e;
        }
    }

    /**
     * 定义SQL类型
     */
    public static class SQL_TYPE{
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
        public static String BEGIN_FOR = "BEGIN_FOR";
    }

    /**
     * 获取SQL类型
     * @param stmt 事物对象
     * @return 返回校验后的类型
     */
    public static String sqlType(SQLStatement stmt){
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
        } else if (stmt instanceof SQLDeclareStatement){
            return SQL_TYPE.DECLARE;
        } else if (stmt instanceof SQLBlockStatement){
            return SQL_TYPE.BEGIN_FOR;
        }
        return "OTHER_TYPE";
    }

    /**
     * 格式化SQL语句
     * @return
     */
    public String formatSql() {
        return SQLUtils.format(sql, dbType);
    }

    /**
     * 对sql进行拆分
     * @return 返回拆分后的sql列表
     */
    public List<String> getSqlList(){
        List<String> sqlList = new ArrayList<String>();
        for(SQLStatement stmt : statementList) {
            String str = stmt.toString();
            if(str.endsWith(";") && !str.toUpperCase().endsWith(" END;") && !str.toUpperCase().endsWith("\nEND;")) {			//begin-end语句末尾“;”不能去掉
                str = str.substring(0, str.length() - 1);
            }
            //这块可能会出现问题。2022年3月7日 15:26:52 ZhangSIWeiG  但是目前没有好的解决办法
            if(str.indexOf("END WHILE") != -1){
                str = str.replace("END WHILE","END LOOP;");
            }
            if(str.indexOf(" DO") != -1){
                str = str.replace(" DO"," LOOP");
            }
            sqlList.add(str);
        }
        return sqlList;
    }

    /**
     * 验证SQL的合法性
     * @return
     */
    public boolean verifySQL() {
        return this.verifySQL;
    }

    /**
     * 获取SQL语句的类型
     * @return
     */
    public String getSqlType() {
        for(SQLStatement stmt : statementList) {
            return sqlType(stmt);
        }
        return null;
    }

    /**
     * 获取单个SELECT语句的Statement对象
     * @return SQLSelectStatement
     */
    public SQLSelectStatement getSelectStatement(){
        SQLSelectStatement sqlSelectStatement = null;
        try {
            for (SQLStatement stmt : statementList) {
                sqlSelectStatement = (SQLSelectStatement) stmt;
                break;
            }
        }catch (Exception e){}
        return sqlSelectStatement;
    }

    /**
     * 获取SQL语句中的表名称以及类型
     * @return 返回表对象
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String,Object>> getTables() {
        List<Map<String,Object>> tableList = new ArrayList<Map<String,Object>>();
        for(SQLStatement stmt : statementList) {
            OracleSchemaStatVisitor visitor=new OracleSchemaStatVisitor();
            stmt.accept(visitor);
            Map<Name, TableStat> tabmap = visitor.getTables();
            for(Iterator iterator = tabmap.keySet().iterator(); iterator.hasNext();) {
                Map<String,Object> map = new HashMap<String,Object>();
                Name name = (Name) iterator.next();
                map.put("tableName", name.toString());
                map.put("tableType", tabmap.get(name).toString());
                tableList.add(map);
            }
            break;
        }
        return tableList;
    }

    /**
     * 获取SQL语句中的字段
     * @return 返回字段列表
     */
    public List<String> getColumns() {
        List<String> columnList = new ArrayList<String>();
        for(SQLStatement stmt : statementList) {
            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) stmt;
            SQLSelectQuery sqlSelectQuery = sqlSelectStatement.getSelect().getQuery();
            SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
            // 获取字段列表
            List<SQLSelectItem> selectItems = sqlSelectQueryBlock.getSelectList();
            for(SQLSelectItem si : selectItems) {
                String column = "";
                if(StringUtil.isNotEmpty(si.getAlias())){
                    column = si.getAlias();
                }else {
                    column = si.getExpr().toString();
                }
                if(StringUtil.isNotEmpty(column)) {
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
    public List<String> sqlSplit(){
        List<String> sqlList = new ArrayList();
        for(SQLStatement sqlStatement:statementList){
            String sql = sqlStatement.toString();
            if(!"".equals(sql)){
                sql = sql.replace("\n", " ");
            }
            if(sql.endsWith(";")){
                sql = sql.substring(0,sql.length()-1);
            }
            sqlList.add(sql);
        }
        return sqlList;
    }

    public static void main(String[] args) throws Exception {
        String sql = "create table zswtestfor as select * from 医保耗材目录 where 1=2;\n" +
                "\n" +
                "DECLARE\n" +
                " p_num integer;\n" +
                "BEGIN\n" +
                " p_num :=1;\n" +
                "  while p_num <= 12        loop\n" +
                "  insert into zswtestfor select * from 医保耗材目录 ;\n" +
                "   p_num := p_num + 1;\n" +
                "END       LOOP;\n" +
                "END;\n" +
                "\n" +
                "select * from zswtestfor;\n" +
                "\n" +
                "drop table zswtestfor;\n";
        SQLParser sqlParser = new SQLParser(sql);
        List<String> list = sqlParser.getSqlList();
        for (String s : list) {
            SQLParser sqlParserType = new SQLParser(s);
            System.out.println(sqlParserType.getSqlType());
        }
    }

    /**
     * 获取索引名称
     * @Title: getIndexName
     * @return String 返回类型
     * @throws
     * @author ZhaoDongXu
     *
     */
    public String getIndexName(){
        String indexName = "";
        for(SQLStatement sqlStatement:statementList){
            if (sqlStatement instanceof SQLCreateIndexStatement) {
                indexName = ((SQLCreateIndexStatement) sqlStatement).getIndexDefinition().getName().getSimpleName();
            }
        }
        return indexName;
    }
}
