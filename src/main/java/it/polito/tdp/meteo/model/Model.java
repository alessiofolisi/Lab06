package it.polito.tdp.meteo.model;

import java.util.*;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	MeteoDAO dao = new MeteoDAO();
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> leCitta;
	private List<Citta> best;

	public Model() {
	
	}
	
	public List<Citta> getLeCitta(){
		leCitta = new ArrayList<>(dao.getCitta());
		
		return this.leCitta;
	}

	// of course you can change the String output with what you think works best
	public Double getUmiditaMedia(int mese , String localita) {
		return dao.getUmiditaMedia(mese, localita);
	}
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		
		List<Citta> parziale = new ArrayList<Citta>();
		this.best = null;
		
		for(Citta c : this.getLeCitta()) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		
		cerca(parziale , 0);
		
		return best;
	}
	
	public List<Citta> getCitta(){
		return dao.getCitta();
	}

	
	public void cerca(List<Citta> parziale , int L){
		
		if(L == NUMERO_GIORNI_TOTALI) {
			double costo = this.calcolaCosto(parziale);
			if(this.best==null || costo<this.calcolaCosto(best)) best = new ArrayList<>(parziale);
		}else {
			
			for(Citta prova : this.getLeCitta()) {
				if(this.sceltaValida(parziale, prova)) {
					
					parziale.add(prova);
					cerca(parziale,L+1);
					parziale.remove(parziale.size()-1);
					
				}
			}	
		}
	}
	
	public double calcolaCosto(List<Citta> parziale) {
		double costo=0;
		
		if(parziale.size()!=0) {
		//calcolo umidità senza spostamenti
		for(int i=1;i<=NUMERO_GIORNI_TOTALI;i++) {
			Citta c = parziale.get(i-1);
			costo += c.getRilevamenti().get(i-1).getUmidita();
		}}
		
		//aggiungo il costo degli spostamenti
		for(int i=2;i<=NUMERO_GIORNI_TOTALI;i++) {
			
			if(!parziale.get(i-1).equals(parziale.get(i-2))) {
				costo += COST;
			}
		}
		
		return costo;
	}
	
	public boolean sceltaValida(List<Citta> parziale , Citta prova) {
		
		
		int conta=0;
		for(Citta precedente : parziale) {
			if(precedente.equals(prova)) conta++;
		}
		
		if(conta>= NUMERO_GIORNI_CITTA_MAX) return false;
	
		if(parziale.size()==0) return true;
		
		if(parziale.size()==1 || parziale.size()==2) {
			if(prova.equals(parziale.get(parziale.size()-1))) return true;
			else return false;
		}
		
		if(parziale.get(parziale.size()-1).equals(prova)) return true;
		
		if(!parziale.get(parziale.size()-1).equals(prova) && parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && 
				parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3))) return true;
		
		
		return false;
	}

}
