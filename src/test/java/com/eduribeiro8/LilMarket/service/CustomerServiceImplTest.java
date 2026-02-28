package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.*;
import com.eduribeiro8.LilMarket.entity.*;
import com.eduribeiro8.LilMarket.mapper.CustomerMapper;
import com.eduribeiro8.LilMarket.mapper.CustomerPaymentMapper;
import com.eduribeiro8.LilMarket.repository.CustomerPaymentRepository;
import com.eduribeiro8.LilMarket.repository.CustomerRepository;
import com.eduribeiro8.LilMarket.rest.exception.BusinessException;
import com.eduribeiro8.LilMarket.rest.exception.CustomerNotFoundException;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerPaymentRepository customerPaymentRepository;

    @Mock
    private CustomerPaymentMapper customerPaymentMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerResponseDTO customerResponseDTO;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Joao")
                .lastName("Silva")
                .email("joao@exemplo.com")
                .phoneNumber("+5511999999999")
                .address("Rua abc, 123")
                .credit(BigDecimal.valueOf(100.00))
                .createdAt(OffsetDateTime.now())
                .build();

        customerResponseDTO = new CustomerResponseDTO(
                1L,
                "Joao",
                "Silva",
                "joao@exemplo.com",
                "+5511999999999",
                "Rua abc, 123",
                BigDecimal.valueOf(100.00),
                OffsetDateTime.now()
        );
    }

    @Nested
    @DisplayName("Testes para buscar todos os clientes")
    class FindAll {

        @Test
        @DisplayName("Deve retornar uma lista de clientes quando existirem clientes cadastrados")
        void findAll_Success() {
            // Arrange
            List<Customer> customers = List.of(customer);
            List<CustomerResponseDTO> responseDTOs = List.of(customerResponseDTO);

            when(customerRepository.findAll()).thenReturn(customers);
            when(customerMapper.toResponseList(customers)).thenReturn(responseDTOs);

            // Act
            List<CustomerResponseDTO> result = customerService.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(customerResponseDTO.id(), result.get(0).id());

            verify(customerRepository, times(1)).findAll();
            verify(customerMapper, times(1)).toResponseList(customers);
            verifyNoMoreInteractions(customerRepository, customerMapper);
        }
    }

    @Nested
    @DisplayName("Testes para buscar cliente por ID")
    class FindById {

        @Test
        @DisplayName("Deve retornar um cliente quando o ID existir")
        void findById_Success() {
            // Arrange
            Long id = 1L;
            when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
            when(customerMapper.toResponse(customer)).thenReturn(customerResponseDTO);

            // Act
            CustomerResponseDTO result = customerService.findById(id);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.id());

            verify(customerRepository, times(1)).findById(id);
            verify(customerMapper, times(1)).toResponse(customer);
            verifyNoMoreInteractions(customerRepository, customerMapper);
        }

        @Test
        @DisplayName("Deve lançar CustomerNotFoundException quando o ID não existir")
        void findById_Fail_CustomerNotFound() {
            // Arrange
            Long id = 99L;
            when(customerRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CustomerNotFoundException.class, () -> customerService.findById(id));

            verify(customerRepository, times(1)).findById(id);
            verifyNoInteractions(customerMapper);
            verifyNoMoreInteractions(customerRepository);
        }
    }

    @Nested
    @DisplayName("Testes para salvar um cliente")
    class Save {

        @Test
        @DisplayName("Deve salvar um novo cliente e retornar o DTO de resposta")
        void save_Success() {
            // Arrange
            CustomerRequestDTO requestDTO = new CustomerRequestDTO(
                    "Joao", "Silva", "joao@exemplo.com", "+5511999999999", "Rua abc, 123", BigDecimal.valueOf(100.00)
            );
            Customer customerToSave = Customer.builder()
                    .firstName("Joao")
                    .lastName("Silva")
                    .email("joao@exemplo.com")
                    .phoneNumber("+5511999999999")
                    .address("Rua abc, 123")
                    .credit(BigDecimal.valueOf(100.00))
                    .build();

            when(customerMapper.toEntity(requestDTO)).thenReturn(customerToSave);
            when(customerRepository.save(customerToSave)).thenReturn(customer);
            when(customerMapper.toResponse(customer)).thenReturn(customerResponseDTO);

            // Act
            CustomerResponseDTO result = customerService.save(requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(customerResponseDTO.id(), result.id());

            verify(customerMapper, times(1)).toEntity(requestDTO);
            verify(customerRepository, times(1)).save(customerToSave);
            verify(customerMapper, times(1)).toResponse(customer);
            verifyNoMoreInteractions(customerRepository, customerMapper);
        }
    }

    @Nested
    @DisplayName("Testes para deletar um cliente")
    class DeleteById {

        @Test
        @DisplayName("Deve deletar um cliente quando o ID existir")
        void deleteById_Success() {
            // Arrange
            Long id = 1L;
            when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

            // Act
            customerService.deleteById(id);

            // Assert
            verify(customerRepository, times(1)).findById(id);
            verify(customerRepository, times(1)).delete(customer);
            verifyNoMoreInteractions(customerRepository);
        }

        @Test
        @DisplayName("Deve lançar CustomerNotFoundException ao tentar deletar um cliente que não existe")
        void deleteById_Fail_CustomerNotFound() {
            // Arrange
            Long id = 99L;
            when(customerRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(id));

            verify(customerRepository, times(1)).findById(id);
            verify(customerRepository, never()).delete(any());
            verifyNoMoreInteractions(customerRepository);
        }
    }

    @Nested
    @DisplayName("Testes para buscar transações do cliente")
    class GetCustomerTransactions {

        @Test
        @DisplayName("Deve retornar uma página de transações do cliente no intervalo de datas")
        void getCustomerTransactions_Success() {
            // Arrange
            Long id = 1L;
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            Pageable pageable = PageRequest.of(0, 10);

            CustomerPayment payment = CustomerPayment.builder()
                    .id(1L)
                    .customer(customer)
                    .amountPaid(BigDecimal.valueOf(50.00))
                    .paymentMethod(PaymentMethod.CASH)
                    .paymentDate(OffsetDateTime.now())
                    .build();

            CustomerPaymentResponseDTO paymentResponseDTO = new CustomerPaymentResponseDTO(
                    1L, 1L, BigDecimal.valueOf(50.00), PaymentMethod.CASH.name(), OffsetDateTime.now(), "Notes"
            );

            Page<CustomerPayment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);

            when(customerPaymentRepository.findAllByCustomerIdAndPaymentDateBetween(
                    eq(id), any(OffsetDateTime.class), any(OffsetDateTime.class), eq(pageable))
            ).thenReturn(paymentPage);
            when(customerPaymentMapper.toResponse(payment)).thenReturn(paymentResponseDTO);

            // Act
            Page<CustomerPaymentResponseDTO> result = customerService.getCustomerTransactions(id, startDate, endDate, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(paymentResponseDTO.paymentId(), result.getContent().get(0).paymentId());

            verify(customerPaymentRepository, times(1)).findAllByCustomerIdAndPaymentDateBetween(
                    eq(id), any(OffsetDateTime.class), any(OffsetDateTime.class), eq(pageable));
            verify(customerPaymentMapper, times(1)).toResponse(payment);
            verifyNoMoreInteractions(customerPaymentRepository, customerPaymentMapper);
        }
    }

    @Nested
    @DisplayName("Testes para adicionar crédito")
    class AddCredit {

        private CustomerDepositRequestDTO depositRequestDTO;
        private CustomerPaymentRequestDTO paymentRequestDTO;
        private User adminUser;

        @BeforeEach
        void setUp() {
            paymentRequestDTO = new CustomerPaymentRequestDTO(
                    1L, BigDecimal.valueOf(50.00), PaymentMethod.CASH, "Depósito"
            );
            depositRequestDTO = new CustomerDepositRequestDTO(1L, paymentRequestDTO);
            adminUser = User.builder()
                    .id(1L)
                    .firstName("Admin")
                    .role(UserRole.ROLE_ADMIN)
                    .build();
        }

        @Test
        @DisplayName("Deve adicionar crédito ao cliente com sucesso quando o usuário for ADMIN ou MANAGER")
        void addCredit_Success() {
            // Arrange
            Long customerId = 1L;
            CustomerPayment customerPayment = CustomerPayment.builder()
                    .amountPaid(BigDecimal.valueOf(50.00))
                    .paymentMethod(PaymentMethod.CASH)
                    .build();

            CustomerPaymentResponseDTO expectedResponse = new CustomerPaymentResponseDTO(
                    1L, customerId, BigDecimal.valueOf(50.00), PaymentMethod.CASH.name(), OffsetDateTime.now(), "Notes"
            );

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(userService.findById(depositRequestDTO.userId())).thenReturn(adminUser);
            when(customerPaymentMapper.toEntity(paymentRequestDTO)).thenReturn(customerPayment);
            when(customerPaymentMapper.toResponse(customerPayment)).thenReturn(expectedResponse);

            BigDecimal initialCredit = customer.getCredit();

            // Act
            CustomerPaymentResponseDTO result = customerService.addCredit(customerId, depositRequestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(initialCredit.add(paymentRequestDTO.amountPaid()), customer.getCredit());
            assertEquals("Crédito adicionado pelo usuario Admin(id = 1).", customerPayment.getNotes());
            assertEquals(customer, customerPayment.getCustomer());

            verify(customerRepository, times(1)).findById(customerId);
            verify(userService, times(1)).findById(depositRequestDTO.userId());
            verify(customerPaymentMapper, times(1)).toEntity(paymentRequestDTO);
            verify(customerPaymentRepository, times(1)).save(customerPayment);
            verify(customerPaymentMapper, times(1)).toResponse(customerPayment);
            verifyNoMoreInteractions(customerRepository, userService, customerPaymentRepository, customerPaymentMapper);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o usuário tiver ROLE_USER")
        void addCredit_Fail_NoPermission() {
            // Arrange
            Long customerId = 1L;
            User regularUser = User.builder()
                    .id(2L)
                    .firstName("User")
                    .role(UserRole.ROLE_USER)
                    .build();

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            when(userService.findById(depositRequestDTO.userId())).thenReturn(regularUser);

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, 
                    () -> customerService.addCredit(customerId, depositRequestDTO));
            assertEquals("Usuário não pode fazer esta ação.", exception.getMessage());

            verify(customerRepository, times(1)).findById(customerId);
            verify(userService, times(1)).findById(depositRequestDTO.userId());
            verifyNoInteractions(customerPaymentMapper, customerPaymentRepository);
            verifyNoMoreInteractions(customerRepository, userService);
        }

        @Test
        @DisplayName("Deve lançar CustomerNotFoundException quando o cliente não existir")
        void addCredit_Fail_CustomerNotFound() {
            // Arrange
            Long customerId = 99L;
            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CustomerNotFoundException.class, 
                    () -> customerService.addCredit(customerId, depositRequestDTO));

            verify(customerRepository, times(1)).findById(customerId);
            verifyNoInteractions(userService, customerPaymentMapper, customerPaymentRepository);
            verifyNoMoreInteractions(customerRepository);
        }
    }
}
