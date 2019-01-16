package com.poll.service.business;

import com.alibaba.fastjson.JSONObject;
import com.poll.ability.dto.RespMessage;
import com.poll.service.annotation.TokenLess;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
public class TestService_V1 {



    public RespMessage test(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return RespMessage.genSuccessWithData(reqJo);
    }

    @TokenLess
    public RespMessage testNoToken(JSONObject reqJo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return RespMessage.genSuccessWithData(reqJo);
    }

}
