package com.poll.exception;

import com.poll.ability.dto.RespMessage;
import com.poll.common.exception.ApiBizException;
import com.poll.common.exception.RollbackRedisDto;
import com.poll.common.util.MsgUtil;
import com.poll.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
    private RedisService redisService;

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public String defaultExceptionHandler(Exception e) {

        if (e instanceof InvocationTargetException) {

            ApiBizException ape = null;
            Throwable targetException = ((InvocationTargetException) e).getTargetException();
            if (targetException != null && targetException instanceof ApiBizException) {
                //回滚限制计数
                ape = (ApiBizException)targetException;
            } else {
                Throwable cause = targetException.getCause();
                if (cause != null && cause instanceof ApiBizException) {
                    //回滚限制计数
                    ape = (ApiBizException)cause;
                }
            }

            if (ape != null) {
                //回滚限制计数
                List<RollbackRedisDto> rollbackRedisDtoList = ape.getRollbackRedisDtoList();
                if (rollbackRedisDtoList != null) {
                    for (RollbackRedisDto rb : rollbackRedisDtoList) {
                        redisService.rollbackCount(rb);
                    }
                }
            }
        }
		String[] strings = MsgUtil.extractCodeMsg(e, null, null, -1);

		RespMessage respMsg = RespMessage.genError(strings[0], strings[1]);

		return respMsg.constrctRespJo().toJSONString();
	}

}
