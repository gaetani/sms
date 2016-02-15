package com.vagas.camel.service;

import com.operadora.api.model.Sms;
import io.swagger.client.ApiException;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SmsRequisicaoLogRouteAsync extends SmsRouteAbstract {

    public void configure() throws Exception {

        super.configure();


        from("disruptor:smsCarrier?concurrentConsumers=10")
                .setExchangePattern(ExchangePattern.InOnly)
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, PROCESSING)")
                .to("jpa:SmsRequisicao")
                .log("received ${body} calling services ")
                .to("bean:smsRequisicaoService?method=sendSms")
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, SENT)")
                .to("jpa:SmsRequisicao");


    }
}
