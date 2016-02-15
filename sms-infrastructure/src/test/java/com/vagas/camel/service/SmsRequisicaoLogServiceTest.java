package com.vagas.camel.service;

import com.vagas.app.configuration.AppConfig;
import com.vagas.model.SmsRequisicao;
import com.vagas.model.domain.StatusEnvio;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.*;


@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = AppConfig.class)
public class SmsRequisicaoLogServiceTest {

    @Autowired
    private SmsRequisicaoLogService smsRequisicaoLogService;


    @Test
    public void testCreateLogOK() throws Exception {

        SmsRequisicao smsRequisicao = createSmsRequisicao();

        smsRequisicaoLogService.createLog(smsRequisicao, StatusEnvio.SENT, null);

        assertEquals(smsRequisicao.getSmsRequisicaoLogs().size(), 1);

    }


    @Test
    public void testCreateLogFailed() throws Exception {

        SmsRequisicao smsRequisicao = createSmsRequisicao();

        smsRequisicaoLogService.createLog(smsRequisicao, StatusEnvio.INVALID, new Exception("Erro generico"));

        assertEquals(smsRequisicao.getSmsRequisicaoLogs().size(), 1);
        assertEquals(smsRequisicao.getSmsRequisicaoLogs().get(0).getErroMsg(), "Erro generico");

    }

    @Test
    public void testCreateLogError() throws Exception {

        SmsRequisicao smsRequisicao = createSmsRequisicao();

        smsRequisicaoLogService.createLog(smsRequisicao, StatusEnvio.ERROR, new Exception("Erro generico"));

        assertEquals(smsRequisicao.getSmsRequisicaoLogs().size(), 1);
        assertEquals(smsRequisicao.getSmsRequisicaoLogs().get(0).getErroMsg(), "Erro generico");
        assertNotNull(smsRequisicao.getSmsRequisicaoLogs().get(0).getDetailError());

    }



    private SmsRequisicao createSmsRequisicao() {
        SmsRequisicao smsRequisicao = new SmsRequisicao();
        smsRequisicao.setStatusEnvio(StatusEnvio.RECEIVED);
        smsRequisicao.setTo("to1");
        smsRequisicao.setFrom("from1");
        smsRequisicao.setBody("body1");
        smsRequisicao.setId(1L);
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusDays(1);
        smsRequisicao.setValidDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return smsRequisicao;
    }
}