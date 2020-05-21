package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import consts.Constants;
import core.DriverSingleton;
import core.Element;
import enums.LogType;

public class Evidence {

	private XWPFDocument		doc;
	private FileOutputStream	out;
	private String				identifier, caseName;
	private int					index	= 0;

	public Evidence(String identifier, String caseName){
		this.identifier = identifier;
		this.caseName = caseName;
	}

	/**
	 * Gera um documento docx que pega o output de imagens e coloca no documento
	 *
	 * @param caseName
	 *            Nome do caso na planilha
	 * @param finalResult
	 *            Resultado final da planilha
	 * @param expectedResult
	 *            Resultado esperado da planilha
	 * @param runtime
	 *            Tempo de execucao do script
	 */
	public void saveEvidenceDocument(String caseName, String finalResult,
			String expectedResult, String runtime) {

		try {
			String time = new SimpleDateFormat("dd/MM/yyyy")
					.format(Calendar.getInstance().getTime());
			String path = findCaseWithCaseName(caseName);
			String saveDocumentDir = path.split("Evidence")[0] + File.separator;
			List<String> imgList = imgsFinder(path);
			imgList = sortImageList(imgList);
			File file = Paths.get(System.getProperty("user.dir"),
					Constants.FILES_FOLDER, Constants.TEMPLATE_NAME).toFile();
			FileInputStream fis = new FileInputStream(file.getAbsolutePath());
			doc = new XWPFDocument(fis);
			XWPFParagraph p = doc.createParagraph();
			XWPFRun r1 = p.createRun();
			r1.setFontFamily("Calibri");
			r1.setFontSize(11);
			r1.setBold(true);
			r1.setText("Caso de Teste: ");
			r1.setText(caseName);
			XWPFTable table = doc.createTable();
			XWPFTableRow tableRowOne = table.getRow(0);
			XWPFParagraph paragraphOne = tableRowOne.getCell(0).addParagraph();
			setRun(paragraphOne.createRun(), "Calibri", 11, "Resultado Final",
					true);
			tableRowOne.createCell();
			paragraphOne = tableRowOne.getCell(1).addParagraph();
			setRun(paragraphOne.createRun(), "Calibri", 11,
					"Resultado Esperado", true);
			tableRowOne.createCell();
			paragraphOne = tableRowOne.getCell(2).addParagraph();
			setRun(paragraphOne.createRun(), "Calibri", 11, "Tempo de Execução",
					true);
			XWPFTableRow tableRowTwo = table.createRow();
			XWPFParagraph paragraphTwo = tableRowTwo.getCell(0).addParagraph();
			setRun(paragraphTwo.createRun(), "Calibri", 11, finalResult, false);
			paragraphTwo = tableRowTwo.getCell(1).addParagraph();
			setRun(paragraphTwo.createRun(), "Calibri", 11, expectedResult,
					false);
			paragraphTwo = doc.createParagraph();
			paragraphTwo = tableRowTwo.getCell(2).addParagraph();
			setRun(paragraphTwo.createRun(), "Calibri", 11, runtime, false);
			paragraphTwo = doc.createParagraph();
			XWPFRun r = paragraphTwo.createRun();
			for (String img : imgList) {
				int format = XWPFDocument.PICTURE_TYPE_PNG;
				String imagePath = path + File.separator + img;
				r.addBreak();
				r.setFontFamily("Calibri");
				r.setFontSize(11);
				r.setText("Nome da Imagem: " + img);
				r.addPicture(new FileInputStream(imagePath), format, imagePath,
						Units.toEMU(500), Units.toEMU(257));
				r.addBreak(BreakType.TEXT_WRAPPING);
			}
			XWPFHeaderFooterPolicy headerFooterPolicy = doc
					.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = doc.createHeaderFooterPolicy();
			XWPFFooter footer = headerFooterPolicy
					.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
			XWPFParagraph paragraphFooter = footer.createParagraph();
			paragraphFooter.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun runFooter = paragraphFooter.createRun();
			runFooter.setFontSize(11);
			runFooter.setFontFamily("calibri");
			runFooter.setText(
					"Teste executado de forma automatizada em " + time);
			CTSectPr sectPr = doc.getDocument().getBody().getSectPr();
			if (sectPr == null)
				sectPr = doc.getDocument().getBody().addNewSectPr();
			out = new FileOutputStream(saveDocumentDir + caseName + ".docx");
			doc.write(out);
			Log.sendLog("Documento de evidencias salvo com sucesso!",
					LogType.INFO);
		} catch (Exception e) {
			Log.sendLog("Erro ao gerar documento de evidencias do caso: "
					+ caseName + "\nException: " + e.getMessage(),
					LogType.ERROR);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.sendLog(e.getMessage(), LogType.WARN);
				}
			}
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					Log.sendLog(e.getMessage(), LogType.WARN);
				}
			}
		}
	}

	/**
	 * Coloca propriedades ou textos em uma regiao
	 *
	 * @param run
	 *            Regiao que vai ser escrita
	 * @param fontFamily
	 *            Fonte do texto
	 * @param fontSize
	 *            Tamanho da fonte
	 * @param text
	 *            Texto a ser escrito na regiao
	 * @param bold
	 *            Se true texto negrito, se false texto sem negrito
	 */
	private void setRun(XWPFRun run, String fontFamily, int fontSize,
			String text, boolean bold) {

		run.setFontFamily(fontFamily);
		run.setFontSize(fontSize);
		run.setText(text);
		run.setBold(bold);
	}

	/**
	 * Esse metodo pega as imagens da pasta e faz o split de '_', separando o texto do numero
	 *
	 * @param listaImagens
	 *            Lista de imagens a serem ordenadas
	 * @return Retorna uma lista das imagens ordenadas
	 */
	private ArrayList<String> sortImageList(List<String> listaImagens) {

		int tamanhoLista = listaImagens.size();
		ArrayList<String> novaLista = new ArrayList<String>();
		for (int i = 0; i < tamanhoLista + 1; i++) {
			novaLista.add("x");
		}
		for (String img : listaImagens) {
			String numImg[] = img.split("_");
			int num = Integer.parseInt(numImg[0]);
			novaLista.set(num, img);
		}
		novaLista.remove(0);
		return novaLista;
	}

	/**
	 * Busca na pasta output a pasta correspondente ao caso desejado
	 *
	 * @param caso
	 *            Caso que deseja procurar a pasta
	 * @return Retorna uma string com o caminho da pasta evidence referente ao caso passado
	 * @throws Exception
	 */
	private String findCaseWithCaseName(String caso) throws Exception {

		ArrayList<String> listaEvidenciasPorTempo = new ArrayList<String>();
		ArrayList<String> listaCasos = new ArrayList<String>();
		listaEvidenciasPorTempo = findCaseWithName(caso,
				Constants.OUTPUT_FOLDER);
		String caminho = "";
		for (String evidenciasPorTempo : listaEvidenciasPorTempo) {
			listaCasos = findCaseWithName("evidence", evidenciasPorTempo);
		}
		for (int i = 0; i < listaCasos.size(); i++) {
			caminho = listaCasos.get(i);
			String[] components = caminho.split(Pattern.quote(File.separator));
			String caminhoCaso = components[components.length - 2];
			if (caminhoCaso.equals(caso)) {
				break;
			}
		}
		return caminho;
	}

	/**
	 * Busca no caminho as pastas e subpastas que correspondem a palavra passada
	 *
	 * @param palavra
	 *            Palavra(pasta) que deseja procurar no caminho e subpastas
	 * @param caminhoInicial
	 *            Caminho inicial para as buscas pela palavra(pasta)
	 * @return Retorna uma lista com as pastas e subpastas que corresponde a palavra desejada
	 * @throws Exception
	 */
	private ArrayList<String> findCaseWithName(String palavra,
			String caminhoInicial) throws Exception {

		ArrayList<String> list = new ArrayList<String>();
		try {
			File file = new File(caminhoInicial);
			list = search(file, palavra, list);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return list;
	}

	/**
	 * Busca as imagens em um caminho passado
	 *
	 * @param caminho
	 *            Caminho onde deseja buscar as imagens
	 * @return Retorna uma lista com os nomes das imagens
	 */
	private List<String> imgsFinder(String caminho) {

		File[] imgs = new File(caminho).listFiles();
		if (ArrayUtils.isEmpty(imgs)) {
			Log.sendLog("Nenhuma imagem foi encontrada no caminho: " + caminho,
					LogType.WARN);
			return Collections.emptyList();
		}
		return Arrays.asList(imgs).stream().filter(imgFile -> imgFile.isFile())
				.map(imgFile -> imgFile.getName()).collect(Collectors.toList());
	}

	/**
	 * Procura no caminho inicial por pastas e subpastas que correspondem a palavra passada
	 *
	 * @param file
	 *            Arquivo de origem onde sera iniciado a busca
	 * @param palavra
	 *            Palavra desejada na buscar
	 * @param list
	 *            Passa a lista que vai ser usada para guardar os caminhos
	 * @return Retorna uma lista com as pastas e subpastas que correspondem a palavra desejada
	 */
	private ArrayList<String> search(File file, String palavra,
			ArrayList<String> list) {

		if (file.isDirectory()) {
			File[] subFolders = file.listFiles();
			for (int i = 0; i < subFolders.length; i++) {
				list = search(subFolders[i], palavra, list);
				if (file.getName().equalsIgnoreCase(palavra))
					list.add(file.getAbsolutePath());
				else if (file.getName().indexOf(palavra) > -1)
					list.add(file.getAbsolutePath());
			}
		} else if (file.getName().equalsIgnoreCase(palavra))
			list.add(file.getAbsolutePath());
		else if (file.getName().indexOf(palavra) > -1)
			list.add(file.getAbsolutePath());
		return list;
	}

	/**
	 * Tira um print do navegador e salva na pasta de evidencias aguardando um elemento especifico
	 *
	 * @param name
	 *            Nome do print que sera salvo
	 * @param elementoEsperado
	 *            Elemento que e aguardado na pagina antes do print ser tirado
	 * @throws Exception
	 */
	public void takeScreenshot(String name, Element elementoEsperado)
			throws Exception {

		elementoEsperado.searchElement();
		index++;
		String evidencePath = Paths.get(System.getProperty("user.dir"),
				Constants.OUTPUT_FOLDER, identifier, caseName, "Evidence")
				.toString();
		String path = String.format("%s/%04d_%s.png", evidencePath, index,
				name);
		TakesScreenshot scrShot = ((TakesScreenshot) DriverSingleton
				.getDriver());
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File(path);
		FileUtils.copyFile(SrcFile, DestFile);
	}

	/**
	 * Tira um print do navegador e salva na pasta de evidencias
	 *
	 * @param name
	 *            Nome do print que sera salvo
	 * @throws Exception
	 */
	public void takeScreenshot(String name) throws Exception {

		index++;
		String evidencePath = Paths.get(System.getProperty("user.dir"),
				Constants.OUTPUT_FOLDER, identifier, caseName, "Evidence")
				.toString();
		String path = String.format("%s/%04d_%s.png", evidencePath, index,
				name);
		TakesScreenshot scrShot = ((TakesScreenshot) DriverSingleton
				.getDriver());
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File(path);
		FileUtils.copyFile(SrcFile, DestFile);
	}
}
