package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import com.zbdigit.myapp.domain.Authority;
import com.zbdigit.myapp.repository.AuthorityRepository;
import com.zbdigit.myapp.repository.UserRepository;
import com.zbdigit.myapp.security.SecurityUtils;
import com.zbdigit.myapp.service.dto.AuthorityDTO;
import com.zbdigit.myapp.service.mapper.AuthorityMapper;
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

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Service Implementation for managing {@link Authority}.
 */
@Service
@Transactional
public class AuthorityService {

    private final Logger log = LoggerFactory.getLogger(AuthorityService.class);
    private final List<String> relationCacheNames = Arrays.asList(
        com.zbdigit.myapp.domain.Authority.class.getName() + ".parent",
        com.zbdigit.myapp.domain.User.class.getName() + ".authority",
        com.zbdigit.myapp.domain.ViewPermission.class.getName() + ".authorities",
        com.zbdigit.myapp.domain.Authority.class.getName() + ".children"
    );

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final AuthorityMapper authorityMapper;

    private final UserRepository userRepository;

    public AuthorityService(
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        AuthorityMapper authorityMapper,
        UserRepository userRepository
    ) {
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.authorityMapper = authorityMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a authority.
     *
     * @param authorityDTO the entity to save.
     * @return the persisted entity.
     */
    public AuthorityDTO save(AuthorityDTO authorityDTO) {
        log.debug("Request to save Authority : {}", authorityDTO);
        Authority authority = authorityMapper.toEntity(authorityDTO);
        clearChildrenCache();
        authority = authorityRepository.save(authority);
        // ????????????
        if (authority.getParent() != null) {
            authority.getParent().addChildren(authority);
        }
        return authorityMapper.toDto(authority);
    }

    /**
     * Get all the authorities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Authorities");
        return authorityRepository.findAll(pageable).map(authorityMapper::toDto);
    }

    /**
     * Get all the authorities for parent is null.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> findAllTop(Pageable pageable) {
        log.debug("Request to get all Authorities for parent is null");
        return authorityRepository.findAllByParentIsNull(pageable).map(authorityMapper::toDto);
    }

    /**
     * count all the authorities.
     *
     * @return the count of entities
     * by wangxin
     */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Request to count all Authorities");
        return authorityRepository.count();
    }

    /**
     * Get all the authorities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AuthorityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return authorityRepository.findAllWithEagerRelationships(pageable).map(authorityMapper::toDto);
    }

    /**
     * Get one authority by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuthorityDTO> findOne(Long id) {
        log.debug("Request to get Authority : {}", id);
        return authorityRepository.findOneWithEagerRelationships(id).map(authorityMapper::toDto);
    }

    /**
     * Get one authority by code.
     *
     * @param code the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuthorityDTO> findFirstByCode(String code) {
        log.debug("Request to get Authority : {}", code);
        return authorityRepository.findFirstByCode(code).map(authorityMapper::toDto);
    }

    /**
     * Delete the authority by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Authority : {}", id);
        Authority authority = authorityRepository.getOne(id);
        if (authority.getParent() != null) {
            authority.getParent().removeChildren(authority);
        }
        if (authority.getChildren() != null) {
            authority
                .getChildren()
                .forEach(
                    subAuthority -> {
                        subAuthority.setParent(null);
                    }
                );
        }
        authorityRepository.deleteById(id);
    }

    /**
     * Update ignore specified fields by authority
     */
    public AuthorityDTO updateByIgnoreSpecifiedFields(AuthorityDTO changeAuthorityDTO, Set<String> unchangedFields) {
        AuthorityDTO authorityDTO = findOne(changeAuthorityDTO.getId()).get();
        BeanUtil.copyProperties(changeAuthorityDTO, authorityDTO, unchangedFields.toArray(new String[0]));
        authorityDTO = save(authorityDTO);
        return authorityDTO;
    }

    /**
     * Update specified fields by authority
     */
    public AuthorityDTO updateBySpecifiedFields(AuthorityDTO changeAuthorityDTO, Set<String> fieldNames) {
        AuthorityDTO authorityDTO = findOne(changeAuthorityDTO.getId()).get();
        AuthorityDTO finalAuthorityDTO = authorityDTO;
        fieldNames.forEach(
            fieldName -> {
                BeanUtil.setFieldValue(finalAuthorityDTO, fieldName, BeanUtil.getFieldValue(changeAuthorityDTO, fieldName));
            }
        );
        authorityDTO = save(finalAuthorityDTO);
        return authorityDTO;
    }

    /**
     * Update specified field by authority
     */
    public AuthorityDTO updateBySpecifiedField(AuthorityDTO changeAuthorityDTO, String fieldName) {
        AuthorityDTO authorityDTO = findOne(changeAuthorityDTO.getId()).get();
        BeanUtil.setFieldValue(authorityDTO, fieldName, BeanUtil.getFieldValue(changeAuthorityDTO, fieldName));
        authorityDTO = save(authorityDTO);
        return authorityDTO;
    }

    // ??????children??????
    private void clearChildrenCache() {
        Objects.requireNonNull(cacheManager.getCache(com.zbdigit.myapp.domain.Authority.class.getName() + ".children")).clear();
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
    // jhipster-needle-service-add-method - JHipster will add getters and setters here, do not remove

}
