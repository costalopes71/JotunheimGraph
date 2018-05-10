package br.com.sinapsis.jotunheimgraph;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.sinapsis.jotunheimgraph.dot.DotBuilder;
import br.com.sinapsis.jotunheimgraph.to.Alimentador;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;
import br.com.sinapsis.jotunheimgraph.workers.WorkerTaskStructure;

public class GraphCreator {

	public static Map<String, Set<Alimentador>> subestacoesMap = new ConcurrentHashMap<>();
	private static Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		System.out.println("===========================================================");
		System.out.println("============Bem-vindo ao Jotunheim Graph Creator===========");
		System.out.println("===========================================================");
		
		String resultado = buildStructure("C:/tmp/_egrid/CEB_TODAS_SUB_20180509-143602_190.egrid", "C:/jotunheim_required_files");
		
		logger.info(resultado);
		logger.info("Tamanho da estrutura: " + subestacoesMap.size());
		logger.info("Inicio da geracao dos arquivos DOT.");
		
		List<File> dotFiles = null;
		try {
			dotFiles = DotBuilder.createDotFiles();
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
		WorkerTaskStructure task1 = new WorkerTaskStructure(0, (listaArquivos.size() / 3), listaArquivos);
		WorkerTaskStructure task2 = new WorkerTaskStructure((listaArquivos.size() / 3), listaArquivos.size() - (listaArquivos.size() / 3), listaArquivos);
		WorkerTaskStructure task3 = new WorkerTaskStructure(listaArquivos.size() - (listaArquivos.size() / 3), listaArquivos.size(), listaArquivos);
		
		Thread thread1 = new Thread(task1, "MontaEstrutura1");
		Thread thread2 = new Thread(task2, "MontaEstrutura2");
		Thread thread3 = new Thread(task3, "MontaEstrutura3");
		logger.info("Iniciando 3 threads para a montagem da estrutra de relacoes de todas as subestacoes.");
		thread1.start();
		thread2.start();
		thread3.start();
		
		logger.info("Threads iniciadas com sucesso. Aguardando o termino de todas as threads...");
		try {
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			logger.error("Erro ao aguardar a Thread terminar. A aplicacao sera abortada!");
			e.printStackTrace();
			System.exit(1);
		}
		logger.info("Todas as threads fizeram sua tarefa com sucesso...");
		
		return "Construcao da estrutura encerrada com sucesso.";
	}
	
}
