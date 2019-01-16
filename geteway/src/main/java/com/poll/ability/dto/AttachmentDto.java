package com.poll.ability.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDto {

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String path;
}
