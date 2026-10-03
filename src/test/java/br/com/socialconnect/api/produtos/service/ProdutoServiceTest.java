package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.exception.ProdutoNomeDuplicadoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
	@Mock
	private ProdutoRepository produtoRepository;

	@InjectMocks
	private ProdutoServiceImpl produtoService;

	@Test
	void deveCriarProdutoQuandoDadosValidos() {
		// Arrange
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");
		Produto salvo = Produto.builder()
				.idProduto(1L)
				.nome(dto.nome())
				.categoria(dto.categoria())
				.estoqueAtual(dto.estoqueAtual())
				.estoqueMinimo(dto.estoqueMinimo())
				.unidadeMedida(dto.unidadeMedida())
				.dataCadastro(LocalDate.now())
				.build();
		when(produtoRepository.existsByNome(dto.nome())).thenReturn(false);
		when(produtoRepository.save(any(Produto.class))).thenReturn(salvo);

		// Act
		ProdutoResponseDTO resposta = produtoService.criar(dto);

		// Assert
		assertThat(resposta.idProduto()).isEqualTo(1L);
		assertThat(resposta.estoqueBaixo()).isTrue();
		assertThat(resposta.unidadeMedida()).isEqualTo("unidade");
		verify(produtoRepository).save(any(Produto.class));
	}

	@Test
	void deveLancarExcecaoQuandoEstoqueNegativo() {
		// Arrange
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, -1, 10, "unidade");

		// Act / Assert
		assertThatThrownBy(() -> produtoService.criar(dto))
				.isInstanceOf(EstoqueNegativoException.class);
	}

	@Test
	void deveLancarExcecaoQuandoNomeDuplicado() {
		// Arrange
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");
		when(produtoRepository.existsByNome(dto.nome())).thenReturn(true);

		// Act / Assert
		assertThatThrownBy(() -> produtoService.criar(dto))
				.isInstanceOf(ProdutoNomeDuplicadoException.class);
	}
}
