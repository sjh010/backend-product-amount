package antigravity.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PromotionProductsRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean existsByProductIdAndPromotionId(int product_id, int promotion_id) {
        String query = "SELECT count(promotion_id) FROM `promotion_products` WHERE product_id = :product_id AND promotion_id = :promotion_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("product_id", product_id);
        params.addValue("promotion_id", promotion_id);

        Integer promotionId = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);

        return promotionId > 0;

    }
}
