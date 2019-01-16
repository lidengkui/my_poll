package com.poll.service.module.event;

import net.engio.mbassy.dispatch.EnvelopedMessageDispatcher;
import net.engio.mbassy.dispatch.FilteredMessageDispatcher;
import net.engio.mbassy.dispatch.IHandlerInvocation;
import net.engio.mbassy.dispatch.IMessageDispatcher;
import net.engio.mbassy.subscription.SubscriptionContext;
import net.engio.mbassy.subscription.SubscriptionFactory;

/**
 * @author lidengkui
 * @className ExSubscriptionFactory
 * @description
 * @date 2019/1/16 0016
 */
public class ExSubscriptionFactory extends SubscriptionFactory {
    protected IMessageDispatcher buildDispatcher(
            SubscriptionContext context, IHandlerInvocation invocation) {
        IMessageDispatcher dispatcher = new ExMessageDispatcher(context, invocation);
        if (context.getHandler().isEnveloped()) {
            dispatcher = new EnvelopedMessageDispatcher(dispatcher);
        }
        if (context.getHandler().isFiltered()) {
            dispatcher = new FilteredMessageDispatcher(dispatcher);
        }
        return dispatcher;
    }
}
