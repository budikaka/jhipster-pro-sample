package com.zbdigit.myapp.repository;

import com.zbdigit.myapp.domain.CommonExtData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Spring Data  repository for the CommonExtData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommonExtDataRepository extends JpaRepository<CommonExtData, Long>, JpaSpecificationExecutor<CommonExtData> {
    List<CommonExtData> findByOwnerIdAndOwnerType(Long ownerId, String ownerType);

    Optional<CommonExtData> findByOwnerIdAndOwnerTypeAndFieldName(Long ownerId, String ownerType, String key);
    // jhipster-needle-repository-add-method - JHipster will add getters and setters here, do not remove
}
