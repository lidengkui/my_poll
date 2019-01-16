package com.poll.common.util;

import java.util.Random;

public class RandomUtil {
	
	public static final char[] allCharArr = {'0','1','2','3','4','5','6','7','8','9',
		  'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		  'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
		  '`','~','!','@','#','$','%','^','&','*','(',')','-','_','=','+','[','{',']','}'/*,'\\'*/,'|',';',':','\'','"',
		  ',','<','.','>','/','?'
		  };
	
	public static final char[] letterNumCharArr = {'0','1','2','3','4','5','6','7','8','9',
										  'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
										  'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	public static final char[] letterCharLowerNumberArr = {'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	public static final char[] letterCharArr = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		  										'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	public static final char[] letterCharLowerArr = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	public static final char[] letterCharUpperArr = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	public static final char[] numberCharArr = {'0','1','2','3','4','5','6','7','8','9'};
	

	/**
	 * 根据指定的char数组库   随机生成字符串
	 * @param length
	 * @param charArr
	 * @return
	 */
	public static String genStrByCharArr(int length, char[] charArr) {
		
		if (length < 1) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		Random random = new Random();
		int len = charArr.length;
		for (int i = 0; i < length; i++) {
			sb.append(charArr[random.nextInt(len)]);
		}
		
		return sb.toString();
	}
	
	/**
	 * 返回一个定长的随机字符串(包含所有键盘字符)
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genAllCharStr(int length) {
		return genStrByCharArr(length, allCharArr);
	}
	
	/**
	 * 返回一个定长的随机字符串(包含大小写字母、数字)
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genLetterNumStr(int length) {
		return genStrByCharArr(length, letterNumCharArr);
	}
	
	public static String genLetterLowerNumStr(int length) {
		return genStrByCharArr(length, letterCharLowerNumberArr);
	}

	/**
	 * 返回一个定长的随机纯字母字符串(只包含大小写字母)
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genLetterStr(int length) {
		return genStrByCharArr(length, letterCharArr);
	}

	/**
	 * 返回一个定长的随机纯小写字母字符串
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genLetterLowerStr(int length) {
		return genStrByCharArr(length, letterCharLowerArr);
	}

	/**
	 * 返回一个定长的随机纯大写字母字符串
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genLetterUpperStr(int length) {
		return genStrByCharArr(length, letterCharUpperArr);
	}

	
	/**
	 * 返回一个定长的随机数字字符串
	 * 
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String genNumberStr(int length) {
		return genStrByCharArr(length, numberCharArr);
	}
	
	/**
	 * 生成0 至 n之前的随机数，包含n   n可以为负， 为负则生成负数
	 * @param n
	 * @return
	 */
	public static int genNumberLessOrEq(int n) {
		
		boolean isLes0 = n < 0 ? true : false;
		
		n = Math.abs(n);
		
		//取绝对值后如果小于1直接返回0
		if (n < 1) {
			return 0;
		}
		
		int num = new Random().nextInt(n + 1);
		
		//若n为负则返回负值
		return isLes0 ? -num : num;
	}
	
	/**
	 * 生成begin 至 end 之间的随机整数     包含 begin 与 end  可以小于0
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int genNumberBtw(int begin, int end) {
		
		//调整begin与end大小顺序
		if (begin == end) {
			return begin;
		} else if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		
		//计算起始值与0的差值
		int diff = 0 - begin;
		
		//将随机范围偏移至0起始
		int num = new Random().nextInt(end + diff + 1);
		
		//还原偏移
		return num - diff;
	}
	
	/**
	 * 指定平均值，生成随机数
	 * @param avg		随机数平均值
	 * @param num		生成随机数数量
	 * @param min		随机数允许的最小值
	 * @param max		随机数允许的最大值
	 * @return
	 */
	public static int[] genRdmsAtAvg(int avg, int num, int min, int max) {
		
		if (min < 0 || max < 0) {
			throw new RuntimeException("最大最小值不能小于0");
		}

		if (num < 1) {
			throw new RuntimeException("生成数量不能小于1");
		}
		
		if (avg < min || avg > max) {
			throw new RuntimeException("平均数未在最大最小值之间");
		}
		
		//结果
		int[] result = new int[num];

		//此处对于数量为1的判断可略去
		if (num == 1) {
			result[0] = avg;
			return result;
		}
		
		//总量
		int total = avg * num;
		
		int rdmBase = max - min + 1;
		
		//向上调整空间
		int topSpace = 0;
		//向下调整空间
		int bottomSpace = 0;
		
		Random random = new Random();
		
		for (int i = 0; i < num; i ++) {
			
			//生成随机数
			int rdm = total <= 0 ? min : min + random.nextInt(rdmBase);
			
			total -= rdm;
			result[i] = rdm;
			
			topSpace += max - rdm;
			bottomSpace += rdm - min;
		}
		
		//最后数值进行调整
		if (total > 0) {
			for (int i = 0; i < result.length && total > 0; i ++) {
				if (max != result[i]) {
					
					int tjfy = max - result[i];
					topSpace -= tjfy;
					if (tjfy > total) {
						tjfy = total;
					}
					
					int tj = 1 + random.nextInt(tjfy);
					total -= tj;
					result[i] += tj;
					
					if (topSpace < total) {
						result[i] += total - topSpace;
						total = topSpace;
					}
				}
			}
		} else if (total < 0) {
			
			total = -total;
			for (int i = 0; i < result.length && total > 0; i ++) {
				if (min != result[i]) {
					
					int tjfy = result[i] - min;
					bottomSpace -= tjfy;
					if (tjfy > total) {
						tjfy = total;
					}
					
					int tj = 1 + random.nextInt(tjfy);
					total -= tj;
					result[i] -= tj;
					
					if (bottomSpace < total) {
						result[i] -= total - bottomSpace;
						total = bottomSpace;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 生成指定平均数的随机数，且随机数之间的差值不超过differNotMoreThan
	 * @param avg
	 * @param num
	 * @param differNotMoreThan
	 * @return
	 */
	public static int[] genRdmsAtAvg(int avg, int num, int differNotMoreThan) {
		
		if (avg < 0) {
			throw new RuntimeException("平均值不能小于0");
		}

		if (num < 1) {
			throw new RuntimeException("生成数量不能小于1");
		}
		
		if (differNotMoreThan < 0) {
			throw new RuntimeException("容差不能小于0");
		}
		
		int min = avg - differNotMoreThan;
		if (min < 0) {
			throw new RuntimeException("随机数下限不能小于0");
		}
		
		int rdmMin = min + new Random().nextInt(differNotMoreThan);
		int rdmMax = rdmMin + differNotMoreThan;
		
		if (rdmMin == avg && rdmMax - 1 > avg) {
			rdmMin --;
			rdmMax --;
		} else if (rdmMax == avg && rdmMin + 1 < avg) {
			rdmMin ++;
			rdmMax ++;
		}
		
		return genRdmsAtAvg(avg, num, rdmMin, rdmMax);
	}
	
	public static void main(String[] args) {

		for (int i = 0; i < 20; i ++) {
			System.out.println(genAllCharStr(16));
		}

		for (int i = 0; i < 100; i++) {
//			int[] genRdmIntAtAvg = genRdmsAtAvg(5, 7, 1, 10);
//			
//			int total = 0;
//			for (int j = 0; j < genRdmIntAtAvg.length; j++) {
//				System.out.print(genRdmIntAtAvg[j] + "  ");
//				total += genRdmIntAtAvg[j];
//			}
//			System.out.println(total);
//			System.out.println("===============================");
			
			int[] genRdmIntAtAvg2 = genRdmsAtAvg(10, 20, 4);
			
			int total2 = 0;
			for (int j = 0; j < genRdmIntAtAvg2.length; j++) {
				System.out.print(genRdmIntAtAvg2[j] + "  ");
				total2 += genRdmIntAtAvg2[j];
			}
			System.out.println(total2);
			System.out.println("===============================");
			

//			if (total != 40) {
//				
//				for (int j = 0; j < genRdmIntAtAvg.length; j++) {
//					System.out.print(genRdmIntAtAvg[j] + "  ");
//				}
//				System.out.println(total);
//				System.out.println("===============================");
//			}
		}
		System.out.println("end");
		
	}
	
}
