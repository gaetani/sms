package com.vagas.camel.service;

import com.operadora.api.DefaultApi;
import com.operadora.api.model.Sms;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.vagas.app.configuration.AppConfig;
import com.vagas.app.i18n.Messages;
import com.vagas.camel.exception.SmsInvalidoException;
import com.vagas.camel.exception.SmsNaoCadastradoException;
import com.vagas.model.SmsRequisicao;
import com.vagas.model.domain.StatusEnvio;
import com.vagas.vo.SmsVoResponse;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = AppConfig.class)
public class SmsRequisicaoServiceTest  extends JerseyTest  {

    @Autowired
    private Messages messages;

    @Autowired
    private ISmsRequisicaoService smsRequisicaoService;

    @Test(expected = SmsInvalidoException.class)
    public void testValidateData() throws Exception {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minusDays(1);
        SmsRequisicao smsRequisicao = createSmsRequisicao();
        smsRequisicao.setValidDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        try {
            smsRequisicaoService.validate(smsRequisicao);
        } catch (SmsInvalidoException s){
            assertEquals(s.getMessage(), messages.dataInvalida());
            throw s;
        }
        fail();
    }

    @Test(expected = SmsInvalidoException.class)
    public void testValidateMSGLonga() throws Exception {

        SmsRequisicao smsRequisicao = createSmsRequisicao();
        String msg = new String(new char[200]).replace("\0", "a");
        smsRequisicao.setBody(msg);
        try{
            smsRequisicaoService.validate(smsRequisicao);
        } catch (SmsInvalidoException s){
            assertEquals(s.getMessage(), messages.limiteUltrapassado());
            throw s;
        }
        fail();
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

    @Test
    public void testGenerateResponse() throws Exception {
        SmsVoResponse smsVoResponse = smsRequisicaoService.generateResponse(createSmsRequisicao());
        assertEquals(smsVoResponse.getMessage(), messages.smsOk());
    }

    @Test
    public void testGenerateFailResponse() throws Exception {
        SmsVoResponse smsVoResponse = smsRequisicaoService.generateFailResponse(createSmsRequisicao(), new SmsInvalidoException(messages.dataInvalida()));
        assertEquals(smsVoResponse.getMessage(), messages.smsFail());
        assertEquals(smsVoResponse.getErrMsg(), messages.dataInvalida());
    }

    @Test
    public void testGenerateErrResponse() throws Exception {
        SmsVoResponse smsVoResponse = smsRequisicaoService.generateErrResponse(createSmsRequisicao());
        assertEquals(smsVoResponse.getMessage(), messages.smsError());
    }

    @Test
    @Ignore ///Falta definir uma estrategia de insercao antes
    public void testConsultarStatusOK() throws Exception{
        SmsVoResponse smsVoResponse = smsRequisicaoService.consultarStatus(1L);
        assertEquals(smsVoResponse.getMessage(), messages.smsSent());
    }

    @Test(expected = SmsInvalidoException.class)
    public void testConsultarStatusException() throws Exception{
        try {
            SmsVoResponse smsVoResponse = smsRequisicaoService.consultarStatus(1234455L);
        } catch (SmsNaoCadastradoException e){
            assertEquals(messages.idRequisicaoNaoEncontrado(), e.getMessage());
            throw e;
        }
        fail();
    }


        @Override
        protected AppDescriptor configure() {
            ClientConfig config = new DefaultClientConfig();
            config.getClasses().add( MultiPartWriter.class );
            config.getClasses().add(SmsResource.class);
            config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
            return  new WebAppDescriptor.Builder( SmsResource.class.getPackage().getName() )
                    .contextPath("/test")
                    .clientConfig( config )
                    .build();
        }


    @Test
    public void testSendSmsOK() throws Exception {

        DefaultApi defaultApi = new DefaultApi(new ApiClient().setBasePath("http://127.0.0.1:9998/test/api/v1"));
        try {
            Sms sms = new Sms();
            sms.setId(1L);
            sms.setBody("body1");
            sms.setFrom("from1");
            sms.setTo("to1");
            defaultApi.sendSMS(sms);
        } catch (ApiException e) {
            fail();
        }

    }



    @Test(expected = ApiException.class)
    public void testSendSmsError() throws Exception {

        SmsResource.THROWS_ERROR_500 = true;

        DefaultApi defaultApi = new DefaultApi(new ApiClient().setBasePath("http://127.0.0.1:9998/test/api/v1"));

            Sms sms = new Sms();
            sms.setId(1L);
            sms.setBody("body1");
            sms.setFrom("from1");
            sms.setTo("to1");
            defaultApi.sendSMS(sms);

    }
}