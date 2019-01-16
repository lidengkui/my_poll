package com.poll.entity.ext;

import com.poll.entity.ThirdInterfaceEntity;
import com.poll.entity.ThirdRoleInterfaceRltEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ThirdRoleInterfaceRltEntityExt extends ThirdRoleInterfaceRltEntity {

	private static final long serialVersionUID = -2926698771211197864L;

	private ThirdInterfaceEntity thirdInterface;

}