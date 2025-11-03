package com.core.domain.user.repository;

import com.core.domain.user.entity.UserEntity;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 사용자 Repository (MyBatis Mapper) - SQL Builder 방식으로 변경
 * 도메인 방식의 패키지 구조에 따른 Repository 인터페이스
 * XML 대신 @SelectProvider, @InsertProvider 등을 사용
 */
@Mapper
public interface UserRepository {

    // ==================== 기본 CRUD 메서드 (SQL Builder) ====================

    /**
     * 모든 사용자 조회 (삭제된 사용자 제외)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findAll")
    @Results(id = "userEntityResultMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "status", column = "status"),
        @Result(property = "orderCount", column = "order_count"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "deletedAt", column = "deleted_at")
    })
    List<UserEntity> findAll();

    /**
     * ID로 사용자 조회
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findById")
    @ResultMap("userEntityResultMap")
    UserEntity findById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findByUsername")
    @ResultMap("userEntityResultMap")
    UserEntity findByUsername(@Param("username") String username);

    /**
     * 이메일로 사용자 조회
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findByEmail")
    @ResultMap("userEntityResultMap")
    UserEntity findByEmail(@Param("email") String email);

    /**
     * 사용자 등록
     */
    @InsertProvider(type = UserRepositorySqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserEntity user);

    /**
     * 사용자 정보 수정
     */
    @UpdateProvider(type = UserRepositorySqlProvider.class, method = "update")
    int update(UserEntity user);

    /**
     * 사용자 삭제
     */
    @DeleteProvider(type = UserRepositorySqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") Long id);

    /**
     * 전체 사용자 수 조회 (삭제된 사용자 제외)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "count")
    int count();

    /**
     * 사용자명 중복 체크
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "existsByUsername")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 이메일 중복 체크
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "existsByEmail")
    boolean existsByEmail(@Param("email") String email);

    // ==================== 극한 동적 쿼리 메서드 (SQL Builder) ====================

    /**
     * 동적 쿼리로 사용자 검색
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "searchUsersWithDynamicQuery")
    @ResultMap("userEntityResultMap")
    List<UserEntity> searchUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적 쿼리로 사용자 수 조회
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "countUsersWithDynamicQuery")
    int countUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적으로 사용자 정보 수정 (NULL이 아닌 필드만 업데이트)
     */
    @UpdateProvider(type = UserRepositorySqlProvider.class, method = "updateUserDynamically")
    int updateUserDynamically(Map<String, Object> params);

    /**
     * 대량 사용자 등록 (Batch Insert)
     */
    @InsertProvider(type = UserRepositorySqlProvider.class, method = "batchInsertUsers")
    int batchInsertUsers(@Param("list") List<UserEntity> users);

    /**
     * 동적 조건으로 사용자 삭제
     */
    @DeleteProvider(type = UserRepositorySqlProvider.class, method = "deleteUsersDynamically")
    int deleteUsersDynamically(UserSearchCriteria criteria);

    /**
     * 주문 정보와 통계를 포함한 사용자 검색 (복잡한 JOIN)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "searchUsersWithOrdersAndStats")
    List<Map<String, Object>> searchUsersWithOrdersAndStats(UserSearchCriteria criteria);
}
