package br.com.sinapsis.jotunheimgraph.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubestacaoDAO {

	public Map<Integer, List<String>> getRelacoesTrecho(String substationFilePath, String siglaSubestacao) throws SQLException {
		
		Connection conn = null;
		Map<Integer, List<String>> map = new HashMap<>();
		Map<Integer, List<String>> resultMap = new HashMap<>();
		
		try {
			conn = ConnectionManager.getInstance().getConnection(substationFilePath);
			PreparedStatement pstm = conn.prepareStatement("SELECT id_rede_pri FROM TB_SUB_PRI WHERE id_rede_sub = (SELECT id_rede_sub FROM TB_SUBESTACAO WHERE descricao LIKE '%-" + siglaSubestacao + "%')");
			
			ResultSet rs = pstm.executeQuery();
			
			Integer numeroAlimentador = null;
			while (rs.next()) {
				numeroAlimentador = rs.getInt("id_rede_pri");
				map.put(numeroAlimentador, null);
			}
			
			rs.close();
			pstm.close();
			
			List<String> listaRelacoes = null;
			for (Integer nrAlim : map.keySet()) {
				
				listaRelacoes = new ArrayList<>();
				pstm = conn.prepareStatement("SELECT id_barra_1, id_barra_2 FROM TB_TRECHO WHERE id_rede = " + nrAlim);
				rs = pstm.executeQuery();
				
				while (rs.next()) {
					String relacao = rs.getString("id_barra_1");
					relacao = relacao + " -- ";
					relacao = relacao + rs.getString("id_barra_2") + ";";
					listaRelacoes.add(relacao);
				}
				
				resultMap.put(nrAlim, listaRelacoes);
				rs.close();
				pstm.close();
			}
			
			map.clear();
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new SQLException(e.getMessage(), e);
		}
		
		return resultMap;
	}
	
}
