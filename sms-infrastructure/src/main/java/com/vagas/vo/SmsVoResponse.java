package com.vagas.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Data
@AllArgsConstructor
public class SmsVoResponse {

    private String message;
    private Long id;
    private String errMsg;



}
