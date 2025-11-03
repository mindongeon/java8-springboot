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
     * 삭제되지 않은 모든 사용자를 조회하여 기본 정보를 반환합니다.
     *
     * @return List<UserResponseDto.Basic> 사용자 기본 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto.Basic> getAllUsers() {
        List<UserEntity> entities = userRepository.findAll();
        return UserResponseDto.Basic.fromList(entities);
    }

    /**
     * ID로 사용자 조회
     * 지정된 ID의 사용자를 조회하여 상세 정보를 반환합니다.
     *
     * @param id 조회할 사용자 ID
     * @return UserResponseDto.Detail 사용자 상세 정보
     * @throws IllegalArgumentException 사용자가 존재하지 않을 경우
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
     * 지정된 사용자명으로 사용자를 조회합니다.
     * VO(Value Object)를 사용하여 사용자명의 유효성을 검증합니다.
     *
     * @param username 조회할 사용자명
     * @return UserResponseDto.Detail 사용자 상세 정보
     * @throws IllegalArgumentException 사용자명이 유효하지 않거나 사용자가 존재하지 않을 경우
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
     * 새로운 사용자를 생성합니다.
     * - VO를 사용하여 username과 email의 유효성을 검증
     * - 중복된 username 또는 email이 있는지 확인
     * - 모든 검증을 통과하면 사용자를 생성하고 저장
     *
     * @param request 사용자 생성 요청 DTO (username, email 필수)
     * @return UserResponseDto.Detail 생성된 사용자 상세 정보
     * @throws IllegalArgumentException 사용자명 또는 이메일이 이미 존재하거나 유효하지 않을 경우
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
     * 기존 사용자의 정보를 수정합니다.
     * - 동적 업데이트: null이 아닌 필드만 업데이트
     * - VO를 사용한 유효성 검증
     * - 다른 사용자가 이미 사용 중인 username/email인지 확인
     *
     * @param id 수정할 사용자 ID
     * @param request 사용자 수정 요청 DTO (수정할 필드만 설정, null인 필드는 무시)
     * @return UserResponseDto.Detail 수정된 사용자 상세 정보
     * @throws IllegalArgumentException 사용자가 존재하지 않거나, username/email이 중복되거나, 유효하지 않을 경우
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
     * 지정된 ID의 사용자를 삭제합니다.
     * 삭제 전에 사용자 존재 여부를 확인합니다.
     *
     * @param id 삭제할 사용자 ID
     * @throws IllegalArgumentException 사용자가 존재하지 않을 경우
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
     * 삭제되지 않은 전체 사용자 수를 반환합니다.
     *
     * @return long 사용자 수 (삭제된 사용자 제외)
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 동적 쿼리로 사용자 검색 (페이징)
     * 다양한 조건으로 사용자를 검색하고 페이징 처리된 결과를 반환합니다.
     * - 검색 조건: ID, username, email, 날짜 범위, 활성화 여부 등
     * - 정렬: 다양한 컬럼 기준 오름차순/내림차순
     * - 페이징: page, size 파라미터로 제어
     *
     * @param searchDto 검색 조건 DTO
     * @return UserResponseDto.Page 페이징 정보와 사용자 목록
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
     * 사용자 정보와 함께 주문 통계 정보를 조회합니다.
     * - orderCount: 주문 수
     * - totalAmount: 총 주문 금액
     * - customerTier: 고객 등급 (VIP, PREMIUM, REGULAR, NORMAL)
     *
     * @param searchDto 검색 조건 DTO
     * @return List<UserResponseDto.WithStats> 주문 통계가 포함된 사용자 목록
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
     * 여러 사용자를 한 번에 생성합니다.
     * - 각 사용자에 대해 VO를 사용한 유효성 검증 수행
     * - 배치 INSERT로 성능 최적화
     *
     * @param requests 사용자 생성 요청 DTO 리스트
     * @return int 생성된 사용자 수
     * @throws IllegalArgumentException 유효하지 않은 데이터가 포함된 경우
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
     * Controller 계층의 DTO를 Repository 계층의 검색 조건 객체로 변환합니다.
     *
     * @param searchDto 검색 DTO
     * @return UserSearchCriteria 검색 조건 객체
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
     * Repository에서 반환한 Map 형태의 결과를 DTO로 변환합니다.
     *
     * @param map Repository 쿼리 결과 Map
     * @return UserResponseDto.WithStats 주문 통계가 포함된 사용자 DTO
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
