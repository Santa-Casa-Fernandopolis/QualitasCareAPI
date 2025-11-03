package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.KitItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitItemRepository extends JpaRepository<KitItem, Long> {
}
