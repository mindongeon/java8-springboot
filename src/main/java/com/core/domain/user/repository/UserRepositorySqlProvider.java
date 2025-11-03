package com.core.domain.user.repository;

import com.core.domain.user.entity.UserEntity;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * UserRepository를 위한 SQL Builder Provider
 * UserEntity를 사용하는 도메인 기반 Repository용 SQL 생성
 */
public class UserRepositorySqlProvider {

    // ==================== 기본 CRUD SQL Builder 메서드 ====================

    /**
     * 모든 사용자 조회 (삭제된 사용자 제외)
     */
    public String findAll() {
        return new SQL() {{
            SELECT("id", "username", "email", "is_active", "status", "order_count",
                   "created_at", "updated_at", "deleted_at");
            FROM("users");
            WHERE("deleted_at IS NULL");
            ORDER_BY("id DESC");
        }}.toString();
    }

    /**
     * ID로 사용자 조회
     */
    public String findById() {
        return new SQL() {{
            SELECT("id", "username", "email", "is_active", "status", "order_count",
                   "created_at", "updated_at", "deleted_at");
            FROM("users");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 사용자명으로 사용자 조회
     */
    public String findByUsername() {
        return new SQL() {{
            SELECT("id", "username", "email", "is_active", "status", "order_count",
                   "created_at", "updated_at", "deleted_at");
            FROM("users");
            WHERE("username = #{username}");
        }}.toString();
    }

    /**
     * 이메일로 사용자 조회
     */
    public String findByEmail() {
        return new SQL() {{
            SELECT("id", "username", "email", "is_active", "status", "order_count",
                   "created_at", "updated_at", "deleted_at");
            FROM("users");
            WHERE("email = #{email}");
        }}.toString();
    }

    /**
     * 사용자 등록
     */
    public String insert(UserEntity user) {
        return new SQL() {{
            INSERT_INTO("users");
            VALUES("username", "#{username}");
            VALUES("email", "#{email}");
            VALUES("is_active", "#{isActive}");
            VALUES("status", "#{status}");
            VALUES("created_at", "NOW()");
        }}.toString();
    }

    /**
     * 사용자 정보 수정
     */
    public String update(UserEntity user) {
        return new SQL() {{
            UPDATE("users");
            SET("username = #{username}");
            SET("email = #{email}");
            SET("is_active = #{isActive}");
            SET("status = #{status}");
            SET("updated_at = NOW()");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 사용자 삭제
     */
    public String deleteById() {
        return new SQL() {{
            DELETE_FROM("users");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 전체 사용자 수 조회 (삭제된 사용자 제외)
     */
    public String count() {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("users");
            WHERE("deleted_at IS NULL");
        }}.toString();
    }

    /**
     * 사용자명 중복 체크
     */
    public String existsByUsername() {
        return new SQL() {{
            SELECT("COUNT(*) > 0");
            FROM("users");
            WHERE("username = #{username}");
            WHERE("deleted_at IS NULL");
        }}.toString();
    }

    /**
     * 이메일 중복 체크
     */
    public String existsByEmail() {
        return new SQL() {{
            SELECT("COUNT(*) > 0");
            FROM("users");
            WHERE("email = #{email}");
            WHERE("deleted_at IS NULL");
        }}.toString();
    }

    // ==================== 극한 동적 쿼리 메서드 ====================

    /**
     * 동적 쿼리로 사용자 검색
     */
    public String searchUsersWithDynamicQuery(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("u.id", "u.username", "u.email", "u.is_active", "u.status", "u.order_count",
                   "u.created_at", "u.updated_at", "u.deleted_at");
            FROM("users u");

            // 동적 WHERE 조건 적용
            applyWhereConditions(this, criteria);

            // 동적 정렬
            applyOrderBy(this, criteria);

            // 페이징
            if (criteria.getLimit() != null && criteria.getLimit() > 0) {
                LIMIT(criteria.getLimit());
            }
            if (criteria.getOffset() != null && criteria.getOffset() >= 0) {
                OFFSET(criteria.getOffset());
            }
        }}.toString();
    }

    /**
     * 동적 쿼리로 사용자 수 조회
     */
    public String countUsersWithDynamicQuery(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("users u");
            applyWhereConditions(this, criteria);
        }}.toString();
    }

    /**
     * 동적으로 사용자 정보 수정
     */
    public String updateUserDynamically(Map<String, Object> params) {
        return new SQL() {{
            UPDATE("users");

            if (params.containsKey("username") && params.get("username") != null) {
                SET("username = #{username}");
            }

            if (params.containsKey("email") && params.get("email") != null) {
                SET("email = #{email}");
            }

            if (params.containsKey("isActive") && params.get("isActive") != null) {
                SET("is_active = #{isActive}");
            }

            if (params.containsKey("status") && params.get("status") != null) {
                SET("status = #{status}");
            }

            SET("updated_at = NOW()");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 대량 사용자 등록
     */
    public String batchInsertUsers(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) params.get("list");

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO users (username, email, is_active, created_at) VALUES ");

        for (int i = 0; i < users.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(#{list[").append(i).append("].username}, ")
               .append("#{list[").append(i).append("].email}, ")
               .append("#{list[").append(i).append("].isActive}, ")
               .append("NOW())");
        }

        return sql.toString();
    }

    /**
     * 동적 조건으로 사용자 삭제
     */
    public String deleteUsersDynamically(UserSearchCriteria criteria) {
        return new SQL() {{
            DELETE_FROM("users");

            if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
                WHERE("id IN (" + buildInClause(criteria.getIds().size(), "ids") + ")");
            }

            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                WHERE("created_at BETWEEN #{startDate} AND #{endDate}");
            }

            if (criteria.getUserStatus() != null && !criteria.getUserStatus().isEmpty()) {
                WHERE("status = #{userStatus}");
            }
        }}.toString();
    }

    /**
     * 주문 정보와 통계를 포함한 사용자 검색
     */
    public String searchUsersWithOrdersAndStats(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("u.id", "u.username", "u.email", "u.created_at");
            SELECT("COALESCE(o.order_count, 0) AS order_count");
            SELECT("COALESCE(o.total_amount, 0) AS total_amount");
            SELECT("CASE " +
                   "WHEN COALESCE(o.order_count, 0) >= 100 THEN 'VIP' " +
                   "WHEN COALESCE(o.order_count, 0) >= 50 THEN 'PREMIUM' " +
                   "WHEN COALESCE(o.order_count, 0) >= 10 THEN 'REGULAR' " +
                   "ELSE 'NORMAL' END AS customer_tier");

            FROM("users u");

            if (criteria.getHasOrders() == null || criteria.getHasOrders()) {
                LEFT_OUTER_JOIN("(" +
                    "SELECT user_id, " +
                    "COUNT(*) AS order_count, " +
                    "SUM(amount) AS total_amount " +
                    "FROM orders " +
                    (criteria.getStartDate() != null && criteria.getEndDate() != null ?
                        "WHERE created_at BETWEEN #{startDate} AND #{endDate} " : "") +
                    "GROUP BY user_id" +
                    ") o ON u.id = o.user_id");
            }

            if (criteria.getId() != null) {
                WHERE("u.id = #{id}");
            }

            if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
            }

            if (criteria.getMinOrderCount() != null && criteria.getMinOrderCount() > 0) {
                WHERE("COALESCE(o.order_count, 0) >= #{minOrderCount}");
            }

            if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
                WHERE("(CASE " +
                      "WHEN COALESCE(o.order_count, 0) >= 100 THEN 'VIP' " +
                      "WHEN COALESCE(o.order_count, 0) >= 50 THEN 'PREMIUM' " +
                      "WHEN COALESCE(o.order_count, 0) >= 10 THEN 'REGULAR' " +
                      "ELSE 'NORMAL' END) = #{priorityLevel}");
            }

            applyOrderBy(this, criteria);

            if (criteria.getLimit() != null && criteria.getLimit() > 0) {
                LIMIT(criteria.getLimit());
            }
            if (criteria.getOffset() != null && criteria.getOffset() >= 0) {
                OFFSET(criteria.getOffset());
            }
        }}.toString();
    }

    // ==================== 헬퍼 메서드 ====================

    /**
     * IN 절 생성 헬퍼
     */
    private String buildInClause(int size, String paramName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append("#{").append(paramName).append("[").append(i).append("]}");
        }
        return sb.toString();
    }

    /**
     * 공통 WHERE 조건 적용
     */
    private void applyWhereConditions(SQL sql, UserSearchCriteria criteria) {
        // 단순 equals 조건
        if (criteria.getId() != null) {
            sql.WHERE("u.id = #{id}");
        }

        if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
            sql.WHERE("u.username = #{username}");
        }

        if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
            sql.WHERE("u.email = #{email}");
        }

        // LIKE 패턴 검색
        if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
            sql.WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
        }

        if (criteria.getEmailPattern() != null && !criteria.getEmailPattern().isEmpty()) {
            sql.WHERE("u.email LIKE CONCAT('%', #{emailPattern}, '%')");
        }

        // 범위 검색
        if (criteria.getMinId() != null && criteria.getMaxId() != null) {
            sql.WHERE("u.id BETWEEN #{minId} AND #{maxId}");
        } else if (criteria.getMinId() != null) {
            sql.WHERE("u.id >= #{minId}");
        } else if (criteria.getMaxId() != null) {
            sql.WHERE("u.id <= #{maxId}");
        }

        // 날짜 범위
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            sql.WHERE("u.created_at BETWEEN #{startDate} AND #{endDate}");
        } else if (criteria.getStartDate() != null) {
            sql.WHERE("u.created_at >= #{startDate}");
        } else if (criteria.getEndDate() != null) {
            sql.WHERE("u.created_at <= #{endDate}");
        }

        // IN 조건
        if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
            sql.WHERE("u.id IN (" + buildInClause(criteria.getIds().size(), "ids") + ")");
        }

        if (criteria.getUsernames() != null && !criteria.getUsernames().isEmpty()) {
            sql.WHERE("u.username IN (" + buildInClause(criteria.getUsernames().size(), "usernames") + ")");
        }

        // 이메일 도메인 검색
        if (criteria.getEmailDomains() != null && !criteria.getEmailDomains().isEmpty()) {
            StringBuilder domainConditions = new StringBuilder("(");
            for (int i = 0; i < criteria.getEmailDomains().size(); i++) {
                if (i > 0) domainConditions.append(" OR ");
                domainConditions.append("u.email LIKE CONCAT('%@', #{emailDomains[")
                               .append(i)
                               .append("]}))");
            }
            domainConditions.append(")");
            sql.WHERE(domainConditions.toString());
        }

        // Boolean 조건
        if (criteria.getIsActive() != null) {
            sql.WHERE("u.is_active = #{isActive}");
        }

        if (criteria.getIncludeDeleted() == null || !criteria.getIncludeDeleted()) {
            sql.WHERE("u.deleted_at IS NULL");
        }

        // 상태 조건
        if (criteria.getUserStatus() != null) {
            switch (criteria.getUserStatus()) {
                case "active":
                    sql.WHERE("u.status = 'ACTIVE'");
                    sql.WHERE("u.is_active = true");
                    break;
                case "inactive":
                    sql.WHERE("(u.status = 'INACTIVE' OR u.is_active = false)");
                    break;
                case "suspended":
                    sql.WHERE("u.status = 'SUSPENDED'");
                    break;
            }
        }

        // 검색 키워드 (OR 조건)
        if (criteria.getSearchKeywords() != null && !criteria.getSearchKeywords().isEmpty()) {
            StringBuilder orConditions = new StringBuilder("(");
            for (int i = 0; i < criteria.getSearchKeywords().size(); i++) {
                if (i > 0) orConditions.append(" OR ");
                orConditions.append("(u.username LIKE CONCAT('%', #{searchKeywords[")
                           .append(i)
                           .append("]}, '%') OR u.email LIKE CONCAT('%', #{searchKeywords[")
                           .append(i)
                           .append("]}, '%'))");
            }
            orConditions.append(")");
            sql.WHERE(orConditions.toString());
        }

        // EXISTS 서브쿼리
        if (criteria.getHasOrders() != null && criteria.getHasOrders()) {
            sql.WHERE("EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)");
        }

        // 집계 조건
        if (criteria.getMinOrderCount() != null && criteria.getMinOrderCount() > 0) {
            sql.WHERE("u.id IN (SELECT o.user_id FROM orders o GROUP BY o.user_id HAVING COUNT(*) >= #{minOrderCount})");
        }

        // CASE WHEN 조건
        if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
            sql.WHERE("(CASE " +
                      "WHEN u.order_count >= 100 THEN 'VIP' " +
                      "WHEN u.order_count >= 50 THEN 'PREMIUM' " +
                      "WHEN u.order_count >= 10 THEN 'REGULAR' " +
                      "ELSE 'NORMAL' END) = #{priorityLevel}");
        }
    }

    /**
     * 공통 ORDER BY 적용
     */
    private void applyOrderBy(SQL sql, UserSearchCriteria criteria) {
        String orderColumn = "u.id";
        if (criteria.getOrderBy() != null) {
            switch (criteria.getOrderBy()) {
                case "id": orderColumn = "u.id"; break;
                case "username": orderColumn = "u.username"; break;
                case "email": orderColumn = "u.email"; break;
                case "created_at": orderColumn = "u.created_at"; break;
            }
        }

        String direction = "DESC";
        if (criteria.getSortDirection() != null) {
            direction = criteria.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
        }

        sql.ORDER_BY(orderColumn + " " + direction);
    }
}
