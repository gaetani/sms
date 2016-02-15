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
import com.vagas.model.SmsRequisicao;
import com.vagas.model.domain.StatusEnvio;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration( classes = AppConfig.class)
public class SmsRequisicaoLogRouteAsyncTest extends JerseyTest {


    @Produce(uri = "disruptor:smsCarrier?waitForTaskToComplete=Always")
    private ProducerTemplate processSmsProducer;

    @EndpointInject(uri = "jpa:SmsRequisicao")
    private JpaEndpoint jpaEndpoint;


    @Autowired
    private ISmsRequisicaoService smsRequisicaoService;

    @Before
    public void befor(){
        DefaultApi defaultApi = new DefaultApi(new ApiClient().setBasePath("http://127.0.0.1:9998/test/api/v1"));
        smsRequisicaoService.setDefaultApi(defaultApi);
    }


    @Test
    public void testSendSmsOK() throws Exception {

        EntityManager em = jpaEndpoint.getEntityManagerFactory().createEntityManager();

        SmsRequisicao smsRequisicao1 = createSmsRequisicao();
        processSmsProducer.sendBody(smsRequisicao1);

        SmsRequisicao smsRequisicao = (SmsRequisicao)em.createQuery("select x from SmsRequisicao x where x.idRequisicao = :idRequisicao").setParameter("idRequisicao", smsRequisicao1.getIdRequisicao()).getSingleResult();


        assertEquals(2, smsRequisicao.getSmsRequisicaoLogs().size());

    }



    private SmsRequisicao createSmsRequisicao(){
        SmsRequisicao smsRequisicao = new SmsRequisicao();
        smsRequisicao.setStatusEnvio(StatusEnvio.RECEIVED);
        smsRequisicao.setTo("to1");
        smsRequisicao.setFrom("from1");
        smsRequisicao.setBody("body1");
        smsRequisicao.setIdRequisicao(14L);
        return smsRequisicao;
    }


    //@Test(expected = ApiException.class)
    public void testSendSmsError() throws Exception {

        SmsResource.THROWS_ERROR_500 = true;


    }

    @Override
    protected AppDescriptor configure() {
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(MultiPartWriter.class);
        config.getClasses().add(SmsResource.class);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        return  new WebAppDescriptor.Builder( SmsResource.class.getPackage().getName() )
                .contextPath("/test")
                .clientConfig( config )
                .build();
    }


}