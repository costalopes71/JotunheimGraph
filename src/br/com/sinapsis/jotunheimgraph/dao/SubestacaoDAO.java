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
import br.com.sinapsis.jotunheimgraph.utils.Constants;

/**
 * Classe responsável por fazer a conexao com o arquivo de banco de dados SQLLite e executar as intrucoes SQLs.
 * @author Joao Lopes
 * @since 09/05/2018
 * @see br.com.sinapsis.jotunheimgraph.tests.SubestaoDAOTestCase;
 */
public class SubestacaoDAO {

	private static Logger logger = LogManager.getLogger(Constants.Loggers.CONSOLE);
	private PreparedStatement pstm;
	private ResultSet rs;
	
	/**
	 * Método responsável por buscar todas as relacoes de barras de uma determinada subestacao. 
	 * @param String substationFilePath, uma <code>String</code> contendo o caminho do arquivo SQLLite da subestacao.
	 * @param String siglaSubestacao, a sigla da subestacao que se quer extrair as relacoes.
	 * @return Set<Alimentador>, um Set contendo todos os alimentadores daquela subestacao, em cada alimentador estara o Set de relacoes de barras.
	 * @throws SQLException
	 */
	public Set<Alimentador> getRelacoesBarras(String substationFilePath, String siglaSubestacao) throws SQLException {
		
		Connection conn = null;
		Set<Alimentador> setAlimentador = new HashSet<>();
		
		try {
			conn = ConnectionManager.getInstance().getConnection(substationFilePath);
			Set<String> setRelacoes = null;
			Set<Alimentador> alimentadoresVazios = new HashSet<>();
			
			obterAlimentadoresDaSubestacao(siglaSubestacao, conn, setAlimentador);
			
			for (Alimentador alim : setAlimentador) {
				
				// extraindo as relacoes de barras da tabela trecho
				setRelacoes = pesquisaRelacoesTabelaTrecho(conn, alim);
				
				//extraindo as barras da tabela de chave (apenas as chaves que estao fechadas)
				pesquisaRelacoesTabelaChave(conn, setRelacoes, alim);
				
				//extraindo as barras da tabela de regulador de tensao
				pesquisaRelacoesTabelaReguladorTensao(conn, setRelacoes, alim);

				// se o alimentador nao tiver nenhuma relacao adiciona o alimentador a lista de alimentadores vazios
				if (setRelacoes.isEmpty()) {
					alimentadoresVazios.add(alim);
				} else {
					alim.setRelacoes(setRelacoes);
				}

			}
			
			//exclui os alimentadores que nao tem relacoes do Set de alimentadores
			setAlimentador.removeAll(alimentadoresVazios);
			
		} catch (ClassNotFoundException | SQLException e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			conn.close();
		}
		
		return setAlimentador;
	}

	private void pesquisaRelacoesTabelaReguladorTensao(Connection conn, Set<String> setRelacoes,
			Alimentador alim) throws SQLException {
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
		rs.close();
		pstm.close();
	}

	private void pesquisaRelacoesTabelaChave(Connection conn, Set<String> setRelacoes, Alimentador alim)
			throws SQLException {
		pstm = conn.prepareStatement("SELECT id_barra_1, id_barra_2 FROM TB_CHAVE WHERE id_rede = " + alim.getCodigo() + " AND (estado_atual = 'F' OR estado_atual LIKE 'NULL')");
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
	}

	private Set<String> pesquisaRelacoesTabelaTrecho(Connection conn, Alimentador alim) throws SQLException {
		
		Set<String> setRelacoes;
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
		return setRelacoes;
	}

	private void obterAlimentadoresDaSubestacao(String siglaSubestacao, Connection conn, Set<Alimentador> setAlimentador) throws SQLException {
		Alimentador alimentador = null;
		pstm = conn.prepareStatement("SELECT id_rede_pri FROM TB_SUB_PRI WHERE id_rede_sub = (SELECT id_rede_sub FROM TB_REDE WHERE sigla LIKE '" + siglaSubestacao + "')");
		rs = pstm.executeQuery();
		
		while (rs.next()) {
			alimentador = new Alimentador();
			alimentador.setCodigo(rs.getInt("id_rede_pri"));
			setAlimentador.add(alimentador);
		}
		
		rs.close();
		pstm.close();
	}
	
}
