package com.core.mapper;

import com.core.model.User;
import com.core.model.UserSearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyBatis 극한 동적 쿼리 테스트
 * XML 방식과 SQL Builder 방식의 다양한 동적 쿼리 테스트
 */
@SpringBootTest
@Transactional
public class UserDynamicQueryTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSqlBuilderMapper sqlBuilderMapper;

    // ==================== XML 방식 동적 쿼리 테스트 ====================

    @Test
    @DisplayName("XML 동적 쿼리 - 단순 equals 조건")
    void testXmlDynamicQuery_SimpleEquals() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setId(1L);

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - LIKE 패턴 검색")
    void testXmlDynamicQuery_LikePattern() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setEmailPattern("example.com");

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 범위 검색 (BETWEEN)")
    void testXmlDynamicQuery_RangeSearch() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setMinId(1L);
        criteria.setMaxId(100L);
        criteria.setStartDate("2024-01-01");
        criteria.setEndDate("2024-12-31");

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);

        int count = userMapper.countUsersWithDynamicQuery(criteria);
        assertTrue(count >= 0);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - IN 조건")
    void testXmlDynamicQuery_InClause() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIds(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        criteria.setUsernames(Arrays.asList("user1", "user2", "user3"));
        criteria.setEmailDomains(Arrays.asList("gmail.com", "naver.com", "kakao.com"));

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - Boolean 조건")
    void testXmlDynamicQuery_BooleanConditions() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIsActive(true);
        criteria.setIncludeDeleted(false);

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 상태 조건 (choose-when-otherwise)")
    void testXmlDynamicQuery_StatusConditions() {
        // Active 상태
        UserSearchCriteria criteria1 = new UserSearchCriteria();
        criteria1.setUserStatus("active");
        List<User> activeUsers = userMapper.searchUsersWithDynamicQuery(criteria1);
        assertNotNull(activeUsers);

        // Inactive 상태
        UserSearchCriteria criteria2 = new UserSearchCriteria();
        criteria2.setUserStatus("inactive");
        List<User> inactiveUsers = userMapper.searchUsersWithDynamicQuery(criteria2);
        assertNotNull(inactiveUsers);

        // Suspended 상태
        UserSearchCriteria criteria3 = new UserSearchCriteria();
        criteria3.setUserStatus("suspended");
        List<User> suspendedUsers = userMapper.searchUsersWithDynamicQuery(criteria3);
        assertNotNull(suspendedUsers);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - OR 조건 그룹 (검색 키워드)")
    void testXmlDynamicQuery_OrConditions() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setSearchKeywords(Arrays.asList("john", "jane", "admin"));

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 동적 정렬")
    void testXmlDynamicQuery_DynamicOrderBy() {
        // ID 내림차순
        UserSearchCriteria criteria1 = new UserSearchCriteria();
        criteria1.setOrderBy("id");
        criteria1.setSortDirection("DESC");
        List<User> users1 = userMapper.searchUsersWithDynamicQuery(criteria1);
        assertNotNull(users1);

        // Username 오름차순
        UserSearchCriteria criteria2 = new UserSearchCriteria();
        criteria2.setOrderBy("username");
        criteria2.setSortDirection("ASC");
        List<User> users2 = userMapper.searchUsersWithDynamicQuery(criteria2);
        assertNotNull(users2);

        // Email 내림차순
        UserSearchCriteria criteria3 = new UserSearchCriteria();
        criteria3.setOrderBy("email");
        criteria3.setSortDirection("DESC");
        List<User> users3 = userMapper.searchUsersWithDynamicQuery(criteria3);
        assertNotNull(users3);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 페이징")
    void testXmlDynamicQuery_Pagination() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setLimit(10);
        criteria.setOffset(0);

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
        assertTrue(users.size() <= 10);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 모든 조건 조합")
    void testXmlDynamicQuery_AllConditions() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setEmailPattern("example");
        criteria.setMinId(1L);
        criteria.setMaxId(1000L);
        criteria.setStartDate("2024-01-01");
        criteria.setEndDate("2024-12-31");
        criteria.setIds(Arrays.asList(1L, 2L, 3L));
        criteria.setIsActive(true);
        criteria.setIncludeDeleted(false);
        criteria.setOrderBy("created_at");
        criteria.setSortDirection("DESC");
        criteria.setLimit(20);
        criteria.setOffset(0);

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);

        int count = userMapper.countUsersWithDynamicQuery(criteria);
        assertTrue(count >= 0);
    }

    @Test
    @DisplayName("XML 동적 쿼리 - 복잡한 JOIN과 서브쿼리")
    void testXmlDynamicQuery_ComplexJoinAndSubquery() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setMinOrderCount(5);
        criteria.setPriorityLevel("VIP");

        List<Map<String, Object>> results = userMapper.searchUsersWithOrdersAndStats(criteria);
        assertNotNull(results);
    }

    @Test
    @DisplayName("XML 동적 UPDATE")
    void testXmlDynamicUpdate() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        params.put("username", "updatedUser");
        params.put("email", "updated@example.com");
        params.put("isActive", true);

        int result = userMapper.updateUserDynamically(params);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("XML 대량 INSERT")
    void testXmlBatchInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setUsername("batchUser" + i);
            user.setEmail("batch" + i + "@example.com");
            users.add(user);
        }

        int result = userMapper.batchInsertUsers(users);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("XML 동적 DELETE")
    void testXmlDynamicDelete() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIds(Arrays.asList(100L, 101L, 102L));
        criteria.setUserStatus("inactive");

        int result = userMapper.deleteUsersDynamically(criteria);
        assertTrue(result >= 0);
    }

    // ==================== SQL Builder 방식 동적 쿼리 테스트 ====================

    @Test
    @DisplayName("SQL Builder - 기본 동적 검색")
    void testSqlBuilder_BasicSearch() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setIsActive(true);

        List<User> users = sqlBuilderMapper.searchUsersWithSqlBuilder(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("SQL Builder - 복잡한 조건 조합")
    void testSqlBuilder_ComplexConditions() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("admin");
        criteria.setEmailPattern("company");
        criteria.setMinId(1L);
        criteria.setMaxId(500L);
        criteria.setIds(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        criteria.setIsActive(true);
        criteria.setSearchKeywords(Arrays.asList("manager", "supervisor"));
        criteria.setOrderBy("username");
        criteria.setSortDirection("ASC");
        criteria.setLimit(50);

        List<User> users = sqlBuilderMapper.searchUsersWithSqlBuilder(criteria);
        assertNotNull(users);

        int count = sqlBuilderMapper.countUsersWithSqlBuilder(criteria);
        assertTrue(count >= 0);
    }

    @Test
    @DisplayName("SQL Builder - JOIN과 집계")
    void testSqlBuilder_JoinAndAggregation() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setMinOrderCount(10);
        criteria.setPriorityLevel("PREMIUM");
        criteria.setStartDate("2024-01-01");
        criteria.setEndDate("2024-12-31");

        List<Map<String, Object>> results = sqlBuilderMapper.searchUsersWithJoinAndAggregation(criteria);
        assertNotNull(results);
    }

    @Test
    @DisplayName("SQL Builder - 동적 UPDATE")
    void testSqlBuilder_DynamicUpdate() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        params.put("username", "sqlBuilderUpdated");
        params.put("isActive", false);

        int result = sqlBuilderMapper.updateUserDynamically(params);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("SQL Builder - 대량 INSERT")
    void testSqlBuilder_BatchInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("sqlBuilderUser" + i);
            user.setEmail("sqlbuilder" + i + "@test.com");
            users.add(user);
        }

        int result = sqlBuilderMapper.batchInsertUsers(users);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("SQL Builder - 동적 DELETE")
    void testSqlBuilder_DynamicDelete() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUserStatus("test");
        criteria.setIsActive(false);

        int result = sqlBuilderMapper.deleteUsersDynamically(criteria);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("SQL Builder - UNION 쿼리")
    void testSqlBuilder_UnionQuery() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setIncludeDeleted(true);

        List<Map<String, Object>> results = sqlBuilderMapper.searchUsersWithUnion(criteria);
        assertNotNull(results);
    }

    @Test
    @DisplayName("SQL Builder - CTE 쿼리")
    void testSqlBuilder_CTEQuery() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setPriorityLevel("VIP");
        criteria.setMinOrderCount(50);

        List<Map<String, Object>> results = sqlBuilderMapper.searchUsersWithCTE(criteria);
        assertNotNull(results);
    }

    @Test
    @DisplayName("SQL Builder - 동적 CASE WHEN")
    void testSqlBuilder_DynamicCaseWhen() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setPriorityLevel("PREMIUM");
        criteria.setIsActive(true);

        List<Map<String, Object>> results = sqlBuilderMapper.selectUsersWithDynamicCase(criteria);
        assertNotNull(results);
    }

    // ==================== Annotation 방식 동적 쿼리 테스트 ====================

    @Test
    @DisplayName("Annotation - Script 태그 동적 검색")
    void testAnnotation_ScriptDynamicSearch() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setIsActive(true);
        criteria.setIds(Arrays.asList(1L, 2L, 3L));
        criteria.setLimit(10);

        List<User> users = sqlBuilderMapper.searchWithAnnotationDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("Annotation - 동적 UPDATE")
    void testAnnotation_DynamicUpdate() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", 1L);
        params.put("username", "annotationUpdated");
        params.put("email", "annotation@test.com");

        int result = sqlBuilderMapper.updateWithAnnotationDynamicQuery(params);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("Annotation - 대량 INSERT")
    void testAnnotation_BatchInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("annotationUser" + i);
            user.setEmail("annotation" + i + "@test.com");
            users.add(user);
        }

        int result = sqlBuilderMapper.batchInsertWithAnnotation(users);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("Annotation - 동적 DELETE")
    void testAnnotation_DynamicDelete() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIds(Arrays.asList(200L, 201L, 202L));
        criteria.setUserStatus("test");

        int result = sqlBuilderMapper.deleteWithAnnotationDynamicQuery(criteria);
        assertTrue(result >= 0);
    }

    @Test
    @DisplayName("Annotation - 복잡한 동적 쿼리 (JOIN, 서브쿼리, CASE)")
    void testAnnotation_ComplexDynamicQuery() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsernamePattern("test");
        criteria.setMinOrderCount(5);
        criteria.setPriorityLevel("REGULAR");
        criteria.setStartDate("2024-01-01");
        criteria.setEndDate("2024-12-31");
        criteria.setOrderBy("username");
        criteria.setSortDirection("ASC");
        criteria.setLimit(20);

        List<Map<String, Object>> results = sqlBuilderMapper.complexAnnotationDynamicQuery(criteria);
        assertNotNull(results);
    }

    // ==================== 엣지 케이스 테스트 ====================

    @Test
    @DisplayName("빈 조건으로 검색 (모든 조건 NULL)")
    void testEmptyCriteria() {
        UserSearchCriteria criteria = new UserSearchCriteria();

        List<User> xmlUsers = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(xmlUsers);

        List<User> builderUsers = sqlBuilderMapper.searchUsersWithSqlBuilder(criteria);
        assertNotNull(builderUsers);
    }

    @Test
    @DisplayName("빈 리스트로 IN 조건 테스트")
    void testEmptyListInClause() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setIds(Collections.emptyList());
        criteria.setUsernames(Collections.emptyList());

        List<User> users = userMapper.searchUsersWithDynamicQuery(criteria);
        assertNotNull(users);
    }

    @Test
    @DisplayName("NULL과 빈 문자열 혼합 조건")
    void testNullAndEmptyStringConditions() {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setUsername("");
        criteria.setEmail(null);
        criteria.setUsernamePattern("");

        List<User> users = sqlBuilderMapper.searchUsersWithSqlBuilder(criteria);
        assertNotNull(users);
    }
}