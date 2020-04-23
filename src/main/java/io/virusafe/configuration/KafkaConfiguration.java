package io.virusafe.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(value = "encryption.provider.enable", havingValue = "true")
@EnableKafka
public class KafkaConfiguration {

    /**
     * Provide a Kafka NewTopic bean to be used for questionnaire messages,
     * configured with name, partitions and replicas as defined in configuration.
     *
     * @param questionnaireTopicName       the configured questionnaire topic name
     * @param questionnaireTopicPartitions the configured questionnaire topic partitions
     * @param questionnaireTopicReplicas   the configured questionnaire topic replicas
     * @return the questionnaire NewTopic bean
     */
    @Bean
    public NewTopic questionnaireTopic(
            @Value("${spring.kafka.properties.questionnaireTopicName}") final String questionnaireTopicName,
            @Value("${spring.kafka.properties.questionnaireTopicPartitions}") final String questionnaireTopicPartitions,
            @Value("${spring.kafka.properties.questionnaireTopicReplicas}") final String questionnaireTopicReplicas) {
        return TopicBuilder.name(questionnaireTopicName)
                .partitions(Integer.parseInt(questionnaireTopicPartitions))
                .replicas(Integer.parseInt(questionnaireTopicReplicas))
                .build();
    }
}
