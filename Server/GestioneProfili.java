import java.sql.ResultSet;
import java.sql.SQLException;




public class GestioneProfili {
	
	public static String getFunctions() throws SQLException{
		java.sql.Connection con=Connessione.getConnection();
		java.sql.Statement st = con.createStatement();
		//
		// Dal sistema di login recupero il mio idutente
		//
		String idprofilo = getProfilo()+"";
		ResultSet rs = st.executeQuery("select distinct idfunzionalita from contenutoprofilo where idprofilo="+idprofilo);
		String out="";
		while(rs.next()){
			out+=rs.getInt("idfunzionalita")+";";
		}
		if(out.length()>1)
			return out.substring(0, out.length()-1);
		else 
			return 0+"";
	}


 
	public static int getProfilo() throws SQLException{
		java.sql.Connection con=Connessione.getConnection();
		java.sql.Statement st = con.createStatement();

		// Dal sistema di login recupero il mio idutente
		
		String idutente = "utente0";
		ResultSet rs=st.executeQuery("select * from profiloutente where idutente='"+idutente+"'");
		rs.next();
		int id=rs.getInt("idprofilo");
		return id;
	}
	
	
	


}
