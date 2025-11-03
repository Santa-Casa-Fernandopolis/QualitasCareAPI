package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.KitVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitVersionRepository extends JpaRepository<KitVersion, Long> {
}
