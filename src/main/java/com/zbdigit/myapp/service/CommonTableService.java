package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zbdigit.myapp.domain.CommonTable;
import com.zbdigit.myapp.domain.CommonTableField;
import com.zbdigit.myapp.domain.CommonTableRelationship;
import com.zbdigit.myapp.domain.User;
import com.zbdigit.myapp.domain.enumeration.RelationshipType;
import com.zbdigit.myapp.repository.CommonTableFieldRepository;
import com.zbdigit.myapp.repository.CommonTableRelationshipRepository;
import com.zbdigit.myapp.repository.CommonTableRepository;
import com.zbdigit.myapp.repository.UserRepository;
import com.zbdigit.myapp.security.SecurityUtils;
import com.zbdigit.myapp.service.dto.CommonTableDTO;
import com.zbdigit.myapp.service.dto.CommonTableFieldDTO;
import com.zbdigit.myapp.service.dto.CommonTableRelationshipDTO;
import com.zbdigit.myapp.service.mapper.CommonTableMapper;
import com.zbdigit.myapp.web.rest.errors.BadRequestAlertException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// jhipster-needle-add-import - JHipster will add getters and setters here, do not remove

/**
 * Service Implementation for managing {@link CommonTable}.
 */
@Service
@Transactional
public class CommonTableService {

    private final Logger log = LoggerFactory.getLogger(CommonTableService.class);
    private final List<String> relationCacheNames = Arrays.asList(
        com.zbdigit.myapp.domain.CommonTableField.class.getName() + ".commonTable",
        com.zbdigit.myapp.domain.CommonTableRelationship.class.getName() + ".commonTable",
        com.zbdigit.myapp.domain.User.class.getName() + ".commonTable",
        com.zbdigit.myapp.domain.BusinessType.class.getName() + ".commonTable"
    );

    private final CommonTableRepository commonTableRepository;

    private final CacheManager cacheManager;

    private final CommonTableMapper commonTableMapper;

    private final UserRepository userRepository;

    private final CommonExtDataService commonExtDataService;

    private final CommonTableFieldRepository commonTableFieldRepository;

    private final CommonTableRelationshipRepository commonTableRelationshipRepository;

    public CommonTableService(
        CommonTableRepository commonTableRepository,
        CommonExtDataService commonExtDataService,
        CacheManager cacheManager,
        CommonTableMapper commonTableMapper,
        UserRepository userRepository,
        CommonTableFieldRepository commonTableFieldRepository,
        CommonTableRelationshipRepository commonTableRelationshipRepository
    ) {
        this.commonTableRepository = commonTableRepository;
        this.cacheManager = cacheManager;
        this.commonTableMapper = commonTableMapper;
        this.userRepository = userRepository;
        this.commonExtDataService = commonExtDataService;
        this.commonTableFieldRepository = commonTableFieldRepository;
        this.commonTableRelationshipRepository = commonTableRelationshipRepository;
    }

    /**
     * Save a commonTable.
     *
     * @param commonTableDTO the entity to save.
     * @return the persisted entity.
     */
    public CommonTableDTO save(CommonTableDTO commonTableDTO) {
        log.debug("Request to save CommonTable : {}", commonTableDTO);
        // 获得未修改数据
        // Optional<CommonTableDTO> old = this.findOne(commonTableDTO.getId());
        // 提取扩展数据
        Map<String, Object> extData = commonTableDTO.getExtData();
        CommonTable commonTable = commonTableMapper.toEntity(commonTableDTO);
        commonTable = commonTableRepository.save(commonTable);
        if (!extData.isEmpty()) {
            commonTable.setExtData(extData);
            commonExtDataService.saveExtData(commonTable);
        }
        return commonTableMapper.toDto(commonTable);
    }

    /**
     * Get all the commonTables.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CommonTableDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CommonTables");
        return commonTableRepository.findAll(pageable).map(commonTableMapper::toDto);
    }

    /**
     * count all the commonTables.
     *
     * @return the count of entities
     * by wangxin
     */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Request to count all CommonTables");
        return commonTableRepository.count();
    }

    /**
     * Get one commonTable by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommonTableDTO> findOne(Long id) {
        log.debug("Request to get CommonTable : {}", id);
        return commonTableRepository.findById(id).map(commonTableMapper::toDto);
    }

    /**
     * Delete the commonTable by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CommonTable : {}", id);
        commonTableRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CommonTableDTO> findByCreatorIsCurrentUser() {
        return commonTableMapper.toDto(commonTableRepository.findByCreatorIsCurrentUser());
    }

    /**
     * Update ignore specified fields by commonTable
     */
    public CommonTableDTO updateByIgnoreSpecifiedFields(CommonTableDTO changeCommonTableDTO, Set<String> unchangedFields) {
        CommonTableDTO commonTableDTO = findOne(changeCommonTableDTO.getId()).get();
        BeanUtil.copyProperties(changeCommonTableDTO, commonTableDTO, unchangedFields.toArray(new String[0]));
        commonTableDTO = save(commonTableDTO);
        return commonTableDTO;
    }

    /**
     * Update specified fields by commonTable
     */
    public CommonTableDTO updateBySpecifiedFields(CommonTableDTO changeCommonTableDTO, Set<String> fieldNames) {
        CommonTableDTO commonTableDTO = findOne(changeCommonTableDTO.getId()).get();
        CommonTableDTO finalCommonTableDTO = commonTableDTO;
        fieldNames.forEach(
            fieldName -> {
                BeanUtil.setFieldValue(finalCommonTableDTO, fieldName, BeanUtil.getFieldValue(changeCommonTableDTO, fieldName));
            }
        );
        commonTableDTO = save(finalCommonTableDTO);
        return commonTableDTO;
    }

    public Optional<CommonTableDTO> copyFromId(Long commonTableId) {
        Optional<CommonTable> byId = commonTableRepository.findById(commonTableId);
        if (byId.isPresent()) {
            CommonTable commonTable = byId.get();
            CommonTable result = new CommonTable();
            BeanUtil.copyProperties(commonTable, result, "creator", "relationships", "commonTableFields");
            result
                .name("copy" + result.getName())
                .entityName("copy" + result.getEntityName())
                .id(null)
                .baseTableId(commonTableId)
                .system(false);
            SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).ifPresent(result::setCreator);
            result = commonTableRepository.save(result);
            CommonTable finalResult = result;
            commonTable
                .getCommonTableFields()
                .forEach(
                    commonTableField -> {
                        CommonTableField newField = new CommonTableField();
                        BeanUtil.copyProperties(commonTableField, newField, "commonTable");
                        newField.setCommonTable(finalResult);
                        newField.setId(null);
                        commonTableFieldRepository.save(newField);
                    }
                );
            commonTable
                .getRelationships()
                .forEach(
                    commonTableRelationship -> {
                        CommonTableRelationship newRelationship = new CommonTableRelationship();
                        BeanUtil.copyProperties(commonTableRelationship, newRelationship, "commonTable");
                        newRelationship.setCommonTable(finalResult);
                        newRelationship.setId(null);
                        commonTableRelationshipRepository.save(newRelationship);
                    }
                );
            return findOne(result.getId());
        } else {
            throw new BadRequestAlertException("Invalid commonTableId", "CommonTable", "IdNotFound");
        }
    }

    /**
     * Update specified field by commonTable
     */
    public CommonTableDTO updateBySpecifiedField(CommonTableDTO changeCommonTableDTO, String fieldName) {
        CommonTableDTO commonTableDTO = findOne(changeCommonTableDTO.getId()).get();
        BeanUtil.setFieldValue(commonTableDTO, fieldName, BeanUtil.getFieldValue(changeCommonTableDTO, fieldName));
        commonTableDTO = save(commonTableDTO);
        return commonTableDTO;
    }

    public Optional<CommonTableDTO> findOneByEntityName(String entityName) {
        return commonTableRepository.findOneByEntityName(entityName).map(commonTableMapper::toDto);
    }
    // jhipster-needle-service-add-method - JHipster will add getters and setters here, do not remove

}
