package com.vagas.camel.service;


import com.vagas.camel.exception.SmsInvalidoException;
import com.vagas.camel.exception.SmsNaoCadastradoException;
import com.vagas.model.SmsRequisicao;
import com.vagas.vo.SmsVoRequest;
import com.vagas.vo.SmsVoResponse;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestOperationResponseMsgDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class SmsRequisicaoRoute extends SmsRouteAbstract {

    public void configure() {
        // servlet is configured in Application.java
        onException(SmsInvalidoException.class)
                .handled(true)
                .logStackTrace(false)
                .setHeader("routeId", exchangeProperty(Exchange.FAILURE_ROUTE_ID))
                .setHeader("endpoint", exchangeProperty(Exchange.FAILURE_ENDPOINT))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("403"))
                .setProperty("exception", exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, INVALID)")
                .to("jpa:smsRequisicao")
                .to("bean:smsRequisicaoService?method=generateFailResponse");
        onException(SmsNaoCadastradoException.class)
                    .handled(true)
                    .logStackTrace(false)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("404"))
                .setProperty("exception", exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                .to("bean:smsRequisicaoService?method=failResponse");

        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest("/sms")
                .description("Send SMS Rest Service")
                .consumes("application/json")
                .produces("application/json")
                .post("/send")
                .type(SmsVoRequest.class)
                .param().name("smsRequest").dataType("smsVoRequest").description("Message to be send to carrier").endParam()
                .description("Send Sms asynchronous")
                .outType(SmsVoResponse.class)
                .to("direct:processSms")
                .responseMessage(new RestOperationResponseMsgDefinition().message("OK"))
                .produces("application/json")
                .get("/status/{id}")
                .param().name("id").dataType("int64").type(RestParamType.path).description("Message's Identifier").endParam()
                .description("Verify if the message was sent to SMS operator")
                .to("direct:processStatusSms");




        from("direct:processSms")
                .log("received ${body}")
                .log("converting ${body}")
                .convertBodyTo(SmsRequisicao.class)
                .log("logging ${body}")
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, RECEIVED)")
                .log("persisting ${body}")
                .to("jpa:SmsRequisicao")
                .log("validating ${body}")
                .to("bean:smsRequisicaoService?method=validate")
                .log("responding ${body}")
                .to("disruptor:smsCarrier")
                .to("bean:smsRequisicaoService?method=generateResponse");

        from("direct:processStatusSms")
                .log("verifying status of id ${header.id}")
                .to("bean:smsRequisicaoService?method=consultarStatus(${header.id})");


    }
}
