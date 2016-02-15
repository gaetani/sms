package com.vagas.camel.service;

import com.vagas.app.configuration.AppConfig;
import com.vagas.app.i18n.Messages;
import com.vagas.camel.converter.SmsConverter;
import com.vagas.camel.exception.SmsNaoCadastradoException;
import com.vagas.model.SmsRequisicao;
import com.vagas.model.domain.StatusEnvio;
import com.vagas.vo.SmsVoRequest;
import com.vagas.vo.SmsVoResponse;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.test.spring.CamelTestContextBootstrapper;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManager;
import javax.xml.ws.Response;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = AppConfig.class)
public class SmsRequisicaoRouteTest{

    @Produce(uri = "direct:processSms")
    private ProducerTemplate processSmsProducer;


    @Produce(uri = "direct:processStatusSms")
    private ProducerTemplate processStatusSms;

    @Autowired
    private ModelCamelContext context;

    @EndpointInject(uri = "mock:smsCarrierMock")
    protected MockEndpoint mockC;

    @EndpointInject(uri = "jpa:SmsRequisicao")
    private JpaEndpoint jpaEndpoint;

    @Autowired
    private Messages messages;

    protected SmsConverter converter = new SmsConverter();

//    /Route(route1)[[From[rest:post:/sms:/send?routeId=route1&produces=application%2Fjson&description=Send+Sms+asynchronous&componentName=servlet&inType=com.vagas.vo.SmsVoRequest&outType=com.vagas.vo.SmsVoResponse&consumes=application%2Fjson]] -> [OnException[[class com.vagas.camel.exception.SmsInvalidoException] -> [SetHeader[routeId, exchangeProperty{exchangeProperty(CamelFailureRouteId)}], SetHeader[endpoint, exchangeProperty{exchangeProperty(CamelFailureEndpoint)}], SetProperty[exception, exchangeProperty{exchangeProperty(CamelExceptionCaught)}], To[bean:smsRequisicaoLogService?method=createLog(${body}, INVALID)], To[jpa:smsRequisicao], To[bean:smsRequisicaoService?method=generateFailResponse]]], RestBinding, To[direct:processSms]]]


    @Before
    public void mockEndpoints() throws Exception {
        AdviceWithRouteBuilder smsCarrierMock = new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {
                // mock the for testing
                interceptSendToEndpoint("disruptor:smsCarrier")
                        .skipSendToOriginalEndpoint()
                        .to("mock:smsCarrierMock");
            }
        };

        context.getRouteDefinition("route2").adviceWith(context, smsCarrierMock);
    }

    @Test
    public void testCallJPA() throws Exception {


        assertEquals(ServiceStatus.Started, context.getStatus());
        SmsVoRequest smsVoRequest = new SmsVoRequest();
        smsVoRequest.setTo("to1");
        smsVoRequest.setFrom("from1");
        smsVoRequest.setBody("body1");
        smsVoRequest.setId(10002L);
        smsVoRequest.setValidDate(new LocalDate().plusDays(1));

        processSmsProducer.sendBody(smsVoRequest);

        EntityManager em = jpaEndpoint.getEntityManagerFactory().createEntityManager();

        SmsRequisicao smsRequisicao = (SmsRequisicao)em.createQuery("select x from SmsRequisicao x where idRequisicao = :idRequisicao").setParameter("idRequisicao", smsVoRequest.getId()).getSingleResult();

        assertEquals(smsVoRequest.getBody(), smsRequisicao.getBody());
        assertEquals(smsVoRequest.getTo(), smsRequisicao.getTo());
        assertEquals(smsVoRequest.getFrom(), smsRequisicao.getFrom());
        assertEquals(smsVoRequest.getId(), smsRequisicao.getIdRequisicao());
        assertEquals(StatusEnvio.RECEIVED, smsRequisicao.getStatusEnvio());
        assertEquals(1, smsRequisicao.getSmsRequisicaoLogs().size());
    }

    @Test
    public void testCallDisruptor() throws Exception {

        assertEquals(ServiceStatus.Started, context.getStatus());
        SmsVoRequest body = new SmsVoRequest();
        body.setTo("to1");
        body.setFrom("from1");
        body.setBody("body1");
        body.setId(10001L);
        body.setValidDate(new LocalDate().plusDays(1));

        mockC.expectedMessageCount(1);


        processSmsProducer.sendBody(body);


        mockC.assertIsSatisfied();
    }

    @Test
    public void testCallGetSuccess() throws Exception {


        assertEquals(ServiceStatus.Started, context.getStatus());

        SmsVoResponse smsVoResponse=(SmsVoResponse) processStatusSms.requestBodyAndHeader("", "id", 10002L);


        assertEquals(Long.valueOf(10002L), smsVoResponse.getId());
    }


    @Test
    public void testCallGetError(){


        assertEquals(ServiceStatus.Started, context.getStatus());


        SmsVoResponse smsVoResponse = (SmsVoResponse) processStatusSms.requestBodyAndHeader("", "id", 166161616L);

        assertEquals(smsVoResponse.getErrMsg(), messages.idRequisicaoNaoEncontrado());
        assertEquals(smsVoResponse.getId(), Long.valueOf(166161616L));

    }






}