package com.poll.common.util;

import com.poll.common.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;


public class FileUtil {

	public static final int bufferSize = 4800;
	
	/**
	 * 删除文件  只读也可删除
	 * 
	 * 若文件为null	返回false
	 * 若文件不存在		返回true
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		
		if (file == null) {
			return false;
		}
		
		if (file.exists()) {
			return file.delete();
		}
		
		//若文件不存在，默认删除失败，返回false
		return false;
	}
	/**
	 * 删除文件
	 * @param filePath 文件路径
	 */
	public static boolean deleteFile(String filePath) {

		// 获取文件
		File tmpFile = new File(filePath);
		
		return deleteFile(tmpFile);
	}
	
	/**
	 * 删除目录以及其包含的所有内容
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
        	String[] children = dir.list();
        	//递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

	/**
	 * 返回文件的大小,byte单位
	 * @return
	 */
	public static long getFileSizeInBytes(String filePath) {
		
		return new File(filePath).length();
	}
	public static long getFileSizeInBytes(File file) {
		
		return file.length();
	}

	/**
	 * 返回文件大小，kb单位
	 * @param file
	 * @return
	 */
	public static BigDecimal getFileSizeInKB(File file) {
		
		BigDecimal len = new BigDecimal(file.length());
		
		return DecimalFormatUtil.format2BigDecimal2(len.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP));
	}
	public static BigDecimal getFileSizeInKB(String filePath) {
		
		return getFileSizeInKB(new File(filePath));
	}
	
	/**
	 * 返回文件大小，Mb单位
	 * @param file
	 * @return
	 */
	public static BigDecimal getFileSizeInMB(File file) {
		
		BigDecimal len = new BigDecimal(file.length());
		
		return DecimalFormatUtil.format2BigDecimal2(len.divide(new BigDecimal(1048576), 2, BigDecimal.ROUND_HALF_UP));
	}
	public static BigDecimal getFileSizeInMB(String filePath) {
		
		return getFileSizeInMB(new File(filePath));
	}
	
	/**
	 * 返回文件大小，Gb单位
	 * @param file
	 * @return
	 */
	public static BigDecimal getFileSizeInGB(File file) {
		
		BigDecimal len = new BigDecimal(file.length());
		
		return DecimalFormatUtil.format2BigDecimal2(len.divide(new BigDecimal(1073741824), 2, BigDecimal.ROUND_HALF_UP));
	}
	public static BigDecimal getFileSizeInGB(String filePath) {
		
		return getFileSizeInGB(new File(filePath));
	}
	
	/**
	 * 返回文件大小，Tb单位
	 * @param file
	 * @return
	 */
	public static BigDecimal getFileSizeInTB(File file) {
		
		BigDecimal len = new BigDecimal(file.length());
		
		return DecimalFormatUtil.format2BigDecimal2(len.divide(new BigDecimal(1099511627776l), 2, BigDecimal.ROUND_HALF_UP));
	}
	public static BigDecimal getFileSizeInTB(String filePath) {
		
		return getFileSizeInTB(new File(filePath));
	}
	
	
	/**
     * 创建一个不重复的文件
     * 
     * @param path			  	文件所在路径
     * @param name				文件名称
     * @param suffix			文件后缀
     * @param replaceIfExists  	是否替换原文件， true 文件已经存在，则直接删除原文件，然后再建立新文件, false 创建不重复文件
     * @return
     */
    public static File createFile(String path, String name, String suffix, boolean replaceIfExists) {
    	
    	File file = null;

    	suffix = StringUtil.trimStr(suffix).replace(".", "");
    	
    	if(suffix.equals("")) {
    		file = new File(path, name);
    	} else {
    		file = new File(path, name + "." + suffix);
    	}

    	//创建文件父路径
    	if (!file.getParentFile().exists()) {
    		file.getParentFile().mkdirs();
		}

    	//若替换原文件
    	if (file.exists() && replaceIfExists) {
			file.delete();
		}
    	
    	//创建文件
    	int index = 1;
    	try {
			while(!file.createNewFile()) {
				if(suffix.equals("")) {
					file = new File(path, name + "(" + index + ")");
				} else {
					file = new File(path, name + "(" + index + ")." + suffix);
				}
				index ++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return file;
    }
    public static File createFile(String path, String name, String suffix) {
    	return createFile(path, name, suffix, false);
    }

	public static File createFile(File file, boolean replaceIfExists) {

		String[] pathNameSuffixArr = getPathNameSuffixArr(file);

		return createFile(pathNameSuffixArr[0], pathNameSuffixArr[1], pathNameSuffixArr[2], replaceIfExists);
	}
	public static File createFile(File file) {
		return createFile(file, false);
	}
	
	/**
	 * 通过file取得，父路径，文件名，文件后缀, 字符串数组
	 * 
	 * 后缀名包含.
	 * 
	 * @param file
	 * @return
	 */
	public static String[] getPathNameSuffixArr(File file) {
		
		String parentPath = null;
		try {
			parentPath = file.getParentFile().getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
		
		String name = file.getName();

		int pointIndex = name.lastIndexOf(".");
		
		String suffix = "";
		
		if (pointIndex != -1) {
			
			String bakname = name;
			name = name.substring(0, pointIndex);
			
			suffix = bakname.substring(pointIndex);
		}
		
		String[] returnArr = {parentPath, name, suffix};
		
		return returnArr;
	}
	
	/**
	 * 将流保存至文件
	 * @param is
	 * @param path
	 * @param name
	 * @param suffix
	 * @param bufferSize
	 * @return
	 * @throws Exception
	 */
	public static File save2File(InputStream is, String path, String name, String suffix, int bufferSize) throws Exception {
		
		File file = createFile(path, name, suffix);
		FileOutputStream fos = new FileOutputStream(file);
		
		int len = -1;
		byte[] b = new byte[bufferSize];
		
		while ((len = is.read(b, 0, bufferSize)) > 0) {
			fos.write(b, 0, len);
		}
		
		fos.close();
		is.close();
		
		return file;
	}
	public static File save2File(InputStream is, String path, String name, String suffix) throws Exception {
		
		return save2File(is, path, name, suffix, bufferSize);
	}
	public static File save2File(InputStream is, File file) throws Exception {
		
		String[] pathNameSuffixArr = getPathNameSuffixArr(file);
		
		return save2File(is, pathNameSuffixArr[0], pathNameSuffixArr[1], pathNameSuffixArr[2]);
	}
	
	
	/**
	 * 复制文件
	 * @param sourceFile
	 * @param targetFile  只需要file对象，调用前不需要创建file
	 * @return
	 * @throws Exception 
	 */
	public static File copyFile(File sourceFile, File targetFile) throws Exception {
		
		return save2File(new FileInputStream(sourceFile), targetFile);
	}
	/**
	 * 将源文件复制至目标路径
	 * 
	 * 若传入文件路径，则将源文件复制至该文件同路径
	 * 
	 * @param sourceFile
	 * @param dirStr
	 * @return
	 * @throws Exception
	 */
	public static File copyFile2Dir(File sourceFile, String dirStr) throws Exception {
		
		String[] sourceFileArr = getPathNameSuffixArr(sourceFile);
		
		//如果传入的dirStr为文件，则截取文件父路径
		File dirFile = new File(dirStr);
		if (dirFile.isFile()) {
			String[] dirFileArr = getPathNameSuffixArr(dirFile);
			dirStr = dirFileArr[0];
		}

		File targetFile = new File(dirStr + "/" +sourceFileArr[1] + sourceFileArr[2]);
		
		return copyFile(sourceFile, targetFile);
	}
	
	/**
	 * 按字节拷贝文件
	 * @param sourceFile		源文件
	 * @param targetFile		目标文件
	 * @param ignoreBytes		忽略开头字节数
	 * @param copyBytes			复制字节数		若小于0，则复制至文件末尾
	 * @param bufferSize		使用缓冲大小
	 * @return
	 * @throws Exception
	 */
	public static File copyFileBytes(File sourceFile, File targetFile, int ignoreBytes, int copyBytes, int bufferSize) throws Exception {
		
		File createFile = createFile(targetFile);
		
		if (copyBytes > 0) {
			FileInputStream fis = new FileInputStream(sourceFile);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			//缓冲数组
			byte[] b = new byte[bufferSize];
			
			//处理忽略字节
			if (ignoreBytes > 0) {
				
				int readTime = ignoreBytes / bufferSize;
				int readMod = ignoreBytes % bufferSize;
				
				for (int i = 0 ; i < readTime; i ++) {
					fis.read(b, 0, b.length);
				}
				
				fis.read(b, 0, readMod);
			}
			
			//复制文件内容
			int curSize = 0;
			int len = 0;
			while ((len = fis.read(b , 0, bufferSize)) > 0) {
				
				//总读取长度
				curSize += len;
				
				if (copyBytes > 0 && curSize >= copyBytes) {
					bos.write(b, 0, len - (curSize - copyBytes));
					break;
				}
				
				bos.write(b, 0, len);
			}
			
			bos.close();
			fis.close();
		}
		return createFile;
	}
	public static File copyFileBytes(File sourceFile, File targetFile, int ignoreBytes, int copyBytes) throws Exception {
		return copyFileBytes(sourceFile, targetFile, ignoreBytes, copyBytes, bufferSize);
	}
	
	/**
	 * 从头部按行复制文本文件
	 * @param sourceFile
	 * @param targetFile
	 * @param ignoreLines		忽略文件头部行数
	 * @param copyLines		  	复制行数
	 * @return
	 * @throws Exception
	 */
	public static File copyFileLines(File sourceFile, File targetFile, int ignoreLines, int copyLines, String charsetName) throws Exception {
		
		File createFile = createFile(targetFile);
		
		if (copyLines > 0) {
			
			BufferedReader br = null;
			BufferedWriter bw = null;
			
			if (charsetName != null) {
				br =  new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), charsetName));
				bw =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), charsetName));
			} else {
				br = new BufferedReader(new FileReader(sourceFile));
				bw = new BufferedWriter(new FileWriter(targetFile));
			}
			
			//处理跳行
			for (int i = 1; i <= ignoreLines; i++) {
				br.readLine();
			}
			
			//复制文件
			for (int i = 1; i <= copyLines; i++) {
				String readLine = br.readLine();
				if (readLine == null) {
					break;
				}
				bw.write(readLine + "\r\n");
			}
			
			br.close();
			bw.close();
		}
		return createFile;
	}
	
	/**
	 * 移动文件
	 * @param sourceFile
	 * @param dirStr
	 * @return
	 * @throws Exception 
	 */
	public static File moveFile2Dir(File sourceFile, String dirStr) throws Exception {
		
		//复制文件至目标目录
		File targetFile = copyFile2Dir(sourceFile, dirStr);
		
		//删除源文件
		deleteFile(sourceFile);
		
		return targetFile;
	}
	
	/**
	 * 备份文件 文件名后加.bak
	 * @param sourceFile
	 * @return
	 * @throws Exception
	 */
	public static File backupFile(File sourceFile) throws Exception {
		
		File targetFile = new File(sourceFile.getCanonicalPath() + ".bak");
		return copyFile(sourceFile, targetFile);
	}
	
	/**
	 * 还原文件
	 * 
	 * 备份文件存在时， 将备份文件与源文件互换
	 * 不存在时，返回源文件
	 * 
	 * @param sourceFile
	 * @return
	 * @throws Exception
	 */
	public static File recoverFile(File sourceFile) throws Exception {
		
		String sourceFileStr = sourceFile.getCanonicalPath();
		String bakFileStr = sourceFileStr + ".bak";
		String sourceFileTmpStr = sourceFileStr + ".tmp";
		
		File bakFile = new File(bakFileStr);
		File tmpFile = new File(sourceFileTmpStr);
		
		//备份文件存在   并且  备份源文件成功
		if (bakFile.exists()) {
			
			tmpFile = copyFile(sourceFile, tmpFile);
			
			//源文件与备份文件互换
			if (sourceFile.delete()) {
				copyFile(bakFile, sourceFile);
				
				if (bakFile.delete()) {
					copyFile(tmpFile, bakFile);
				}
			}
		}
		//删除临时文件
		tmpFile.deleteOnExit();
		
		return sourceFile;
	}


	/**
	 * 读取文本文件内容为字符串
	 * 
	 * 注：readLine方法每行最多8192个字符，若一行字符数超过该值，则输出时会强制换行，可能造成与源文本不一致
	 * 
	 * @param file		
	 * @param skipLineNum		跳过文件头行数
	 * @param lineNum			最大读取文件行数  小于0则不限制
	 * @return
	 * @throws Exception 
	 */
	public static String getFileContentByReadLine(File file, int skipLineNum, int lineNum, String charsetName) throws Exception {

		if (skipLineNum < 0) {
			skipLineNum = 0;
		}
		
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = null;
		if (charsetName == null) {
			br = new BufferedReader(new FileReader(file));
		} else {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
		}
		
		String line = null;
		
		//处理跳行
		for (int i = 1; i <= skipLineNum; i++) {
			br.readLine();
		}
		
		if (lineNum < 0) {
			while ((line = br.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} else {
			for (int i = 1; i <= lineNum; i++) {
				
				line = br.readLine();
				
				if (line == null) {
					break;
				}
				
				sb.append(line + "\r\n");
			}
		}
		
		br.close();
		
		return sb.toString();
	}
	public static String getFileContentByReadLine(File file, String charsetName) throws Exception {
		return getFileContentByReadLine(file, 0, -1, charsetName);
	}
	public static String getFileContentByReadLine(File file) throws Exception {
		return getFileContentByReadLine(file, 0, -1, "utf8");
	}
	
    /**
     * 读取全部文本至byte数组，然后将byte数组转为charsetName编码格式的字符串输出
     * @param file
     * @param ignoreBytes		跳过文件头大小
     * @param readBytes			读取最大限制
     * @param charsetName
     * @param bufferSize		缓冲区大小 最好设为4800 或 2*3*4的倍数
     * @return
     * @throws Exception
     */
	public static String getFileContentByReadBytes(File file, int ignoreBytes, int readBytes, String charsetName, int bufferSize) throws Exception {

		if (readBytes > 0) {
			FileInputStream fis = new FileInputStream(file);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			//缓冲数组
			byte[] b = new byte[bufferSize];
			
			//处理忽略字节
			if (ignoreBytes > 0) {
				
				int readTime = ignoreBytes / bufferSize;
				int readMod = ignoreBytes % bufferSize;
				
				for (int i = 0 ; i < readTime; i ++) {
					fis.read(b, 0, b.length);
				}
				
				fis.read(b, 0, readMod);
			}
			
			int curSize = 0;
			int len = 0;
			while ((len = fis.read(b , 0, bufferSize)) > 0) {
				
				//总读取长度
				curSize += len;
				
				if (readBytes > 0 && curSize >= readBytes) {
					bos.write(b, 0, len - (curSize - readBytes));
					break;
				}
				
				bos.write(b, 0, len);
			}
			
			bos.close();
			fis.close();
			
			return new String(bos.toByteArray(), charsetName);
		}
		return "";
	}
	public static String getFileContentByReadBytes(File file, String charsetName) throws Exception {
		return getFileContentByReadBytes(file, 0, -1, charsetName, bufferSize);
	}
	public static String getFileContentByReadBytes(File file) throws Exception {
		return getFileContentByReadBytes(file, 0, -1, Constants.CHARSET_UTF8, bufferSize);
	}
	
	/**
	 * 统计csv文件有效记录数
	 * @param file
	 * @param charsetName
	 * @param ignoreLineNum
	 * @return
	 */
	public static int countEffectiveLine(File file, String charsetName, int ignoreLineNum) {
		
		int totalRecord = 0;
		int lineNum = 0;
		
		//输入流
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
			String line = null;
			while ((line = br.readLine()) != null) {
				
				lineNum ++;
				
				if (lineNum <= ignoreLineNum) {
					continue;
				}
				
				if (line.equals(Constants.STR_BLANK)) {
					continue;
				}
				
				//记录数增加
				totalRecord ++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return totalRecord;
	}
	
}
