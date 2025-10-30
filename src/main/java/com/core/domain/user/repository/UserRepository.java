package com.core.domain.user.repository;

import com.core.domain.user.entity.UserEntity;
import com.core.model.UserSearchCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 사용자 Repository (MyBatis Mapper)
 * 도메인 방식의 패키지 구조에 따른 Repository 인터페이스
 */
@Mapper
public interface UserRepository {

    /**
     * 모든 사용자 조회
     */
    List<UserEntity> findAll();

    /**
     * ID로 사용자 조회
     */
    UserEntity findById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     */
    UserEntity findByUsername(@Param("username") String username);

    /**
     * 이메일로 사용자 조회
     */
    UserEntity findByEmail(@Param("email") String email);

    /**
     * 사용자 등록
     */
    int insert(UserEntity user);

    /**
     * 사용자 정보 수정
     */
    int update(UserEntity user);

    /**
     * 사용자 삭제
     */
    int deleteById(@Param("id") Long id);

    /**
     * 전체 사용자 수 조회
     */
    int count();

    /**
     * 사용자명 중복 체크
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 이메일 중복 체크
     */
    boolean existsByEmail(@Param("email") String email);

    // ==================== 극한 동적 쿼리 메서드 ====================

    /**
     * 동적 쿼리로 사용자 검색
     */
    List<UserEntity> searchUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적 쿼리로 사용자 수 조회
     */
    int countUsersWithDynamicQuery(UserSearchCriteria criteria);

    /**
     * 동적으로 사용자 정보 수정 (NULL이 아닌 필드만 업데이트)
     */
    int updateUserDynamically(Map<String, Object> params);

    /**
     * 대량 사용자 등록 (Batch Insert)
     */
    int batchInsertUsers(List<UserEntity> users);

    /**
     * 동적 조건으로 사용자 삭제
     */
    int deleteUsersDynamically(UserSearchCriteria criteria);

    /**
     * 주문 정보와 통계를 포함한 사용자 검색 (복잡한 JOIN)
     */
    List<Map<String, Object>> searchUsersWithOrdersAndStats(UserSearchCriteria criteria);
}
