package com.core.domain.user.vo;

/**
 * 고객 등급 VO (Value Object)
 * 주문 횟수에 따른 고객 등급을 표현
 */
public enum CustomerTier {
    VIP(100, "VIP 고객", 0.20),
    PREMIUM(50, "프리미엄 고객", 0.15),
    REGULAR(10, "일반 고객", 0.10),
    NORMAL(0, "신규 고객", 0.05);

    private final int minOrderCount;
    private final String displayName;
    private final double discountRate;

    CustomerTier(int minOrderCount, String displayName, double discountRate) {
        this.minOrderCount = minOrderCount;
        this.displayName = displayName;
        this.discountRate = discountRate;
    }

    public int getMinOrderCount() {
        return minOrderCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    /**
     * 주문 수를 기반으로 고객 등급 결정
     */
    public static CustomerTier fromOrderCount(int orderCount) {
        if (orderCount >= VIP.minOrderCount) {
            return VIP;
        } else if (orderCount >= PREMIUM.minOrderCount) {
            return PREMIUM;
        } else if (orderCount >= REGULAR.minOrderCount) {
            return REGULAR;
        } else {
            return NORMAL;
        }
    }

    /**
     * 문자열을 CustomerTier로 변환
     */
    public static CustomerTier fromString(String value) {
        if (value == null) {
            return null;
        }
        for (CustomerTier tier : CustomerTier.values()) {
            if (tier.name().equalsIgnoreCase(value)) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid CustomerTier: " + value);
    }

    /**
     * 할인 금액 계산
     */
    public double calculateDiscount(double originalPrice) {
        return originalPrice * discountRate;
    }

    /**
     * 할인된 최종 금액 계산
     */
    public double calculateFinalPrice(double originalPrice) {
        return originalPrice * (1 - discountRate);
    }
}
