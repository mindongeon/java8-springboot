package com.core.mapper;

import com.core.model.User;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 사용자 Mapper 인터페이스 - SQL Builder 방식으로 변경
 * XML 대신 @SelectProvider, @InsertProvider 등을 사용
 */
@Mapper
public interface UserMapper {

    // ==================== 기본 CRUD 메서드 (SQL Builder) ====================

    /**
     * 모든 사용자 조회
     */
    @SelectProvider(type = UserSqlProvider.class, method = "findAll")
    @Results(id = "userResultMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "createdAt", column = "created_at")
    })
    List<User> findAll();

    /**
     * ID로 사용자 조회
     */
    @SelectProvider(type = UserSqlProvider.class, method = "findById")
    @ResultMap("userResultMap")
    User findById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     */
    @SelectProvider(type = UserSqlProvider.class, method = "findByUsername")
    @ResultMap("userResultMap")
    User findByUsername(@Param("username") String username);

    /**
     * 사용자 등록
     */
    @InsertProvider(type = UserSqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 사용자 정보 수정
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "update")
    int update(User user);

    /**
     * 사용자 삭제
     */
    @DeleteProvider(type = UserSqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") Long id);

    /**
     * 전체 사용자 수 조회
     */
    @SelectProvider(type = UserSqlProvider.class, method = "count")
    int count();

    // ==================== 극한 동적 쿼리 메서드 (SQL Builder) ====================

    /**
     * 동적 쿼리로 사용자 검색
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithSqlBuilder")
    @ResultMap("userResultMap")
    List<User> searchUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적 쿼리로 사용자 수 조회
     */
    @SelectProvider(type = UserSqlProvider.class, method = "countUsersWithSqlBuilder")
    int countUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적으로 사용자 정보 수정 (NULL이 아닌 필드만 업데이트)
     */
    @UpdateProvider(type = UserSqlProvider.class, method = "updateUserDynamically")
    int updateUserDynamically(Map<String, Object> params);

    /**
     * 대량 사용자 등록 (Batch Insert)
     */
    @InsertProvider(type = UserSqlProvider.class, method = "batchInsertUsers")
    int batchInsertUsers(@Param("list") List<User> users);

    /**
     * 동적 조건으로 사용자 삭제
     */
    @DeleteProvider(type = UserSqlProvider.class, method = "deleteUsersDynamically")
    int deleteUsersDynamically(UserSearchCriteria criteria);

    /**
     * 주문 정보와 통계를 포함한 사용자 검색 (복잡한 JOIN)
     */
    @SelectProvider(type = UserSqlProvider.class, method = "searchUsersWithJoinAndAggregation")
    List<Map<String, Object>> searchUsersWithOrdersAndStats(UserSearchCriteria criteria);
}
