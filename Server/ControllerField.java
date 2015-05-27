import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ControllerField {
	private ArrayList<Integer> alout;

	public ControllerField(int idfunzionalita) throws SQLException{
		java.sql.Connection con=Connessione.getConnection();
		java.sql.Statement st = con.createStatement();
		//
		// Dal sistema di login recupero il mio idutente
		//
		String idprofilo = GestioneProfili.getProfilo()+"";
		
		
		ResultSet rs = st.executeQuery("select count(*) , idcontenuto from contenutoprofilo where idprofilo="+idprofilo+" and idfunzionalita="+idfunzionalita);
		rs.next();
		alout = new ArrayList<Integer>();
		while(rs.next()){
			alout.add(rs.getInt("idcontenuto"));
		}
		
	}
	
	public String getField(int idcontenuto,String contenuto ){
		/*
		if(alout.indexOf(idcontenuto)>=0)
			return contenuto;
		else
			return "x";
		*/
		return contenuto;
		
	}

}
