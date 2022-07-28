package com.dfdk.common.codegenerator;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
1.Mybatis-plus 自动生成 entity  mapper  *Mapper.xml  service  serviceImpl 等文件

2.请不要随便修改 代码生成器

3. application.yml 中开启驼峰命名 javaBean与表字段自动映射,不需要手动配置javaBean与表之间的关系
        mybatis:
           configuration:
                map-underscore-to-camel-case: true

4.每次只允许输入一张表名, 一次只生成一张数据库表的配置

5.直接运行 run  CodeGenerator.java  然后输入一张表名 即可生成表的配置文件

6.多表关联查询 需要在 *Mapper.xml 文件里,自定义SQL语句

7.修改 数据库连接 密码 用户名 数据库名 , 在 CodeGenerator.java类中请修改 DBUtil类的 getDataSourceConfig方法配置

8.如需生成数据库所有表的配置, 请联系韦明发操作.
*/

public class CodeGenerator {


    public static void main(String[] args) {
       /* Scanner scanner = new Scanner(System.in);
        System.out.println("Mybatis自动工程-请输入表名: ");
        if(scanner.hasNext()) {
            String flag = scanner.nextLine().trim();
            createTableService(flag);
        }*/
        createAlltables();

    }

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public  static  void createAlltables(){
        //查询所有的数据库表
        String allTables = getAllTables();
        //String[] tablelist = allTables.split(",");
        createTableService(allTables);
        /*for (String item : tablelist) {
            createTableService(item);
        }*/

    }


    /**
     * <p>
     * 读取控制台内容
     * </p>
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
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static   void createTableService(String tableName) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = "HuiZhouElectricityAndOpticalCable";
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("frank");
        gc.setSwagger2(true);
        gc.setOpen(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        //gc.setControllerName("%Controller");
        gc.setFileOverride(true);

        mpg.setGlobalConfig(gc);

        DataSourceConfig dsc = DBUtil.getDataSourceConfig();
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //pc.setModuleName(scanner("模块名"));
        pc.setParent(null);
        // 这个地址是生成的 javaBean配置文件的包路径
        pc.setEntity("com.dfdk.entity");
        //生成 controller 类包名
        pc.setController("com.dfdk.controller");
        //生成 mapper 类包名
        pc.setMapper("com.dfdk.mapper");
        //生成service 包名
        pc.setService("com.dfdk.service");
        //生成serviceImpl实现类包
        pc.setServiceImpl("com.dfdk.service.impl");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        //String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper"
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        templateConfig.setController("/templates/controller.java.vm");


        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        //开启entity 表字段注解
        strategy.setEntityTableFieldAnnotationEnable(true);

        //为防止出错 每次只能 操作一张表
        strategy.setInclude(tableName.split(","));

        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplate(templateConfig);
        //mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

    public static String getAllTables(){
        String tableList="";

        System.out.println("开始查询数据库表,请稍等!");
        DBUtil dbUtil = new DBUtil();
        Connection conn = dbUtil.getConnection();
        PreparedStatement pstmt = null;
        String sql="select table_name from information_schema.tables where table_schema='huizhou_electric_cable_system'";
        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("查询数据库表完成!");


            while (rs.next()){
                String tableName = rs.getString(1);
                if(!tableName.equalsIgnoreCase("PDMAN_DB_VERSION")){
                    tableList=tableList+tableName+",";
                }
            }


        } catch (SQLException throwables) {

        }finally {
            dbUtil.closeJDBC(null,pstmt,conn);
        }
        int index = tableList.lastIndexOf(",");
        String substring = tableList.substring(0,index);
        System.out.println("所有数据库表:"+substring);
        return substring;
    }
}


