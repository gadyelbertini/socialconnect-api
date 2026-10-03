package br.com.socialconnect.api.produtos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	Page<Produto> findByCategoria(CategoriaProduto categoria, Pageable pageable);

	Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	Page<Produto> findByNomeContainingIgnoreCaseAndCategoria(
			String nome,
			CategoriaProduto categoria,
			Pageable pageable);

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdProdutoNot(String nome, Long idProduto);
}
