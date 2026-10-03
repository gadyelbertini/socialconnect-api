package br.com.socialconnect.api.produtos.dto;

import java.time.LocalDate;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta de um produto")
public record ProdutoResponseDTO(

    @Schema(
        description = "Identificador do produto",
        example = "1"
    )
    Long idProduto,

    @Schema(
        description = "Nome do produto",
        example = "Arroz Branco 5kg"
    )
    String nome,

    @Schema(
        description = "Categoria do produto",
        example = "ALIMENTO"
    )
    CategoriaProduto categoria,

    @Schema(
        description = "Quantidade atual em estoque",
        example = "25"
    )
    Integer estoqueAtual,

    @Schema(
        description = "Quantidade mínima de estoque",
        example = "10"
    )
    Integer estoqueMinimo,

    @Schema(
        description = "Unidade de medida",
        example = "KG"
    )
    String unidadeMedida,

    @Schema(
        description = "Data de cadastro",
        example = "2026-10-02"
    )
    LocalDate dataCadastro,

    @Schema(
        description = "Indica se o estoque está abaixo do estoque mínimo",
        example = "false"
    )
    boolean estoqueBaixo
) {}
