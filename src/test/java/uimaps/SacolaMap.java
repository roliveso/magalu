package uimaps;

import core.Element;
import enums.ByValue;

public class SacolaMap {

	public Element titleSacola = new Element(ByValue.XPATH, "//div[@class='BasketPage-title']");

	public Element produtoSacola = new Element(ByValue.XPATH, "(//div[@class='BasketItem']//p)[1]");
}