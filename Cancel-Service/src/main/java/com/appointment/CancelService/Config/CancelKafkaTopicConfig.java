package com.appointment.CancelService.Config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CancelKafkaTopicConfig {

    // Set up KafkaAdmin bean to configure the AdminClient
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(configs);
    }

    // Create the topic bean with 3 partitions
    @Bean
    public NewTopic topicName() {
        // Replace 'topicName' with your desired topic name
        // Use 3 for the number of partitions
        // Use 1 for the replication factor
        return new NewTopic("cancel", 3, (short) 1);
    }
}
