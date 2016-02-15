package com.vagas.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import lombok.Data;
import org.joda.time.LocalDate;

@Data
public class SmsVoRequest {


    private Long id = null;


    private String from = null;


    private String to = null;


    private String body = null;


    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate validDate = null;

}
