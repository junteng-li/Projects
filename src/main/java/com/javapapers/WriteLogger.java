package com.javapapers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 *
 * @Description:
 * @author 蔡彬彬
 * @date 2016年3月23日 下午1:58:45
 * @version V1.0
 */
@Component
public class WriteLogger {

    private static JedisCluster jc;

    private static  ShardedJedisPool shardedJedisPool;

    public static void main(String[] args) {
        System.out.println("111");
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(500);
//        poolConfig.setMaxIdle(1000 * 60);
//        poolConfig.setTestOnBorrow(true);
//		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(2);
//		JedisShardInfo A = new JedisShardInfo("115.182.104.139", 7002);
//		JedisShardInfo B = new JedisShardInfo("115.182.104.138", 7006);
//		shards.add(A );
//		shards.add(B);
//		shardedJedisPool = new ShardedJedisPool(poolConfig, shards , Hashing.MURMUR_HASH);

        Set<HostAndPort> clus = new HashSet<>();
        clus.add(new HostAndPort("115.182.104.139", 7000));
        clus.add(new HostAndPort("115.182.104.139", 7002));
        clus.add(new HostAndPort("115.182.104.139", 7003));
        clus.add(new HostAndPort("115.182.104.139", 7001));
        clus.add(new HostAndPort("115.182.104.139", 7004));
        clus.add(new HostAndPort("115.182.104.139", 7005));

		clus.add(new HostAndPort("115.182.104.138", 7006));
		clus.add(new HostAndPort("115.182.104.138", 7007));
		clus.add(new HostAndPort("115.182.104.138", 7008));
		clus.add(new HostAndPort("115.182.104.138", 7009));
		clus.add(new HostAndPort("115.182.104.138", 7010));
		clus.add(new HostAndPort("115.182.104.138", 7011));

        jc = new JedisCluster(clus);
        jc.expire("1233", 24*60*60);
        jc.set("111","好");
        System.out.println(jc.get("111") + jc.get("1233"));

//        Jedis jedis = new Jedis("115.182.104.139", 7001);
//
//        jedis.set("111","坏");
//
//        System.out.println(jedis.get("111") + jedis.get("1233"));
    }

    static{

    }
    /**
     * 记录日志
     */
    public void logAsync(String key,String value){
        CompletableFuture.runAsync(() -> writeLog(null,key,value));
    }
    private void writeLog(ShardedJedis jedis,String key, String value) {
        jedis.lpush(key, value);
    }
    /**
     *
     */
    public void logAsyncWithCount(String key,Map<String,Object> map){
        CompletableFuture.runAsync(() -> writeLogWithCount(key,map));
    }

    private  void writeLogWithCount(String key, Map<String, Object> map) {
//		ApplicationContext ac =  new ClassPathXmlApplicationContext("spring-mybatis.xml");
//		jedisCluster =  (JedisCluster)ac.getBean("jedisCluster");
//		ShardedJedis jedis = shardedJedisPool.getResource();
        try{
            jc.incr("1");
            String num = jc.get("1233");
            jc.expire("1233", 24*60*60);
            if(map == null)
                map = new HashMap<String, Object>();
            map.put("count", num);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
//			 shardedJedisPool.returnResource(jedis);
        }

    }
}
