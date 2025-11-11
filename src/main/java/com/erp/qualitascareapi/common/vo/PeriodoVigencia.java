package com.erp.qualitascareapi.common.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * Value object representing validity periods used across IAM and approvals.
 */
@Embeddable
public class PeriodoVigencia {

    @Column(name = "vigencia_inicio")
    private Instant inicio;

    @Column(name = "vigencia_fim")
    private Instant fim;

    public PeriodoVigencia() {
    }

    public PeriodoVigencia(Instant inicio, Instant fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public Instant getInicio() {
        return inicio;
    }

    public void setInicio(Instant inicio) {
        this.inicio = inicio;
    }

    public Instant getFim() {
        return fim;
    }

    public void setFim(Instant fim) {
        this.fim = fim;
    }
}
