package pages;

import consts.Constants;
import core.DriverSingleton;
import uimaps.MagaluHomeMap;
import utils.Evidence;
import utils.ExcelManager;

public class MagaluHomePage {

	private Evidence evidence;
	private ExcelManager excelManager;
	private MagaluHomeMap magaluHomeMap = new MagaluHomeMap();

	public MagaluHomePage(Evidence evidence, ExcelManager excelManager) {
		this.evidence = evidence;
		this.excelManager = excelManager;
	}

	/**
	 * Realiza a pesquisa de um produto na home page
	 *
	 * @param url   URL do site
	 * @param texto produto a ser procurado
	 * @return String vazia caso o metodo tenha sido executado com sucesso, ou
	 *         preenchida com o erro caso contrario
	 * @throws Exception
	 */
	public String pesquisar(String url, String texto) throws Exception {
		try {
			url = excelManager.getValue(url);
			texto = excelManager.getValue(texto);
			DriverSingleton.navegar(url);
			evidence.takeScreenshot("Realiza_Pesquisa_Home", magaluHomeMap.inputProcurar);
			magaluHomeMap.inputProcurar.sendKeys(texto);
			magaluHomeMap.botaoProcurar.click();
		} catch (Exception e) {
			return e.getMessage();
		}
		return Constants.SUCESSO;
	}
}