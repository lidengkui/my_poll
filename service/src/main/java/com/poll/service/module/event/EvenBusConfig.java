package com.poll.service.module.event;

import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author lidengkui
 * @className EvenBusConfig
 * @description
 * @date 2019/1/15 0015
 */

@Configuration
@EnableConfigurationProperties({EventProperties.class})
@ConditionalOnProperty(value = "event.enable", matchIfMissing = true)
public class EvenBusConfig {
    protected Logger log = LogManager.getLogger();

    @Bean
    public EventBus messageBus() {

         EventBus bus = new EventBus(
                        new BusConfiguration()
                                .addFeature(Feature.SyncPubSub.Default())
                                .addFeature( Feature.AsynchronousHandlerInvocation.Default())
                                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                                .addPublicationErrorHandler(
                                        error -> {
                                            Method handler = error.getHandler();
                                            String name = handler.getDeclaringClass().getSimpleName() + "#" + handler.getName();
                                            Throwable throwable = error.getCause();
                                            if (throwable instanceof InvocationTargetException) {
                                                throwable = ((InvocationTargetException) throwable).getTargetException();
                                            }
                                            log.error("调用方法:{} 失败，异常为：", name, throwable);
                                        })
                                .setProperty(IBusConfiguration.Properties.BusId, "global bus"));
        log.info("====================事件bus====================：" + bus);
        return bus;
    }
    @Configuration
    public static class EventHandlerConfig {
        protected Logger log = LogManager.getLogger();
        @Autowired
        private EventBus eventBus;
        @Autowired
        private  ApplicationContext context;
        @PostConstruct
        public void afterPropertiesSet() throws Exception {
            Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(EventHandler.class);
            for (Object o : beansWithAnnotation.values()) {
                eventBus.subscribe(o);
                log.info("注册事件处理器:{}", o.getClass().getName());
            }
        }

        @PreDestroy
        public void destroy() throws Exception {
            Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(EventHandler.class);
            for (Object o : beansWithAnnotation.values()) {
                eventBus.unsubscribe(o);
                log.info("销毁事件处理器:{}", o.getClass().getName());
            }
        }
    }
}
