package com.vagas.camel.service;

import com.operadora.api.DefaultApi;
import com.operadora.api.model.Sms;
import com.vagas.camel.exception.SmsInvalidoException;
import com.vagas.camel.exception.SmsNaoCadastradoException;
import com.vagas.model.SmsRequisicao;
import com.vagas.vo.SmsVoResponse;
import io.swagger.client.ApiException;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;

/**
 * Created by gaetani on 2/14/16.
 */
public interface ISmsRequisicaoService {
    void setDefaultApi(DefaultApi defaultApi);

    void validate(@Body SmsRequisicao smsRequest) throws SmsInvalidoException;

    SmsVoResponse generateResponse(@Body SmsRequisicao smsRequisicao);

    SmsVoResponse generateFailResponse(@Body SmsRequisicao smsRequisicao, @ExchangeProperty(value = "exception") Exception exception);

    SmsVoResponse generateErrResponse(@Body SmsRequisicao smsRequisicao);

    SmsVoResponse failResponse(Long idRequisicao, @ExchangeProperty(value = "exception") Exception exception);

    void sendSms(Sms sms) throws ApiException;

    SmsVoResponse consultarStatus(Long id) throws SmsNaoCadastradoException;
}
