package com.poll.redis;

import com.poll.common.Constants;
import com.poll.common.MsgCode;
import com.poll.common.exception.ApiBizException;
import com.poll.common.exception.RollbackRedisDto;
import com.poll.common.util.RandomUtil;
import com.poll.common.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private StringRedisSerializer stringRedisSerializer;
	
	@Autowired
	private JdkSerializationRedisSerializer jdkSerializationRedisSerializer;

	@Autowired
	private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer;

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public StringRedisSerializer getStringRedisSerializer() {
		return stringRedisSerializer;
	}

	public JdkSerializationRedisSerializer getJdkSerializationRedisSerializer() {
		return jdkSerializationRedisSerializer;
	}

	public Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
		return jackson2JsonRedisSerializer;
	}

	//redis的key过期时间策略
    @AllArgsConstructor
	public static enum ExpireAtType {

        LESS_THAN(-1),      //expireAt小于key的过期时间时才设置
        NULL(0),            //过期时间不存在时设置为此过期时间
        GREATER_THAN(1),    //expireAt大于key的过期时间时才设置
        DIRECT_SET(2)       //直接设置为指定过期时间
        ;
        @Getter
        private int value;
    }


	/**
	 * 请求频繁控制
	 * @param key
	 * @param expireMills 过期时间 毫秒单位
	 * @param errorCode
	 * @param errorMsg
	 * @throws Exception
	 */
	public void ifKeyFrequent(String key, long expireMills, String errorCode, String errorMsg) throws Exception {
		
		BoundSetOperations<String,String> boundSetOps = redisTemplate.boundSetOps(key);

		//向set中添加空字符串，若返回结果为0，说明已经存在，此种判断方式经测试效率优于判断key是否存在的方式
		if(boundSetOps.add(Constants.STR_BLANK) == 0){
			//额外判断key是否有过期时间，避免由于异常导致的过期时间未设置而导致key永远不消亡
			Long expire = boundSetOps.getExpire();
			if (expire == null || expire == -1) { //expire 为-2时，key已经不存在，不需要额外再设置过期
                boundSetOps.expire(expireMills, TimeUnit.MILLISECONDS);  // 设置过期
			}
			throw new ApiBizException(errorCode == null ? MsgCode.C00000019.code : errorCode, errorMsg == null ? MsgCode.C00000019.msg : errorMsg);
		}
		boundSetOps.expire(expireMills, TimeUnit.MILLISECONDS);  // 设置过期
	}
	public void ifKeyFrequent(String key, long expireMills) throws Exception {
		ifKeyFrequent(key, expireMills, MsgCode.C00000019.code, MsgCode.C00000019.msg);
	}
	public void ifKeyFrequent(String markCode1, String markCode2, long expireMills) throws Exception {
		ifKeyFrequent(String.format("%s%s:%s", Constants.REDIS_KEY_PREFIX_FREQT, markCode1, markCode2), expireMills);
	}

	/**
	 * key是否存在
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 获取key的有效期
	 * @param key
	 * @return 单位秒
	 */
	public long expire(String key) {
		BoundSetOperations<String, String> boundSetOps = redisTemplate.boundSetOps(key);
		return boundSetOps.getExpire();
	}

	/**
	 * 设置值
	 * @param key
	 * @param value
	 * @param expireMills
	 */
	public void setValue(String key, String value, long expireMills) {

		BoundValueOperations<String,String> boundValueOps = redisTemplate.boundValueOps(key);
		boundValueOps.set(value, expireMills, TimeUnit.MILLISECONDS);
	}

	/**
	 * 取值
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		
		return redisTemplate.boundValueOps(key).get();
	}

	/**
	 * 取值并通过指定序列化对象反序列化
	 * @param key
	 * @param valueSerializer
	 * @return
	 */
	public Object getValue(String key, RedisSerializer<?> valueSerializer) {
		
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		Object value = valueSerializer.deserialize(connection.get(key.getBytes()));
		connection.close();
		
		return value;
	}

	/**
	 * 删除指定key
	 * @param key
	 */
	public void delKey(String key) {
		
		redisTemplate.delete(key);
	}

	/**
	 * 删除多个key
	 * @param keys
	 */
	public void delKeys(Collection<String> keys) {
		
		redisTemplate.delete(keys);
	}

	/**
	 * 通过pattern删除key
	 * @param keyPattern
	 */
	public void delKeyPattern(String keyPattern) {
		
		redisTemplate.delete(redisTemplate.keys(keyPattern));
	}

	/**
	 * 从左侧将值推入list
	 * @param listKey
	 * @param value
	 * @param listMaxSize	若指定list最大值，则当list超过最大值时，会从右侧移除数据 -1不限制list大小
	 * @param expireAt		可为空
	 */
	public void push2ListLeft(String listKey, String value, int listMaxSize, Date expireAt) {
		
		BoundListOperations<String,String> listOps = redisTemplate.boundListOps(listKey);
		
		Long size = listOps.leftPush(value);

		if (isNeedSetExpireAt(listOps.getExpire(), expireAt, ExpireAtType.NULL)) {
		    listOps.expireAt(expireAt);
        }

		//如果队列已满，从右侧移除旧数据
		if (listMaxSize > 0 && size > listMaxSize) {
			listOps.rightPop();
		}
	}

	/**
	 * 从右侧将值推入list
	 * @param listKey
	 * @param value
	 * @param listMaxSize	若指定list最大值，则当list超过最大值时，会从左侧移除数据 -1不限制list大小
	 * @param expireAt
	 */
	public void push2ListRight(String listKey, String value, int listMaxSize, Date expireAt) {
		
		BoundListOperations<String,String> listOps = redisTemplate.boundListOps(listKey);
		
		Long size = listOps.rightPush(value);

        if (isNeedSetExpireAt(listOps.getExpire(), expireAt, ExpireAtType.NULL)) {
            listOps.expireAt(expireAt);
        }

		//如果队列已满，从右侧移除旧数据
		if (listMaxSize > 0 && size > listMaxSize) {
			listOps.leftPop();
		}
	}

	/**
	 * 从左侧将值全部推入list
	 * @param listKey
	 * @param valueList
	 * @param expireAt
	 */
	public void pushAll2ListLeft(String listKey, List<String> valueList, Date expireAt) {

		if (valueList == null || valueList.size() < 1) {
			return;
		}

		BoundListOperations<String,String> listOps = redisTemplate.boundListOps(listKey);

		listOps.leftPushAll(valueList.toArray(new String[valueList.size()]));

        if (isNeedSetExpireAt(listOps.getExpire(), expireAt, ExpireAtType.NULL)) {
            listOps.expireAt(expireAt);
        }
	}
	/**
	 * 从右侧将值全部推入list
	 * @param listKey
	 * @param valueList
	 * @param expireAt
	 */
	public void pushAll2ListRight(String listKey, List<String> valueList, Date expireAt) {

		if (valueList == null || valueList.size() < 1) {
			return;
		}

		BoundListOperations<String,String> listOps = redisTemplate.boundListOps(listKey);

		listOps.rightPushAll(valueList.toArray(new String[valueList.size()]));

        if (isNeedSetExpireAt(listOps.getExpire(), expireAt, ExpireAtType.NULL)) {
            listOps.expireAt(expireAt);
        }
	}


	/**
	 * 从指定list中取值
	 * @param listKey
	 * @param size
	 * @return
	 */
	public List<String> getValueFromListLeft(String listKey, int size) {

		return redisTemplate.boundListOps(listKey).range(0, size - 1);
	}

	/**
	 * 从list左侧pop值
	 * @param listKey
	 * @return
	 */
	public String popFromListLeft(String listKey) {

		return redisTemplate.boundListOps(listKey).leftPop();
	}
	/**
	 * 从list右侧pop值
	 * @param listKey
	 * @return
	 */
	public String popFromListRight(String listKey) {
		
		return redisTemplate.boundListOps(listKey).rightPop();
	}

    /**
     *
     * @param key
     * @param addValue
     * @param maxValue      小于0不限制上限
     * @param expireAt
     * @param expireAtType
     * @param upperTips
     * @return
     * @throws Exception
     */
    public long count(String key, int addValue, long maxValue, Date expireAt, ExpireAtType expireAtType, String upperTips) throws Exception {

        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);

        Long result = ops.increment(addValue);

        //设置过期
        if (isNeedSetExpireAt(ops.getExpire(), expireAt, expireAtType)) {
            ops.expireAt(expireAt);
        }

        //判断上限
        if (maxValue < 0) {
            return result;
        }

        //检查是否达到上限
        if (addValue > 0 && result > maxValue) {
            ops.increment(-addValue);
            throw new ApiBizException(MsgCode.C00000030.code, upperTips == null ? MsgCode.C00000030.msg : upperTips, null);
        }

        //若为冲正，则当结果小于0,且非不限量时，控制result不能小于0
        if (addValue < 0 && result < 0) {
            ops.increment(-addValue);
            throw new ApiBizException(MsgCode.C00000030.code, upperTips == null ? MsgCode.C00000030.msg : upperTips, null);
        }

        return result;
    }

	/**
	 * 计数
	 * @param key
	 * @param addValue
	 * @param maxValue		小于0不限制上限
	 * @param expireAt
	 * @param upperTips
	 * @return
	 * @throws Exception
	 */
	public long count(String key, int addValue, long maxValue, Date expireAt, String upperTips) throws Exception {
		
		return count(key, addValue, maxValue, expireAt, ExpireAtType.NULL, upperTips);
	}


	public void rollbackCount(RollbackRedisDto rollbackRedisDto) {
		try {
			count(rollbackRedisDto.getKey(),
					-rollbackRedisDto.getAddValue(),
					-1,
					rollbackRedisDto.getExpireAt(),
					rollbackRedisDto.getUpperTips());
		} catch (Exception e) {
		}
	}

	/**
	 * 去重计数
	 * @param key
	 * @param value
	 * @param maxValue
	 * @param expireAt
	 * @param upperTips
	 * @return
	 * @throws Exception
	 */
	public long countNoDuplicate(String key, String value, long maxValue, Date expireAt, String upperTips) throws Exception {
		
		BoundSetOperations<String, String> ops = redisTemplate.boundSetOps(key + ":noDup");

		Long result = ops.add(value);

        //设置过期
        if (isNeedSetExpireAt(ops.getExpire(), expireAt, ExpireAtType.NULL)) {
            ops.expireAt(expireAt);
        }

		if (result > 0) {
			try {
				count(key, result.intValue(), maxValue, expireAt, upperTips);
			} catch (Exception e) {
				ops.remove(value);
				throw e;
			}
		}
		
		return result;
	}

	/**
	 * 回滚去重计数
	 * @param key
	 * @param value
	 * @param expireAt
	 * @return
	 * @throws Exception
	 */
	public long rollbackNoDuplicate(String key, String value, Date expireAt) throws Exception {
		
		BoundSetOperations<String, String> ops = redisTemplate.boundSetOps(key + ":noDup");
		
		Long result = ops.remove(value);
		
		if (result > 0) {
			try {
				count(key, -result.intValue(), -1, expireAt, null);
			} catch (Exception e) {
				ops.remove(value);
				throw e;
			}
		}
		
		return result;
	}

	/**
	 * set中是否存在目标值
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean isSetContain(String key, String value) {
		
		BoundSetOperations<String, String> ops = redisTemplate.boundSetOps(key);

		return ops.isMember(value);
	}

	public List<String> multiGet(Collection<String> keys) {

		if (keys.size() > 0) {
			return redisTemplate.opsForValue().multiGet(keys);
		}
		return new ArrayList<String>();
	}


	/**
	 * 按指定key生成token，若token已经存在，则生成失败
	 * @param key
	 * @param durationSecd		过期时间 最小1秒 秒单位
	 * @return
	 * @throws Exception
	 */
	public String genToken(String key, long durationSecd) throws Exception {

        if (durationSecd < 1) {
            durationSecd = 1;
        }

        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps(key);

        Long result = ops.increment(0);

        if (result != 0) {
            throw new ApiBizException(MsgCode.C00000281.code, MsgCode.C00000281.msg);
        }

        ops.expire(durationSecd, TimeUnit.SECONDS);

		return key;
	}

	/**
	 * 系统将拼接prefix markCode，并随机生成keyLen长度的字符串作为key来生成token
	 * @param prefix
	 * @param markCode
	 * @param keyLen		最小1 最大50
	 * @param durationSecd
	 * @return
	 * @throws Exception
	 */
	public String genToken(String prefix, String markCode, int keyLen, long durationSecd) throws Exception {

		if (keyLen < 1) {
			keyLen = 1;
		} else if (keyLen > 50) {
			keyLen = 50;
		}

        if (durationSecd < 1) {
            durationSecd = 1;
        }

		prefix = StringUtil.trimStr(prefix);

		markCode = StringUtil.trimStr(markCode);
		if (!markCode.equals(Constants.STR_BLANK)) {
			prefix = String.format("%s%s:", prefix, markCode);
		}

		int genTimes = 0;
		do {
            String rmd = RandomUtil.genLetterNumStr(keyLen);
            //生成redis key
            String key = String.format("%s%s", prefix, rmd);

            try {
               genToken(key, durationSecd);
               return rmd;
            } catch (Exception e) {
            }
        } while (genTimes < 10);

		throw new ApiBizException(MsgCode.C00000281.code, MsgCode.C00000281.msg);
	}

	/**
	 * 检查令牌
	 *
	 * @param key
	 * @param maxValue 为-1时，视token为时效性令牌，key存在即返回成功
	 * @return
	 * @throws Exception
	 */
	public void checkToken(String key, long maxValue) throws Exception {

	    //首先判断key是否存在
		if (hasKey(key)) {

		    //若maxValue为负，则视为时效令牌，直接返回
			if (maxValue < 0) {
				return;
			}

			//检查次数
			try {
				count(key, 1, maxValue, new Date(System.currentTimeMillis() + Constants.MILLS_MINUTE30), ExpireAtType.NULL, null);
				return;
			} catch (Exception e) {
				throw new ApiBizException(MsgCode.C00000283.code, MsgCode.C00000283.msg);
			}
		}
		throw new ApiBizException(MsgCode.C00000282.code, MsgCode.C00000282.msg);
	}
	public void checkToken(String prefix, String markCode, String token, long maxValue) throws Exception {

		prefix = StringUtil.trimStr(prefix);

		markCode = StringUtil.trimStr(markCode);
		if (!markCode.equals(Constants.STR_BLANK)) {
			prefix = String.format("%s%s:", prefix, markCode);
		}

		checkToken(prefix + token, maxValue);
	}

    /**
     * 检查一次性令牌
     * @param prefix
     * @param markCode
     * @param token
     * @throws Exception
     */
	public void checkTokenOnce(String prefix, String markCode, String token) throws Exception {

		checkToken(prefix, markCode, token, 1);
	}
	public void checkTokenOnce(String key) throws Exception {
		checkToken(key, 1);
	}

    /**
     * 检查时效性令牌
     * @param prefix
     * @param markCode
     * @param token
     * @throws Exception
     */
	public void checkTokenTimeliness(String prefix, String markCode, String token) throws Exception {
		checkToken(prefix, markCode, token, -1);
	}
	public void checkTokenTimeliness(String key) throws Exception {
		checkToken(key,-1);
	}

    /**
     * 判断是否需要设置key的过期时间
     * @param keyExpireSecd         通过getExpire()得到的key的过期时间 -1永不过期 -2key不存在 其他存在过期
     * @param expireAt              需要设定的过期时间
     * @param type expireAt不为空时，该参数才起作用
     *                     -1 expireAt小于key的过期时间时才设置
     *                     0 过期时间不存在时设置为此过期时间
     *                     1 expireAt大于key的过期时间时才设置
     *                     2 若其他值 直接设置为该过期时间
     * @return
     */
    private boolean isNeedSetExpireAt(long keyExpireSecd, Date expireAt, ExpireAtType type) {

        if (expireAt != null && type != null) {
            if (type.getValue() == ExpireAtType.NULL.getValue()) {
                if (keyExpireSecd < 0) {
                    return true;
                }
            } else if (type.getValue() == ExpireAtType.LESS_THAN.getValue()) {
                if (keyExpireSecd < 0) {
                    return true;
                }
            } else if (type.getValue() == ExpireAtType.GREATER_THAN.getValue()) {
                if (expireAt.getTime() > keyExpireSecd) {
                    return true;
                }
            } else if (type.getValue() == ExpireAtType.DIRECT_SET.getValue()) {
                return true;
            }
        }
        return false;
    }


	/*
	public List<byte[]> multiGet(byte[]... keys) {

		if (keys.length > 0) {
			RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
			List<byte[]> mGet = connection.mGet(keys);
			connection.close();
			return mGet;
		}
		return new ArrayList<byte[]>();
	}

	public List<byte[]> multiGet(List<String> keys) {
		
		if (keys != null && keys.size() > 0) {
			byte[][] keysByte = new byte[keys.size()][1];
			for (int i = 0; i < keys.size(); i++) {
				keysByte[i] = (keys.get(i)).getBytes();
			}
			return multiGet(keysByte);
		}
		return new ArrayList<byte[]>();
	}

	public List<Object> multiGetDeserializeWithStringRedis(List<String> keys) {
		
		List<Object> list = new ArrayList<Object>();
		
		if (keys != null && keys.size() > 0) {
			for (byte[] bs : multiGet(keys)) {
				list.add(stringRedisSerializer.deserialize(bs));
			}
		}
		return list;
	}

	public List<Object> multiGetDeserializeWithJdkRedis(List<String> keys) {
		
		List<Object> list = new ArrayList<Object>();
		
		if (keys != null && keys.size() > 0) {
			for (byte[] bs : multiGet(keys)) {
				list.add(jdkSerializationRedisSerializer.deserialize(bs));
			}
		}
		return list;
	}
	*/

    @CacheEvict(value={
                        "second2",
                        "second5",
                        "second10",
                        "second30",
                        "minute1",
                        "minute2",
                        "minute5",
                        "minute10",
                        "minute30",
                        "hour1",
                        "hour2",
                        "hour5",
                        "hour10",
                        "day1",
                        "day2",
                        "day5",
                        "day10",
                        "day30",
                      },
            key="#key")
    public void cleanCacheAll(String key) {
    }

    @CacheEvict(value="second2", key="#key")
    public void clearCacheSecond2(String key) {
    }
    @CacheEvict(value="second5", key="#key")
    public void clearCacheSecond5(String key) {
    }
    @CacheEvict(value="second10", key="#key")
    public void clearCacheSecond10(String key) {
    }
    @CacheEvict(value="second30", key="#key")
    public void clearCacheSecond30(String key) {
    }

    @CacheEvict(value="minute1", key="#key")
    public void clearCacheMinute1(String key) {
    }
    @CacheEvict(value="minute2", key="#key")
    public void clearCacheMinute2(String key) {
    }
    @CacheEvict(value="minute5", key="#key")
    public void clearCacheMinute5(String key) {
    }
    @CacheEvict(value="minute10", key="#key")
    public void clearCacheMinute10(String key) {
    }
    @CacheEvict(value="minute30", key="#key")
    public void clearCacheMinute30(String key) {
    }

    @CacheEvict(value="hour1", key="#key")
    public void clearCacheHour1(String key) {
    }
    @CacheEvict(value="hour2", key="#key")
    public void clearCacheHour2(String key) {
    }
    @CacheEvict(value="hour5", key="#key")
    public void clearCacheHour5(String key) {
    }
    @CacheEvict(value="hour10", key="#key")
    public void clearCacheHour10(String key) {
    }

    @CacheEvict(value="day1", key="#key")
    public void clearCacheDay1(String key) {
    }
    @CacheEvict(value="day2", key="#key")
    public void clearCacheDay2(String key) {
    }
    @CacheEvict(value="day5", key="#key")
    public void clearCacheDay5(String key) {
    }
    @CacheEvict(value="day10", key="#key")
    public void clearCacheDay10(String key) {
    }
    @CacheEvict(value="day30", key="#key")
    public void clearCacheDay30(String key) {
    }

}
