package core;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import consts.Constants;

public class DriverSingleton {

	private static WebDriver driver;

	/**
	 * Gerencia uma instancia singular do driver
	 * 
	 * @return WebDriver
	 */
	public static WebDriver getDriver() {

		if (driver != null)
			return driver;
		try {
			driver = Config.buildDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		} catch (Exception e) {
			Assert.fail("Nao foi possivel iniciar a instancia do driver\n"
					+ e.getMessage());
		}
		return driver;
	}

	/**
	 * Encerra a instancia do webdriver
	 */
	public static void closeDriver() {

		if (driver == null)
			return;
		driver.close();
		driver = null;
	}

	/**
	 * Navega ate determinado endereco
	 * 
	 * @param url
	 *            Endereco a ser acessado
	 */
	public static void navegar(String url) {

		DriverSingleton.getDriver().navigate().to(url);
		esperarPaginaCarregar();
	}

	/**
	 * Aguarda que a pagina esteja carregada
	 */
	public static void esperarPaginaCarregar() {

		WebDriverWait wait = new WebDriverWait(DriverSingleton.getDriver(),
				Constants.TEMPO_ESPERA_MED);
		wait.until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
				.executeScript("return document.readyState")
				.equals("complete"));
	}

	/**
	 * Utiliza o driver para acessar um frame via index ou voltar para o raiz
	 * 
	 * @param index
	 *            Index do frame que sera acessado
	 * 
	 * @throws Exception
	 */
	public static void moveToFrame(Integer index) throws Exception {

		if (index == null) {
			DriverSingleton.getDriver().switchTo().defaultContent();
		} else {
			DriverSingleton.getDriver().switchTo().frame(index);
		}
	}
}
