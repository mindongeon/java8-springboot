package com.core.domain.user.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 사용자명 VO (Value Object)
 * 불변 객체로 사용자명을 표현하고 유효성 검증을 수행
 */
public class Username {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]+$");

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    /**
     * 사용자명 생성 (유효성 검증 포함)
     */
    public static Username of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자명은 필수입니다");
        }

        String trimmedValue = value.trim();

        if (trimmedValue.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "사용자명은 " + MIN_LENGTH + "자 이상이어야 합니다");
        }

        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "사용자명은 " + MAX_LENGTH + "자 이하여야 합니다");
        }

        if (!USERNAME_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException(
                    "사용자명은 영문자, 숫자, 언더스코어(_), 하이픈(-)만 사용할 수 있습니다");
        }

        return new Username(trimmedValue);
    }

    /**
     * 사용자명 길이 반환
     */
    public int getLength() {
        return value.length();
    }

    /**
     * 사용자명이 특정 패턴과 일치하는지 확인
     */
    public boolean matches(String pattern) {
        return value.matches(pattern);
    }

    /**
     * 사용자명에 특정 문자열이 포함되어 있는지 확인
     */
    public boolean contains(String substring) {
        return value.toLowerCase().contains(substring.toLowerCase());
    }

    /**
     * 마스킹된 사용자명 반환 (개인정보 보호)
     * 예: testuser -> te****er
     */
    public String getMasked() {
        if (value.length() <= 4) {
            return value.charAt(0) + "***";
        }
        int visibleChars = 2;
        String prefix = value.substring(0, visibleChars);
        String suffix = value.substring(value.length() - visibleChars);
        int maskedLength = value.length() - (visibleChars * 2);

        // Java 8 호환: repeat() 대신 StringBuilder 사용
        StringBuilder masked = new StringBuilder(prefix);
        for (int i = 0; i < maskedLength; i++) {
            masked.append("*");
        }
        masked.append(suffix);
        return masked.toString();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
