package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import utils.httpinterface.Pagination;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * jdbc 数据库连接
 *
 * @author sds
 */
public class DbHelper {

    public String _driverName;
    public String _url;
    public String _userName;
    public String _password;
    public Connection _conn;
    public PreparedStatement _stmt;
    public Statement stmt;


    /**
     * @param driverName 驱动
     * @param url        连接串
     * @param userName   用户名
     * @param password   密码
     * @throws Exception
     */
    public DbHelper(String driverName, String url, String userName, String password) throws Exception {
        this._driverName = driverName;
        this._url = url;
        this._userName = userName;
        this._password = password;
        getConnectionThrowable();
    }

    public Connection getConnection() {
        try {

            if (_conn == null) {
                Class.forName(this._driverName);
                _conn = DriverManager.getConnection(this._url, this._userName, this._password);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return _conn;
    }

    public Connection getConnectionThrowable() throws Exception {
        try {

            if (_conn == null) {
                Class.forName(this._driverName);
                _conn = DriverManager.getConnection(this._url, this._userName, this._password);
            }
        } catch (Exception e) {
            throw e;
        }
        return _conn;
    }

    /**
     * @param sql 执行查询sql
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(String sql) throws Exception {
        _stmt = _conn.prepareStatement(sql);
        return _stmt.executeQuery();
    }

    /**
     * 高效查询数据库数据  ZhangSIWeiG
     * @param sql 执行查询sql
     * @param cacheDataCount 默认缓存数据数量
     * @return 返回数据集
     * @throws SQLException
     */
    public ResultSet executeQueryEfficient(String sql,int cacheDataCount) throws Exception {
        PreparedStatement stmt = _conn.prepareStatement(
                sql, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);     //设置连接属性statement以TYPE_FORWARD_ONLY打开
        stmt.setFetchSize(cacheDataCount);//默认加载数据量
        stmt.setFetchDirection(ResultSet.FETCH_REVERSE);//禁止取反方向
        ResultSet rs = stmt.executeQuery();
        return rs;
    }

    /**
     * @param sql 游标可滚动的,查询
     * @return
     * @throws SQLException
     */
    public ResultSet executeAnotherQuery(String sql) throws SQLException {
        _stmt = _conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return _stmt.executeQuery();
    }

    /**
     * @param sql 游标可滚动的,查询s
     *            返回CachedRowSet可缓存结果集
     * @return
     * @throws SQLException
     */
    public CachedRowSet executeAnotherQueryCRS(String sql) throws SQLException {
        _stmt = _conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet crs = factory.createCachedRowSet();
        crs.populate(_stmt.executeQuery());
        return crs;
    }

    /**
     * @param sql 神通数据库游标可滚动的,查询s
     *            返回CachedRowSet可缓存结果集
     * @return
     * @throws SQLException
     */
    public CachedRowSet executeAnotherQueryST(String sql) throws SQLException {
        ResultSet rs = null;
        stmt = _conn.createStatement();
        boolean execute = false;
        try {
            execute = stmt.execute(sql);
            if (execute) {
                rs = stmt.getResultSet();
            } else {
                _stmt = _conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = _stmt.executeQuery();
            }
        } catch (SQLException e) {
            e.getMessage();
            _stmt = _conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = _stmt.executeQuery();
        }
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet crs = factory.createCachedRowSet();
        crs.populate(rs);
        return crs;
    }

    /**
     * @param sql    带参数的执行查询sql
     * @param params
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(String sql, List<Object> params) throws SQLException {
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = null;
        if (params != null && params.size() != 0) {
            int index = 1;
            for (Object object : params) {
                _stmt.setObject(index++, object);
            }
        }
        rs = _stmt.executeQuery();
        return rs;
    }

    public ResultSet executeQueryByPage(Pagination page, String tableName) throws SQLException {
        String sql = getPageSql(page, tableName);
        String countSql = "select count(1) rc  from " + tableName + " " + page.getWhereStr();
        _stmt = _conn.prepareStatement(countSql);
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 根据Sql脚本多种数据库分页（用于分析模型、预警分析模型） 会根据DbHeper实例化的接口类型自动判断执行哪种数据库分页 ZhangSiWeiG
     *
     * @param page      分页信息
     * @param sqlScript Sql脚本
     * @return 返回结果集
     * @throws SQLException 抛出异常
     */
    public ResultSet dataBasePage(Pagination page, String sqlScript) throws SQLException {
        ResultSet rs = null;
        switch (_driverName) {
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
                rs = executeQueryByPagezsw(page, sqlScript);
                break;// SqlServer分页
            case "oracle.jdbc.driver.OracleDriver":
                rs = executeQueryByPageDMOra(page, sqlScript);
                break;// Oracle分页
            case "dm.jdbc.driver.DmDriver":
                rs = executeQueryByPageDMOra(page, sqlScript);
                break;// 达梦数据库分页
            case "com.oscar.Driver":
                rs = executeQueryByPageSTOra(page, sqlScript);
                break;// 神通数据库分页
        }
        return rs;
    }

    /**
     * 根据接口名称获取数据库类型
     *
     * @return 返回数据库类型 1、SqlServer 2、oracle
     */
    public int getDataBaseType() {
        switch (_driverName) {
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
                return 1; // SqlServer
            case "oracle.jdbc.OracleDriver":
                return 1; // Oracle
        }
        return 0;
    }

    /**
     * 传入表名实现数据库分页 ZhangSiWeiG
     *
     * @param page      分页信息
     * @param tableName 表名
     * @return 返回结果集
     * @throws SQLException 抛出异常
     */
    public ResultSet dataBasePageTableName(Pagination page, String tableName) throws SQLException {
        ResultSet rs = null;
        switch (_driverName) {
            case "com.microsoft.sqlserver.jdbc.SQLServerDriver":
                rs = executeQueryByPage(page, tableName);
                break;// SqlServer分页
            case "oracle.jdbc.OracleDriver":
                rs = executeQueryByPageDMOraTableName(page, tableName);
                break;// Oracle分页
            case "dm.jdbc.driver.DmDriver":
                rs = executeQueryByPageDMOraTableName(page, tableName);
                break;// 达梦数据库分页
        }
        return rs;
    }

    /**
     * 传入表名实现达梦、oracle数据库分页 zhangsiweiG
     *
     * @param page      分页信息
     * @param tableName 表名
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPageDMOraTableName(Pagination page, String tableName) throws SQLException {
        String sql = getPageTableNameDMOracle(page, tableName);
        String countSql = "select count(1) rc  from " + tableName + page.getWhereStr();
        _stmt = _conn.prepareStatement(countSql);
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 传入SqlScript实现分页 zhangsiwei
     *
     * @param page      分页信息
     * @param sqlScript 模型脚本
     * @return 返回数据
     * @throws SQLException
     */
    private ResultSet executeQueryByPagezsw(Pagination page, String sqlScript) throws SQLException {
        String distinctColunm = getDistinctColunm(sqlScript);
        if (StringUtils.isNotBlank(distinctColunm)) {
            sqlScript = distinctColunm;
        }
        System.out.println("distinctColunm" + distinctColunm);
        String sql = getPageSqlzsw(page, sqlScript);
        String countSql = "select count(1) rc  from (" + sqlScript + ") a " + page.getWhereStr();
        System.out.println("countSql:" + countSql);
        _stmt = _conn.prepareStatement(countSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet countRs = _stmt.executeQuery();
        Long count = 0L;
        while (countRs.next()) {
//			Long count = countRs.getLong("rc");  //这段代码是谁注释掉的 ?????
            //count = countRs.getLong("rc");
            count = (long) countRs.getInt(1);
        }
        page.setDataCount(count);
        ResultSet rs = executeAnotherQuery(sql);
        return rs;
    }


    public static String getDistinctColunm(String sqlScript) throws SQLException {
        int fromIndex = sqlScript.indexOf("from");
        //此处是为了只去除字段
        int selectIndex = sqlScript.indexOf("distinct");
        if (selectIndex != -1) {
            selectIndex += 8;
        } else {
            selectIndex = 7;
        }
        if (fromIndex > -1) {
            String column = sqlScript.substring(selectIndex, fromIndex).trim();
            Map<String, Object> columnNameMap = new HashMap<String, Object>();
            String[] columnNameArr = column.split(",");
            if (columnNameArr.length > 0) {
                for (int i = 0; i < columnNameArr.length; i++) {
                    //列明别名
                    int lastIndex = columnNameArr[i].lastIndexOf(".");
                    if (lastIndex > -1) {
                        columnNameMap.put(columnNameArr[i].substring(lastIndex + 1), columnNameArr[i]);
                    } else {
                        columnNameMap.put(columnNameArr[i], columnNameArr[i]);
                    }
                }
            }
            //没有重复列
            if (columnNameArr.length == columnNameMap.size()) {
                return "";
            }
            String sql = "";
            if (selectIndex != 7) {
                sql = "select distinct ";
            } else {
                sql = "select ";
            }
            Set<String> sets = columnNameMap.keySet();
            for (String set : sets) {
                sql += columnNameMap.get(set) + ",";
            }
            sql = sql.substring(0, sql.length() - 1);
            String fromStr = sqlScript.substring(fromIndex);
            String result = sql + "  " + fromStr;
            return result;
        }
        return "";
    }

    /**
     * 传入SqlScript实现分页 zhangsiwei
     *
     * @param page      分页信息
     * @param sqlScript 模型脚本
     * @param params    适合于参数形式是 ? ? ? ? ?格式
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPagezsw(Pagination page, String sqlScript, List<Object> params) throws SQLException {


        String sql = getPageSqlzsw(page, sqlScript);
        String countSql = "select count(1) rc  from (" + sqlScript + ") a " + page.getWhereStr();
        _stmt = _conn.prepareStatement(countSql);
        if (params != null && params.size() != 0) {
            int index = 1;
            for (Object object : params) {
                _stmt.setObject(index++, object);
            }
        }
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }
        _stmt = _conn.prepareStatement(sql);
        if (params != null && params.size() != 0) {
            int index = 1;
            for (Object object : params) {
                _stmt.setObject(index++, object);
            }
        }
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 传入SqlScript实现分页 zhangsiwei
     *
     * @param page         分页信息
     * @param sqlScript    模型脚本
     * @param paramsvalues 适合于参数形式是 ?参数名称1,?参数名称2 ?参数名称3 ?参数名称4 ?参数名称5 格式
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPagezsw(Pagination page, String sqlScript, Map<String, Object> paramsvalues)
            throws SQLException {
        String sql = getPageSqlzsw(page, sqlScript);
        String countSql = "select count(1) rc  from (" + sqlScript + ") a " + page.getWhereStr();

        if (paramsvalues != null && paramsvalues.size() != 0) {
            NSQL nsqlobj = NSQL.get(countSql);
            _stmt = _conn.prepareStatement(nsqlobj.getSql());
            for (String key : paramsvalues.keySet()) {
                nsqlobj.setParameter(_stmt, key, paramsvalues.get(key));
            }
        } else {
            _stmt = _conn.prepareStatement(countSql);
        }
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }

        if (paramsvalues != null && paramsvalues.size() != 0) {
            NSQL nsqlobj = NSQL.get(sql);
            _stmt = _conn.prepareStatement(nsqlobj.getSql());
            for (String key : paramsvalues.keySet()) {
                nsqlobj.setParameter(_stmt, key, paramsvalues.get(key));
            }
        } else {
            _stmt = _conn.prepareStatement(sql);
        }

        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 传入SqlScript实现达梦、oracle数据库分页 zhangsiweiG
     *
     * @param page      分页信息
     * @param sqlScript 模型脚本
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPageDMOra(Pagination page, String sqlScript) throws SQLException {
        String sql = getPageSqlDMOracle(page, sqlScript);
        String countSql = "select count(1) rc  from (" + sqlScript + ") a " + page.getWhereStr();
        _stmt = _conn.prepareStatement(countSql);
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 传入SqlScript实现达梦、oracle数据库分页 zhangsiweiG
     *
     * @param page      分页信息
     * @param sqlScript 模型脚本
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPageDMOra(Pagination page, String sqlScript,Integer tableRowCount) throws SQLException {
        page.setDataCount(tableRowCount);
        page.getPageCount();
        String sql = getPageSqlDMOracle(page, sqlScript);
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }

    /**
     * 传入SqlScript实现达梦、ST数据库分页 zhangsiweiG
     *
     * @param page      分页信息
     * @param sqlScript 模型脚本
     * @return 返回数据
     * @throws SQLException
     */
    public ResultSet executeQueryByPageSTOra(Pagination page, String sqlScript) throws SQLException {
        String sql = getPageSqlSTOracle(page, sqlScript);
        String countSql = "select count(1) rc  from (" + sqlScript + ") a " + page.getWhereStr();
        _stmt = _conn.prepareStatement(countSql);
        ResultSet countRs = _stmt.executeQuery();
        while (countRs.next()) {
            Long count = countRs.getLong("rc");
            page.setDataCount(count);
            page.getPageCount();
        }
        _stmt = _conn.prepareStatement(sql);
        ResultSet rs = _stmt.executeQuery();
        return rs;
    }


    /**
     * @param tableName 获取表下所有字段 如为null 获取所有表字段 （oracle）
     * @return
     * @throws SQLException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Map> getTableColumnsORACLE(String tableName) throws SQLException {
        List<Map> list = new ArrayList<Map>();
        String sqlString = "select Table_Name tableName, column_name columnName, data_type columnType, data_length columnLen, nullable isnullable  from user_tab_columns where 1=1 ";
        if (!StringUtil.isEmpty(sqlString)) {
            sqlString += " and Table_Name='" + tableName + "'";
        }
        sqlString += " order by column_id";
        _stmt = _conn.prepareStatement(sqlString);
        ResultSet result = _stmt.executeQuery();
        while (result.next()) {
            Map temp = new HashMap();
            temp.put("columnName", result.getString("columnName"));
            temp.put("tableName", result.getString("tableName"));
            temp.put("columnType", result.getString("columnType"));
            list.add(temp);
        }
        return list;
    }

    public boolean execute(String sql) {
        boolean flag = false;
        try {
            flag = this.execute(sql, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    public boolean execute(String sql, boolean isClose) {
        try {
            _stmt = _conn.prepareStatement(sql);
            _stmt.execute();
            if (isClose) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            System.out.println("----------execute():" + e.getMessage());
            if (_conn != null) {
                this.close();
            }
            return false;
        }
    }

    public boolean executeForDmpBak(String sql, boolean isClose) throws SQLException {
        _stmt = _conn.prepareStatement(sql);
        _stmt.execute();
        if (isClose) {
            this.close();
        }
        return true;
    }

    public void executeORCLAndThrow(String sqls) throws Exception {
        String temp = "";
        try {
            String sql[] = sqls.split(";");
            for (String s : sql) {
                if (!s.trim().equals("")) {
                    temp = s;
                    _stmt = _conn.prepareStatement(s);
                    _stmt.execute();
                }
            }
        } catch (Exception e) {
            System.out.println("执行SQL:【" + temp + "】出错");
            e.printStackTrace();
            throw e;
        }
    }

    public boolean execute(String sql, List<Object> params) {
        boolean flag = false;
        try {
            flag = this.execute(sql, true, params);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    public boolean execute(String sql, boolean isClose, List<Object> params) {
        try {
            _stmt = _conn.prepareStatement(sql);
            if (params != null && params.size() != 0) {
                int index = 1;
                for (Object object : params) {
                    _stmt.setObject(index++, object);
                }
            }
            _stmt.execute();
            if (isClose) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            System.out.println("----------execute():" + e.getMessage());
            if (_conn != null) {
                this.close();
            }
            return false;
        }
    }

    public boolean execute(String sql, Map<String, Object> params) {
        boolean flag = false;
        try {
            flag = this.execute(sql, true, params);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }

    public boolean execute(String sql, boolean isClose, Map<String, Object> paramsvalues) {
        try {
            /*
             * _stmt = _conn.prepareStatement(sql); if (params != null && params.size() !=
             * 0) { int index = 1; for (Object object : params) { _stmt.setObject(index++,
             * object); } }
             */

            if (paramsvalues != null && paramsvalues.size() != 0) {
                NSQL nsqlobj = NSQL.get(sql);
                _stmt = _conn.prepareStatement(nsqlobj.getSql());
                for (String key : paramsvalues.keySet()) {
                    nsqlobj.setParameter(_stmt, key, paramsvalues.get(key));
                }
            } else {
                _stmt = _conn.prepareStatement(sql);
            }

            _stmt.execute();
            if (isClose) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            System.out.println("----------execute():" + e.getMessage());
            if (_conn != null) {
                this.close();
            }
            return false;
        }
    }

    /**
     * 判断连接是否关闭 如果没关闭返回false 关闭返回true
     *
     * @return 返回false 或true
     * @throws SQLException Sql异常
     */
    public boolean isClose() throws SQLException {
        return _conn.isClosed();
    }

    public void close() {
        try {
            this._stmt.close();
            this._conn.close();
        } catch (Exception e) {

        }
    }

    public String getPageSql(Pagination page, String tableName) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
        if (start != 0) {
            start++;
            end = start + page.getPageSize() - 1;
        }
        String sql = "select  * from (SELECT  t_random.*, ROW_NUMBER() OVER (ORDER BY  " + page.getOrderBy()
                + "   ) as rownr FROM ( " + "select  *  from    " + tableName + "   " + page.getWhereStr()
                + ") t_random   ) t1_random  " + " where t1_random.rownr between " + start + " and " + (end) + "    ";
        return sql;
    }

    /**
     * 传入一个Sql返回分页信息
     *
     * @param page
     * @param sqlScript
     * @return
     */
    public String getPageSqlzsw(Pagination page, String sqlScript) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
        if (start != 0) {
            start++;
            end = start + page.getPageSize() - 1;
        }
        String sql = "select  * from (SELECT  t_random.*, ROW_NUMBER() OVER (ORDER BY  " + page.getOrderBy()
                + "   ) as rownr FROM ( " + sqlScript + "   " + page.getWhereStr() + ") t_random   ) t1_random  "
                + " where t1_random.rownr between " + start + " and " + (end) + "    ";
        return sql;
    }

    /**
     * 达梦数据库分页Sql ZhangSiWeiG
     *
     * @param page      分页信息
     * @param sqlScript Sql语句
     * @return
     */
    public String getPageSqlDMOracle(Pagination page, String sqlScript) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
        if (start != 0) {
            start++;
            end = start + page.getPageSize() - 1;
        }
        String sql = "select  * from (SELECT  t_random.*,ROWNUM rownrs  FROM ( " + sqlScript + "   "
                + page.getWhereStr() + ") t_random   ) t1_random  " + " where t1_random.rownrs between " + start
                + " and " + (end) + "    ";
        return sql;
    }

    /**
     * 达梦数据库分页Sql ZhangSiWeiG
     *
     * @param page      分页信息
     * @param sqlScript Sql语句
     * @return
     */
    public String getPageSqlSTOracle(Pagination page, String sqlScript) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
        if (start != 0) {
            start++;
            end = start + page.getPageSize() - 1;
        }
        String sql = "select  * from (SELECT  t_random.*,ROWNUM rownrs  FROM ( " + sqlScript + "   "
                + page.getWhereStr() + ") t_random   ) t1_random  " + " where t1_random.rownrs between " + start
                + " and " + (end) + "    ";
        return sql;
    }

    /**
     * 达梦数据库分页Sql ZhangSiWeiG
     *
     * @param page      分页信息
     * @param tableName 表名
     * @return
     */
    public String getPageTableNameDMOracle(Pagination page, String tableName) {
        int start = (page.getPageNum() - 1) * page.getPageSize();
        int end = start + page.getPageSize();
        if (start != 0) {
            start++;
            end = start + page.getPageSize() - 1;
        }
        String sql = "select  * from (SELECT  t_random.*,ROWNUM rownrs  FROM ( " + "select * from " + tableName + " "
                + page.getWhereStr() + ") t_random   ) t1_random  " + " where t1_random.rownrs between " + start
                + " and " + (end) + "    ";
        return sql;
    }

    /**
     * 获取insert语句 加入到疑点库专用 ZhangSiWeiG
     *
     * @param rs 要获取的结果集
     * @return 返回insert语句
     * @throws Exception
     */
    private String getInsertSql(ResultSet rs) throws Exception {
        ResultSetMetaData rsmd = rs.getMetaData();
        String column = "";
        String insertSql = "insert into aa_doubtbase (疑点详细编号,疑点编号,疑点名称,疑点状态,创建时间,模型名称,模型编号,预算单位名称,预算单位代码,疑点说明,延伸意见,序号,%s) values "
                + "(";
        for (int i = 0; i < rsmd.getColumnCount(); i++) // 循环列将下标加上
        {
            column += "n" + (i + 1) + ",";
            insertSql += "?" + ",";
        }
        column = column.substring(0, column.length() - 1);
        insertSql += "?,?,?,?,?,?,?,?,?,?,?,?)";// 加上固定字段的下标
        insertSql = String.format(insertSql, column);
        return insertSql;
    }



    /**
     * 将英文数据类型转化为中文
     *
     * @param dataType
     * @return
     */
    public String getDataTypeByOracle(String dataType) {
        String dataTypeNew = null;
        switch (dataType) {
            case "MONEY":
                dataTypeNew = "";
                break;
            case "VARCHAR":
                dataTypeNew = "字符型";
                break;
            case "VARCHAR2":
                dataTypeNew = "字符型";
                break;
            case "NUMBER":
                dataTypeNew = "数字型";
                break;
            case "TIMESTAMP(6)":
                dataTypeNew = "时间戳型";
                break;
            case "TIMESTAMP":
                dataTypeNew = "时间戳型";
                break;
            case "DATE":
                dataTypeNew = "日期型";
                break;
        }
        return dataTypeNew;
    }

    public boolean execute_stmt(String sql) throws SQLException {
        _stmt = _conn.prepareStatement(sql);
        _stmt.execute();
        return true;
    }
    public boolean executeCreateTables(String sql) throws SQLException{
        _stmt = _conn.prepareStatement(sql);
        _stmt.execute();
        return true;
    }
}
