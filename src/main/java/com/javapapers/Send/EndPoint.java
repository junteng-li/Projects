package com.javapapers.Send;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created by lijunteng on 16/7/22.
 * 抽象类
 */
public abstract class EndPoint {

    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public EndPoint(String endpointName) throws IOException, TimeoutException {
        this.endPointName = endpointName;

        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //hostname of your rabbitmq server
        factory.setHost("192.168.2.89");
        //factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");

        //getting a connection
        connection = factory.newConnection();

        //creating a channel
        channel = connection.createChannel();

        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        /**
         * 持久化：
            Rabbit MQ默认是不持久队列、Exchange、Binding以及队列中的消息的，
            这意味着一旦消息服务器重启，所有已声明的队列，Exchange，Binding以及队列中的消息都会丢失。
            通过设置Exchange和MessageQueue的durable属性为true，可以使得队列和Exchange持久化，
            但是这还不能使得队列中的消息持久化，这需要生产者在发送消息的时候，
            将delivery mode设置为2，只有这3个全部设置完成后，才能保证服务器重启不会对现有的队列造成影响。
            这里需要注意的是，只有durable为true的Exchange和durable为ture的Queues才能绑定，否则在绑定时，
            RabbitMQ都会抛错的。持久化会对RabbitMQ的性能造成比较大的影响，可能会下降10倍不止。
         */
        channel.queueDeclare(endpointName, false, false, false, null);
    }


    /**
     * 关闭channel和connection。并非必须，因为隐含是自动调用的。
     * @throws IOException
     */
    public void close() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}
