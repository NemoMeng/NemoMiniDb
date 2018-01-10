/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/10 16:03
 */
package com.nemo.db.bean;

import java.util.List;
import java.util.Map;

/**
 * 数据库Bean
 * 数据结构设计：
 {"name":"test",
  "tables":{
     "t_user":{
         "a5bde8f1-5f42-454f-80b2-4c917485f3bd":{
         "sex":"男",
         "name":"帅哥Nemo",
         "age":18
        }
     }
  }
 }
 * Created by Nemo on 2018/1/10.
 */
public class Database {

    /**
     * 数据库名称
     */
    private String name;

    /**
     * 表列表从外往里依次是：表名 --》 数据列表 --》uuid --》数据列/数据详情
     */
    private Map<String,Map<String,Map<String,Object>>> tables;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Map<String, Map<String, Object>>> getTables() {
        return tables;
    }

    public void setTables(Map<String, Map<String, Map<String, Object>>> tables) {
        this.tables = tables;
    }
}
