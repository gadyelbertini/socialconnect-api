package br.com.socialconnect.api.produtos.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.socialconnect.api.exception.ProdutoNomeDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.exception.EstoqueNegativoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;

@Service
public class ProdutoServiceImpl implements ProdutoService {
	private final ProdutoRepository produtoRepository;

	public ProdutoServiceImpl(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Override
	public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
		boolean temNome = nome != null && !nome.isBlank();
		Page<Produto> produtos;

		if (temNome && categoria != null) {
			produtos = produtoRepository.findByNomeContainingIgnoreCaseAndCategoria(nome.trim(), categoria, pageable);
		} else if (temNome) {
			produtos = produtoRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
		} else if (categoria != null) {
			produtos = produtoRepository.findByCategoria(categoria, pageable);
		} else {
			produtos = produtoRepository.findAll(pageable);
		}

		return produtos.map(this::toResponseDTO);
	}

	@Override
	public ProdutoResponseDTO buscarPorId(Long id) {
		return produtoRepository.findById(id)
				.map(this::toResponseDTO)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
	}

	@Override
	public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
		validarEstoque(dto);
		if (produtoRepository.existsByNome(dto.nome())) {
			throw new ProdutoNomeDuplicadoException(dto.nome());
		}

		Produto produto = Produto.builder()
				.nome(dto.nome())
				.categoria(dto.categoria())
				.estoqueAtual(dto.estoqueAtual())
				.estoqueMinimo(dto.estoqueMinimo())
				.unidadeMedida(dto.unidadeMedida())
				.dataCadastro(LocalDate.now())
				.build();

		return toResponseDTO(produtoRepository.save(produto));
	}

	@Override
	public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
		validarEstoque(dto);
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));

		if (produtoRepository.existsByNomeAndIdProdutoNot(dto.nome(), id)) {
			throw new ProdutoNomeDuplicadoException(dto.nome());
		}

		produto.setNome(dto.nome());
		produto.setCategoria(dto.categoria());
		produto.setEstoqueAtual(dto.estoqueAtual());
		produto.setEstoqueMinimo(dto.estoqueMinimo());
		produto.setUnidadeMedida(dto.unidadeMedida());

		return toResponseDTO(produtoRepository.save(produto));
	}

	@Override
	public void deletar(Long id) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
		produtoRepository.delete(produto);
	}

	private ProdutoResponseDTO toResponseDTO(Produto produto) {
		return new ProdutoResponseDTO(
				produto.getIdProduto(),
				produto.getNome(),
				produto.getCategoria(),
				produto.getEstoqueAtual(),
				produto.getEstoqueMinimo(),
				produto.getUnidadeMedida(),
				produto.getDataCadastro(),
				produto.getEstoqueAtual() < produto.getEstoqueMinimo());
	}

	private void validarEstoque(ProdutoRequestDTO dto) {
		if ((dto.estoqueAtual() != null && dto.estoqueAtual() < 0)
				|| (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0)) {
			throw new EstoqueNegativoException();
		}
	}
}
