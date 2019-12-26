package de.unidue.inf.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.ViewProject;
import de.unidue.inf.is.utils.DBUtil;


public class View_ProjectServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private String query;

	private ViewProject vp;
	
	Integer kennung;
	Integer vorgaengerkennung;
	String vorgaengertitel;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		query = req.getQueryString();
		
		
		try {
		
		Connection con = DBUtil.getExternalConnection();
		
		Statement st = con.createStatement();
		
		ResultSet rs = st.executeQuery("SELECT P.KENNUNG, P.STATUS, P.BESCHREIBUNG, P.TITEL ,P.FINANZIERUNGSLIMIT, P.VORGAENGER , "
				+ "K.ICON , B.NAME , S.SPENDENSUMME "
				+ "FROM DBP068.PROJEKT AS P JOIN DBP068.KATEGORIE AS K ON P.KATEGORIE = K.ID "
				+ "JOIN DBP068.BENUTZER AS B ON P.ERSTELLER = B.EMAIL "
				+ "LEFT OUTER JOIN (SELECT PROJEKT , SUM(SPENDENBETRAG) AS SPENDENSUMME "
				+ "FROM DBP068.SPENDEN GROUP BY PROJEKT) AS S "
				+ "ON P.KENNUNG = S.PROJEKT "
				+ "WHERE P."+query);
		
		while(rs.next()) {
			String status = rs.getString("STATUS");
			String beschreibung = rs.getString("BESCHREIBUNG");
			String titel = rs.getString("TITEL");
			BigDecimal fl = rs.getBigDecimal("FINANZIERUNGSLIMIT");
			String icon = rs.getString("ICON");
			String ersteller = rs.getString("NAME");;
			BigDecimal spendensumme = rs.getBigDecimal("SPENDENSUMME");
			vorgaengerkennung = rs.getInt("VORGAENGER");
			kennung = rs.getInt("KENNUNG");
			
			if(spendensumme == null) {
				spendensumme = new BigDecimal("0");
			}
			
			
			vp = new ViewProject(status,beschreibung,titel,fl,icon,ersteller,spendensumme);
					

			}
		
		if(vorgaengerkennung != 0) {
			rs = st.executeQuery("SELECT TITEL FROM DBP068.PROJEKT WHERE KENNUNG ="+vorgaengerkennung);
		
			while(rs.next()) {
				vorgaengertitel = rs.getString("TITEL");
			}
		
		}else {
			vorgaengertitel = "Kein Vorgänger vorhanden";
			vorgaengerkennung = kennung;
		}
		
		
		
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("status", vp.getStatus());
		req.setAttribute("beschreibung", vp.getBeschreibung());
		req.setAttribute("titel", vp.getTitel());
		req.setAttribute("finanzierungslimit", vp.getFinanzierungslimit());
		req.setAttribute("icon", vp.getIcon());
		req.setAttribute("ersteller", vp.getErsteller());
		req.setAttribute("spendensumme", vp.getSpendensumme());
		
		req.setAttribute("vorgaengertitel", vorgaengertitel);
		req.setAttribute("vorgaengerkennung", vorgaengerkennung);

	
		
		req.getRequestDispatcher("/view_project.ftl").forward(req, resp);
			
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	
	

}
