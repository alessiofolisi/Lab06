package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		List<Rilevamento> rilevamentiMese = new ArrayList<Rilevamento>();
		
		for(Rilevamento r : this.getAllRilevamenti()) {
			
			int meseScelto = r.getData().getMonth();
			
			if(meseScelto == mese && localita.compareTo(r.getLocalita())==0) {
				rilevamentiMese.add(r);
			}
		}
			
		return rilevamentiMese;
	}
	
	public double getUmiditaMedia(int mese , String localita) {
		
		double umidita=0;
		
		for(Rilevamento r : this.getAllRilevamentiLocalitaMese(mese, localita)) {
			umidita+=r.getUmidita();
		}
		
		return (umidita/this.getAllRilevamentiLocalitaMese(mese, localita).size());
		
	}

	
	public List<Citta> getCitta(){
		
		List<Citta> result = new ArrayList<Citta>();
		
		final String sql = "SELECT DISTINCT localita FROM situazione ORDER BY localita";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Citta c = new Citta(rs.getString("Localita"));
				result.add(c);
			}

			conn.close();
			return result;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		
	}

}
