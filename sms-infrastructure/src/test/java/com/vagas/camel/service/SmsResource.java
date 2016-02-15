package com.vagas.camel.service;
    /*
         * Mocking the service
         * @throws Exception
         */

import com.operadora.api.model.Sms;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Path("api/v1")
public class SmsResource {

    public static boolean THROWS_ERROR_500 = false;
    @PUT
    @Path("/sms")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void sendSMS(Sms sms) {

        if(THROWS_ERROR_500){
            throw new RuntimeException("Teste com o servico lancando erro inesperado. ");
        }
        assertNotNull(sms);
        assertEquals(sms.getBody(), "body1");
        assertEquals(sms.getTo(), "to1");
        assertEquals(sms.getId(), Long.valueOf(14L));
        assertEquals(sms.getFrom(), "from1");
    }


}

