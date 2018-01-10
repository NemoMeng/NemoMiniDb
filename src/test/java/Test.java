/*
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/10 12:43
 */

import bean.User;
import com.alibaba.fastjson.JSONObject;
import com.nemo.db.NemoMiniDbCore;
import com.nemo.db.bean.Data;
import com.nemo.db.bean.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nemo on 2018/1/10.
 */
public class Test {

    String tableName = "t_user";

    @org.junit.Test
    public void test(){
        NemoMiniDbCore core = new NemoMiniDbCore("test","f:/");

        //先删除
        core.dropTableWhenExits(tableName);

        //创建表
        core.createTable(tableName);

        //新增数据
        User user = new User();
        user.setAge(18);
        user.setName("Nemo");
        user.setSex("男");
        core.insert(user,tableName);

        User user1 = new User();
        user1.setAge(18);
        user1.setName("NemoMeng");
        user1.setSex("男");
        core.insert(user1,tableName);

        //得到表所有的数据
        List<Data> dataList = core.find(tableName);
        System.out.println(JSONObject.toJSONString(dataList));

        //根据名称查询
        Map<String,Object> map = new HashMap<>();
        map.put("name","Nemo");
        List<Data> findData = core.find(tableName, map);
        System.out.println(JSONObject.toJSON(findData));

        //删除数据
        long result = core.delete(map,tableName);
        System.out.println(result);

        //得到所有表
        List<Table> tables = core.getTables();
        System.out.println(JSONObject.toJSONString(tables));

        //更新数据
        user.setAge(18);
        user.setName("帅哥Nemo");
        user.setSex("男");
        map.put("name","NemoMeng");
        result = core.update(map,user,tableName);
        System.out.println(result);


        //得到所有表
         tables = core.getTables();
        System.out.println(JSONObject.toJSONString(tables));

        //批量插入
        List<User> users = new ArrayList<>();
        for(int i=0;i<1000000;i++){
            users.add(user);
        }
        core.insertBatchObj(users,tableName);


    }

}
