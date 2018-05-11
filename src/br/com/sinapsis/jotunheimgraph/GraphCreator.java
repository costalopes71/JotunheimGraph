package br.com.sinapsis.jotunheimgraph;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;
import br.com.sinapsis.jotunheimgraph.dot.DotBuilder;
import br.com.sinapsis.jotunheimgraph.job.Job;
import br.com.sinapsis.jotunheimgraph.to.Alimentador;
import br.com.sinapsis.jotunheimgraph.utils.Constants;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;
import static br.com.sinapsis.jotunheimgraph.utils.Constants.JotunheimConstantes.*;

public class GraphCreator {

	public static Map<String, Set<Alimentador>> subestacoesMap = new ConcurrentHashMap<>();
	private static Logger logger = LogManager.getLogger(Constants.Loggers.CONSOLE);
	private static boolean realConsole = false;
	
	public static void main(String[] args) {
		
		// atualiza o flag
		if (System.console() != null)
			realConsole = true;

		//
		// Carrega o recurso para saidas coloridas no console
		//
		if (realConsole)// ser realmente estiver rodando no console carregar o AnsiConsole
			AnsiConsole.systemInstall();
		
		long start = System.currentTimeMillis();
		System.out.println("===========================================================");
		System.out.println("============Bem-vindo ao Jotunheim Graph Creator===========");
		System.out.println("===========================================================");

		String resultado = buildStructure(CAMINHO_ARQUIVO_EGRID, DIRETORIO_DESTINO);
		
		logger.info(resultado);
		logger.info("Tamanho da estrutura: " + subestacoesMap.size());
		logger.info("Inicio da geracao dos arquivos DOT.");
		
		List<File> dotFiles = null;
		try {
			dotFiles = DotBuilder.createDotFiles(subestacoesMap);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		logger.info("fim da geracao dos arquivos DOT.");
		logger.info(dotFiles.size() + " arquivos gerados.");
		
		logger.info("iniciando a criacao do arquivo BAT para a geracao dos grafos.");
		String batPath = null;
		try {
			batPath = MyUtils.generateBatchFile();
		} catch (IOException e) {
			logger.fatal(" erro ao criar o arquivo BAT. A execucao da aplicacao sera abortada!");
			e.printStackTrace();
			System.exit(2);
		}
		logger.info("arquivo BAT criado com sucesso!");
		
		logger.info("inicio da criacao dos diretorios das imagens!");
		MyUtils.mkdirsImages();
		logger.info("sucesso: fim da criacao dos diretorios dos grafos!");
		logger.info("inicio da execucao do arquivo BAT para geracao dos grafos!");
		try {
			MyUtils.runBatFile(batPath);
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(4);
		}
		logger.info("sucesso! Fim da execucao do arquivo BAT para geracao dos grafos!");
		
		subestacoesMap.clear();
		logger.info("estrutura de mapa limpa: " + subestacoesMap.size());
		
		long elapsed = (System.currentTimeMillis() - start) / 1000;
		System.out.println("===========================================================");
		System.out.println("TERMINO. Elapsed time: " + elapsed + " segundos.");
		System.out.println("Tudo deu certo. Obrigado por usar.");
		System.out.println("===========================================================");
	}

	public static String buildStructure(String zipFilePath, String outputFolder) {
		
		List<File> listaArquivos = null;
		try {
			listaArquivos = MyUtils.unzipFiles(zipFilePath, outputFolder);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		Job job = new Job();
		job.runStructureBuilderJob(listaArquivos);
		
		return "Construcao da estrutura encerrada com sucesso.";
	}
	
}
