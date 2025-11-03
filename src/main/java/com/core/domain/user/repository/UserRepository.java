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
     * SQL Builder를 사용하여 deleted_at IS NULL 조건으로 삭제되지 않은 사용자만 조회합니다.
     *
     * @return List<UserEntity> 사용자 엔티티 리스트 (ID 내림차순 정렬)
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
     * 지정된 ID의 사용자를 조회합니다. 삭제된 사용자도 포함됩니다.
     *
     * @param id 조회할 사용자 ID
     * @return UserEntity 사용자 엔티티 (존재하지 않으면 null)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findById")
    @ResultMap("userEntityResultMap")
    UserEntity findById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     * 지정된 사용자명으로 사용자를 조회합니다.
     *
     * @param username 조회할 사용자명
     * @return UserEntity 사용자 엔티티 (존재하지 않으면 null)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findByUsername")
    @ResultMap("userEntityResultMap")
    UserEntity findByUsername(@Param("username") String username);

    /**
     * 이메일로 사용자 조회
     * 지정된 이메일로 사용자를 조회합니다.
     *
     * @param email 조회할 이메일
     * @return UserEntity 사용자 엔티티 (존재하지 않으면 null)
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "findByEmail")
    @ResultMap("userEntityResultMap")
    UserEntity findByEmail(@Param("email") String email);

    /**
     * 사용자 등록
     * 새로운 사용자를 데이터베이스에 등록합니다.
     * 자동 생성된 ID가 엔티티 객체에 설정됩니다.
     *
     * @param user 등록할 사용자 엔티티
     * @return int 영향을 받은 행 수 (1: 성공, 0: 실패)
     */
    @InsertProvider(type = UserRepositorySqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserEntity user);

    /**
     * 사용자 정보 수정
     * 기존 사용자의 정보를 수정합니다.
     * updated_at 필드가 자동으로 현재 시간으로 갱신됩니다.
     *
     * @param user 수정할 사용자 엔티티 (ID 필수)
     * @return int 영향을 받은 행 수 (1: 성공, 0: 실패)
     */
    @UpdateProvider(type = UserRepositorySqlProvider.class, method = "update")
    int update(UserEntity user);

    /**
     * 사용자 삭제
     * 지정된 ID의 사용자를 물리적으로 삭제합니다.
     * (주의: soft delete가 아닌 hard delete)
     *
     * @param id 삭제할 사용자 ID
     * @return int 영향을 받은 행 수 (1: 성공, 0: 실패)
     */
    @DeleteProvider(type = UserRepositorySqlProvider.class, method = "deleteById")
    int deleteById(@Param("id") Long id);

    /**
     * 전체 사용자 수 조회 (삭제된 사용자 제외)
     * deleted_at IS NULL 조건으로 삭제되지 않은 사용자만 카운트합니다.
     *
     * @return int 사용자 수
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "count")
    int count();

    /**
     * 사용자명 중복 체크
     * 지정된 사용자명이 이미 존재하는지 확인합니다 (삭제된 사용자 제외).
     *
     * @param username 확인할 사용자명
     * @return boolean true: 존재함, false: 존재하지 않음
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "existsByUsername")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 이메일 중복 체크
     * 지정된 이메일이 이미 존재하는지 확인합니다 (삭제된 사용자 제외).
     *
     * @param email 확인할 이메일
     * @return boolean true: 존재함, false: 존재하지 않음
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "existsByEmail")
    boolean existsByEmail(@Param("email") String email);

    // ==================== 극한 동적 쿼리 메서드 (SQL Builder) ====================

    /**
     * 동적 쿼리로 사용자 검색
     * SQL Builder를 사용하여 다양한 조건으로 사용자를 검색합니다.
     * 조건은 UserSearchCriteria에 설정된 값에 따라 동적으로 생성됩니다.
     *
     * @param criteria 검색 조건 객체 (id, username, email, 날짜 범위, 정렬, 페이징 등)
     * @return List<UserEntity> 검색된 사용자 엔티티 리스트
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "searchUsersWithDynamicQuery")
    @ResultMap("userEntityResultMap")
    List<UserEntity> searchUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적 쿼리로 사용자 수 조회
     * searchUsersWithDynamicQuery와 동일한 조건으로 사용자 수를 조회합니다.
     * 페이징 처리를 위한 전체 개수 확인에 사용됩니다.
     *
     * @param criteria 검색 조건 객체
     * @return int 검색 조건에 맞는 사용자 수
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "countUsersWithDynamicQuery")
    int countUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적으로 사용자 정보 수정 (NULL이 아닌 필드만 업데이트)
     * Map에 포함된 필드만 업데이트하는 동적 UPDATE 쿼리를 실행합니다.
     * updated_at는 자동으로 현재 시간으로 갱신됩니다.
     *
     * @param params 업데이트할 필드를 담은 Map (id 필수, 나머지 선택적)
     *               - id: 수정할 사용자 ID (필수)
     *               - username: 수정할 사용자명 (선택)
     *               - email: 수정할 이메일 (선택)
     *               - isActive: 수정할 활성화 상태 (선택)
     *               - status: 수정할 상태 (선택)
     * @return int 영향을 받은 행 수
     */
    @UpdateProvider(type = UserRepositorySqlProvider.class, method = "updateUserDynamically")
    int updateUserDynamically(Map<String, Object> params);

    /**
     * 대량 사용자 등록 (Batch Insert)
     * 여러 사용자를 한 번의 쿼리로 등록하여 성능을 최적화합니다.
     * INSERT INTO users (...) VALUES (...), (...), (...) 형태의 쿼리가 실행됩니다.
     *
     * @param users 등록할 사용자 엔티티 리스트
     * @return int 등록된 사용자 수
     */
    @InsertProvider(type = UserRepositorySqlProvider.class, method = "batchInsertUsers")
    int batchInsertUsers(@Param("list") List<UserEntity> users);

    /**
     * 동적 조건으로 사용자 삭제
     * 다양한 조건으로 여러 사용자를 삭제합니다.
     * (주의: 조건에 맞는 모든 사용자가 삭제되므로 신중하게 사용)
     *
     * @param criteria 삭제 조건 객체 (ids, startDate, endDate, userStatus 등)
     * @return int 삭제된 사용자 수
     */
    @DeleteProvider(type = UserRepositorySqlProvider.class, method = "deleteUsersDynamically")
    int deleteUsersDynamically(UserSearchCriteria criteria);

    /**
     * 주문 정보와 통계를 포함한 사용자 검색 (복잡한 JOIN)
     * 사용자 정보와 주문 통계를 LEFT JOIN으로 조회합니다.
     * - orderCount: 주문 수
     * - totalAmount: 총 주문 금액
     * - customerTier: 고객 등급 (VIP, PREMIUM, REGULAR, NORMAL)
     *
     * @param criteria 검색 조건 객체
     * @return List<Map<String, Object>> 사용자 정보와 주문 통계를 담은 Map 리스트
     *         각 Map의 키:
     *         - id, username, email, created_at: 사용자 기본 정보
     *         - order_count: 주문 수
     *         - total_amount: 총 주문 금액
     *         - customer_tier: 고객 등급
     */
    @SelectProvider(type = UserRepositorySqlProvider.class, method = "searchUsersWithOrdersAndStats")
    List<Map<String, Object>> searchUsersWithOrdersAndStats(UserSearchCriteria criteria);
}
