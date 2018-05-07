package br.com.sinapsis.jotunheimgraph.dot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.sinapsis.jotunheimgraph.GraphCreator;
import br.com.sinapsis.jotunheimgraph.utils.MyUtils;

public class DotBuilder {

	public static List<File> createDotFiles() throws IOException {
		
		List<File> dotFiles = new ArrayList<>();
		Map<String, Map<Integer, List<String>>> mapSub = GraphCreator.subestacoesMap;
		PrintWriter pw = null;
		
		for (Map.Entry<String, Map<Integer, List<String>>> entry : mapSub.entrySet()) {
		    
			String siglaSub = entry.getKey();
			
			Map<Integer, List<String>> mapRelacao = entry.getValue();

			for (Map.Entry<Integer, List<String>> entry2 : mapRelacao.entrySet()) {
				
				int nrAlim = entry2.getKey();
				List<String> listaRelacao = entry2.getValue();
				
				File directory = new File("C:/teste_/dots/" + siglaSub);
				
				if (!directory.exists()) {
					directory.mkdir();
				}
				
				try {
					pw = MyUtils.createPrintWriter(directory + "/" + siglaSub + "_" + nrAlim + ".dot");
				} catch (IOException e) {
					e.printStackTrace();
					throw new IOException("ERRO. Não foi possível criar o arquivo no local: C:/teste_/dots/" + siglaSub + "/" + siglaSub + "_" + nrAlim + ".dot");
				}
			    
			    pw.println("graph {");
			    
			    for (int i = 0; i < listaRelacao.size(); i++) {
			    	pw.println("\t" + listaRelacao.get(i));
				}
			    
			    pw.println("}");
			    pw.close();
			    dotFiles.add(new File(directory + "/" + siglaSub + "_" + nrAlim + ".dot"));
				
			}
		    
		}

		return dotFiles;
	}

	
	
}
