package core;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import consts.Constants;

public class Config {

	/**
	 * Gera um identificador utilizando o timestamp atual
	 * 
	 * @return Timestamp gerado
	 */
	public static String generateTimestampId() {

		return DateTimeFormatter.ofPattern("yyyyMMddhhmmss").format(LocalDateTime.now());
	}

	/**
	 * Metodo que recupera o caminho do webdriver
	 * 
	 * @return O caminho do webdriver
	 */
	private static String getDriverPath() {

		return String.format("%s%c%s%c%s", System.getProperty("user.dir"), File.separatorChar, Constants.DRIVERS_FOLDER,
				File.separatorChar, isWindows() ? "chromedriver.exe" : "chromedriver");
	}

	/**
	 * Metodo que instacia o driver, utilizando headless para linux
	 * 
	 * @return Webdriver instanciado
	 */
	public static WebDriver buildDriver() {

		System.setProperty("webdriver.chrome.driver", getDriverPath());
		if (!isWindows()) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("headless").addArguments("disable-gpu").addArguments("no-sandbox")
					.addArguments("disable-dev-shm-usage").addArguments("window-size=1024,768");
			return new ChromeDriver(options);
		}

		return new ChromeDriver();
	}

	/**
	 * Metodo que verifica que se o SO e windows
	 * 
	 * @return True para windows e false caso contrario
	 */
	private static boolean isWindows() {

		return System.getProperty("os.name").contains("Windows");
	}
}