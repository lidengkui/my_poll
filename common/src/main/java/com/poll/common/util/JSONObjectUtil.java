package com.poll.common.util;

import com.alibaba.fastjson.JSONObject;
import com.poll.common.Constants;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



public class JSONObjectUtil {
	
	/**
	 * copy dataJson 对象的属性值给jo对象
	 * 从dataJson中取相应key的值value，并以同名key为jo对象赋值
	 * @param jo					待设置的json对象		 可为空，为空则新创建一个json对象
	 * @param dataJson				取值的对象				 可为空，为空则将赋值为null
	 * @param ignoreNumPre			设置目标json值时忽略keys中的key的前导字符个数       例如  key=ha_age_avg	ignoreNumPre=3  则设置目标对象key时转为 age_avg
	 * @param ignoreNumAft			设置目标json值时忽略keys中的key的后缀字符个数       例如  key=age_avg_ha	ignoreNumAft=3  则设置目标对象key时转为 age_avg
	 * @param transUnderlineHump	key是否进行下划线、驼峰互转  		 age_avg转为ageAvg,ageAvg转为age_avg 
	 * @param keys					需要为jo设置的属性名称
	 * @return
	 */
	public static JSONObject setJoAttrsByDataJson(JSONObject jo, JSONObject dataJson, int ignoreNumPre, int ignoreNumAft, boolean transUnderlineHump, String... keys) {
		
		if(jo == null) {
			jo = new JSONObject();
		}
		
		for(String key : keys) {
			key = StringUtil.trimStr(key);
			if(key.equals(Constants.STR_BLANK)) {
				continue;
			}
			
			String targetKey = key;
			targetKey = key.substring(ignoreNumPre, key.length() - ignoreNumAft);
			if (transUnderlineHump) {
				if (targetKey.contains(Constants.STR_UNDERLINE)) {
					targetKey = StringUtil.cvtUnderline2Hump(targetKey);
				} else {
					targetKey = StringUtil.cvtHump2Underline(targetKey);
				}
			}
			
			if (dataJson == null) {
				jo.put(key, Constants.STR_BLANK);
			} else {
				//此处为空时，赋值为空字符串，若赋值为null，则jo中不会出现该key的键值对
				setJoAttrByDataJson(jo, targetKey, dataJson, key);
			}
		}
		
		return jo;
	}
	
	/**
	 * copy dataJson 对象的属性值给jo对象
	 * 从dataJson中取相应key的值value，并以同名key为jo对象赋值
	 * 
	 * @param jo			待设置的json对象		 可为空，为空则新创建一个json对象
	 * @param dataJson		取值的对象				 可为空，为空则将赋值为null
	 * @param keys			需要为jo设置的属性名称
	 * @return
	 */
	public static JSONObject setJoAttrsByDataJson(JSONObject jo, JSONObject dataJson, String... keys) {
		
		return setJoAttrsByDataJson(jo, dataJson, 0, 0, false, keys);
	}
	
	/**
	 * copy dataObj 对象的属性值给jo对象
	 * 从dataObj对象中取相应attrName的值attrValue，并以同名attrName为jo对象赋值
	 * 
	 * @param jo
	 * @param dataObj
	 * @param dateFmtStr				若返回值为date类型，则按传入值进行格式化输出,若为空，则返回时间戳
	 * @param ignoreNumPre				设置目标json值时忽略keys中的key的前导字符个数       例如  key=ha_age_avg	ignoreNumPre=3  则设置目标对象key时转为 age_avg
	 * @param ignoreNumAft				设置目标json值时忽略keys中的key的后缀字符个数       例如  key=age_avg_ha	ignoreNumAft=3  则设置目标对象key时转为 age_avg
	 * @param transUnderlineHump		key是否进行下划线、驼峰互转  		 age_avg转为ageAvg,ageAvg转为age_avg 
	 * @param attrNames
	 * @return
	 */
	public static JSONObject setJoAttrsByDataObj(JSONObject jo, Object dataObj, String dateFmtStr, int ignoreNumPre, int ignoreNumAft, boolean transUnderlineHump, String... attrNames) {
		
		if(jo == null) {
			jo = new JSONObject();
		}
		
		for (String attrName : attrNames) {
			attrName = StringUtil.trimStr(attrName);
			if(attrName.equals(Constants.STR_BLANK)) {
				continue;
			}
			
			String targetAttrName = attrName;
			targetAttrName = attrName.substring(ignoreNumPre, attrName.length() - ignoreNumAft);
			if (transUnderlineHump) {
				targetAttrName = StringUtil.cvtHump2Underline(targetAttrName);
			}
			
			if (dataObj == null) {
				jo.put(targetAttrName, Constants.STR_BLANK);
			} else {
				//拼对应属性的get方法
				String getFunName = "get" + StringUtil.replaceFirstChar2Upper(attrName);
				Object value = null;
				try {
					//反射取值
					Method getMethod = dataObj.getClass().getMethod(getFunName);
					
					Class<?> returnType = getMethod.getReturnType();
					if (returnType.getName().equals(Date.class.getName())) {
						
						dateFmtStr = StringUtil.trimStr(dateFmtStr);
						if(dateFmtStr.equals(Constants.STR_BLANK)) {
							value = String.valueOf(((Date)getMethod.invoke(dataObj)).getTime());
						} else {
							value = DateUtil.convertDate2Str((Date)getMethod.invoke(dataObj), dateFmtStr);
						}
					} else if (returnType.getName().equals(java.sql.Date.class.getName())) {
						dateFmtStr = StringUtil.trimStr(dateFmtStr);
						if(dateFmtStr.equals(Constants.STR_BLANK)) {
							value = String.valueOf(((java.sql.Date)getMethod.invoke(dataObj)).getTime());
						} else {
							Date date = new Date(((java.sql.Date)getMethod.invoke(dataObj)).getTime());
							value = DateUtil.convertDate2Str(date, dateFmtStr);
						}
					} else if (returnType.getName().equals(java.sql.Timestamp.class.getName())) {
						dateFmtStr = StringUtil.trimStr(dateFmtStr);
						if(dateFmtStr.equals(Constants.STR_BLANK)) {
							value = String.valueOf(((java.sql.Timestamp)getMethod.invoke(dataObj)).getTime());
						} else {
							Date date = new Date(((java.sql.Timestamp)getMethod.invoke(dataObj)).getTime());
							value = DateUtil.convertDate2Str(date, dateFmtStr);
						}
					} else {
						value = getMethod.invoke(dataObj);
					}
					
				} catch (Exception e) {
				} 
				
				if (value == null) {
					value = Constants.STR_BLANK;
				}
				jo.put(targetAttrName, value);
			}
				
		}
		
		return jo;
	}
	
	public static JSONObject setJoAttrsByDataObj(JSONObject jo, Object dataObj, String dateFmtStr, String... attrNames) {
		
		return setJoAttrsByDataObj(jo, dataObj, dateFmtStr, 0, 0, false, attrNames);
	}
	
	/**
	 * copy指定的json对象的属性值给json对象
	 * 
	 * @param jo			待设置的对象			可为空，为空则新建json对象
	 * @param key			设置的属性名
	 * @param dataJson		存储目标值的json对象		
	 * @param dataKey		取存储目标值的json对象的key   可为空，为空则默认等于key
	 * @return
	 */
	public static JSONObject setJoAttrByDataJson(JSONObject jo, String key, JSONObject dataJson, String dataKey) {
		
		if(jo == null) {
			jo = new JSONObject();
		}
		
		key = StringUtil.trimStr(key);
		if(key.equals(Constants.STR_BLANK)) {
			return jo;
		}
		
		dataKey = StringUtil.trimStr(dataKey);
		if(dataKey.equals(Constants.STR_BLANK)) {
			dataKey = key;
		}
		
		if(dataJson == null) {
			jo.put(key, Constants.STR_BLANK);
		} else {
			jo.put(key, StringUtil.trimStr(dataJson.getString(dataKey)));
		}
		
		return jo;
	}
	
	/**
	 * 将request中的所有参数封装为Json对象
	 * @param request
	 * @return
	 */
	public static JSONObject constructJsonObjFromRequest(HttpServletRequest request) {
		
		JSONObject jo = new JSONObject();
		
		if (request != null) {
			
			Map<String, String[]> parameterMap = request.getParameterMap();
			Set<String> keySet = parameterMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				
				String[] values = parameterMap.get(key);

				if (values.length < 1) {
					continue;
				}
				
				if (values.length < 2) {
					jo.put(key, values[0]);
				} else {
					jo.put(key, values);
				}
			}
		}
		
		return jo;
	}

	/**
	 * 解析文本为一个json
	 * @param jsonStr
	 * @param errorReturnNull		解析失败时是否返回空对象
	 * @return
	 */
	public static JSONObject parseJsonObject(String jsonStr, boolean errorReturnNull) {
		
		JSONObject parseObject = null;
		
		try {
			parseObject = JSONObject.parseObject(StringUtil.trimStr(jsonStr));
		} catch (Exception e) {
			if (errorReturnNull) {
				return parseObject;
			}
		}
		
		if (parseObject == null) {
			parseObject = new JSONObject();
		}
		
		return parseObject;
	}

	/**
	 * 从json对象中取得一个不为空的json对象
	 * @param jo
	 * @param key
	 * @return
	 */
	public static JSONObject getJsonObject(JSONObject jo, String key) {
		
		JSONObject returnJo = jo.getJSONObject(key);
		
		if (returnJo == null) {
			returnJo = new JSONObject();
		}
		
		return returnJo;
	}
	
}
