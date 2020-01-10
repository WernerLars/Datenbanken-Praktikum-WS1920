package de.unidue.inf.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.ViewProjectCommend;
import de.unidue.inf.is.domain.ViewProjectDonator;
import de.unidue.inf.is.utils.DBUtil;

public class View_ProjectServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private String sqlGetProject = "SELECT P.KENNUNG, P.STATUS, P.BESCHREIBUNG, P.TITEL ,P.FINANZIERUNGSLIMIT, P.VORGAENGER , "
			+ "K.ICON ,B.EMAIL , B.NAME , S.SPENDENSUMME "
			+ "FROM DBP068.PROJEKT AS P JOIN DBP068.KATEGORIE AS K ON P.KATEGORIE = K.ID "
			+ "JOIN DBP068.BENUTZER AS B ON P.ERSTELLER = B.EMAIL "
			+ "LEFT OUTER JOIN (SELECT PROJEKT , SUM(SPENDENBETRAG) AS SPENDENSUMME "
			+ "FROM DBP068.SPENDEN GROUP BY PROJEKT) AS S "
			+ "ON P.KENNUNG = S.PROJEKT "
			+ "WHERE P.KENNUNG=?";
	
	private String sqlGetVorgaenger = "SELECT TITEL FROM DBP068.PROJEKT WHERE KENNUNG = ?";
	
	private String sqlGetDonations = "SELECT S.SPENDENBETRAG , S.SICHTBARKEIT , B.NAME "
			+ "FROM DBP068.SPENDEN AS S JOIN DBP068.BENUTZER AS B "
			+ "ON S.SPENDER = B.EMAIL "
			+ "WHERE S.PROJEKT = ? "
			+ "ORDER BY S.SPENDENBETRAG DESC";
	
	private String sqlGetCommends = "SELECT K.TEXT, K.SICHTBARKEIT , B.NAME "
			+ "FROM DBP068.SCHREIBT AS S JOIN DBP068.KOMMENTAR AS K "
			+ "ON S.KOMMENTAR = K.ID "
			+ "JOIN DBP068.BENUTZER AS B "
			+ "ON S.BENUTZER = B.EMAIL "
			+ "WHERE S.PROJEKT = ? "
			+ "ORDER BY DATUM DESC";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String queryKennung = req.getParameter("kennung");
		Integer kennung = 0;
		String status ="";
		String beschreibung="";
		String titel="";
		BigDecimal finanzierungslimit = new BigDecimal("0");
		String icon="";
		String email="";
		String ersteller="";
		BigDecimal spendensumme = new BigDecimal("0");
		Integer vorgaengerkennung = 0;
		String vorgaengertitel="";	
		String htmlcode="";	
		List<ViewProjectDonator> donators = new ArrayList<>();
		List<ViewProjectCommend> commends = new ArrayList<>();	
		String fehler = resp.getHeader("fehler");

		if(fehler == null) {
			fehler = " ";
		}

		if(queryKennung != null) {		
		
			try (Connection con = DBUtil.getExternalConnection();
					PreparedStatement getProject = con.prepareStatement(sqlGetProject);
					PreparedStatement getVorgaenger = con.prepareStatement(sqlGetVorgaenger);
					PreparedStatement getDonations = con.prepareStatement(sqlGetDonations);
					PreparedStatement getCommends = con.prepareStatement(sqlGetCommends)){
	
				getProject.setString(1, queryKennung);
				ResultSet rs = getProject.executeQuery();
		
				while(rs.next()) {
					status = rs.getString("STATUS");
					beschreibung = rs.getString("BESCHREIBUNG");
					titel = rs.getString("TITEL");
					finanzierungslimit = rs.getBigDecimal("FINANZIERUNGSLIMIT");
					icon = rs.getString("ICON");
					email = rs.getString("EMAIL");
					ersteller = rs.getString("NAME");
					spendensumme = rs.getBigDecimal("SPENDENSUMME");
					vorgaengerkennung = rs.getInt("VORGAENGER");
					kennung = rs.getInt("KENNUNG");
			
					if(spendensumme == null) {
						spendensumme = new BigDecimal("0");
					}
					if(beschreibung == null) {
						beschreibung = " ";
					}	
				}
		
				if(vorgaengerkennung != 0) {		
					getVorgaenger.setString(1, ""+vorgaengerkennung);	
					rs = getVorgaenger.executeQuery();
		
					while(rs.next()) {
						vorgaengertitel = rs.getString("TITEL");
					}
					
					htmlcode = "<a href=\"./view_project?kennung="+vorgaengerkennung+"\" target=\"_blank\">"+vorgaengertitel+"</a>";	
			
				}else {
					htmlcode = "Kein Vorgänger vorhanden";			
				}
		
				getDonations.setString(1, ""+kennung);
				rs = getDonations.executeQuery();
	
				while(rs.next()) {		
					BigDecimal spendenbetrag = rs.getBigDecimal("SPENDENBETRAG");
					String sichtbarkeit = rs.getString("SICHTBARKEIT");
					String name = rs.getString("NAME");
			
					if(sichtbarkeit.equals("privat")) {
						name = "Anonym";
					}
			
					ViewProjectDonator vs = new ViewProjectDonator(spendenbetrag,name);
				
					synchronized(donators) {
						donators.add(vs);
					}
				}
		
				getCommends.setString(1, ""+kennung);
				rs = getCommends.executeQuery();
		
				while(rs.next()) {			
					String text = rs.getString("TEXT");
					String sichtbarkeit = rs.getString("SICHTBARKEIT");
					String name = rs.getString("NAME");
			
					if(sichtbarkeit.equals("privat")) {
						name = "Anonym";
					}
			
					ViewProjectCommend vk = new ViewProjectCommend(name,text);
				
					synchronized(commends) {
						commends.add(vk);
					}
				}
		
			}catch(SQLException e) {
				e.printStackTrace();
			}
		
			req.setAttribute("status", status);
			req.setAttribute("beschreibung", beschreibung);
			req.setAttribute("titel", titel);
			req.setAttribute("finanzierungslimit", finanzierungslimit);
			req.setAttribute("icon", icon);
			req.setAttribute("email", email);
			req.setAttribute("ersteller", ersteller);
			req.setAttribute("spendensumme", spendensumme);
			req.setAttribute("kennung", kennung);	
			req.setAttribute("htmlcode", htmlcode);
			req.setAttribute("spender", donators);
			req.setAttribute("kommentare", commends);
			req.setAttribute("fehler", fehler);
		
			req.getRequestDispatcher("/view_project.ftl").forward(req, resp);
			
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
