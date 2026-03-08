package com.eduribeiro8.LilMarket.service;

import com.eduribeiro8.LilMarket.dto.ProductRequestDTO;
import com.eduribeiro8.LilMarket.dto.ProductResponseDTO;
import com.eduribeiro8.LilMarket.entity.Product;
import com.eduribeiro8.LilMarket.entity.ProductCategory;
import com.eduribeiro8.LilMarket.entity.UnitType;
import com.eduribeiro8.LilMarket.mapper.ProductMapper;
import com.eduribeiro8.LilMarket.repository.BatchRepository;
import com.eduribeiro8.LilMarket.repository.ProductCategoryRepository;
import com.eduribeiro8.LilMarket.repository.ProductRepository;
import com.eduribeiro8.LilMarket.rest.exception.DuplicateBarcodeException;
import com.eduribeiro8.LilMarket.rest.exception.ProductCategoryNotFoundException;
import com.eduribeiro8.LilMarket.rest.exception.ProductNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductCategory category;
    private Product product;
    private ProductRequestDTO requestDTO;
    private ProductResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        category = ProductCategory.builder()
                .id(1L)
                .name("categoria1")
                .description("abc123")
                .build();

        product = Product.builder()
                .id(1L)
                .name("produto1")
                .barcode("1234567890123")
                .description("abc123")
                .price(new BigDecimal("10.00"))
                .averagePrice(BigDecimal.ZERO)
                .profitMargin(new BigDecimal("50.00"))
                .autoPricing(true)
                .minQuantityInStock(5)
                .productCategory(category)
                .unitType(UnitType.COUNT)
                .isPerishable(false)
                .alert(true)
                .totalQuantity(BigDecimal.TEN)
                .build();

        requestDTO = new ProductRequestDTO(
                "produto1",
                "1234567890123",
                "abc123",
                new BigDecimal("10.00"),
                true,
                new BigDecimal("50.00"),
                5,
                1L,
                UnitType.COUNT,
                false,
                true
        );

        responseDTO = new ProductResponseDTO(
                1L,
                "produto1",
                "1234567890123",
                "abc123",
                new BigDecimal("10.00"),
                BigDecimal.ZERO,
                BigDecimal.TEN,
                true,
                new BigDecimal("50.00"),
                5,
                1L,
                UnitType.COUNT,
                "categoria1",
                false,
                true
        );
    }

    @Nested
    @DisplayName("Testes para salvar um Produto")
    class SaveProduct {

        @Test
        @DisplayName("Deve salvar um produto com sucesso")
        void save_Success() {
            //Arrange
            when(productRepository.existsByBarcode(anyString())).thenReturn(false);
            when(productCategoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(productMapper.toEntity(any(ProductRequestDTO.class))).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toResponse(any(Product.class))).thenReturn(responseDTO);

            //Act
            ProductResponseDTO result = productService.save(requestDTO);

            //Assert
            assertNotNull(result);
            assertEquals(responseDTO.barcode(), result.barcode());
            verify(productRepository).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, productCategoryRepository, productMapper);
        }

        @Test
        @DisplayName("Deve lançar DuplicateBarcodeException quando o código de barras já existe")
        void save_Fail_DuplicateBarcode() {
            //Arrange
            when(productRepository.existsByBarcode(anyString())).thenReturn(true);

            //Act & Assert
            assertThrows(DuplicateBarcodeException.class, () -> productService.save(requestDTO));
            verify(productRepository, never()).save(any(Product.class));
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Deve lançar ProductCategoryNotFoundException quando a categoria não existe")
        void save_Fail_CategoryNotFound() {
            //Arrange
            when(productRepository.existsByBarcode(anyString())).thenReturn(false);
            when(productCategoryRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductCategoryNotFoundException.class, () -> productService.save(requestDTO));
            verify(productRepository, never()).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, productCategoryRepository);
        }
    }

    @Nested
    @DisplayName("Testes para buscar Produtos")
    class FindProduct {

        @Test
        @DisplayName("Deve retornar todos os produtos paginados")
        void getAllProducts_Success() {
            //Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product));

            when(productRepository.findAll(pageable)).thenReturn(productPage);
            when(productMapper.toResponse(any(Product.class))).thenReturn(responseDTO);

            //Act
            Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

            //Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(productRepository).findAll(pageable);
            verifyNoMoreInteractions(productRepository, productMapper);
        }

        @Test
        @DisplayName("Deve retornar ProductResponseDTO por ID")
        void findProductByIdDTO_Success() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(productMapper.toResponse(product)).thenReturn(responseDTO);

            //Act
            ProductResponseDTO result = productService.findProductByIdDTO(1L);

            //Assert
            assertNotNull(result);
            assertEquals(1, result.id());
            verify(productRepository).findById(1L);
            verifyNoMoreInteractions(productRepository, productMapper);
        }

        @Test
        @DisplayName("Deve lançar ProductNotFoundException ao buscar DTO por ID inexistente")
        void findProductByIdDTO_Fail_ProductNotFound() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductNotFoundException.class, () -> productService.findProductByIdDTO(1L));
            verify(productRepository).findById(1L);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Deve retornar Entidade Product por ID")
        void findProductById_Success() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

            //Act
            Product result = productService.findProductById(1L);

            //Assert
            assertNotNull(result);
            assertEquals(1, result.getId());
            verify(productRepository).findById(1L);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Deve retornar ProductResponseDTO por código de barras")
        void findProductByBarcode_Success() {
            //Arrange
            when(productRepository.findByBarcode(anyString())).thenReturn(Optional.of(product));
            when(productMapper.toResponse(product)).thenReturn(responseDTO);

            //Act
            ProductResponseDTO result = productService.findProductByBarcode("1234567890123");

            //Assert
            assertNotNull(result);
            assertEquals("1234567890123", result.barcode());
            verify(productRepository).findByBarcode("1234567890123");
            verifyNoMoreInteractions(productRepository, productMapper);
        }

        @Test
        @DisplayName("Deve lançar ProductNotFoundException ao buscar código de barras inexistente")
        void findProductByBarcode_Fail_ProductNotFound() {
            //Arrange
            when(productRepository.findByBarcode(anyString())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductNotFoundException.class, () -> productService.findProductByBarcode("1234567890123"));
            verify(productRepository).findByBarcode("1234567890123");
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Deve lançar ProductNotFoundException ao buscar entidade por ID inexistente")
        void findProductById_Fail_ProductNotFound() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductNotFoundException.class, () -> productService.findProductById(1L));
            verify(productRepository).findById(1L);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    @DisplayName("Testes para atualizar Produto")
    class UpdateProduct {

        @Test
        @DisplayName("Deve atualizar um produto com sucesso sem mudar categoria")
        void updateProduct_Success() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(productMapper.updateEntityFromDTO(any(ProductRequestDTO.class), any(Product.class))).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toResponse(any(Product.class))).thenReturn(responseDTO);

            //Act
            ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

            //Assert
            assertNotNull(result);
            verify(productCategoryRepository, never()).findById(anyLong());
            verify(productRepository).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, productCategoryRepository, productMapper);
        }

        @Test
        @DisplayName("Deve atualizar um produto mudando a categoria")
        void updateProduct_Success_WithCategoryChange() {
            //Arrange
            ProductRequestDTO updatedRequest = new ProductRequestDTO(
                    "produto1", "1234567890123", "Desc", BigDecimal.TEN, true,
                    BigDecimal.TEN, 5, 2L, UnitType.COUNT, false, true
            );

            ProductCategory newCategory = ProductCategory.builder().id(2L).name("New Category").build();

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(productCategoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
            when(productMapper.updateEntityFromDTO(any(ProductRequestDTO.class), any(Product.class))).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toResponse(any(Product.class))).thenReturn(responseDTO);

            //Act
            productService.updateProduct(1L, updatedRequest);

            //Assert
            assertEquals(newCategory, product.getProductCategory());
            verify(productCategoryRepository).findById(2L);
            verify(productRepository).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, productCategoryRepository, productMapper);
        }
    }

    @Nested
    @DisplayName("Testes para deletar Produto")
    class DeleteProduct {

        @Test
        @DisplayName("Deve deletar um produto com sucesso")
        void deleteById_Success() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

            //Act
            productService.deleteById(1L);

            //Assert
            verify(productRepository).delete(product);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        @DisplayName("Deve lançar ProductNotFoundException ao deletar produto inexistente")
        void deleteById_Fail_ProductNotFound() {
            //Arrange
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(ProductNotFoundException.class, () -> productService.deleteById(1L));
            verify(productRepository, never()).delete(any(Product.class));
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    @DisplayName("Testes para precificação automática")
    class AutoPricing {

        @Test
        @DisplayName("Deve calcular novo preço baseado no custo médio")
        void calculatePriceBasedOnStock_Success() {
            //Arrange
            product.setAutoPricing(true);
            product.setProfitMargin(new BigDecimal("50.00")); // 50%
            BigDecimal averageCost = new BigDecimal("100.00");

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(batchRepository.calculateAverageCostByProduct(anyLong())).thenReturn(averageCost);

            //Act
            productService.calculatePriceBasedOnStock(1L);

            //Assert
            // Cost 100 + 50% profit = 150
            assertEquals(0, new BigDecimal("150.00").compareTo(product.getPrice()));
            assertEquals(0, new BigDecimal("100.00").compareTo(product.getAveragePrice()));
            verify(productRepository).save(product);
            verifyNoMoreInteractions(productRepository, batchRepository);
        }

        @Test
        @DisplayName("Não deve atualizar preço se autoPricing estiver desativado")
        void calculatePriceBasedOnStock_AutoPricingDisabled() {
            //Arrange
            product.setAutoPricing(false);
            BigDecimal averageCost = new BigDecimal("100.00");

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(batchRepository.calculateAverageCostByProduct(anyLong())).thenReturn(averageCost);

            //Act
            productService.calculatePriceBasedOnStock(1L);

            //Assert
            assertEquals(0, new BigDecimal("100.00").compareTo(product.getAveragePrice()));
            verify(batchRepository).calculateAverageCostByProduct(anyLong());
            verify(productRepository).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, batchRepository);
        }

        @Test
        @DisplayName("Não deve atualizar preço se custo médio for nulo (estoque vazio)")
        void calculatePriceBasedOnStock_EmptyStock() {
            //Arrange
            product.setAutoPricing(true);

            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(batchRepository.calculateAverageCostByProduct(anyLong())).thenReturn(null);

            //Act
            productService.calculatePriceBasedOnStock(1L);

            //Assert
            verify(productRepository, never()).save(any(Product.class));
            verifyNoMoreInteractions(productRepository, batchRepository);
        }
    }

    @Nested
    @DisplayName("Testes para simulação e preço manual")
    class SimulationAndManualPrice {

        @Test
        @DisplayName("Deve simular preço e custo com sucesso")
        void simulatePricing_Success() {
            // Arrange
            product.setTotalQuantity(new BigDecimal("10.00"));
            product.setAveragePrice(new BigDecimal("5.00"));
            product.setPrice(new BigDecimal("10.00"));
            product.setAutoPricing(true);
            product.setProfitMargin(new BigDecimal("100.00")); // 100% lucro

            BigDecimal newQuantity = new BigDecimal("10.00");
            BigDecimal newPurchasePrice = new BigDecimal("15.00");

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            var result = productService.simulatePricing(1L, newQuantity, newPurchasePrice);

            // Assert
            // (10 * 5 + 10 * 15) / 20 = (50 + 150) / 20 = 200 / 20 = 10.00 (Novo Custo Médio)
            // 10.00 cost + 100% margin = 20.00 (Novo Preço de Venda)
            assertEquals(0, new BigDecimal("10.00").compareTo(result.simulatedAverageCost()));
            assertEquals(0, new BigDecimal("20.00").compareTo(result.simulatedSellingPrice()));
            assertEquals(0, new BigDecimal("5.00").compareTo(result.currentAverageCost()));
            assertEquals(0, new BigDecimal("10.00").compareTo(result.currentSellingPrice()));
        }

        @Test
        @DisplayName("Deve atualizar preço manual do produto")
        void updatePrice_Success() {
            // Arrange
            BigDecimal newPrice = new BigDecimal("25.00");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            // Act
            productService.updatePrice(1L, newPrice);

            // Assert
            assertEquals(0, new BigDecimal("25.00").compareTo(product.getPrice()));
            verify(productRepository).save(product);
        }
    }
}
