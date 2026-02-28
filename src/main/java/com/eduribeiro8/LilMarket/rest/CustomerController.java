package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
import com.eduribeiro8.LilMarket.dto.CustomerDepositRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerPaymentResponseDTO;
import com.eduribeiro8.LilMarket.dto.CustomerRequestDTO;
import com.eduribeiro8.LilMarket.dto.CustomerResponseDTO;
import com.eduribeiro8.LilMarket.entity.CustomerPayment;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Endpoints para gestão de clientes")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Busca um cliente por ID", description = "Retorna o cliente que possui o ID informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerResponseDTO> getCustomer(
            @Parameter(required = true, description = "ID do cliente", example = "1")
            @PathVariable Long customerId) {
        CustomerResponseDTO customer = customerService.findById(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/customer")
    @Operation(summary = "Lista todos os clientes", description = "Retorna a lista de todos os clientes cadastrados")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @PostMapping("/customer")
    @Operation(summary = "Cria um novo cliente", description = "Registra um novo cliente no sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Cliente já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerResponseDTO> saveCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um cliente",
                    required = true
            )
            @Valid @RequestBody CustomerRequestDTO theCustomer) {
        CustomerResponseDTO created = customerService.save(theCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/customer/{customerId}")
    @Operation(summary = "Atualiza um cliente existente", description = "Atualiza os dados de um cliente já cadastrado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @Parameter(required = true, description = "ID do cliente a ser atualizado", example = "1")
            @PathVariable Long customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do cliente",
                    required = true
            )
            @Valid @RequestBody CustomerRequestDTO theCustomer) {
        CustomerResponseDTO updated = customerService.save(theCustomer);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/customer/{customerId}")
    @Operation(summary = "Exclui um cliente", description = "Remove o cliente com o ID informado do sistema")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCustomerById(
            @Parameter(required = true, description = "ID do cliente a ser excluído", example = "1")
            @PathVariable Long customerId) {
        customerService.deleteById(customerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/customer/{customerId}/transactions")
    public ResponseEntity<Page<CustomerPaymentResponseDTO>> customerTransactions(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable){
        return ResponseEntity.ok(customerService.getCustomerTransactions(customerId, startDate, endDate, pageable));
    }

    @PostMapping("/customer/{customerId}/deposit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Realiza um depósito na conta do cliente", description = "Adiciona crédito ao saldo do cliente informado")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CustomerPaymentResponseDTO> addCredit(
            @Parameter(required = true, description = "ID do cliente", example = "1")
            @PathVariable Long customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do depósito",
                    required = true
            )
            @RequestBody @Valid CustomerDepositRequestDTO customerDepositRequestDTO){
        return ResponseEntity.ok(customerService.addCredit(customerId, customerDepositRequestDTO));
    }
}
