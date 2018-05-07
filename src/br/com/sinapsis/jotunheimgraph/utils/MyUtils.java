package br.com.sinapsis.jotunheimgraph.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class MyUtils {

	public static List<File> unzipFiles(String zipFilePath, String outputFolder) throws IOException {
		
		System.out.println("===========================================================");
		System.out.println("====== Iniciando a descompactação do arquivo ZIP! =D ======");
		System.out.println("===========================================================");
		List<File> filesList = new ArrayList<>();
		
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
			zis = new ZipInputStream(new FileInputStream(zipFilePath));
			ze = zis.getNextEntry();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Erro ao tentar acessar o conteudo do arquivo ZIP. Por favor certifique-se que o arquivo ou o caminho realmente existem. =( ");
		}
		
		while (ze != null) {
			
			String fileName = ze.getName();
			File newFile = new File(outputFolder + File.separator + fileName);
			
			System.out.println("[" + count + "] arquivo descompactado - " + newFile.getAbsolutePath());
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

	public static PrintWriter createPrintWriter(String dotPath) throws IOException {

		FileWriter outputStream;

		try {
			outputStream = new FileWriter(dotPath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("ERROR - Unable to create or open the new CSV file (ordered csv) in this path: " + dotPath);
		}
		return new PrintWriter(outputStream);
	}
	
}
