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

import de.unidue.inf.is.domain.ViewProfileCreatedProject;
import de.unidue.inf.is.domain.ViewProfileSupportedProjects;
import de.unidue.inf.is.utils.DBUtil;

public class View_ProfileServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private String sqlGetName = "SELECT B.NAME FROM DBP068.BENUTZER AS B "
			+ "WHERE B.EMAIL = ?";
	
	private String sqlGetCreatedProjects = "SELECT K.ICON ,P.KENNUNG, P.TITEL , SP.SPENDENSUMME , P.STATUS "
			+ "FROM DBP068.PROJEKT AS P "
			+ "JOIN DBP068.KATEGORIE AS K "
			+ "ON P.KATEGORIE = K.ID "
			+ "LEFT OUTER JOIN (SELECT S.PROJEKT ,  SUM(S.SPENDENBETRAG) AS SPENDENSUMME "
			+ "FROM DBP068.SPENDEN AS S "
			+ "GROUP BY S.PROJEKT) AS SP "
			+ "ON P.KENNUNG = SP.PROJEKT "
			+ "WHERE P.ERSTELLER = ?";
	
	private String sqlGetSupportedProjects = "SELECT K.ICON ,P.KENNUNG,  P.TITEL , P.FINANZIERUNGSLIMIT , P.STATUS ,"
			+ " SP.SPENDENBETRAG , SP.SICHTBARKEIT "
			+ "FROM (SELECT S.PROJEKT , S.SPENDENBETRAG , S.SICHTBARKEIT FROM DBP068.SPENDEN AS S "
			+ "WHERE S.SPENDER = ?) AS SP "
			+ "JOIN DBP068.PROJEKT AS P " 
			+ "ON SP.PROJEKT = P.KENNUNG "
			+ "JOIN DBP068.KATEGORIE AS K "
			+ "ON P.KATEGORIE = K.ID";
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String email = req.getParameter("ersteller");
		String name = " ";
		int numberCreatedProjects = 0;
		int numberSupportedProjects = 0;		
		List<ViewProfileCreatedProject> createdProjects = new ArrayList<>();
		List<ViewProfileSupportedProjects> supportedProjects = new ArrayList<>();

		try(Connection con = DBUtil.getExternalConnection();
			 PreparedStatement getName = con.prepareStatement(sqlGetName);
			 PreparedStatement getCreatedProjects = con.prepareStatement(sqlGetCreatedProjects);
			 PreparedStatement getSupportedProjects = con.prepareStatement(sqlGetSupportedProjects)) {
			
			getName.setString(1, email);
			ResultSet rs = getName.executeQuery();	
		
			while(rs.next()) {
				name = rs.getString("NAME");
			}
				
			getCreatedProjects.setString(1, email);
			rs = getCreatedProjects.executeQuery();		
				
			while(rs.next()) {			
				numberCreatedProjects += 1;				
				String icon = rs.getString("ICON");
				int kennung = rs.getInt("KENNUNG");
				String titel = rs.getString("TITEL");
				BigDecimal spendensumme = rs.getBigDecimal("SPENDENSUMME");
				
				if(spendensumme == null) {
					spendensumme = new BigDecimal("0");
				}
				
				String status = rs.getString("STATUS");
				ViewProfileCreatedProject vpcp = new ViewProfileCreatedProject(icon,kennung,titel,spendensumme,status);
				
				synchronized(createdProjects) {
					createdProjects.add(vpcp);
				}
			}
		
			getSupportedProjects.setString(1, email);
			rs = getSupportedProjects.executeQuery();
					
			while(rs.next()) {					
				numberSupportedProjects +=1;
				String sichtbarkeit = rs.getString("SICHTBARKEIT");
				
				if(sichtbarkeit.equals("oeffentlich")) {
					String icon = rs.getString("ICON");
					int kennung = rs.getInt("KENNUNG");
					String titel = rs.getString("TITEL");
					BigDecimal finlimit = rs.getBigDecimal("FINANZIERUNGSLIMIT");
					String status = rs.getString("STATUS");
					BigDecimal betrag = rs.getBigDecimal("SPENDENBETRAG");
					ViewProfileSupportedProjects vpsp = new ViewProfileSupportedProjects(icon,kennung,titel,finlimit,status,betrag);
					
					synchronized(supportedProjects) {
							supportedProjects.add(vpsp);
					}
				}	
			}
						
		}catch(SQLException e) {
			e.printStackTrace();
		}			
			
		req.setAttribute("email", email);
		req.setAttribute("name", name);
		req.setAttribute("numberCreatedProjects", numberCreatedProjects);
		req.setAttribute("numberSupportedProjects", numberSupportedProjects);			
		req.setAttribute("createdProjects", createdProjects);
		req.setAttribute("supportedProjects", supportedProjects);
			
		req.getRequestDispatcher("./view_profile.ftl").forward(req, resp);
			
	}		

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
