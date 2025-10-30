package com.core.domain.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 사용자 요청 DTO
 * 클라이언트로부터 받는 데이터 전송 객체
 */
public class UserRequestDto {

    /**
     * 사용자 생성 요청 DTO
     */
    public static class Create {
        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 3, max = 50, message = "사용자명은 3~50자 사이여야 합니다")
        private String username;

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        public Create() {
        }

        public Create(String username, String email) {
            this.username = username;
            this.email = email;
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

        @Override
        public String toString() {
            return "Create{" +
                    "username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    /**
     * 사용자 수정 요청 DTO
     */
    public static class Update {
        @Size(min = 3, max = 50, message = "사용자명은 3~50자 사이여야 합니다")
        private String username;

        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        private Boolean isActive;

        private String status;

        public Update() {
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

        @Override
        public String toString() {
            return "Update{" +
                    "username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", isActive=" + isActive +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    /**
     * 사용자 검색 요청 DTO
     */
    public static class Search {
        private Long id;
        private String username;
        private String email;
        private String usernamePattern;
        private String emailPattern;
        private Long minId;
        private Long maxId;
        private String startDate;
        private String endDate;
        private Boolean isActive;
        private String userStatus;
        private String orderBy;
        private String sortDirection;
        private Integer page;
        private Integer size;

        public Search() {
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

        public String getUsernamePattern() {
            return usernamePattern;
        }

        public void setUsernamePattern(String usernamePattern) {
            this.usernamePattern = usernamePattern;
        }

        public String getEmailPattern() {
            return emailPattern;
        }

        public void setEmailPattern(String emailPattern) {
            this.emailPattern = emailPattern;
        }

        public Long getMinId() {
            return minId;
        }

        public void setMinId(Long minId) {
            this.minId = minId;
        }

        public Long getMaxId() {
            return maxId;
        }

        public void setMaxId(Long maxId) {
            this.maxId = maxId;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        // 페이징 계산
        public Integer getOffset() {
            if (page == null || size == null) {
                return null;
            }
            return page * size;
        }

        public Integer getLimit() {
            return size;
        }

        @Override
        public String toString() {
            return "Search{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", usernamePattern='" + usernamePattern + '\'' +
                    ", emailPattern='" + emailPattern + '\'' +
                    ", minId=" + minId +
                    ", maxId=" + maxId +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    ", isActive=" + isActive +
                    ", userStatus='" + userStatus + '\'' +
                    ", orderBy='" + orderBy + '\'' +
                    ", sortDirection='" + sortDirection + '\'' +
                    ", page=" + page +
                    ", size=" + size +
                    '}';
        }
    }
}