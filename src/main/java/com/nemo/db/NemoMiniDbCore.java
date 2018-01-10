/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/10 16:03
 */
package com.nemo.db;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nemo.db.bean.Data;
import com.nemo.db.bean.Database;
import com.nemo.db.bean.Table;
import com.nemo.util.BeanUtils;
import com.nemo.util.ListUtils;
import com.nemo.util.MapUtils;

import java.io.*;
import java.util.*;

/**
 * 数据库核心包
 * 这里主要封装一些基本的增删改查操作
 * Created by Nemo on 2018/1/10.
 */
public class NemoMiniDbCore {

    private String dbName;
    private File dbFile;
    private Database database;

    /**
     * 得到实例化的时候初始化数据库
     * @param dbName
     * @param path
     */
    public NemoMiniDbCore(String dbName,String path){
        this.dbName = dbName;

        String basePath = path + "/" + dbName + ".json";
        dbFile = new File(basePath);
        if(dbFile.exists()){
            //已存在，拿到这个文件的流即可
            loadExistsDb();
        }else{
            //不存在，创建这个数据库文件
            init();
        }
    }

    /**
     * 创建一个表
     * @param name
     */
    public void createTable(String name){

        Table table = getTable(name);
        if(table!=null){
            throw new RuntimeException("抱歉，需要创建的表已经存在");
        }

        database.getTables().put(name,new HashMap<>());
        saveData();
    }

    /**
     * 删除一个表
     * @param name
     */
    public void dropTable(String name){
        Table table = getTable(name);
        if (table==null) {
            throw new RuntimeException("抱歉，需要删除的表不存在");
        }
        database.getTables().remove(name);
        saveData();
    }

    /**
     * 删除表：当存在的时候删除，不存在则不提示直接返回
     * @param name
     */
    public void dropTableWhenExits(String name){
        Table table = getTable(name);
        if (table==null) {
            return;
        }
        database.getTables().remove(name);
        saveData();
    }

    /**
     * 插入一条数据
     * @param data
     * @param tableName
     */
    public void insert(Map<String,Object> data, String tableName){
        Table table = getTable(tableName);

        if(table==null){
            throw new RuntimeException("表["+tableName+"]不存在");
        }

        database.getTables().get(tableName).put(UUID.randomUUID().toString(),data);

        saveData();
    }

    /**
     * 插入一条数据
     * @param data
     * @param tableName
     */
    public void insert(Object data,String tableName){
        Map<String,Object> map = BeanUtils.transBean2Map(data);
        insert(map,tableName);
    }

    /**
     * 批量插入数据
     * @param datas
     * @param tableName
     */
    public void insertBatch(List<Map<String,Object>> datas,String tableName){

        Table table = getTable(tableName);

        if(table==null){
            throw new RuntimeException("表["+tableName+"]不存在");
        }

        if(ListUtils.isNotEmpty(datas)) {
            for (Map<String, Object> data : datas) {
                database.getTables().get(tableName).put(UUID.randomUUID().toString(), data);
            }
            saveData();
        }
    }

    /**
     * 批量插入数据
     * @param datas
     * @param tableName
     */
    public void insertBatchObj(List datas,String tableName){
        Table table = getTable(tableName);

        if(table==null){
            throw new RuntimeException("表["+tableName+"]不存在");
        }

        if(ListUtils.isNotEmpty(datas)) {
            for (Object data : datas) {
                database.getTables().get(tableName).put(UUID.randomUUID().toString(), BeanUtils.transBean2Map(data));
            }
            saveData();
        }
    }

    /**
     * 删除数据
     * @param queryMap
     * @param tableName
     * @return
     */
    public long delete(Map<String,Object> queryMap, String tableName){
        List<Data> findedDatas = find(tableName, queryMap);
        Map<String, Map<String, Object>> maps = database.getTables().get(tableName);

        long result = 0;
        for(Data data : findedDatas){
            maps.remove(data.getUuid());
            result ++;
        }
        saveData();

        return result;
    }

    /**
     * 数据更新
     * @param queryMap
     * @param newData
     * @param tableName
     * @return
     */
    public long update(Map<String,Object> queryMap,Map<String,Object> newData, String tableName){
        List<Data> findedDatas = find(tableName, queryMap);
        Map<String, Map<String, Object>> maps = database.getTables().get(tableName);

        long result = 0;
        for(Data data : findedDatas){
            maps.put(data.getUuid(),newData);
            result ++;
        }
        saveData();

        return result;
    }

    /**
     * 数据更新
     * @param queryMap
     * @param newData
     * @param tableName
     * @return
     */
    public long update(Map<String,Object> queryMap,Object newData, String tableName){
        Map<String,Object> map = BeanUtils.transBean2Map(newData);
        return update(queryMap,map,tableName);
    }

    /**
     * 得到数据库下的表列表
     * @return
     */
    public List<Table> getTables(){
        Map<String, Map<String, Map<String, Object>>> tableMap = database.getTables();
        List<Table> tables = new ArrayList<>();
        if(tableMap == null) {
            return tables;
        }
        for(String key : tableMap.keySet()){
            Map<String, Map<String, Object>> datas = tableMap.get(key);
            Table table = dealTable(key,datas);
            tables.add(table);
        }
        return tables;
    }

    /**
     * 得到一个表数据
     * @param name
     * @return
     */
    public Table getTable(String name){
        if(name == null){
            throw new RuntimeException("抱歉，表名称不能为空");
        }

        Map<String, Map<String, Map<String, Object>>> tableMap = database.getTables();
        Map<String, Map<String, Object>> datas = tableMap.get(name);
        if(datas == null){
            return null;
        }
        return dealTable(name,datas);
    }

    /**
     * 得到一张表所有的数据
     * @param tableName
     * @return
     */
    public List<Data> find(String tableName){
        Table table = getTable(tableName);
        if(table == null){
            throw new RuntimeException("找不到指定的表哦");
        }
        return table.getDataList();
    }

    /**
     * 遍历并查找与condition中所有属性匹配的对象。
     * @param tableName 表名
     * @param queryMap 查询条件对象
     * @return JSO对象列表
     */
    public List<Data> find(String tableName, Map<String,Object> queryMap){
        List<Data> result = new ArrayList<>();

        List<Data> allData = find(tableName);
        for(Data data : allData){
            if(this.match(data.getData(), queryMap)){
                result.add(data);
            }
        }

        return result;
    }

    /**
     * 判断第一个参数中的属性是否符合第二个参数的条件。
     * @param data 被判断数据对象
     * @param queryMap 条件集合对象
     * @return 当第二个参数中所有属性都可以在第一个参数中找到，并且属性值与之相等时，返回true，否则false。
     */
    private boolean match(Map<String,Object> data, Map<String,Object> queryMap){
        boolean result = true;
        for(Object key: queryMap.keySet()){
            if(data.containsKey(key) && data.get(key).equals(queryMap.get(key))){
                continue;
            }else{
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 封装一个table返回
     * @param name
     * @param datas
     * @return
     */
    private Table dealTable(String name,Map<String, Map<String, Object>> datas){
        Table table = new Table();
        List<Data> dataList = new ArrayList<>();
        if(MapUtils.isNotEmpty(datas)){
            for(String uuid : datas.keySet()){
                Data data = new Data();
                data.setUuid(uuid);
                data.setData(datas.get(uuid));
                dataList.add(data);
            }
        }
        table.setName(name);
        table.setDataList(dataList);
        return table;
    }

    /**
     * 初始化一个数据库
     */
    private void init(){

        database = new Database();
        database.setName(dbName);
        database.setTables(new HashMap<>());

        //保存
        saveData();
    }

    /**
     * 加载一个已经存在的数据库文件
     */
    private void loadExistsDb(){
        try {
            FileInputStream inputStream = new FileInputStream(dbFile);
            database = JSONObject.parseObject(inputStream, Database.class, Feature.values());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入数据
     */
    private void saveData(){
        try {
            FileOutputStream outputStream = new FileOutputStream(dbFile);
            JSONObject.writeJSONString(outputStream,database, SerializerFeature.PrettyFormat);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
