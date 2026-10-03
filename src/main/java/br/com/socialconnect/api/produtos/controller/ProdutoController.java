package br.com.socialconnect.api.produtos.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
@Tag(
    name = "Produtos",
    description = "Operações de gerenciamento de produtos"
)
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(
        summary = "Lista produtos",
        description = "Retorna uma lista paginada de produtos. "
                    + "Permite filtrar por nome parcial e categoria."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Produtos encontrados"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetros inválidos"
        )
    })
    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @RequestParam(required = false) String nome,

            @RequestParam(required = false)
            CategoriaProduto categoria,

            @PageableDefault(size = 10)
            Pageable pageable) {

        return ResponseEntity.ok(
            produtoService.listar(nome, categoria, pageable)
        );
    }

    @Operation(
        summary = "Busca produto por ID",
        description = "Retorna os dados de um produto pelo seu identificador."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Produto encontrado"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado"
        )
    })
    @GetMapping("/{id_produto}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @PathVariable("id_produto") Long idProduto) {

        return ResponseEntity.ok(
            produtoService.buscarPorId(idProduto)
        );
    }

    @Operation(
        summary = "Cria um produto",
        description = "Cadastra um novo produto."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Produto criado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Produto com nome já cadastrado"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Estoque inválido"
        )
    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO produto = produtoService.criar(dto);

        URI location = URI.create(
            "/api/v1/produtos/" + produto.idProduto()
        );

        return ResponseEntity
                .created(location)
                .body(produto);
    }

    @Operation(
        summary = "Atualiza um produto",
        description = "Atualização total dos dados de um produto."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Produto atualizado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Produto com nome já cadastrado"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Estoque inválido"
        )
    })
    @PutMapping("/{id_produto}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable("id_produto") Long idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.ok(
            produtoService.atualizar(idProduto, dto)
        );
    }

    @Operation(
        summary = "Remove um produto",
        description = "Exclui um produto pelo seu identificador."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Produto removido com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado"
        )
    })
    @DeleteMapping("/{id_produto}")
    public ResponseEntity<Void> deletar(
            @PathVariable("id_produto") Long idProduto) {

        produtoService.deletar(idProduto);

        return ResponseEntity.noContent().build();
    }
}
