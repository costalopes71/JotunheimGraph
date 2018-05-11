package br.com.sinapsis.jotunheimgraph.job;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.sinapsis.jotunheimgraph.job.tasks.WorkerTaskStructure;
import br.com.sinapsis.jotunheimgraph.utils.Constants;

public class Job {

	private static Logger logger = LogManager.getLogger(Constants.Loggers.CONSOLE);
	
	public void runStructureBuilderJob(List<File> listaArquivos) {
		
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
		
	}
	
}
