package com.poll.common.util;

import com.alibaba.fastjson.JSONObject;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpUtil {
	
	//http请求方法
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String REQUEST_METHOD_HEAD = "HEAD";
	public static final String REQUEST_METHOD_OPTIONS = "OPTIONS";
	public static final String REQUEST_METHOD_PUT = "PUT";
	public static final String REQUEST_METHOD_DELETE = "DELETE";
	public static final String REQUEST_METHOD_TRACE = "TRACE";
	
	//http请求返回content存储在map中的key
	public static final String RESPONSE_KEY_CODE = "responseCode";
	public static final String RESPONSE_KEY_CONTENT = "responseContent";
	
	//http请求头名称
	public static final String HEADER_NAME_ACCEPT = "Accept";
	public static final String HEADER_NAME_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_NAME_ACCEPT_LANGUAGE = "Accept-Language";
	public static final String HEADER_NAME_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_NAME_CONNECTION = "Connection";
	public static final String HEADER_NAME_COOKIE = "Cookie";
	public static final String HEADER_NAME_HOST = "Host";
	public static final String HEADER_NAME_PRAGMA = "Pragma";
	public static final String HEADER_NAME_USER_AGENT = "User-Agent";
	public static final String HEADER_NAME_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_NAME_CONTENT_DATE = "Date";
	public static final String HEADER_NAME_CONTENT_EXPIRES = "Expires";
	public static final String HEADER_NAME_CONTENT_SERVER = "Server";
	public static final String HEADER_NAME_CONTENT_SET_COOKIE = "Set-Cookie";
	
	/**
	 * 发送get请求  返回头部信息以及内容
	 * @param url
	 * @param urlParams
	 * @param headerParamMap
	 * @param responseCharsetName
	 * @param connTimeoutMills
	 * @param readTimeoutMills
	 * @return 以key value形式存储返回信息 包括头部信息
	 *         body信息以response为key存储
	 * @throws Exception
	 */
	public static Map<String, Object> get(String url, String urlParams, Map<String, String> headerParamMap, String responseCharsetName, int connTimeoutMills, int readTimeoutMills) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		url = StringUtil.trimStr(url);
		if (url.equals(Constants.STR_BLANK)) {
			return resultMap;
		}
		
		if (connTimeoutMills < 0) {
			connTimeoutMills = 0;
		}

		//处理参数？号
		urlParams = StringUtil.trimStr(urlParams);
		if (url.contains(Constants.STR_QUESTION_MARK)) {
			if (urlParams.startsWith(Constants.STR_QUESTION_MARK)) {
				urlParams = urlParams.substring(1);
			}
			if (url.contains(Constants.STR_AMPERSAND)) {
				urlParams = Constants.STR_AMPERSAND + urlParams;
			}
		} else {
			if (!urlParams.startsWith(Constants.STR_QUESTION_MARK)) {
				urlParams = Constants.STR_QUESTION_MARK + urlParams;
			}
		}
		
		//拼接url
		url = url + urlParams;
		
		// 创建URL对象
		URL connURL = null;
		HttpURLConnection httpConn = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder(Constants.STR_BLANK);
		
		try {
			connURL = new URL(url);
			// 打开URL连接
			if (url.toLowerCase().startsWith("https://")) {

				//创建SSLContext对象，并使用我们指定的信任管理器初始化
				TrustManager[] tm = {new MyX509TrustManager()}; 
				SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE"); 
				sslContext.init(null, tm, new java.security.SecureRandom()); 

				//从上述SSLContext对象中得到SSLSocketFactory对象
				SSLSocketFactory ssf = sslContext.getSocketFactory();

				//创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
				httpConn = (HttpsURLConnection) connURL.openConnection();
				((HttpsURLConnection)httpConn).setSSLSocketFactory(ssf);
				
			} else {
				httpConn = (HttpURLConnection) connURL.openConnection();
			}
			
			// 设置请求头
			if (headerParamMap != null) {
				for (String key : headerParamMap.keySet()) {
					String value = headerParamMap.get(key);
					httpConn.setRequestProperty(key, value);
				}
			}
			
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod(REQUEST_METHOD_GET);
			
			//设置超时
			httpConn.setConnectTimeout(connTimeoutMills);
			httpConn.setReadTimeout(readTimeoutMills);
			
			// 定义BufferedReader输入流来读取URL的响应，设置编码方式
			responseCharsetName = StringUtil.trimStr(responseCharsetName);
			if (responseCharsetName.equals(Constants.STR_BLANK)) {
				responseCharsetName = Constants.CHARSET_UTF8;
			}
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), responseCharsetName));
			
			//取返回头信息
			Map<String, List<String>> headerFields = httpConn.getHeaderFields();
			for (String key : headerFields.keySet()) {
				resultMap.put(key, headerFields.get(key));
			}
			
			resultMap.put(RESPONSE_KEY_CODE, httpConn.getResponseCode());
			
			String line = null;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result.append(line);
				result.append(Constants.STR_CRLF);
			}
			
			//添加返回结果
			resultMap.put(RESPONSE_KEY_CONTENT, result.toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new ApiBizException(MsgCode.C00000016.code, MsgCode.C00000016.msg);
//			resultMap.put(RESPONSE_KEY_CONTENT, e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (httpConn != null) {
					httpConn.disconnect();
				}
			} catch (IOException ex) {
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 发送get请求  返回头部信息以及内容
	 * @param url
	 * @param urlParams
	 * @param headerParamMap
	 * @param responseCharsetName
	 * @param timeoutMills
	 * @return 以key value形式存储返回信息 包括头部信息
	 *         body信息以response为key存储
	 * @throws Exception
	 */
	public static Map<String, Object> get(String url, String urlParams, Map<String, String> headerParamMap, String responseCharsetName, int timeoutMills) throws Exception {
		
		return get(url, urlParams, headerParamMap, responseCharsetName, timeoutMills, timeoutMills);
	}
	
	public static Map<String, Object> get(String url, String responseCharsetName, int timeoutMills) throws Exception {
		
		return get(url, null, null, responseCharsetName, timeoutMills);
	}
	
	public static String get(String url, Map<String, String> headerParamMap, String responseCharsetName, int timeoutMills) throws Exception {
		
		Map<String, Object> resultMap = get(url, null, headerParamMap, responseCharsetName, timeoutMills);
		
		Object contentObj = resultMap.get(RESPONSE_KEY_CONTENT);
		
		if (contentObj == null) {
			return Constants.STR_BLANK;
		}
		
		return contentObj.toString();
	}
	
	public static String get(String url, String responseCharsetName) throws Exception {
		
		return get(url, null, responseCharsetName, 5000);
	}
	
	public static String get(String url) throws Exception {
		
		return get(url, Constants.CHARSET_UTF8);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 发送post请求
	 * @param url
	 * @param postParams
	 * @param headerParamMap
	 * @param responseCharsetName
	 * @param connTimeoutMills
	 * @param readTimeoutMills
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> post(String url, String postParams, Map<String, String> headerParamMap, String responseCharsetName, int connTimeoutMills, int readTimeoutMills) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		url = StringUtil.trimStr(url);
		if (url.equals(Constants.STR_BLANK)) {
			return resultMap;
		}
		
		if (connTimeoutMills < 0) {
			connTimeoutMills = 0;
		}
		
		if (readTimeoutMills < 0) {
			readTimeoutMills = 0;
		}

		postParams = StringUtil.trimStr(postParams);
		
		// 创建URL对象
		URL connURL = null;
		HttpURLConnection httpConn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder(Constants.STR_BLANK);
		
		try {
			connURL = new URL(url);
			// 打开URL连接
			if (url.toLowerCase().startsWith("https://")) {

				//创建SSLContext对象，并使用我们指定的信任管理器初始化
				TrustManager[] tm = {new MyX509TrustManager()}; 
				SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE"); 
				sslContext.init(null, tm, new java.security.SecureRandom()); 
				
				//从上述SSLContext对象中得到SSLSocketFactory对象
				SSLSocketFactory ssf = sslContext.getSocketFactory();

				//创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
				httpConn = (HttpsURLConnection) connURL.openConnection();
				((HttpsURLConnection)httpConn).setSSLSocketFactory(ssf);
				
			} else {
				httpConn = (HttpURLConnection) connURL.openConnection();
			}
			
			// 设置请求头
			if (headerParamMap != null) {
				for (String key : headerParamMap.keySet()) {
					String value = headerParamMap.get(key);
					httpConn.setRequestProperty(key, value);
				}
			}
			
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod(REQUEST_METHOD_POST);
			
			//设置超时
			httpConn.setConnectTimeout(connTimeoutMills);
			httpConn.setReadTimeout(readTimeoutMills);
			
			// 发送请求参数
			if (!postParams.equals(Constants.STR_BLANK)) {
				// 获取HttpURLConnection对象对应的输出流
				out = new PrintWriter(httpConn.getOutputStream());
				
				out.write(postParams);
				// flush输出流的缓冲
				out.flush();
			}
			
			// 定义BufferedReader输入流来读取URL的响应，设置编码方式
			responseCharsetName = StringUtil.trimStr(responseCharsetName);
			if (responseCharsetName.equals(Constants.STR_BLANK)) {
				responseCharsetName = Constants.CHARSET_UTF8;
			}
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), responseCharsetName));
			
			
			//取返回头信息
			Map<String, List<String>> headerFields = httpConn.getHeaderFields();
			for (String key : headerFields.keySet()) {
				resultMap.put(key, headerFields.get(key));
			}
			
			resultMap.put(RESPONSE_KEY_CODE, httpConn.getResponseCode());
			
			String line = null;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result.append(line);
				result.append(Constants.STR_CRLF);
			}
			
			//添加返回结果
			resultMap.put(RESPONSE_KEY_CONTENT, result.toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new ApiBizException(MsgCode.C00000016.code, MsgCode.C00000016.msg);
//			resultMap.put(RESPONSE_KEY_CONTENT, e.getMessage());
			
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (httpConn != null) {
					httpConn.disconnect();
				}
			} catch (IOException ex) {
			}
		}
		
		return resultMap;
	}
	
	/**
	 * 发送post请求
	 * @param url
	 * @param postParams
	 * @param headerParamMap
	 * @param responseCharsetName
	 * @param timeoutMills
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> post(String url, String postParams, Map<String, String> headerParamMap, String responseCharsetName, int timeoutMills) throws Exception {
		
		return post(url, postParams, headerParamMap, responseCharsetName, timeoutMills, timeoutMills);
	}
	
	public static Map<String, Object> post(String url, String postParams, String responseCharsetName, int timeoutMills) throws Exception {
		
		return post(url, postParams, null, responseCharsetName, timeoutMills);
	}
	
	public static String post2(String url, String postParams, Map<String, String> headerParamMap, String responseCharsetName, int timeoutMills) throws Exception {
		
		Map<String, Object> resultMap = post(url, postParams, headerParamMap, responseCharsetName, timeoutMills);
		
		Object contentObj = resultMap.get(RESPONSE_KEY_CONTENT);
		
		if (contentObj == null) {
			return Constants.STR_BLANK;
		}
		
		return contentObj.toString();
	}
	
	public static String post2(String url, String postParams, String responseCharsetName) throws Exception {
		
		return post2(url, postParams, null, responseCharsetName, 5000);
	}
	
	public static String post2(String url, String postParams) throws Exception {
		
		return post2(url, postParams, Constants.CHARSET_UTF8);
	}
	
	public static String post2(String url, String postParams, int timeoutMills) throws Exception {
		
		return post2(url, postParams, null, Constants.CHARSET_UTF8, timeoutMills);
	}
	
	public static String postJson(String url, String postParams, int timeoutMills) throws Exception {
		Map<String, String> headerParamMap = new HashMap<String, String>();
		headerParamMap.put(HttpUtil.HEADER_NAME_CONTENT_TYPE, "application/json");
		headerParamMap.put(HttpUtil.HEADER_NAME_ACCEPT, "application/json");
		return post2(url, postParams, headerParamMap, Constants.CHARSET_UTF8, timeoutMills);
	}
	
	
	/**
	 * 判断url是否可用
	 * @param urlStr
	 * @param attempTimes  尝试次数
	 * @return
	 */
	public static boolean isURLCorrect(String urlStr, int attempTimes) {
		
		urlStr = StringUtil.trimStr(urlStr);
		if (!urlStr.matches(RegularUtil.httpReg)) {
			return false;
		}
		
		if(attempTimes < 1) {
			attempTimes = 1;
		}
		int counts = 0;
		while (counts < attempTimes) {
			try {
				URL url = new URL(urlStr);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(4000);
				con.setRequestMethod(REQUEST_METHOD_HEAD);
				
				int state = con.getResponseCode();
				if (state == 200) {
					return true;
				}
			} catch (Exception ex) {
			}
			counts++;
		}
		return false;
	}

	/**
	 * 解析字符串格式为“a=b&c=d”的参数为map类型
	 * @param param
	 * @return
	 */
	public static Map<String, String> parseUrlParam(String param) {

		Map<String, String> paramMap = new HashMap<>();
		if (param != null) {
			//去掉开头的&符号
			if (param.startsWith(Constants.STR_AMPERSAND)) {
				param = param.substring(1);
			}

			//分割各个参数
			String[] paramArr = param.split(Constants.STR_AMPERSAND);
			for (String p : paramArr) {
				int i = p.indexOf(Constants.STR_EQUAL);
				if (i > 0) {
					paramMap.put(p.substring(0, i), p.substring(i+1));
				}
			}
		}
		return paramMap;
	}

	public static void main(String[] args) {
		String s = "&a=b&c=d&e=f&g&t=";
		System.out.println(JSONObject.toJSONString(parseUrlParam(s)));
	}
}



class MyX509TrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
	
}