package com.eduribeiro8.LilMarket.repository;

import com.eduribeiro8.LilMarket.entity.Sale;
import com.eduribeiro8.LilMarket.repository.projection.SaleProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Page<Sale> findByTimestampBetween(OffsetDateTime start, OffsetDateTime end, Pageable pageable);

    @Query(value = """
        SELECT\s
            DATE_FORMAT(CONVERT_TZ(sale_timestamp, '+00:00', '-03:00'), '%Y-%m-%d %H:00') as label,\s
            COUNT(sale_id) as saleCount,\s
            SUM(total_amount) as revenue,\s
            SUM(net_profit) as netProfit,\s
            SUM(discount) as discount\s
        FROM sales\s
        WHERE sale_timestamp >= :start AND sale_timestamp <= :end\s
            AND customer_id NOT IN :excludedClients\s
        GROUP BY label\s
        ORDER BY label ASC
       \s""", nativeQuery = true)
    List<SaleProjection> findHourlySalesAggregation(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("excludedClients") List<Long> excludedClients
    );

    @Query(value = """
        SELECT\s
            DATE_FORMAT(CONVERT_TZ(sale_timestamp, '+00:00', '-03:00'), '%Y-%m-%d') as label,\s
            COUNT(sale_id) as saleCount,\s
            SUM(total_amount) as revenue,\s
            SUM(net_profit) as netProfit,\s
            SUM(discount) as discount\s
        FROM sales\s
        WHERE sale_timestamp >= :start AND sale_timestamp <= :end\s
            AND customer_id NOT IN :excludedClients\s
        GROUP BY label\s
        ORDER BY label ASC
       \s""", nativeQuery = true)
    List<SaleProjection> findDailySalesAggregation(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("excludedClients") List<Long> excludedClients
    );

    @Query(value = """
        SELECT\s
            DATE_FORMAT(CONVERT_TZ(sale_timestamp, '+00:00', '-03:00'), '%Y-%m') as label,\s
            COUNT(sale_id) as saleCount,\s
            SUM(total_amount) as revenue,\s
            SUM(net_profit) as netProfit,\s
            SUM(discount) as discount\s
        FROM sales\s
        WHERE sale_timestamp >= :start AND sale_timestamp <= :end\s
            AND customer_id NOT IN :excludedClients\s
        GROUP BY label\s
        ORDER BY label ASC
       \s""", nativeQuery = true)
    List<SaleProjection> findMonthlySalesAggregation(
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            @Param("excludedClients") List<Long> excludedClients
    );
}
