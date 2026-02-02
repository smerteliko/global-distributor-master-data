package com.gda.masterdata.repository;

import com.gda.masterdata.entity.portal.PortalModuleEntity;
import com.gda.masterdata.enums.ModuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortalModuleRepository extends JpaRepository<PortalModuleEntity, String> {
    List<PortalModuleEntity> findAllByStatusNotOrderBySortOrderAsc(ModuleStatus status);
    List<PortalModuleEntity> findAllByOrderBySortOrderAsc();
}
