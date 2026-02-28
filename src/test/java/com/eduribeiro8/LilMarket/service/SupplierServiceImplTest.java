package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.SupplierRequestDTO;
import com.eduribeiro8.LilMarket.dto.SupplierResponseDTO;
import com.eduribeiro8.LilMarket.entity.Supplier;
import com.eduribeiro8.LilMarket.mapper.SupplierMapper;
import com.eduribeiro8.LilMarket.repository.SupplierRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateSupplierException;
import com.eduribeiro8.LilMarket.rest.exception.SupplierNotFoundException;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupplierService Unit Tests")
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierServiceImpl supplierService;

    private SupplierRequestDTO requestDTO;
    private Supplier supplierFound;

    @Nested
    @DisplayName("Create Supplier Tests")
    class createSupplier {

        @BeforeEach
        void setUp(){
            requestDTO = new SupplierRequestDTO(
                    "abc",
                    "123456789",
                    "rua abc",
                    "def",
                    "sao paulo"
            );


        }


        @Test
        @DisplayName("Deve salvar um novo fornecedor e retornar o DTO de resposta quando os dados forem válidos")
        void saveNewSupplier_Success() {
            //Arrange
            Supplier supplierToSave = Supplier.builder()
                    .name("abc")
                    .phoneNumber("123456789")
                    .address("rua abc")
                    .district("def")
                    .city("sao paulo")
                    .build();

            Supplier supplierPersisted = Supplier.builder()
                    .id(1L)
                    .name("abc")
                    .phoneNumber("123456789")
                    .address("rua abc")
                    .district("def")
                    .city("sao paulo")
                    .build();

            SupplierResponseDTO responseDTO = new SupplierResponseDTO(
                    1L,
                    "abc",
                    "123456789",
                    "rua abc",
                    "def",
                    "sao paulo",
                    OffsetDateTime.now()
            );

            Mockito.when(supplierRepository.findByName("abc")).thenReturn(null);
            Mockito.when(supplierMapper.toEntity(requestDTO)).thenReturn(supplierToSave);
            Mockito.when(supplierRepository.save(supplierToSave)).thenReturn(supplierPersisted);
            Mockito.when(supplierMapper.toResponse(supplierPersisted)).thenReturn(responseDTO);

            //Act
            SupplierResponseDTO response = supplierService.save(requestDTO);

            //Assert
            assertNotNull(response);
            assertEquals(response.id(), responseDTO.id());
            assertEquals(response.name(), responseDTO.name());
            assertEquals(response.city(), responseDTO.city());
            assertNotNull(response.createdAt());

            Mockito.verify(supplierRepository).findByName(requestDTO.name());
            Mockito.verify(supplierRepository, Mockito.times(1)).save(Mockito.any(Supplier.class));
            Mockito.verifyNoMoreInteractions(supplierRepository, supplierMapper);
        }

        @Test
        @DisplayName("Deve lançar DuplicateSupplierNameException quando o nome do fornecedor já estiver cadastrado")
        void saveNewSupplier_Fail_DuplicateSupplier(){
            //Arrange
            Supplier supplierExisting = Supplier.builder()
                    .name("abc")
                    .phoneNumber("123456789")
                    .address("rua abc")
                    .district("def")
                    .city("sao paulo")
                    .build();

            Mockito.when(supplierRepository.findByName(requestDTO.name())).thenReturn(supplierExisting);

            //Act & Assert
            DuplicateSupplierException exception = assertThrows(DuplicateSupplierException.class, () ->
                    supplierService.save(requestDTO)
                );

            assertTrue(exception.getMessage().contains("Fornecedor já cadastrado"));

            Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any());
            Mockito.verifyNoInteractions(supplierMapper);
        }

    }

    @Nested
    @DisplayName("Find Supplier Tests")
    class findSupplier{

        @BeforeEach
        void setUp(){
            supplierFound = Supplier.builder()
                    .name("abc")
                    .phoneNumber("123456789")
                    .address("rua abc")
                    .district("def")
                    .city("sao paulo")
                    .build();
        }

        @Test
        @DisplayName("Deve retornar SupplierResponseDTO ao buscar por um id existente")
        void findByIdDTO_Success() {
            //Arrange
            Long id = 1L;

            SupplierResponseDTO responseDTO = new SupplierResponseDTO(
                    1L,
                    "abc",
                    "123456789",
                    "rua abc",
                    "def",
                    "sao paulo",
                    OffsetDateTime.now()
            );

            Mockito.when(supplierRepository.findById(id)).thenReturn(Optional.of(supplierFound));
            Mockito.when(supplierMapper.toResponse(supplierFound)).thenReturn(responseDTO);

            //Act
            SupplierResponseDTO responseDTO1 = supplierService.findByIdDTO(id);

            //Assert
            assertNotNull(responseDTO1);
            assertEquals(responseDTO.id(), responseDTO1.id());
            assertEquals(responseDTO.name(), responseDTO1.name());
            assertEquals(responseDTO.district(), responseDTO1.district());

            Mockito.verify(supplierRepository, Mockito.times(1)).findById(Mockito.any(Long.class));
            Mockito.verifyNoMoreInteractions(supplierRepository, supplierMapper);
        }

        @Test
        @DisplayName("Deve lançar SupplierNotFound ao buscar por um id não cadastrado")
        void findByIdDTO_Fail_SupplierNotFound(){
            //Arrange
            Long id = 1L;

            Mockito.when(supplierRepository.findById(id)).thenReturn(Optional.empty());

            //Act

            //Assert
            SupplierNotFoundException exception = assertThrows(SupplierNotFoundException.class, () ->
                supplierService.findByIdDTO(id)
            );
            assertTrue(exception.getMessage().contains("não encontrado"));

            Mockito.verify(supplierRepository).findById(id);
            Mockito.verifyNoMoreInteractions(supplierRepository);
            Mockito.verifyNoInteractions(supplierMapper);
        }

        @Test
        @DisplayName("Deve retornar Supplier ao buscar por um id existente")
        void findById_Success() {
            //Arrange
            Long id = 1L;

            Mockito.when(supplierRepository.findById(id)).thenReturn(Optional.of(supplierFound));

            //Act
            Supplier response = supplierService.findById(id);

            //Assert
            assertNotNull(response);
            assertEquals(response.getId(), supplierFound.getId());
            assertEquals(response.getName(), supplierFound.getName());
            assertEquals(response.getDistrict(), supplierFound.getDistrict());

            Mockito.verify(supplierRepository).findById(Mockito.any());
            Mockito.verifyNoMoreInteractions(supplierRepository, supplierMapper);
        }

        @Test
        @DisplayName("Deve lançar SupplierNotFound ao buscar por um id não cadastrado")
        void findById_Fail_SupplierNotFound(){
            //Arrange
            Long id = 1L;

            Mockito.when(supplierRepository.findById(id)).thenReturn(Optional.empty());

            //Act & Assert
            SupplierNotFoundException exception = assertThrows(SupplierNotFoundException.class, () ->
                supplierService.findById(id)
            );
            assertTrue(exception.getMessage().contains("não encontrado"));

            Mockito.verify(supplierRepository).findById(id);
            Mockito.verifyNoMoreInteractions(supplierRepository);
            Mockito.verifyNoInteractions(supplierMapper);
        }

        @Test
        @DisplayName("Deve retornar um Page de SupplierResponseDTO ao buscar com um Pageable")
        void getAll_Success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Supplier> suppliers = List.of(supplierFound);
            Page<Supplier> supplierPage = new PageImpl<>(suppliers, pageable, suppliers.size());

            SupplierResponseDTO responseDTO = new SupplierResponseDTO(
                    1L,
                    "abc",
                    "123456789",
                    "rua abc",
                    "def",
                    "sao paulo",
                    OffsetDateTime.now()
            );

            Mockito.when(supplierRepository.findAll(pageable)).thenReturn(supplierPage);
            Mockito.when(supplierMapper.toResponse(supplierFound)).thenReturn(responseDTO);

            //Act
            Page<SupplierResponseDTO> result = supplierService.getAll(pageable);

            //Assert
            assertNotNull(result);

            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());
            assertEquals(responseDTO.id(), result.getContent().get(0).id());
            assertEquals(responseDTO.name(), result.getContent().get(0).name());
            assertEquals(responseDTO.district(), result.getContent().get(0).district());
            Mockito.verify(supplierRepository).findAll(pageable);
        }
    }
}