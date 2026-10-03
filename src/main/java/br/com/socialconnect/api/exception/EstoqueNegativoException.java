package br.com.socialconnect.api.exception;

public class EstoqueNegativoException extends RuntimeException {
	public EstoqueNegativoException() {
		super("O estoque atual e o estoque mínimo não podem ser negativos.");
	}
}
