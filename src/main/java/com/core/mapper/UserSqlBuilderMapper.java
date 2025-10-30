package com.core.mapper;

import com.core.model.User;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * SQL Builder를 사용하는 사용자 Mapper
 * @SelectProvider, @UpdateProvider 등을 활용한 극한 동적 쿼리 예시
 */
@Mapper
public interface UserSqlBuilderMapper {

    /**
     * SQL Builder로 복잡한 동적 검색 쿼리 실행
     * 모든 가능한 동적 조건을 포함한 극한의 예시
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithSqlBuilder")
    @Results(id = "userResultMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<User> searchUsersWithSqlBuilder(UserSearchCriteria criteria);

    /**
     * 동적 COUNT 쿼리
     */
    @SelectProvider(type = UserSqlProvider.class, method = "countUsersWithSqlBuilder")
    int countUsersWithSqlBuilder(UserSearchCriteria criteria);

    /**
     * 복잡한 JOIN과 집계를 포함한 검색
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithJoinAndAggregation")
    List<Map<String, Object>> searchUsersWithJoinAndAggregation(UserSearchCriteria criteria);

    /**
     * 동적 UPDATE - NULL이 아닌 필드만 업데이트
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "updateUserDynamically")
    int updateUserDynamically(Map<String, Object> params);

    /**
     * 대량 INSERT - SQL Builder 방식
     */
    @InsertProvider(type = UserSqlProvider.class, method = "batchInsertUsers")
    int batchInsertUsers(@Param("list") List<User> users);

    /**
     * 동적 조건으로 DELETE
     */
    @DeleteProvider(type = UserSqlProvider.class, method = "deleteUsersDynamically")
    int deleteUsersDynamically(UserSearchCriteria criteria);

    /**
     * UNION 쿼리 - 여러 소스의 데이터 결합
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithUnion")
    List<Map<String, Object>> searchUsersWithUnion(UserSearchCriteria criteria);

    /**
     * CTE(Common Table Expression)를 사용한 복잡한 쿼리
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithCTE")
    List<Map<String, Object>> searchUsersWithCTE(UserSearchCriteria criteria);

    /**
     * 동적 CASE WHEN 절을 포함한 검색
     */
    @SelectProvider(type = UserSqlProvider.class, method = "selectUsersWithDynamicCase")
    List<Map<String, Object>> selectUsersWithDynamicCase(UserSearchCriteria criteria);

    // ==================== Annotation 기반 동적 쿼리 추가 예시 ====================

    /**
     * @Select + Script 태그를 사용한 동적 쿼리
     * XML 없이 Annotation으로 동적 쿼리 작성
     */
    @Select({
        "<script>",
        "SELECT u.id, u.username, u.email, u.created_at",
        "FROM users u",
        "<where>",
        "  <if test='id != null'>",
        "    AND u.id = #{id}",
        "  </if>",
        "  <if test='usernamePattern != null'>",
        "    AND u.username LIKE CONCAT('%', #{usernamePattern}, '%')",
        "  </if>",
        "  <if test='emailPattern != null'>",
        "    AND u.email LIKE CONCAT('%', #{emailPattern}, '%')",
        "  </if>",
        "  <if test='isActive != null'>",
        "    AND u.is_active = #{isActive}",
        "  </if>",
        "  <if test='ids != null and ids.size() > 0'>",
        "    AND u.id IN",
        "    <foreach item='id' collection='ids' open='(' separator=',' close=')'>",
        "      #{id}",
        "    </foreach>",
        "  </if>",
        "</where>",
        "ORDER BY u.id DESC",
        "<if test='limit != null and limit > 0'>",
        "  LIMIT #{limit}",
        "</if>",
        "<if test='offset != null and offset >= 0'>",
        "  OFFSET #{offset}",
        "</if>",
        "</script>"
    })
    @ResultMap("userResultMap")
    List<User> searchWithAnnotationDynamicQuery(UserSearchCriteria criteria);

    /**
     * @Update + Script를 사용한 동적 UPDATE
     */
    @Update({
        "<script>",
        "UPDATE users",
        "<set>",
        "  <if test='username != null'>username = #{username},</if>",
        "  <if test='email != null'>email = #{email},</if>",
        "  <if test='isActive != null'>is_active = #{isActive},</if>",
        "  updated_at = NOW()",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int updateWithAnnotationDynamicQuery(Map<String, Object> params);

    /**
     * @Insert + Script를 사용한 대량 INSERT
     */
    @Insert({
        "<script>",
        "INSERT INTO users (username, email, created_at)",
        "VALUES",
        "<foreach collection='list' item='user' separator=','>",
        "  (#{user.username}, #{user.email}, NOW())",
        "</foreach>",
        "</script>"
    })
    int batchInsertWithAnnotation(@Param("list") List<User> users);

    /**
     * @Delete + Script를 사용한 동적 DELETE
     */
    @Delete({
        "<script>",
        "DELETE FROM users",
        "<where>",
        "  <if test='ids != null and ids.size() > 0'>",
        "    AND id IN",
        "    <foreach item='id' collection='ids' open='(' separator=',' close=')'>",
        "      #{id}",
        "    </foreach>",
        "  </if>",
        "  <if test='userStatus != null'>",
        "    AND status = #{userStatus}",
        "  </if>",
        "  <if test='startDate != null and endDate != null'>",
        "    AND created_at BETWEEN #{startDate} AND #{endDate}",
        "  </if>",
        "</where>",
        "</script>"
    })
    int deleteWithAnnotationDynamicQuery(UserSearchCriteria criteria);

    /**
     * 복잡한 Annotation 기반 동적 쿼리 - JOIN, 서브쿼리, CASE WHEN 포함
     */
    @Select({
        "<script>",
        "SELECT ",
        "  u.id,",
        "  u.username,",
        "  u.email,",
        "  u.created_at,",
        "  COALESCE(o.order_count, 0) AS order_count,",
        "  COALESCE(o.total_amount, 0) AS total_amount,",
        "  CASE",
        "    WHEN COALESCE(o.order_count, 0) >= 100 THEN 'VIP'",
        "    WHEN COALESCE(o.order_count, 0) >= 50 THEN 'PREMIUM'",
        "    WHEN COALESCE(o.order_count, 0) >= 10 THEN 'REGULAR'",
        "    ELSE 'NORMAL'",
        "  END AS customer_tier",
        "FROM users u",
        "<if test='hasOrders == null or hasOrders == true'>",
        "  LEFT JOIN (",
        "    SELECT ",
        "      user_id,",
        "      COUNT(*) AS order_count,",
        "      SUM(amount) AS total_amount",
        "    FROM orders",
        "    <where>",
        "      <if test='startDate != null and endDate != null'>",
        "        AND created_at BETWEEN #{startDate} AND #{endDate}",
        "      </if>",
        "    </where>",
        "    GROUP BY user_id",
        "  ) o ON u.id = o.user_id",
        "</if>",
        "<where>",
        "  <if test='usernamePattern != null'>",
        "    AND u.username LIKE CONCAT('%', #{usernamePattern}, '%')",
        "  </if>",
        "  <if test='minOrderCount != null and minOrderCount > 0'>",
        "    AND COALESCE(o.order_count, 0) >= #{minOrderCount}",
        "  </if>",
        "  <if test='priorityLevel != null'>",
        "    AND (",
        "      CASE",
        "        WHEN COALESCE(o.order_count, 0) >= 100 THEN 'VIP'",
        "        WHEN COALESCE(o.order_count, 0) >= 50 THEN 'PREMIUM'",
        "        WHEN COALESCE(o.order_count, 0) >= 10 THEN 'REGULAR'",
        "        ELSE 'NORMAL'",
        "      END",
        "    ) = #{priorityLevel}",
        "  </if>",
        "</where>",
        "<choose>",
        "  <when test='orderBy == \"username\"'>ORDER BY u.username</when>",
        "  <when test='orderBy == \"email\"'>ORDER BY u.email</when>",
        "  <when test='orderBy == \"created_at\"'>ORDER BY u.created_at</when>",
        "  <otherwise>ORDER BY u.id</otherwise>",
        "</choose>",
        "<choose>",
        "  <when test='sortDirection == \"ASC\"'>ASC</when>",
        "  <otherwise>DESC</otherwise>",
        "</choose>",
        "<if test='limit != null and limit > 0'>",
        "  LIMIT #{limit}",
        "</if>",
        "<if test='offset != null and offset >= 0'>",
        "  OFFSET #{offset}",
        "</if>",
        "</script>"
    })
    List<Map<String, Object>> complexAnnotationDynamicQuery(UserSearchCriteria criteria);
}