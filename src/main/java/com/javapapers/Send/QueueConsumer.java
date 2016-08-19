package com.javapapers.Send;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Created by lijunteng on 16/7/22.
 */
public class QueueConsumer extends EndPoint implements Runnable, Consumer {

    public QueueConsumer(String endPointName) throws IOException, TimeoutException {
        super(endPointName);
    }

    public void run() {
        try {
            //start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true,this);
            System.out.println("QueueConsumer over!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag) {
        System.out.println("Consumer "+consumerTag +" registered");
    }

    /**
     * Called when new message is available.
     */
    public void handleDelivery(String consumerTag, Envelope env,
                               BasicProperties props, byte[] body) throws IOException {

        Map map = (HashMap) SerializationUtils.deserialize(body);
        System.out.println("Message Number "+ map.get("message number") + " received.");

        channel.basicAck(env.getDeliveryTag(), false);//下一个消息
        /**
         * 调用basicReject命令拒绝某一个消息时，可以设置一个requeue的属性，如果为true，
         * 则消息服务器会重传该消息给下一个订阅者；如果为false，则会直接删除该消息。
         * 当然，也可以通过basicAck，让消息服务器直接删除该消息并且不会重传。
         */
        channel.basicReject(env.getDeliveryTag(), false);
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}

}
