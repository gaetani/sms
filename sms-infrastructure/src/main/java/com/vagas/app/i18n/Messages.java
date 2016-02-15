package com.vagas.app.i18n;

import c10n.C10NKey;
import c10n.C10NMessages;

@C10NKey("service.smsrequisicao")
public interface Messages {


    @C10NKey("validation.data")
    String dataInvalida();

    @C10NKey("validation.limite")
    String limiteUltrapassado();

    @C10NKey("sms.ok")
    String smsOk();

    @C10NKey("sms.error")
    String smsError();

    @C10NKey("sms.fail")
    String smsFail();

    @C10NKey("sms.sent")
    String smsSent();

    @C10NKey("validation.id_requisicao_nao_encontrado")
    String idRequisicaoNaoEncontrado();
}
