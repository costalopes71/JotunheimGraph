package br.com.sinapsis.jotunheimgraph.workers;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import br.com.sinapsis.jotunheimgraph.GraphCreator;
import br.com.sinapsis.jotunheimgraph.dao.SubestacaoDAO;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;

public class WorkerTaskStructure implements Runnable {

	private int inicioIndex;
	private int fimIndex;
	private List<File> listaArquivos;
	private SubestacaoDAO dao;
	
	public WorkerTaskStructure(int inicioIndex, int fimIndex, List<File> listaArquivos) {
		this.dao = new SubestacaoDAO();
		this.inicioIndex = inicioIndex;
		this.fimIndex = fimIndex;
		this.listaArquivos = listaArquivos;
	}

	@Override
	public void run() {

		for (int i = inicioIndex; i < fimIndex; i++) {
			
			File arquivoSqllite = listaArquivos.get(i);
			String siglaSub = MyUtils.extraiSiglaSubestacao(arquivoSqllite.getName());
			String absolutePath = MyUtils.adaptaParaCaminhoWindows(arquivoSqllite.getAbsolutePath());
			Map<Integer, List<String>> mapRelacoes = null;
			try {
				mapRelacoes = dao.getRelacoesTrecho(absolutePath, siglaSub);
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			GraphCreator.subestacoesMap.put(siglaSub, mapRelacoes);
		}
		
	}

	
}
