package com.vagas.camel.service;

import com.operadora.api.DefaultApi;
import com.operadora.api.model.Sms;
import com.vagas.app.i18n.Messages;
import com.vagas.camel.exception.SmsInvalidoException;
import com.vagas.camel.exception.SmsNaoCadastradoException;
import com.vagas.model.SmsRequisicao;
import com.vagas.vo.SmsVoResponse;
import io.swagger.client.ApiException;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperty;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Date;


@Service
public class SmsRequisicaoService extends BusinessAbstract<SmsRequisicao> implements ISmsRequisicaoService {

    private DefaultApi defaultApi;

    @Autowired
    private Messages messages;

    public SmsRequisicaoService(){
        defaultApi = new DefaultApi();
    }

    public SmsRequisicaoService(DefaultApi defaultApi){
        this.defaultApi = defaultApi;
    }


    @Override
    public void setDefaultApi(DefaultApi defaultApi){
        this.defaultApi = defaultApi;
    }

    @Override
    public void validate(@Body SmsRequisicao smsRequest) throws SmsInvalidoException {
        if(smsRequest.getValidDate().before(new Date())){
            throw new SmsInvalidoException(messages.dataInvalida());
        }
        if(smsRequest.getBody().length() >160){
            throw new SmsInvalidoException(messages.limiteUltrapassado());
        }
    }


    @Override
    public SmsVoResponse generateResponse(@Body SmsRequisicao smsRequisicao){
        return new SmsVoResponse(messages.smsOk(), smsRequisicao.getIdRequisicao(), null);
    }

    @Override
    public SmsVoResponse generateFailResponse(@Body SmsRequisicao smsRequisicao, @ExchangeProperty(value = "exception") Exception exception){
        return new SmsVoResponse(messages.smsFail() , smsRequisicao.getIdRequisicao(), exception.getMessage());
    }

    @Override
    public SmsVoResponse generateErrResponse(@Body SmsRequisicao smsRequisicao){
        return new SmsVoResponse(messages.smsError(), smsRequisicao.getIdRequisicao(), null);
    }

    @Override
    public SmsVoResponse failResponse(@Header("id") Long idRequisicao, @ExchangeProperty(value = "exception") Exception exception){
        return new SmsVoResponse(messages.smsFail() , idRequisicao, exception.getMessage());
    }

    @Override
    public void sendSms(Sms sms) throws ApiException{
        defaultApi.sendSMS(sms);
    }

    @Override
    public SmsVoResponse consultarStatus(Long id) throws SmsNaoCadastradoException {
        TypedQuery<SmsRequisicao> findByIdRequisicao = entityManager.createNamedQuery("findByIdRequisicao", SmsRequisicao.class);
        findByIdRequisicao.setParameter("idRequisicao", id);
        try {
            SmsRequisicao singleResult = findByIdRequisicao.getSingleResult();

            switch (singleResult.getStatusEnvio()) {
                case ERROR:
                    return generateErrResponse(singleResult);
                case PROCESSING:
                case NOT_SENT:
                case RECEIVED:
                    return generateResponse(singleResult);
            }

            return new SmsVoResponse(messages.smsSent(), singleResult.getIdRequisicao(), null);
        } catch (NoResultException nre){
            throw new SmsNaoCadastradoException(messages.idRequisicaoNaoEncontrado());
        }
    }
}
