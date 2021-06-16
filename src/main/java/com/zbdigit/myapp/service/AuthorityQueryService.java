package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.zbdigit.myapp.domain.*; // for static metamodels
import com.zbdigit.myapp.domain.Authority;
import com.zbdigit.myapp.domain.enumeration.RelationshipType;
import com.zbdigit.myapp.repository.AuthorityRepository;
import com.zbdigit.myapp.repository.CommonTableRepository;
import com.zbdigit.myapp.service.dto.AuthorityCriteria;
import com.zbdigit.myapp.service.dto.AuthorityDTO;
import com.zbdigit.myapp.service.mapper.AuthorityMapper;
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
import org.hibernate.SQLQuery;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.transform.Transformers;
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

/**
 * Service for executing complex queries for {@link Authority} entities in the database.
 * The main input is a {@link AuthorityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AuthorityDTO} or a {@link Page} of {@link AuthorityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuthorityQueryService extends QueryService<Authority> {

    private final Logger log = LoggerFactory.getLogger(AuthorityQueryService.class);

    private final AuthorityRepository authorityRepository;

    private final EntityManager em;

    private final CommonTableRepository commonTableRepository;

    private final AuthorityMapper authorityMapper;

    public AuthorityQueryService(
        AuthorityRepository authorityRepository,
        EntityManager em,
        CommonTableRepository commonTableRepository,
        AuthorityMapper authorityMapper
    ) {
        this.authorityRepository = authorityRepository;
        this.em = em;
        this.commonTableRepository = commonTableRepository;
        this.authorityMapper = authorityMapper;
    }

    /**
     * Return a {@link List} of {@link AuthorityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AuthorityDTO> findByCriteria(AuthorityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Authority> specification = createSpecification(criteria);
        return authorityMapper.toDto(authorityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AuthorityDTO} which matches the criteria from the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> findBySpecification(Specification<Authority> specification, Pageable page) {
        log.debug("find by criteria : {}, page: {}", specification, page);
        return authorityRepository.findAll(specification, page).map(authorityMapper::toDto);
    }

    /**
     * Return a {@link Page} of {@link AuthorityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> findByCriteria(AuthorityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Authority> specification = createSpecification(criteria);
        return authorityRepository.findAll(specification, page).map(authorityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuthorityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Authority> specification = createSpecification(criteria);
        return authorityRepository.count(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countBySpecification(Specification specification) {
        log.debug("count by specification : {}", specification);
        return authorityRepository.count(specification);
    }

    /**
     * Function to convert {@link AuthorityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Authority> createSpecification(AuthorityCriteria criteria) {
        Specification<Authority> specification = Specification.where(null);
        if (criteria != null) {
            if (StringUtils.isNotEmpty(criteria.getJhiCommonSearchKeywords())) {
                if (StringUtils.isNumeric(criteria.getJhiCommonSearchKeywords())) {
                    specification =
                        specification.or(
                            buildSpecification(
                                new LongFilter().setEquals(Long.valueOf(criteria.getJhiCommonSearchKeywords())),
                                Authority_.id
                            )
                        );
                    specification =
                        specification.or(
                            buildRangeSpecification(
                                (IntegerFilter) new IntegerFilter().setEquals(Integer.valueOf(criteria.getJhiCommonSearchKeywords())),
                                Authority_.order
                            )
                        );
                } else {
                    specification =
                        specification.or(
                            buildStringSpecification(new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()), Authority_.name)
                        );
                    specification =
                        specification.or(
                            buildStringSpecification(new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()), Authority_.code)
                        );
                    specification =
                        specification.or(
                            buildStringSpecification(new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()), Authority_.info)
                        );
                }
            } else {
                if (criteria.getId() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getId(), Authority_.id));
                }
                if (criteria.getName() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getName(), Authority_.name));
                }
                if (criteria.getCode() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getCode(), Authority_.code));
                }
                if (criteria.getInfo() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getInfo(), Authority_.info));
                }
                if (criteria.getOrder() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getOrder(), Authority_.order));
                }
                if (criteria.getDisplay() != null) {
                    specification = specification.and(buildSpecification(criteria.getDisplay(), Authority_.display));
                }
                /* if (criteria.getChildrenId() != null) {
                    specification = specification.and( buildSpecification(criteria.getChildrenId(),
                        root -> root.join(Authority_.children, JoinType.LEFT).get(Authority_.id)));
                }
                if (criteria.getUsersId() != null) {
                    specification = specification.and( buildSpecification(criteria.getUsersId(),
                        root -> root.join(Authority_.users, JoinType.LEFT).get(User_.id)));
                }
                if (criteria.getUsersLogin() != null) {
                    specification = specification.and( buildSpecification(criteria.getUsersLogin(),
                        root -> root.join(Authority_.users, JoinType.LEFT).get(User_.login)));
                }
                if (criteria.getViewPermissionId() != null) {
                    specification = specification.and( buildSpecification(criteria.getViewPermissionId(),
                        root -> root.join(Authority_.viewPermissions, JoinType.LEFT).get(ViewPermission_.id)));
                }
                if (criteria.getViewPermissionText() != null) {
                    specification = specification.and( buildSpecification(criteria.getViewPermissionText(),
                        root -> root.join(Authority_.viewPermissions, JoinType.LEFT).get(ViewPermission_.text)));
                }
                if (criteria.getParentId() != null) {
                    specification = specification.and( buildSpecification(criteria.getParentId(),
                        root -> root.join(Authority_.parent, JoinType.LEFT).get(Authority_.id)));
                }
                if (criteria.getParentName() != null) {
                    specification = specification.and( buildSpecification(criteria.getParentName(),
                        root -> root.join(Authority_.parent, JoinType.LEFT).get(Authority_.name)));
                } */
            }
        }
        return specification;
    }

    @Transactional
    public boolean updateBySpecifield(String fieldName, Object value, AuthorityCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Authority> q = cb.createCriteriaUpdate(Authority.class);
        CriteriaQuery<Authority> sq = cb.createQuery(Authority.class);
        Root<Authority> root = q.from(Authority.class);
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
     * @return Page<AuthorityDTO>
     */
    @Transactional(readOnly = true)
    public Page<AuthorityDTO> selectByCustomEntity(
        String entityName,
        AuthorityCriteria criteria,
        Predicate predicate,
        Specification specification,
        Pageable pageable
    ) {
        List<Authority> dataList = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<Authority> root = q.from(Authority.class);
        if (StringUtils.isEmpty(entityName)) {
            entityName = "Authority";
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
                        manyToOneRelationships.add(commonTableRelationship);
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
                Authority authority = BeanUtil.mapToBean(itemmap, Authority.class, true);
                // 组装manytoone的内容
                manyToOneRelationships.forEach(
                    relationship -> {
                        try {
                            String clazzName = Authority.class.getDeclaredField(relationship.getRelationshipName()).getType().getName();
                            String idFieldName = JpaUtil.getPkColumn(clazzName);
                            String alaisId = relationship.getRelationshipName() + "." + idFieldName;
                            String alaisField = relationship.getRelationshipName() + "." + relationship.getOtherEntityField();
                            Object relationObject = ReflectUtil.newInstance(clazzName);
                            if (itemmap.get(alaisId) != null) {
                                BeanUtil.setFieldValue(relationObject, idFieldName, itemmap.get(alaisId));
                                if (!alaisField.equals(alaisId)) {
                                    BeanUtil.setFieldValue(relationObject, relationship.getOtherEntityField(), itemmap.get(alaisField));
                                }
                                BeanUtil.setFieldValue(authority, relationship.getRelationshipName(), relationObject);
                            }
                        } catch (NoSuchFieldException e) {
                            log.error(e.getMessage());
                        }
                    }
                );
                toManyRelationships.forEach(
                    commonTableRelationship -> {
                        CriteriaQuery<Tuple> subQuery = cb.createTupleQuery();
                        Class<?> clazz =
                            ((PluralAttributePath<?>) root.get(commonTableRelationship.getRelationshipName())).getAttribute()
                                .getElementType()
                                .getJavaType();
                        Root<?> manyRoot = subQuery.from(clazz);
                        if (clazz.getSimpleName().equals("Authority")) {
                            BeanUtil.setFieldValue(
                                authority,
                                commonTableRelationship.getRelationshipName(),
                                this.selectByCustomEntity(
                                        "Authority",
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
                                BeanUtil.setFieldValue(authority, commonTableRelationship.getRelationshipName(), subdataList);
                            } else {
                                // todo 暂时不予处理getOtherEntityField为null的情况。
                            }
                        }
                    }
                );
                dataList.add(authority);
            }
        }
        return new PageImpl<>(authorityMapper.toDto(dataList), pageable == null ? Pageable.unpaged() : pageable, totalItems);
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

    public String toUpperFirstChar2(String string) {
        char[] chars = string.toCharArray();
        if (chars[0] >= 'a' && chars[0] <= 'z') {
            chars[0] -= 32;
            return String.valueOf(chars);
        }
        return string;
    }
}
