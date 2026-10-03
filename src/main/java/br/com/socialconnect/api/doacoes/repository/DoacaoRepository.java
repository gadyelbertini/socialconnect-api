package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    // O nome do método define a query SQL (Between = >= e <=, And = condição extra)
    // O Pageable DEVE ser sempre o último parâmetro
    Page<Doacao> findByDataDoacaoBetweenAndTipo(
            LocalDate dataInicio,
            LocalDate dataFim,
            TipoDoacao tipo,
            Pageable pageable
    );
}