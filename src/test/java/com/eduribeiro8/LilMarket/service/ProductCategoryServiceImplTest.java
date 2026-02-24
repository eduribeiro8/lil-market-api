package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductCategoryRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductCategoryResponseDTO;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import com.eduribeiro8.LilMarket.mapper.ProductCategoryMapper;
import com.eduribeiro8.LilMarket.repository.ProductCategoryRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateProductCategoryException;
import com.eduribeiro8.LilMarket.rest.exception.ProductCategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCategoryService Unit Tests")
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @InjectMocks
    private ProductCategoryServiceImpl productCategoryService;

    private ProductCategoryRequestDTO requestDTO;
    private ProductCategory category;

    @BeforeEach
    void setUp() {
        requestDTO = new ProductCategoryRequestDTO("Bebidas", "Sucos e refrigerantes");
        category = ProductCategory.builder()
                .id(1)
                .name("BEBIDAS")
                .description("Sucos e refrigerantes")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Save Category Tests")
    class SaveCategory {

        @Test
        @DisplayName("Deve salvar uma nova categoria e retornar o DTO de resposta quando os dados forem válidos")
        void save_Success() {
            // Arrange
            ProductCategory productCategoryToSave = ProductCategory.builder()
                    .name("Bebidas")
                    .description("Sucos e refrigerantes")
                    .build();

            ProductCategoryResponseDTO responseDTO = new ProductCategoryResponseDTO(
                    1, "BEBIDAS", "Sucos e refrigerantes", OffsetDateTime.now(), OffsetDateTime.now());

            Mockito.when(productCategoryRepository.existsByName(requestDTO.name())).thenReturn(false);
            Mockito.when(productCategoryMapper.toEntity(requestDTO)).thenReturn(productCategoryToSave);
            Mockito.when(productCategoryRepository.save(Mockito.any(ProductCategory.class))).thenReturn(category);
            Mockito.when(productCategoryMapper.toResponse(category)).thenReturn(responseDTO);

            // Act
            ProductCategoryResponseDTO result = productCategoryService.save(requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(responseDTO.id(), result.id());
            assertEquals(responseDTO.name(), result.name());
            assertEquals(responseDTO.description(), result.description());

            Mockito.verify(productCategoryRepository).existsByName(requestDTO.name());
            Mockito.verify(productCategoryRepository).save(Mockito.any(ProductCategory.class));
            Mockito.verify(productCategoryMapper).toEntity(requestDTO);
            Mockito.verify(productCategoryMapper).toResponse(category);
            Mockito.verifyNoMoreInteractions(productCategoryRepository, productCategoryMapper);
        }

        @Test
        @DisplayName("Deve lançar DuplicateProductCategoryException quando o nome da categoria já estiver cadastrado")
        void save_Fail_DuplicateName() {
            // Arrange
            Mockito.when(productCategoryRepository.existsByName(requestDTO.name())).thenReturn(true);

            // Act & Assert
            DuplicateProductCategoryException exception = assertThrows(DuplicateProductCategoryException.class,
                    () -> productCategoryService.save(requestDTO));

            assertTrue(exception.getMessage().contains("already registered"));

            Mockito.verify(productCategoryRepository).existsByName(requestDTO.name());
            Mockito.verify(productCategoryRepository, Mockito.never()).save(Mockito.any());
            Mockito.verifyNoInteractions(productCategoryMapper);
        }
    }

    @Nested
    @DisplayName("Find Category Tests")
    class FindCategory {

        @Test
        @DisplayName("Deve retornar ProductCategoryResponseDTO ao buscar por um id existente")
        void findById_Success() {
            // Arrange
            int id = 1;
            ProductCategoryResponseDTO responseDTO = new ProductCategoryResponseDTO(
                    1, "BEBIDAS", "Sucos e refrigerantes", OffsetDateTime.now(), OffsetDateTime.now());

            Mockito.when(productCategoryRepository.findById(id)).thenReturn(Optional.of(category));
            Mockito.when(productCategoryMapper.toResponse(category)).thenReturn(responseDTO);

            // Act
            ProductCategoryResponseDTO result = productCategoryService.findById(id);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.id());
            assertEquals("BEBIDAS", result.name());

            Mockito.verify(productCategoryRepository).findById(id);
            Mockito.verify(productCategoryMapper).toResponse(category);
            Mockito.verifyNoMoreInteractions(productCategoryRepository, productCategoryMapper);
        }

        @Test
        @DisplayName("Deve lançar ProductCategoryNotFoundException ao buscar por um id não cadastrado")
        void findById_Fail_NotFound() {
            // Arrange
            int id = 99;
            Mockito.when(productCategoryRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            ProductCategoryNotFoundException exception = assertThrows(ProductCategoryNotFoundException.class,
                    () -> productCategoryService.findById(id));

            assertTrue(exception.getMessage().contains("not found"));

            Mockito.verify(productCategoryRepository).findById(id);
            Mockito.verifyNoMoreInteractions(productCategoryRepository);
            Mockito.verifyNoInteractions(productCategoryMapper);
        }

        @Test
        @DisplayName("Deve retornar ProductCategoryResponseDTO ao buscar por um nome existente")
        void findByName_Success() {
            // Arrange
            String name = "BEBIDAS";
            ProductCategoryResponseDTO responseDTO = new ProductCategoryResponseDTO(
                    1, "BEBIDAS", "Sucos e refrigerantes", OffsetDateTime.now(), OffsetDateTime.now());

            Mockito.when(productCategoryRepository.findByName(name)).thenReturn(Optional.of(category));
            Mockito.when(productCategoryMapper.toResponse(category)).thenReturn(responseDTO);

            // Act
            ProductCategoryResponseDTO result = productCategoryService.findByName(name);

            // Assert
            assertNotNull(result);
            assertEquals(name, result.name());

            Mockito.verify(productCategoryRepository).findByName(name);
            Mockito.verify(productCategoryMapper).toResponse(category);
            Mockito.verifyNoMoreInteractions(productCategoryRepository, productCategoryMapper);
        }

        @Test
        @DisplayName("Deve lançar ProductCategoryNotFoundException ao buscar por um nome não cadastrado")
        void findByName_Fail_NotFound() {
            // Arrange
            String name = "Inexistente";
            Mockito.when(productCategoryRepository.findByName(name)).thenReturn(Optional.empty());

            // Act & Assert
            ProductCategoryNotFoundException exception = assertThrows(ProductCategoryNotFoundException.class,
                    () -> productCategoryService.findByName(name));

            assertTrue(exception.getMessage().contains("not found"));

            Mockito.verify(productCategoryRepository).findByName(name);
            Mockito.verifyNoMoreInteractions(productCategoryRepository);
            Mockito.verifyNoInteractions(productCategoryMapper);
        }
    }

    @Nested
    @DisplayName("List Category Tests")
    class ListCategory {

        @Test
        @DisplayName("Deve retornar uma lista de ProductCategoryResponseDTO")
        void getAll_Success() {
            // Arrange
            List<ProductCategory> categoryList = List.of(category);
            ProductCategoryResponseDTO responseDTO = new ProductCategoryResponseDTO(
                    1, "BEBIDAS", "Sucos e refrigerantes", OffsetDateTime.now(), OffsetDateTime.now());
            List<ProductCategoryResponseDTO> responseList = List.of(responseDTO);

            Mockito.when(productCategoryRepository.findAll()).thenReturn(categoryList);
            Mockito.when(productCategoryMapper.toResponseList(categoryList)).thenReturn(responseList);

            // Act
            List<ProductCategoryResponseDTO> result = productCategoryService.getAll();

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(responseDTO.name(), result.get(0).name());

            Mockito.verify(productCategoryRepository).findAll();
            Mockito.verify(productCategoryMapper).toResponseList(categoryList);
            Mockito.verifyNoMoreInteractions(productCategoryRepository, productCategoryMapper);
        }
    }
}
