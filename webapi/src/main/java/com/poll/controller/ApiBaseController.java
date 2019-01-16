package com.poll.controller;

import com.alibaba.fastjson.JSONObject;
import com.poll.ability.dto.ReqMessage;
import com.poll.ability.dto.RespMessage;
import com.poll.common.MsgCode;
import com.poll.service.module.AccessModuleService;
import com.poll.service.util.TransferUtil;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class ApiBaseController {

	@Getter
	protected Logger log = LogManager.getLogger();

	@Autowired
	protected AccessModuleService accessModuleService;

	public String getServiceName(String serviceName) {
		if (serviceName == null) {
			return AccessModuleService.constructServiceName(this);
		}
		return AccessModuleService.constructServiceName(serviceName);
	}


	/*
	 * 用该controller的class name 拼接对应servcie
	 * 例如：
	 *     ApiBaseController对应service为 apiBaseService
	 */
	public ApiBaseController() {
	}

	/**
	 * 此接口在har加解密框架之内
	 * @param version
	 * @param serviceCode
	 * @param method
	 * @param reqBodyParam
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{version}/{serviceCode}/{method}")
	@ResponseBody
	public String invoke(@PathVariable String version, @PathVariable String serviceCode, @PathVariable String method, @RequestBody(required = false) String reqBodyParam, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String requestUri = request.getRequestURI();
        getLog().info(String.format("%s receive data->%s", requestUri, reqBodyParam));

        String serviceName = getServiceName(serviceCode);
        //业务对象名为空直接返回
        if (serviceName == null) {
            return RespMessage.genError(MsgCode.C00000020.code, MsgCode.C00000020.msg).constrctRespJo().toJSONString();
        }

        //入口参数
        ReqMessage reqMsg = accessModuleService.parseReqParam(reqBodyParam);

        JSONObject reqJo = reqMsg.getReqJo();
        getLog().debug(String.format("%s receive data jo plain->%s", requestUri, reqJo.toJSONString()));

        //请求入口处传入时间戳标记
        TransferUtil.setDate2Json(new Date(), reqJo);

        //执行业务
        RespMessage respMsg = accessModuleService.invokeBusiness(serviceName, method, requestUri, version, reqMsg, null, request, response);

        if (respMsg == null) {
            return null;
        }
        getLog().info(String.format("%s response data->%s", requestUri, respMsg.constrctRespJo().toJSONString()));

        //适配返回
        return accessModuleService.adaptRespParam(reqMsg, respMsg).constrctRespJo().toJSONString();
	}

    @RequestMapping("/{version}/upld/{serviceCode}/{method}")
    @ResponseBody
    public String upld(@PathVariable String version, @PathVariable String serviceCode, @PathVariable String method, @RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println(request.getParameter("groupId"));
	    if (file == null) {
            return RespMessage.genError(MsgCode.C00000011.code, "文件不存在").constrctRespJo().toJSONString();
        }

        String requestUri = request.getRequestURI();
        getLog().info(String.format("%s receive data->%s", requestUri, file.getName()));

        String serviceName = getServiceName(serviceCode);
        //业务对象名为空直接返回
        if (serviceName == null) {
            return RespMessage.genError(MsgCode.C00000020.code, MsgCode.C00000020.msg).constrctRespJo().toJSONString();
        }

        //入口参数
        ReqMessage reqMsg = accessModuleService.parseReqParam(null);

        JSONObject reqJo = reqMsg.getReqJo();
        getLog().debug(String.format("%s receive data jo plain->%s", requestUri, reqJo.toJSONString()));

        //请求入口处传入时间戳标记
        TransferUtil.setDate2Json(new Date(), reqJo);

        //执行业务
        RespMessage respMsg = accessModuleService.invokeBusiness(serviceName, method, requestUri, version, reqMsg, file, request, response);

        if (respMsg == null) {
            return null;
        }
        getLog().info(String.format("%s response data->%s", requestUri, respMsg.constrctRespJo().toJSONString()));

        //适配返回
        return accessModuleService.adaptRespParam(reqMsg, respMsg).constrctRespJo().toJSONString();
    }




	@InitBinder
	public void initBinder(WebDataBinder binder) {    
		
		// 对输入字符串参数转义
//		binder.registerCustomEditor(String.class, new StringEscapeEditor(true, true, true));
	}
}
