package uimaps;

import core.Element;
import enums.ByValue;

public class ResultadoBuscaMap {

	public Element produtoBusca = new Element(ByValue.XPATH,
			"//li[@class='nm-product-item']//h2[@class='nm-product-name']");

	public Element buttonAddSacola = new Element(ByValue.XPATH, "//button[contains(@class,'buy-product')]");

	public Element buttonContinuar = new Element(ByValue.XPATH, "//a[contains(@class,'btn--continue')]");

	public Element botaoFecharInvite = new Element(ByValue.ID, "acsFocusFirst");

	public Element tituloPage = new Element(ByValue.ID, "main-title");

	public Element produtoASelecionar(String nomeProduto) {
		return new Element(ByValue.XPATH,
				"(//li[@class='nm-product-item']//a[contains(@title,'" + nomeProduto + "')])[1]");
	}
}