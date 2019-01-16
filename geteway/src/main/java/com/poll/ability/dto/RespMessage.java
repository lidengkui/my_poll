package com.poll.ability.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.poll.ability.ConstantsOfParamName;
import com.poll.ability.util.RSAUtil;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.AESUtil;
import com.poll.common.util.MsgUtil;
import com.poll.common.util.RandomUtil;
import com.poll.common.util.StringUtil;
import com.poll.entity.ThirdAccessEntity;
import lombok.Data;

/**
 * 返回报文辅助对象
 **/
@Data
public class RespMessage {
	
	private String appIdName = ConstantsOfParamName.APP_ID_CAPLZ;
	private String appId;
	private String partnerIdName = ConstantsOfParamName.PARTNER_ID_CAPLZ;
	private String partnerId;
	private String aliasName = ConstantsOfParamName.ALIAS_CAPLZ;
	private String alias;
	private String msgKeyName = ConstantsOfParamName.MSG_KEY_CAPLZ;
	private String msgKey;
	private String dataName = ConstantsOfParamName.DATA_CAPLZ;
	private String data;
	private String signName = ConstantsOfParamName.SIGN_CAPLZ;
	private String sign;
	private String versionName = ConstantsOfParamName.VERSION_CAPLZ;
	private String version;
	private String tokenName = ConstantsOfParamName.TOKEN_CAPLZ;
	private String token;
	private String reqTypeName = ConstantsOfParamName.REQ_TYPE_CAPLZ;
	private String reqType;
	private String devTypeName = ConstantsOfParamName.DEV_TYPE_CAPLZ;
	private String devType;
	private String respCodeName = ConstantsOfParamName.RESP_CODE_CAPLZ;
	private String respCode;
	private String respDescName = ConstantsOfParamName.RESP_DESC_CAPLZ;
	private String respDesc;
	
	//保存加密key明文
	private String msgKeyPlain;
	
	//返回成功标记
	private String successCode = MsgCode.C00000000.code;
	private String successMsg = MsgCode.C00000000.msg;
	
	private JSONObject transferData;

	private Object tempData;

	//接收请求第三方请求返回原始参数
	private String resultStr;

	//标记是否将data由str转为json类型
	private boolean cvtData2Jo = false;
	
	public RespMessage() {
	}
	
	public RespMessage(String resultStr) {
		this.resultStr = resultStr;
	}
	

	public JSONObject constrctRespJo(){
		
		JSONObject respJo = new JSONObject();

		respJo.put(respCodeName, respCode);
		respJo.put(respDescName, respDesc);
		respJo.put(appIdName, appId);
		respJo.put(partnerIdName, partnerId);
		respJo.put(msgKeyName, msgKey);
		if (cvtData2Jo) {
			try {
				respJo.put(dataName, JSONObject.parseObject(data));
			} catch (Exception e) {
				respJo.put(dataName, data);
			}
		} else {
			respJo.put(dataName, data);
		}
		respJo.put(signName, sign);
		respJo.put(versionName, version);
		respJo.put(tokenName, token);
		respJo.put(reqTypeName, reqType);
		respJo.put(devTypeName, devType);

		return respJo;
	}

	public JSONObject parseData2Jo() {
		
		if (this.data != null && !Constants.STR_BLANK.equals(this.data)) {
			try {
				return JSONObject.parseObject(this.data);
			} catch (Exception e) {
				e.printStackTrace();
//				throw new ApiBizException(MsgCode.C00000014.code, MsgCode.C00000014.msg);
			}
		}
		return new JSONObject();
	}
	
	public JSONArray parseData2JoArr() {
		
		if (this.data != null && !Constants.STR_BLANK.equals(this.data)) {
			try {
				return JSONObject.parseArray(this.data);
			} catch (Exception e) {
				e.printStackTrace();
//				throw new ApiBizException(MsgCode.C00000014.code, MsgCode.C00000014.msg);
			}
		}
		return new JSONArray();
	}


    /**
     * 将本系统返回信息对象转为rsa加密
     *
     * ios transformat需要特别处理AES/CBC/PKCS5Padding
     * android  默认为           AES/ECB/PKCS5Padding
     *
     * @param reqMsg
     * @return
     */
    public RespMessage cvt2RsaRespMsg(ReqMessage reqMsg) throws Exception {

        //第三方请求
        ThirdAccessEntity third = reqMsg.getThird();

        this.appId = third.getCode();
        this.partnerId = third.getPartnerCode();

        if (data == null) {
            data = Constants.STR_BLANK;
        }

        RSAUtil rsa = new RSAUtil();

        //签名标记
        String signFlag = third.getSignFlag();

        //生成aes加密key
        String msgKeyPlain = null;
        if (Constants.FLAG_YES_CHAR == signFlag.charAt(4)) {

            msgKeyPlain = reqMsg.getMsgKeyPlain();

        } else {

            msgKeyPlain = RandomUtil.genLetterNumStr(16);
            //加密msgkey
            rsa.loadPublicKeyStr(third.getPublicKeyAtThird());

            this.msgKey = rsa.encryptStr(msgKeyPlain);
        }

        try {
            //加密Data  ios transformat需要特别处理AES/CBC/PKCS5Padding
            String dataEncStr = AESUtil.getInstance(msgKeyPlain, third.getEncTransformat()).encrypt(data);

            this.sign = reqMsg.makeSign(rsa, third, data, dataEncStr);

            //设置data
            this.data = dataEncStr;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiBizException(MsgCode.C00000015.code, MsgCode.C00000015.msg);
        }

        return this;
    }
    /**
     * 根据条件决定是否制作签名
     * @param rsa
     * @param dataEncStr
     * @return 若制作签名 返回签名 否则返回空
     * @throws Exception
     */
    public String makeSign(RSAUtil rsa, ThirdAccessEntity third, String data, String dataEncStr) throws Exception {

        if (Constants.FLAG_YES_CHAR == third.getSignFlag().charAt(0)) {
            //制作签名
            rsa.loadPrivateKeyStr(third.getPrivateKey());

            if (Constants.FLAG_YES_CHAR == third.getSignFlag().charAt(1)) {
                return rsa.makeSign(dataEncStr);
            } else {
                return rsa.makeSign(data);
            }
        }
        return null;
    }

    /**
	 * 获取成功标记的返回对象
	 * @param respMsg
	 * @return
	 */
	public static RespMessage genSuccess(RespMessage respMsg) {
		
		if (respMsg == null) {
			respMsg = new RespMessage();
		}
		
		respMsg.setRespCode(MsgCode.C00000000.code);
		respMsg.setRespDesc(MsgCode.C00000000.msg);
		
		return respMsg;
	}
	public static RespMessage genSuccessWithData(JSONObject respJo) {
		
		RespMessage respMsg = new RespMessage();
		
		respMsg.setRespCode(MsgCode.C00000000.code);
		respMsg.setRespDesc(MsgCode.C00000000.msg);
		respMsg.setData(respJo == null ? Constants.STR_BLANK : respJo.toJSONString());
		
		return respMsg;
	}
	public static RespMessage genSuccess() {
		return genSuccess(null);
	}
	
	public static RespMessage genError(String errorCode, String errorMsg) {
		
		if (errorCode == null) {
			errorCode = MsgCode.C00000001.code;
		}

		errorMsg = MsgUtil.handleErrorMsg(errorMsg);
		
		RespMessage respMsg = new RespMessage();
		respMsg.setRespCode(errorCode);
		respMsg.setRespDesc(errorMsg);
		
		return respMsg;
	}
	
	public static RespMessage genError(Throwable e) {
		
		String[] codeMsg = MsgUtil.extractCodeMsg(e, null, null, -1);
		
		return genError(codeMsg[0], codeMsg[1]);
	}
	
	public static RespMessage genError() {
		
		return genError(MsgCode.C00000000.code, MsgCode.C00000000.msg);
	}
	
	public static RespMessage genError11() {
		
		return genError(MsgCode.C00000011.code, MsgCode.C00000011.msg);
	}
	
	public boolean isReqSuccess() {
		return MsgCode.C00000000.code.equals(this.respCode);
	}
}
