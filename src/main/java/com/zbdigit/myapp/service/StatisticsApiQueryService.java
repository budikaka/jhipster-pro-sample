package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.zbdigit.myapp.domain.*; // for static metamodels
import com.zbdigit.myapp.domain.StatisticsApi;
import com.zbdigit.myapp.domain.enumeration.RelationshipType;
import com.zbdigit.myapp.repository.CommonTableRepository;
import com.zbdigit.myapp.repository.StatisticsApiRepository;
import com.zbdigit.myapp.service.criteria.StatisticsApiCriteria;
import com.zbdigit.myapp.service.dto.StatisticsApiDTO;
import com.zbdigit.myapp.service.mapper.StatisticsApiMapper;
import com.zbdigit.myapp.util.JpaUtil;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Service for executing complex queries for {@link StatisticsApi} entities in the database.
 * The main input is a {@link StatisticsApiCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StatisticsApiDTO} or a {@link Page} of {@link StatisticsApiDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatisticsApiQueryService extends QueryService<StatisticsApi> {

    private final Logger log = LoggerFactory.getLogger(StatisticsApiQueryService.class);

    private final StatisticsApiRepository statisticsApiRepository;

    private final EntityManager em;

    private final CommonTableRepository commonTableRepository;

    private final StatisticsApiMapper statisticsApiMapper;

    public StatisticsApiQueryService(
        StatisticsApiRepository statisticsApiRepository,
        EntityManager em,
        CommonTableRepository commonTableRepository,
        StatisticsApiMapper statisticsApiMapper
    ) {
        this.statisticsApiRepository = statisticsApiRepository;
        this.em = em;
        this.commonTableRepository = commonTableRepository;
        this.statisticsApiMapper = statisticsApiMapper;
    }

    /**
     * Return a {@link List} of {@link StatisticsApiDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StatisticsApiDTO> findByCriteria(StatisticsApiCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StatisticsApi> specification = createSpecification(criteria);
        return statisticsApiMapper.toDto(statisticsApiRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StatisticsApiDTO} which matches the criteria from the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatisticsApiDTO> findBySpecification(Specification<StatisticsApi> specification, Pageable page) {
        log.debug("find by criteria : {}, page: {}", specification, page);
        return statisticsApiRepository.findAll(specification, page).map(statisticsApiMapper::toDto);
    }

    /**
     * Return a {@link Page} of {@link StatisticsApiDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatisticsApiDTO> findByCriteria(StatisticsApiCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StatisticsApi> specification = createSpecification(criteria);
        return statisticsApiRepository.findAll(specification, page).map(statisticsApiMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StatisticsApiCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StatisticsApi> specification = createSpecification(criteria);
        return statisticsApiRepository.count(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countBySpecification(Specification specification) {
        log.debug("count by specification : {}", specification);
        return statisticsApiRepository.count(specification);
    }

    /**
     * Function to convert {@link StatisticsApiCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StatisticsApi> createSpecification(StatisticsApiCriteria criteria) {
        Specification<StatisticsApi> specification = Specification.where(null);
        if (criteria != null) {
            if (StringUtils.isNotEmpty(criteria.getJhiCommonSearchKeywords())) {
                if (StringUtils.isNumeric(criteria.getJhiCommonSearchKeywords())) {
                    specification =
                        specification.or(
                            buildSpecification(
                                new LongFilter().setEquals(Long.valueOf(criteria.getJhiCommonSearchKeywords())),
                                StatisticsApi_.id
                            )
                        );
                    specification =
                        specification.or(
                            buildRangeSpecification(
                                (IntegerFilter) new IntegerFilter().setEquals(Integer.valueOf(criteria.getJhiCommonSearchKeywords())),
                                StatisticsApi_.updateInterval
                            )
                        );
                } else {
                    specification =
                        specification.or(
                            buildStringSpecification(
                                new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()),
                                StatisticsApi_.title
                            )
                        );
                    specification =
                        specification.or(
                            buildStringSpecification(
                                new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()),
                                StatisticsApi_.apiKey
                            )
                        );
                }
            } else {
                if (criteria.getId() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getId(), StatisticsApi_.id));
                }
                if (criteria.getTitle() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getTitle(), StatisticsApi_.title));
                }
                if (criteria.getApiKey() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getApiKey(), StatisticsApi_.apiKey));
                }
                if (criteria.getCreateAt() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getCreateAt(), StatisticsApi_.createAt));
                }
                if (criteria.getUpdateAt() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getUpdateAt(), StatisticsApi_.updateAt));
                }
                if (criteria.getSourceType() != null) {
                    specification = specification.and(buildSpecification(criteria.getSourceType(), StatisticsApi_.sourceType));
                }
                if (criteria.getUpdateInterval() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getUpdateInterval(), StatisticsApi_.updateInterval));
                }
                if (criteria.getLastSQLRunTime() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getLastSQLRunTime(), StatisticsApi_.lastSQLRunTime));
                }
                if (criteria.getCommonTableId() != null) {
                    specification =
                        specification.and(
                            buildSpecification(
                                criteria.getCommonTableId(),
                                root -> root.join(StatisticsApi_.commonTable, JoinType.LEFT).get(CommonTable_.id)
                            )
                        );
                }
                if (criteria.getCreatorId() != null) {
                    specification =
                        specification.and(
                            buildSpecification(
                                criteria.getCreatorId(),
                                root -> root.join(StatisticsApi_.creator, JoinType.LEFT).get(User_.id)
                            )
                        );
                }
                if (criteria.getModifierId() != null) {
                    specification =
                        specification.and(
                            buildSpecification(
                                criteria.getModifierId(),
                                root -> root.join(StatisticsApi_.modifier, JoinType.LEFT).get(User_.id)
                            )
                        );
                }
            }
        }
        return specification;
    }

    @Transactional
    public boolean updateBySpecifield(String fieldName, Object value, StatisticsApiCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<StatisticsApi> q = cb.createCriteriaUpdate(StatisticsApi.class);
        CriteriaQuery<StatisticsApi> sq = cb.createQuery(StatisticsApi.class);
        Root<StatisticsApi> root = q.from(StatisticsApi.class);
        q.set(root.get(fieldName), value).where(createSpecification(criteria).toPredicate(root, sq, cb));
        int result = em.createQuery(q).executeUpdate();
        em.flush();
        return result > 0;
    }

    /**
     * 直接转换为dto。maytoone的，直接查询结果。one-to-many和many-to-many后续加载
     * @param entityName 模型名称
     * @param criteria 条件表达式
     * @param predicate 条件
     * @param pageable 分页
     * @return Page<StatisticsApiDTO>
     */
    @Transactional(readOnly = true)
    public Page<StatisticsApiDTO> selectByCustomEntity(
        String entityName,
        StatisticsApiCriteria criteria,
        Predicate predicate,
        Specification specification,
        Pageable pageable
    ) {
        List<StatisticsApi> dataList = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<StatisticsApi> root = q.from(StatisticsApi.class);
        if (StringUtils.isEmpty(entityName)) {
            entityName = "StatisticsApi";
        }
        Optional<CommonTable> oneByEntityName = commonTableRepository.findOneByEntityName(entityName);
        List<String> fields = new ArrayList<>();
        List<CommonTableRelationship> toManyRelationships = new ArrayList<>();
        List<CommonTableRelationship> manyToOneRelationships = new ArrayList<>();
        List<Selection<?>> s = new ArrayList<>();
        if (oneByEntityName.isPresent()) {
            List<CommonTableField> tableFields = oneByEntityName
                .get()
                .getCommonTableFields()
                .stream()
                .filter(commonTableField -> !commonTableField.getHideInList())
                .collect(Collectors.toList());
            tableFields.forEach(commonTableField -> fields.add(commonTableField.getEntityFieldName()));
            List<CommonTableRelationship> tableRelationships = oneByEntityName
                .get()
                .getRelationships()
                .stream()
                .filter(commonTableRelationship -> !commonTableRelationship.getHideInList())
                .collect(Collectors.toList());
            tableRelationships.forEach(
                commonTableRelationship -> {
                    if (
                        commonTableRelationship.getRelationshipType().equals(RelationshipType.ONE_TO_MANY) ||
                        commonTableRelationship.getRelationshipType().equals(RelationshipType.MANY_TO_MANY)
                    ) {
                        toManyRelationships.add(commonTableRelationship);
                    } else {
                        root.join(commonTableRelationship.getRelationshipName(), JoinType.LEFT);
                        s.add(
                            root
                                .get(commonTableRelationship.getRelationshipName())
                                .get("id")
                                .alias(commonTableRelationship.getRelationshipName() + ".id")
                        );
                        String relationPredicateAlias =
                            commonTableRelationship.getRelationshipName() + "." + commonTableRelationship.getOtherEntityField();
                        if (s.stream().noneMatch(predicateRelation -> predicateRelation.getAlias().equals(relationPredicateAlias))) {
                            s.add(
                                root
                                    .get(commonTableRelationship.getRelationshipName())
                                    .get(commonTableRelationship.getOtherEntityField())
                                    .alias(relationPredicateAlias)
                            );
                        }
                    }
                }
            );
        }
        s.addAll(fields.stream().map(fieldName -> root.get(fieldName).alias(fieldName)).collect(Collectors.toList()));
        q.multiselect(s);
        Predicate criteriaPredicate = createSpecification(criteria).toPredicate(root, q, cb);
        Predicate specificationPredicate = specification == null ? null : specification.toPredicate(root, q, cb);
        if (criteriaPredicate != null) {
            q.where(criteriaPredicate);
        } else if (predicate != null) {
            q.where(predicate);
        } else if (specificationPredicate != null) {
            q.where(specificationPredicate);
        }
        q.distinct(true);
        long totalItems;
        if (specificationPredicate != null) {
            totalItems = countBySpecification(specification);
        } else {
            totalItems = countByCriteria(criteria);
        }
        if (totalItems > 0) {
            if (pageable != null) {
                List<Order> orders = new ArrayList<>();
                pageable
                    .getSort()
                    .forEach(
                        order ->
                            orders.add(order.isAscending() ? cb.asc(root.get(order.getProperty())) : cb.desc(root.get(order.getProperty())))
                    );
                q.orderBy(orders);
            }
            TypedQuery<Tuple> query = em.createQuery(q);
            if (pageable != null) {
                pageable.getSort().toList();
                query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                query.setMaxResults(pageable.getPageSize());
            }
            List<Tuple> list = query.getResultList();
            for (Tuple tu : list) {
                Map<String, Object> itemmap = new HashMap<>();
                for (TupleElement<?> element : tu.getElements()) {
                    itemmap.put(element.getAlias(), tu.get(element.getAlias()));
                }
                StatisticsApi statisticsApi = BeanUtil.mapToBean(itemmap, StatisticsApi.class, true);
                // 组装manytoone的内容
                manyToOneRelationships.forEach(
                    relationship -> {
                        try {
                            String clazzName = StatisticsApi.class.getDeclaredField(relationship.getRelationshipName()).getType().getName();
                            String idFieldName = JpaUtil.getPkColumn(clazzName);
                            String alaisId = relationship.getRelationshipName() + "." + idFieldName;
                            String alaisField = relationship.getRelationshipName() + "." + relationship.getOtherEntityField();
                            Object relationObject = ReflectUtil.newInstance(clazzName);
                            if (itemmap.get(alaisId) != null) {
                                BeanUtil.setFieldValue(relationObject, idFieldName, itemmap.get(alaisId));
                                if (!alaisField.equals(alaisId)) {
                                    BeanUtil.setFieldValue(relationObject, relationship.getOtherEntityField(), itemmap.get(alaisField));
                                }
                                BeanUtil.setFieldValue(statisticsApi, relationship.getRelationshipName(), relationObject);
                            }
                        } catch (NoSuchFieldException e) {
                            log.error(e.getMessage());
                        }
                    }
                );
                // 组装manytomany等的内容
                toManyRelationships.forEach(
                    commonTableRelationship -> {
                        CriteriaQuery<Tuple> subQuery = cb.createTupleQuery();
                        Class<?> clazz =
                            ((PluralAttributePath<?>) root.get(commonTableRelationship.getRelationshipName())).getAttribute()
                                .getElementType()
                                .getJavaType();
                        Root<?> manyRoot = subQuery.from(clazz);
                        if (clazz.getSimpleName().equals("StatisticsApi")) {
                            BeanUtil.setFieldValue(
                                statisticsApi,
                                commonTableRelationship.getRelationshipName(),
                                this.selectByCustomEntity(
                                        "StatisticsApi",
                                        null,
                                        cb.equal(
                                            manyRoot.get(commonTableRelationship.getOtherEntityRelationshipName()).get("id"),
                                            itemmap.get("id")
                                        ),
                                        null,
                                        null
                                    )
                            );
                        } else {
                            Selection<Object> idAlias = manyRoot.get("id").alias("id");
                            if (commonTableRelationship.getOtherEntityField() != null) {
                                subQuery.multiselect(
                                    idAlias,
                                    manyRoot
                                        .get(commonTableRelationship.getOtherEntityField())
                                        .alias(commonTableRelationship.getOtherEntityField())
                                );
                                subQuery.where(
                                    cb.equal(
                                        manyRoot.get(commonTableRelationship.getOtherEntityRelationshipName()).get("id"),
                                        itemmap.get("id")
                                    )
                                );
                                Set<Object> subdataList = new LinkedHashSet<>();
                                List<Tuple> sublist = em.createQuery(subQuery).getResultList();
                                for (Tuple stu : sublist) {
                                    Map<String, Object> subitemmap = new HashMap<>();
                                    for (TupleElement<?> element : stu.getElements()) {
                                        subitemmap.put(element.getAlias(), stu.get(element.getAlias()));
                                    }
                                    subdataList.add(BeanUtil.mapToBean(subitemmap, clazz, true));
                                }
                                BeanUtil.setFieldValue(statisticsApi, commonTableRelationship.getRelationshipName(), subdataList);
                            } else {
                                // todo 暂时不予处理getOtherEntityField为null的情况。
                            }
                        }
                    }
                );
                dataList.add(statisticsApi);
            }
        }
        return new PageImpl<>(statisticsApiMapper.toDto(dataList), pageable == null ? Pageable.unpaged() : pageable, totalItems);
    }

    public List<Map<String, Object>> findAllByJpql(String jqpl) {
        Query query = em.createQuery(jqpl);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    public Map<String, Object> getByJpql(String jqpl) {
        Query query = em.createQuery(jqpl);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (Map<String, Object>) query.getSingleResult();
    }

    public List runSql(String sql) {
        Query query = em.createNativeQuery(sql);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    public String toUpperFirstChar2(String string) {
        char[] chars = string.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] -= 32;
            return String.valueOf(chars);
        }
        return string;
    }
}
