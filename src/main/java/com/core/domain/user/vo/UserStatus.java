package com.core.domain.user.vo;

/**
 * 사용자 상태 VO (Value Object)
 * 불변 객체로 사용자의 상태를 표현
 */
public enum UserStatus {
    ACTIVE("활성", "정상적으로 활동 중인 사용자"),
    INACTIVE("비활성", "비활성화된 사용자"),
    SUSPENDED("정지", "정지된 사용자"),
    DELETED("삭제", "삭제된 사용자");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 문자열을 UserStatus로 변환
     */
    public static UserStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (UserStatus status : UserStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid UserStatus: " + value);
    }

    /**
     * UserStatus가 활성 상태인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * UserStatus가 비활성 상태인지 확인
     */
    public boolean isInactive() {
        return this == INACTIVE || this == SUSPENDED || this == DELETED;
    }
}
