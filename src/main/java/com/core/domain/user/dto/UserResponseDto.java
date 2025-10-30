package com.core.domain.user.dto;

import com.core.domain.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 응답 DTO
 * 클라이언트에게 전달하는 데이터 전송 객체
 */
public class UserResponseDto {

    /**
     * 사용자 기본 정보 응답 DTO
     */
    public static class Basic {
        private Long id;
        private String username;
        private String email;
        private String createdAt;

        public Basic() {
        }

        public Basic(Long id, String username, String email, String createdAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.createdAt = createdAt;
        }

        // Entity에서 DTO로 변환
        public static Basic from(UserEntity entity) {
            if (entity == null) {
                return null;
            }
            Basic dto = new Basic();
            dto.setId(entity.getId());
            dto.setUsername(entity.getUsername());
            dto.setEmail(entity.getEmail());
            if (entity.getCreatedAt() != null) {
                dto.setCreatedAt(entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            return dto;
        }

        // 리스트 변환
        public static List<Basic> fromList(List<UserEntity> entities) {
            return entities.stream()
                    .map(Basic::from)
                    .collect(Collectors.toList());
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "Basic{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    '}';
        }
    }

    /**
     * 사용자 상세 정보 응답 DTO
     */
    public static class Detail {
        private Long id;
        private String username;
        private String email;
        private Boolean isActive;
        private String status;
        private Integer orderCount;
        private String createdAt;
        private String updatedAt;

        public Detail() {
        }

        // Entity에서 DTO로 변환
        public static Detail from(UserEntity entity) {
            if (entity == null) {
                return null;
            }
            Detail dto = new Detail();
            dto.setId(entity.getId());
            dto.setUsername(entity.getUsername());
            dto.setEmail(entity.getEmail());
            dto.setIsActive(entity.getIsActive());
            dto.setStatus(entity.getStatus());
            dto.setOrderCount(entity.getOrderCount());
            if (entity.getCreatedAt() != null) {
                dto.setCreatedAt(entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            if (entity.getUpdatedAt() != null) {
                dto.setUpdatedAt(entity.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            return dto;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public String toString() {
            return "Detail{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", isActive=" + isActive +
                    ", status='" + status + '\'' +
                    ", orderCount=" + orderCount +
                    ", createdAt='" + createdAt + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }
    }

    /**
     * 사용자 통계 정보 응답 DTO (주문 정보 포함)
     */
    public static class WithStats {
        private Long id;
        private String username;
        private String email;
        private Integer orderCount;
        private Double totalAmount;
        private String customerTier;
        private String createdAt;

        public WithStats() {
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCustomerTier() {
            return customerTier;
        }

        public void setCustomerTier(String customerTier) {
            this.customerTier = customerTier;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "WithStats{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", orderCount=" + orderCount +
                    ", totalAmount=" + totalAmount +
                    ", customerTier='" + customerTier + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    '}';
        }
    }

    /**
     * 페이징된 사용자 목록 응답 DTO
     */
    public static class Page {
        private List<Basic> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public Page() {
        }

        public Page(List<Basic> content, int page, int size, long totalElements) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = (int) Math.ceil((double) totalElements / size);
        }

        // Getters and Setters
        public List<Basic> getContent() {
            return content;
        }

        public void setContent(List<Basic> content) {
            this.content = content;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        @Override
        public String toString() {
            return "Page{" +
                    "content=" + content +
                    ", page=" + page +
                    ", size=" + size +
                    ", totalElements=" + totalElements +
                    ", totalPages=" + totalPages +
                    '}';
        }
    }
}