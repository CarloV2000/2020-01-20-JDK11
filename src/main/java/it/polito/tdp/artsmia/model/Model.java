package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private List<String>allRoles;
	private ArtsmiaDAO dao;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	private List<Artist> allArtists;
	
	private List<Artist>migliore;
	private int dimMax;
	private Map<Integer, Artist> idMapArtists;
	
	public Model() {
		this.dao = new ArtsmiaDAO();
		this.allRoles = new ArrayList<>(dao.listRuoli());
		this.allArtists = new ArrayList<>();
		this.idMapArtists = new HashMap<>();
	}


	public String creaGrafo(String ruolo) {
	this.grafo = new SimpleWeightedGraph<Artist, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		this.allArtists = dao.listArtists(ruolo);
		Graphs.addAllVertices(grafo, this.allArtists);
		
		for(Artist a : this.allArtists) {
			this.idMapArtists.put(a.getId(), a);
		}
		
		for(Artist x : this.allArtists) {
			for(Artist y : this.allArtists) {
				/*Un arco collega due artisti solo se hanno esposto insieme (con il
				ruolo selezionato), cioè se le loro opere (“objects”)
				appaiono contemporaneamente in una delle possibili
				mostre (“exhibitions”). Il peso dell’arco è pari al numero di
				esposizioni in cui i due artisti hanno esposto almeno un
				oggetto insieme.*/
				if(!x.equals(y)) {
					int peso = dao.getWeight(x, y);
					if(peso >= 1) {
						grafo.addEdge(x, y);
						grafo.setEdgeWeight(x, y, peso);
					}
				}
			}	
		}
		return "Grafo creato con "+grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi.";
	}

	
	public List<CoppiaA> listArchi(){
		List<CoppiaA>archi = new ArrayList<>();
		for(DefaultWeightedEdge x : grafo.edgeSet()) {
			Artist r1 = grafo.getEdgeSource(x);
			Artist r2 = grafo.getEdgeTarget(x);
			int peso = (int)grafo.getEdgeWeight(x);
			
			CoppiaA arco = new CoppiaA(r1, r2, peso);
			archi.add(arco);
		}
		Collections.sort(archi);
		return archi;
	}
	
	/*	a. Permettere all’utente di inserire nella casella di testo “Artista (id)” il numero identificativo di un artista
			(“artist_id”).
	  	b. Alla pressione del bottone “Calcola Percorso”, si verifichi che il numero inserito sia corretto. In caso
			affermativo, si determini il cammino più lungo che parte dall’artista selezionato e che connette gli artisti con
			un ugual numero di esposizioni condivise. Precisamente deve essere trovato il cammino tra i vari artisti che
			comprenda solamente archi con ugual peso e che non comprenda cicli né vertici ripetuti.
		c. Si stampi il percorso così ottenuto, elencando gli artisti coinvolti ed il numero di esposizioni per cui il
			percorso risulta massimo.*/
	
	//metodo base per ricorsione
	
		public List<Artist>  calcolaPercorso(Integer artistID) {
			Artist a = this.idMapArtists.get(artistID);
			this.dimMax = 1;
			this.migliore = new ArrayList<Artist>();
			List<Artist> rimanenti = new ArrayList<>(this.grafo.vertexSet());
			List<Artist> parziale = new ArrayList<>();
			parziale.add(a);
			rimanenti.remove(a);
						
			ricorsione(1, parziale, rimanenti, 0);
			return migliore;
		}
		
		
		
		private void ricorsione(Integer liv, List<Artist> parziale, List<Artist> rimanenti, Integer pesoArco){
			
			
			if(parziale.size() == 2) {
				pesoArco = this.getPeso(parziale, parziale.get(parziale.size()-2));
			}
			// Condizione Terminale
			if (rimanenti.isEmpty()) {
				//calcolo costo
				Integer dimensione = parziale.size();
				if (dimensione>this.dimMax) {
					this.dimMax = dimensione;
					this.migliore = new ArrayList<>(parziale);
				}
				return;
			}
			
			
	       	for (Artist p : rimanenti) {
	       		if(this.getPeso(parziale, p) == pesoArco && !parziale.contains(p)) {
	 			List<Artist> currentRimanenti = new ArrayList<>(rimanenti);
	 				parziale.add(p);
	 				currentRimanenti.remove(p);
	 				ricorsione(liv+1, parziale, currentRimanenti, pesoArco);
	 				parziale.remove(parziale.size()-1);
	 			}
	 		}
			
		}
		
		
		
		private Integer getPeso(List<Artist> parziale, Artist daAggiungere) {
			DefaultWeightedEdge e = grafo.getEdge(parziale.get(parziale.size()-1), daAggiungere);
			Integer peso = 0;
			if(e!=null) {
				peso = (int) grafo.getEdgeWeight(e);
			}
			return peso;
		}

	public List<String> getAllRoles() {
		return allRoles;
	}

	public ArtsmiaDAO getDao() {
		return dao;
	}
	

	public Graph<Artist, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public List<Artist> getAllArtists() {
		return allArtists;
	}


	public List<Artist> getMigliore() {
		return migliore;
	}


	public int getDimMax() {
		return dimMax;
	}


	public Map<Integer, Artist> getIdMapArtists() {
		return idMapArtists;
	}
	
	
	
}
