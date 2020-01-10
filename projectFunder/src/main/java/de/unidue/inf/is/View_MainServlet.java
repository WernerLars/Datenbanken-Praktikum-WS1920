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

import de.unidue.inf.is.domain.ViewMainProject;
import de.unidue.inf.is.utils.DBUtil;

public final class View_MainServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private String sqlGetProject = "SELECT P.STATUS, P.KENNUNG, P.TITEL , K.ICON ,B.EMAIL , B.NAME , S.SPENDENSUMME "
			+ "FROM DBP068.PROJEKT AS P JOIN DBP068.KATEGORIE AS K ON P.KATEGORIE = K.ID "
			+ "JOIN DBP068.BENUTZER AS B ON P.ERSTELLER = B.EMAIL "
			+ "LEFT OUTER JOIN (SELECT PROJEKT , SUM(SPENDENBETRAG) AS SPENDENSUMME "
			+ "FROM DBP068.SPENDEN GROUP BY PROJEKT) AS S "
			+ "ON P.KENNUNG = S.PROJEKT "
			+ "ORDER BY P.KENNUNG";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		List<ViewMainProject> openProjects = new ArrayList<>();	
		List<ViewMainProject> closedProjects = new ArrayList<>();
		
		try (Connection con = DBUtil.getExternalConnection();
			 PreparedStatement ps = con.prepareStatement(sqlGetProject)){
		
			ResultSet rs = ps.executeQuery();
		
			while(rs.next()) {
				String status = rs.getString("STATUS");
				int kennung = rs.getInt("KENNUNG");
				String titel = rs.getString("TITEL");
				String icon = rs.getString("ICON");
				String email = rs.getString("EMAIL");
				String name = rs.getString("NAME");
				
				BigDecimal spendensumme = rs.getBigDecimal("SPENDENSUMME");			
				if(spendensumme == null) {
					spendensumme = new BigDecimal("0");
				}
				
				ViewMainProject vp = new ViewMainProject(kennung,titel,icon,email,name,spendensumme);

				if(status.equals("offen")) {
					synchronized(openProjects){
						openProjects.add(vp);
					}				
				}else {			
					synchronized(closedProjects){
						closedProjects.add(vp);
					}			
				}				
			}

		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		req.setAttribute("openProjects", openProjects);
		req.setAttribute("closedProjects", closedProjects);

		req.getRequestDispatcher("/view_main.ftl").forward(req, resp);	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		doGet(req,resp);
		
	}
	
}
