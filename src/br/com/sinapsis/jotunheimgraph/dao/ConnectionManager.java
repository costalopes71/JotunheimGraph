package br.com.sinapsis.jotunheimgraph.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConnectionManager {

	private static ConnectionManager instance;
	
	private ConnectionManager() throws ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");
		
	}
	
	public static ConnectionManager getInstance() throws ClassNotFoundException {
		
		try {
			if (instance == null) {
				instance = new ConnectionManager();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException("ERRO: Classe do driver JDBC não encontrada.");
		}
		return instance;
	}
	
	public Connection getConnection(String substationFilePath) throws SQLException {
		
		//paramentros da conexao
		try {
			return DriverManager.getConnection("jdbc:sqlite:" + substationFilePath);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException("Erro ao abrir a conexao com o banco de dados " + substationFilePath, e);
		}
		
	}
	
}
