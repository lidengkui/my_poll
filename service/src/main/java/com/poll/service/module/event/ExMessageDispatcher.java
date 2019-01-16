package com.poll.service.module.event;

import net.engio.mbassy.bus.MessagePublication;
import net.engio.mbassy.dispatch.IHandlerInvocation;
import net.engio.mbassy.dispatch.MessageDispatcher;
import net.engio.mbassy.subscription.SubscriptionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lidengkui
 * @className ExMessageDispatcher
 * @description
 * @date 2019/1/16 0016
 */
public class ExMessageDispatcher extends MessageDispatcher {
    protected Logger log = LogManager.getLogger();


    private final IHandlerInvocation invocation;

    public ExMessageDispatcher(SubscriptionContext context, IHandlerInvocation invocation) {
        super(context, invocation);
        this.invocation = invocation;
    }

    @Override
    public void dispatch(
            final MessagePublication publication, final Object message, final Iterable listeners) {
        publication.markDispatched();
        for (Object listener : listeners) {
            log.info("listener:{} 收到消息:{}", listener.getClass().getSimpleName(), message);
            getInvocation().invoke(listener, message, publication);
        }
    }

    @Override
    public IHandlerInvocation getInvocation() {
        return invocation;
    }
}