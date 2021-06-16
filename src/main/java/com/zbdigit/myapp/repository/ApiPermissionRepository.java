package com.zbdigit.myapp.repository;

import com.zbdigit.myapp.domain.ApiPermission;
import com.zbdigit.myapp.domain.enumeration.ApiPermissionType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Spring Data  repository for the ApiPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ApiPermissionRepository extends JpaRepository<ApiPermission, Long>, JpaSpecificationExecutor<ApiPermission> {
    Page<ApiPermission> findAllByParentIsNull(Pageable pageable);

    Optional<ApiPermission> findOneByCode(String groupCode);

    List<ApiPermission> findAllByType(ApiPermissionType type);

    @Query("select authority.apiPermissions from Authority authority join authority.users u where u.login = ?#{principal.username}")
    List<ApiPermission> findAllApiPermissionsByCurrentUser();

    @Query(
        value = "select apiPermission from ApiPermission apiPermission left join fetch apiPermission.authorities",
        countQuery = "select count(distinct apiPermission) from ApiPermission apiPermission"
    )
    Page<ApiPermission> findAllWithEagerRelationships(Pageable pageable);
    // jhipster-needle-repository-add-method - JHipster will add getters and setters here, do not remove
}
