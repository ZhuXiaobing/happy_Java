package com.zhuxiaobing;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
//        testProducer01();
        testProducer02();
    }

    private static void testProducer02() {
        Properties props = getProducerProperties();
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord pr = new ProducerRecord<String, String>("first", Integer.toString(i), "zhuxiaobing" + Integer.toString(i * i * i));
            producer.send(pr, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        System.out.println(recordMetadata.topic() + "-->" + recordMetadata.partition() + "-->" + recordMetadata.offset());
                    } else {
                        System.out.println("error occured");
                    }
                }
            });
        }
        producer.close();
    }

    private static void testProducer01() {
        Properties props = getProducerProperties();
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord pr = new ProducerRecord<String, String>("first", Integer.toString(i), "zhuxiaobing" + Integer.toString(i * i * i));
            producer.send(pr);
        }
        producer.close();
    }

    private static Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.241.128:9092,192.168.241.129:9092,192.168.241.130:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // 自定义分区器。
        props.put("partitioner.class", "com.zhuxiaobing.MyCustomPartitioner");
        return props;
    }
}
