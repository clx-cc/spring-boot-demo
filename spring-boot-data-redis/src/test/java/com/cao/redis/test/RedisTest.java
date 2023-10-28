package com.cao.redis.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    //操作 String 类型的数据
    @Test
    public void testValueOperations(){
        //获取 StringOperations
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //向 redis 中存,会进行序列化:默认是 jdk 序列化,可以通过配置修改为 String序列化方式
        valueOperations.set("balance",10);
        //设置超时时间
        valueOperations.set("city","中国",10l, TimeUnit.SECONDS);
        //如果存在则不设置
        valueOperations.setIfAbsent("balance",1000);
        //获取 value
        Integer balance = (Integer) valueOperations.get("balance");
        System.out.println(balance);

    }
    //操作 Hash 类型的数据
    @Test
    public void testHashOperations(){
        HashOperations hashOperations = redisTemplate.opsForHash();
        //存
        hashOperations.put("user","id","001");
        hashOperations.put("user","name","ccc");
        hashOperations.put("user","age","20");
        //获取某个字段
        String id = (String) hashOperations.get("user", "id");
        String name = (String) hashOperations.get("user", "name");
        String age = (String) hashOperations.get("user", "age");
        System.out.println("id:" + id + ",name:" + name + ",age:" + age);
        //获取所有字段
        Set user = hashOperations.keys("user");
        for (Object o : user) {
            System.out.println(o);
        }
        //获取所有的value
        List values = hashOperations.values("user");
        for (Object value : values) {
            System.out.println(value);
        }
        //判断某个 key 是否存在
        System.out.println(hashOperations.hasKey("user", "id"));
    }
    //操作 List 类型的数据:有序
    @Test
    public void testListOperations(){
        ListOperations listOperations = redisTemplate.opsForList();
        //向 list 列表中存
        //存一个
        listOperations.leftPushAll("mylist",1);
        //存多个
        listOperations.leftPushAll("mylist",2,3,4,5);
        //取值
        List mylist = listOperations.range("mylist", 0, -1);
        for (Object item : mylist) {
            System.out.print(item + "\t");
        }
        //获取队列中元素个数
        Long size = listOperations.size("mylist");
        //出队列
        for (int i = 0; i < size; i++) {
            System.out.println(listOperations.rightPop("mylist"));
        }

    }
    //操作 Set 类型的数据:无序不重复
    @Test
    public void testSetOperations(){
        SetOperations setOperations = redisTemplate.opsForSet();
        //存
        setOperations.add("myset",1,2,3,4,5,1,2,3,4,5);
        //取:1	3	4	2	5	无序不重复
        Set myset = setOperations.members("myset");
        for (Object ele : myset) {
            System.out.printf(ele + "\t");
        }
        //删除成员
        setOperations.remove("myset", 1, 2);
        System.out.println("\n删除后:");
        myset = setOperations.members("myset");
        for (Object ele : myset) {
            System.out.printf(ele + "\t");
        }

    }
    //操作 zset ,有序不重复集合,如果重复,后者覆盖前者
    @Test
    public void testZSetOperations(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        //存值
        zSetOperations.add("myZset","ccc",100.0);
        zSetOperations.add("myZset","ccc",70.0);
        zSetOperations.add("myZset","lll",90.0);
        zSetOperations.add("myZset","xxx",80.0);
        //取值:ccc	xxx	lll
        Set myZset = zSetOperations.range("myZset", 0, -1);
        for (Object ele : myZset) {
            System.out.print(ele + "\t");
        }
        //增加分数:xxx	lll	ccc
        zSetOperations.incrementScore("myZset","ccc",100.0);
        System.out.println(zSetOperations.score("myZset", "ccc"));
        myZset = zSetOperations.range("myZset", 0, -1);
        for (Object ele : myZset) {
            System.out.print(ele + "\t");
        }
        //删除成员:xxx
        zSetOperations.remove("myZset","ccc","lll");
        System.out.println("\n删除成员后:");
        myZset = zSetOperations.range("myZset", 0, -1);
        for (Object ele : myZset) {
            System.out.print(ele + "\t");
        }
    }
    //key 的操作命令:直接通过 redisTemplate 进行操作
    @Test
    public void testCommon(){
        //获取 redis 中的所有 key
        Set keys = redisTemplate.keys("*");
        System.out.println("所有的 key:");
        for (Object key : keys) {
            System.out.print(key + "\t");
        }
        //判断某个 key 是否存在
        System.out.println("\n判断某个key是否存在:");
        System.out.println(redisTemplate.hasKey("mylist"));
        //删除指定 key
        System.out.println("删除指定的key:");
        System.out.println(redisTemplate.delete("mylist"));
        //获取指定 key 的数据类型
        System.out.println("获取key的数据类型:");
        System.out.println(redisTemplate.type("myZset"));
    }
}
