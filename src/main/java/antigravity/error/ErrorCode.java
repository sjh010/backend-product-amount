package antigravity.error;

public enum ErrorCode {

    // 상품
    NOT_EXIST_PRODUCT(40007, "상품이 존재하지 않습니다."),
    PRICE_MINIMUM(40001, "최소 상품 가격은 10,000원 입니다."),
    PRICE_MAXIMUM(40002, "최대 상품 가격은 10,000,000원 입니다."),

    // 프로모션 및 할인
    NOT_YET_PROMOTION_DATE(40003, "프로모션 기간 전입니다."),
    PROMOTION_EXPIRATION(40004, "프로모션 기간이 종료되었습니다."),
    NOT_EXIST_PROMOTION(40005, "해당 상품은 프로모션 적용 대상이 아닙니다."),
    OVER_DISCOUNT(40006, "할인 금액은 상품 금액보다 클 수 없습니다."),

    ;

    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
