package com.core.model;

import java.util.List;

/**
 * 사용자 검색 조건 DTO
 * 극한의 동적 쿼리를 위한 다양한 검색 조건을 포함
 */
public class UserSearchCriteria {
    // 기본 검색 조건
    private Long id;
    private String username;
    private String email;
    private String usernamePattern;  // LIKE 검색용
    private String emailPattern;     // LIKE 검색용

    // 범위 검색
    private Long minId;
    private Long maxId;
    private String startDate;
    private String endDate;

    // IN 조건
    private List<Long> ids;
    private List<String> usernames;
    private List<String> emailDomains;

    // 정렬 옵션
    private String orderBy;          // id, username, email, created_at
    private String sortDirection;    // ASC, DESC

    // 페이징
    private Integer limit;
    private Integer offset;

    // 복합 조건
    private Boolean includeDeleted;  // soft delete 가정
    private Boolean isActive;
    private String userStatus;       // active, inactive, suspended 등

    // OR 조건 그룹
    private List<String> searchKeywords;  // username 또는 email에서 검색

    // EXISTS 서브쿼리용
    private Boolean hasOrders;       // 주문이 있는 사용자만
    private Integer minOrderCount;   // 최소 주문 수

    // CASE WHEN 조건용
    private String priorityLevel;    // VIP, NORMAL, etc.

    // 생성자
    public UserSearchCriteria() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsernamePattern() {
        return usernamePattern;
    }

    public void setUsernamePattern(String usernamePattern) {
        this.usernamePattern = usernamePattern;
    }

    public String getEmailPattern() {
        return emailPattern;
    }

    public void setEmailPattern(String emailPattern) {
        this.emailPattern = emailPattern;
    }

    public Long getMinId() {
        return minId;
    }

    public void setMinId(Long minId) {
        this.minId = minId;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getEmailDomains() {
        return emailDomains;
    }

    public void setEmailDomains(List<String> emailDomains) {
        this.emailDomains = emailDomains;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public Boolean getHasOrders() {
        return hasOrders;
    }

    public void setHasOrders(Boolean hasOrders) {
        this.hasOrders = hasOrders;
    }

    public Integer getMinOrderCount() {
        return minOrderCount;
    }

    public void setMinOrderCount(Integer minOrderCount) {
        this.minOrderCount = minOrderCount;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String toString() {
        return "UserSearchCriteria{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", usernamePattern='" + usernamePattern + '\'' +
                ", emailPattern='" + emailPattern + '\'' +
                ", minId=" + minId +
                ", maxId=" + maxId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", ids=" + ids +
                ", usernames=" + usernames +
                ", emailDomains=" + emailDomains +
                ", orderBy='" + orderBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                ", includeDeleted=" + includeDeleted +
                ", isActive=" + isActive +
                ", userStatus='" + userStatus + '\'' +
                ", searchKeywords=" + searchKeywords +
                ", hasOrders=" + hasOrders +
                ", minOrderCount=" + minOrderCount +
                ", priorityLevel='" + priorityLevel + '\'' +
                '}';
    }
}