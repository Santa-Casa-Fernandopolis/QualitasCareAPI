package com.erp.qualitascareapi.quality.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.quality.enums.NaoConformidadeStatus;

public interface NaoConformidadeBase {

    Long getId();

    Tenant getTenant();

    String getTitulo();

    NaoConformidadeStatus getStatus();

    TipoNaoConformidade getTipo();
}
