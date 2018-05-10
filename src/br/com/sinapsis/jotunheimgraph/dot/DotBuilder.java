package br.com.sinapsis.jotunheimgraph.dot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.sinapsis.jotunheimgraph.GraphCreator;
import br.com.sinapsis.jotunheimgraph.to.Alimentador;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;

public class DotBuilder {

	private static Logger logger = LogManager.getLogger();
	
	public static List<File> createDotFiles() throws IOException {
		
		List<File> dotFiles = new ArrayList<>();
		Map<String, Set<Alimentador>> mapSub = GraphCreator.subestacoesMap;
		PrintWriter pw = null;
		
		for (Map.Entry<String, Set<Alimentador>> entry : mapSub.entrySet()) {
		    
			String siglaSub = entry.getKey();
			Set<Alimentador> setAlimentador = entry.getValue();

			for (Alimentador alim : setAlimentador) {
				
				int nrAlim = alim.getCodigo();
				Set<String> setRelacao = alim.getRelacoes();
				
				File directory = new File("C:/jotunheim_required_files/dots/" + siglaSub);
				
				if (!directory.exists()) {
					directory.mkdirs();
				}
				
				try {
					pw = MyUtils.createPrintWriter(directory + "/" + siglaSub + "_" + nrAlim + ".dot");
				} catch (IOException e) {
					e.printStackTrace();
					throw new IOException("ERRO. Nao foi possivel criar o arquivo no local: C:/jotunheim_required_files/dots/" + siglaSub + "/" + siglaSub + "_" + nrAlim + ".dot");
				}

				pw.println("digraph G {");
				pw.println("\trankdir=LR;");
				pw.println("\tranksep = 1.5");
			    pw.println("\tnode[fontsize=40]");
			    pw.println("\tedge [style=\"setlinewidth(3)\"]");
			    
			    for (String relacao : setRelacao) {
					pw.println("\t" + relacao);
				}
			    
			    pw.println("}");
			    pw.close();
			    dotFiles.add(new File(directory + "/" + siglaSub + "_" + nrAlim + ".dot"));
				
			}
		    logger.info("arquivos .DOT dos alimentadores da subestacao [" + siglaSub + "] gerados com sucesso!");
		}

		return dotFiles;
	}

}