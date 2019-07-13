package br.com.seubarriga.core.utils;

import br.com.seubarriga.core.domain.Transacao;
import br.com.seubarriga.core.domain.enums.TipoMovimentacao;
import io.restassured.RestAssured;

public class BarrigaUtils {

	public static Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	public static Integer getIdMovimentacaoPelaDescricao(String descricao) {
		return RestAssured.get("/transacoes?descricao=" + descricao).then().extract().path("id[0]");
	}
	
	public static Transacao getTransacaoValida() {
		return new Transacao(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"), "Descrição da movimentação", 
				"Envolvido na movimentação", TipoMovimentacao.RECEITA, DateUtils.getDataDiferencaDias(-1), 
				DateUtils.getDataDiferencaDias(5), 100f, true);
	}
}
