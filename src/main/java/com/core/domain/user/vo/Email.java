package com.core.domain.user.vo;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 이메일 VO (Value Object)
 * 불변 객체로 이메일 주소를 표현하고 유효성 검증을 수행
 */
public class Email {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    /**
     * 이메일 생성 (유효성 검증 포함)
     */
    public static Email of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일은 필수입니다");
        }

        String trimmedValue = value.trim();
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다: " + value);
        }

        return new Email(trimmedValue);
    }

    /**
     * 이메일 도메인 추출
     */
    public String getDomain() {
        int atIndex = value.indexOf('@');
        if (atIndex > 0) {
            return value.substring(atIndex + 1);
        }
        return "";
    }

    /**
     * 이메일 로컬 파트 추출
     */
    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        if (atIndex > 0) {
            return value.substring(0, atIndex);
        }
        return value;
    }

    /**
     * 특정 도메인인지 확인
     */
    public boolean hasDomain(String domain) {
        return getDomain().equalsIgnoreCase(domain);
    }

    /**
     * 마스킹된 이메일 반환 (개인정보 보호)
     * 예: test@example.com -> t***@example.com
     */
    public String getMasked() {
        String localPart = getLocalPart();
        if (localPart.length() <= 1) {
            return value;
        }
        String masked = localPart.charAt(0) + "***";
        return masked + "@" + getDomain();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value.toLowerCase(), email.value.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
