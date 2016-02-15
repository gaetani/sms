package com.vagas.camel.converter;

import com.operadora.api.model.Sms;
import com.vagas.model.SmsRequisicao;
import com.vagas.model.SmsRequisicaoLog;
import com.vagas.model.domain.StatusEnvio;
import com.vagas.vo.SmsVoRequest;
import com.vagas.vo.SmsVoResponse;
import org.apache.camel.Converter;

import java.io.*;
import java.time.LocalDate;

@Converter
public class SmsConverter {

    /**
     * Converter do ValueObject do serviço para a entidade a ser persistida.
     *  Num futuro próximo, dozer.
     *
     * @param smsVoRequest ValueObject do servico
     * @return entidade
     */
    @Converter
    public SmsRequisicao convertFrom(SmsVoRequest smsVoRequest){

        SmsRequisicao smsRequisicao = new SmsRequisicao();

        smsRequisicao.setBody(smsVoRequest.getBody());
        smsRequisicao.setFrom(smsVoRequest.getFrom());
        smsRequisicao.setIdRequisicao(smsVoRequest.getId());
        smsRequisicao.setStatusEnvio(StatusEnvio.RECEIVED);
        smsRequisicao.setTo(smsVoRequest.getTo());
        smsRequisicao.setValidDate(smsVoRequest.getValidDate().toDate());

        return smsRequisicao;
    }

    /**
     * Converter da Entidade SmsRequisicao para o SMS do serviço da operadora.
     *  Num futuro próximo, dozer.
     *
     * @param smsRequisicao entidade a ser convertida
     * @return sms
     */
    @Converter
    public Sms convertFrom(SmsRequisicao smsRequisicao){

        Sms sms = new Sms();
        sms.setTo(smsRequisicao.getTo());
        sms.setBody(smsRequisicao.getBody());
        sms.setFrom(smsRequisicao.getFrom());
        sms.setId(smsRequisicao.getIdRequisicao());

        return sms;
    }

    /**
     * Converter da Entidade SmsRequisicao para o SMS do serviço da operadora.
     *  Num futuro próximo, dozer.
     *
     * @param smsRequisicaoLog entidade a ser convertida
     * @return sms
     */
    @Converter
    public Sms convertFrom(SmsRequisicaoLog smsRequisicaoLog){

        SmsRequisicao smsRequisicao = smsRequisicaoLog.getSmsRequisicao();

        Sms sms = new Sms();
        sms.setTo(smsRequisicao.getTo());
        sms.setBody(smsRequisicao.getBody());
        sms.setFrom(smsRequisicao.getFrom());
        sms.setId(smsRequisicao.getIdRequisicao());

        return sms;
    }


    @Converter
    public byte[] serialize(SmsRequisicao obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    @Converter
    public SmsRequisicao deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return (SmsRequisicao)o.readObject();
    }





}
