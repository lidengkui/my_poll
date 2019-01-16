package com.poll.dao.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.poll.dao.mapper.ThirdAccessMapper;
import com.poll.dao.service.ThirdAccessService;
import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ThirdInterfaceEntity;
import com.poll.entity.ext.ThirdAccessEntityExt;
import com.poll.entity.ext.ThirdRoleInterfaceRltEntityExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("thirdAccesService")
public class ThirdAccessServiceImpl extends ServiceImpl<ThirdAccessMapper, ThirdAccessEntity> implements ThirdAccessService {

    @Override
    @Cacheable(value = "day30", key = "'poll:thirdAccess:' + #id")
    public ThirdAccessEntity selectByIdCache(String id) {

        if (id == null) {
            return null;
        }
        return super.selectById(id);
    }

    @Override
    @Cacheable(value = "day30", key = "'poll:thirdAccessAuth:' + #id")
    public ThirdAccessEntityExt selectWithAuthInfo(String id) {

        if (id == null) {
            return null;
        }

        ThirdAccessEntityExt entityExt = baseMapper.selectWithAuthInfo(id);
        if (entityExt == null) {
            return null;
        }

        List<ThirdRoleInterfaceRltEntityExt> list = entityExt.getRoleInterfaceRltList();
        if (list != null) {
            Map<String, Map<String, ThirdRoleInterfaceRltEntityExt>> methodMapMap = new HashMap<String, Map<String, ThirdRoleInterfaceRltEntityExt>>();

            for (ThirdRoleInterfaceRltEntityExt trirExt : list) {
                ThirdInterfaceEntity ti = trirExt.getThirdInterface();
                if (ti != null) {
                    Map<String, ThirdRoleInterfaceRltEntityExt> methodMap = methodMapMap.get(ti.getServiceNameTi());
                    if (methodMap == null) {
                        methodMap = new HashMap<String, ThirdRoleInterfaceRltEntityExt>();
                        methodMapMap.put(ti.getServiceNameTi(), methodMap);
                    }
                    methodMap.put(ti.getMethodNameTi(), trirExt);
                }
            }
            entityExt.setRoleInterfaceRltMap(methodMapMap);
        }
        return entityExt;
    }
}
