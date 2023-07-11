package it.polito.tdp.artsmia;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.CoppiaA;
import it.polito.tdp.artsmia.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ArtsmiaController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnArtistiConnessi;

    @FXML
    private Button btnCalcolaPercorso;

    @FXML
    private ComboBox<String> boxRuolo;

    @FXML
    private TextField txtArtista;

    @FXML
    private TextArea txtResult;

    @FXML
    void doArtistiConnessi(ActionEvent event) {
    	for(CoppiaA x : model.listArchi()) {
    		this.txtResult.appendText("\n"+x.getA1().getId()+" "+x.getA1().getName()+"<->"+x.getA2().getId()+" "+x.getA2().getName()+"_"+x.getPeso());
    	}
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	String artista = this.txtArtista.getText();
    	Integer artistID;
    	try {
    		artistID = Integer.parseInt(artista);
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Inserire un artistID! ");
    		return;
    	}
    	List<Integer>listaID = new ArrayList<>();
    	for(Artist x : model.getGrafo().vertexSet()) {
    		listaID.add(x.getId());
    	}
    	if(!listaID.contains(artistID)) {
    		this.txtResult.setText("Inserire un artistID corrispondente ad un artista!");
    		return;
    	}
    	
    	List<Artist>migliore = new ArrayList<>(model.calcolaPercorso(artistID));
    	Integer nMax = migliore.size();
    	String s = "Percorso migliore trovato :";
    	if(migliore.isEmpty()) {
    		this.txtResult.setText("No path found");
    		return;
    	}
    	for(Artist x : migliore) {
    		s += "\n"+x.getId()+" "+x.getName();
    	}
    	this.txtResult.setText(s);
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	String ruolo = this.boxRuolo.getValue();
    	if(ruolo == null) {
    		this.txtResult.setText("Selezionare un ruolo! ");
    		return;
    	}
    	String s = model.creaGrafo(ruolo);
    	this.txtResult.setText(s);
    		
    }

    public void setModel(Model model) {
    	this.model = model;
    	for(String x : model.getAllRoles()) {
    		this.boxRuolo.getItems().add(x);
    	}
    }

    
    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnArtistiConnessi != null : "fx:id=\"btnArtistiConnessi\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert boxRuolo != null : "fx:id=\"boxRuolo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtArtista != null : "fx:id=\"txtArtista\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Artsmia.fxml'.";

    }
}
