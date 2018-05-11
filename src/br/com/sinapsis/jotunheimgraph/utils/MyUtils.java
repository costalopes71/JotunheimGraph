package br.com.sinapsis.jotunheimgraph.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static br.com.sinapsis.jotunheimgraph.utils.Constants.JotunheimConstantes.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import br.com.sinapsis.jotunheimgraph.GraphCreator;
import br.com.sinapsis.jotunheimgraph.to.Alimentador;

/**
 * Classe utilitária que possue diversos métodos que são utilizados nas demais classes do projeto.
 * @author Joao Lopes
 * @since 09/05/2018
 */
public final class MyUtils {

	private static Logger logger = LogManager.getLogger();
	
	/**
	 * Método responsável por descompactar um arquivo compactado.
	 * @param String compressedFilePath, caminho para o arquivo compactado.
	 * @param String outputFolder, caminho do diretorio onde os arquivos serao descompactados.
	 * @return List<File>, uma lista contendo todos os arquivos com extensao .ES que foram descompactados.
	 * @throws IOException
	 */
	public static List<File> unzipFiles(String compressedFilePath, String outputFolder) throws IOException {
		
		System.out.println("===========================================================");
		System.out.println("====== Iniciando a descompactação do arquivo ZIP! =D ======");
		System.out.println("===========================================================");
		List<File> filesList = new Vector<>();
		
		int count = 1;
		byte[] buffer = new byte[1024];
		
		File destinationFolder = new File(outputFolder);
		
		// cria o diretorio de destino se ele nao existir
		if (!destinationFolder.exists()) {
			destinationFolder.mkdir();
		}
		
		// acessa o conteudo do zip
		ZipInputStream zis = null;
		ZipEntry ze = null;
		
		try {
			zis = new ZipInputStream(new FileInputStream(compressedFilePath));
			ze = zis.getNextEntry();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Erro ao tentar acessar o conteudo do arquivo ZIP. Por favor certifique-se que o arquivo ou o caminho realmente existem. =( ");
		}
		
		while (ze != null) {
			
			String fileName = ze.getName();
			File newFile = new File(outputFolder + File.separator + fileName);
			
			logger.info("[" + count + "] arquivo descompactado - " + newFile.getAbsolutePath());
			count++;
			
			// cria todos os diretorios que nao existem
			new File(newFile.getParent()).mkdirs();
			
			FileOutputStream fos = new FileOutputStream(newFile);
			
			int len;
			while((len = zis.read(buffer)) > 0) {
				
				fos.write(buffer, 0, len);
				
			}
			
			fos.close();
			
			if (newFile.getName().contains(".es")) {
				filesList.add(newFile);
			}
			
			ze = zis.getNextEntry();
			
		}
		
		zis.closeEntry();
		zis.close();
		System.out.println("======================= :) :) :) :) =======================");
		System.out.println("====== [" + (count-1) + "] arquivos descompactados com sucesso! =) ======");
		System.out.println("===========================================================");
		return filesList;
	}
	
	public static String extraiSiglaSubestacao(String relativeFilePath) {
		return relativeFilePath.substring(relativeFilePath.indexOf("_") + 1, relativeFilePath.indexOf("."));
	}
	
	public static String adaptaParaCaminhoWindows(String absolutePath) {
		return absolutePath.replaceAll("\\\\", "/");
	}

	public synchronized static PrintWriter createPrintWriter(String dotPath) throws IOException {

		FileWriter outputStream;

		try {
			outputStream = new FileWriter(dotPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("nao foi possivel criar ou abrir o arquivo neste caminho: " + dotPath);
		}
		return new PrintWriter(outputStream);
	}

	public static String generateBatchFile() throws IOException {
		
		String batPath = DIRETORIO_DOTS_FILE + "/arquivoBAT.bat";
		PrintWriter pw = createPrintWriter(batPath);
		
		Map<String, Set<Alimentador>> subestacoesMap = GraphCreator.subestacoesMap;
		
		for (Map.Entry<String, Set<Alimentador>> entry : subestacoesMap.entrySet()) {
			
			String siglaSub = entry.getKey();
			Set<Alimentador> setAlimentador = entry.getValue();

			for (Alimentador alimentador : setAlimentador) {
				pw.println("dot -Tpng " + DIRETORIO_DOTS_FILE + "/" + siglaSub + "/" + siglaSub + "_" + alimentador.getCodigo() + ".dot > "
						+ IMAGES_DIRECTORY + "/" + siglaSub + "/" + siglaSub + "_" + alimentador.getCodigo() + ".png");
			}
			
		}
		pw.println("exit");
		pw.close();
		return batPath;
	}
	
	public static final void mkdirsImages() {
		File imageDirectory = null;
		for (String siglaSub : GraphCreator.subestacoesMap.keySet()) {
			imageDirectory = new File(IMAGES_DIRECTORY + "/" + siglaSub);
			
			if (!imageDirectory.exists()) {
				imageDirectory.mkdirs();
			}
		}
	}

	public static void runBatFile(String batPath) throws Exception {
		Process p;
		try {
			p = Runtime.getRuntime().exec("cmd /c start /wait " + batPath);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("nao foi possivel localizar o arquivo BAT.");
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new InterruptedException("um erro ocorreu quando da execucao ou da espera da execucao do arquivo BAT. A execucao da aplicacao sera abortada!");
		}
	}
	
}
