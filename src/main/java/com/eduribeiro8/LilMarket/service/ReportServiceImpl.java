package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleReportDataPointResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportPeriodResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportRequestDTO;
import com.eduribeiro8.LilMarket.repository.SaleRepository;
import com.eduribeiro8.LilMarket.repository.projection.SaleProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final SaleRepository saleRepository;

    @Override
    public SaleReportPeriodResponseDTO getSaleReport(SaleReportRequestDTO saleReportRequestDTO) {

        String type = saleReportRequestDTO.type().trim().toUpperCase(Locale.ROOT);

        long totalSaleCount = 0L;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalNetProfit = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<SaleReportDataPointResponseDTO> saleReportDataPointResponseDTOS = new ArrayList<>();
        List<Long> clientsToExclude = saleReportRequestDTO.excludedClients();
        if (clientsToExclude == null || clientsToExclude.isEmpty()) {
            clientsToExclude = List.of(-1L);
        }


        ZonedDateTime startOfBrtDay = saleReportRequestDTO.startDate().atStartOfDay(ZoneId.of("America/Sao_Paulo"));
        OffsetDateTime startUtc = startOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

        switch (type) {
            case "DAILY" -> {
                ZonedDateTime endOfBrtDay = saleReportRequestDTO.startDate().atTime(23, 59, 59).atZone(ZoneId.of("America/Sao_Paulo"));
                OffsetDateTime endUtc = endOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

                List<SaleProjection> saleProjections = saleRepository.findHourlySalesAggregation(startUtc, endUtc, clientsToExclude);

                Map<String, SaleProjection> projectionMap =
                        saleProjections
                                .stream()
                                .collect(Collectors.toMap(SaleProjection::getLabel, p -> p));

                for (int i = 0; i < 24; i++) {

                    String currentLabel = String.format("%s %02d:00", saleReportRequestDTO.startDate(), i);

                    SaleProjection saleProjection = projectionMap.get(currentLabel);

                    if (saleProjection != null) {
                        Long saleCount = saleProjection.getSaleCount();
                        BigDecimal revenue = saleProjection.getRevenue();
                        BigDecimal netProfit = saleProjection.getNetProfit();
                        BigDecimal averageTicket = BigDecimal.ZERO;
                        BigDecimal discount = saleProjection.getDiscount();

                        if (saleCount > 0) {
                            averageTicket = revenue.divide(BigDecimal.valueOf(saleCount), 2, RoundingMode.HALF_UP);
                        }

                        SaleReportDataPointResponseDTO saleReportDataPointResponseDTO = new SaleReportDataPointResponseDTO(
                                currentLabel,
                                saleCount,
                                revenue,
                                netProfit,
                                averageTicket,
                                discount
                        );

                        totalSaleCount += saleCount;
                        totalRevenue = totalRevenue.add(revenue);
                        totalNetProfit = totalNetProfit.add(netProfit);
                        totalDiscount = totalDiscount.add(discount);

                        saleReportDataPointResponseDTOS.add(saleReportDataPointResponseDTO);
                    } else {
                        saleReportDataPointResponseDTOS.add(new SaleReportDataPointResponseDTO(
                                currentLabel, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                        ));
                    }
                }

                BigDecimal averageTicket = BigDecimal.ZERO;

                if (totalSaleCount > 0) {
                    averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalSaleCount), 2, RoundingMode.HALF_UP);
                }

                return new SaleReportPeriodResponseDTO(
                        "DAILY",
                        saleReportRequestDTO.startDate(),
                        saleReportRequestDTO.startDate(),
                        totalSaleCount,
                        totalRevenue,
                        totalNetProfit,
                        averageTicket,
                        totalDiscount,
                        saleReportDataPointResponseDTOS
                );
            }
            case "WEEKLY" -> {
                ZonedDateTime endOfBrtDay = saleReportRequestDTO.startDate().plusDays(6).atTime(23, 59, 59).atZone(ZoneId.of("America/Sao_Paulo"));
                OffsetDateTime endUtc = endOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

                List<SaleProjection> saleProjections = saleRepository.findDailySalesAggregation(startUtc, endUtc, clientsToExclude);

                Map<String, SaleProjection> projectionMap =
                        saleProjections
                                .stream()
                                .collect(Collectors.toMap(SaleProjection::getLabel, p -> p));

                for (int i = 0; i < 7; i++) {

                    String currentLabel = String.format("%s", saleReportRequestDTO.startDate().plusDays(i));

                    SaleProjection saleProjection = projectionMap.get(currentLabel);

                    if (saleProjection != null) {
                        Long saleCount = saleProjection.getSaleCount();
                        BigDecimal revenue = saleProjection.getRevenue();
                        BigDecimal netProfit = saleProjection.getNetProfit();
                        BigDecimal averageTicket = BigDecimal.ZERO;
                        BigDecimal discount = saleProjection.getDiscount();

                        if (saleCount > 0) {
                            averageTicket = revenue.divide(BigDecimal.valueOf(saleCount), 2, RoundingMode.HALF_UP);
                        }

                        SaleReportDataPointResponseDTO saleReportDataPointResponseDTO = new SaleReportDataPointResponseDTO(
                                currentLabel,
                                saleCount,
                                revenue,
                                netProfit,
                                averageTicket,
                                discount
                        );

                        totalSaleCount += saleCount;
                        totalRevenue = totalRevenue.add(revenue);
                        totalNetProfit = totalNetProfit.add(netProfit);
                        totalDiscount = totalDiscount.add(discount);

                        saleReportDataPointResponseDTOS.add(saleReportDataPointResponseDTO);
                    } else {
                        saleReportDataPointResponseDTOS.add(new SaleReportDataPointResponseDTO(
                                currentLabel, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                        ));
                    }
                }

                BigDecimal averageTicket = BigDecimal.ZERO;

                if (totalSaleCount > 0) {
                    averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalSaleCount), 2, RoundingMode.HALF_UP);
                }

                return new SaleReportPeriodResponseDTO(
                        "WEEKLY",
                        saleReportRequestDTO.startDate(),
                        saleReportRequestDTO.startDate().plusDays(6),
                        totalSaleCount,
                        totalRevenue,
                        totalNetProfit,
                        averageTicket,
                        totalDiscount,
                        saleReportDataPointResponseDTOS
                );
            }
            case "MONTHLY" -> {
                LocalDate startDate = saleReportRequestDTO.startDate();

                LocalDate safeStartDate = startDate.withDayOfMonth(1);
                startDate = safeStartDate;
                LocalDate safeEndDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

                startOfBrtDay = safeStartDate.atStartOfDay().atZone(ZoneId.of("America/Sao_Paulo"));
                ZonedDateTime endOfBrtDay = safeEndDate.atTime(23, 59, 59).atZone(ZoneId.of("America/Sao_Paulo"));

                startUtc = startOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
                OffsetDateTime endUtc = endOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

                List<SaleProjection> saleProjections = saleRepository.findDailySalesAggregation(startUtc, endUtc, clientsToExclude);

                Map<String, SaleProjection> projectionMap =
                        saleProjections
                                .stream()
                                .collect(Collectors.toMap(SaleProjection::getLabel, p -> p));

                while (!safeStartDate.isAfter(safeEndDate)) {

                    String currentLabel = String.format("%s", safeStartDate);

                    SaleProjection saleProjection = projectionMap.get(currentLabel);

                    if (saleProjection != null) {
                        Long saleCount = saleProjection.getSaleCount();
                        BigDecimal revenue = saleProjection.getRevenue();
                        BigDecimal netProfit = saleProjection.getNetProfit();
                        BigDecimal averageTicket = BigDecimal.ZERO;
                        BigDecimal discount = saleProjection.getDiscount();

                        if (saleCount > 0) {
                            averageTicket = revenue.divide(BigDecimal.valueOf(saleCount), 2, RoundingMode.HALF_UP);
                        }

                        SaleReportDataPointResponseDTO saleReportDataPointResponseDTO = new SaleReportDataPointResponseDTO(
                                currentLabel,
                                saleCount,
                                revenue,
                                netProfit,
                                averageTicket,
                                discount
                        );

                        totalSaleCount += saleCount;
                        totalRevenue = totalRevenue.add(revenue);
                        totalNetProfit = totalNetProfit.add(netProfit);
                        totalDiscount = totalDiscount.add(discount);

                        saleReportDataPointResponseDTOS.add(saleReportDataPointResponseDTO);
                    } else {
                        saleReportDataPointResponseDTOS.add(new SaleReportDataPointResponseDTO(
                                currentLabel, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                        ));
                    }

                    safeStartDate = safeStartDate.plusDays(1);
                }

                BigDecimal averageTicket = BigDecimal.ZERO;

                if (totalSaleCount > 0) {
                    averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalSaleCount), 2, RoundingMode.HALF_UP);
                }

                return new SaleReportPeriodResponseDTO(
                        "MONTHLY",
                        startDate,
                        safeEndDate,
                        totalSaleCount,
                        totalRevenue,
                        totalNetProfit,
                        averageTicket,
                        totalDiscount,
                        saleReportDataPointResponseDTOS
                );
            }
            case "YEARLY" -> {
                LocalDate startDate = saleReportRequestDTO.startDate();

                LocalDate safeStartDate = startDate.withDayOfMonth(1);
                startDate = safeStartDate;
                LocalDate safeEndDate = startDate.plusMonths(11).with(TemporalAdjusters.lastDayOfMonth());

                startOfBrtDay = safeStartDate.atStartOfDay().atZone(ZoneId.of("America/Sao_Paulo"));
                ZonedDateTime endOfBrtDay = safeEndDate.atTime(23, 59, 59).atZone(ZoneId.of("America/Sao_Paulo"));

                startUtc = startOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);
                OffsetDateTime endUtc = endOfBrtDay.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

                List<SaleProjection> saleProjections = saleRepository.findMonthlySalesAggregation(startUtc, endUtc, clientsToExclude);

                Map<String, SaleProjection> projectionMap =
                        saleProjections
                                .stream()
                                .collect(Collectors.toMap(SaleProjection::getLabel, p -> p));

                while (!safeStartDate.isAfter(safeEndDate)) {

                    String currentLabel = String.format("%s-%02d", safeStartDate.getYear(), safeStartDate.getMonth().getValue());

                    SaleProjection saleProjection = projectionMap.get(currentLabel);

                    if (saleProjection != null) {
                        Long saleCount = saleProjection.getSaleCount();
                        BigDecimal revenue = saleProjection.getRevenue();
                        BigDecimal netProfit = saleProjection.getNetProfit();
                        BigDecimal averageTicket = BigDecimal.ZERO;
                        BigDecimal discount = saleProjection.getDiscount();

                        if (saleCount > 0) {
                            averageTicket = revenue.divide(BigDecimal.valueOf(saleCount), 2, RoundingMode.HALF_UP);
                        }

                        SaleReportDataPointResponseDTO saleReportDataPointResponseDTO = new SaleReportDataPointResponseDTO(
                                currentLabel,
                                saleCount,
                                revenue,
                                netProfit,
                                averageTicket,
                                discount
                        );

                        totalSaleCount += saleCount;
                        totalRevenue = totalRevenue.add(revenue);
                        totalNetProfit = totalNetProfit.add(netProfit);
                        totalDiscount = totalDiscount.add(discount);

                        saleReportDataPointResponseDTOS.add(saleReportDataPointResponseDTO);
                    } else {
                        saleReportDataPointResponseDTOS.add(new SaleReportDataPointResponseDTO(
                                currentLabel, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
                        ));
                    }

                    safeStartDate = safeStartDate.plusMonths(1);
                }

                BigDecimal averageTicket = BigDecimal.ZERO;

                if (totalSaleCount > 0) {
                    averageTicket = totalRevenue.divide(BigDecimal.valueOf(totalSaleCount), 2, RoundingMode.HALF_UP);
                }

                return new SaleReportPeriodResponseDTO(
                        "YEARLY",
                        startDate,
                        safeEndDate,
                        totalSaleCount,
                        totalRevenue,
                        totalNetProfit,
                        averageTicket,
                        totalDiscount,
                        saleReportDataPointResponseDTOS
                );
            }
        }

        return null;
    }
}
