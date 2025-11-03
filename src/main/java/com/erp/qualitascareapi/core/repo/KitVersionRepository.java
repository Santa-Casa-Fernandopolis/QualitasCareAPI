package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitVersionRepository extends JpaRepository<KitVersion, Long> {
}
