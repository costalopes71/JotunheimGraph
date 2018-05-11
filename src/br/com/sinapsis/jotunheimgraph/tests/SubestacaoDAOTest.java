package br.com.sinapsis.jotunheimgraph.tests;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import br.com.sinapsis.jotunheimgraph.dao.SubestacaoDAO;
import br.com.sinapsis.jotunheimgraph.to.Alimentador;

public class SubestacaoDAOTest {
	
	private SubestacaoDAO dao;
	public static final Integer[] SET_VALUES = new Integer[] { 5004982, 5004007, 5003996, 5003994, 5003992, 5003986};
	public static final Set<Integer> CODIGOSALIMENTADORES = new HashSet<>(Arrays.asList(SET_VALUES));

	@Before
	public void setUp() {
		dao = new SubestacaoDAO();
	}
	
	
	@Test
	public void criaSetDeAlimentadorPorSubestacao() {
		
		Set<Alimentador> alimentadorSet = null;
		try {
			alimentadorSet = dao.getRelacoesBarras("TestFiles/Sub_SEN.es", "SEN");
		} catch (SQLException e) {
			fail(e.getMessage());
		}
		
		assertEquals(6, alimentadorSet.size());
		
		long totalRelacoesDaSubestacao = 0;
		for (Alimentador alimentador : alimentadorSet) {
			totalRelacoesDaSubestacao += alimentador.getRelacoes().size();
			
			if (!CODIGOSALIMENTADORES.contains(alimentador.getCodigo())) {
				assertTrue(false);
			}
			
		}
		
		assertEquals(2048, totalRelacoesDaSubestacao);
		
	}

}
