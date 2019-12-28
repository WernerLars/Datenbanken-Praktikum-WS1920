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

import de.unidue.inf.is.domain.ViewProfileErstPro;
import de.unidue.inf.is.domain.ViewProfileUnPro;
import de.unidue.inf.is.utils.DBUtil;

public class View_ProfileServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private String query;
	private String email;
	private String name;
	private int anzErProjekte;
	private int anzUnProjekte;

	private static List<ViewProfileErstPro> erstpro = new ArrayList<>();
	private static List<ViewProfileUnPro> unpro = new ArrayList<>();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		query = req.getQueryString();
		
		if(query.substring(0, 10).equals("ersteller=")) {
			
			email = query.substring(10);
			anzErProjekte = 0;
			anzUnProjekte = 0;
			
			try {
				
				Connection con = DBUtil.getExternalConnection();
				
				PreparedStatement ps = con.prepareStatement("SELECT B.NAME FROM DBP068.BENUTZER AS B "
						+ "WHERE B.EMAIL = ?");
				ps.setString(1, email);
				ResultSet rs = ps.executeQuery();				
				while(rs.next()) {
					name = rs.getString("NAME");
				}
				
				ps = con.prepareStatement("SELECT K.ICON ,P.KENNUNG, P.TITEL , SP.SPENDENSUMME , P.STATUS "
						+ "FROM DBP068.PROJEKT AS P "
						+ "JOIN DBP068.KATEGORIE AS K "
						+ "ON P.KATEGORIE = K.ID "
						+ "LEFT OUTER JOIN (SELECT S.PROJEKT ,  SUM(S.SPENDENBETRAG) AS SPENDENSUMME "
						+ "FROM DBP068.SPENDEN AS S "
						+ "GROUP BY S.PROJEKT) AS SP "
						+ "ON P.KENNUNG = SP.PROJEKT "
						+ "WHERE P.ERSTELLER = ?");
				ps.setString(1, email);
				rs = ps.executeQuery();
				
				while(rs.next()) {
					
					anzErProjekte += 1;
					
					String icon = rs.getString("ICON");
					int kennung = rs.getInt("KENNUNG");
					String titel = rs.getString("TITEL");
					BigDecimal spendensumme = rs.getBigDecimal("SPENDENSUMME");
					if(spendensumme == null) {
						spendensumme = new BigDecimal("0");
					}
					String status = rs.getString("STATUS");
					
					ViewProfileErstPro vpep = new ViewProfileErstPro(icon,kennung,titel,spendensumme,status);
					erstpro.add(vpep);
				}
				
					ps = con.prepareStatement("SELECT K.ICON ,P.KENNUNG,  P.TITEL , P.FINANZIERUNGSLIMIT , P.STATUS ,"
							+ " SP.SPENDENBETRAG , SP.SICHTBARKEIT "
							+ "FROM (SELECT S.PROJEKT , S.SPENDENBETRAG , S.SICHTBARKEIT FROM DBP068.SPENDEN AS S "
							+ "WHERE S.SPENDER = ?) AS SP "
							+ "JOIN DBP068.PROJEKT AS P " 
							+ "ON SP.PROJEKT = P.KENNUNG "
							+ "JOIN DBP068.KATEGORIE AS K "
							+ "ON P.KATEGORIE = K.ID");
					ps.setString(1, email);
					rs = ps.executeQuery();
					
					while(rs.next()) {
						
						anzUnProjekte +=1;
						
						String sichtbarkeit = rs.getString("SICHTBARKEIT");
						if(sichtbarkeit.equals("oeffentlich")) {
						
							String icon = rs.getString("ICON");
							int kennung = rs.getInt("KENNUNG");
							String titel = rs.getString("TITEL");
							BigDecimal finlimit = rs.getBigDecimal("FINANZIERUNGSLIMIT");
							String status = rs.getString("STATUS");
							BigDecimal betrag = rs.getBigDecimal("SPENDENBETRAG");
						
							ViewProfileUnPro vpup = new ViewProfileUnPro(icon,kennung,titel,finlimit,status,betrag);
							unpro.add(vpup);
						}
					}
						
				
				con.close();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}			
			
			req.setAttribute("email", email);
			req.setAttribute("name", name);
			req.setAttribute("anzerprojekte", anzErProjekte);
			req.setAttribute("anzunprojekte", anzUnProjekte);
			
			req.setAttribute("erstpro", erstpro);
			req.setAttribute("unpro", unpro);
			
			req.getRequestDispatcher("./view_profile.ftl").forward(req, resp);
			
			erstpro.clear();
			unpro.clear();
		}
		
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		super.doGet(req, resp);
	}


}
