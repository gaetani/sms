package com.vagas.camel.service;

import com.vagas.model.SmsRequisicao;
import com.vagas.model.SmsRequisicaoLog;
import com.vagas.model.domain.StatusEnvio;
import org.apache.camel.ExchangeProperty;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

@Service
public class SmsRequisicaoLogService {


    public void createLog(SmsRequisicao smsRequisicao, StatusEnvio statusEnvio, @ExchangeProperty(value = "exception") Exception exception){

        smsRequisicao.setStatusEnvio(statusEnvio);

        SmsRequisicaoLog smsRequisicaoLog = new SmsRequisicaoLog();
        smsRequisicaoLog.setSmsRequisicao(smsRequisicao);
        smsRequisicaoLog.setStatusEnvio(statusEnvio);
        smsRequisicaoLog.setOcorrencia(new Date());
        if(exception != null){
            smsRequisicaoLog.setErroMsg(exception.getMessage());

            if(statusEnvio == StatusEnvio.ERROR) {
                StringWriter stringWriter = new StringWriter();
                ExceptionUtils.printRootCauseStackTrace(exception, new PrintWriter(stringWriter));
                smsRequisicaoLog.setDetailError(stringWriter.toString());
            }
        }

        smsRequisicao.addRequisicaoList(smsRequisicaoLog);

    }

}
