package com.poll.entity.ext;

import com.poll.entity.CompanyEntity;
import com.poll.entity.UserEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserEntityExt extends UserEntity {

    private static final long serialVersionUID = -7334188204937936279L;
    private CompanyEntity company;
}
