package br.com.sinapsis.jotunheimgraph.to;

import java.util.Set;

public class Alimentador {

	private int codigo;
	private Set<String> relacoes;
	
	public Alimentador() { }
	
	public Alimentador(int codigo, Set<String> relacoes) {
		super();
		this.codigo = codigo;
		this.relacoes = relacoes;
	}

	public int getCodigo() {
		return codigo;
	}
	
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	
	public Set<String> getRelacoes() {
		return relacoes;
	}
	
	public void setRelacoes(Set<String> relacoes) {
		this.relacoes = relacoes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alimentador other = (Alimentador) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}
	
}
