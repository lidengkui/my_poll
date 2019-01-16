package com.poll.dao.util;

import com.poll.common.util.DateUtil;
import com.poll.common.util.FileUtil;
import com.poll.common.util.RandomUtil;
import com.poll.common.util.StringUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * 读取数据库对应表辅助生成MybatisPlus相关文件
 */
public class JavaCodeUtil4MyBatisPlus {

    public static void main(String[] args) throws Exception {

        String tableName = "";

        createEntity(tableName);
//        createMapperJava(tableName);
        createMapperXml(tableName);
//        createService(tableName);
//        createServiceImpl(tableName);
//        createServiceTest(tableName);

    }

    //注释相关
    private static final String AUTHOR = "gaoyuan";

    //数据库相关配置
    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
    private static final String CONNECTION_URL = "jdbc:mysql://192.168.3.114:3306/har-poll?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull";
    private static final String DB_USER_NAME = "root";
    private static final String DB_PASSWD = "root";

    //maven工程基础路径
    private static final String MAVEN_MAIN_JAVA_PATH = "src/main/java/";
    private static final String MAVEN_MAIN_RESOURCES_PATH = "src/main/resources/";
    private static final String MAVEN_TEST_JAVA_PATH = "src/test/java/";


    //实体类包路径
    private static final String ENTITY_PACKAGE_PATH = "com/poll/entity";
    //实体类放置路径
    private static final String ENTITY_FILE_PATH = "/entity/" + MAVEN_MAIN_JAVA_PATH + ENTITY_PACKAGE_PATH + "/";
    //实体类文件名后缀
    private static final String ENTITY_FILE_NAME_SUFFIX = "Entity";


    //.java mapper文件包路径
    private static final String MAPPER_JAVA_PACKAGE_PATH = "com/poll/dao/mapper";
    //.java mapper放置路径
    private static final String MAPPER_JAVA_FILE_PATH = "/dao/" + MAVEN_MAIN_JAVA_PATH + MAPPER_JAVA_PACKAGE_PATH + "/";
    //.java mapper文件后缀
    private static final String MAPPER_JAVA_FILE_NAME_SUFFIX = "Mapper";


    //.xml mapper文件包路径
    private static final String MAPPER_XML_PACKAGE_PATH = "mapper";
    //.xml mapper放置路径
    private static final String MAPPER_XML_FILE_PATH = "/dao/" + MAVEN_MAIN_RESOURCES_PATH + MAPPER_XML_PACKAGE_PATH + "/";
    //.xml mapper文件后缀
    private static final String MAPPER_XML_FILE_NAME_SUFFIX = "Mapper";


    //service文件包路径
    private static final String SERVICE_PACKAGE_PATH = "com/poll/dao/service";
    //service放置路径
    private static final String SERVICE_FILE_PATH = "/dao/" + MAVEN_MAIN_JAVA_PATH + SERVICE_PACKAGE_PATH + "/";
    //service文件后缀
    private static final String SERVICE_FILE_NAME_SUFFIX = "Service";


    //service实现类文件包路径
    private static final String SERVICE_IMPL_PACKAGE_PATH = SERVICE_PACKAGE_PATH + "/impl";
    //service实现类放置路径
    private static final String SERVICE_IMPL_FILE_PATH = "/dao/" + MAVEN_MAIN_JAVA_PATH + SERVICE_IMPL_PACKAGE_PATH + "/";
    //service实现类文件后缀
    private static final String SERVICE_IMPL_FILE_NAME_SUFFIX = "ServiceImpl";


    //service测试类文件包路径
    private static final String SERVICE_TEST_PACKAGE_PATH = SERVICE_IMPL_PACKAGE_PATH;
    //service测试类放置路径
    private static final String SERVICE_TEST_FILE_PATH = "/dao/" + MAVEN_TEST_JAVA_PATH + SERVICE_TEST_PACKAGE_PATH + "/";
    //service测试类文件后缀
    private static final String SERVICE_TEST_FILE_NAME_SUFFIX = "ServiceImplTest";


	/**
	 * 取当前工程根路径
	 * @return
	 */
	public static String getProjPath() {
		return System.getProperty("user.dir");
	}

    /**
     * 取得数据库字段与类型map映射
     * @param tableName
     * @return
     */
	public static Map<String, String> getTableFieldTypeMap(String tableName) throws Exception {

        Class.forName(DRIVER_CLASS_NAME).newInstance();
        Connection connection = DriverManager.getConnection(CONNECTION_URL, DB_USER_NAME, DB_PASSWD);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery( "desc " + tableName);

        Map<String, String> fieldTypeMap = new LinkedHashMap<String, String>();

        while (rs.next()) {
            fieldTypeMap.put(rs.getString(1), rs.getString(2) + " " + rs.getString(4) + " " + rs.getString(6));
        }

        rs.close();
        statement.close();
        connection. close();

	    return fieldTypeMap;
    }

    /**
     * 将数据库数据类型转换为对应java类型
     * @param dbFieldType
     * @return
     */
    public static String cvtDbType2JavaType(String dbFieldType) {

        if (dbFieldType.startsWith("bigint")) {
            return "Long";
        }
        if (dbFieldType.startsWith("mediumint") || dbFieldType.startsWith("int")) {
            return "Integer";
        }
        if (dbFieldType.startsWith("tinyint")) {
            return "Byte";
        }
        if (dbFieldType.startsWith("smallint")) {
            return "Short";
        }
        if (dbFieldType.startsWith("float")) {
            return "Float";
        }
        if (dbFieldType.startsWith("double")) {
            return "Double";
        }
        if (dbFieldType.startsWith("decimal")) {
            return "BigDecimal";
        }
        if (dbFieldType.startsWith("varchar") || dbFieldType.startsWith("char") || dbFieldType.startsWith("tinytext")
                || dbFieldType.startsWith("mediumtext") || dbFieldType.startsWith("longtext") || dbFieldType.startsWith("text")  ) {
            return "String";
        }
        if (dbFieldType.startsWith("datetime") || dbFieldType.startsWith("timestamp") || dbFieldType.startsWith("date")
                || dbFieldType.startsWith("time")  || dbFieldType.startsWith("year")) {
            return "Date";
        }
        if (dbFieldType.startsWith("bit")) {
            return "Boolean";
        }

	    return null;
    }

    /**
     * 转化数据库类型至xmlMapper文件中的数据类型
     * @param dbFieldType
     * @return
     */
    public static String cvtDbType2MapperXmlJdbcType(String dbFieldType) {

        if (dbFieldType.startsWith("mediumint") || dbFieldType.startsWith("int")) {
            return "INTEGER";
        }
        if (dbFieldType.startsWith("float")) {
            return "REAL";
        }
        if (dbFieldType.startsWith("double")) {
            return "DOUBLE";
        }
        if (dbFieldType.startsWith("decimal")) {
            return "DECIMAL";
        }
        if (dbFieldType.startsWith("char")) {
            return "CHAR";
        }
        if (dbFieldType.startsWith("varchar") || dbFieldType.startsWith("tinytext")) {
            return "VARCHAR";
        }
        if (dbFieldType.startsWith("mediumtext") || dbFieldType.startsWith("longtext") || dbFieldType.startsWith("text")) {
            return "LONGVARCHAR";
        }
        if (dbFieldType.startsWith("datetime") || dbFieldType.startsWith("timestamp")) {
            return "TIMESTAMP";
        }
        if (dbFieldType.startsWith("date") || dbFieldType.startsWith("year")) {
            return "DATE";
        }
        if (dbFieldType.startsWith("time")) {
            return "TIME";
        }

        return dbFieldType.toUpperCase();
    }

    /**
     * 根据参数类型字符串生成set方法参数字符串以及导入字符串
     * @param paramTypeStr
     * @return
     */
    public static String[] genSetMethodParamStrAndImportStr(String paramTypeStr) {

        String paramSetStr = null;
        String importStr = null;

        if (paramTypeStr.contains("Long")) {
            paramSetStr = "1L";
        } else if (paramTypeStr.contains("Byte")) {
            paramSetStr = "new Byte(\"1\")";
        } else if (paramTypeStr.contains("Short")) {
            paramSetStr = "new Short(\"1\")";
        } else if (paramTypeStr.contains("Integer")) {
            paramSetStr = "1";
        } else if (paramTypeStr.contains("Float")) {
            paramSetStr = String.format("new Float(\"%s\")", RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2));
        } else if (paramTypeStr.contains("Double")) {
            paramSetStr = RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2);
        } else if (paramTypeStr.contains("BigDecimal")) {
            paramSetStr = "new BigDecimal(\"" + RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2) + "\")";
            importStr = "import java.math.BigDecimal;";
        } else if (paramTypeStr.contains("String")) {
            paramSetStr = String.format("\"测试%s\"", RandomUtil.genLetterNumStr(2));
        } else if (paramTypeStr.contains("Date")) {
            paramSetStr = "new Date()";
            importStr = "import java.util.Date;";
        } else if (paramTypeStr.contains("Boolean")) {
            paramSetStr = "true";
        }

        return new String[] {paramSetStr, importStr};
    }

    /**
     * 根据数据库字段类型字符串生成set方法参数字符串以及导入字符串
     * @param dbFieldTypeStr
     * @return
     */
    public static String[] genSetMethodParamStrAndImportStrFromDb(String dbFieldTypeStr) {

        String paramSetStr = null;
        String importStr = null;

        if (dbFieldTypeStr.contains("bigint")) {
            paramSetStr = "1L";
        } else if (dbFieldTypeStr.contains("tinyint")) {
            paramSetStr = "new Byte(\"1\")";
        } else if (dbFieldTypeStr.contains("smallint")) {
            paramSetStr = "new Short(\"1\")";
        } else if (dbFieldTypeStr.contains("int") ||
                dbFieldTypeStr.contains("mediumint")) {
            paramSetStr = "1";
        } else if (dbFieldTypeStr.contains("flot")) {
            paramSetStr = String.format("new Float(\"%s\")", RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2));
        } else if (dbFieldTypeStr.contains("double")) {
            paramSetStr = RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2);
        } else if (dbFieldTypeStr.contains("decimal")) {
            paramSetStr = "new BigDecimal(\"" + RandomUtil.genNumberStr(1) + "." + RandomUtil.genNumberStr(2) + "\")";
            importStr = "import java.math.BigDecimal;";
        } else if (dbFieldTypeStr.contains("char") ||
                    dbFieldTypeStr.contains("varchar") ||
                    dbFieldTypeStr.contains("tinytext") ||
                    dbFieldTypeStr.contains("text") ||
                    dbFieldTypeStr.contains("mediumtext") ||
                    dbFieldTypeStr.contains("longtext")
                ) {
            paramSetStr = String.format("\"测试%s\"", RandomUtil.genLetterNumStr(2));
        } else if (dbFieldTypeStr.contains("date") ||
                dbFieldTypeStr.contains("time") ||
                dbFieldTypeStr.contains("datetime") ||
                dbFieldTypeStr.contains("timestamp") ||
                dbFieldTypeStr.contains("year")
                ) {
            paramSetStr = "new Date()";
            importStr = "import java.util.Date;";
        } else if (dbFieldTypeStr.contains("bit")) {
            paramSetStr = "true";
        }

        return new String[] {paramSetStr, importStr};
    }


    /**
     * 生成文件注释
     * @return
     */
    public static String genFileNotes() {
        return String.format("/**\n" +
                " * \n" +
                " * @author %s\n" +
                " * @since %s\n" +
                " **/", AUTHOR, DateUtil.getCurrentDateY_M_D());
    }

    /**
     * 创建entity
     * @param tableName
     * @throws Exception
     */
    public static void createEntity(String tableName) throws Exception {

        String className = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName)) + ENTITY_FILE_NAME_SUFFIX;

        File file = FileUtil.createFile(getProjPath() + ENTITY_FILE_PATH, className, ".java", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //导入部分
        StringBuilder sbImport = new StringBuilder();
        sbImport.append("import com.baomidou.mybatisplus.annotations.TableId;\r\n");
        sbImport.append("import com.baomidou.mybatisplus.annotations.TableName;\r\n");
        sbImport.append("import com.baomidou.mybatisplus.enums.IdType;\r\n");
        sbImport.append("import lombok.Getter;\r\n");
        sbImport.append("import lombok.Setter;\r\n\n");
        sbImport.append("import java.io.Serializable;\r\n");

        //类体部分
        StringBuilder sbBody = new StringBuilder();
        sbBody.append("@Getter\r\n");
        sbBody.append("@Setter\r\n");
        sbBody.append(String.format("@TableName(\"%s\")\r\n", tableName));
        sbBody.append(String.format("public class %s implements Serializable {\r\n\n",className));

        //此set用于import语句去重
        Set<String> setImport = new HashSet<String>();

        Iterator<Map.Entry<String, String>> iterator = getTableFieldTypeMap(tableName).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains(" PRI")) {
                sbBody.append(String.format("\t@TableId(type = IdType.%s)\r\n", value.contains(" auto_increment") ? "AUTO" : "INPUT"));
            }
            String javaType = cvtDbType2JavaType(value);
            sbBody.append(String.format("\tprivate %s %s;\r\n", javaType, StringUtil.cvtUnderline2Hump(key)));

            if ("Date".equals(javaType)) {
                setImport.add("import java.util.Date;");
            } else if ("BigDecimal".equals(javaType)) {
                setImport.add("import java.math.BigDecimal;");
            }
        }

        sbBody.append("}");

        //将引入语句追加至StringBuilder
        for (String ipt : setImport) {
            sbImport.append(ipt + "\r\n");
        }

        //写包名
        bw.write(String.format("package %s; \n\n", ENTITY_PACKAGE_PATH.replace("/", ".")));
        //写导入部分
        bw.write(sbImport.toString() + "\r\n");
        //写注释
        bw.write(genFileNotes() + "\r\n");
        //写类体
        bw.write(sbBody.toString());

        bw.flush();
        bw.close();

        System.out.println("创建entity------->" + file.getAbsolutePath());
    }

    /**
     * 创建.java mapper
     * @param tableName
     * @throws Exception
     */
    public static void createMapperJava(String tableName) throws Exception {

        String baseName = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName));
        String entityName = baseName + ENTITY_FILE_NAME_SUFFIX;
        String mapperName = baseName + MAPPER_JAVA_FILE_NAME_SUFFIX;

        File file = FileUtil.createFile(getProjPath() + MAPPER_JAVA_FILE_PATH, mapperName, ".java", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //导入部分
        StringBuilder sbImport = new StringBuilder();
        sbImport.append("import com.baomidou.mybatisplus.mapper.BaseMapper;\r\n");
        sbImport.append("import org.apache.ibatis.annotations.Mapper;\r\n");
        sbImport.append("import org.springframework.stereotype.Repository;\r\n");
        sbImport.append(String.format("import %s.%s;\r\n", ENTITY_PACKAGE_PATH.replace("/", "."), entityName));

        //类体部分
        StringBuilder sbBody = new StringBuilder();
        sbBody.append("@Mapper\r\n");
        sbBody.append(String.format("@Repository(\"%s\")\r\n", StringUtil.replaceFirstChar2Lower(mapperName)));
        sbBody.append(String.format("public interface %s extends BaseMapper<%s> {\r\n\n", mapperName, entityName));
        sbBody.append("}");

        //写包名
        bw.write(String.format("package %s; \r\n\n", MAPPER_JAVA_PACKAGE_PATH.replace("/", ".")));
        //写导入部分
        bw.write(sbImport.toString() + "\r\n");
        //写注释
        bw.write(genFileNotes() + "\r\n");
        //写类体
        bw.write(sbBody.toString());

        bw.flush();
        bw.close();

        System.out.println("创建mappper.java->" + file.getAbsolutePath());
    }

    /**
     * 创建.xml mapper
     * @param tableName
     * @throws Exception
     */
    public static void createMapperXml(String tableName) throws Exception {

        String baseName = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName));
        String entityName = baseName + ENTITY_FILE_NAME_SUFFIX;
        String mapperName = baseName + MAPPER_XML_FILE_NAME_SUFFIX;

        File file = FileUtil.createFile(getProjPath() + MAPPER_XML_FILE_PATH, mapperName, ".xml", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //写包名
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\r\n\n");

        bw.write(String.format("<mapper namespace=\"%s.%s\" > \r\n\n", MAPPER_JAVA_PACKAGE_PATH.replace("/", "."), mapperName));

        bw.write(String.format("\t<resultMap id=\"BaseResultMap\" type=\"%s.%s\" >\r\n",ENTITY_PACKAGE_PATH.replace("/", "."), entityName));

        StringBuilder sbColumns = new StringBuilder();

        int index = 0;

        Iterator<Map.Entry<String, String>> iterator = getTableFieldTypeMap(tableName).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            String prefix = value.contains(" PRI") ? "id" : "result";
            bw.write(String.format("\t\t<%s column=\"%s\" property=\"%s\" jdbcType=\"%s\" />\r\n", prefix, key, StringUtil.cvtUnderline2Hump(key), cvtDbType2MapperXmlJdbcType(value.split(" ")[0].split("\\(")[0])));

            if (sbColumns.length() > 0) {
                sbColumns.append(", ");
            }

            int i = sbColumns.length() / 100;

            if (i > index) {
                sbColumns.append("\r\n\t\t");
                index = i;
            }

            sbColumns.append(key);
        }
        bw.write("\t</resultMap>\r\n\n");

        bw.write(String.format("\t<sql id=\"Base_Column_List\" >\r\n\t\t%s\r\n\t</sql>\r\n\r\n", sbColumns.toString()));
        bw.write("\t<sql id=\"Min_Column_List\" >\r\n\t</sql>\r\n\r\n");

        bw.write("</mapper>");

        bw.flush();
        bw.close();

        System.out.println("创建mappper.xml-->" + file.getAbsolutePath());
    }

    /**
     * 创建service
     * @param tableName
     * @throws Exception
     */
    public static void createService(String tableName) throws Exception {

        String baseName = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName));
        String entityName = baseName + ENTITY_FILE_NAME_SUFFIX;
        String serviceName = baseName + SERVICE_FILE_NAME_SUFFIX;

        File file = FileUtil.createFile(getProjPath() + SERVICE_FILE_PATH, serviceName, ".java", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //导入部分
        StringBuilder sbImport = new StringBuilder();
        sbImport.append("import com.baomidou.mybatisplus.service.IService;\r\n");
        sbImport.append(String.format("import %s.%s;\r\n", ENTITY_PACKAGE_PATH.replace("/", "."), entityName));

        //类体部分
        StringBuilder sbBody = new StringBuilder();
        sbBody.append(String.format("public interface %s extends IService<%s> {\r\n\n", serviceName, entityName));
        sbBody.append("}");

        //写包名
        bw.write(String.format("package %s; \r\n\n", SERVICE_PACKAGE_PATH.replace("/", ".")));
        //写导入部分
        bw.write(sbImport.toString() + "\r\n");
        //写注释
        bw.write(genFileNotes() + "\r\n");
        //写类体
        bw.write(sbBody.toString());

        bw.flush();
        bw.close();

        System.out.println("创建service------>" + file.getAbsolutePath());
    }

    /**
     * 创建service实现类
     * @param tableName
     * @throws Exception
     */
    public static void createServiceImpl(String tableName) throws Exception {

        String baseName = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName));
        String entityName = baseName + ENTITY_FILE_NAME_SUFFIX;
        String serviceImplName = baseName + SERVICE_IMPL_FILE_NAME_SUFFIX;
        String serviceName = baseName + SERVICE_FILE_NAME_SUFFIX;
        String mapperName = baseName + MAPPER_JAVA_FILE_NAME_SUFFIX;

        File file = FileUtil.createFile(getProjPath() + SERVICE_IMPL_FILE_PATH, serviceImplName, ".java", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //导入部分
        StringBuilder sbImport = new StringBuilder();
        sbImport.append("import com.baomidou.mybatisplus.service.impl.ServiceImpl;\r\n");
        sbImport.append("import org.springframework.stereotype.Service;\r\n");
        sbImport.append(String.format("import %s.%s;\r\n", MAPPER_JAVA_PACKAGE_PATH.replace("/", "."), mapperName));
        sbImport.append(String.format("import %s.%s;\r\n", SERVICE_PACKAGE_PATH.replace("/", "."), serviceName));
        sbImport.append(String.format("import %s.%s;\r\n", ENTITY_PACKAGE_PATH.replace("/", "."), entityName));

        //类体部分
        StringBuilder sbBody = new StringBuilder();
        sbBody.append(String.format("@Service(\"%s\")\r\n", StringUtil.replaceFirstChar2Lower(serviceName)));
        sbBody.append(String.format("public class %s extends ServiceImpl<%s, %s> implements %s {\r\n\n", serviceImplName, mapperName, entityName, serviceName));
        sbBody.append("}");

        //写包名
        bw.write(String.format("package %s; \r\n\n", SERVICE_IMPL_PACKAGE_PATH.replace("/", ".")));
        //写导入部分
        bw.write(sbImport.toString() + "\r\n");
        //写注释
        bw.write(genFileNotes() + "\r\n");
        //写类体
        bw.write(sbBody.toString());

        bw.flush();
        bw.close();

        System.out.println("创建serviceImpl-->" + file.getAbsolutePath());
    }

    /**
     * 创建service实现类测试文件
     * @param tableName
     * @throws Exception
     */
    public static void createServiceTest(String tableName) throws Exception {

        String baseName = StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(tableName));
        String serviceName = baseName + SERVICE_FILE_NAME_SUFFIX;
        String serviceNameFl = StringUtil.replaceFirstChar2Lower(serviceName);
        String serviceTestName = baseName + SERVICE_TEST_FILE_NAME_SUFFIX;
        String entityName = baseName + ENTITY_FILE_NAME_SUFFIX;
        String entityNameWithPackage = String.format("%s.%s",ENTITY_PACKAGE_PATH.replace("/", "."), entityName);

        File file = FileUtil.createFile(getProjPath() + SERVICE_TEST_FILE_PATH, serviceTestName, ".java", true);

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        //导入部分
        StringBuilder sbImport = new StringBuilder();
        sbImport.append("import org.junit.Test;\r\n");
        sbImport.append("import org.junit.runner.RunWith;\r\n");
        sbImport.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
        sbImport.append("import org.springframework.boot.test.context.SpringBootTest;\r\n");
        sbImport.append("import org.springframework.test.context.junit4.SpringRunner;\r\n");
        sbImport.append("import com.alibaba.fastjson.JSONObject;\r\n");
        sbImport.append(String.format("import %s.%s;\r\n", SERVICE_PACKAGE_PATH.replace("/", "."), serviceName));
        sbImport.append(String.format("import %s;\r\n", entityNameWithPackage));

        //类体部分
        StringBuilder sbBody = new StringBuilder();
        sbBody.append("@RunWith(SpringRunner.class)\r\n");
        sbBody.append("@SpringBootTest\r\n");
        sbBody.append(String.format("public class %s {\r\n\n", serviceTestName));

        //声明
        sbBody.append("\t@Autowired\r\n");
        sbBody.append(String.format("\tprivate %s %s;\r\n\n\n", serviceName, serviceNameFl));

        //插入
        sbBody.append("\t@Test\r\n");
        sbBody.append("\tpublic void insertOrUpdate() {\r\n");
        sbBody.append(String.format("\t\t%s entity = new %s();\r\n", entityName, entityName));

        //导入排重
        Set<String> importSet = new HashSet<String>();

        /**
         //反射注入数据---此方法有生成java文件后，不能读取到class问题，改由读取数据库字段代替
         Class c = Class.forName(entityNameWithPackage);
         Method[] declaredMethods = c.getDeclaredMethods();
         for (Method m : declaredMethods) {
         if (m.getName().startsWith("set")) {
         String[] strings = genSetMethodParamStrAndImportStr(m.getParameterTypes()[0].getSimpleName());
         sbBody.append(String.format("\t\tentity.%s(%s);\r\n",m.getName(), strings[0]));
         if (strings[1] != null) {
         importSet.add(strings[1]);
         }
         }
         }
         */
        String idStr = null;
        Iterator<Map.Entry<String, String>> iterator = getTableFieldTypeMap(tableName).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String[] strings = genSetMethodParamStrAndImportStrFromDb(entry.getValue());
            sbBody.append(String.format("\t\tentity.set%s(%s);\r\n", StringUtil.replaceFirstChar2Upper(StringUtil.cvtUnderline2Hump(entry.getKey())), strings[0]));
            if (strings[1] != null) {
                importSet.add(strings[1]);
            }
            if (idStr == null) {
                idStr = strings[0];
            }
        }

        sbBody.append(String.format("\t\tboolean result = %s.insertOrUpdate(entity);\r\n", serviceNameFl));

        sbBody.append("\t\tSystem.out.println(result);\r\n");
        sbBody.append("\t}\r\n");
        sbBody.append("\r\n");

        //查询
        sbBody.append("\t@Test\r\n");
        sbBody.append("\tpublic void selectById() {\r\n");
        sbBody.append(String.format("\t\t%s entity = %s.selectById(%s);\r\n",entityName, serviceNameFl, idStr));
        sbBody.append("\t\tSystem.out.println(JSONObject.toJSONString(entity));\r\n");
        sbBody.append("\t}\r\n");
        sbBody.append("\r\n");

        sbBody.append("}");

        //补充导入语句
        for (String imp : importSet) {
            sbImport.append(imp + "\r\n");
        }

        //写包名
        bw.write(String.format("package %s; \r\n\n", SERVICE_TEST_PACKAGE_PATH.replace("/", ".")));
        //写导入部分
        bw.write(sbImport.toString() + "\r\n");
        //写注释
        bw.write(genFileNotes() + "\r\n");
        //写类体
        bw.write(sbBody.toString());

        bw.flush();
        bw.close();

        System.out.println("创建serviceTest-->" + file.getAbsolutePath());
    }

}
