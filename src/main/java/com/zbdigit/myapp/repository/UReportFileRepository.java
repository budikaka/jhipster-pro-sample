package com.zbdigit.myapp.repository;

import com.zbdigit.myapp.domain.UReportFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Spring Data  repository for the UReportFile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UReportFileRepository extends JpaRepository<UReportFile, Long>, JpaSpecificationExecutor<UReportFile> {
    Boolean existsByName(String name);

    Optional<UReportFile> getByName(String name);

    void deleteByName(String name);
    // jhipster-needle-repository-add-method - JHipster will add getters and setters here, do not remove
}
