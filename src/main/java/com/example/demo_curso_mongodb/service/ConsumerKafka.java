package com.example.demo_curso_mongodb.service;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

//@Component
//@Slf4j
public class ConsumerKafka {
	
	 /**
     * <p>
     * This method is used for listening to the configured topic.
     * </p>
     *
     * @param consumerRecord
     * @throws IOException
     */
    //@KafkaListener(topics = "${spring.kafka.topic.consumer}", autoStartup = "true")
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
    		
    		//log.info("Request in key :: " + consumerRecord.key());
    		//log.info("Request in value :: " + consumerRecord.value());
    }

}
