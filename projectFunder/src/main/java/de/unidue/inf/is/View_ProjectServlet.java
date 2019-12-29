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

import de.unidue.inf.is.domain.ViewKommentar;
import de.unidue.inf.is.domain.ViewProject;
import de.unidue.inf.is.domain.ViewSpender;
import de.unidue.inf.is.utils.DBUtil;


public class View_ProjectServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private String query;

	private ViewProject vp;
	
	private Integer kennung;
	private Integer vorgaengerkennung;
	private String vorgaengertitel;
	private String code;
	
	private static List<ViewSpender> spender = new ArrayList<>();
	private static List<ViewKommentar> kommentare = new ArrayList<>();
	
	private String fehler;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		query = req.getParameter("kennung");
		
		fehler = resp.getHeader("fehler");
		
		if(fehler == null) {
			fehler = "";
		}

		if(query != null) {		
		
		try {
		
		Connection con = DBUtil.getExternalConnection();
		
		PreparedStatement ps = con.prepareStatement("SELECT P.KENNUNG, P.STATUS, P.BESCHREIBUNG, P.TITEL ,P.FINANZIERUNGSLIMIT, P.VORGAENGER , "
				+ "K.ICON ,B.EMAIL , B.NAME , S.SPENDENSUMME "
				+ "FROM DBP068.PROJEKT AS P JOIN DBP068.KATEGORIE AS K ON P.KATEGORIE = K.ID "
				+ "JOIN DBP068.BENUTZER AS B ON P.ERSTELLER = B.EMAIL "
				+ "LEFT OUTER JOIN (SELECT PROJEKT , SUM(SPENDENBETRAG) AS SPENDENSUMME "
				+ "FROM DBP068.SPENDEN GROUP BY PROJEKT) AS S "
				+ "ON P.KENNUNG = S.PROJEKT "
				+ "WHERE P.KENNUNG=?");
		
		ps.setString(1, query);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			String status = rs.getString("STATUS");
			String beschreibung = rs.getString("BESCHREIBUNG");
			String titel = rs.getString("TITEL");
			BigDecimal fl = rs.getBigDecimal("FINANZIERUNGSLIMIT");
			String icon = rs.getString("ICON");
			String email = rs.getString("EMAIL");
			String ersteller = rs.getString("NAME");
			BigDecimal spendensumme = rs.getBigDecimal("SPENDENSUMME");
			vorgaengerkennung = rs.getInt("VORGAENGER");
			kennung = rs.getInt("KENNUNG");
			
			if(spendensumme == null) {
				spendensumme = new BigDecimal("0");
			}
			if(beschreibung == null) {
				beschreibung = " ";
			}
			
			
			vp = new ViewProject(status,beschreibung,titel,fl,icon,email,ersteller,spendensumme);
					

			}
		
		if(vorgaengerkennung != 0) {
			
			ps = con.prepareStatement("SELECT TITEL FROM DBP068.PROJEKT WHERE KENNUNG = ?");
			
			ps.setString(1, ""+vorgaengerkennung);
			
			rs = ps.executeQuery();
		
			while(rs.next()) {
				vorgaengertitel = rs.getString("TITEL");
			}
			
			code = "<a href=\"./view_project?kennung="+vorgaengerkennung+"\" target=\"_blank\">"+vorgaengertitel+"</a>";
			
		}else {

			code = "Kein Vorgänger vorhanden";		
			
		}
		
		ps = con.prepareStatement("SELECT S.SPENDENBETRAG , S.SICHTBARKEIT , B.NAME "
				+ "FROM DBP068.SPENDEN AS S JOIN DBP068.BENUTZER AS B "
				+ "ON S.SPENDER = B.EMAIL "
				+ "WHERE S.PROJEKT = ? "
				+ "ORDER BY S.SPENDENBETRAG DESC");
		
		ps.setString(1, ""+kennung);
		
		rs = ps.executeQuery();
		
		
		while(rs.next()) {
			
			
			BigDecimal spendenbetrag = rs.getBigDecimal("SPENDENBETRAG");
			String sichtbarkeit = rs.getString("SICHTBARKEIT");
			String name = rs.getString("NAME");
			
			if(sichtbarkeit.equals("privat")) {
				name = "Anonym";
			}
			
			ViewSpender vs = new ViewSpender(spendenbetrag,name);
			
			spender.add(vs);		
		}
		
		ps = con.prepareStatement("SELECT K.TEXT, K.SICHTBARKEIT , B.NAME "
				+ "FROM DBP068.SCHREIBT AS S JOIN DBP068.KOMMENTAR AS K "
				+ "ON S.KOMMENTAR = K.ID "
				+ "JOIN DBP068.BENUTZER AS B "
				+ "ON S.BENUTZER = B.EMAIL "
				+ "WHERE S.PROJEKT = ?");
		
		ps.setString(1, ""+kennung);
		
		rs = ps.executeQuery();
		
		while(rs.next()) {
			
			String text = rs.getString("TEXT");
			String sichtbarkeit = rs.getString("SICHTBARKEIT");
			String name = rs.getString("NAME");
			
			if(sichtbarkeit.equals("privat")) {
				name = "Anonym";
			}
			
			ViewKommentar vk = new ViewKommentar(text,name);
			
			kommentare.add(vk);
			
		}
		
		con.close();
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("status", vp.getStatus());
		req.setAttribute("beschreibung", vp.getBeschreibung());
		req.setAttribute("titel", vp.getTitel());
		req.setAttribute("finanzierungslimit", vp.getFinanzierungslimit());
		req.setAttribute("icon", vp.getIcon());
		req.setAttribute("email", vp.getEmail());
		req.setAttribute("ersteller", vp.getErsteller());
		req.setAttribute("spendensumme", vp.getSpendensumme());
		req.setAttribute("kennung", kennung);
		
		req.setAttribute("code", code);

		req.setAttribute("spender", spender);
		req.setAttribute("kommentare", kommentare);

		req.setAttribute("fehler", fehler);
		
		req.getRequestDispatcher("/view_project.ftl").forward(req, resp);
		
		spender.clear();
		kommentare.clear();
			
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		super.doGet(req, resp);
	}
	
	

}
