package com.icss.gateway.utils.mybatis;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 可生成controller\service\dao\domain相关规范代码，适用于人保团队
 * 注意：表设计需加注释，代码自动生成用到
 */
public class CodeGenerator {

    /**
     * 表名称（自行修改）
     */
    public static String[] tables = {"XC_POLICY"};
    /**
     * 表前缀（自行修改）
     */
    public static final String Table_PREFIX = "XC_";
    /**
     * 数据源连接用户（自行修改）
     */
    public static final String DB_USERNAME = "rbfj";
    /**
     * 数据源连接密码
     */
    public static final String DB_PWD = "pass1009";
    /**
     * 包子模块名（自行修改）
     */
    public static final String PACK_MODULE_NAME = "policyManagement";
    /**
     * 项目名称（自行修改）
     */
    public static final String PROJECT_NAME = "/com.icss.dataanalysis.impl";

    /**
     * controller项目路径
     */
    public static final String PROJECT_NAME_CONTROLLER = "/com.icss.dataanalysis.api";

    /**
     * 作者名称
     */
    public static final String AUTHOR_NAME = "weiyujie";

    /**
     * 读取控制台内容
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        return "";
    }

    public static void main(String[] args) {
        buildImpl();
    }

    /**
     * 生成方法
     */
    private static void buildImpl(){
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
// 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.icss.dataanalysis");
        packageConfig.setModuleName("");
        packageConfig.setController("controller" + "." + PACK_MODULE_NAME);
        packageConfig.setEntity("model" + "." + PACK_MODULE_NAME);
        packageConfig.setMapper("dao" + "." + PACK_MODULE_NAME);
        packageConfig.setService("service" + "." + PACK_MODULE_NAME);
        packageConfig.setServiceImpl("service.impl" + "." + PACK_MODULE_NAME);
        mpg.setPackageInfo(packageConfig);
        // 获取项目路径
        String projectPath = getProjectPath();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setFileOverride(true);
//        globalConfig.setActiveRecord(true);
        globalConfig.setEnableCache(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setOpen(true);
        globalConfig.setSwagger2(true);
        globalConfig.setAuthor(AUTHOR_NAME);
        globalConfig.setMapperName("%sMapper");
        globalConfig.setXmlName("%sMapper");
        globalConfig.setServiceName("%sService");
        globalConfig.setServiceImplName("%sServiceImpl");
        globalConfig.setControllerName("%sController");
        globalConfig.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        mpg.setGlobalConfig(globalConfig);

        // 数据源配置

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.ORACLE);
        dsc.setUrl("jdbc:oracle:thin:@10.10.23.21:1521:orcl");
        dsc.setDriverName("oracle.jdbc.driver.OracleDriver");
        dsc.setUsername(DB_USERNAME);
        dsc.setPassword(DB_PWD);
        mpg.setDataSource(dsc);



        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> fileOutList = new ArrayList<FileOutConfig>();
        fileOutList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/resources/mappers/" + PACK_MODULE_NAME + "/Mapper.xml";
            }
        });
        cfg.setFileOutConfigList(fileOutList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        templateConfig.setEntity("templates/entity.java.vm");
        templateConfig.setService("templates/service.java.vm");
        templateConfig.setController("templates/controller.java.vm");
        templateConfig.setServiceImpl("templates/serviceImpl.java.vm");
        templateConfig.setMapper("templates/mapper.java.vm");
        templateConfig.setXml(null);
//        templateConfig.setXml("templates/mapper.xml.vm");
        mpg.setTemplate(templateConfig);

        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
//        String templatePath = "/templates/mapper.xml.ftl";
        String templatePath = "/templates/mapper.xml.vm";

        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                String path = projectPath + "/src/main/resources/mapper/" + PACK_MODULE_NAME + "/" +
                        tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                File file=new File(projectPath + "/src/main/resources/mapper/" + PACK_MODULE_NAME);
                if (file.exists()) {
                    file.delete();
                    file.mkdirs();
                } else {
                    file.mkdirs();
                }
                return path;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setControllerMappingHyphenStyle(true)
                .setLogicDeleteFieldName("IS_DELETED")
                .setTablePrefix(Table_PREFIX)
                .setInclude(tables)
                .setEntityColumnConstant(true)
//                .setSuperEntityColumns("keyword")
                .setTableFillList(Arrays.asList(
                        new TableFill("CREATE_TIME", FieldFill.INSERT),
                        new TableFill("UPDATE_TIME", FieldFill.UPDATE),
                        new TableFill("CREATE_USER_NAME", FieldFill.INSERT),
                        new TableFill("UPDATE_USER_NAME", FieldFill.UPDATE),
                        new TableFill("CREATE_USER_UUID", FieldFill.INSERT),
                        new TableFill("UPDATE_USER_UUID", FieldFill.UPDATE),
                        new TableFill("IS_DELETED", FieldFill.INSERT)
                ));
        mpg.setStrategy(strategyConfig);
        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }
    private static void buildController(){
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.icss.dataanalysis");
        packageConfig.setModuleName("");
        packageConfig.setController("controller" + "." + PACK_MODULE_NAME);
        mpg.setPackageInfo(packageConfig);
        // 获取项目路径
        String projectPath = getControllerPath();
        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setFileOverride(true);
//        globalConfig.setActiveRecord(true);
        globalConfig.setEnableCache(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setOpen(true);
        globalConfig.setSwagger2(true);
        globalConfig.setAuthor(AUTHOR_NAME);
        globalConfig.setControllerName("%sController");
        globalConfig.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
        mpg.setGlobalConfig(globalConfig);

        // 数据源配置

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.ORACLE);
        dsc.setUrl("jdbc:oracle:thin:@111.200.255.51:29200:orcl");
        dsc.setDriverName("oracle.jdbc.driver.OracleDriver");
        dsc.setUsername(DB_USERNAME);
        dsc.setPassword(DB_PWD);
        mpg.setDataSource(dsc);



        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别

        templateConfig.setController("templates/controller.java.vm");
        mpg.setTemplate(templateConfig);


        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig()
                .setNaming(NamingStrategy.underline_to_camel)
                .setColumnNaming(NamingStrategy.underline_to_camel)
                .setEntityLombokModel(true)
                .setRestControllerStyle(true)
                .setControllerMappingHyphenStyle(true)
                .setLogicDeleteFieldName("IS_DELETED")
                .setTablePrefix(Table_PREFIX)
                .setInclude(tables)
                .setEntityColumnConstant(true)
//                .setSuperEntityColumns("keyword")
                .setTableFillList(Arrays.asList(
                        new TableFill("CREATE_TIME", FieldFill.INSERT),
                        new TableFill("UPDATE_TIME", FieldFill.UPDATE),
                        new TableFill("CREATE_USER_NAME", FieldFill.INSERT),
                        new TableFill("UPDATE_USER_NAME", FieldFill.UPDATE),
                        new TableFill("CREATE_USER_UUID", FieldFill.INSERT),
                        new TableFill("UPDATE_USER_UUID", FieldFill.UPDATE),
                        new TableFill("IS_DELETED", FieldFill.INSERT)
                ));
        mpg.setStrategy(strategyConfig);
        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }
    /**
     * 获取impl项目路径
     * @return 返回路径
     */
    private static String getProjectPath() {
        String projectPath = System.getProperty("user.dir") + PROJECT_NAME;
        return projectPath;
    }


    /**
     * 获取controller项目路径
     * @return 返回路径
     */
    private static String getControllerPath(){
        String projectPath = System.getProperty("user.dir") + PROJECT_NAME_CONTROLLER;
        return projectPath;
    }
}

