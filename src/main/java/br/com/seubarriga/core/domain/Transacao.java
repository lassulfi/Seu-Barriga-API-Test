package br.com.seubarriga.core.domain;

import br.com.seubarriga.core.domain.enums.TipoMovimentacao;

public class Transacao {

	private Integer id;
	private Integer conta_id;
	private Integer usuario_id;
	private String descricao;
	private String envolvido;
	private String tipo;
	private String data_transacao;
	private String data_pagamento;
	private Float valor;
	private boolean status;
	
	public Transacao() {
		
	}
	
	public Transacao(Integer conta_id, String descricao, String envolvido, TipoMovimentacao tipo,
			String data_transacao, String data_pagamento, Float valor, boolean status) {
		super();
		this.conta_id = conta_id;
		this.descricao = descricao;
		this.envolvido = envolvido;
		this.tipo = tipo.getDescricao();
		this.data_transacao = data_transacao;
		this.data_pagamento = data_pagamento;
		this.valor = valor;
		this.status = status;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getConta_id() {
		return conta_id;
	}

	public void setConta_id(Integer conta_id) {
		this.conta_id = conta_id;
	}

	public Integer getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(Integer usuario_id) {
		this.usuario_id = usuario_id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getEnvolvido() {
		return envolvido;
	}

	public void setEnvolvido(String envolvido) {
		this.envolvido = envolvido;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getData_transacao() {
		return data_transacao;
	}

	public void setData_transacao(String data_transacao) {
		this.data_transacao = data_transacao;
	}

	public String getData_pagamento() {
		return data_pagamento;
	}

	public void setData_pagamento(String data_pagamento) {
		this.data_pagamento = data_pagamento;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
