package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import com.zbdigit.myapp.domain.Authority;
import com.zbdigit.myapp.domain.User;
import com.zbdigit.myapp.domain.ViewPermission;
import com.zbdigit.myapp.repository.ViewPermissionRepository;
import com.zbdigit.myapp.security.SecurityUtils;
import com.zbdigit.myapp.service.criteria.ViewPermissionCriteria;
import com.zbdigit.myapp.service.dto.ViewPermissionDTO;
import com.zbdigit.myapp.service.mapper.ViewPermissionMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove
/**
 * Service Implementation for managing {@link ViewPermission}.
 */
@Service
@Transactional
public class ViewPermissionService {

    private final Logger log = LoggerFactory.getLogger(ViewPermissionService.class);
    private final List<String> relationCacheNames = Arrays.asList(
        com.zbdigit.myapp.domain.ViewPermission.class.getName() + ".parent",
        com.zbdigit.myapp.domain.ApiPermission.class.getName() + ".viewPermissions",
        com.zbdigit.myapp.domain.ViewPermission.class.getName() + ".children",
        com.zbdigit.myapp.domain.Authority.class.getName() + ".viewPermission"
    );

    private final ViewPermissionRepository viewPermissionRepository;

    private final CacheManager cacheManager;

    private final ViewPermissionMapper viewPermissionMapper;

    private final UserService userService;

    private final ViewPermissionQueryService viewPermissionQueryService;

    public ViewPermissionService(
        ViewPermissionRepository viewPermissionRepository,
        CacheManager cacheManager,
        ViewPermissionMapper viewPermissionMapper,
        UserService userService,
        ViewPermissionQueryService viewPermissionQueryService
    ) {
        this.viewPermissionRepository = viewPermissionRepository;
        this.cacheManager = cacheManager;
        this.viewPermissionMapper = viewPermissionMapper;
        this.userService = userService;
        this.viewPermissionQueryService = viewPermissionQueryService;
    }

    /**
     * Save a viewPermission.
     *
     * @param viewPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public ViewPermissionDTO save(ViewPermissionDTO viewPermissionDTO) {
        log.debug("Request to save ViewPermission : {}", viewPermissionDTO);
        ViewPermission viewPermission = viewPermissionMapper.toEntity(viewPermissionDTO);
        clearChildrenCache();
        viewPermission = viewPermissionRepository.save(viewPermission);
        // 更新缓存
        if (viewPermission.getParent() != null) {
            viewPermission.getParent().addChildren(viewPermission);
        }
        return viewPermissionMapper.toDto(viewPermission);
    }

    /**
     * Get all the viewPermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ViewPermissionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ViewPermissions");
        return viewPermissionRepository.findAll(pageable).map(viewPermissionMapper::toDto);
    }

    /**
     * Get all the viewPermissions for parent is null.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ViewPermissionDTO> findAllTop(Pageable pageable) {
        log.debug("Request to get all ViewPermissions for parent is null");
        return viewPermissionRepository.findAllByParentIsNull(pageable).map(viewPermissionMapper::toDto);
    }

    /**
     * count all the viewPermissions.
     *
     * @return the count of entities
     * by wangxin
     */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Request to count all ViewPermissions");
        return viewPermissionRepository.count();
    }

    /**
     * Get all the viewPermissions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ViewPermissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return viewPermissionRepository.findAllWithEagerRelationships(pageable).map(viewPermissionMapper::toDto);
    }

    /**
     * Get one viewPermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ViewPermissionDTO> findOne(Long id) {
        log.debug("Request to get ViewPermission : {}", id);
        return viewPermissionRepository.findOneWithEagerRelationships(id).map(viewPermissionMapper::toDto);
    }

    /**
     * Get one viewPermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ViewPermissionDTO> findOneNoChildren(Long id) {
        log.debug("Request to get ViewPermission : {}", id);
        return viewPermissionRepository.findOneWithEagerRelationships(id).map(viewPermissionMapper::toNoChildrenDto);
    }

    /**
     * Delete the viewPermission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ViewPermission : {}", id);
        ViewPermission viewPermission = viewPermissionRepository.getOne(id);
        if (viewPermission.getParent() != null) {
            viewPermission.getParent().removeChildren(viewPermission);
        }
        if (viewPermission.getChildren() != null) {
            viewPermission
                .getChildren()
                .forEach(
                    subViewPermission -> {
                        subViewPermission.setParent(null);
                    }
                );
        }
        viewPermissionRepository.deleteById(id);
    }

    /**
     * Update ignore specified fields by viewPermission
     */
    public ViewPermissionDTO updateByIgnoreSpecifiedFields(ViewPermissionDTO changeViewPermissionDTO, Set<String> unchangedFields) {
        ViewPermissionDTO viewPermissionDTO = findOne(changeViewPermissionDTO.getId()).get();
        BeanUtil.copyProperties(changeViewPermissionDTO, viewPermissionDTO, unchangedFields.toArray(new String[0]));
        viewPermissionDTO = save(viewPermissionDTO);
        return viewPermissionDTO;
    }

    /**
     * Update specified fields by viewPermission
     */
    public ViewPermissionDTO updateBySpecifiedFields(ViewPermissionDTO changeViewPermissionDTO, Set<String> fieldNames) {
        ViewPermissionDTO viewPermissionDTO = findOne(changeViewPermissionDTO.getId()).get();
        ViewPermissionDTO finalViewPermissionDTO = viewPermissionDTO;
        fieldNames.forEach(
            fieldName -> {
                BeanUtil.setFieldValue(finalViewPermissionDTO, fieldName, BeanUtil.getFieldValue(changeViewPermissionDTO, fieldName));
            }
        );
        viewPermissionDTO = save(finalViewPermissionDTO);
        return viewPermissionDTO;
    }

    /**
     * Update specified field by viewPermission
     */
    public ViewPermissionDTO updateBySpecifiedField(ViewPermissionDTO changeViewPermissionDTO, String fieldName) {
        ViewPermissionDTO viewPermissionDTO = findOne(changeViewPermissionDTO.getId()).get();
        BeanUtil.setFieldValue(viewPermissionDTO, fieldName, BeanUtil.getFieldValue(changeViewPermissionDTO, fieldName));
        viewPermissionDTO = save(viewPermissionDTO);
        return viewPermissionDTO;
    }

    // 清除children缓存
    private void clearChildrenCache() {
        Objects.requireNonNull(cacheManager.getCache(com.zbdigit.myapp.domain.ViewPermission.class.getName() + ".children")).clear();
    }

    private void clearRelationsCache() {
        this.relationCacheNames.forEach(
                cacheName -> {
                    if (cacheManager.getCache(cacheName) != null) {
                        cacheManager.getCache(cacheName).clear();
                    }
                }
            );
    }

    /**
     * get all viewPermission by currentUserLogin
     *
     * @return List<ViewPermissionDTO>
     */
    public List<ViewPermissionDTO> getAllByLogin() {
        List<ViewPermission> resultList = new ArrayList<>();
        // 获得所有角色的viewPermission。
        if (!SecurityUtils.isAuthenticated()) {
            return viewPermissionMapper.toDto(resultList);
        }
        List<ViewPermission> userViewPermissions = viewPermissionRepository.findAllViewPermissionsByCurrentUser();
        List<ViewPermission> addViewPermissions = new ArrayList<>();
        userViewPermissions.forEach(
            viewPermission -> {
                while (viewPermission != null && viewPermission.getParent() != null) {
                    Long parentId = viewPermission.getParent().getId();
                    if (userViewPermissions.stream().noneMatch(viewPermission1 -> viewPermission1.getId().equals(parentId))) {
                        Optional<ViewPermission> oneNoChildren = viewPermissionRepository.findOneWithEagerRelationships(parentId);
                        if (oneNoChildren.isPresent()) {
                            addViewPermissions.add(oneNoChildren.get());
                            viewPermission = oneNoChildren.get();
                        } else {
                            viewPermission = null;
                        }
                    } else {
                        viewPermission = null;
                    }
                }
            }
        );
        userViewPermissions.addAll(addViewPermissions);
        // 已经找到所有的当前User相关的ViewPermissions及上级，接下来转换为树形结构。
        userViewPermissions.forEach(
            userViewPermission -> {
                if (userViewPermission.getParent() == null) {
                    Long finalId = userViewPermission.getId();
                    if (resultList.stream().noneMatch(resultViewPermission -> resultViewPermission.getId().equals(finalId))) {
                        resultList.add(userViewPermission);
                    }
                } else {
                    ViewPermission topParent = null;
                    while (userViewPermission != null && userViewPermission.getParent() != null) {
                        Long tempId = userViewPermission.getParent().getId();
                        Optional<ViewPermission> tempViewPermission = userViewPermissions
                            .stream()
                            .filter(viewPermission -> viewPermission.getId().equals(tempId))
                            .findAny();
                        if (tempViewPermission.isPresent()) {
                            if (tempViewPermission.get().getParent() == null) {
                                topParent = tempViewPermission.get();
                                tempViewPermission.get().getChildren().add(userViewPermission);
                                userViewPermission = null;
                            } else {
                                tempViewPermission.get().getChildren().add(userViewPermission);
                                userViewPermission = tempViewPermission.get();
                            }
                        } else {
                            userViewPermission = null;
                        }
                    }
                    if (topParent != null) {
                        ViewPermission finalTopParent = topParent;
                        Optional<ViewPermission> any = resultList
                            .stream()
                            .filter(resultViewPermission -> resultViewPermission.getId().equals(finalTopParent.getId()))
                            .findAny();
                        if (any.isPresent()) {
                            // 处理子列表
                            Set<ViewPermission> resultChildren = any.get().getChildren();
                            Set<ViewPermission> unCheckChildren = topParent.getChildren();
                            addToResult(resultChildren, unCheckChildren);
                        } else {
                            resultList.add(topParent);
                        }
                    }
                }
            }
        );
        return viewPermissionMapper.toDto(resultList);
    }

    private void addToResult(Set<ViewPermission> resultChildren, Set<ViewPermission> unCheckChildren) {
        if (unCheckChildren.size() > 0) {
            unCheckChildren.forEach(
                child -> {
                    Long childId = child.getId();
                    Optional<ViewPermission> any = resultChildren
                        .stream()
                        .filter(viewPermission -> viewPermission.getId().equals(childId))
                        .findAny();
                    if (any.isPresent()) {
                        addToResult(any.get().getChildren(), child.getChildren());
                    } else {
                        resultChildren.add(child);
                    }
                }
            );
        }
    }
    // jhipster-needle-service-add-method - JHipster will add getters and setters here, do not remove

}
