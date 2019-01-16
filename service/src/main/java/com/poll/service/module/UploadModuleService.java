package com.poll.service.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.util.ApplicationContextUtil;
import com.poll.common.util.CheckUtil;
import com.poll.common.util.DateUtil;
import com.poll.common.util.RandomUtil;
import com.poll.service.conf.UploadConf;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;


@Service
public class UploadModuleService {
    @Autowired
    private UploadConf uploadConf;
    protected Logger log = LogManager.getLogger();
    private static Map<String, String> typeMapping = new HashMap<>();

    private final String patten = "yyyy" + File.separator + "MM" + File.separator + "dd";

    static {
        //初始化类型映射(系统支持类型)
        typeMapping.put("data:image/jpeg;", "et");
        typeMapping.put("data:image/x-icon;", "ett");
        typeMapping.put("data:image/gif;", "xls");
        typeMapping.put("data:image/png;", "xlt");
        typeMapping.put("data:image/png;", "xlsx");
    }

    public JSONObject handle(MultipartFile file,String key,Callback callback) throws Exception {
        Map<String, UploadConf.Config> configMap = uploadConf.getConfigs();
        if (configMap != null && !configMap.isEmpty() && configMap.containsKey(key)) {
            UploadConf.Config config = configMap.get(key);
            if (config.check()) {
                if (null != file && !file.isEmpty()) {
                    Date now = new Date();
                    //校验与写入文件
                    String dateDir = Constants.STR_BLANK;
                    if (config.isDateDir()) {
                        dateDir = DateUtil.convertDate2Str(now, patten);
                    }
                    if (file.getSize() > config.getMaxSize()) {
                        log.info("文件大小超过限制");
                        throw new ApiBizException(MsgCode.C00000001.code, "上传失败，文件太大啦");
                    }
                    String origFileName = file.getOriginalFilename();
                    String fileType = getFileType(origFileName);
                    boolean isMatch = false;
                    for (String allowType : config.getAllowType()) {
                        if (CheckUtil.isEmpty(fileType)) {
                            break;
                        }
                        if (allowType.equalsIgnoreCase(fileType)) {
                            isMatch = true;
                            break;
                        }
                        continue;
                    }
                    if (!isMatch) {
                        log.info("文件类型:" + (CheckUtil.isEmpty(fileType) ? "未获取到" : fileType));
                        throw new ApiBizException(MsgCode.C00000001.code, "上传失败，不支持的文件类型");
                    }
                    String randomFileName = RandomUtil.genLetterLowerNumStr(23);
                    String datePath = File.separator + key + File.separator + (CheckUtil.isEmpty(dateDir) ? dateDir : dateDir + File.separator);
                    String savePath = config.getDir() + datePath;
                    File savePathFile = new File(savePath);
                    if (!savePathFile.exists()) {
                        savePathFile.mkdirs();
                    }
                    String fileName = randomFileName + Constants.STR_DOT + fileType;
                    String filePath = savePath + fileName;
                    try {
                        file.transferTo(new File(filePath));
                    } catch (IOException e) {
                        log.error("文件key【" + file.getName() + "】:" + filePath + " 写入失败");
                        throw new ApiBizException(MsgCode.C00000001.code, "上传失败，文件内容处理错误");
                    }
                    //业务处理
                    JSONObject retJo = new JSONObject();
                    callback.call(filePath, retJo);
                    return retJo;
                }
            } else {
                log.error("配置文件上传key【" + key + "】:配置不存在");
            }
        }
        throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
    }

    /**
     * 处理上传 多文件上传公共方法 未插入业务逻辑
     * @param multipartHttpServletRequest
     * @param key                         配置key
     * @return
     */
    public JSON handle(JSONObject reqJo, StandardMultipartHttpServletRequest multipartHttpServletRequest, String key,Callback callback) throws Exception {
        //获取配置信息
        Map<String, UploadConf.Config> configMap = uploadConf.getConfigs();
        if (configMap != null && !configMap.isEmpty() && configMap.containsKey(key)) {
            UploadConf.Config config = configMap.get(key);
            if (config.check()) {
                boolean isSingleFile = false;
                if (config.getNum() == 1) {//single file upload
                    isSingleFile = true;
                }
                JSONArray jsonFiles = new JSONArray(config.getNum());
                Map<String, MultipartFile> multipartFileMap = multipartHttpServletRequest.getFileMap();
                Set<Map.Entry<String, MultipartFile>> entrySet = multipartFileMap.entrySet();
                Iterator<Map.Entry<String, MultipartFile>> iterable = entrySet.iterator();
                int currentFilePoint = 0;
                Date now = new Date();
                while (iterable.hasNext()) {
                    ++currentFilePoint;
                    if (currentFilePoint <= config.getNum()) {
                        JSONObject jsonFile = new JSONObject();
                        Map.Entry<String, MultipartFile> currentEntry = iterable.next();
                        MultipartFile multipartFile = currentEntry.getValue();
                        if (!isSingleFile) {
                            jsonFile.put("key", currentEntry.getKey());
                        }
                        if (null != multipartFile && !multipartFile.isEmpty()) {
                            //校验与写入文件
                            String dateDir = Constants.STR_BLANK;
                            if (config.isDateDir()) {
                                dateDir = DateUtil.convertDate2Str(now, patten);
                            }
                            if (multipartFile.getSize() > config.getMaxSize()) {
                                log.info("文件大小超过限制");
                                String errorMsg = "上传失败,文件太大啦";
                                if (isSingleFile) {
                                    throw new ApiBizException(MsgCode.C00000001.code, errorMsg);
                                }
                                jsonFile.put("status", 0);
                                jsonFile.put("errorMsg", errorMsg);
                                jsonFile.put("path", Constants.STR_BLANK);
                                jsonFiles.add(jsonFile);
                                continue;
                            }
                            String origFileName = multipartFile.getOriginalFilename();
                            String fileType = getFileType(origFileName);
                            boolean isMatch = false;
                            for (String allowType : config.getAllowType()) {
                                if (CheckUtil.isEmpty(fileType)) {
                                    break;
                                }
                                if (allowType.equalsIgnoreCase(fileType)) {
                                    isMatch = true;
                                    break;
                                }
                                continue;
                            }
                            if (!isMatch) {
                                log.info("文件类型:" + (CheckUtil.isEmpty(fileType) ? "未获取到" : fileType));
                                String errorMsg = "上传失败,不支持的文件类型";
                                if (isSingleFile) {
                                    throw new ApiBizException(MsgCode.C00000001.code, errorMsg);
                                }
                                jsonFile.put("status", 0);
                                jsonFile.put("errorMsg", errorMsg);
                                jsonFile.put("path", Constants.STR_BLANK);
                                jsonFiles.add(jsonFile);
                                continue;
                            }
                            String randomFileName = RandomUtil.genLetterLowerNumStr(23);
                            String datePath = File.separator + key + File.separator + (CheckUtil.isEmpty(dateDir) ? dateDir : dateDir + File.separator);
                            String savePath = config.getDir() + datePath;
                            File savePathFile = new File(savePath);
                            if (!savePathFile.exists()) {
                                savePathFile.mkdirs();
                            }
                            String fileName = randomFileName + Constants.STR_DOT + fileType;
                            String filePath = savePath + fileName;
                            try {
                                multipartFile.transferTo(new File(filePath));
                            } catch (IOException e) {
                                log.error("文件key【" + currentEntry.getKey() + "】:" + filePath + " 写入失败");
                                String errorMsg = "上传失败,文件内容处理错误";
                                if (isSingleFile) {
                                    throw new ApiBizException(MsgCode.C00000001.code, errorMsg);
                                }
                                jsonFile.put("status", 0);
                                jsonFile.put("errorMsg", errorMsg);
                                jsonFiles.add(jsonFile);
                                continue;
                            }
                            jsonFile.put("path", filePath);

                            //业务处理
                            callback.call(filePath,jsonFile);

                            if (isSingleFile) {
                                return jsonFile;
                            }
                            jsonFile.put("status", 1);
                            jsonFile.put("errorMsg", Constants.STR_BLANK);
                            jsonFiles.add(jsonFile);
                        } else {
                            String errorMsg = "上传失败,文件不存在或内容不存在";
                            if (isSingleFile) {
                                throw new ApiBizException(MsgCode.C00000001.code, errorMsg);
                            }
                            jsonFile.put("path", Constants.STR_BLANK);
                            jsonFile.put("status", 0);
                            jsonFile.put("errorMsg", errorMsg);
                            jsonFiles.add(jsonFile);
                        }
                    }
                    return jsonFiles;
                }
            } else {
                log.error("配置文件上传key【" + key + "】:配置不存在");
            }
        }
        throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
    }

    /**
     * 获取文件类型后缀
     *
     * @param fileName
     * @return
     */
    private String getFileType(String fileName) {
        if (CheckUtil.isNotEmpty(fileName)) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex < 1 || fileName.length() <= ++dotIndex) {
                return null;
            }
            return fileName.substring(dotIndex);
        }
        return null;
    }

    /**
     * base64格式文件上传
     *
     * @param reqJo
     * @param key
     * @return
     */
    public JSONObject handle(JSONObject reqJo, String key,Callback callback) throws Exception {
        Map<String, UploadConf.Config> configMap = uploadConf.getConfigs();
        if (configMap != null && !configMap.isEmpty() && configMap.containsKey(key)) {
            UploadConf.Config config = configMap.get(key);
            if (config.check()) {
                //取得文件数据
                String base64Data = reqJo.getString(key);
                if (CheckUtil.isEmpty(base64Data)) {
                    throw new ApiBizException(MsgCode.C00000001.code, "上传失败,文件不存在或内容不存在");
                }
                String[] data = base64Data.split("base64,");
                //base64文件类型
                String fileBase64Type = null;
                //base64文件内容
                String fileBase64Content = null;
                if (data.length == 2) {
                    fileBase64Type = data[0];
                    fileBase64Content = data[1];
                }else {
                    //兼容app上传默认格式png
                    fileBase64Type = "data:image/png;";
                    fileBase64Content = data[0];
                }

                //确定文件类型
                String fileType = null;
                if (typeMapping.containsKey(fileBase64Type)) {
                    if (config.getAllowType().contains(typeMapping.get(fileBase64Type))) {
                        fileType = typeMapping.get(fileBase64Type);
                    }
                }
                if (CheckUtil.isEmpty(fileType)) {
                    throw new ApiBizException(MsgCode.C00000001.code, "上传失败,不支持的文件类型");
                }
                //处理文件内容
                byte[] content = Base64Utils.decodeFromString(fileBase64Content);

                if (content.length < 1) {
                    throw new ApiBizException(MsgCode.C00000001.code, "上传失败,文件内容处理错误");
                }

                //生成文件名
                String randomFileName = RandomUtil.genLetterLowerNumStr(23);
                //生成文件保存目录
                String dateDir = Constants.STR_BLANK;
                if (config.isDateDir()) {
                    dateDir = DateUtil.convertDate2Str(new Date(), patten);
                }
                String datePath = File.separator + key + File.separator + (CheckUtil.isEmpty(dateDir) ? dateDir : dateDir + File.separator);
                String savePath = config.getDir() + datePath;

                File savePathFile = new File(savePath);
                if (!savePathFile.exists()) {
                    savePathFile.mkdirs();
                }
                String fileName = randomFileName + Constants.STR_DOT + fileType;
                String filePath = savePath + fileName;
                File file = new File(filePath);
                try {
                    FileUtils.writeByteArrayToFile(file, content);
                } catch (IOException e) {
                    log.info("文件写入失败", e);
                    throw new ApiBizException(MsgCode.C00000001.code, "上传失败,文件内容处理错误");
                }
                if (file.length() > config.getMaxSize()) {
                    log.info("文件大小超过限定大小");
                    log.info("删除文件【" + filePath + "】");
                    if (!file.delete()) {
                        log.info("文件删除失败!打上删除标识");
                        String renameFilePath = savePath + randomFileName + "_delete" + Constants.STR_DOT + fileType;
                        if (!file.renameTo(new File(renameFilePath))) {
                            log.error("文件重命名失败:【" + renameFilePath + "】");
                        }
                    }
                    throw new ApiBizException(MsgCode.C00000001.code, "上传失败,文件太大啦");
                }
                String dataBasePath = datePath + fileName;
                if (!callback.call(dataBasePath,null)) {//业务逻辑
                    log.info("头像更新失败");
                    log.info("删除文件【" + filePath + "】");
                    if (!file.delete()) {
                        log.info("文件删除失败!打上删除标识");
                        String renameFilePath = savePath + randomFileName + "_delete" + Constants.STR_DOT + fileType;
                        if (!file.renameTo(new File(renameFilePath))) {
                            log.error("文件重命名失败:【" + renameFilePath + "】");
                        }
                    }
                    throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
                }
                JSONObject retJo = new JSONObject();
                retJo.put("url", ApplicationContextUtil.domainWithContext + File.separator + "resource" + dataBasePath);
                return retJo;
            }
        }
        throw new ApiBizException(MsgCode.C00000999.code, MsgCode.C00000999.msg);
    }

    /**
     * 插入业务逻辑
     */
    public interface Callback {
        boolean call(String dataBasePath,JSONObject jo) throws Exception;
    }

}
