package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
class ProdutoControllerIntegrationTest {
	@Container
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

	@DynamicPropertySource
	static void configurarBanco(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
		registry.add("spring.flyway.enabled", () -> true);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProdutoRepository produtoRepository;

	@BeforeEach
	void limparProdutos() {
		produtoRepository.deleteAll();
	}

	@Test
	void deveCriarProdutoQuandoDadosValidos() {
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");

		ResponseEntity<String> resposta = restTemplate.postForEntity(
				"/api/v1/produtos", dto, String.class);

		assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(resposta.getHeaders().getLocation()).isNotNull();
		assertThat(resposta.getBody()).contains("\"estoqueBaixo\":true");
	}

	@Test
	void deveRetornar409QuandoNomeDuplicado() {
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, 3, 10, "unidade");
		restTemplate.postForEntity("/api/v1/produtos", dto, String.class);

		ResponseEntity<String> resposta = restTemplate.postForEntity(
				"/api/v1/produtos", dto, String.class);

		assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(resposta.getBody()).contains("\"status\":409");
	}

	@Test
	void deveRetornar422QuandoEstoqueNegativo() {
		ProdutoRequestDTO dto = new ProdutoRequestDTO(
				"Arroz 5kg", CategoriaProduto.ALIMENTO, -1, 10, "unidade");

		ResponseEntity<String> resposta = restTemplate.postForEntity(
				"/api/v1/produtos", dto, String.class);

		assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(resposta.getBody()).contains("\"status\":422");
	}
}
