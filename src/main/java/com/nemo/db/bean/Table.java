/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/10 16:11
 */
package com.nemo.db.bean;

import java.util.List;

/**
 * 单表数据Bean
 * Created by Nemo on 2018/1/10.
 */
public class Table {

    /**
     * 表名
     */
    private String name;

    /**
     * 数据列表
     */
    private List<Data> dataList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }
}
