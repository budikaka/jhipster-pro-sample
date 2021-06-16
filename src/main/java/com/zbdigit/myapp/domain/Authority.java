package com.zbdigit.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 角色
 * 采用自分组的形式,采用向下包含关系，选中本节点继承父层并包含本节点内容及其所有子节点内容。
 */

@Entity
@Table(name = "jhi_authority")
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 角色代号
     */
    @Column(name = "code")
    private String code;

    /**
     * 信息
     */
    @Column(name = "info")
    private String info;

    /**
     * 排序
     */
    @Column(name = "jhi_order")
    private Integer order;

    /**
     * 展示
     */
    @Column(name = "display")
    private Boolean display;

    /**
     * 子节点
     */
    @OneToMany(mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> children = new LinkedHashSet<>();

    /**
     * 用户
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(
        name = "jhi_user_authority",
        joinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<User> users = new LinkedHashSet<>();

    /**
     * 可视权限
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(
        name = "rel_jhi_authority__view_permissions",
        joinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "view_permissions_id", referencedColumnName = "id")
    )
    private Set<ViewPermission> viewPermissions = new LinkedHashSet<>();

    /**
     * API权限
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(
        name = "rel_jhi_authority__api_permissions",
        joinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "api_permissions_id", referencedColumnName = "id")
    )
    private Set<ApiPermission> apiPermissions = new LinkedHashSet<>();

    /**
     * 上级
     */
    @ManyToOne
    @JsonIgnoreProperties("children")
    private Authority parent;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Authority name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public Authority code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public Authority info(String info) {
        this.info = info;
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getOrder() {
        return order;
    }

    public Authority order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getDisplay() {
        return display;
    }

    public Authority display(Boolean display) {
        this.display = display;
        return this;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Set<Authority> getChildren() {
        return children;
    }

    public Authority children(Set<Authority> authorities) {
        this.children = authorities;
        return this;
    }

    public Authority addChildren(Authority authority) {
        this.children.add(authority);
        authority.setParent(this);
        return this;
    }

    public Authority removeChildren(Authority authority) {
        this.children.remove(authority);
        authority.setParent(null);
        return this;
    }

    public void setChildren(Set<Authority> children) {
        this.children = children;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Authority users(Set<User> users) {
        this.users = users;
        return this;
    }

    public Authority addUsers(User user) {
        this.users.add(user);
        return this;
    }

    public Authority removeUsers(User user) {
        this.users.remove(user);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<ViewPermission> getViewPermissions() {
        return viewPermissions;
    }

    public Authority viewPermissions(Set<ViewPermission> viewPermissions) {
        this.viewPermissions = viewPermissions;
        return this;
    }

    public Authority addViewPermissions(ViewPermission viewPermission) {
        this.viewPermissions.add(viewPermission);
        viewPermission.getAuthorities().add(this);
        return this;
    }

    public Authority removeViewPermissions(ViewPermission viewPermission) {
        this.viewPermissions.remove(viewPermission);
        viewPermission.getAuthorities().remove(this);
        return this;
    }

    public void setViewPermissions(Set<ViewPermission> viewPermissions) {
        this.viewPermissions = viewPermissions;
    }

    public Set<ApiPermission> getApiPermissions() {
        return apiPermissions;
    }

    public void setApiPermissions(Set<ApiPermission> apiPermissions) {
        this.apiPermissions = apiPermissions;
    }

    public Authority apiPermissions(Set<ApiPermission> apiPermissions) {
        this.apiPermissions = apiPermissions;
        return this;
    }

    public Authority addApiPermissions(ApiPermission apiPermission) {
        this.apiPermissions.add(apiPermission);
        apiPermission.getAuthorities().add(this);
        return this;
    }

    public Authority removeApiPermissions(ApiPermission apiPermission) {
        this.apiPermissions.remove(apiPermission);
        apiPermission.getAuthorities().remove(this);
        return this;
    }

    public Authority getParent() {
        return parent;
    }

    public Authority parent(Authority parent) {
        this.parent = parent;
        return this;
    }

    public void setParent(Authority authority) {
        this.parent = authority;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authority)) {
            return false;
        }
        return id != null && id.equals(((Authority) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Authority{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", info='" + getInfo() + "'" +
            ", order=" + getOrder() +
            ", display='" + getDisplay() + "'" +
            "}";
    }
}
