/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/10 16:11
 */
package com.nemo.db.bean;

import java.util.Map;

/**
 * 单行数据Bean
 * Created by Nemo on 2018/1/10.
 */
public class Data {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 数据集合
     */
    private Map<String,Object> data;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
