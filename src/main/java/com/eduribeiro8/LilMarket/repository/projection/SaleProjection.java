package com.eduribeiro8.LilMarket.repository.projection;

import java.math.BigDecimal;

public interface SaleProjection {
    String getLabel();
    Long getSaleCount();
    BigDecimal getRevenue();
    BigDecimal getNetProfit();
    BigDecimal getDiscount();

}
