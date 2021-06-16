package com.zbdigit.myapp.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zbdigit.myapp.domain.CommonTable;
import com.zbdigit.myapp.domain.CommonTableField;
import com.zbdigit.myapp.domain.CommonTableRelationship;
import com.zbdigit.myapp.domain.enumeration.RelationshipType;
import com.zbdigit.myapp.repository.CommonTableFieldRepository;
import com.zbdigit.myapp.repository.CommonTableRepository;
import com.zbdigit.myapp.repository.search.CommonTableFieldSearchRepository;
import com.zbdigit.myapp.service.dto.CommonTableFieldDTO;
import com.zbdigit.myapp.service.mapper.CommonTableFieldMapper;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Service Implementation for managing {@link CommonTableField}.
 */
@Service
@Transactional
public class CommonTableFieldService {

    private final Logger log = LoggerFactory.getLogger(CommonTableFieldService.class);

    private final List<String> relationCacheNames = Arrays.asList(
        com.zbdigit.myapp.domain.CommonTable.class.getName() + ".commonTableFields"
    );

    private final CommonTableFieldRepository commonTableFieldRepository;

    private final CacheManager cacheManager;

    private final CommonExtDataService commonExtDataService;

    private final CommonTableRepository commonTableRepository;

    private final CommonTableFieldMapper commonTableFieldMapper;

    private final CommonTableFieldSearchRepository commonTableFieldSearchRepository;

    public CommonTableFieldService(
        CommonTableFieldRepository commonTableFieldRepository,
        CacheManager cacheManager,
        CommonExtDataService commonExtDataService,
        CommonTableRepository commonTableRepository,
        CommonTableFieldMapper commonTableFieldMapper,
        CommonTableFieldSearchRepository commonTableFieldSearchRepository
    ) {
        this.commonTableFieldRepository = commonTableFieldRepository;
        this.cacheManager = cacheManager;
        this.commonExtDataService = commonExtDataService;
        this.commonTableRepository = commonTableRepository;
        this.commonTableFieldMapper = commonTableFieldMapper;
        this.commonTableFieldSearchRepository = commonTableFieldSearchRepository;
    }

    /**
     * Save a commonTableField.
     *
     * @param commonTableFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public CommonTableFieldDTO save(CommonTableFieldDTO commonTableFieldDTO) {
        log.debug("Request to save CommonTableField : {}", commonTableFieldDTO);
        // 获得未修改数据
        Optional<CommonTableFieldDTO> old = this.findOne(commonTableFieldDTO.getId());
        // 提取扩展数据
        Map<String, Object> extData = commonTableFieldDTO.getExtData();
        CommonTableField commonTableField = commonTableFieldMapper.toEntity(commonTableFieldDTO);
        commonTableFieldRepository.save(commonTableField);
        commonExtDataService.saveExtData(commonTableField);
        return commonTableFieldMapper.toDto(commonTableField);
    }

    /**
     * Partially update a commonTableField.
     *
     * @param commonTableFieldDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommonTableFieldDTO> partialUpdate(CommonTableFieldDTO commonTableFieldDTO) {
        log.debug("Request to partially update CommonTableField : {}", commonTableFieldDTO);

        return commonTableFieldRepository
            .findById(commonTableFieldDTO.getId())
            .map(
                existingAdministrativeDivision -> {
                    commonTableFieldMapper.partialUpdate(existingAdministrativeDivision, commonTableFieldDTO);
                    return existingAdministrativeDivision;
                }
            )
            .map(commonTableFieldRepository::save)
            .map(commonTableFieldMapper::toDto);
    }

    /**
     * Get all the commonTableFields.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CommonTableFieldDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CommonTableFields");
        return commonTableFieldRepository.findAll(pageable).map(commonTableFieldMapper::toDto);
    }

    /**
     * Get all the commonTableFields where ExtData is {@code null}.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CommonTableFieldDTO> findAllWhereExtDataIsNull() {
        log.debug("Request to get all commonTableFields where ExtData is null");
        return StreamSupport
            .stream(commonTableFieldRepository.findAll().spliterator(), false)
            .filter(commonTableField -> commonTableField.getExtData() == null)
            .map(commonTableFieldMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one commonTableField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommonTableFieldDTO> findOne(Long id) {
        log.debug("Request to get CommonTableField : {}", id);
        return commonTableFieldRepository
            .findById(id)
            .map(commonTableField -> (CommonTableField) commonExtDataService.getExtData(commonTableField))
            .map(commonTableFieldMapper::toDto);
    }

    /**
     * Delete the commonTableField by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CommonTableField : {}", id);
        commonTableFieldRepository.deleteById(id);
        commonTableFieldSearchRepository.deleteById(id);
    }

    /**
     * Search for the commonTableField corresponding to the query.
     *
     * @param query    the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CommonTableFieldDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CommonTableFields for query {}", query);
        return commonTableFieldSearchRepository.search(queryStringQuery(query), pageable).map(commonTableFieldMapper::toDto);
    }

    /**
     * Update ignore specified fields by commonTableField
     */
    public CommonTableFieldDTO updateByIgnoreSpecifiedFields(CommonTableFieldDTO changeCommonTableFieldDTO, Set<String> unchangedFields) {
        CommonTableFieldDTO commonTableFieldDTO = findOne(changeCommonTableFieldDTO.getId()).get();
        BeanUtil.copyProperties(changeCommonTableFieldDTO, commonTableFieldDTO, unchangedFields.toArray(new String[0]));
        commonTableFieldDTO = save(commonTableFieldDTO);
        return commonTableFieldDTO;
    }

    /**
     * Update specified fields by commonTableField
     */
    public CommonTableFieldDTO updateBySpecifiedFields(CommonTableFieldDTO changeCommonTableFieldDTO, Set<String> fieldNames) {
        CommonTableFieldDTO commonTableFieldDTO = findOne(changeCommonTableFieldDTO.getId()).get();
        CommonTableFieldDTO finalCommonTableFieldDTO = commonTableFieldDTO;
        fieldNames.forEach(
            fieldName -> {
                BeanUtil.setFieldValue(finalCommonTableFieldDTO, fieldName, BeanUtil.getFieldValue(changeCommonTableFieldDTO, fieldName));
            }
        );
        commonTableFieldDTO = save(finalCommonTableFieldDTO);
        return commonTableFieldDTO;
    }

    /**
     * Update specified field by commonTableField
     */
    public CommonTableFieldDTO updateBySpecifiedField(CommonTableFieldDTO changeCommonTableFieldDTO, String fieldName) {
        CommonTableFieldDTO commonTableFieldDTO = findOne(changeCommonTableFieldDTO.getId()).get();
        BeanUtil.setFieldValue(commonTableFieldDTO, fieldName, BeanUtil.getFieldValue(changeCommonTableFieldDTO, fieldName));
        commonTableFieldDTO = save(commonTableFieldDTO);
        return commonTableFieldDTO;
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
