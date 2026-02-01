package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.rest.exception.ErrorResponse;
import com.eduribeiro8.LilMarket.service.UserService;
import com.eduribeiro8.LilMarket.config.ApiStandardErrors;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gestão de usuários")
@SecurityRequirement(name = "basicScheme")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Cria um novo usuário",
            description = "Registra um novo usuário no sistema.")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou sintaxe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Usuário já existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/user")
    public UserResponseDTO save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um usuário.",
                    required = true
            )
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.save(userRequestDTO);
    }

    @Operation(summary = "Busca usuário por username",
            description = "Retorna o usuário com o username informado.")
    @ApiStandardErrors
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/username/{username}")
    public UserResponseDTO getUserByUsername(
            @Parameter(required = true, description = "Nome de usuário", example = "john.doe")
            @PathVariable String username) {
        return userService.findUserByUsernameDTO(username);
    }
}
