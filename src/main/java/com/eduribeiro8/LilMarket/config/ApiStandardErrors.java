package com.eduribeiro8.LilMarket.config;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                        content = @Content),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão para esta operação",
                        content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                        content = @Content)
})
public @interface ApiStandardErrors {
}
