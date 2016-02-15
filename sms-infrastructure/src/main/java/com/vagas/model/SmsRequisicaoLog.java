package com.vagas.model;

import com.vagas.model.domain.StatusEnvio;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sms_requisicao_log")
public class SmsRequisicaoLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SMS_REQU_LOG")
    @SequenceGenerator(name = "SQ_SMS_REQU_LOG", sequenceName = "SQ_SMS_REQU_LOG")
    @Column(name = "cd_sms_requ_log")
    @Setter @Getter
    private Long id;

    @ManyToOne
    @JoinColumn(name="cd_sms_requisicao", nullable=false,  referencedColumnName = "cd_sms_requisicao", foreignKey = @ForeignKey(name = "fk_sms_requ_log"))
    @Setter @Getter
    private SmsRequisicao smsRequisicao;

    @Column(name = "tx_error_msg")
    @Setter @Getter
    private String erroMsg;

    @Column(name = "in_status_envio")
    @Setter @Getter
    private StatusEnvio statusEnvio;

    @Column(name = "dt_ocorrencia")
    @Temporal(TemporalType.TIMESTAMP)
    @Setter @Getter
    private Date ocorrencia;

    @Column(name = "tx_detail_error")
    @Setter @Getter
    private String detailError;


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


}
