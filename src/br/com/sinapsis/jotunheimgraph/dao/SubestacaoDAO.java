package br.com.sinapsis.jotunheimgraph.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.sinapsis.jotunheimgraph.to.Alimentador;

public class SubestacaoDAO {

	private static Logger logger = LogManager.getLogger();
	
	public Set<Alimentador> getRelacoesTrecho(String substationFilePath, String siglaSubestacao) throws SQLException {
		
		Connection conn = null;
		Set<Alimentador> setAlimentador = new HashSet<>();
		
		try {
			conn = ConnectionManager.getInstance().getConnection(substationFilePath);
			PreparedStatement pstm = conn.prepareStatement("SELECT id_rede_pri FROM TB_SUB_PRI WHERE id_rede_sub = (SELECT id_rede_sub FROM TB_REDE WHERE sigla LIKE '" + siglaSubestacao + "')");
			Alimentador alimentador = null;
			
			ResultSet rs = pstm.executeQuery();
			
			while (rs.next()) {
				alimentador = new Alimentador();
				alimentador.setCodigo(rs.getInt("id_rede_pri"));
				setAlimentador.add(alimentador);
			}
			
			rs.close();
			pstm.close();
			
			Set<String> setRelacoes = null;
			Set<Alimentador> alimentadoresVazios = new HashSet<>();
			
			for (Alimentador alim : setAlimentador) {
				
				setRelacoes = new HashSet<>();
				pstm = conn.prepareStatement("SELECT id_barra_1, id_barra_2 FROM TB_TRECHO WHERE id_rede = " + alim.getCodigo());
				rs = pstm.executeQuery();
				
				while (rs.next()) {
					String relacao = rs.getString("id_barra_1");
					relacao = relacao + " -> ";
					relacao = relacao + rs.getString("id_barra_2") + ";";
					setRelacoes.add(relacao);
				}
				
				rs.close();
				pstm.close();
				
				//extraindo as barras da tabela de chave (apenas as chaves que estao fechadas)
				
				pstm = conn.prepareStatement("SELECT id_barra_1, id_barra_2 FROM TB_CHAVE WHERE id_rede = " + alim.getCodigo() + " AND estado_atual = 'F'");
				rs = pstm.executeQuery();
				
				while (rs.next()) {
					String relacao = rs.getString("id_barra_1");
					relacao = relacao + " -> ";
					relacao = relacao + rs.getString("id_barra_2") + ";";
					
					if (setRelacoes.contains(relacao)) {
						logger.warn("relacao duplicada: alimentador [" + alim.getCodigo() + "] - relacao [" + relacao + "]");
						relacao = relacao.replace(";", "") + "[color=red,penwidth=1.0];";
						setRelacoes.add(relacao);
					} else {
						relacao = relacao.replace(";", "") + "[color=red,penwidth=1.0];";
						setRelacoes.add(relacao);
					}
				}
				
				rs.close();
				pstm.close();
				
				//extraindo as barras da tabela de regulador de tensao
				
				pstm = conn.prepareStatement("SELECT id_barra_1, id_barra_2 FROM TB_REGULADOR_TENSAO WHERE id_rede = " + alim.getCodigo());
				rs = pstm.executeQuery();
				
				while (rs.next()) {
					String relacao = rs.getString("id_barra_1");
					relacao = relacao + " -> ";
					relacao = relacao + rs.getString("id_barra_2") + ";";
					
					if (setRelacoes.contains(relacao)) {
						logger.warn("relacao duplicada: alimentador [" + alim.getCodigo() + "] - relacao [" + relacao + "]");
						relacao = relacao.replace(";", "") + "[color=green,penwidth=1.0];";
						setRelacoes.add(relacao);
					} else {
						relacao = relacao.replace(";", "") + "[color=green,penwidth=1.0];";
						setRelacoes.add(relacao);
					}
				}

				if (setRelacoes.isEmpty()) {
					alimentadoresVazios.add(alim);
				} else {
					alim.setRelacoes(setRelacoes);
				}

				rs.close();
				pstm.close();
			}
			
			setAlimentador.removeAll(alimentadoresVazios);
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new SQLException(e.getMessage(), e);
		}
		
		return setAlimentador;
	}
	
}
