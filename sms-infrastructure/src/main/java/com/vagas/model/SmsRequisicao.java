package com.vagas.model;

import com.vagas.model.domain.StatusEnvio;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sms_requisicao")
@NamedQuery(name ="findByIdRequisicao" , query = "from SmsRequisicao where idRequisicao = :idRequisicao")
public class SmsRequisicao implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_SMS_REQUISICAO")
    @SequenceGenerator(name = "SQ_SMS_REQUISICAO", sequenceName = "SQ_SMS_REQUISICAO")
    @Column(name = "cd_sms_requisicao")
    @Setter @Getter
    private Long id;

    @Column(name = "nr_id_requisicao")
    @Setter @Getter
    private Long idRequisicao;

    @Column(name = "tx_from")
    @Setter @Getter
    private String from;

    @Column(name = "tx_to")
    @Setter @Getter
    private String to;

    @Column(name = "tx_body")
    @Setter @Getter
    private String body;

    @Column(name = "dt_valid_date")
    @Temporal(TemporalType.DATE)
    @Setter @Getter
    private Date validDate;

    @Column(name = "in_status_envio")
    @Setter @Getter
    private StatusEnvio statusEnvio;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="cd_sms_requisicao", nullable=true)
    @Setter @Getter
    private List<SmsRequisicaoLog> smsRequisicaoLogs;

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

    public void addRequisicaoList(SmsRequisicaoLog smsRequisicaoLog) {
        if(smsRequisicaoLogs == null){
            smsRequisicaoLogs = new ArrayList<>();
        }
        smsRequisicaoLogs.add(smsRequisicaoLog);
    }
}
