package antigravity.service;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import antigravity.domain.entity.PromotionProducts;
import antigravity.domain.type.DiscountType;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import antigravity.repository.ProductRepository;
import antigravity.repository.PromotionProductsRepository;
import antigravity.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final PromotionProductsRepository promotionProductsRepository;

    private final PromotionRepository promotionRepository;

    public ProductAmountResponse getProductAmount(ProductInfoRequest request) {
        System.out.println("상품 가격 추출 로직을 완성 시켜주세요.");

        Product product = this.productRepository.getProduct(request.getProductId());

        List<PromotionProducts> promotionProductsList = promotionProductsRepository.getPromotionProductsList(product.getId());

        promotionProductsList.stream().forEach(item -> log.info("promotion_id : {}", item.getPromotionId()));
        log.info("request : {}", request);
        log.info("product : {}", product);

        int finalPrice = product.getPrice();
        int totalDiscountValue = 0;

        for (PromotionProducts promotionProducts : promotionProductsList) {
            Promotion promotion = promotionRepository.getPromotion(promotionProducts.getPromotionId());
            log.info("promotion : {}", promotion);

            if (validatePromotionDate(promotion.getUse_started_at(), promotion.getUse_ended_at())) {
                int discountPrice = getDiscountPrice(promotion.getDiscount_type(), promotion.getDiscount_value(), finalPrice);
                finalPrice -= discountPrice;
                totalDiscountValue += discountPrice;

            }
        }

        return ProductAmountResponse.builder()
                .name(product.getName())
                .originPrice(product.getPrice())
                .discountPrice(totalDiscountValue)
                .finalPrice(new BigDecimal(finalPrice).setScale(-4, BigDecimal.ROUND_DOWN).intValue())
                .build();
    }

    private int getDiscountPrice(DiscountType discountType, int discountValue, int price) {

        BigDecimal discountPrice = new BigDecimal(0);

        switch (discountType) {
            case WON:
                discountPrice = new BigDecimal(discountValue);
                break;
            case PERCENT:
                discountPrice = new BigDecimal(price).multiply(new BigDecimal(discountValue).divide(new BigDecimal(100)));
                break;
        }

        return discountPrice.intValue();
    }


    /**
     * 프로모션 유효 기간 체크
     *
     * @param startDate 프로모션 시작
     * @param endDate 프로모션 종료
     * @return
     */
    private boolean validatePromotionDate(Date startDate, Date endDate) {
        Date now = new Date();

        if ((now.equals(startDate) || now.after(startDate))
                && now.before(endDate)) {
            return true;
        } else {
            return false;
        }

    }

}
