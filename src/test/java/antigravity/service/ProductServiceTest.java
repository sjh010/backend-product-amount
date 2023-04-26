package antigravity.service;

import antigravity.error.ErrorCode;
import antigravity.error.exception.CustomException;
import antigravity.model.request.ProductInfoRequest;
import antigravity.model.response.ProductAmountResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("상품 가격 추출 테스트")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("성공 테스트 - 쿠폰 미요청")
    void get_product_amount_success_no_coupon() {
        // given
        // 상품가격 : 215,000
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(1)
                .build();

        // when
        ProductAmountResponse response = productService.getProductAmount(request);

        // then
        assertAll(
                () -> assertThat(response.getName()).isEqualTo("피팅노드상품"),
                () -> assertThat(response.getOriginPrice()).isEqualTo(215000),
                () -> assertThat(response.getDiscountPrice()).isEqualTo(0),
                () -> assertThat(response.getFinalPrice()).isEqualTo(215000)
        );
    }

    @Test
    @DisplayName("성공 테스트 - 금액할인 쿠폰 적용")
    void get_product_amount_success_one_coupon() {
        // given
        // 상품가격 : 100,000 / 할인금액 : 50,000
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(2)
                .couponIds(new int[]{3})
                .build();

        // when
        ProductAmountResponse response = productService.getProductAmount(request);

        // then
        assertAll(
                () -> assertThat(response.getName()).isEqualTo("피팅노드상품2"),
                () -> assertThat(response.getOriginPrice()).isEqualTo(100000),
                () -> assertThat(response.getDiscountPrice()).isEqualTo(50000),
                () -> assertThat(response.getFinalPrice()).isEqualTo(50000)
        );
    }

    @Test
    @DisplayName("성공 테스트 - 금액할일 쿠폰, %할인 쿠폰 적용")
    void get_product_amount_success_tow_coupons() {
        // given
        // 상품가격 : 100,000 / 할인금액 : 50,000 / 할인 % : 10%
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(2)
                .couponIds(new int[]{3, 4})
                .build();

        // when
        ProductAmountResponse response = productService.getProductAmount(request);
        System.out.println("response = " + response);
        // then
        assertAll(
                () -> assertThat(response.getName()).isEqualTo("피팅노드상품2"),
                () -> assertThat(response.getOriginPrice()).isEqualTo(100000),
                () -> assertThat(response.getDiscountPrice()).isEqualTo(60000),
                () -> assertThat(response.getFinalPrice()).isEqualTo(40000)
        );
    }

    @Test
    @DisplayName("실패 테스트 - 프로모션 기간 전")
    void get_product_amount_failure_not_yet_promotion() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(3)
                .couponIds(new int[]{5})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_YET_PROMOTION_DATE);
    }

    @Test
    @DisplayName("실패 테스트 - 프로모션 기간 만료")
    void get_product_amount_failure_promotion_expiration() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(1)
                .couponIds(new int[]{1, 2})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.PROMOTION_EXPIRATION);
    }

    @Test
    @DisplayName("실패 테스트 - 프로모션 적용 상품 아님")
    void get_product_amount_failure_not_promotion_product() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(4)
                .couponIds(new int[]{2,3})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_PROMOTION);
    }

    @Test
    @DisplayName("실패 테스트 - 할인금액이 상품금액보다 큰 경우")
    void get_product_amount_failure_over_discount() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(3)
                .couponIds(new int[]{3})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.OVER_DISCOUNT);
    }

    @Test
    @DisplayName("실패 테스트 - 상품 가격이 최소 가격보다 아래인 경우")
    void get_product_amount_failure_minimum_price() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(5)
                .couponIds(new int[]{3})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.PRICE_MINIMUM);
    }

    @Test
    @DisplayName("실패 테스트 - 상품 가격이 최대 가격보다 높은 경우")
    void get_product_amount_failure_maximum_price() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(6)
                .couponIds(new int[]{3})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.PRICE_MAXIMUM);
    }

    @Test
    @DisplayName("실패 테스트 - 존재하지 않는 상품")
    void get_product_amount_failure_not_exist_product() {
        // given
        ProductInfoRequest request = ProductInfoRequest.builder()
                .productId(7)
                .couponIds(new int[]{3})
                .build();

        // when
        CustomException customException = assertThrows(CustomException.class, () -> productService.getProductAmount(request));

        // then
        assertThat(customException.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_PRODUCT);
    }
}