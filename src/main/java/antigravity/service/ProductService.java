package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.type.DiscountType;
import antigravity.error.ErrorCode;
import antigravity.error.exception.CustomException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PromotionProductsRepository promotionProductsRepository;
    private final PromotionRepository promotionRepository;

    @Value("${product.price.minimum}")
    private int minimumPrice;   // 최소 상품 금액

    @Value("${product.price.maximum}")
    private int maximumPrice;   // 최대 상품 금액

    /**
     * 요청받은 상품 및 쿠폰리스트에 따라 할인된 상품 가격을 구한다.
     * - 상품 최대, 최소 가격 / 프로모션 기간 등의 유효성 체크를 수행한다.
     *
     * @param request 상품 가격 요청(상품 아이디, 쿠폰 아이디 리스트)
     * @return 상품 가격 응답
     */
    @Transactional
    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        log.info("request : {}", request);

        Product product = productRepository.getProduct(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_PRODUCT));

        log.info("product : {}", product);

        // 1. 상품 가격 체크
        validateProductPrice(product.getPrice());

        int finalPrice = product.getPrice();    // 최종 할인된 금액
        int totalDiscountValue = 0;             // 총 할인 금액

        if (request.getCouponIds() != null) {
            List<Promotion> promotionList = new ArrayList<>();

            // 2. 요청받은 쿠폰(프로모션)이 해당 상품에 적용되는지 확인
            Arrays.stream(request.getCouponIds()).forEach(couponId -> {
                if (promotionProductsRepository.existsByProductIdAndPromotionId(request.getProductId(), couponId)) {
                    promotionList.add(promotionRepository.getPromotion(couponId));
                }
            });

            if (request.getCouponIds().length > 0 && promotionList.size() == 0) {
                // 요청 쿠폰이 있지만, 해당 쿠폰이 상품에 적용되어 있지 않았을 경우
                throw new CustomException(ErrorCode.NOT_EXIST_PROMOTION);
            } else {
                for (Promotion promotion : promotionList) {
                    // 3. 프로모션 기간 체크
                    validatePromotionDate(promotion.getUse_started_at(), promotion.getUse_ended_at());

                    // 4. 할인금액 구하기
                    int discountPrice = getDiscountPrice(promotion.getDiscount_type(), promotion.getDiscount_value(), product.getPrice());
                    finalPrice -= discountPrice;
                    totalDiscountValue += discountPrice;

                    // 4. 할인금액 체크
                    if (finalPrice < 0) {
                        throw new CustomException(ErrorCode.OVER_DISCOUNT);
                    }

                }
            }
        }

        if (product.getPrice() > finalPrice) {
            // 할인된 경우에 한해서만 천단위 절삭 (절삭된 금액은 총 할인 금액에 포함시키지 않음)
            finalPrice = new BigDecimal(finalPrice).setScale(-4, RoundingMode.DOWN).intValue();
        }

        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(totalDiscountValue)
                .finalPrice(finalPrice)
                .build();
    }

    /**
     * 할인 가격 구하기
     * @param discountType 할인 종류
     * @param discountValue 할인 값
     * @param price 상품 가격
     * @return 할인 가격
     */
    private int getDiscountPrice(DiscountType discountType, int discountValue, int price) {
        BigDecimal discountPrice = new BigDecimal(0);

        switch (discountType) {
            case WON:
                discountPrice = new BigDecimal(discountValue);
                break;
            case PERCENT:
                discountPrice = new BigDecimal(price).multiply(new BigDecimal(discountValue)).divide(new BigDecimal(100), 1, RoundingMode.DOWN);
                break;
        }

        return discountPrice.intValue();
    }

    /**
     * 상품 가격 유효성 체크
     * @param price 상품 가격
     */
    private void validateProductPrice(int price) {
        if (price < minimumPrice) {
            throw new CustomException(ErrorCode.PRICE_MINIMUM);
        } else if (price > maximumPrice) {
            throw new CustomException(ErrorCode.PRICE_MAXIMUM);
        }
    }

    /**
     * 프로모션 유효 기간 체크
     *
     * @param startDate 프로모션 시작
     * @param endDate 프로모션 종료
     */
    private void validatePromotionDate(Date startDate, Date endDate) {
        Date now = new Date();

        if (now.before(startDate)) {
            throw new CustomException(ErrorCode.NOT_YET_PROMOTION_DATE);
        } else if (now.after(endDate)) {
            throw new CustomException(ErrorCode.PROMOTION_EXPIRATION);
        }
    }

}
