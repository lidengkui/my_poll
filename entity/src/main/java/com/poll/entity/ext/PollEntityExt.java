package com.poll.entity.ext;

import com.poll.entity.PollEntity;
import com.poll.entity.PollPrdSnpEntity;
import com.poll.entity.PollSgSnpEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class PollEntityExt extends PollEntity {

    private static final long serialVersionUID = 5490109604376082784L;

    //所选组
    private List<PollSgSnpEntity> sgList;

    //所选权益
    private List<PollPrdSnpEntity> prdList;

}
