package com.poll.common.util;

import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import org.springframework.dao.DuplicateKeyException;

import java.lang.reflect.InvocationTargetException;


public class MsgUtil {

	public static final int ERROR_MSG_MAX_LEN = 50;

	/**
	 * 
	 * @param e
	 * @param msgPrefix4DupKey	         重复索引前缀
	 * @param msgSuffixAll		   	所有异常后缀
	 * @param splitDuplctKeyIndex  当异常为唯一索引重复异常时，取重复值    例：重复值为'201788-24-18030540425'，若小于0，则全返回，否则，返回根据-分割的数组的splitDuplctKeyIndex下标返回
	 * @return
	 */
	public static String extractMsg(Throwable e, String msgPrefix4DupKey, String msgSuffixAll, int splitDuplctKeyIndex) {
		
		return extractCodeMsg(e, msgPrefix4DupKey, msgSuffixAll, splitDuplctKeyIndex)[1];
	}
	
	public static String[] extractCodeMsg(Throwable e, String msgPrefix4DupKey, String msgSuffixAll, int splitDuplctKeyIndex) {
		
		String code = MsgCode.C00000001.code;
		String msg = MsgCode.C00000001.msg;
		
		if (e != null) {

			if (e instanceof ApiBizException) {

			} else if (e instanceof InvocationTargetException) {

                Throwable cause = ((InvocationTargetException) e).getTargetException().getCause();
                if (cause != null && cause instanceof ApiBizException) {
                    e = (ApiBizException) cause;
                } else {
				    e = ((InvocationTargetException) e).getTargetException();
                }
            }

			if (e instanceof DuplicateKeyException) {

				msg = extractDuplicateMsg(e, msgPrefix4DupKey, splitDuplctKeyIndex, msg);
						
			} else if (e instanceof ApiBizException) {
				
				ApiBizException ape = (ApiBizException)e;
				code = ape.getErrorCode();
				msg = ape.getMessage();
				
			} else if (e.getMessage() != null) {

				msg = e.getMessage();

			} else if (e.getCause() != null) {

				msg = e.getCause().getMessage();
			}
		}
		
		msg = StringUtil.trimStr(msg);
		if (msg.equals(Constants.STR_BLANK)) {
			msg = MsgCode.C00000001.msg;
		} else if (msg.startsWith("No handler found")) {
		    code = MsgCode.C00000404.code;
		    msg = MsgCode.C00000404.msg;
        }

		msg += StringUtil.trimStr(msgSuffixAll);
		
		msg = handleErrorMsg(msg);
		
		return new String[] {code, msg};
	}

	public static String handleErrorMsg(String errorMsg) {
		if (errorMsg == null || errorMsg.length() > ERROR_MSG_MAX_LEN) {
			errorMsg = MsgCode.C00000001.msg;
		}
		return errorMsg;
	}

	/**
	 * @param e
	 * @param msgPrefix4DupKey
	 * @param splitDuplctKeyIndex
	 * @param msg
	 * @return
	 */
	private static String extractDuplicateMsg(Throwable e, String msgPrefix4DupKey, int splitDuplctKeyIndex, String msg) {
		
		if (e.getCause() != null) {
			msg = e.getCause().getMessage();
		}
		
		int fromIndex = msg.indexOf("'");
		if (fromIndex != -1 && msg.length() > 1) {

			msg = msg.substring(fromIndex + 1);
			
			int toIndex = msg.indexOf("'");
			if (toIndex != -1) {
				msg = msg.substring(0, toIndex);
				
				if (splitDuplctKeyIndex >= 0) {
					String[] split = msg.split("-");
					if (splitDuplctKeyIndex >= split.length) {
						splitDuplctKeyIndex = split.length - 1;
					}
					msg = split[splitDuplctKeyIndex];
				}
			}
		}
		
		return StringUtil.trimStr(msgPrefix4DupKey) + msg + "已经存在";
	}
}
