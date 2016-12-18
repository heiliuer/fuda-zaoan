package com.heiliuer;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.heiliuer.fuda_zaoan");
        // 1: 数据库版本号
        // com.xxx.bean:自动生成的Bean对象会放到/java-gen/com/xxx/bean中

        schema.setDefaultJavaPackageDao("com.heiliuer.fuda_zaoan.dao");
        // DaoMaster.java、DaoSession.java、BeanDao.java会放到/java-gen/com/xxx/dao中

        // 上面这两个文件夹路径都可以自定义，也可以不设置


        addHomeTitle(schema);


        new DaoGenerator().generateAll(schema, "app/src/main/java");// 自动创建
    }


    private static void addHomeTitle(Schema schema) {

        Entity homeTitle = schema.addEntity("SignRecord");// 表名
        homeTitle.setTableName("SignRecord"); // 可以对表重命名

        homeTitle.addLongProperty("id").primaryKey().index().autoincrement();
        homeTitle.addLongProperty("createTime");
        homeTitle.addLongProperty("signTime");
        homeTitle.addBooleanProperty("success");
        homeTitle.addBooleanProperty("autoSigned");
        homeTitle.addStringProperty("signDate");
        homeTitle.addStringProperty("logJson");
    }


}
