package com.poll.ability.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class ToMailDto {

    @Getter
    @Setter
    private String toMail;
    @Getter
    @Setter
    private String toName;
}
