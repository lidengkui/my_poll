package com.poll.common.dto;

import com.poll.common.Constants;
import com.poll.common.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class WinRecordDto {

	private String i;	//用户id 微信号 openId 手机号
	private String a;	//头像
	private String n;	//昵称
	private String r;	//奖品名称
	
	private String s; //用户id 微信号 openId 手机号 或昵称 替换后的字符串
	
	private String initS() {
		
		String str = StringUtil.cvtUnicode2Normal(StringUtil.trimStr(this.n));
		
		if (str.equals(Constants.STR_BLANK)) {
			
			str = StringUtil.trimStr(this.i);
		}
		
		if (str.length() > 11) {
			str = str.substring(str.length() - 11);
		}
		
		s = StringUtil.replaceCharAutoMb4(str);
		
		return s;
	}
	
	/**
	 * 
	 */
	public WinRecordDto(String user, String avatar, String nickName, String giftPackName) {
		this.i = user;
		this.a = avatar;
		this.n = nickName;
		this.r = giftPackName;
		initS();
	}
}
