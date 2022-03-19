package es.unizar.tmdad.config;

import es.unizar.tmdad.listener.MessageListener;
import lombok.extern.slf4j.*;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.listener.*;
import org.springframework.amqp.rabbit.listener.adapter.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
@Slf4j
public class RabbitConfiguration {

    @Value("${chat.exchanges.input}")
    private String exchangeName;

    @Value("${spring.application.name}")
    private String appName;

    @Bean("rabbitQueue")
    String queueName(){
        return getQueueName(appName, exchangeName);
    }

    @Bean
    Queue queue(@Qualifier("rabbitQueue") String queueName) {
        return new Queue(queueName, false, true, false);
    }

    private String getQueueName(String appName, String exchangeName) {
        return appName + "." + exchangeName;
    }

    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(exchangeName, true, false);
    }

    @Bean
    Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter,
                                             @Qualifier("rabbitQueue") String queueName) {
        log.info("Binding to queue {}", queueName);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MessageListener receiver) {
        return new MessageListenerAdapter(receiver, "apply");
    }
}
