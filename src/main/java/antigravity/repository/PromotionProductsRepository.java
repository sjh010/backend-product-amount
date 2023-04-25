package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.PromotionProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PromotionProductsRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<PromotionProducts> getPromotionProductsList(int product_id) {
        String query = "SELECT * FROM `promotion_products` WHERE product_id = :product_id ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("product_id", product_id);

        List<PromotionProducts> promotionProductsList = new ArrayList<>();

        return namedParameterJdbcTemplate.query(
                query,
                params,
                (rs, rowNum) -> PromotionProducts.builder()
                        .promotionId(rs.getInt("promotion_id"))
                        .productId(rs.getInt("product_id"))
                        .build()

        );
    }

}
