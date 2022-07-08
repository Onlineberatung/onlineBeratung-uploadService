package de.caritas.cob.uploadservice.testconfig;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import de.caritas.cob.uploadservice.statisticsservice.generated.web.model.EventType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RabbitMqTestConfig {

  public static final String STATISTICS_EXCHANGE_NAME = "statistics.topic";
  private static final String QUEUE_PREFIX = "statistics.";
  public static final String QUEUE_NAME_CREATE_MESSAGE = QUEUE_PREFIX + EventType.CREATE_MESSAGE;

  @Bean ConnectionFactory connectionFactory() {
    return new CachingConnectionFactory(new MockConnectionFactory());
  }

  @Bean
  public Declarables topicBindings() {
    Queue assignSessionStatisticEventQueue = new Queue(QUEUE_NAME_CREATE_MESSAGE, true);

    TopicExchange topicExchange = new TopicExchange(STATISTICS_EXCHANGE_NAME, true, false);

    return new Declarables(
        assignSessionStatisticEventQueue,
        topicExchange,
        BindingBuilder
            .bind(assignSessionStatisticEventQueue)
            .to(topicExchange).with(
                EventType.CREATE_MESSAGE));
  }
}
