package com.poll.common.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class CheckUtil {

    /**
     * 检查参数是否合法
     *
     * @param value           参数值
     * @param alias           参数别名
     * @param allowNull       是否允许为null
     * @param emptyRegardNull 若参数为空字符等是否视为null
     * @param defaultVal      为null时的默认值
     * @param minLen          最小长度
     * @param maxLen          最大长度
     * @param minVal          参数最小值
     * @param maxVal          参数最大值
     * @param isValIn         参数值是否在指定数组中
     * @param regular         正则表达式，日期类型该参数不进行正则校验，用于传递日期格式
     * @return
     * @throws RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static <T> T checkParamSimple(T value, String alias, boolean allowNull, boolean emptyRegardNull, T defaultVal, int minLen, int maxLen, T minVal, T maxVal, T[] isValIn, String regular) throws RuntimeException {

        // 处理空值
        String valueStr = "";
        if (value != null) {
            valueStr = value.toString();
            if (value instanceof String) {
                valueStr = valueStr.trim();
                value = (T) valueStr;
            }
        }
        if (value == null || (emptyRegardNull && valueStr.equals(""))) {
            if (allowNull) {
                return defaultVal;
            } else {
                throw new RuntimeException(String.format("【%s】不能为空", alias));
            }
        }

        // 判断是否为日期类型
        boolean isDateType = isValueDateType(value);

        if (!isDateType) {
            // 处理长度
            if (minLen > 0 && valueStr.length() < minLen) {
                throw new RuntimeException(String.format("【%s】长度不能小于%s", alias, minLen));
            }
            if (maxLen > 0 && valueStr.length() > maxLen) {
                throw new RuntimeException(String.format("【%s】长度不能大于%s", alias, maxLen));
            }
        }

        // 处理最大值与最小值
        if (minVal != null || maxVal != null) {
            Method method = null;
            Integer minResult = null;
            Integer maxResult = null;
            try {
                boolean isString = false;
                if (value instanceof java.sql.Date) {
                    method = Date.class.getMethod("compareTo", Date.class);
                } else if (value instanceof String) {
                    method = Double.class.getMethod("compareTo", Double.class);
                    isString = true;
                } else {
                    method = value.getClass().getMethod("compareTo", value.getClass());
                }
                if (isString && valueStr.matches(RegularUtil.numReg)) {
                    Double valueDouble = new Double(valueStr);
                    if (minVal != null) {
                        minResult = (Integer) method.invoke(valueDouble, new Double(String.valueOf(minVal)));
                    }
                    if (maxVal != null) {
                        maxResult = (Integer) method.invoke(valueDouble, new Double(String.valueOf(maxVal)));
                    }
                } else {
                    if (minVal != null) {
                        minResult = (Integer) method.invoke(value, minVal);
                    }
                    if (maxVal != null) {
                        maxResult = (Integer) method.invoke(value, maxVal);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("【%s】最大或最小值错误", alias));
            }

            if (minResult != null && minResult < 0) {

                String tips = minVal.toString();

                if (regular != null && isDateType) {
                    try {
                        tips = new SimpleDateFormat(regular).format(minVal);
                    } catch (Exception e) {
                    }
                }
                throw new RuntimeException(String.format("【%s】不能小于%s", alias, tips));
            }
            if (maxResult != null && maxResult > 0) {

                String tips = maxVal.toString();

                if (regular != null && isDateType) {
                    try {
                        tips = new SimpleDateFormat(regular).format(maxVal);
                    } catch (Exception e) {
                    }
                }
                throw new RuntimeException(String.format("【%s】不能大于%s", alias, tips));
            }
        }

        // 查询是否在罗列值中
        if (isValIn != null && isValIn.length > 0) {
            boolean isIn = false;
            for (T t : isValIn) {
                if (value.equals(t)) {
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                throw new RuntimeException(String.format("【%s】非法", alias, maxLen));
            }
        }

        // 处理正则
        if (!isDateType && regular != null && !valueStr.matches(regular)) {
            throw new RuntimeException(String.format("【%s】格式错误", alias));
        }

        return value;
    }

    /**
     * 验证参数是否正确，且在正确时，将值注入至目标对象
     *
     * @param value
     * @param alias
     * @param nameInToObj
     * @param toObj
     * @param valueTrans2Str  赋值时是否将其转为字符串
     * @param allowNull
     * @param emptyRegardNull
     * @param defaultVal
     * @param minLen
     * @param maxLen
     * @param minVal
     * @param maxVal
     * @param isValIn
     * @param regular
     * @return
     * @throws RuntimeException
     */
    public static <T> T checkParamSimple2Obj(T value, String alias, String nameInToObj, Object toObj, boolean valueTrans2Str, boolean allowNull, boolean emptyRegardNull, T defaultVal, int minLen, int maxLen, T minVal, T maxVal,
                                             T[] isValIn, String regular) throws RuntimeException {

        // 执行检查
        T t = checkParamSimple(value, alias, allowNull, emptyRegardNull, defaultVal, minLen, maxLen, minVal, maxVal, isValIn, regular);

        // 设值
        setValue(t, alias, nameInToObj, toObj, valueTrans2Str, regular);

        return t;
    }

    /**
     * 从一个对象中取值，经检查后设置至另一对象
     *
     * @param name
     * @param nameInToObj
     * @param alias
     * @param pClass
     * @param from
     * @param to
     * @param valueTrans2Str
     * @param allowNull
     * @param emptyRegardNull
     * @param defaultVal
     * @param minLen
     * @param maxLen
     * @param isValIn
     * @param minVal
     * @param maxVal
     * @param regular
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    public static <T> T checkParamSimpleFromTo(String name, String nameInToObj, String alias, Class<T> pClass, Object from, Object to, boolean valueTrans2Str, boolean allowNull, boolean emptyRegardNull, String defaultVal, int minLen,
                                               int maxLen, String minVal, String maxVal, String[] isValIn, String regular) throws RuntimeException {

        // 处理别名
        alias = handleAlias(alias, name);

        boolean getValueRegular = true;
        if (regular != null && regular.startsWith("nf->")) {
            regular = regular.substring(4, regular.length());
            getValueRegular = false;
        }

        // 取值   当regular以nf->开始时，取值不启用regular
        T value = getValue(name, alias, pClass, from, getValueRegular ? regular : null);

        if (nameInToObj == null) {
            nameInToObj = name;
        }

        // 校验 并 设值
        value = checkParamSimple2Obj(value, alias, nameInToObj, to, valueTrans2Str, allowNull, emptyRegardNull, parseFromStr(defaultVal, alias, pClass, regular), minLen, maxLen, parseFromStr(minVal, alias, pClass, regular),
                parseFromStr(maxVal, alias, pClass, regular), parseArrFromStr(isValIn, alias, pClass, regular), regular);

        return value;
    }

    public static <T> T checkParamSimpleFromTo(String name, String alias, Class<T> pClass, Object from, Object to, boolean allowNull,
                                               String defaultVal, int minLen, int maxLen, String regular) throws RuntimeException {
        return checkParamSimpleFromTo(name, name, alias, pClass, from, to, false, allowNull, true,
                defaultVal, minLen, maxLen, null, null, null, regular);
    }

    public static <T> T checkParamSimpleFromTo(String name, String nameInToObj, String alias, Class<T> pClass, Object from, Object to, boolean allowNull, String defaultVal, int minLen, int maxLen, String regular) throws RuntimeException {
        return checkParamSimpleFromTo(name, nameInToObj, alias, pClass, from, to, false, allowNull, true, defaultVal, minLen, maxLen, null, null, null, regular);
    }

    public static <T> T checkParamSimpleFromTo(String name, String alias, Class<T> pClass, Object from, Object to, boolean allowNull, String defaultVal, String minVal, String maxVal, String regular) throws RuntimeException {
        return checkParamSimpleFromTo(name, name, alias, pClass, from, to, false, allowNull, true, defaultVal, -1, -1, minVal, maxVal, null, regular);
    }

    public static <T> T checkParamSimpleFromTo(String name, String nameInToObj, String alias, Class<T> pClass, Object from, Object to, boolean allowNull, String defaultVal, String minVal, String maxVal, String regular) throws RuntimeException {
        return checkParamSimpleFromTo(name, nameInToObj, alias, pClass, from, to, false, allowNull, true, defaultVal, -1, -1, minVal, maxVal, null, regular);
    }

    public static <T> T checkParamSimpleFromTo(String name, String alias, Class<T> pClass, Object from, Object to, boolean allowNull, String defaultVal, String[] isValIn) throws RuntimeException {
        return checkParamSimpleFromTo(name, name, alias, pClass, from, to, false, allowNull, true, defaultVal, -1, -1, null, null, isValIn, null);
    }

    public static <T> T checkParamSimpleFromTo(String name, String nameInToObj, String alias, Class<T> pClass, Object from, Object to, boolean allowNull, String defaultVal, String[] isValIn) throws RuntimeException {
        return checkParamSimpleFromTo(name, nameInToObj, alias, pClass, from, to, false, allowNull, true, defaultVal, -1, -1, null, null, isValIn, null);
    }


    /**
     * 检查有length属性或者size()方法的arr传入对象在数量上是否合法
     *
     * @param arr
     * @param arrAlias
     * @param arrAllowNull
     * @param arrEmptyRegardNull
     * @param arrMinSize
     * @param arrMaxSize
     * @throws RuntimeException
     */
    public static void checkParamArrSize(Object arr, String arrAlias, boolean arrAllowNull, boolean arrEmptyRegardNull, int arrMinSize, int arrMaxSize) throws RuntimeException {

        arrAlias = handleAlias(arrAlias, "");

        int size = 0;

        // 处理空值
        if (arr == null) {
            if (!arrAllowNull) {
                throw new RuntimeException(String.format("【%s】不能为空", arrAlias));
            }
        } else {
            Class<?> arrClass = arr.getClass();

            if (arrClass.isArray()) {
                try {
                    size = Array.getLength(arr);
                } catch (IllegalArgumentException e) {
                }
            } else {
                try {
                    Method mtdSize = arrClass.getMethod("size");
                    size = (Integer) mtdSize.invoke(arr);
                } catch (Exception e1) {
                }
            }
        }

        if (size == 0 && arrEmptyRegardNull) {
            if (!arrAllowNull) {
                throw new RuntimeException(String.format("【%s】不能为空", arrAlias));
            }
        }

        // 处理最小长度
        if (arrMinSize > 0 && size < arrMinSize) {
            throw new RuntimeException(String.format("【%s】长度不能小于%s", arrAlias, arrMinSize));
        }
        // 处理最大长度
        if (arrMaxSize > 0 && size > arrMaxSize) {
            throw new RuntimeException(String.format("【%s】长度不能大于%s", arrAlias, arrMaxSize));
        }
    }

    /**
     * 检查简单数组参数是否合法
     *
     * @param arr
     * @param arrAlias
     * @param arrAllowNull
     * @param arrEmptyRegardNull
     * @param arrMinSize
     * @param arrMaxSize
     * @param valAllowNull
     * @param valEmptyRegardNull
     * @param valDefaultVal
     * @param valMinLen
     * @param valMaxLen
     * @param valMinVal
     * @param valMaxVal
     * @param valIsValIn
     * @param valRegular
     * @return
     * @throws RuntimeException
     */
    public static <T> List<T> checkParamSimpleArr(List<T> arr, String arrAlias, boolean arrAllowNull, boolean arrEmptyRegardNull, int arrMinSize, int arrMaxSize, boolean valAllowNull,
                                                  boolean valEmptyRegardNull, T valDefaultVal, int valMinLen, int valMaxLen, T valMinVal, T valMaxVal, T[] valIsValIn, String valRegular, List<String> trans2StrList) throws RuntimeException {

        arrAlias = handleAlias(arrAlias, "");

        checkParamArrSize(arr, arrAlias, arrAllowNull, arrEmptyRegardNull, arrMinSize, arrMaxSize);

        List<T> list = new ArrayList<T>();

        //循环检查
        for (int i = 0; i < arr.size(); i++) {

            T t = checkParamSimple(arr.get(i), String.format("%s第%s项", arrAlias, i + 1), valAllowNull, valEmptyRegardNull, valDefaultVal, valMinLen, valMaxLen, valMinVal, valMaxVal, valIsValIn, valRegular);

            list.add(t);

            if (trans2StrList != null) {
                trans2StrList.add(transValue2Str(t, valRegular));
            }
        }

        return list;
    }

    public static <T> List<T> checkParamSimpleArr2Obj(List<T> arr, String arrAlias, String arrNameInToObj, Object toObj, boolean arrAllowNull, boolean arrEmptyRegardNull, int arrMinSize, int arrMaxSize, boolean valueTrans2Str,
                                                      boolean valAllowNull, boolean valEmptyRegardNull, T valDefaultVal, int valMinLen, int valMaxLen, T valMinVal, T valMaxVal,
                                                      T[] valIsValIn, String valRegular) throws RuntimeException {

        List<String> trans2StrList = null;
        if (valueTrans2Str) {
            trans2StrList = new ArrayList<String>();
        }

        List<T> list = checkParamSimpleArr(arr, arrAlias, arrAllowNull, arrEmptyRegardNull, arrMinSize, arrMaxSize, valAllowNull, valEmptyRegardNull, valDefaultVal, valMinLen, valMaxLen, valMinVal, valMaxVal, valIsValIn, valRegular, trans2StrList);

        setValue(valueTrans2Str ? trans2StrList : list, arrAlias, arrNameInToObj, toObj, false, null);

        return list;
    }

    public static <T> List<T> checkParamSimpleArr2Obj(List<T> arr, String arrAlias, String arrNameInToObj, Object toObj, boolean arrAllowNull,
                                                      boolean valAllowNull, T valDefaultVal, int valMinLen, int valMaxLen, String valRegular) throws RuntimeException {
        return checkParamSimpleArr2Obj(arr, arrAlias, arrNameInToObj, toObj, arrAllowNull, true, -1, -1, false, valAllowNull, true, valDefaultVal, valMinLen, valMaxLen, null, null, null, valRegular);
    }

    public static <T> List<T> checkParamSimpleArr2Obj(List<T> arr, String arrAlias, String arrNameInToObj, Object toObj, boolean arrAllowNull,
                                                      boolean valAllowNull, T valDefaultVal, T valMinVal, T valMaxVal, String valRegular) throws RuntimeException {
        return checkParamSimpleArr2Obj(arr, arrAlias, arrNameInToObj, toObj, arrAllowNull, true, -1, -1, false, valAllowNull, true, valDefaultVal, -1, -1, valMinVal, valMaxVal, null, valRegular);
    }

    public static <T> List<T> checkParamSimpleArr2Obj(List<T> arr, String arrAlias, String arrNameInToObj, Object toObj, boolean arrAllowNull,
                                                      boolean valAllowNull, T valDefaultVal, T[] valIsValIn) throws RuntimeException {
        return checkParamSimpleArr2Obj(arr, arrAlias, arrNameInToObj, toObj, arrAllowNull, true, -1, -1, false, valAllowNull, true, valDefaultVal, -1, -1, null, null, valIsValIn, null);
    }

    /**
     * 判断是否为日期类型
     *
     * @param value
     * @return
     */
    private static boolean isValueDateType(Object value) {
        return value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp;
    }

    private static boolean isClassDateType(Class<?> pClass) {
        return Date.class == pClass || java.sql.Date.class == pClass || Timestamp.class == pClass;
    }

    private static boolean isClassDateTypeWithoutTimesamp(Class<?> pClass) {
        return Date.class == pClass || java.sql.Date.class == pClass;
    }

    private static boolean isClassCharType(Class<?> pClass) {
        return Character.class == pClass || char.class == pClass;
    }

    @SuppressWarnings("rawtypes")
    private static <T> T getValue(String name, String alias, Class<T> pClass, Object from, String regular) {
        T value = null;
        if (from instanceof JSONObject) {
            value = getValueFromJson(name, alias, pClass, (JSONObject) from, regular);
        } else if (from instanceof HttpServletRequest) {
            value = getValueFromRequest(name, alias, pClass, (HttpServletRequest) from, regular);
        } else if (from instanceof Map) {
            value = getValueFromMap(name, alias, pClass, (Map) from, regular);
        } else {
            value = getValueFromObj(name, alias, pClass, from, regular);
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    private static void setValue(Object value, String alias, String nameInToObj, Object toObj, boolean valueTrans2Str, String regular) {
        if (toObj instanceof JSONObject) {
            setValue2Json(value, alias, nameInToObj, (JSONObject) toObj, valueTrans2Str, regular);
        } else if (toObj instanceof Map) {
            setValue2Map(value, alias, nameInToObj, (Map) toObj, valueTrans2Str, regular);
        } else {
            setValue2Obj(value, alias, nameInToObj, toObj, valueTrans2Str, regular);
        }
    }

    private static String transValue2Str(Object value, String dateFmt) {
        if (value != null) {
            // 判断是否为日期类型
            if (dateFmt != null && isValueDateType(value)) {
                return new SimpleDateFormat(dateFmt).format(value);
            }
            return value.toString();
        }
        return null;
    }

    /**
     * 调用set方法设置value值至目标对象
     *
     * @param value
     * @param alias
     * @param nameInToObj
     * @param toObj
     * @param valueTrans2Str
     * @param regular
     */
    private static void setValue2Obj(Object value, String alias, String nameInToObj, Object toObj, boolean valueTrans2Str, String regular) {
        // 设值
        if (toObj != null && nameInToObj != null) {

            nameInToObj = nameInToObj.trim();
            if (!nameInToObj.equals("")) {

                String methodName = "set" + nameInToObj.substring(0, 1).toUpperCase() + (nameInToObj.length() > 1 ? nameInToObj.substring(1) : "");

                try {
                    Class<?> type = null;

                    if (valueTrans2Str) {
                        type = String.class;
                        value = transValue2Str(value, regular);
                    } else {
                        type = toObj.getClass().getDeclaredField(nameInToObj).getType();
                    }

                    Method method = toObj.getClass().getMethod(methodName, type);
                    method.invoke(toObj, value);

                } catch (Exception e) {
                    throw new RuntimeException(String.format("【%s】传值失败", alias));
                }
            }
        }
    }

    /**
     * 设置值至目标json对象
     *
     * @param value
     * @param alias
     * @param keyInToJson
     * @param toJson
     * @param valueTrans2Str
     * @param regular
     */
    private static void setValue2Json(Object value, String alias, String keyInToJson, JSONObject toJson, boolean valueTrans2Str, String regular) {
        // 设置
        if (toJson != null && keyInToJson != null) {

            keyInToJson = keyInToJson.trim();
            if (!keyInToJson.equals("")) {

                try {
                    if (value == null) {
                        toJson.put(keyInToJson, null);
                    } else {
                        if (valueTrans2Str) {
                            value = transValue2Str(value, regular);
                        }
                        toJson.put(keyInToJson, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(String.format("【%s】传值失败", alias));
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setValue2Map(Object value, String alias, String keyInToMap, Map toMap, boolean valueTrans2Str, String regular) {
        // 设值
        if (toMap != null && keyInToMap != null) {

            keyInToMap = keyInToMap.trim();
            if (!keyInToMap.equals("")) {

                try {
                    if (value == null) {
                        toMap.put(keyInToMap, null);
                    } else {
                        if (valueTrans2Str) {
                            value = transValue2Str(value, regular);
                        }
                        toMap.put(keyInToMap, value);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(String.format("【%s】传值失败", alias));
                }
            }
        }
    }

    // 处理别名
    private static String handleAlias(String alias, String defaultAlias) {

        if (alias == null) {
            alias = defaultAlias;
        } else {
            alias = alias.trim();
            if (alias.equals("")) {
                alias = defaultAlias;
            }
        }
        return alias;
    }

    /**
     * 从json中取值
     *
     * @param key      参数名
     * @param alias    参数别名
     * @param pClass   参数类型
     * @param fromJson 数据来源
     * @param dateFmt  若该参数不同空，则从fromJson取值时，先用getString取值，再用dateFmt转型为时间类型
     * @return
     */
    private static <T> T getValueFromJson(String key, String alias, Class<T> pClass, JSONObject fromJson, String dateFmt) {

        if (fromJson == null) {
            throw new RuntimeException("数据来源为空");
        }

        String simpleName = pClass.getSimpleName();
        boolean isChar = isClassCharType(pClass);
        boolean convertDate = "Date".equals(simpleName) && dateFmt != null;
        boolean convertTimestamp = "Timestamp".equals(simpleName) && dateFmt != null;

        // 字符 类型首先当成字符串取值 若 dateFmt不为空，则时间类型同样当成字符串取值
        if (isChar || convertDate || convertTimestamp) {
            simpleName = "String";
        } else if (pClass.isPrimitive()) {
            simpleName = simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1, simpleName.length()) + "Value";
        } else if (pClass == java.sql.Date.class) {
            simpleName = "SqlDate";
        }

        Object value = null;
        try {
            Method method = fromJson.getClass().getMethod("get" + simpleName, String.class);
            value = method.invoke(fromJson, key);
        } catch (Exception e) {
            throw new RuntimeException(String.format("【%s】解析异常", alias));
        }

        if (value instanceof String) {
            String valueStr = ((String) value).trim();
            if (valueStr.equals("")) {
                return null;
            }
            return parseFromStr(String.valueOf(value), alias, pClass, dateFmt);
        }
        return parseFromObj(value, alias, pClass, dateFmt);
    }

    private static <T> T getValueFromRequest(String name, String alias, Class<T> pClass, HttpServletRequest fromRequest, String dateFmt) {

        if (fromRequest == null) {
            throw new RuntimeException("数据来源为空");
        }
        return parseFromStr(fromRequest.getParameter(name), alias, pClass, dateFmt);
    }

    @SuppressWarnings("rawtypes")
    private static <T> T getValueFromMap(String name, String alias, Class<T> pClass, Map fromMap, String dateFmt) {

        if (fromMap == null) {
            throw new RuntimeException("数据来源为空");
        }

        Object value = fromMap.get(name);
        if (value instanceof String) {
            String valueStr = ((String) value).trim();
            if (valueStr.equals("")) {
                return null;
            }
            return parseFromStr(String.valueOf(value), alias, pClass, dateFmt);
        }
        return parseFromObj(fromMap.get(name), alias, pClass, dateFmt);
    }

    private static <T> T getValueFromObj(String name, String alias, Class<T> pClass, Object fromObj, String dateFmt) {

        if (fromObj == null) {
            throw new RuntimeException("数据来源为空");
        }

        Object value = null;
        try {
            Method method = fromObj.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : ""));
            value = method.invoke(fromObj);
        } catch (Exception e) {
            throw new RuntimeException(String.format("【%s】解析异常", alias));
        }

        return parseFromObj(value, alias, pClass, dateFmt);
    }

    /**
     * 尝试从基础类型转为对应封装对象类型
     * <p>
     * 例如 从 int.class 转为 Integer.class
     *
     * @param pClass
     * @return
     */
    private static Class<?> transPrimitive2Enclose(Class<?> pClass) {

        if (pClass != null && pClass.isPrimitive()) {
            if (int.class == pClass) {
                return Integer.class;
            }
            if (long.class == pClass) {
                return Long.class;
            }
            if (byte.class == pClass) {
                return Byte.class;
            }
            if (double.class == pClass) {
                return Double.class;
            }
            if (float.class == pClass) {
                return Float.class;
            }
            if (short.class == pClass) {
                return Short.class;
            }
            if (boolean.class == pClass) {
                return Boolean.class;
            }
            if (char.class == pClass) {
                return Character.class;
            }
        }
        return pClass;
    }

    /**
     * 从字符串解析为对应对象类型
     *
     * @param value
     * @param alias
     * @param pClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T parseFromStr(String value, String alias, Class<T> pClass, String dateFmt) {

        if (value != null) {
            try {
                Class<?> encloseClass = transPrimitive2Enclose(pClass);
                if (isClassDateType(pClass)) {
                    Long timeMills = null;
                    // 未传递格式，认定为时间戳字符串
                    if (dateFmt == null) {
                        timeMills = Long.parseLong(value);
                    } else {
                        timeMills = new SimpleDateFormat(dateFmt).parse(value).getTime();
                    }
                    return (T) encloseClass.getConstructor(long.class).newInstance(timeMills);
                }
                if (Character.class == encloseClass) {
                    return (T) encloseClass.getConstructor(char.class).newInstance(value.charAt(0));
                }
                return (T) encloseClass.getConstructor(String.class).newInstance(value);
            } catch (Exception e) {
            }

            throw new RuntimeException(String.format("【%s】格式化异常", alias));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseFromObj(Object value, String alias, Class<T> pClass, String dateFmt) {

        if (value != null) {
            try {
                if (isClassCharType(pClass)) {
                    return (T) new Character(value.toString().charAt(0));
                } else if (dateFmt != null && isClassDateTypeWithoutTimesamp(pClass)) {
                    return (T) new SimpleDateFormat(dateFmt).parse(value.toString());
                } else if (dateFmt != null && Timestamp.class == pClass) {
                    return (T) new Timestamp(new SimpleDateFormat(dateFmt).parse(value.toString()).getTime());
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("【%s】格式化异常", alias));
            }
            return (T) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] parseArrFromStr(String[] valueArr, String alias, Class<T> pClass, String dateFmt) {

        if (valueArr != null) {
            Object[] objArr = new Object[valueArr.length];
            for (int i = 0; i < objArr.length; i++) {
                objArr[i] = parseFromStr(valueArr[i], alias, pClass, dateFmt);
            }
            return (T[]) objArr;
        }
        return null;
    }

    public static void main(String[] args) {

        JSONObject fromJo = new JSONObject();
        fromJo.put("str", " xx332d0ikjh  ");
        fromJo.put("int", "23");
        fromJo.put("long", "23");
        fromJo.put("short", "23");
        fromJo.put("byte", "23");
        fromJo.put("double", "23.00");
        fromJo.put("float", "23.0");
        fromJo.put("bigint", "23");
        fromJo.put("bigint2", "");
        fromJo.put("bigdecimal", "123456.654321");
        fromJo.put("boolean", true);
        fromJo.put("char", "abc");
        fromJo.put("date", "20180530000001");
        fromJo.put("sqldate", new java.sql.Date(System.currentTimeMillis()));
        fromJo.put("timestamp", "20180530030303");

        JSONObject toJo = new JSONObject();
        checkParamSimpleFromTo("str", "str2", "str2", String.class, fromJo, toJo, false, false, true, "12333", 1, 15, null, null, new String[]{"xx332d0ikjh", "2"}, null);
        checkParamSimpleFromTo("int", "int2", "int2", Integer.class, fromJo, toJo, true, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("long", "long2", "long2", Integer.class, fromJo, toJo, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("short", "short2", "short2", Short.class, fromJo, toJo, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("byte", "byte2", "byte2", Byte.class, fromJo, toJo, true, false, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("double2", "double2", "double2", Double.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("float", "float2", "float2", Float.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("bigint2", "bigint2", "bigint", BigInteger.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("bigdecimal", "bigdecimal", "bigdecimal", BigDecimal.class, fromJo, toJo, true, true, false, "123", 1, 15, null, null, new String[]{"1", "123456.654321"}, null);
        checkParamSimpleFromTo("boolean", "boolean", "boolean", boolean.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, null);
        checkParamSimpleFromTo("char", "char", "char", char.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, null);
        checkParamSimpleFromTo("date", "date", "date", Date.class, fromJo, toJo, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
        checkParamSimpleFromTo("sqldate", "sqldate", "sqldate", java.sql.Date.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, "nf->yyyyMMddHHmmss");
        checkParamSimpleFromTo("timestamp", "timestamp", "timestamp", Timestamp.class, fromJo, toJo, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
        System.out.println(toJo.toJSONString());

        Map<String, Object> toMap = new HashMap<String, Object>();
        checkParamSimpleFromTo("str", "str2", "str2", String.class, fromJo, toMap, false, false, true, "12333", 1, 15, null, null, new String[]{"xx332d0ikjh", "2"}, null);
        checkParamSimpleFromTo("int", "int2", "int2", Integer.class, fromJo, toMap, true, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("long", "long2", "long2", Integer.class, fromJo, toMap, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("short", "short2", "short2", Short.class, fromJo, toMap, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("byte", "byte2", "byte2", Byte.class, fromJo, toMap, true, false, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("double2", "double2", "double2", Double.class, fromJo, toMap, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("float", "float2", "float2", Float.class, fromJo, toMap, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("bigint2", "bigint2", "bigint", BigInteger.class, fromJo, toMap, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
        checkParamSimpleFromTo("bigdecimal", "bigdecimal", "bigdecimal", BigDecimal.class, fromJo, toMap, true, true, false, "123", 1, 15, null, null, new String[]{"1", "123456.654321"}, null);
        checkParamSimpleFromTo("boolean", "boolean", "boolean", boolean.class, fromJo, toMap, false, true, false, null, -1, -1, null, null, null, null);
        checkParamSimpleFromTo("char", "char", "char", char.class, fromJo, toMap, false, true, false, null, -1, -1, null, null, null, null);
        checkParamSimpleFromTo("date", "date", "date", Date.class, fromJo, toMap, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
        checkParamSimpleFromTo("sqldate", "sqldate", "sqldate", java.sql.Date.class, fromJo, toMap, false, true, false, null, -1, -1, null, null, null, "nf->yyyyMMddHHmmss");
        checkParamSimpleFromTo("timestamp", "timestamp", "timestamp", Timestamp.class, fromJo, toMap, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
        System.out.println(JSONObject.toJSONString(toMap));

        Integer[] testArr = new Integer[]{1, 2, 4};
        List<Integer> testList = checkParamSimpleArr(Arrays.asList(testArr), "数组测试", false, true, 1, 3, false, true, 2, 1, 2, 1, 5, null, null, null);
        System.out.println(JSONObject.toJSONString(testList));

        List<JSONObject> testJoList = new ArrayList<JSONObject>();
        testJoList.add(fromJo);
        testJoList.add(fromJo);
        testJoList.add(fromJo);

        for (int i = 0; i < testJoList.size(); i++) {

            fromJo = testJoList.get(i);

            toJo = new JSONObject();
            checkParamSimpleFromTo("str", "str2", "str2", String.class, fromJo, toJo, true, true, true, "12333", 1, 15, null, null, new String[]{"xx332d0ikjh", "2"}, null);
            checkParamSimpleFromTo("int", "int2", "int2", Integer.class, fromJo, toJo, true, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("long", "long2", "long2", Integer.class, fromJo, toJo, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("short", "short2", "short2", Short.class, fromJo, toJo, false, true, true, "12333", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("byte", "byte2", "byte2", Byte.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("double2", "double2", "double2", Double.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("float", "float2", "float2", Float.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("bigint2", "bigint2", "bigint", BigInteger.class, fromJo, toJo, true, true, false, "123", 1, 15, "1", "30", new String[]{"1", "23"}, null);
            checkParamSimpleFromTo("bigdecimal", "bigdecimal", "bigdecimal", BigDecimal.class, fromJo, toJo, true, true, false, "123", 1, 15, null, null, new String[]{"1", "123456.654321"}, null);
            checkParamSimpleFromTo("boolean", "boolean", "boolean", boolean.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, null);
            checkParamSimpleFromTo("char", "char", "char", char.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, null);
            checkParamSimpleFromTo("date", "date", "date", Date.class, fromJo, toJo, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
            checkParamSimpleFromTo("sqldate", "sqldate", "sqldate", java.sql.Date.class, fromJo, toJo, false, true, false, null, -1, -1, null, null, null, "nf->yyyyMMddHHmmss");
            checkParamSimpleFromTo("timestamp", "timestamp", "timestamp", Timestamp.class, fromJo, toJo, true, true, false, null, -1, -1, null, null, null, "yyyyMMddHHmmss");
            toJo.put("nnnn", i);

            testJoList.set(i, toJo);
        }
        System.out.println(JSONObject.toJSONString(testJoList));

//        checkParamArrSize(new String[] {}, "1111", false, true, 2, 3);

    }

    /**
     * 校验字符串是否为空或空字符
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (null == str || str.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
