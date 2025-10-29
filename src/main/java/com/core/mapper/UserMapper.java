package com.core.mapper;

import com.core.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사용자 Mapper 인터페이스
 */
@Mapper
public interface UserMapper {

    /**
     * 모든 사용자 조회
     */
    List<User> findAll();

    /**
     * ID로 사용자 조회
     */
    User findById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     */
    User findByUsername(@Param("username") String username);

    /**
     * 사용자 등록
     */
    int insert(User user);

    /**
     * 사용자 정보 수정
     */
    int update(User user);

    /**
     * 사용자 삭제
     */
    int deleteById(@Param("id") Long id);

    /**
     * 전체 사용자 수 조회
     */
    int count();
}
