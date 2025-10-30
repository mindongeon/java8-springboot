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
