package antigravity.repository;

import antigravity.domain.entity.Product;
import antigravity.domain.entity.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PromotionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Promotion getPromotion(int id) {
        String query = "SELECT * FROM `promotion` WHERE id = :id ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(
                query,
                params,
                (rs, rowNum) -> Promotion.builder()
                        .id(rs.getInt("id"))
                        .promotion_type(rs.getString("promotion_type"))
                        .name(rs.getString("name"))
                        .discount_type(rs.getString("discount_type"))
                        .discount_value(rs.getInt("discount_value"))
                        .use_started_at(rs.getDate("use_started_at"))
                        .use_ended_at(rs.getDate("use_ended_at"))
                        .build()
        );
    }
}
