package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import org.springframework.stereotype.Component;

@Component
public class SavePatientConnector extends UnsupportedLegacyPatientConnector {

    @Override
    protected SameSourceSystem sourceSystem() {
        return SameSourceSystem.SAVE;
    }
}
