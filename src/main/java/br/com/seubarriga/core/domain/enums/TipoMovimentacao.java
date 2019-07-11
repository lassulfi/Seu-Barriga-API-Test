package br.com.seubarriga.core.domain.enums;

public enum TipoMovimentacao {
	DESPESA("DESC"),
	RECEITA("REC");
	
	private String descricao;
	
	private TipoMovimentacao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return this.descricao;
	}
}