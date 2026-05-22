package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.StockMovementRequestDTO;
import com.eduribeiro8.LilMarket.dto.StockMovementResponseDTO;
import com.eduribeiro8.LilMarket.entity.MovementType;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.StockMovement;
import com.eduribeiro8.LilMarket.repository.StockMovementRepository;
import com.eduribeiro8.LilMarket.rest.exception.InvalidDateIntervalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockMovementService Tests")
class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private Product product;
    private StockMovement movementEntry;
    private StockMovement movementExit;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Arroz")
                .build();

        movementEntry = StockMovement.builder()
                .id(10L)
                .product(product)
                .movementType(MovementType.ENTRY)
                .quantity(new BigDecimal("10.000"))
                .timestamp(OffsetDateTime.parse("2026-05-01T12:00:00Z"))
                .referenceId(100L)
                .description("Entrada de lote")
                .build();

        movementExit = StockMovement.builder()
                .id(11L)
                .product(product)
                .movementType(MovementType.EXIT)
                .quantity(new BigDecimal("2.000"))
                .timestamp(OffsetDateTime.parse("2026-05-01T13:00:00Z"))
                .referenceId(200L)
                .description("Saida por venda")
                .build();
    }

    @Nested
    @DisplayName("Testes para consultar movimentacoes")
    class GetStockMovement {

        @Test
        @DisplayName("Deve retornar movimentacoes com saldo acumulado")
        void getStockMovement_Success_WithRunningStock() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(1L, null, null, null);
            Page<StockMovement> movementPage = new PageImpl<>(List.of(movementEntry, movementExit), pageable, 2);

            when(stockMovementRepository.findStockMovementByProductOptionalType(1L, null, null, null, pageable))
                    .thenReturn(movementPage);
            when(stockMovementRepository.sumMovementsBefore(1L, movementEntry.getTimestamp(), movementEntry.getId()))
                    .thenReturn(new BigDecimal("5.000"));

            // Act
            Page<StockMovementResponseDTO> result = stockMovementService.getStockMovement(filter, pageable);

            // Assert
            assertEquals(2, result.getTotalElements());
            assertEquals(new BigDecimal("15.000"), result.getContent().get(0).quantityInStock());
            assertEquals(new BigDecimal("13.000"), result.getContent().get(1).quantityInStock());
            verify(stockMovementRepository).findStockMovementByProductOptionalType(1L, null, null, null, pageable);
            verify(stockMovementRepository).sumMovementsBefore(1L, movementEntry.getTimestamp(), movementEntry.getId());
            verifyNoMoreInteractions(stockMovementRepository);
        }

        @Test
        @DisplayName("Deve retornar pagina vazia quando nao houver movimentacoes")
        void getStockMovement_Success_EmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(1L, MovementType.ENTRY, null, null);

            when(stockMovementRepository.findStockMovementByProductOptionalType(1L, MovementType.ENTRY, null, null, pageable))
                    .thenReturn(Page.empty(pageable));

            // Act
            Page<StockMovementResponseDTO> result = stockMovementService.getStockMovement(filter, pageable);

            // Assert
            assertEquals(0, result.getTotalElements());
            verify(stockMovementRepository).findStockMovementByProductOptionalType(1L, MovementType.ENTRY, null, null, pageable);
            verify(stockMovementRepository, never()).sumMovementsBefore(any(), any(), any());
            verifyNoMoreInteractions(stockMovementRepository);
        }

        @Test
        @DisplayName("Deve converter intervalo completo de datas para UTC")
        void getStockMovement_Success_WithDateRange() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(
                    1L,
                    MovementType.ENTRY,
                    LocalDate.of(2026, 5, 1),
                    LocalDate.of(2026, 5, 2)
            );

            when(stockMovementRepository.findStockMovementByProductOptionalType(eq(1L), eq(MovementType.ENTRY), any(), any(), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            // Act
            stockMovementService.getStockMovement(filter, pageable);

            // Assert
            ArgumentCaptor<OffsetDateTime> startCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
            ArgumentCaptor<OffsetDateTime> endCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
            verify(stockMovementRepository).findStockMovementByProductOptionalType(
                    eq(1L),
                    eq(MovementType.ENTRY),
                    startCaptor.capture(),
                    endCaptor.capture(),
                    eq(pageable)
            );

            OffsetDateTime expectedStart = LocalDate.of(2026, 5, 1)
                    .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);
            OffsetDateTime expectedEnd = LocalDate.of(2026, 5, 2)
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.of("America/Sao_Paulo"))
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);

            assertEquals(expectedStart, startCaptor.getValue());
            assertEquals(expectedEnd, endCaptor.getValue());
            verify(stockMovementRepository, never()).sumMovementsBefore(any(), any(), any());
            verifyNoMoreInteractions(stockMovementRepository);
        }

        @Test
        @DisplayName("Deve aplicar somente startDate quando endDate for nulo")
        void getStockMovement_Success_WithOnlyStartDate() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(
                    1L,
                    null,
                    LocalDate.of(2026, 5, 1),
                    null
            );

            when(stockMovementRepository.findStockMovementByProductOptionalType(eq(1L), isNull(), any(), isNull(), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            // Act
            stockMovementService.getStockMovement(filter, pageable);

            // Assert
            ArgumentCaptor<OffsetDateTime> startCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
            verify(stockMovementRepository).findStockMovementByProductOptionalType(
                    eq(1L),
                    isNull(),
                    startCaptor.capture(),
                    isNull(),
                    eq(pageable)
            );
            OffsetDateTime expectedStart = LocalDate.of(2026, 5, 1)
                    .atStartOfDay(ZoneId.of("America/Sao_Paulo"))
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);
            assertEquals(expectedStart, startCaptor.getValue());
            verify(stockMovementRepository, never()).sumMovementsBefore(any(), any(), any());
            verifyNoMoreInteractions(stockMovementRepository);
        }

        @Test
        @DisplayName("Deve aplicar somente endDate quando startDate for nulo")
        void getStockMovement_Success_WithOnlyEndDate() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(
                    1L,
                    null,
                    null,
                    LocalDate.of(2026, 5, 2)
            );

            when(stockMovementRepository.findStockMovementByProductOptionalType(eq(1L), isNull(), isNull(), any(), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            // Act
            stockMovementService.getStockMovement(filter, pageable);

            // Assert
            ArgumentCaptor<OffsetDateTime> endCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
            verify(stockMovementRepository).findStockMovementByProductOptionalType(
                    eq(1L),
                    isNull(),
                    isNull(),
                    endCaptor.capture(),
                    eq(pageable)
            );
            OffsetDateTime expectedEnd = LocalDate.of(2026, 5, 2)
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.of("America/Sao_Paulo"))
                    .toOffsetDateTime()
                    .withOffsetSameInstant(ZoneOffset.UTC);
            assertEquals(expectedEnd, endCaptor.getValue());
            verify(stockMovementRepository, never()).sumMovementsBefore(any(), any(), any());
            verifyNoMoreInteractions(stockMovementRepository);
        }

        @Test
        @DisplayName("Deve lancar InvalidDateIntervalException quando endDate for anterior ao startDate")
        void getStockMovement_Fail_InvalidDateRange() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            StockMovementRequestDTO filter = new StockMovementRequestDTO(
                    1L,
                    null,
                    LocalDate.of(2026, 5, 3),
                    LocalDate.of(2026, 5, 2)
            );

            // Act & Assert
            assertThrows(InvalidDateIntervalException.class, () -> stockMovementService.getStockMovement(filter, pageable));
            verifyNoMoreInteractions(stockMovementRepository);
        }
    }
}
