package com.poll.entity.ext;

import lombok.Data;

import java.io.Serializable;

@Data
public class PollProductExt  implements Serializable {
    private static final long serialVersionUID = -2089332606796434455L;

    private  String productCodeP;
    private  Integer productNum;
}
