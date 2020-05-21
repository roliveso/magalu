package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelManager {

	private HashMap<String, Integer>	headers			= new HashMap<String, Integer>();
	private XSSFWorkbook				workbook;
	private XSSFSheet					sheet;
	private FileInputStream				fis;
	private String						worksheetPath;
	private Integer						columnsCount	= 0;

	/**
	 * Construtor que carrega o excel de dados ja na aba correta
	 * 
	 * @param worksheetPath
	 *            Caminho do excel de dados
	 * @param sheetName
	 *            Nome da aba que sera aberta
	 */
	public ExcelManager(String worksheetPath, String sheetName)
			throws Exception{
		this.worksheetPath = worksheetPath;
		fis = new FileInputStream(worksheetPath);
		workbook = new XSSFWorkbook(fis);
		sheet = workbook.getSheet(sheetName);
		loadHeaders();
	}

	/**
	 * Metodo que carrega todos os cabecalhos do caso de teste
	 */
	private void loadHeaders() {

		XSSFRow firstRow = sheet.getRow(0);
		this.columnsCount = (int) firstRow.getLastCellNum();
		for (int i = 0; i < columnsCount; i++) {
			headers.put(String.valueOf(firstRow.getCell(i)), i);
		}
	}

	/**
	 * Metodo que retorna o valor de um dos cabecalhos carregados
	 * 
	 * @param columnName
	 *            Nome do cabecalho procurado
	 * @return O valor associado ao cabecalho procurado
	 * @throws Exception
	 */
	public String getValue(String columnName) throws Exception {

		XSSFRow row = sheet.getRow(1);
		Integer headersPos = headers.get(columnName);
		if (headersPos == null) {
			throw new Exception("A coluna: " + columnName
					+ " nao foi encontrada no excel de dados");
		}
		return String.valueOf(row.getCell(headersPos));
	}

	/**
	 * Metodo que seta o valor de um dos cabecalhos carregados
	 * 
	 * @param columnName
	 *            Nome do cabecalho procurado
	 * @param value
	 *            Valor que sera setado para o cabecalho passado
	 * @throws Exception
	 */
	public void setValue(String columnName, String value) throws Exception {

		XSSFRow row;
		if (sheet.getRow(1) == null)
			row = sheet.createRow(1);
		else
			row = sheet.getRow(1);
		Integer headersPos = headers.get(columnName);
		if (headersPos == null) {
			throw new Exception("A coluna: " + columnName
					+ " nao foi encontrada no excel de dados");
		}
		Cell cell = row.createCell(headersPos);
		cell.setCellValue(value);
	}

	/**
	 * Metodo que seta o valor de um dos cabecalhos carregados em outra aba
	 * 
	 * @param sheetName
	 *            Nome da aba que sera alterada
	 * @param columnName
	 *            Nome do cabecalho procurado
	 * @param value
	 *            Valor que sera setado para o cabecalho passado
	 * @throws Exception
	 */
	public void setValueOtherTab(String sheetName, String columnName,
			String value) throws Exception {

		XSSFSheet sheet = workbook.getSheet(sheetName);
		HashMap<String, Integer> headers = new HashMap<String, Integer>();
		XSSFRow firstRow = sheet.getRow(0);
		Integer columnsCount = (int) firstRow.getLastCellNum();
		for (int i = 0; i < columnsCount; i++) {
			headers.put(String.valueOf(firstRow.getCell(i)), i);
		}
		XSSFRow row;
		if (sheet.getRow(1) == null)
			row = sheet.createRow(1);
		else
			row = sheet.getRow(1);
		Integer headersPos = headers.get(columnName);
		if (headersPos == null) {
			throw new Exception("A coluna: " + columnName
					+ " nao foi encontrada no excel de dados");
		}
		Cell cell = row.createCell(headersPos);
		cell.setCellValue(value);
	}

	/**
	 * Salva o excel de dados no caminho em que ele foi aberto
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {

		fis.close();
		FileOutputStream fos = new FileOutputStream(worksheetPath);
		workbook.write(fos);
		fos.close();
	}
}
