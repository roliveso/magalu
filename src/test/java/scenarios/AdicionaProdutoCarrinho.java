package scenarios;

import org.junit.Before;
import org.junit.Test;

import core.BaseTest;
import enums.LogType;
import pages.MagaluHomePage;
import pages.ResultadoBuscaPage;
import pages.SacolaPage;
import utils.Log;

public class AdicionaProdutoCarrinho extends BaseTest {
	MagaluHomePage magaluHomePage;
	ResultadoBuscaPage resultadoBuscaPage;
	SacolaPage sacolaPage;

	@Before
	public void beforeTest() throws Exception {
		magaluHomePage = new MagaluHomePage(evidence, excelManager);
		resultadoBuscaPage = new ResultadoBuscaPage(evidence, excelManager);
		sacolaPage = new SacolaPage(evidence, excelManager);
	}

	@Test
	public void script() throws Exception {
		try {
			executeStep(magaluHomePage.pesquisar("vUrl", "vProduto"), "realiza-pesquisa");
			executeStep(resultadoBuscaPage.validaRetornoBusca("vProduto"), "valida-busca-realizada");
			executeStep(resultadoBuscaPage.selecionaProduto("vProduto"), "seleciona-produto-procurado");
			executeStep(sacolaPage.validaProdutoSacola("vProduto"), "valida-produto-sacola");
		} catch (Exception e) {
			Log.sendLog("<====== SCENARIO FAILED ======>", LogType.ERROR);
			if (errorMsg.isEmpty()) {
				errorMsg = "SCRIPT_ERROR: " + e.getMessage();
			}
			Log.sendLog(errorMsg, LogType.ERROR);
		}
	}
}