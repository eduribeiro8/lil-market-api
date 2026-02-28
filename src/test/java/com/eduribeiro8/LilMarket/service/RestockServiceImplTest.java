package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.BatchRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockRequestDTO;
import com.eduribeiro8.LilMarket.dto.RestockResponseDTO;
import com.eduribeiro8.LilMarket.entity.Restock;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.RestockMapper;
import com.eduribeiro8.LilMarket.repository.RestockRepository;
import com.eduribeiro8.LilMarket.rest.exception.RestockNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestockService Tests")
class RestockServiceImplTest {

    @Mock
    private RestockRepository restockRepository;

    @Mock
    private RestockMapper restockMapper;

    @Mock
    private BatchService batchService;

    @Mock
    private SupplierService supplierService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private RestockServiceImpl restockService;

    private Supplier supplier;
    private Restock restockPersisted;
    private RestockResponseDTO responseDTO;

    @BeforeEach
    public void setUp(){
        supplier = Supplier.builder()
                .id(1L)
                .name("ABC")
                .district("DEF")
                .build();

        restockPersisted = Restock.builder()
                .id(1L)
                .supplier(supplier)
                .boughtAt(LocalDate.now())
                .createdAt(OffsetDateTime.now())
                .amountPaid(new BigDecimal("100.00"))
                .build();

        responseDTO = new RestockResponseDTO(
                1L,
                1L,
                "ABC",
                new BigDecimal("100.00"),
                LocalDate.now(),
                OffsetDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes para salvar um Restock")
    class CreateRestock {

        @Test
        void save_Success() {
            //Arrange
            RestockRequestDTO requestDTO = new RestockRequestDTO(
                    1L,
                    List.of(new BatchRequestDTO(
                                    1L,
                                    "",
                                    null,
                                    LocalDate.now().plusDays(100),
                                    new BigDecimal("4"),
                                    BigDecimal.ZERO,
                                    new BigDecimal("15.00")
                            ),
                            new BatchRequestDTO(
                                    2L,
                                    "",
                                    null,
                                    LocalDate.now().plusDays(200),
                                    new BigDecimal("2"),
                                    BigDecimal.ZERO,
                                    new BigDecimal("20.00")
                            )
                    ),
                    new BigDecimal("100.00"),
                    LocalDate.now()
            );

            when(supplierService.findById(1L)).thenReturn(supplier);
            when(restockRepository.save(Mockito.any(Restock.class))).thenReturn(restockPersisted);
            when(restockMapper.toResponse(restockPersisted)).thenReturn(responseDTO);

            //Act
            RestockResponseDTO serviceResponse = restockService.save(requestDTO);

            //Assert
            assertNotNull(serviceResponse);

            assertEquals(serviceResponse.id(), restockPersisted.getId());
            assertEquals(serviceResponse.supplierId(), restockPersisted.getSupplier().getId());
            assertEquals(serviceResponse.supplierName(), restockPersisted.getSupplier().getName());
            assertEquals(0, serviceResponse.amountPaid().compareTo(restockPersisted.getAmountPaid()));
            assertNotNull(serviceResponse.createdAt());
            assertNotNull(serviceResponse.boughtAt());

            verify(batchService).saveFromRestock(any(Restock.class), anyList());
            verify(productService, times(2)).calculatePriceBasedOnStock(anyLong());

            verify(restockRepository, times(1)).save(any(Restock.class));
            verifyNoMoreInteractions(batchService, productService, supplierService, restockMapper);
        }
    }

    @Nested
    @DisplayName("Testes para encontrar Restock")
    class FindRestock{

        @Test
        @DisplayName("Deve retornar um Restock ao buscar por um id cadastrado")
        void findById_Success() {
            //Arrange

            when(restockRepository.findById(1L)).thenReturn(Optional.of(restockPersisted));

            //Act
            Restock response = restockService.findById(1L);

            //Assert
            assertNotNull(response);
            assertEquals(1, response.getId());
            assertEquals(0, response.getAmountPaid().compareTo(new BigDecimal("100.00")));
            assertNotNull(response.getBoughtAt());
            assertEquals(supplier, response.getSupplier());
            assertNotNull(response.getCreatedAt());

            verify(restockRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar RestockNotFound ao procurar por um Restock não cadastrado")
        void findById_Fail_RestockNotFound() {
            //Arrange

            when(restockRepository.findById(1L)).thenReturn(Optional.empty());

            //Act
            RestockNotFoundException exception = assertThrows(RestockNotFoundException.class, () -> {
                restockService.findById(1L);
            });

            //Assert
            assertNotNull(exception);

            assertTrue(exception.getMessage().contains("não encontrada"));

            verifyNoMoreInteractions(restockRepository);
        }

        @Test
        @DisplayName("Deve retornar um RestockResponseDTO ao buscar por um id cadastrado")
        void findByIdDTO_Success() {
            //Arrange

            when(restockRepository.findById(1L)).thenReturn(Optional.of(restockPersisted));
            when(restockMapper.toResponse(restockPersisted)).thenReturn(responseDTO);

            //Act
            RestockResponseDTO response = restockService.findByIdDTO(1L);

            //Assert
            assertNotNull(response);
            assertEquals(1, response.id());
            assertEquals(0, response.amountPaid().compareTo(new BigDecimal("100.00")));
            assertNotNull(response.boughtAt());
            assertEquals(1, response.supplierId());
            assertNotNull(response.createdAt());

            verify(restockRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar RestockNotFound ao procurar por um Restock não cadastrado")
        void findByIdDTO_Fail_RestockNotFound() {
            //Arrange

            when(restockRepository.findById(1L)).thenReturn(Optional.empty());

            //Act
            RestockNotFoundException exception = assertThrows(RestockNotFoundException.class, () ->
                restockService.findByIdDTO(1L)
            );

            //Assert
            assertNotNull(exception);

            assertTrue(exception.getMessage().contains("não encontrada"));

            verifyNoMoreInteractions(restockRepository);
        }

        @Test
        @DisplayName("Deve retornar um Page<RestockResponseDTO> ao buscar com um Pageable")
        void getAll_Success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Restock> restocks = List.of(restockPersisted);
            Page<Restock> restockPage = new PageImpl<>(restocks, pageable, restocks.size());

            when(restockRepository.findAll(pageable)).thenReturn(restockPage);
            when(restockMapper.toResponse(any(Restock.class))).thenReturn(responseDTO);

            //Act

            Page<RestockResponseDTO> result = restockService.getAll(pageable);

            //Assert
            assertNotNull(result);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertEquals(1L, result.getContent().get(0).id());
            assertEquals(0, result.getContent().get(0).amountPaid().compareTo(new BigDecimal("100.00")));
            assertNotNull(result.getContent().get(0).boughtAt());
            assertEquals(1L, result.getContent().get(0).supplierId());
            assertNotNull(result.getContent().get(0).createdAt());

            verifyNoMoreInteractions(restockRepository);
        }

        @Test
        @DisplayName("Deve lançar RestockNotFound ao não ter Restock cadastrado")
        void getAll_Fail_RestockNotFound() {
            //Arrange
            Page<Restock> restockPage = new PageImpl<>(List.of(), Pageable.unpaged(), 0);

            when(restockRepository.findAll(Pageable.unpaged())).thenReturn(restockPage);

            //Act

            RestockNotFoundException exception = assertThrows(RestockNotFoundException.class, () ->
                restockService.getAll(Pageable.unpaged())
            );

            //Assert
            assertNotNull(exception);

            assertTrue(exception.getMessage().contains("há compras registradas"));
            verify(restockRepository).findAll(Pageable.unpaged());
            verifyNoMoreInteractions(restockMapper);
        }
    }
}