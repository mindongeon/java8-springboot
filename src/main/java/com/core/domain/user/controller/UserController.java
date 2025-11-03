package com.core.domain.user.controller;

import com.core.domain.user.dto.UserRequestDto;
import com.core.domain.user.dto.UserResponseDto;
import com.core.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 REST API 컨트롤러
 * 도메인 방식의 패키지 구조에 따른 Controller 클래스
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 모든 사용자 조회
     * GET /api/v1/users
     *
     * @return ResponseEntity 사용자 목록과 전체 개수를 포함한 응답
     *         - users: 사용자 목록 (UserResponseDto.Basic)
     *         - totalCount: 전체 사용자 수
     *         - success: 성공 여부 (true)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserResponseDto.Basic> users = userService.getAllUsers();
        long totalCount = userService.getUserCount();

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("totalCount", totalCount);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * ID로 사용자 조회
     * GET /api/v1/users/{id}
     *
     * @param id 조회할 사용자 ID
     * @return ResponseEntity 사용자 상세 정보 또는 에러 메시지
     *         성공 시:
     *         - user: 사용자 상세 정보 (UserResponseDto.Detail)
     *         - success: true
     *         실패 시 (404):
     *         - success: false
     *         - error: 에러 메시지
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            UserResponseDto.Detail user = userService.getUserById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 사용자 생성
     * POST /api/v1/users
     *
     * @param request 사용자 생성 요청 DTO (username, email 필수)
     * @return ResponseEntity 생성된 사용자 정보 또는 에러 메시지
     *         성공 시 (201):
     *         - user: 생성된 사용자 정보 (UserResponseDto.Detail)
     *         - success: true
     *         - message: 성공 메시지
     *         실패 시 (400):
     *         - success: false
     *         - error: 에러 메시지 (중복 사용자명/이메일, 유효성 검증 실패 등)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody UserRequestDto.Create request) {
        try {
            UserResponseDto.Detail user = userService.createUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("success", true);
            response.put("message", "사용자가 성공적으로 생성되었습니다");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * 사용자 수정
     * PUT /api/v1/users/{id}
     *
     * @param id 수정할 사용자 ID
     * @param request 사용자 수정 요청 DTO (수정할 필드만 전달, null인 필드는 업데이트하지 않음)
     * @return ResponseEntity 수정된 사용자 정보 또는 에러 메시지
     *         성공 시 (200):
     *         - user: 수정된 사용자 정보 (UserResponseDto.Detail)
     *         - success: true
     *         - message: 성공 메시지
     *         실패 시:
     *         - success: false
     *         - error: 에러 메시지
     *         - 404: 사용자를 찾을 수 없음
     *         - 400: 중복 데이터 또는 유효성 검증 실패
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto.Update request) {
        try {
            UserResponseDto.Detail user = userService.updateUser(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("success", true);
            response.put("message", "사용자가 성공적으로 수정되었습니다");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());

            HttpStatus status = e.getMessage().contains("존재하지 않는")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status).body(errorResponse);
        }
    }

    /**
     * 사용자 삭제
     * DELETE /api/v1/users/{id}
     *
     * @param id 삭제할 사용자 ID
     * @return ResponseEntity 삭제 결과 메시지
     *         성공 시 (200):
     *         - success: true
     *         - message: 성공 메시지
     *         실패 시 (404):
     *         - success: false
     *         - error: 에러 메시지 (사용자를 찾을 수 없음)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "사용자가 성공적으로 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * 사용자 검색 (동적 쿼리 + 페이징)
     * GET /api/v1/users/search
     *
     * @param searchDto 검색 조건 DTO
     *                  - id: 사용자 ID
     *                  - username: 사용자명 (정확히 일치)
     *                  - usernamePattern: 사용자명 (부분 일치)
     *                  - email: 이메일 (정확히 일치)
     *                  - emailPattern: 이메일 (부분 일치)
     *                  - minId, maxId: ID 범위
     *                  - startDate, endDate: 생성일 범위
     *                  - isActive: 활성화 여부
     *                  - userStatus: 사용자 상태
     *                  - orderBy: 정렬 컬럼 (id, username, email, created_at)
     *                  - sortDirection: 정렬 방향 (ASC, DESC)
     *                  - page: 페이지 번호 (기본값: 0)
     *                  - size: 페이지 크기 (기본값: 20)
     * @return ResponseEntity 검색 결과와 페이징 정보
     *         - data: 페이징 정보와 사용자 목록 (UserResponseDto.Page)
     *         - success: true
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @ModelAttribute UserRequestDto.Search searchDto) {
        UserResponseDto.Page page = userService.searchUsers(searchDto);

        Map<String, Object> response = new HashMap<>();
        response.put("data", page);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 검색 (주문 통계 포함)
     * GET /api/v1/users/search/with-stats
     *
     * @param searchDto 검색 조건 DTO (search 메소드와 동일한 파라미터 사용)
     * @return ResponseEntity 검색 결과와 주문 통계 정보
     *         - users: 주문 통계가 포함된 사용자 목록 (UserResponseDto.WithStats)
     *           각 사용자는 orderCount(주문 수), totalAmount(총 금액), customerTier(고객 등급) 포함
     *         - success: true
     */
    @GetMapping("/search/with-stats")
    public ResponseEntity<Map<String, Object>> searchUsersWithStats(
            @ModelAttribute UserRequestDto.Search searchDto) {
        List<UserResponseDto.WithStats> users = userService.searchUsersWithStats(searchDto);

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }

    /**
     * 대량 사용자 생성
     * POST /api/v1/users/batch
     *
     * @param requests 사용자 생성 요청 DTO 리스트
     * @return ResponseEntity 생성 결과
     *         성공 시 (201):
     *         - success: true
     *         - message: 생성된 사용자 수 메시지
     *         - count: 생성된 사용자 수
     *         실패 시 (400):
     *         - success: false
     *         - error: 에러 메시지 (중복, 유효성 검증 실패 등)
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchCreateUsers(
            @Valid @RequestBody List<UserRequestDto.Create> requests) {
        try {
            int count = userService.batchCreateUsers(requests);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", count + "명의 사용자가 생성되었습니다");
            response.put("count", count);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * 사용자 수 조회
     * GET /api/v1/users/count
     *
     * @return ResponseEntity 전체 사용자 수
     *         - count: 사용자 수 (삭제된 사용자 제외)
     *         - success: true
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        long count = userService.getUserCount();

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("success", true);

        return ResponseEntity.ok(response);
    }
}
