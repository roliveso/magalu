package core;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;

import consts.Constants;
import enums.LogType;
import exceptions.StepErrorException;
import utils.Evidence;
import utils.ExcelManager;
import utils.Log;
import utils.Watch;

public class BaseTest {

	public Evidence evidence;
	public ExcelManager excelManager;
	public String errorMsg = "";
	private String casoTeste;
	protected long initStopwatch, endStopwatch;

	/**
	 * Inicializa valores antes da execucao do cenario
	 */
	@Before
	public void before() {

		casoTeste = this.getClass().getSimpleName();
		Log.sendLog("====> Caso de Teste: " + casoTeste, LogType.INFO);
		evidence = new Evidence(Config.generateTimestampId(), casoTeste);
		try {
			excelManager = new ExcelManager(System.getProperty("user.dir") + File.separator + Constants.FILES_FOLDER
					+ File.separator + Constants.EXCEL_NAME, casoTeste);
		} catch (Exception e) {
			String erro = "Nao foi possivel carregar o excel de dados corretamente\n" + e.getMessage();
			errorMsg = erro;
			Log.sendLog(erro, LogType.ERROR);
			Assert.fail(erro);
		}
		initStopwatch = Watch.getCurrentTime();
	}

	/**
	 * Finaliza calculo de execucao e gera evidencia apos o cenario
	 */
	@After
	public void after() {

		endStopwatch = Watch.getCurrentTime();
		try {
			excelManager.save();
		} catch (Exception e) {
			Log.sendLog("Nao foi possivel salvar e encerrar o excel de dados corretamente\n" + e.getMessage(),
					LogType.ERROR);
		}
		if (errorMsg.isEmpty()) {
			errorMsg = "SUCESSO";
		}
		evidence.saveEvidenceDocument(casoTeste, errorMsg, "SUCESSO",
				Watch.stopWatchElapsedTime(initStopwatch, endStopwatch));
		Assert.assertEquals("SUCESSO", errorMsg);
	}

	/**
	 * Encerra o browser apos a execucao da classe de teste
	 */
	@AfterClass
	public static void afterClass() {

		DriverSingleton.closeDriver();
	}

	/**
	 * Verifica o resultado da execucao de um determinado passo
	 *
	 * @param stepResult Resultado da execucao do passo
	 * @param stepLabel  Identificador do passo
	 * @throws Exception Lanca excecao em caso de erro na execucao do passo
	 */
	protected void executeStep(String stepResult, String stepLabel) throws Exception {

		if (!stepResult.isEmpty()) {
			evidence.takeScreenshot("erro_" + stepLabel);
			throw new StepErrorException(stepLabel, stepResult);
		}
		Log.sendLog("Step executado: " + stepLabel, LogType.INFO);
	}
}
