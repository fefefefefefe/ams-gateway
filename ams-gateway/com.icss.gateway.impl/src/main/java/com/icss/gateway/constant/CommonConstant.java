package com.icss.gateway.constant;

public class CommonConstant {

	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_OK_200 = 200;
    
    /**访问权限认证未通过 510*/
    public static final Integer SC_JEECG_NO_AUTHZ=510;


    /**
     * 删除标识(0、否  1、是)
     */
    public static final Integer IS_DELETE_0 = 0;
    public static final Integer IS_DELETE_1 = 1;
    /**
     * 执行类型  1:sql编辑器运行，2：定时跑批运行,3:最后一个sql语句数据导出，4：图形化工具sql执行
     */
    public static final String EXECUTE_TYPE_1 = "1";
    public static final String EXECUTE_TYPE_2 = "2";
    public static final String EXECUTE_TYPE_3 = "3";
    public static final String EXECUTE_TYPE_4 = "4";


    public static final long EXPORT_MAX_NUM = 999999999999999l;


    /**
     * 项目规划库登录名、模式的默认前缀
     */
    public static final String PROJECT_DB_PREFIX ="RBFJBUS_";


    /**
     * 数据库类型
     */
    public static final String DB_TYPE_ORACLE ="oracle";

    /**
     * 数据库类型
     */
    public static final String DB_DRIVER_ORACLE ="oracle.jdbc.driver.OracleDriver";

    /**
     * 规则运行状态 1:待执行,2:执行中,3:执行失败,4:已完成
     */
    public static final Integer RUN_STATUS_1 =1;
    public static final Integer RUN_STATUS_2 =2;
    public static final Integer RUN_STATUS_3 =3;
    public static final Integer RUN_STATUS_4 =4;

    /**
     * 数据库树层级类型  folder：文件夹 ，table：表 ，view：视图
     */
    public static final String DATABASE_FILE_TYPE_FOLDER ="folder";
    public static final String DATABASE_FILE_TYPE_TABLE ="table";
    public static final String DATABASE_FILE_TYPE_VIEW ="view";
    public static final String DATABASE_FILE_TYPE_COLUMN ="column";
    /**
     * 规则分类  1：全国通用规则，2：地区个性化规则
     */
    public static final String[] FOLDER_TYPES = {"ce612246-0b2b-4076-90e5-fafe31e5644e","d39a21f4-13a7-4e19-9f4b-e008a4294866"};

}
