package com.poll.ability.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.poll.ability.ConstantsOfParamName;
import com.poll.ability.util.RSAUtil;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.AESUtil;
import com.poll.common.util.RandomUtil;
import com.poll.common.util.StringUtil;
import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ext.ThirdAccessEntityExt;

import lombok.Data;

/**
 * 请求报文辅助对象
 **/
@Data
public class ReqMessage {

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
	
	//第三方接入配置对象
	private ThirdAccessEntityExt third;
	//接收第三方请求原始参数
	private String reqParamFromThird;
	//第三方请求json
	private JSONObject reqJo;
	//保存加密key明文
	private String msgKeyPlain;
	
	
	public ReqMessage() {
	}
	
	public ReqMessage(ThirdAccessEntityExt third, String data) {

		this.third = third;
		this.data = data;
	}
	
	public ReqMessage(ThirdAccessEntityExt third) {
		
		this.third = third;
	}

	public String parseAppIdFromReqJo() {
		return this.reqJo == null ? null : this.reqJo.getString(appIdName);
	}
	
	public ReqMessage(String reqParamFromThird) {
		
		this.reqParamFromThird = reqParamFromThird;
		try {
			this.reqJo = JSONObject.parseObject(this.reqParamFromThird);
		} catch (Exception e) {
		}
		if (this.reqJo == null) {
			this.reqJo = new JSONObject();
		}
	}
	/**
	 * 解析第三方rsa请求报文为reqmsg对象
	 *
	 * ios transformat需要特别处理AES/CBC/PKCS5Padding
	 * android  默认为           AES/ECB/PKCS5Padding
	 *
	 * @return
	 * @throws ApiBizException
	 */
	public ReqMessage parse2ReqMsg() throws ApiBizException {


		try {
			msgKey = reqJo.getString(msgKeyName);
			sign = reqJo.getString(signName);
			String reqData = reqJo.getString(dataName);

			RSAUtil rsaUtil = new RSAUtil();

			boolean verifySignPlain = verifySignEnc(rsaUtil, third, sign, reqData);

			// 加载本方私钥
			rsaUtil.loadPrivateKeyStr(third.getPrivateKey());

			// 得到报文加密key
			msgKeyPlain = rsaUtil.decryptStr(msgKey);

			//解密得到数据明文  ios transformat需要特别处理AES/CBC/PKCS5Padding
			data = AESUtil.getInstance(msgKeyPlain, third.getEncTransformat()).decrypt(reqData);

			//验签明文
			if (verifySignPlain && !rsaUtil.verifySign(data, sign)) {
				throw new ApiBizException(MsgCode.C00000012.code, MsgCode.C00000012.msg);
			}

			this.appId = third.getCode();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiBizException(MsgCode.C00000011.code, MsgCode.C00000011.msg);
		}

		this.reqJo = parseData2Jo();

		return this;
	}
	/**
	 * 解析标记决定是否验签密文
	 * @param rsa
	 * @param third
	 * @param sign
	 * @param dataEncStr
	 * @return 返回是否验签明文
	 * @throws Exception
	 */
	public boolean verifySignEnc(RSAUtil rsa, ThirdAccessEntity third, String sign, String dataEncStr) throws Exception {

		boolean verifySign = Constants.FLAG_YES_CHAR == third.getSignFlag().charAt(2);
		boolean verifySignEnc = false;

		if (verifySign) {
			verifySignEnc = Constants.FLAG_YES_CHAR == third.getSignFlag().charAt(3);

			//加载第三方公钥验签
			rsa.loadPublicKeyStr(third.getPublicKeyAtThird());

			dataEncStr = StringUtil.trimStr(dataEncStr);

			//验签密文
			if (verifySignEnc && (dataEncStr.equals(Constants.STR_BLANK) || !rsa.verifySign(dataEncStr, sign))) {
				throw new ApiBizException(MsgCode.C00000012.code, MsgCode.C00000012.msg);
			}
		}

		return verifySign && !verifySignEnc && !dataEncStr.equals(Constants.STR_BLANK);
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
	 * 将当前对象信息组装成json对象返回
	 * @return
	 */
	public JSONObject constrctReqJo() {

		JSONObject reqMsgJo = new JSONObject();

		reqMsgJo.put(appIdName, appId);
		reqMsgJo.put(partnerIdName, partnerId);
		reqMsgJo.put(aliasName, alias);
		reqMsgJo.put(msgKeyName, msgKey);
		reqMsgJo.put(dataName, data);
		reqMsgJo.put(signName, sign);
		reqMsgJo.put(versionName, version);
		reqMsgJo.put(tokenName, token);
		reqMsgJo.put(reqTypeName, reqType);
		reqMsgJo.put(devTypeName, devType);

		return reqMsgJo;
	}
	
	/**
	 * 将当前data字符串解析为json对象
	 * @return
	 */
	public JSONObject parseData2Jo() {

		if (this.data != null && !Constants.STR_BLANK.equals(this.data)) {
			try {
				return JSONObject.parseObject(this.data);
			} catch (Exception e) {
				e.printStackTrace();
//				throw new ApiBizException(MsgCode.C00000010.code, MsgCode.C00000010.msg);
			}
		}
		return new JSONObject();
	}

	/**
	 * 将当前data字符串解析为jsonArr
	 * @return
	 * @throws Exception
	 */
	public JSONArray parseData2JoArr() {
		
		if (this.data != null && !Constants.STR_BLANK.equals(this.data)) {
			try {
				return JSONObject.parseArray(this.data);
			} catch (Exception e) {
				e.printStackTrace();
//				throw new ApiBizException(MsgCode.C00000010.code, MsgCode.C00000010.msg);
			}
		}
		return new JSONArray();
	}
	


    /**
     * 构造一对公私钥且默认使用相同aes加密key处理data字段请求的ReqMessage对象
     * @param appId
     * @param rsaPublicKey
	 * @param signFlag			签名方式，可为空 默认为00001
     * @return
     */
	public static ReqMessage constructSinglePairRsa(String appId, String rsaPublicKey, String signFlag) {

        ReqMessage reqMsg = new ReqMessage();

        ThirdAccessEntityExt third = new ThirdAccessEntityExt();
        third.setCodeAtThird(appId);
        third.setPublicKeyAtThird(rsaPublicKey);
        if (signFlag == null) {
			third.setSignFlag("00001");
		} else {
			third.setSignFlag(signFlag);
		}
        reqMsg.setThird(third);

        return reqMsg;
    }

    /**
     * 构造两对公私钥且默认使用相同aes加密key处理data字段请求的ReqMessage对象
     * @param appId
     * @param rsaPublicKeyThird
     * @param rsaPrivateKeySelf
     * @param partnerCodeAtThird	可为空
     * @param signFlag			    签名方式，可为空 默认为10100
     * @return
     */
	public static ReqMessage constructTwoPairRsa(String appId, String rsaPublicKeyThird, String rsaPrivateKeySelf, String partnerCodeAtThird, String signFlag) {

        ReqMessage reqMsg = new ReqMessage();

        ThirdAccessEntityExt third = new ThirdAccessEntityExt();
        third.setCodeAtThird(appId);
        third.setPublicKeyAtThird(rsaPublicKeyThird);
        third.setPrivateKey(rsaPrivateKeySelf);
        third.setPartnerCodeAtThird(partnerCodeAtThird);
        if (signFlag == null) {
			third.setSignFlag("10100");
		} else {
			third.setSignFlag(signFlag);
		}
        reqMsg.setThird(third);

        return reqMsg;
    }
}
