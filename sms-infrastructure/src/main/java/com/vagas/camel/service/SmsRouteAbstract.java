package com.vagas.camel.service;


import com.sun.jersey.api.client.ClientHandlerException;
import io.swagger.client.ApiException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.io.IOException;

public class SmsRouteAbstract extends RouteBuilder{

    /**
     * Barreira de excecao
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {
/*        onException(Exception.class)
                .setHeader("routeId", exchangeProperty(Exchange.FAILURE_ROUTE_ID))
                .setHeader("endpoint", exchangeProperty(Exchange.FAILURE_ENDPOINT))
                .setHeader("exception", exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                .logStackTrace(true); */
        onException(ApiException.class)
                .logStackTrace(true)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("404"))
                .setProperty("exception", exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, NOT_SENT)")
                .to("jpa:smsRequisicao")
                .to("bean:smsRequisicaoService?method=generateFailResponse");

        onException(IOException.class)
                .onException(ClientHandlerException.class)
                .logStackTrace(true)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("404"))
                .setProperty("exception", exchangeProperty(Exchange.EXCEPTION_CAUGHT))
                .to("bean:smsRequisicaoLogService?method=createLog(${body}, ERROR)")
                .to("jpa:smsRequisicao")
                .to("bean:smsRequisicaoService?method=generateErrResponse");

    }


}
