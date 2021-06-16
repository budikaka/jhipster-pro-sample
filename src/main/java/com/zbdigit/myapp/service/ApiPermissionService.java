package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import com.zbdigit.myapp.domain.ApiPermission;
import com.zbdigit.myapp.domain.enumeration.ApiPermissionType;
import com.zbdigit.myapp.repository.ApiPermissionRepository;
import com.zbdigit.myapp.security.SecurityUtils;
import com.zbdigit.myapp.security.annotation.PermissionDefine;
import com.zbdigit.myapp.service.dto.ApiPermissionDTO;
import com.zbdigit.myapp.service.mapper.ApiPermissionMapper;
import java.beans.PropertyDescriptor;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Service Implementation for managing {@link ApiPermission}.
 */
@Service
@Transactional
public class ApiPermissionService {

    private final Logger log = LoggerFactory.getLogger(ApiPermissionService.class);
    private final List<String> relationCacheNames = Arrays.asList(
        com.zbdigit.myapp.domain.ApiPermission.class.getName() + ".parent",
        com.zbdigit.myapp.domain.ApiPermission.class.getName() + ".children" //,
        // com.zbdigit.myapp.domain.ViewPermission.class.getName() + ".apiPermissions"
    );

    private final ApiPermissionRepository apiPermissionRepository;

    private final CacheManager cacheManager;

    private final ApiPermissionMapper apiPermissionMapper;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public ApiPermissionService(
        ApiPermissionRepository apiPermissionRepository,
        CacheManager cacheManager,
        ApiPermissionMapper apiPermissionMapper,
        RequestMappingHandlerMapping requestMappingHandlerMapping
    ) {
        this.apiPermissionRepository = apiPermissionRepository;
        this.cacheManager = cacheManager;
        this.apiPermissionMapper = apiPermissionMapper;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * Save a apiPermission.
     *
     * @param apiPermissionDTO the entity to save.
     * @return the persisted entity.
     */
    public ApiPermissionDTO save(ApiPermissionDTO apiPermissionDTO) {
        log.debug("Request to save ApiPermission : {}", apiPermissionDTO);
        ApiPermission apiPermission = apiPermissionMapper.toEntity(apiPermissionDTO);
        clearChildrenCache();
        apiPermission = apiPermissionRepository.save(apiPermission);
        // 更新缓存
        if (apiPermission.getParent() != null) {
            apiPermission.getParent().addChildren(apiPermission);
        }
        return apiPermissionMapper.toDto(apiPermission);
    }

    /**
     * Get all the apiPermissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ApiPermissionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ApiPermissions");
        return apiPermissionRepository.findAll(pageable).map(apiPermissionMapper::toDto);
    }

    /**
     * Get all the apiPermissions for parent is null.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ApiPermissionDTO> findAllTop(Pageable pageable) {
        log.debug("Request to get all ApiPermissions for parent is null");
        return apiPermissionRepository.findAllByParentIsNull(pageable).map(apiPermissionMapper::toDto);
    }

    /**
     * count all the apiPermissions.
     *
     * @return the count of entities
     * by wangxin
     */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Request to count all ApiPermissions");
        return apiPermissionRepository.count();
    }

    /**
     * Get all the apiPermissions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ApiPermissionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return apiPermissionRepository.findAllWithEagerRelationships(pageable).map(apiPermissionMapper::toDto);
    }

    /**
     * Get one apiPermission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ApiPermissionDTO> findOne(Long id) {
        log.debug("Request to get ApiPermission : {}", id);
        return apiPermissionRepository.findById(id).map(apiPermissionMapper::toDto);
    }

    /**
     * Get all the apiPermissions.
     *
     * @param type the ApiPermissionType.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ApiPermissionDTO> findAllByType(ApiPermissionType type) {
        log.debug("Request to get all ApiPermissions by type");
        return apiPermissionMapper.toDto(apiPermissionRepository.findAllByType(type));
    }

    /**
     * Delete the apiPermission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ApiPermission : {}", id);
        ApiPermission apiPermission = apiPermissionRepository.getOne(id);
        if (apiPermission.getParent() != null) {
            apiPermission.getParent().removeChildren(apiPermission);
        }
        if (apiPermission.getChildren() != null) {
            apiPermission
                .getChildren()
                .forEach(
                    subApiPermission -> {
                        subApiPermission.setParent(null);
                    }
                );
        }
        apiPermissionRepository.deleteById(id);
    }

    /**
     * Update ignore specified fields by apiPermission
     */
    public ApiPermissionDTO updateByIgnoreSpecifiedFields(ApiPermissionDTO changeApiPermissionDTO, Set<String> unchangedFields) {
        ApiPermissionDTO apiPermissionDTO = findOne(changeApiPermissionDTO.getId()).get();
        BeanUtil.copyProperties(changeApiPermissionDTO, apiPermissionDTO, unchangedFields.toArray(new String[0]));
        apiPermissionDTO = save(apiPermissionDTO);
        return apiPermissionDTO;
    }

    /**
     * Update specified fields by apiPermission
     */
    public ApiPermissionDTO updateBySpecifiedFields(ApiPermissionDTO changeApiPermissionDTO, Set<String> fieldNames) {
        ApiPermissionDTO apiPermissionDTO = findOne(changeApiPermissionDTO.getId()).get();
        ApiPermissionDTO finalApiPermissionDTO = apiPermissionDTO;
        fieldNames.forEach(
            fieldName -> {
                BeanUtil.setFieldValue(finalApiPermissionDTO, fieldName, BeanUtil.getFieldValue(changeApiPermissionDTO, fieldName));
            }
        );
        apiPermissionDTO = save(finalApiPermissionDTO);
        return apiPermissionDTO;
    }

    /**
     * Update specified field by apiPermission
     */
    public ApiPermissionDTO updateBySpecifiedField(ApiPermissionDTO changeApiPermissionDTO, String fieldName) {
        ApiPermissionDTO apiPermissionDTO = findOne(changeApiPermissionDTO.getId()).get();
        BeanUtil.setFieldValue(apiPermissionDTO, fieldName, BeanUtil.getFieldValue(changeApiPermissionDTO, fieldName));
        apiPermissionDTO = save(apiPermissionDTO);
        return apiPermissionDTO;
    }

    // 清除children缓存
    private void clearChildrenCache() {
        Objects.requireNonNull(cacheManager.getCache(com.zbdigit.myapp.domain.ApiPermission.class.getName() + ".children")).clear();
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
     * regenerate ApiPermissions from Annotation
     */
    public void regenerateApiPermissions() {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            HandlerMethod method = m.getValue();
            PermissionDefine permissionDefineAnnotation = method.getMethodAnnotation(PermissionDefine.class);
            if (permissionDefineAnnotation != null) {
                // 处理group
                ApiPermission apiPermissionGroup = apiPermissionRepository
                    .findOneByCode(permissionDefineAnnotation.groupCode())
                    .orElseGet(
                        () ->
                            apiPermissionRepository.save(
                                new ApiPermission()
                                    .code(permissionDefineAnnotation.groupCode())
                                    .name(permissionDefineAnnotation.groupName())
                                    .type(ApiPermissionType.BUSINESS)
                            )
                    );
                // 处理实体
                ApiPermission apiPermissionEntity = apiPermissionRepository
                    .findOneByCode(permissionDefineAnnotation.groupCode() + "_" + permissionDefineAnnotation.entityCode())
                    .orElseGet(
                        () ->
                            apiPermissionRepository.save(
                                new ApiPermission()
                                    .code(permissionDefineAnnotation.groupCode() + "_" + permissionDefineAnnotation.entityCode())
                                    .name(permissionDefineAnnotation.entityName())
                                    .type(ApiPermissionType.ENTITY)
                            )
                    );
                apiPermissionRepository.save(apiPermissionEntity.parent(apiPermissionGroup));
                // 处理permission
                // 获得相关的methodType

                ApiPermission apiPermission = apiPermissionRepository
                    .findOneByCode(
                        permissionDefineAnnotation.groupCode() +
                        "_" +
                        permissionDefineAnnotation.entityCode() +
                        "_" +
                        permissionDefineAnnotation.permissionCode()
                    )
                    .orElseGet(
                        () ->
                            apiPermissionRepository.save(
                                new ApiPermission()
                                    .code(
                                        permissionDefineAnnotation.groupCode() +
                                        "_" +
                                        permissionDefineAnnotation.entityCode() +
                                        "_" +
                                        permissionDefineAnnotation.permissionCode()
                                    )
                                    .name(permissionDefineAnnotation.permissionName())
                                    .parent(apiPermissionEntity)
                                    .type(ApiPermissionType.API)
                            )
                    );

                GetMapping getMappingAnnotation = method.getMethodAnnotation(GetMapping.class);
                PostMapping postMappingAnnotation = method.getMethodAnnotation(PostMapping.class);
                DeleteMapping deleteMappingAnnotation = method.getMethodAnnotation(DeleteMapping.class);
                PutMapping putMappingAnnotation = method.getMethodAnnotation(PutMapping.class);
                RequestMapping requestMappingAnnotation = method.getMethodAnnotation(RequestMapping.class);
                StringBuilder methodType = new StringBuilder();
                if (getMappingAnnotation != null) {
                    methodType = new StringBuilder("GET");
                }
                if (postMappingAnnotation != null) {
                    methodType = new StringBuilder("POST");
                }
                if (deleteMappingAnnotation != null) {
                    methodType = new StringBuilder("DELETE");
                }
                if (putMappingAnnotation != null) {
                    methodType = new StringBuilder("PUT");
                }
                if (requestMappingAnnotation != null && requestMappingAnnotation.method() != null) {
                    if (requestMappingAnnotation.method().length > 0) {
                        RequestMethod[] methods = requestMappingAnnotation.method();
                        for (RequestMethod r : methods) {
                            if (methodType.indexOf(r.name()) == -1) {
                                methodType.append(",").append(r.name());
                            }
                        }
                        if (methodType.charAt(0) == ',') {
                            methodType.deleteCharAt(0);
                        }
                    }
                }
                // url
                PatternsRequestCondition patternsCondition = m.getKey().getPatternsCondition();
                String url = patternsCondition.toString();
                apiPermission.method(methodType.toString()).url(url);
                apiPermissionRepository.save(apiPermission.parent(apiPermissionEntity));
            }
        }
    }

    public List<ApiPermissionDTO> findAllApiPermissionsByCurrentUser() {
        if (SecurityUtils.isAuthenticated()) {
            return apiPermissionMapper.toDto(apiPermissionRepository.findAllApiPermissionsByCurrentUser());
        } else {
            return new ArrayList<>();
        }
    }
    // jhipster-needle-service-add-method - JHipster will add getters and setters here, do not remove

}
