package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SaleItemRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleItemResponseDTO;
import com.eduribeiro8.LilMarket.dto.SaleRequestDTO;
import com.eduribeiro8.LilMarket.dto.SaleResponseDTO;
import com.eduribeiro8.LilMarket.entity.*;
import com.eduribeiro8.LilMarket.mapper.SaleMapper;
import com.eduribeiro8.LilMarket.repository.*;
import com.eduribeiro8.LilMarket.rest.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService Tests")
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerPaymentRepository customerPaymentRepository;

    @Mock
    private BatchService batchService;

    @Mock
    private SaleMapper saleMapper;

    @Captor
    private ArgumentCaptor<Sale> saleCaptor;

    @InjectMocks
    private SaleServiceImpl saleService;

    private SaleRequestDTO requestDTO;
    private SaleResponseDTO responseDTO;
    private User user;
    private Customer customer;
    private List<SaleItemRequestDTO> itemRequestDTOList;
    private Product product;
    private List<Batch> batches;
    private Sale salePersisted;

    @BeforeEach
    void setUp(){
        product = Product.builder()
                .id(1L)
                .name("Agua")
                .price(new BigDecimal("20.00"))
                .build();

        itemRequestDTOList = List.of(new SaleItemRequestDTO(
                1L,
                new BigDecimal("2.00"),
                new BigDecimal("0.00")
        ));

        requestDTO = new SaleRequestDTO(
                1L,
                1L,
                itemRequestDTOList,
                new BigDecimal("40.00"),
                new BigDecimal("0.00"),
                false,
                "",
                PaymentStatus.PAID,
                PaymentMethod.PIX
        );

        responseDTO = new SaleResponseDTO(
                1L,
                OffsetDateTime.now(),
                "João",
                "Maria",
                List.of(new SaleItemResponseDTO(
                                1L,
                                "Agua",
                                new BigDecimal("1.00"),
                                new BigDecimal("20.00"),
                                new BigDecimal("20.00"),
                                new BigDecimal("0.00"),
                                1L
                        ),
                        new SaleItemResponseDTO(
                                1L,
                                "Agua",
                                new BigDecimal("1.00"),
                                new BigDecimal("20.00"),
                                new BigDecimal("20.00"),
                                new BigDecimal("0.00"),
                                2L
                        )
                ),
                new BigDecimal("40.00"),
                new BigDecimal("0.00"),
                new BigDecimal("40.00"),
                new BigDecimal("9.00"),
                new BigDecimal("0.00"),
                "PAID",
                ""
        );

        customer = Customer.builder()
                .id(1L)
                .firstName("Joao")
                .credit(new BigDecimal("0.00"))
                .build();

        user = User.builder()
                .id(1L)
                .username("Maria")
                .build();
        Batch batch1 = Batch.builder()
                .id(1L)
                .product(product)
                .batchCode("ABC-123")
                .quantityInStock(new BigDecimal("1"))
                .purchasePrice(new BigDecimal("15.00"))
                .build();

        Batch batch2 = Batch.builder()
                .id(2L)
                .product(product)
                .batchCode("ABC-124")
                .quantityInStock(new BigDecimal("1"))
                .purchasePrice(new BigDecimal("16.00"))
                .build();

        batches = List.of(
                batch1,
                batch2
        );

        salePersisted = Sale.builder()
                .user(user)
                .customer(customer)
                .items(List.of(
                        SaleItem.builder()
                                .product(product)
                                .batch(batch1)
                                .unitPrice(new BigDecimal("20.00"))
                                .subtotal(new BigDecimal("20.00"))
                                .build(),
                        SaleItem.builder()
                                .product(product)
                                .batch(batch2)
                                .unitPrice(new BigDecimal("20.00"))
                                .subtotal(new BigDecimal("20.00"))
                                .build()
                ))
                .total(new BigDecimal("40.00"))
                .netProfit(new BigDecimal("9.00"))
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Testes para criação de uma nova venda")
    class CreateSale {



        @Test
        @DisplayName("Deve retornar um SaleResponseDTO ao criar uma nova venda com sucesso")
        void save_Success() {
            //Arrange

            CustomerPayment customerPaymentExpected = CustomerPayment.builder()
                            .customer(customer)
                            .amountPaid(new BigDecimal("40.00"))
                            .build();

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchService.findBatchesInStock(product, new BigDecimal("2.00"))).thenReturn(batches);
            when(batchService.decrementBatches(batches, product, new BigDecimal("2.00"))).thenReturn(batches);
            when(saleRepository.save(any(Sale.class))).thenReturn(salePersisted);
            when(saleMapper.toResponse(salePersisted)).thenReturn(responseDTO);
            when(customerPaymentRepository.save(any(CustomerPayment.class))).thenReturn(customerPaymentExpected);

            //Act
            SaleResponseDTO response = saleService.save(requestDTO);

            //Assert
            verify(saleRepository).save(saleCaptor.capture());
            Sale capturedSale = saleCaptor.getValue();
            assertNotNull(response);

            assertEquals(response.items().get(0).batchId(), salePersisted.getItems().get(0).getBatch().getId());
            assertEquals(response.items().get(1).batchId(), salePersisted.getItems().get(1).getBatch().getId());
            assertEquals(0, salePersisted.getTotal().compareTo(response.totalAmount()));
            assertEquals(0, capturedSale.getTotal().compareTo(new BigDecimal("40.00")));
            assertEquals(0, salePersisted.getNetProfit().compareTo(response.netProfit()));
            assertEquals(0, capturedSale.getNetProfit().compareTo(new BigDecimal("9.00")));

            Mockito.verifyNoMoreInteractions(batchService, userRepository, productRepository,
                    customerRepository, customerPaymentRepository, saleRepository, saleMapper
            );

        }

        @Test
        @DisplayName("Deve lançar CustomerNotFound ao buscar por um cliente não registrado")
        void save_Fail_CustomerNotFound(){
            //Arrange

            when(customerRepository.findById(1L)).thenReturn(Optional.empty());

            //Act
            CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () ->
                saleService.save(requestDTO)
            );

            //Assert
            assertTrue(exception.getMessage().contains("not found"));

            verify(saleRepository, never()).save(any());
            verifyNoInteractions(batchService, userRepository, saleRepository,
                    saleMapper, customerPaymentRepository, productRepository
            );
            verifyNoMoreInteractions(customerRepository);
        }

        @Test
        @DisplayName("Deve lançar UserNotFound ao buscar por um usuário não registrado")
        void save_Fail_UserNotFound(){
            //Arrange

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            //Act
            UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                saleService.save(requestDTO)
            );

            //Assert
            assertTrue(exception.getMessage().contains("not found"));

            verify(saleRepository, never()).save(any());
            verifyNoInteractions(batchService, saleRepository,
                    saleMapper, customerPaymentRepository, productRepository
            );
            verifyNoMoreInteractions(customerRepository, userRepository);
        }

        @Test
        @DisplayName("Deve lançar ProductNotFound ao buscar por um produto não registrado")
        void save_Fail_ProductNotFound(){
            //Arrange

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            //Act
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () ->
                saleService.save(requestDTO)
            );

            //Assert
            assertTrue(exception.getMessage().contains("not found"));

            verify(saleRepository, never()).save(any());
            verifyNoInteractions(batchService, saleRepository,
                    saleMapper, customerPaymentRepository
            );
            verifyNoMoreInteractions(customerRepository, userRepository, productRepository);
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao não pagar uma venda não fiada")
        void save_Fail_BusinessException(){
            //Arrange

        requestDTO = new SaleRequestDTO(
                1L,
                1L,
                itemRequestDTOList,
                new BigDecimal("39.00"),
                new BigDecimal("0.00"),
                false,
                "",
                PaymentStatus.PAID, //bait
                PaymentMethod.PIX
        );

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchService.findBatchesInStock(product, new BigDecimal("2.00"))).thenReturn(batches);
            when(batchService.decrementBatches(batches, product, new BigDecimal("2.00"))).thenReturn(batches);

            //Act
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                saleService.save(requestDTO);
            });

            //Assert
            assertTrue(exception.getMessage().contains("Venda não pode ser completada"));

            verify(saleRepository, never()).save(any());
            verifyNoInteractions(saleMapper, customerPaymentRepository);
            verifyNoMoreInteractions(customerRepository, userRepository, productRepository,
                    batchService, saleRepository);
        }
    }

    @Nested
    @DisplayName("Testes para encontrar um Sale")
    class FindSale{

        @Test
        @DisplayName("Deve retornar um SaleResponseDTO ao encontrar um Sale")
        void findSaleById_Success() {
            //Arrange

            when(saleRepository.findById(1L)).thenReturn(Optional.of(salePersisted));
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(responseDTO);

            //Act
            SaleResponseDTO response = saleService.findSaleById(1L);

            //Assert
            assertNotNull(response);

            assertEquals(response.items().get(0).batchId(), salePersisted.getItems().get(0).getBatch().getId());
            assertEquals(response.items().get(1).batchId(), salePersisted.getItems().get(1).getBatch().getId());
            assertEquals(0, salePersisted.getTotal().compareTo(response.totalAmount()));
            assertEquals(0, salePersisted.getNetProfit().compareTo(response.netProfit()));

            verify(saleRepository, times(1)).findById(1L);
            verifyNoInteractions(batchService, userRepository, customerPaymentRepository,
                    customerRepository, productRepository);
            verifyNoMoreInteractions(saleRepository, saleMapper);
        }


        @Test
        @DisplayName("Deve lançar SaleNotFound ao buscar um Sale não registrado")
        void findSaleById_Fail_SaleNotFound() {
            //Arrange

            when(saleRepository.findById(1L)).thenReturn(Optional.empty());

            //Act

            SaleNotFoundException exception = assertThrows(SaleNotFoundException.class, () ->
                saleService.findSaleById(1L)
            );

            //Assert

            assertTrue(exception.getMessage().contains("not found"));
            verifyNoInteractions(batchService, userRepository, customerPaymentRepository,
                    customerRepository, productRepository, saleMapper);
            verifyNoMoreInteractions(saleRepository);
        }

        @Test
        @DisplayName("Deve retornar um List<SaleResponseDTO> que tem o timestamp entre o intervalo buscado")
        void getSalesByDate_Success() {
            //Arrange
            OffsetDateTime startDate = OffsetDateTime.now().minusDays(1L);
            OffsetDateTime endDate = OffsetDateTime.now().plusDays(1L);

            when(saleRepository.findByTimestampBetween(startDate, endDate)).thenReturn(List.of(salePersisted));
            when(saleMapper.toResponseList(List.of(salePersisted))).thenReturn(List.of(responseDTO));

            //Act
            List<SaleResponseDTO> responseDTOS = saleService.getSalesByDate(startDate, endDate);

            //Assert
            assertNotNull(responseDTOS);
            assertNotNull(responseDTOS.get(0).timestamp());

            assertEquals(0, responseDTOS.get(0).netProfit().compareTo(salePersisted.getNetProfit()));
            assertEquals(0, responseDTOS.get(0).totalAmount().compareTo(salePersisted.getTotal()));

            verifyNoInteractions(batchService, userRepository, customerPaymentRepository,
                    customerRepository, productRepository);
            verifyNoMoreInteractions(saleRepository, saleMapper);
        }

        @Test
        @DisplayName("Deve lançar um InvalidDateInterval ao buscar um intervalo impossível")
        void getSalesByDate_Fail_InvalidDateInterval() {
            //Arrange
            OffsetDateTime startDate = OffsetDateTime.now().plusDays(2L);
            OffsetDateTime endDate = OffsetDateTime.now().plusDays(1L);

            //Act
            InvalidDateIntervalException exception = assertThrows(InvalidDateIntervalException.class, () ->
                saleService.getSalesByDate(startDate, endDate)
            );

            //Assert
            assertTrue(exception.getMessage().contains("data final"));

            verifyNoInteractions(batchService, userRepository, customerPaymentRepository,
                    customerRepository, productRepository, saleRepository, saleMapper);
        }

        @Test
        @DisplayName("Deve lançar um SaleNotFound caso não encontre nenhuma venda no intervalo")
        void getSalesByDate_Fail_SaleNotFound() {
            //Arrange
            OffsetDateTime startDate = OffsetDateTime.now().minusDays(1L);
            OffsetDateTime endDate = OffsetDateTime.now().plusDays(1L);

            when(saleRepository.findByTimestampBetween(startDate, endDate)).thenReturn(List.of());

            //Act
            SaleNotFoundException exception = assertThrows(SaleNotFoundException.class, () ->
                saleService.getSalesByDate(startDate, endDate)
            );

            //Assert
            assertTrue(exception.getMessage().contains("Nenhuma venda encontrada"));

            verifyNoInteractions(batchService, userRepository, customerPaymentRepository,
                    customerRepository, productRepository, saleMapper);
            verifyNoMoreInteractions(saleRepository);
        }
    }
    @Nested
    @DisplayName("Testes para simulação de descontos")
    class DiscountSimulation {

        @Test
        @DisplayName("Deve calcular corretamente os subtotais, totais e lucros quando ha descontos no item e na venda global")
        void save_WithDiscounts_CalculatesValuesCorrectly() {
            // Arrange
            List<SaleItemRequestDTO> discountItemRequestDTOList = List.of(new SaleItemRequestDTO(
                    1L,
                    new BigDecimal("2.00"), // quantity
                    new BigDecimal("5.00") // unitDiscount
            ));

            SaleRequestDTO discountRequestDTO = new SaleRequestDTO(
                    1L,
                    1L,
                    discountItemRequestDTOList,
                    new BigDecimal("20.00"), // amountPaid
                    new BigDecimal("10.00"), // discount (Global)
                    false,
                    "",
                    PaymentStatus.PAID,
                    PaymentMethod.PIX
            );

            Batch discountBatch = Batch.builder()
                    .id(1L)
                    .product(product)
                    .batchCode("ABC-123")
                    .quantityInStock(new BigDecimal("2.00"))
                    .purchasePrice(new BigDecimal("10.00"))
                    .build();

            List<Batch> discountBatches = List.of(discountBatch);

            Sale discountSalePersisted = Sale.builder()
                    .user(user)
                    .customer(customer)
                    .total(new BigDecimal("20.00")) // 30 - 10 global
                    .discount(new BigDecimal("10.00"))
                    .netProfit(new BigDecimal("0.00"))
                    .amountPaid(new BigDecimal("20.00"))
                    .items(List.of(
                            SaleItem.builder()
                                    .product(product)
                                    .batch(discountBatch)
                                    .unitPrice(new BigDecimal("20.00"))
                                    .unitDiscount(new BigDecimal("5.00"))
                                    .subtotal(new BigDecimal("30.00")) // (20-5)*2
                                    .build()
                    ))
                    .timestamp(OffsetDateTime.now())
                    .build();

            SaleResponseDTO discountResponseDTO = new SaleResponseDTO(
                    1L,
                    OffsetDateTime.now(),
                    "João",
                    "Maria",
                    List.of(new SaleItemResponseDTO(
                            1L,
                            "Agua",
                            new BigDecimal("2.00"),
                            new BigDecimal("20.00"),
                            new BigDecimal("30.00"),
                            new BigDecimal("5.00"),
                            1L
                    )),
                    new BigDecimal("20.00"), // totalAmount
                    new BigDecimal("10.00"), // discount
                    new BigDecimal("20.00"), // amountPaid
                    new BigDecimal("0.00"),  // netProfit
                    new BigDecimal("0.00"),  // change
                    "PAID",
                    ""
            );

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchService.findBatchesInStock(product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(batchService.decrementBatches(discountBatches, product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(saleRepository.save(any(Sale.class))).thenReturn(discountSalePersisted);
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(discountResponseDTO);

            // Act
            SaleResponseDTO response = saleService.save(discountRequestDTO);

            // Assert
            verify(saleRepository).save(saleCaptor.capture());
            Sale capturedSale = saleCaptor.getValue();

            assertNotNull(response);

            assertEquals(0, capturedSale.getDiscount().compareTo(new BigDecimal("10.00")), "Desconto global deve ser 10");
            assertEquals(0, capturedSale.getTotal().compareTo(new BigDecimal("20.00")), "Total final da venda deve ser 20");
            assertEquals(0, capturedSale.getNetProfit().compareTo(new BigDecimal("0.00")), "Lucro liquido deve ser 0");

            SaleItem capturedItem = capturedSale.getItems().get(0);
            assertEquals(0, capturedItem.getUnitDiscount().compareTo(new BigDecimal("5.00")), "Desconto unitario deve ser 5");
            assertEquals(0, capturedItem.getSubtotal().compareTo(new BigDecimal("30.00")), "Subtotal do item deve ser 30");
        }

        @Test
        @DisplayName("Deve calcular corretamente os totais e lucros quando ha apenas desconto unitario no item")
        void save_WithOnlyItemDiscount_CalculatesValuesCorrectly() {
            // Arrange
            List<SaleItemRequestDTO> discountItemRequestDTOList = List.of(new SaleItemRequestDTO(
                    1L,
                    new BigDecimal("2.00"), // quantity
                    new BigDecimal("5.00") // unitDiscount
            ));

            SaleRequestDTO discountRequestDTO = new SaleRequestDTO(
                    1L,
                    1L,
                    discountItemRequestDTOList,
                    new BigDecimal("30.00"), // amountPaid
                    new BigDecimal("0.00"), // discount (Global)
                    false,
                    "",
                    PaymentStatus.PAID,
                    PaymentMethod.PIX
            );

            Batch discountBatch = Batch.builder()
                    .id(1L)
                    .product(product)
                    .batchCode("ABC-123")
                    .quantityInStock(new BigDecimal("2.00"))
                    .purchasePrice(new BigDecimal("10.00"))
                    .build();

            List<Batch> discountBatches = List.of(discountBatch);

            Sale discountSalePersisted = Sale.builder()
                    .user(user)
                    .customer(customer)
                    .total(new BigDecimal("30.00"))
                    .discount(new BigDecimal("0.00"))
                    .netProfit(new BigDecimal("10.00"))
                    .amountPaid(new BigDecimal("30.00"))
                    .items(List.of(
                            SaleItem.builder()
                                    .product(product)
                                    .batch(discountBatch)
                                    .unitPrice(new BigDecimal("20.00"))
                                    .unitDiscount(new BigDecimal("5.00"))
                                    .subtotal(new BigDecimal("30.00"))
                                    .build()
                    ))
                    .timestamp(OffsetDateTime.now())
                    .build();

            SaleResponseDTO discountResponseDTO = new SaleResponseDTO(
                    1L,
                    OffsetDateTime.now(),
                    "João",
                    "Maria",
                    List.of(new SaleItemResponseDTO(
                            1L,
                            "Agua",
                            new BigDecimal("2.00"),
                            new BigDecimal("20.00"),
                            new BigDecimal("30.00"),
                            new BigDecimal("5.00"),
                            1L
                    )),
                    new BigDecimal("30.00"), // totalAmount
                    new BigDecimal("0.00"), // discount
                    new BigDecimal("30.00"), // amountPaid
                    new BigDecimal("10.00"),  // netProfit
                    new BigDecimal("0.00"),  // change
                    "PAID",
                    ""
            );

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchService.findBatchesInStock(product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(batchService.decrementBatches(discountBatches, product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(saleRepository.save(any(Sale.class))).thenReturn(discountSalePersisted);
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(discountResponseDTO);

            // Act
            SaleResponseDTO response = saleService.save(discountRequestDTO);

            // Assert
            verify(saleRepository, times(1)).save(saleCaptor.capture());
            Sale capturedSale = saleCaptor.getValue();

            assertNotNull(response);

            assertEquals(0, capturedSale.getDiscount().compareTo(new BigDecimal("0.00")), "Desconto global deve ser 0");
            assertEquals(0, capturedSale.getTotal().compareTo(new BigDecimal("30.00")), "Total final da venda deve ser 30");
            assertEquals(0, capturedSale.getNetProfit().compareTo(new BigDecimal("10.00")), "Lucro liquido deve ser 10");

            SaleItem capturedItem = capturedSale.getItems().get(0);
            assertEquals(0, capturedItem.getUnitDiscount().compareTo(new BigDecimal("5.00")), "Desconto unitario deve ser 5");
            assertEquals(0, capturedItem.getSubtotal().compareTo(new BigDecimal("30.00")), "Subtotal do item deve ser 30");
        }

        @Test
        @DisplayName("Deve calcular corretamente os totais e lucros quando ha apenas desconto global na venda")
        void save_WithOnlyGlobalDiscount_CalculatesValuesCorrectly() {
            // Arrange
            List<SaleItemRequestDTO> discountItemRequestDTOList = List.of(new SaleItemRequestDTO(
                    1L,
                    new BigDecimal("2.00"), // quantity
                    new BigDecimal("0.00") // unitDiscount
            ));

            SaleRequestDTO discountRequestDTO = new SaleRequestDTO(
                    1L,
                    1L,
                    discountItemRequestDTOList,
                    new BigDecimal("25.00"), // amountPaid
                    new BigDecimal("15.00"), // discount (Global)
                    false,
                    "",
                    PaymentStatus.PAID,
                    PaymentMethod.PIX
            );

            Batch discountBatch = Batch.builder()
                    .id(1L)
                    .product(product)
                    .batchCode("ABC-123")
                    .quantityInStock(new BigDecimal("2.00"))
                    .purchasePrice(new BigDecimal("10.00"))
                    .build();

            List<Batch> discountBatches = List.of(discountBatch);

            Sale discountSalePersisted = Sale.builder()
                    .user(user)
                    .customer(customer)
                    .total(new BigDecimal("25.00"))
                    .discount(new BigDecimal("15.00"))
                    .netProfit(new BigDecimal("5.00")) // lucro normal 20, menos 15 global = 5
                    .amountPaid(new BigDecimal("25.00"))
                    .items(List.of(
                            SaleItem.builder()
                                    .product(product)
                                    .batch(discountBatch)
                                    .unitPrice(new BigDecimal("20.00"))
                                    .unitDiscount(new BigDecimal("0.00"))
                                    .subtotal(new BigDecimal("40.00"))
                                    .build()
                    ))
                    .timestamp(OffsetDateTime.now())
                    .build();

            SaleResponseDTO discountResponseDTO = new SaleResponseDTO(
                    1L,
                    OffsetDateTime.now(),
                    "João",
                    "Maria",
                    List.of(new SaleItemResponseDTO(
                            1L,
                            "Agua",
                            new BigDecimal("2.00"),
                            new BigDecimal("20.00"),
                            new BigDecimal("40.00"),
                            new BigDecimal("0.00"),
                            1L
                    )),
                    new BigDecimal("25.00"), // totalAmount
                    new BigDecimal("15.00"), // discount
                    new BigDecimal("25.00"), // amountPaid
                    new BigDecimal("5.00"),  // netProfit
                    new BigDecimal("0.00"),  // change
                    "PAID",
                    ""
            );

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(batchService.findBatchesInStock(product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(batchService.decrementBatches(discountBatches, product, new BigDecimal("2.00"))).thenReturn(discountBatches);
            when(saleRepository.save(any(Sale.class))).thenReturn(discountSalePersisted);
            when(saleMapper.toResponse(any(Sale.class))).thenReturn(discountResponseDTO);

            // Act
            SaleResponseDTO response = saleService.save(discountRequestDTO);

            // Assert
            verify(saleRepository, times(1)).save(saleCaptor.capture());
            Sale capturedSale = saleCaptor.getValue();

            assertNotNull(response);

            assertEquals(0, capturedSale.getDiscount().compareTo(new BigDecimal("15.00")), "Desconto global deve ser 15");
            assertEquals(0, capturedSale.getTotal().compareTo(new BigDecimal("25.00")), "Total final da venda deve ser 25");
            assertEquals(0, capturedSale.getNetProfit().compareTo(new BigDecimal("5.00")), "Lucro liquido deve ser 5");

            SaleItem capturedItem = capturedSale.getItems().get(0);
            assertEquals(0, capturedItem.getUnitDiscount().compareTo(new BigDecimal("0.00")), "Desconto unitario deve ser 0");
            assertEquals(0, capturedItem.getSubtotal().compareTo(new BigDecimal("40.00")), "Subtotal do item deve ser 40");
        }
    }
}
