package pages;

import consts.Constants;
import uimaps.SacolaMap;
import utils.Evidence;
import utils.ExcelManager;

public class SacolaPage {

	private Evidence evidence;
	private ExcelManager excelManager;
	private SacolaMap sacolaMap = new SacolaMap();

	public SacolaPage(Evidence evidence, ExcelManager excelManager) {
		this.evidence = evidence;
		this.excelManager = excelManager;
	}

	/**
	 * Valida se o produto foi adicionado na sacola
	 *
	 * @param texto produto a ser verificado
	 * @return String vazia caso o metodo tenha sido executado com sucesso, ou
	 *         preenchida com o erro caso contrario
	 * @throws Exception
	 */
	public String validaProdutoSacola(String texto) throws Exception {
		try {
			texto = excelManager.getValue(texto);
			evidence.takeScreenshot("valida_produto_sacola", sacolaMap.titleSacola);
			if (!sacolaMap.produtoSacola.getText(true).contains(texto))
				throw new Exception("O produto não foi adicionado na sacola");
		} catch (Exception e) {
			return e.getMessage();
		}
		return Constants.SUCESSO;
	}
}