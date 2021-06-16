package com.zbdigit.myapp.repository;

import com.zbdigit.myapp.domain.ViewPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Spring Data  repository for the ViewPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ViewPermissionRepository extends JpaRepository<ViewPermission, Long>, JpaSpecificationExecutor<ViewPermission> {
    Page<ViewPermission> findAllByParentIsNull(Pageable pageable);

    @Query(
        value = "select distinct viewPermission from ViewPermission viewPermission left join fetch viewPermission.authorities",
        countQuery = "select count(distinct viewPermission) from ViewPermission viewPermission"
    )
    Page<ViewPermission> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct viewPermission from ViewPermission viewPermission left join fetch viewPermission.authorities")
    List<ViewPermission> findAllWithEagerRelationships();

    @Query(
        "select viewPermission from ViewPermission viewPermission left join fetch viewPermission.authorities where viewPermission.id =:id"
    )
    Optional<ViewPermission> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select authority.viewPermissions from Authority authority join authority.users u where u.login = ?#{principal.username}")
    List<ViewPermission> findAllViewPermissionsByCurrentUser();
    // jhipster-needle-repository-add-method - JHipster will add getters and setters here, do not remove
}
