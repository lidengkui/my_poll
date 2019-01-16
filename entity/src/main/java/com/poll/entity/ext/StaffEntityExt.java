package com.poll.entity.ext;

import com.poll.entity.StaffEntity;
import com.poll.entity.StaffGpEntity;
import lombok.Data;

@Data
public class StaffEntityExt  extends StaffEntity {
    private static final long serialVersionUID = -1883804203799798339L;
    private StaffGpEntity group;
    private Long totalCount;
}
