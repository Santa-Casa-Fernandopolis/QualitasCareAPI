package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.KitItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitItemRepository extends JpaRepository<KitItem, Long> {
}
