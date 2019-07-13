package br.com.seubarriga.tests.refact;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.seubarriga.core.BaseTest;
import br.com.seubarriga.core.utils.BarrigaUtils;

public class SaldoTest extends BaseTest {
	
	@Test
	public void deveCalcularSaldoDasContas() {
		Integer contaId = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id ==" + contaId + "}.saldo", is("534.00"))
		;
	}
}
