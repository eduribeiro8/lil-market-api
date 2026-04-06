package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleReportDataPointResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportPeriodResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleReportRequestDTO;
import com.eduribeiro8.LilMarket.repository.SaleRepository;
import com.eduribeiro8.LilMarket.repository.projection.SaleProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportServiceImpl Tests")
class ReportServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private SaleProjection mockProjection(String label, Long count, BigDecimal revenue, BigDecimal profit, BigDecimal discount) {
        SaleProjection projection = mock(SaleProjection.class);
        when(projection.getLabel()).thenReturn(label);
        when(projection.getSaleCount()).thenReturn(count);
        when(projection.getRevenue()).thenReturn(revenue);
        when(projection.getNetProfit()).thenReturn(profit);
        when(projection.getDiscount()).thenReturn(discount);
        return projection;
    }

    @Nested
    @DisplayName("Relatório Diário (DAILY)")
    class DailyReportTests {

        @Test
        @DisplayName("Deve gerar relatório diário com 24 horas corretamente, incluindo pontos zerados e cálculo de ticket médio")
        void shouldGenerateDailyReport() {
            // Arrange
            LocalDate startDate = LocalDate.of(2026, 4, 5);
            SaleReportRequestDTO request = new SaleReportRequestDTO("DAILY", startDate, null, null);
            
            String labelAt14 = "2026-04-05 14:00";
            SaleProjection projAt14 = mockProjection(labelAt14, 2L, new BigDecimal("100.00"), new BigDecimal("20.00"), new BigDecimal("5.00"));
            
            when(saleRepository.findHourlySalesAggregation(any(), any(), eq(List.of(-1L))))
                    .thenReturn(List.of(projAt14));

            // Act
            SaleReportPeriodResponseDTO response = reportService.getSaleReport(request);

            // Assert
            assertNotNull(response);
            assertEquals("DAILY", response.type());
            assertEquals(startDate, response.startTime());
            assertEquals(startDate, response.endTime());
            
            // Verifica totais
            assertEquals(2L, response.totalSaleCount());
            assertEquals(new BigDecimal("100.00"), response.totalRevenue());
            assertEquals(new BigDecimal("20.00"), response.totalNetProfit());
            assertEquals(new BigDecimal("5.00"), response.totalDiscounts());
            assertEquals(new BigDecimal("50.00"), response.averageTicket()); // 100 / 2
            
            // Verifica data points
            assertEquals(24, response.data().size());
            
            // Verifica uma hora sem venda (ex: 00:00)
            SaleReportDataPointResponseDTO pointAt0 = response.data().get(0);
            assertEquals("2026-04-05 00:00", pointAt0.label());
            assertEquals(0L, pointAt0.saleCount());
            assertEquals(BigDecimal.ZERO, pointAt0.revenue());
            
            // Verifica hora com venda (14:00)
            SaleReportDataPointResponseDTO pointAt14 = response.data().get(14);
            assertEquals("2026-04-05 14:00", pointAt14.label());
            assertEquals(2L, pointAt14.saleCount());
            assertEquals(new BigDecimal("100.00"), pointAt14.revenue());
            assertEquals(new BigDecimal("50.00"), pointAt14.averageTicket());
            
            verify(saleRepository, times(1)).findHourlySalesAggregation(any(), any(), eq(List.of(-1L)));
        }
    }

    @Nested
    @DisplayName("Relatório Semanal (WEEKLY)")
    class WeeklyReportTests {

        @Test
        @DisplayName("Deve gerar relatório semanal com 7 dias, tratando a lista de exclusão de clientes")
        void shouldGenerateWeeklyReport() {
            // Arrange
            LocalDate startDate = LocalDate.of(2026, 4, 1);
            List<Long> excluded = List.of(99L);
            SaleReportRequestDTO request = new SaleReportRequestDTO("WEEKLY", startDate, null, excluded);
            
            String labelDay3 = "2026-04-03";
            SaleProjection projDay3 = mockProjection(labelDay3, 10L, new BigDecimal("500.00"), new BigDecimal("100.00"), BigDecimal.ZERO);
            
            when(saleRepository.findDailySalesAggregation(any(), any(), eq(excluded)))
                    .thenReturn(List.of(projDay3));

            // Act
            SaleReportPeriodResponseDTO response = reportService.getSaleReport(request);

            // Assert
            assertNotNull(response);
            assertEquals("WEEKLY", response.type());
            assertEquals(startDate, response.startTime());
            assertEquals(startDate.plusDays(6), response.endTime());
            
            assertEquals(10L, response.totalSaleCount());
            assertEquals(new BigDecimal("50.00"), response.averageTicket()); // 500 / 10
            assertEquals(7, response.data().size());
            
            SaleReportDataPointResponseDTO pointDay3 = response.data().get(2);
            assertEquals("2026-04-03", pointDay3.label());
            assertEquals(10L, pointDay3.saleCount());
            
            verify(saleRepository, times(1)).findDailySalesAggregation(any(), any(), eq(excluded));
        }
    }

    @Nested
    @DisplayName("Relatório Mensal (MONTHLY)")
    class MonthlyReportTests {

        @Test
        @DisplayName("Deve gerar relatório mensal calculando o início e fim do mês corretamente (ex: ano bissexto)")
        void shouldGenerateMonthlyReport() {
            // Arrange
            // Fevereiro de 2024 (bissexto) tem 29 dias
            LocalDate startDate = LocalDate.of(2024, 2, 15);
            SaleReportRequestDTO request = new SaleReportRequestDTO("MONTHLY", startDate, null, Collections.emptyList());
            
            String labelFeb29 = "2024-02-29";
            SaleProjection projFeb29 = mockProjection(labelFeb29, 1L, new BigDecimal("50.00"), new BigDecimal("10.00"), BigDecimal.ZERO);
            
            when(saleRepository.findDailySalesAggregation(any(), any(), eq(List.of(-1L))))
                    .thenReturn(List.of(projFeb29));

            // Act
            SaleReportPeriodResponseDTO response = reportService.getSaleReport(request);

            // Assert
            assertNotNull(response);
            assertEquals("MONTHLY", response.type());
            assertEquals(LocalDate.of(2024, 2, 1), response.startTime());
            assertEquals(LocalDate.of(2024, 2, 29), response.endTime());
            
            assertEquals(1L, response.totalSaleCount());
            assertEquals(29, response.data().size());
            
            SaleReportDataPointResponseDTO pointLastDay = response.data().get(28);
            assertEquals("2024-02-29", pointLastDay.label());
            assertEquals(1L, pointLastDay.saleCount());
            
            verify(saleRepository, times(1)).findDailySalesAggregation(any(), any(), eq(List.of(-1L)));
        }
    }

    @Nested
    @DisplayName("Relatório Anual (YEARLY)")
    class YearlyReportTests {

        @Test
        @DisplayName("Deve gerar relatório anual para 12 meses, calculando os labels corretamente")
        void shouldGenerateYearlyReport() {
            // Arrange
            LocalDate startDate = LocalDate.of(2026, 4, 15);
            SaleReportRequestDTO request = new SaleReportRequestDTO("YEARLY", startDate, null, null);
            
            // Mês 4 (Abril) e Mês 10 (Outubro - para garantir o problema do leading zero format)
            String labelApr = "2026-04";
            SaleProjection projApr = mockProjection(labelApr, 5L, new BigDecimal("500.00"), new BigDecimal("100.00"), BigDecimal.ZERO);
            
            String labelOct = "2026-10";
            SaleProjection projOct = mockProjection(labelOct, 5L, new BigDecimal("1000.00"), new BigDecimal("200.00"), BigDecimal.ZERO);
            
            when(saleRepository.findMonthlySalesAggregation(any(), any(), eq(List.of(-1L))))
                    .thenReturn(List.of(projApr, projOct));

            // Act
            SaleReportPeriodResponseDTO response = reportService.getSaleReport(request);

            // Assert
            assertNotNull(response);
            assertEquals("YEARLY", response.type());
            assertEquals(LocalDate.of(2026, 4, 1), response.startTime());
            assertEquals(LocalDate.of(2027, 3, 31), response.endTime());
            
            assertEquals(10L, response.totalSaleCount());
            assertEquals(new BigDecimal("1500.00"), response.totalRevenue());
            assertEquals(new BigDecimal("150.00"), response.averageTicket()); // 1500 / 10
            
            assertEquals(12, response.data().size());
            
            // Abril é o index 0 no array (começa no safeStartDate = April 1st)
            SaleReportDataPointResponseDTO pointApr = response.data().get(0);
            assertEquals("2026-04", pointApr.label());
            assertEquals(5L, pointApr.saleCount());
            
            // Outubro é o index 6
            SaleReportDataPointResponseDTO pointOct = response.data().get(6);
            assertEquals("2026-10", pointOct.label());
            assertEquals(5L, pointOct.saleCount());
            
            verify(saleRepository, times(1)).findMonthlySalesAggregation(any(), any(), eq(List.of(-1L)));
        }
    }

    @Nested
    @DisplayName("Tipos não suportados")
    class UnsupportedTypeTests {

        @Test
        @DisplayName("Deve retornar null se o tipo de relatório não for reconhecido")
        void shouldReturnNullForUnsupportedType() {
            // Arrange
            SaleReportRequestDTO request = new SaleReportRequestDTO("INVALID", LocalDate.now(), null, null);

            // Act
            SaleReportPeriodResponseDTO response = reportService.getSaleReport(request);

            // Assert
            assertNull(response);
            verifyNoInteractions(saleRepository);
        }
    }
}
