package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchInvalidationRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchLossReportRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.BatchResponseDTO;
import com.eduribeiro8.LilMarket.entity.Batch;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.Restock;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.BatchMapper;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.rest.exception.BatchNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBatchCodeException;
import com.eduribeiro8.LilMarket.rest.exception.InsufficientQuantityInSaleException;
import com.eduribeiro8.LilMarket.rest.exception.InvalidDateIntervalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BatchService Tests")
class BatchServiceImplTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private BatchMapper batchMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BatchServiceImpl batchService;

    private Product product;
    private Supplier supplier;
    private Restock restock;
    private Batch batch;
    private BatchRequestDTO requestDTO;
    private BatchResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("produto1")
                .totalQuantity(BigDecimal.ZERO)
                .build();

        supplier = Supplier.builder()
                .id(1L)
                .name("fornecedor1")
                .build();

        restock = Restock.builder()
                .id(1L)
                .supplier(supplier)
                .build();

        batch = Batch.builder()
                .id(1L)
                .product(product)
                .supplier(supplier)
                .restock(restock)
                .batchCode("LOTE123")
                .quantityInStock(BigDecimal.TEN)
                .quantityLost(BigDecimal.ZERO)
                .expirationDate(LocalDate.now().plusMonths(6))
                .purchasePrice(BigDecimal.valueOf(1.50))
                .build();

        requestDTO = new BatchRequestDTO(
                1L,
                "LOTE123",
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                BigDecimal.TEN,
                BigDecimal.ZERO,
                BigDecimal.valueOf(1.50)
        );

        responseDTO = new BatchResponseDTO(
                1L, 1L, 1L, "fornecedor1", "produto1", "LOTE123",
                LocalDate.now(), LocalDate.now().plusMonths(6),
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO,
                BigDecimal.valueOf(1.50), OffsetDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes para salvar lotes a partir de um Restock")
    class SaveFromRestock {

        @Test
        @DisplayName("Deve salvar lotes com sucesso")
        void saveFromRestock_Success() {
            // Arrange
            List<BatchRequestDTO> batchRequests = List.of(requestDTO);
            when(batchRepository.existsByBatchCode(anyString())).thenReturn(false);
            when(batchMapper.toEntity(any(BatchRequestDTO.class))).thenReturn(batch);
            when(productService.findProductById(anyLong())).thenReturn(product);

            // Act
            batchService.saveFromRestock(restock, batchRequests);

            // Assert
            verify(batchRepository).existsByBatchCode(anyString());
            verify(batchRepository).saveAll(anyList());
            verify(productService).findProductById(1L);
            verify(batchMapper).toEntity(any(BatchRequestDTO.class));
            assertEquals(BigDecimal.TEN, product.getTotalQuantity());
            verifyNoMoreInteractions(batchRepository, productService, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar DuplicateBatchCodeException quando o código do lote já existe no banco")
        void saveFromRestock_Fail_DuplicateBatchCodeInDb() {
            // Arrange
            List<BatchRequestDTO> batchRequests = List.of(requestDTO);
            when(batchRepository.existsByBatchCode("LOTE123")).thenReturn(true);

            // Act & Assert
            assertThrows(DuplicateBatchCodeException.class, () -> batchService.saveFromRestock(restock, batchRequests));
            verify(batchRepository).existsByBatchCode("LOTE123");
            verify(batchRepository, never()).saveAll(anyList());
            verifyNoMoreInteractions(batchRepository, productService, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar DuplicateBatchCodeException quando há códigos repetidos na mesma requisição")
        void saveFromRestock_Fail_DuplicateBatchCodeInRequest() {
            // Arrange
            List<BatchRequestDTO> batchRequests = List.of(requestDTO, requestDTO);
            when(batchMapper.toEntity(any(BatchRequestDTO.class))).thenReturn(batch);
            when(productService.findProductById(anyLong())).thenReturn(product);

            // Act & Assert
            assertThrows(DuplicateBatchCodeException.class, () -> batchService.saveFromRestock(restock, batchRequests));
            verify(productService).findProductById(1L);
            verify(batchMapper).toEntity(any(BatchRequestDTO.class));
            verify(batchRepository).existsByBatchCode("LOTE123");
            verify(batchRepository, never()).saveAll(anyList());
            verifyNoMoreInteractions(batchRepository, productService, batchMapper);
        }

        @Test
        @DisplayName("Deve gerar código automático quando o código do lote é nulo ou em branco")
        void saveFromRestock_Success_AutomaticBatchCode() {
            // Arrange
            BatchRequestDTO autoRequest = new BatchRequestDTO(
                    1L,
                    "",
                    LocalDate.now(),
                    LocalDate.now().plusMonths(6),
                    BigDecimal.TEN,
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(1.50)
            );
            List<BatchRequestDTO> batchRequests = List.of(autoRequest);

            when(batchMapper.toEntity(any(BatchRequestDTO.class))).thenReturn(batch);
            when(productService.findProductById(anyLong())).thenReturn(product);
            when(batchRepository.existsByBatchCode(anyString())).thenReturn(false);

            // Act
            batchService.saveFromRestock(restock, batchRequests);

            // Assert
            verify(batchRepository, atLeastOnce()).existsByBatchCode(anyString());
            verify(batchRepository).saveAll(anyList());
            verify(productService).findProductById(1L);
            verify(batchMapper).toEntity(any(BatchRequestDTO.class));
            assertNotNull(batch.getBatchCode());
            assertFalse(batch.getBatchCode().isEmpty());
            verifyNoMoreInteractions(batchRepository, productService, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar lotes em estoque por data")
    class GetAllBatchesInStockByDate {

        @Test
        @DisplayName("Deve retornar página de lotes com sucesso")
        void getAllBatchesInStockByDate_Success() {
            // Arrange
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now().plusDays(10);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Batch> batchPage = new PageImpl<>(List.of(batch));

            when(batchRepository.findByQuantityInStockGreaterThanAndExpirationDateBetween(
                    BigDecimal.ZERO, start, end, pageable)
                ).thenReturn(batchPage);
            when(batchMapper.toResponse(any(Batch.class))).thenReturn(responseDTO);

            // Act
            Page<BatchResponseDTO> result = batchService.getAllBatchesInStockByDate(start, end, pageable);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(batchRepository).findByQuantityInStockGreaterThanAndExpirationDateBetween(BigDecimal.ZERO, start, end, pageable);
            verify(batchMapper).toResponse(any(Batch.class));
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar InvalidDateIntervalException quando a data inicial é após a final")
        void getAllBatchesInStockByDate_Fail_InvalidInterval() {
            // Arrange
            LocalDate start = LocalDate.now().plusDays(10);
            LocalDate end = LocalDate.now();

            // Act & Assert
            assertThrows(InvalidDateIntervalException.class, () -> batchService.getAllBatchesInStockByDate(start, end, Pageable.unpaged()));
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException quando não houver lotes no período")
        void getAllBatchesInStockByDate_Fail_NotFound() {
            // Arrange
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now().plusDays(10);
            when(batchRepository.findByQuantityInStockGreaterThanAndExpirationDateBetween(any(), any(), any(), any()))
                    .thenReturn(Page.empty());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.getAllBatchesInStockByDate(start, end, Pageable.unpaged()));
            verify(batchRepository).findByQuantityInStockGreaterThanAndExpirationDateBetween(any(), any(), any(), any());
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar todos os lotes em estoque")
    class GetAllBatchesInStock {
        @Test
        @DisplayName("Deve retornar todos os lotes em estoque")
        void getAllBatchesInStock_Success() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Batch> batchPage = new PageImpl<>(List.of(batch));
            when(batchRepository.findByQuantityInStockGreaterThan(BigDecimal.ZERO, pageable)).thenReturn(batchPage);
            when(batchMapper.toResponse(batch)).thenReturn(responseDTO);

            // Act
            Page<BatchResponseDTO> result = batchService.getAllBatchesInStock(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(batchRepository).findByQuantityInStockGreaterThan(BigDecimal.ZERO, pageable);
            verify(batchMapper).toResponse(batch);
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar lotes por ID de Restock")
    class GetAllBatchesByRestockId {
        @Test
        @DisplayName("Deve retornar lotes de um restock")
        void getAllBatchesByRestockId_Success() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Batch> batchPage = new PageImpl<>(List.of(batch));
            when(batchRepository.findByRestockId(1L, pageable)).thenReturn(batchPage);
            when(batchMapper.toResponse(batch)).thenReturn(responseDTO);

            // Act
            Page<BatchResponseDTO> result = batchService.getAllBatchesByRestockId(1L, pageable);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(batchRepository).findByRestockId(1L, pageable);
            verify(batchMapper).toResponse(batch);
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException quando não houver lotes para o restock")
        void getAllBatchesByRestockId_Fail_NotFound() {
            // Arrange
            when(batchRepository.findByRestockId(anyLong(), any())).thenReturn(Page.empty());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.getAllBatchesByRestockId(1L, Pageable.unpaged()));
            verify(batchRepository).findByRestockId(anyLong(), any());
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar lote por ID")
    class GetBatchById {
        @Test
        @DisplayName("Deve retornar lote por ID com sucesso")
        void getBatchById_Success() {
            // Arrange
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));
            when(batchMapper.toResponse(batch)).thenReturn(responseDTO);

            // Act
            BatchResponseDTO result = batchService.getBatchById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.batchId());
            verify(batchRepository).findById(1L);
            verify(batchMapper).toResponse(batch);
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException ao buscar ID inexistente")
        void getBatchById_Fail_NotFound() {
            // Arrange
            when(batchRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.getBatchById(1L));
            verify(batchRepository).findById(1L);
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar lotes em estoque de um produto com quantidade mínima")
    class GetBatchesInStockDTO {
        @Test
        @DisplayName("Deve retornar lista de DTOs com sucesso")
        void getBatchesInStockDTO_Success() {
            // Arrange
            when(productService.findProductById(1L)).thenReturn(product);
            when(batchRepository.findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(product, BigDecimal.TEN))
                    .thenReturn(List.of(batch));
            when(batchMapper.toResponseList(anyList())).thenReturn(List.of(responseDTO));

            // Act
            List<BatchResponseDTO> result = batchService.getBatchesInStockDTO(1L, BigDecimal.TEN);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(productService).findProductById(1L);
            verify(batchRepository).findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(product, BigDecimal.TEN);
            verify(batchMapper).toResponseList(anyList());
            verifyNoMoreInteractions(productService, batchRepository, batchMapper);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException quando não houver lotes suficientes")
        void getBatchesInStockDTO_Fail_NotFound() {
            // Arrange
            when(productService.findProductById(1L)).thenReturn(product);
            when(batchRepository.findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(any(), any()))
                    .thenReturn(Collections.emptyList());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.getBatchesInStockDTO(1L, BigDecimal.TEN));
            verify(productService).findProductById(1L);
            verify(batchRepository).findByProductAndQuantityInStockGreaterThanEqualOrderByExpirationDateAsc(product, BigDecimal.TEN);
            verifyNoMoreInteractions(productService, batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para encontrar lotes para venda (FIFO)")
    class FindBatchesInStock {
        @Test
        @DisplayName("Deve retornar lista de lotes que satisfaçam a quantidade solicitada")
        void findBatchesInStock_Success() {
            // Arrange
            Batch batch2 = Batch.builder().quantityInStock(BigDecimal.TEN).build();
            when(batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO))
                    .thenReturn(List.of(batch, batch2));

            // Act
            List<Batch> result = batchService.findBatchesInStock(product, BigDecimal.valueOf(15));

            // Assert
            assertEquals(2, result.size());
            verify(batchRepository).findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO);
            verifyNoMoreInteractions(batchRepository);
        }
    }

    @Nested
    @DisplayName("Testes para buscar lotes prestes a vencer")
    class FindBatchesToExpireIn {
        @Test
        @DisplayName("Deve retornar lotes que vencem em X dias")
        void findBatchesToExpireIn_Success() {
            // Arrange
            when(batchRepository.findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(eq(BigDecimal.ZERO), any(LocalDate.class)))
                    .thenReturn(List.of(batch));
            when(batchMapper.toResponseList(anyList())).thenReturn(List.of(responseDTO));

            // Act
            List<BatchResponseDTO> result = batchService.findBatchesToExpireIn(30);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(batchRepository).findByQuantityInStockGreaterThanAndExpirationDateBeforeOrderByExpirationDate(eq(BigDecimal.ZERO), any(LocalDate.class));
            verify(batchMapper).toResponseList(anyList());
            verifyNoMoreInteractions(batchRepository, batchMapper);
        }
    }

    @Nested
    @DisplayName("Testes para decrementar estoque")
    class DecrementStock {
        @Test
        @DisplayName("Deve decrementar estoque com sucesso de múltiplos lotes")
        void decrementStock_Success() {
            // Arrange
            product.setTotalQuantity(BigDecimal.valueOf(20));
            Batch batch2 = Batch.builder().quantityInStock(BigDecimal.TEN).build();
            when(batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO))
                    .thenReturn(List.of(batch, batch2));

            // Act
            batchService.decrementStock(product, BigDecimal.valueOf(15));

            // Assert
            assertEquals(BigDecimal.ZERO, batch.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(5), batch2.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(5), product.getTotalQuantity());
            verify(batchRepository).findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO);
            verifyNoMoreInteractions(batchRepository);
        }

        @Test
        @DisplayName("Deve lançar InsufficientQuantityInSaleException quando o estoque total é insuficiente")
        void decrementStock_Fail_Insufficient() {
            // Arrange
            when(batchRepository.findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO))
                    .thenReturn(List.of(batch));

            // Act & Assert
            assertThrows(InsufficientQuantityInSaleException.class, () -> batchService.decrementStock(product, BigDecimal.valueOf(15)));
            verify(batchRepository).findByProductAndQuantityInStockGreaterThanOrderByExpirationDateAsc(product, BigDecimal.ZERO);
            verifyNoMoreInteractions(batchRepository);
        }
    }

    @Nested
    @DisplayName("Testes para reportar perda de lote")
    class ReportLoss {
        @Test
        @DisplayName("Deve reportar perda com sucesso")
        void reportLoss_Success() {
            // Arrange
            BatchLossReportRequestDTO lossDTO = new BatchLossReportRequestDTO(1L, BigDecimal.valueOf(2L), "Quebrado");
            product.setTotalQuantity(BigDecimal.TEN);
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

            // Act
            batchService.reportLoss(lossDTO);

            // Assert
            assertEquals(BigDecimal.valueOf(8), batch.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(2L), batch.getQuantityLost());
            assertEquals(BigDecimal.valueOf(8), product.getTotalQuantity());
            verify(batchRepository).findById(1L);
            verify(batchRepository).save(batch);
            verifyNoMoreInteractions(batchRepository);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException ao reportar perda de ID inexistente")
        void reportLoss_Fail_NotFound() {
            // Arrange
            BatchLossReportRequestDTO lossDTO = new BatchLossReportRequestDTO(1L, BigDecimal.valueOf(2L), "Quebrado");
            when(batchRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.reportLoss(lossDTO));
            verify(batchRepository).findById(1L);
            verifyNoMoreInteractions(batchRepository);
        }
    }

    @Nested
    @DisplayName("Testes para invalidar lote")
    class InvalidateBatch {
        @Test
        @DisplayName("Deve invalidar lote com sucesso")
        void invalidateBatch_Success() {
            // Arrange
            BatchInvalidationRequestDTO invalidDTO = new BatchInvalidationRequestDTO("Vencido");
            product.setTotalQuantity(BigDecimal.TEN);
            when(batchRepository.findById(1L)).thenReturn(Optional.of(batch));

            // Act
            batchService.invalidateBatch(1L, invalidDTO);

            // Assert
            assertEquals(BigDecimal.ZERO, batch.getQuantityInStock());
            assertEquals(BigDecimal.TEN, batch.getQuantityLost());
            assertEquals(BigDecimal.ZERO, product.getTotalQuantity());
            verify(batchRepository).findById(1L);
            verify(batchRepository).save(batch);
            verifyNoMoreInteractions(batchRepository);
        }

        @Test
        @DisplayName("Deve lançar BatchNotFoundException ao invalidar ID inexistente")
        void invalidateBatch_Fail_NotFound() {
            // Arrange
            BatchInvalidationRequestDTO invalidDTO = new BatchInvalidationRequestDTO("Vencido");
            when(batchRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BatchNotFoundException.class, () -> batchService.invalidateBatch(1L, invalidDTO));
            verify(batchRepository).findById(1L);
            verifyNoMoreInteractions(batchRepository);
        }
    }
}
