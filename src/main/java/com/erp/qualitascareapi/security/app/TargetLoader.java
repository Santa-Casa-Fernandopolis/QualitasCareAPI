package com.erp.qualitascareapi.security.app;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class TargetLoader {

    public Object load(String targetType, Serializable targetId) {
        // Carregue o alvo conforme o tipo (use projeções leves quando possível)
        // Ex.: if ("NC".equals(targetType)) return ncRepository.findProjection((Long) targetId);
        return null;
    }
}

