package com.poll.entity.ext;

import com.poll.entity.ThirdAccessEntity;
import com.poll.entity.ThirdRoleEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
public class ThirdAccessEntityExt extends ThirdAccessEntity {

    private static final long serialVersionUID = -6963681312724931194L;

    private ThirdRoleEntity role;

    private List<ThirdRoleInterfaceRltEntityExt> roleInterfaceRltList;

    private Map<String, Map<String, ThirdRoleInterfaceRltEntityExt>> roleInterfaceRltMap;
}
