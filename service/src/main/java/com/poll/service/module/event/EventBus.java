package com.poll.service.module.event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.*;


/**
 * @author lidengkui
 * @className EventBus
 * @description
 * @date 2019/1/14 0014
 */
@Component
public class EventBus<T> extends MBassador<T> implements InitializingBean {
    protected Logger log = LogManager.getLogger();

    private TransactionTemplate transactionTemplate;

    @Autowired(required = false)
    private PlatformTransactionManager platformTransactionManager;

    public EventBus() {
        super();
    }

    public EventBus(IPublicationErrorHandler errorHandler) {
        super(errorHandler);
    }

    public EventBus(IBusConfiguration configuration) {
        super(configuration);
    }

    /**
     * 仅当当前事务提交成功后才发布消息,非事务环境直接发布消息
     * @param message
     */
    public void publishAfterTransactionCommitted(T message) {
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && transactionTemplate != null) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == TransactionSynchronization.STATUS_COMMITTED) {
                                transactionTemplate.execute(
                                        status1 -> {
                                            EventBus.this.publish(message);
                                            log.info("事务环境下发部消息结束，消息Object：{}",message);
                                            return null;
                                        });
                            }
                        }
                    });
        } else {
            EventBus.this.publish(message);
            log.info("非事务发部消息结束，消息Object：{}",message);
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        if (platformTransactionManager != null) {
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionTemplate = new TransactionTemplate(platformTransactionManager, defaultTransactionDefinition);
            log.info("spring事务管理器存在，publishAfterTransactionCommitted方法绑定到事务中!");
        } else {
            log.warn("spring事务管理器不存在，publishAfterTransactionCommitted方法不会绑定到事务中!");
        }
    }
}
