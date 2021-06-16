package com.zbdigit.myapp.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.DynaBean;
import cn.hutool.core.util.ReflectUtil;
import com.zbdigit.myapp.anltr.CriteriaLogicExprListener;
import com.zbdigit.myapp.anltr.LogicExprLexer;
import com.zbdigit.myapp.anltr.LogicExprParser;
import com.zbdigit.myapp.domain.*; // for static metamodels
import com.zbdigit.myapp.domain.CommonCondition;
import com.zbdigit.myapp.domain.enumeration.RelationshipType;
import com.zbdigit.myapp.repository.CommonConditionRepository;
import com.zbdigit.myapp.repository.CommonTableRepository;
import com.zbdigit.myapp.service.criteria.CommonConditionCriteria;
import com.zbdigit.myapp.service.dto.CommonConditionDTO;
import com.zbdigit.myapp.service.mapper.CommonConditionMapper;
import com.zbdigit.myapp.util.JpaUtil;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang3.ClassUtils;
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
import tech.jhipster.service.filter.*;

/**
 * Service for executing complex queries for {@link CommonCondition} entities in the database.
 * The main input is a {@link CommonConditionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommonConditionDTO} or a {@link Page} of {@link CommonConditionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommonConditionQueryService extends QueryService<CommonCondition> {

    private final Logger log = LoggerFactory.getLogger(CommonConditionQueryService.class);

    public static Map<String, Object> specificationMap = new HashMap<>();

    private final CommonConditionRepository commonConditionRepository;

    private final EntityManager em;

    private final CommonTableRepository commonTableRepository;

    private final CommonConditionMapper commonConditionMapper;

    public CommonConditionQueryService(
        CommonConditionRepository commonConditionRepository,
        EntityManager em,
        CommonTableRepository commonTableRepository,
        CommonConditionMapper commonConditionMapper
    ) {
        this.commonConditionRepository = commonConditionRepository;
        this.em = em;
        this.commonTableRepository = commonTableRepository;
        this.commonConditionMapper = commonConditionMapper;
    }

    /**
     * Return a {@link List} of {@link CommonConditionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommonConditionDTO> findByCriteria(CommonConditionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CommonCondition> specification = createSpecification(criteria);
        return commonConditionMapper.toDto(commonConditionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommonConditionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommonConditionDTO> findByCriteria(CommonConditionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CommonCondition> specification = createSpecification(criteria);
        return commonConditionRepository.findAll(specification, page).map(commonConditionMapper::toDto);
    }

    /**
     * Return a {@link Page} of {@link CommonConditionDTO} which matches the criteria from the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommonConditionDTO> findBySpecification(Specification<CommonCondition> specification, Pageable page) {
        log.debug("find by specification : {}, page: {}", specification, page);
        return commonConditionRepository.findAll(specification, page).map(commonConditionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommonConditionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CommonCondition> specification = createSpecification(criteria);
        return commonConditionRepository.count(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param specification The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countBySpecification(Specification specification) {
        log.debug("count by specification : {}", specification);
        return commonConditionRepository.count(specification);
    }

    /**
     * Function to convert {@link CommonConditionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CommonCondition> createSpecification(CommonConditionCriteria criteria) {
        Specification<CommonCondition> specification = Specification.where(null);
        if (criteria != null) {
            if (StringUtils.isNotEmpty(criteria.getJhiCommonSearchKeywords())) {
                if (StringUtils.isNumeric(criteria.getJhiCommonSearchKeywords())) {
                    specification =
                        specification.or(
                            buildSpecification(
                                new LongFilter().setEquals(Long.valueOf(criteria.getJhiCommonSearchKeywords())),
                                CommonCondition_.id
                            )
                        );
                } else {
                    specification =
                        specification.or(
                            buildStringSpecification(
                                new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()),
                                CommonCondition_.name
                            )
                        );
                    specification =
                        specification.or(
                            buildStringSpecification(
                                new StringFilter().setContains(criteria.getJhiCommonSearchKeywords()),
                                CommonCondition_.description
                            )
                        );
                }
            } else {
                if (criteria.getId() != null) {
                    specification = specification.and(buildRangeSpecification(criteria.getId(), CommonCondition_.id));
                }
                if (criteria.getName() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getName(), CommonCondition_.name));
                }
                if (criteria.getDescription() != null) {
                    specification = specification.and(buildStringSpecification(criteria.getDescription(), CommonCondition_.description));
                }
                if (criteria.getLastModifiedTime() != null) {
                    specification =
                        specification.and(buildRangeSpecification(criteria.getLastModifiedTime(), CommonCondition_.lastModifiedTime));
                }
                /*if (criteria.getItemsId() != null) {
                    specification = specification.and( buildSpecification(criteria.getItemsId(),
                        root -> root.join(CommonCondition_.items, JoinType.LEFT).get(CommonConditionItem_.id)));
                }*/
                /*if (criteria.getModifierId() != null) {
                    specification = specification.and( buildSpecification(criteria.getModifierId(),
                        root -> root.join(CommonCondition_.modifier, JoinType.LEFT).get(User_.id)));
                }*/
                /*if (criteria.getCommonTableId() != null) {
                    specification = specification.and( buildSpecification(criteria.getCommonTableId(),
                        root -> root.join(CommonCondition_.commonTable, JoinType.LEFT).get(CommonTable_.id)));
                }*/
                /*if (criteria.getCommonTableClazzName() != null) {
                    specification = specification.and( buildSpecification(criteria.getCommonTableClazzName(),
                        root -> root.join(CommonCondition_.commonTable, JoinType.LEFT).get(CommonTable_.clazzName)));
                }*/
            }
        }
        return specification;
    }

    @Transactional
    public boolean updateBySpecifield(String fieldName, Object value, CommonConditionCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<CommonCondition> q = cb.createCriteriaUpdate(CommonCondition.class);
        CriteriaQuery<CommonCondition> sq = cb.createQuery(CommonCondition.class);
        Root<CommonCondition> root = q.from(CommonCondition.class);
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
     * @return Page<CommonConditionDTO>
     */
    @Transactional(readOnly = true)
    public Page<CommonConditionDTO> selectByCustomEntity(
        String entityName,
        CommonConditionCriteria criteria,
        Predicate predicate,
        Specification specification,
        Pageable pageable
    ) {
        List<CommonCondition> dataList = new ArrayList<>();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<CommonCondition> root = q.from(CommonCondition.class);
        if (StringUtils.isEmpty(entityName)) {
            entityName = "CommonCondition";
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
                CommonCondition commonCondition = BeanUtil.mapToBean(itemmap, CommonCondition.class, true);
                // 组装manytoone的内容
                manyToOneRelationships.forEach(
                    relationship -> {
                        try {
                            String clazzName =
                                CommonCondition.class.getDeclaredField(relationship.getRelationshipName()).getType().getName();
                            String idFieldName = JpaUtil.getPkColumn(clazzName);
                            String alaisId = relationship.getRelationshipName() + "." + idFieldName;
                            String alaisField = relationship.getRelationshipName() + "." + relationship.getOtherEntityField();
                            Object relationObject = ReflectUtil.newInstance(clazzName);
                            if (itemmap.get(alaisId) != null) {
                                BeanUtil.setFieldValue(relationObject, idFieldName, itemmap.get(alaisId));
                                if (!alaisField.equals(alaisId)) {
                                    BeanUtil.setFieldValue(relationObject, relationship.getOtherEntityField(), itemmap.get(alaisField));
                                }
                                BeanUtil.setFieldValue(commonCondition, relationship.getRelationshipName(), relationObject);
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
                        if (clazz.getSimpleName().equals("CommonCondition")) {
                            BeanUtil.setFieldValue(
                                commonCondition,
                                commonTableRelationship.getRelationshipName(),
                                this.selectByCustomEntity(
                                        "CommonCondition",
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
                                BeanUtil.setFieldValue(commonCondition, commonTableRelationship.getRelationshipName(), subdataList);
                            } else {
                                // todo 暂时不予处理getOtherEntityField为null的情况。
                            }
                        }
                    }
                );
                dataList.add(commonCondition);
            }
        }
        return new PageImpl<>(commonConditionMapper.toDto(dataList), pageable == null ? Pageable.unpaged() : pageable, totalItems);
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

    // 把commonQuery转为Specification
    public Specification createSpecification(Long commonQueryId) throws ClassNotFoundException {
        CommonCondition commonCondition = commonConditionRepository.getOne(commonQueryId);
        String packageName = ClassUtils.getPackageName(CommonCondition.class);
        String servicePackageName = ClassUtils.getPackageName(this.getClass());
        Specification specification;
        if (commonCondition != null) {
            Class targetClass = Class.forName(packageName + "." + commonCondition.getCommonTable().getClazzName() + "_");
            Class targetCriteriaClass = Class.forName(
                servicePackageName + ".dto." + commonCondition.getCommonTable().getClazzName() + "Criteria"
            );
            StringBuffer s = new StringBuffer();
            Set<CommonConditionItem> conditionItems = commonCondition.getItems();
            conditionItems.forEach(
                queryItem -> {
                    String prefix = queryItem.getPrefix();
                    String suffix = queryItem.getSuffix();
                    if (StringUtils.isNotEmpty(prefix)) {
                        switch (prefix) {
                            case ")":
                                s.append(" )");
                                break;
                            case "(":
                                s.append(" (");
                                break;
                            case "AND":
                                s.append(" AND");
                                break;
                            case "OR":
                                s.append(" AND");
                                break;
                            default:
                        }
                    }
                    try {
                        Integer specId = specificationMap.size() + 1;
                        specificationMap.put(
                            queryItem.getId() + "_" + specId,
                            createSpecificationByQueryItem(queryItem, targetClass, targetCriteriaClass)
                        );
                        s.append(" " + queryItem.getId() + "_" + specId);
                    } catch (NoSuchFieldException | InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.isNotEmpty(suffix)) {
                        switch (suffix) {
                            case ")":
                                s.append(" )");
                                break;
                            case "(":
                                s.append(" (");
                                break;
                            case "AND":
                                s.append(" AND");
                                break;
                            case "OR":
                                s.append(" AND");
                                break;
                            default:
                        }
                    }
                }
            );
            ANTLRInputStream input = new ANTLRInputStream(s.toString());
            LogicExprLexer lexer = new LogicExprLexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            LogicExprParser parser = new LogicExprParser(tokenStream);
            LogicExprParser.StatContext parseTree = parser.stat();
            CriteriaLogicExprListener visitor = new CriteriaLogicExprListener();
            System.out.println(parseTree.toStringTree(parser)); //打印规则数
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(visitor, parseTree);
            specification = Specification.where(visitor.specifications.get(parseTree));
            return specification;
        } else {
            return null;
        }
    }

    private Specification createSpecificationByQueryItem(
        CommonConditionItem commonConditionItem,
        Class jPAMetaModelClass,
        Class targetCriteriaClass
    ) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        if (
            StringUtils.isNotEmpty(commonConditionItem.getFieldName()) &&
            StringUtils.isNotEmpty(commonConditionItem.getOperator()) &&
            StringUtils.isNotEmpty(commonConditionItem.getValue())
        ) {
            DynaBean criteria = DynaBean.create(targetCriteriaClass.newInstance());
            DynaBean filter;
            switch (commonConditionItem.getFieldType()) {
                case "LONG":
                    filter = DynaBean.create(LongFilter.class);
                    filter.set(commonConditionItem.getOperator(), Long.parseLong(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildRangeSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "INTEGER":
                    filter = DynaBean.create(IntegerFilter.class);
                    filter.set(commonConditionItem.getOperator(), Integer.parseInt(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildRangeSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "STRING":
                    filter = DynaBean.create(new StringFilter());
                    filter.set(commonConditionItem.getOperator(), commonConditionItem.getValue());
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildStringSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "FLOAT":
                    filter = DynaBean.create(FloatFilter.class);
                    filter.set(commonConditionItem.getOperator(), Float.parseFloat(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildRangeSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "DOUBLE":
                    filter = DynaBean.create(DoubleFilter.class);
                    filter.set(commonConditionItem.getOperator(), Double.parseDouble(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildRangeSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "BOOLEAN":
                    filter = DynaBean.create(BooleanFilter.class);
                    filter.set(commonConditionItem.getOperator(), Boolean.parseBoolean(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                case "ZONED_DATE_TIME":
                    filter = DynaBean.create(ZonedDateTimeFilter.class);
                    filter.set(commonConditionItem.getOperator(), ZonedDateTime.parse(commonConditionItem.getValue()));
                    criteria.set(commonConditionItem.getFieldName(), filter.getBean());
                    return buildRangeSpecification(
                        criteria.get(commonConditionItem.getFieldName()),
                        (SingularAttribute) jPAMetaModelClass.getField(commonConditionItem.getFieldName()).get(targetCriteriaClass)
                    );
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
}
