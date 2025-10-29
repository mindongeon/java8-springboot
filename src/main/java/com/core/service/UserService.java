package com.core.service;

import com.core.mapper.UserMapper;
import com.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 서비스
 */
@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }

    /**
     * 사용자명으로 사용자 조회
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 사용자 등록
     */
    public User createUser(User user) {
        // 비즈니스 로직 (예: 중복 체크)
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + user.getUsername());
        }

        userMapper.insert(user);
        return user;
    }

    /**
     * 사용자 정보 수정
     */
    public User updateUser(User user) {
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + user.getId());
        }

        userMapper.update(user);
        return userMapper.findById(user.getId());
    }

    /**
     * 사용자 삭제
     */
    public void deleteUser(Long id) {
        User existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + id);
        }

        userMapper.deleteById(id);
    }

    /**
     * 전체 사용자 수 조회
     */
    @Transactional(readOnly = true)
    public int getUserCount() {
        return userMapper.count();
    }
}
