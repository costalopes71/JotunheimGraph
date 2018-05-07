package br.com.sinapsis.jotunheimgraph;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.sinapsis.jotunheimgraph.dot.DotBuilder;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;
import br.com.sinapsis.jotunheimgraph.workers.WorkerTaskStructure;

public class GraphCreator {

	public static Map<String, Map<Integer, List<String>>> subestacoesMap = new HashMap<>();
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		System.out.println("===========================================================");
		System.out.println("============Bem-vindo ao Jotunheim Graph Creator===========");
		System.out.println("===========================================================");
		
		String resultado = buildStructure("C:/tmp/_egrid/COPEL_TODAS_SUB_20180507-120230_473.egrid", "C:/teste_");
		
		System.out.println(resultado);
		System.out.println("Tamanho da estrutura: " + subestacoesMap.size());
		
		System.out.println("Início da geração dos arquivos DOT.");
		List<File> dotFiles = null;
		try {
			dotFiles = DotBuilder.createDotFiles();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		subestacoesMap.clear();
		
		System.out.println("Fim da geração dos arquivos DOT.");
		System.out.println(dotFiles.size() + "arquivos gerados.");
		System.out.println("Mapa limpo: " + subestacoesMap.size());
		
		long elapsed = (System.currentTimeMillis() - start) / 1000;
		System.out.println("===========================================================");
		System.out.println("TÉRMINO. Elapsed time: " + elapsed + " segundos.");
	}
	
	public static String buildStructure(String zipFilePath, String outputFolder) {
		
		List<File> listaArquivos = null;
		try {
			listaArquivos = MyUtils.unzipFiles(zipFilePath, outputFolder);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		WorkerTaskStructure task1 = new WorkerTaskStructure(0, (listaArquivos.size() / 2), listaArquivos);
		WorkerTaskStructure task2 = new WorkerTaskStructure((listaArquivos.size() / 2), listaArquivos.size(),listaArquivos);
		
		Thread thread1 = new Thread(task1, "MontaEstrutura1");
		Thread thread2 = new Thread(task2, "MontaEstrutura2");
		System.out.println("Iniciando 2 threads para a montagem da estrutra de relações de todas as subestações.");
		thread1.start();
		thread2.start();
		
		System.out.println("Threads iniciadas com sucesso. Aguardando o término de todas as threads...");
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			System.err.println("Erro ao aguardar a Thread terminar.");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Todas as threads fizeram sua tarefa com sucesso...");
		
		return "Construção da estrutura encerrada com sucesso.";
	}
	
}
