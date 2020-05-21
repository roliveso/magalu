package core;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import consts.Constants;
import enums.ByValue;
import exceptions.ElementException;

public class Element {

	private WebDriver driver;
	private ByValue byValue;
	private String value;
	private Integer timeSeconds = Constants.TEMPO_ESPERA_MIN;

	/**
	 * @param by    Representa o By utilizado para mapear o Element
	 * @param value Representa o valor do mapeamento do Element
	 */
	public Element(ByValue by, String value) {

		this.byValue = by;
		this.value = value;
		this.driver = DriverSingleton.getDriver();
	}

	private void setTimeSeconds(Integer timeSeconds) {

		this.timeSeconds = timeSeconds;
	}

	/**
	 * Clica no elemento utilizando javascript
	 * 
	 * @throws ElementException
	 */
	public void clickJS() throws ElementException {

		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click()", searchElement());
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Move o mouse ate o elemento
	 * 
	 * @throws ElementException
	 */
	public void hover() throws ElementException {

		try {
			Actions actions = new Actions(driver);
			WebElement webE = searchElement();
			actions.moveToElement(webE).perform();
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Clica no elemento
	 * 
	 * @throws ElementException
	 */
	public void click() throws ElementException {

		try {
			searchElement().click();
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Rola a barra ate o elemento ficar visivel na tela
	 * 
	 * @throws ElementException
	 */
	public void scrollToElement() throws ElementException {
		try {
			JavascriptExecutor js = (JavascriptExecutor) DriverSingleton.getDriver();
			js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchElement());
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Preenche o campo de texto desejado
	 * 
	 * @param text String desejada para preencher o campo
	 * @throws ElementException
	 */
	public void sendKeys(String text) throws ElementException {

		try {
			WebElement webE = searchElement();
			webE.sendKeys(text);
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Recupera o valor de um campo
	 * 
	 * @param isText Flag que controla se sera pegado o text ou o attribute value
	 * 
	 * @return Valor encontrado no campo
	 * @throws ElementException
	 */
	public String getText(boolean isText) throws ElementException {

		try {
			if (isText) {
				return searchElement().getText();
			} else {
				return searchElement().getAttribute("value");
			}
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Recupera o valor de um campo e compara com o resultado esperado
	 * 
	 * @param expectedText Valor que era esperado no campo
	 * @throws ElementException
	 */
	public void getTextCompare(String expectedText) throws ElementException {

		try {
			String currentValue = searchElement().getText();
			if (!expectedText.equals(currentValue)) {
				throw new ElementException(
						new Exception(
								"O valor obtido do elemento e: " + currentValue + " e o esperado e: " + expectedText),
						byValue, value);
			}
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Seleciona uma opcao em um drop down utilizando um valor visivel
	 * 
	 * @param visibleText Opcao desejada no select
	 * @throws ElementException
	 */
	public void select(String visibleText) throws ElementException {

		try {
			Select select = new Select(searchElement());
			select.selectByVisibleText(visibleText);
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Espera um elemento ser visivel e retorna uma exception caso nao apareca
	 * 
	 * @throws Exception
	 */
	public void waitAppearException() throws ElementException {

		WebDriverWait wait = new WebDriverWait(driver, timeSeconds);
		try {
			wait.until(ExpectedConditions.visibilityOf(searchElement()));
		} catch (Exception e) {
			throw new ElementException(e, byValue, value);
		}
	}

	/**
	 * Metodo que verifica se o elemento esta presente no frame atual e inicia o
	 * processo de busca em profundidade caso ele nao esteja
	 * 
	 * @return WebElement encontrado nos frames ou null caso nao seja encontrado em
	 *         nenhum
	 * @throws Exception
	 */
	public WebElement searchElement() throws Exception {

		WebElement webE = createWebElement();
		if (webE != null) {
			return webE;
		}
		DriverSingleton.moveToFrame(null);
		setTimeSeconds(Constants.TEMPO_ESPERA_FRAME);
		webE = searchFrames();
		setTimeSeconds(Constants.TEMPO_ESPERA_MIN);
		if (webE != null) {
			return webE;
		} else {
			throw new Exception("O elemento:" + this.value + " nao foi encontrado em nenhum frame");
		}
	}

	/**
	 * Busca em profundidade nos frames recursivo
	 * 
	 * @return WebElement encontrado nos frames ou null caso nao seja encontrado em
	 *         nenhum
	 * @throws Exception
	 */
	private WebElement searchFrames() throws Exception {

		WebElement webE = createWebElement();
		if (webE != null) {
			return webE;
		}
		Integer frameIndex = 0;
		while (true) {
			try {
				DriverSingleton.moveToFrame(frameIndex);
			} catch (Exception e) {
				DriverSingleton.getDriver().switchTo().parentFrame();
				return null;
			}
			webE = searchFrames();
			if (webE != null) {
				return webE;
			}
			frameIndex++;
		}
	}

	/**
	 * Instancia um WebElement do Element
	 * 
	 * @return WebElement do proprio Element
	 * @throws Exception
	 */
	private WebElement createWebElement() throws Exception {

		DriverSingleton.esperarPaginaCarregar();
		WebDriverWait wait = new WebDriverWait(driver, timeSeconds);
		try {
			switch (byValue) {
			case XPATH:
				return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(value)));
			case ID:
				return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(value)));
			case CLASS_NAME:
				return wait.until(ExpectedConditions.presenceOfElementLocated(By.className(value)));
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Instancia e cria uma lits de WebElement do Element
	 * 
	 * @return WebElement do proprio Element
	 * @throws Exception
	 */
	public List<WebElement> createWebElements() throws Exception {

		DriverSingleton.esperarPaginaCarregar();
		WebDriverWait wait = new WebDriverWait(driver, timeSeconds);
		try {
			switch (byValue) {
			case XPATH:
				return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(value)));
			case ID:
				return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(value)));
			case CLASS_NAME:
				return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(value)));
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}