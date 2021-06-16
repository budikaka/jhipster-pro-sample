package com.zbdigit.myapp.repository;

import com.zbdigit.myapp.domain.CommonTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Spring Data  repository for the CommonTable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommonTableRepository extends JpaRepository<CommonTable, Long>, JpaSpecificationExecutor<CommonTable> {
    @Query("select commonTable from CommonTable commonTable where commonTable.creator.login = ?#{principal.username}")
    List<CommonTable> findByCreatorIsCurrentUser();

    Optional<CommonTable> findOneByEntityName(String entityName);
    // jhipster-needle-repository-add-method - JHipster will add getters and setters here, do not remove
}
