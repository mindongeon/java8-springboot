package com.core.domain.user.service;

import com.core.domain.user.dto.UserRequestDto;
import com.core.domain.user.dto.UserResponseDto;
import com.core.domain.user.entity.UserEntity;
import com.core.domain.user.repository.UserRepository;
import com.core.domain.user.vo.Email;
import com.core.domain.user.vo.Username;
import com.core.model.UserSearchCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 서비스
 * 도메인 방식의 패키지 구조에 따른 Service 클래스
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 모든 사용자 조회
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto.Basic> getAllUsers() {
        List<UserEntity> entities = userRepository.findAll();
        return UserResponseDto.Basic.fromList(entities);
    }

    /**
     * ID로 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserResponseDto.Detail getUserById(Long id) {
        UserEntity entity = userRepository.findById(id);
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + id);
        }
        return UserResponseDto.Detail.from(entity);
    }

    /**
     * 사용자명으로 사용자 조회
     */
    @Transactional(readOnly = true)
    public UserResponseDto.Detail getUserByUsername(String username) {
        // VO를 사용한 유효성 검증
        Username usernameVo = Username.of(username);

        UserEntity entity = userRepository.findByUsername(usernameVo.getValue());
        if (entity == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + username);
        }
        return UserResponseDto.Detail.from(entity);
    }

    /**
     * 사용자 생성
     */
    public UserResponseDto.Detail createUser(UserRequestDto.Create request) {
        // VO를 사용한 유효성 검증
        Username username = Username.of(request.getUsername());
        Email email = Email.of(request.getEmail());

        // 중복 체크
        if (userRepository.existsByUsername(username.getValue())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username.getValue());
        }

        if (userRepository.existsByEmail(email.getValue())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email.getValue());
        }

        // Entity 생성 및 저장
        UserEntity entity = new UserEntity();
        entity.setUsername(username.getValue());
        entity.setEmail(email.getValue());
        entity.setIsActive(true);

        userRepository.insert(entity);

        return UserResponseDto.Detail.from(entity);
    }

    /**
     * 사용자 수정
     */
    public UserResponseDto.Detail updateUser(Long id, UserRequestDto.Update request) {
        // 존재 여부 확인
        UserEntity existingEntity = userRepository.findById(id);
        if (existingEntity == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + id);
        }

        // 동적 업데이트를 위한 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        // VO를 사용한 유효성 검증 및 업데이트 데이터 설정
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            Username username = Username.of(request.getUsername());
            // 다른 사용자가 이미 사용 중인지 확인
            UserEntity duplicateUser = userRepository.findByUsername(username.getValue());
            if (duplicateUser != null && !duplicateUser.getId().equals(id)) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username.getValue());
            }
            params.put("username", username.getValue());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            Email email = Email.of(request.getEmail());
            // 다른 사용자가 이미 사용 중인지 확인
            UserEntity duplicateUser = userRepository.findByEmail(email.getValue());
            if (duplicateUser != null && !duplicateUser.getId().equals(id)) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email.getValue());
            }
            params.put("email", email.getValue());
        }

        if (request.getIsActive() != null) {
            params.put("isActive", request.getIsActive());
        }

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            params.put("status", request.getStatus());
        }

        // 동적 업데이트 수행
        userRepository.updateUserDynamically(params);

        // 업데이트된 엔티티 조회 및 반환
        UserEntity updatedEntity = userRepository.findById(id);
        return UserResponseDto.Detail.from(updatedEntity);
    }

    /**
     * 사용자 삭제
     */
    public void deleteUser(Long id) {
        UserEntity existingEntity = userRepository.findById(id);
        if (existingEntity == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다: " + id);
        }

        userRepository.deleteById(id);
    }

    /**
     * 전체 사용자 수 조회
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 동적 쿼리로 사용자 검색 (페이징)
     */
    @Transactional(readOnly = true)
    public UserResponseDto.Page searchUsers(UserRequestDto.Search searchDto) {
        // SearchDto를 Criteria로 변환
        UserSearchCriteria criteria = convertToCriteria(searchDto);

        // 검색 실행
        List<UserEntity> entities = userRepository.searchUsersWithDynamicQuery(criteria);
        int totalCount = userRepository.countUsersWithDynamicQuery(criteria);

        // DTO 변환
        List<UserResponseDto.Basic> content = UserResponseDto.Basic.fromList(entities);

        // 페이징 정보와 함께 반환
        int page = searchDto.getPage() != null ? searchDto.getPage() : 0;
        int size = searchDto.getSize() != null ? searchDto.getSize() : 20;

        return new UserResponseDto.Page(content, page, size, totalCount);
    }

    /**
     * 주문 통계를 포함한 사용자 검색
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto.WithStats> searchUsersWithStats(UserRequestDto.Search searchDto) {
        UserSearchCriteria criteria = convertToCriteria(searchDto);
        List<Map<String, Object>> results = userRepository.searchUsersWithOrdersAndStats(criteria);

        // Map을 DTO로 변환 (Java 8 호환)
        return results.stream()
                .map(this::mapToWithStatsDto)
                .collect(Collectors.toList());
    }

    /**
     * 대량 사용자 생성
     */
    public int batchCreateUsers(List<UserRequestDto.Create> requests) {
        List<UserEntity> entities = requests.stream()
                .map(request -> {
                    Username username = Username.of(request.getUsername());
                    Email email = Email.of(request.getEmail());

                    UserEntity entity = new UserEntity();
                    entity.setUsername(username.getValue());
                    entity.setEmail(email.getValue());
                    entity.setIsActive(true);
                    return entity;
                })
                .collect(Collectors.toList());

        return userRepository.batchInsertUsers(entities);
    }

    // ==================== 헬퍼 메서드 ====================

    /**
     * SearchDto를 Criteria로 변환
     */
    private UserSearchCriteria convertToCriteria(UserRequestDto.Search searchDto) {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setId(searchDto.getId());
        criteria.setUsername(searchDto.getUsername());
        criteria.setEmail(searchDto.getEmail());
        criteria.setUsernamePattern(searchDto.getUsernamePattern());
        criteria.setEmailPattern(searchDto.getEmailPattern());
        criteria.setMinId(searchDto.getMinId());
        criteria.setMaxId(searchDto.getMaxId());
        criteria.setStartDate(searchDto.getStartDate());
        criteria.setEndDate(searchDto.getEndDate());
        criteria.setIsActive(searchDto.getIsActive());
        criteria.setUserStatus(searchDto.getUserStatus());
        criteria.setOrderBy(searchDto.getOrderBy());
        criteria.setSortDirection(searchDto.getSortDirection());
        criteria.setLimit(searchDto.getLimit());
        criteria.setOffset(searchDto.getOffset());
        return criteria;
    }

    /**
     * Map을 WithStatsDto로 변환
     */
    private UserResponseDto.WithStats mapToWithStatsDto(Map<String, Object> map) {
        UserResponseDto.WithStats dto = new UserResponseDto.WithStats();
        dto.setId(((Number) map.get("id")).longValue());
        dto.setUsername((String) map.get("username"));
        dto.setEmail((String) map.get("email"));
        dto.setOrderCount(map.get("order_count") != null ? ((Number) map.get("order_count")).intValue() : 0);
        dto.setTotalAmount(map.get("total_amount") != null ? ((Number) map.get("total_amount")).doubleValue() : 0.0);
        dto.setCustomerTier((String) map.get("customer_tier"));
        dto.setCreatedAt(map.get("created_at") != null ? map.get("created_at").toString() : null);
        return dto;
    }
}
