


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class OperationDB {


	private static final int REFRESH_CACHE = 2; //minuti

	/**
	 * 
	 * @param soccid
	 * @param type  0 soccerwiki    1 transfermarket   2 wikipedia  3 pes
	 * @return
	 */
	public static void insCache(int soccid, int type, String js){
		
		update("delete from cachejson where sccid="+soccid+" and type="+type);
		String sql = "INSERT INTO cachejson(sccid,json,type,datecreate) VALUES('"+soccid+"','"+js+"',"+type+",NOW())";
		update(sql);


	}
	public static ArrayList<String> getPlayerforclub(String club) throws SQLException{
		ResultSet rs =query("select * from giocatore where idsquadra = (select id from squadra where nome like '"+club+"')");
		ArrayList<String> al = new ArrayList<String>();
		while(rs.next()){
			al.add(rs.getString("nome")+" "+rs.getShort("cognome"));
		}
		return al;
	}
	public static String getCache(int soccid, int type ){

		String sql ="";
		String json="ERR";

		sql="SELECT json FROM cachejson WHERE sccid='"+soccid+"'  AND NOW() <= DATE_ADD(datecreate, INTERVAL "+REFRESH_CACHE+" MINUTE)"+" AND type="+type;
		try {
			ResultSet rs = query(sql);
			if(false){//rs.next()){
				System.out.println("CACHE");
				json=rs.getString("json");
			}else{
				System.out.println("NOT CACHE");
				json="NOTCACHE";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return json;

	}
	public static String getNazione(String nazione){
		String sql= "select pes from nazioni where nazione='"+nazione+"'";
		String ret="NO";
		try {
			ResultSet rs = query(sql);
			if(rs.next()){
				System.out.println("CACHE");
				ret= rs.getString("pes");
			}else{
				System.out.println("NOT CACHE");
				ret= "NO";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return ret;
	}


	private static ResultSet query(String sql) throws SQLException{
		Connection con = Connessione.getConnection();
		Statement st = con.createStatement();
		ResultSet res = st.executeQuery(sql);
		return res;
	}

	private static int update(String sql){
		int res=0;
		try{
			Connection con = Connessione.getConnection();
			Statement st = con.createStatement();


			res = st.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if(e.getErrorCode()==1062)
				res=-1;
			else	
				System.out.println("ERRORE UPDATEDB ERR SQLEXCEPTION:  "+e.getErrorCode()+e.getMessage());
		} 
		return res;

	}
}
