package com.core.mapper;

import com.core.model.User;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;

/**
 * MyBatis SQL Builder를 사용한 극한 동적 쿼리 Provider
 * SQL Builder 패턴으로 복잡한 동적 쿼리를 타입 세이프하게 작성
 */
public class UserSqlProvider {

    // ==================== 기본 CRUD SQL Builder 메서드 ====================

    /**
     * 모든 사용자 조회
     */
    public String findAll() {
        return new SQL() {{
            SELECT("id", "username", "email", "created_at");
            FROM("users");
            ORDER_BY("id DESC");
        }}.toString();
    }

    /**
     * ID로 사용자 조회
     */
    public String findById() {
        return new SQL() {{
            SELECT("id", "username", "email", "created_at");
            FROM("users");
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 사용자명으로 사용자 조회
     */
    public String findByUsername() {
        return new SQL() {{
            SELECT("id", "username", "email", "created_at");
            FROM("users");
            WHERE("username = #{username}");
        }}.toString();
    }

    /**
     * 이메일로 사용자 조회
     */
    public String findByEmail() {
        return new SQL() {{
            SELECT("id", "username", "email", "created_at");
            FROM("users");
            WHERE("email = #{email}");
        }}.toString();
    }

    /**
     * 사용자 등록
     */
    public String insert(User user) {
        return new SQL() {{
            INSERT_INTO("users");
            VALUES("username", "#{username}");
            VALUES("email", "#{email}");
            VALUES("created_at", "NOW()");
        }}.toString();
    }

    /**
     * 사용자 정보 수정
     */
    public String update(User user) {
        return new SQL() {{
            UPDATE("users");
            SET("username = #{username}");
            SET("email = #{email}");
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
     * 전체 사용자 수 조회
     */
    public String count() {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("users");
        }}.toString();
    }

    /**
     * 극한 동적 쿼리 - 복잡한 검색 조건
     * SQL Builder의 모든 기능을 활용한 예시
     */
    public String searchUsersWithSqlBuilder(UserSearchCriteria criteria) {
        return new SQL() {{
            // SELECT 절 - 동적으로 컬럼 추가
            SELECT("u.id", "u.username", "u.email", "u.created_at");

            // 추가 계산 컬럼 (조건부)
            if (criteria.getHasOrders() != null && criteria.getHasOrders()) {
                SELECT("(SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count");
            }

            if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
                SELECT("CASE " +
                       "WHEN u.order_count >= 100 THEN 'VIP' " +
                       "WHEN u.order_count >= 50 THEN 'PREMIUM' " +
                       "WHEN u.order_count >= 10 THEN 'REGULAR' " +
                       "ELSE 'NORMAL' END AS priority_level");
            }

            FROM("users u");

            // WHERE 절 - 다양한 동적 조건

            // 1. 단순 equals 조건
            if (criteria.getId() != null) {
                WHERE("u.id = #{id}");
            }

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                WHERE("u.username = #{username}");
            }

            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                WHERE("u.email = #{email}");
            }

            // 2. LIKE 패턴 검색
            if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
            }

            if (criteria.getEmailPattern() != null && !criteria.getEmailPattern().isEmpty()) {
                WHERE("u.email LIKE CONCAT('%', #{emailPattern}, '%')");
            }

            // 3. 범위 검색 (BETWEEN, >=, <=)
            if (criteria.getMinId() != null && criteria.getMaxId() != null) {
                WHERE("u.id BETWEEN #{minId} AND #{maxId}");
            } else if (criteria.getMinId() != null) {
                WHERE("u.id >= #{minId}");
            } else if (criteria.getMaxId() != null) {
                WHERE("u.id <= #{maxId}");
            }

            // 4. 날짜 범위 검색
            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                WHERE("u.created_at BETWEEN #{startDate} AND #{endDate}");
            } else if (criteria.getStartDate() != null) {
                WHERE("u.created_at >= #{startDate}");
            } else if (criteria.getEndDate() != null) {
                WHERE("u.created_at <= #{endDate}");
            }

            // 5. IN 조건
            if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
                WHERE("u.id IN (" + buildInClause(criteria.getIds().size(), "ids") + ")");
            }

            if (criteria.getUsernames() != null && !criteria.getUsernames().isEmpty()) {
                WHERE("u.username IN (" + buildInClause(criteria.getUsernames().size(), "usernames") + ")");
            }

            // 6. Boolean 조건
            if (criteria.getIsActive() != null) {
                WHERE("u.is_active = #{isActive}");
            }

            if (criteria.getIncludeDeleted() == null || !criteria.getIncludeDeleted()) {
                WHERE("u.deleted_at IS NULL");
            }

            // 7. 복잡한 OR 조건 (검색 키워드)
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
                WHERE(orConditions.toString());
            }

            // 8. EXISTS 서브쿼리
            if (criteria.getHasOrders() != null && criteria.getHasOrders()) {
                WHERE("EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)");
            }

            // 9. 집계 함수 조건 (서브쿼리)
            if (criteria.getMinOrderCount() != null && criteria.getMinOrderCount() > 0) {
                WHERE("u.id IN (SELECT o.user_id FROM orders o GROUP BY o.user_id HAVING COUNT(*) >= #{minOrderCount})");
            }

            // 10. CASE WHEN 조건
            if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
                WHERE("(CASE " +
                      "WHEN u.order_count >= 100 THEN 'VIP' " +
                      "WHEN u.order_count >= 50 THEN 'PREMIUM' " +
                      "WHEN u.order_count >= 10 THEN 'REGULAR' " +
                      "ELSE 'NORMAL' END) = #{priorityLevel}");
            }

            // 11. 이메일 도메인 검색 (OR 조건)
            if (criteria.getEmailDomains() != null && !criteria.getEmailDomains().isEmpty()) {
                StringBuilder domainConditions = new StringBuilder("(");
                for (int i = 0; i < criteria.getEmailDomains().size(); i++) {
                    if (i > 0) domainConditions.append(" OR ");
                    domainConditions.append("u.email LIKE CONCAT('%@', #{emailDomains[")
                                   .append(i)
                                   .append("]}))");
                }
                domainConditions.append(")");
                WHERE(domainConditions.toString());
            }

            // 12. 상태 조건 (복잡한 분기)
            if (criteria.getUserStatus() != null) {
                switch (criteria.getUserStatus()) {
                    case "active":
                        WHERE("u.status = 'ACTIVE'");
                        WHERE("u.is_active = true");
                        break;
                    case "inactive":
                        WHERE("(u.status = 'INACTIVE' OR u.is_active = false)");
                        break;
                    case "suspended":
                        WHERE("u.status = 'SUSPENDED'");
                        break;
                }
            }

            // ORDER BY 절 - 동적 정렬
            String orderColumn = "u.id"; // 기본값
            if (criteria.getOrderBy() != null) {
                switch (criteria.getOrderBy()) {
                    case "id":
                        orderColumn = "u.id";
                        break;
                    case "username":
                        orderColumn = "u.username";
                        break;
                    case "email":
                        orderColumn = "u.email";
                        break;
                    case "created_at":
                        orderColumn = "u.created_at";
                        break;
                }
            }

            String direction = "DESC"; // 기본값
            if (criteria.getSortDirection() != null) {
                direction = criteria.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
            }

            ORDER_BY(orderColumn + " " + direction);

            // LIMIT/OFFSET - 페이징
            if (criteria.getLimit() != null && criteria.getLimit() > 0) {
                LIMIT(criteria.getLimit());
            }
            if (criteria.getOffset() != null && criteria.getOffset() >= 0) {
                OFFSET(criteria.getOffset());
            }
        }}.toString();
    }

    /**
     * 동적 COUNT 쿼리
     */
    public String countUsersWithSqlBuilder(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("COUNT(*)");
            FROM("users u");

            // WHERE 조건은 검색 쿼리와 동일
            applyWhereConditions(this, criteria);
        }}.toString();
    }

    /**
     * 극한 동적 쿼리 - 복잡한 JOIN과 집계
     */
    public String searchUsersWithJoinAndAggregation(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("u.id", "u.username", "u.email", "u.created_at");
            SELECT("COALESCE(stats.order_count, 0) AS order_count");
            SELECT("COALESCE(stats.total_amount, 0) AS total_amount");
            SELECT("COALESCE(stats.avg_amount, 0) AS avg_amount");
            SELECT("CASE " +
                   "WHEN COALESCE(stats.order_count, 0) >= 100 THEN 'VIP' " +
                   "WHEN COALESCE(stats.order_count, 0) >= 50 THEN 'PREMIUM' " +
                   "WHEN COALESCE(stats.order_count, 0) >= 10 THEN 'REGULAR' " +
                   "ELSE 'NORMAL' END AS customer_tier");

            FROM("users u");

            // 동적 LEFT JOIN
            if (criteria.getHasOrders() == null || criteria.getHasOrders()) {
                LEFT_OUTER_JOIN("(" +
                    "SELECT user_id, " +
                    "COUNT(*) AS order_count, " +
                    "SUM(amount) AS total_amount, " +
                    "AVG(amount) AS avg_amount " +
                    "FROM orders " +
                    (criteria.getStartDate() != null && criteria.getEndDate() != null ?
                        "WHERE created_at BETWEEN #{startDate} AND #{endDate} " : "") +
                    "GROUP BY user_id" +
                    ") stats ON u.id = stats.user_id");
            }

            // WHERE 조건
            if (criteria.getId() != null) {
                WHERE("u.id = #{id}");
            }

            if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
            }

            if (criteria.getEmailPattern() != null && !criteria.getEmailPattern().isEmpty()) {
                WHERE("u.email LIKE CONCAT('%', #{emailPattern}, '%')");
            }

            if (criteria.getMinOrderCount() != null && criteria.getMinOrderCount() > 0) {
                WHERE("COALESCE(stats.order_count, 0) >= #{minOrderCount}");
            }

            if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
                WHERE("(CASE " +
                      "WHEN COALESCE(stats.order_count, 0) >= 100 THEN 'VIP' " +
                      "WHEN COALESCE(stats.order_count, 0) >= 50 THEN 'PREMIUM' " +
                      "WHEN COALESCE(stats.order_count, 0) >= 10 THEN 'REGULAR' " +
                      "ELSE 'NORMAL' END) = #{priorityLevel}");
            }

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
     * 동적 UPDATE - 조건부 필드 업데이트
     */
    public String updateUserDynamically(Map<String, Object> params) {
        return new SQL() {{
            UPDATE("users");

            // NULL이 아닌 필드만 동적으로 SET
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

            if (params.containsKey("orderCount") && params.get("orderCount") != null) {
                SET("order_count = #{orderCount}");
            }

            // 항상 업데이트 시간 갱신
            SET("updated_at = NOW()");

            // WHERE 조건 (필수)
            WHERE("id = #{id}");
        }}.toString();
    }

    /**
     * 대량 INSERT - SQL Builder로 동적 생성
     * 주의: MyBatis SQL Builder는 VALUES 절 반복을 직접 지원하지 않으므로
     * 문자열 빌더를 조합하는 방식 사용
     */
    public String batchInsertUsers(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) params.get("list");

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO users (username, email, created_at) VALUES ");

        for (int i = 0; i < users.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(#{list[").append(i).append("].username}, ")
               .append("#{list[").append(i).append("].email}, ")
               .append("NOW())");
        }

        return sql.toString();
    }

    /**
     * 동적 DELETE - 복잡한 조건
     */
    public String deleteUsersDynamically(UserSearchCriteria criteria) {
        return new SQL() {{
            DELETE_FROM("users");

            // 동적 WHERE 조건
            if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
                WHERE("id IN (" + buildInClause(criteria.getIds().size(), "ids") + ")");
            }

            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                WHERE("created_at BETWEEN #{startDate} AND #{endDate}");
            }

            if (criteria.getUserStatus() != null && !criteria.getUserStatus().isEmpty()) {
                WHERE("status = #{userStatus}");
            }

            if (criteria.getIsActive() != null) {
                WHERE("is_active = #{isActive}");
            }

            // Soft delete 조건
            if (criteria.getIncludeDeleted() != null && criteria.getIncludeDeleted()) {
                // 모든 레코드 포함
            } else {
                WHERE("deleted_at IS NULL");
            }
        }}.toString();
    }

    /**
     * UNION 쿼리 - 여러 테이블의 데이터 결합
     */
    public String searchUsersWithUnion(UserSearchCriteria criteria) {
        // UNION 쿼리 동적 생성 예시
        StringBuilder sql = new StringBuilder();

        // 첫 번째 쿼리 - 활성 사용자
        sql.append(new SQL() {{
            SELECT("id", "username", "email", "created_at", "'ACTIVE' AS source");
            FROM("users");
            WHERE("is_active = true");
            if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                WHERE("username LIKE CONCAT('%', #{usernamePattern}, '%')");
            }
        }}.toString());

        // UNION 추가 (조건부)
        if (criteria.getIncludeDeleted() != null && criteria.getIncludeDeleted()) {
            sql.append(" UNION ALL ");

            // 두 번째 쿼리 - 삭제된 사용자
            sql.append(new SQL() {{
                SELECT("id", "username", "email", "created_at", "'DELETED' AS source");
                FROM("users");
                WHERE("deleted_at IS NOT NULL");
                if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                    WHERE("username LIKE CONCAT('%', #{usernamePattern}, '%')");
                }
            }}.toString());
        }

        // 전체 결과에 대한 정렬
        if (criteria.getOrderBy() != null) {
            sql.append(" ORDER BY ").append(criteria.getOrderBy());
            if (criteria.getSortDirection() != null) {
                sql.append(" ").append(criteria.getSortDirection());
            }
        }

        return sql.toString();
    }

    /**
     * 복잡한 서브쿼리 - WITH 절 (CTE) 활용
     * SQL Builder로 CTE 표현하기
     */
    public String searchUsersWithCTE(UserSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder();

        // CTE 정의 (WITH 절)
        sql.append("WITH user_stats AS (");
        sql.append(new SQL() {{
            SELECT("user_id");
            SELECT("COUNT(*) AS order_count");
            SELECT("SUM(amount) AS total_amount");
            SELECT("MAX(created_at) AS last_order_date");
            FROM("orders");
            if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
                WHERE("created_at BETWEEN #{startDate} AND #{endDate}");
            }
            GROUP_BY("user_id");
        }}.toString());
        sql.append("), ");

        // 두 번째 CTE - 사용자 등급 계산
        sql.append("user_tiers AS (");
        sql.append(new SQL() {{
            SELECT("user_id");
            SELECT("order_count");
            SELECT("CASE " +
                   "WHEN order_count >= 100 THEN 'VIP' " +
                   "WHEN order_count >= 50 THEN 'PREMIUM' " +
                   "WHEN order_count >= 10 THEN 'REGULAR' " +
                   "ELSE 'NORMAL' END AS tier");
            FROM("user_stats");
        }}.toString());
        sql.append(") ");

        // 메인 쿼리
        sql.append(new SQL() {{
            SELECT("u.id", "u.username", "u.email", "u.created_at");
            SELECT("COALESCE(ut.order_count, 0) AS order_count");
            SELECT("COALESCE(ut.tier, 'NORMAL') AS customer_tier");
            SELECT("us.total_amount", "us.last_order_date");
            FROM("users u");
            LEFT_OUTER_JOIN("user_tiers ut ON u.id = ut.user_id");
            LEFT_OUTER_JOIN("user_stats us ON u.id = us.user_id");

            if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
                WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
            }

            if (criteria.getPriorityLevel() != null && !criteria.getPriorityLevel().isEmpty()) {
                WHERE("ut.tier = #{priorityLevel}");
            }

            if (criteria.getMinOrderCount() != null && criteria.getMinOrderCount() > 0) {
                WHERE("COALESCE(ut.order_count, 0) >= #{minOrderCount}");
            }

            ORDER_BY("u.id DESC");
        }}.toString());

        return sql.toString();
    }

    /**
     * 동적 CASE WHEN 절 생성
     */
    public String selectUsersWithDynamicCase(UserSearchCriteria criteria) {
        return new SQL() {{
            SELECT("u.id", "u.username", "u.email");

            // 동적으로 CASE WHEN 절 생성
            StringBuilder caseWhen = new StringBuilder("CASE ");
            if (criteria.getPriorityLevel() != null) {
                caseWhen.append("WHEN u.order_count >= 100 THEN 'VIP' ")
                        .append("WHEN u.order_count >= 50 THEN 'PREMIUM' ")
                        .append("WHEN u.order_count >= 10 THEN 'REGULAR' ")
                        .append("ELSE 'NORMAL' END AS tier");
                SELECT(caseWhen.toString());
            }

            // 추가 동적 CASE - 상태 표시
            if (criteria.getIsActive() != null) {
                SELECT("CASE " +
                       "WHEN u.is_active = true AND u.deleted_at IS NULL THEN 'ACTIVE' " +
                       "WHEN u.is_active = false THEN 'INACTIVE' " +
                       "WHEN u.deleted_at IS NOT NULL THEN 'DELETED' " +
                       "ELSE 'UNKNOWN' END AS status_display");
            }

            FROM("users u");

            applyWhereConditions(this, criteria);
            applyOrderBy(this, criteria);
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
        if (criteria.getId() != null) {
            sql.WHERE("u.id = #{id}");
        }

        if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
            sql.WHERE("u.username = #{username}");
        }

        if (criteria.getUsernamePattern() != null && !criteria.getUsernamePattern().isEmpty()) {
            sql.WHERE("u.username LIKE CONCAT('%', #{usernamePattern}, '%')");
        }

        if (criteria.getEmailPattern() != null && !criteria.getEmailPattern().isEmpty()) {
            sql.WHERE("u.email LIKE CONCAT('%', #{emailPattern}, '%')");
        }

        if (criteria.getIsActive() != null) {
            sql.WHERE("u.is_active = #{isActive}");
        }

        if (criteria.getIncludeDeleted() == null || !criteria.getIncludeDeleted()) {
            sql.WHERE("u.deleted_at IS NULL");
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