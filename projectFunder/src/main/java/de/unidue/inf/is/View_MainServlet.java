package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.ViewProject;
import de.unidue.inf.is.utils.DBUtil;



public final class View_MainServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private static List<ViewProject> offen = new ArrayList<>();
	
	private static List<ViewProject> geschlossen = new ArrayList<>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
		Connection con = DBUtil.getExternalConnection();
		
		Statement st = con.createStatement();
		
		ResultSet rs = st.executeQuery("SELECT P.STATUS, P.TITEL , K.ICON , B.NAME , S.SPENDENSUMME "
				+ "FROM DBP068.PROJEKT AS P JOIN DBP068.KATEGORIE AS K ON P.KATEGORIE = K.ID "
				+ "JOIN DBP068.BENUTZER AS B ON P.ERSTELLER = B.EMAIL "
				+ "LEFT OUTER JOIN (SELECT PROJEKT , SUM(SPENDENBETRAG) AS SPENDENSUMME "
				+ "FROM DBP068.SPENDEN GROUP BY PROJEKT) AS S "
				+ "ON P.KENNUNG = S.PROJEKT");
		
		while(rs.next()) {
			
		    String status = rs.getString("STATUS");
			String titel = rs.getString("TITEL");
			String icon = rs.getString("ICON");
			String name = rs.getString("NAME");
			Integer spendensumme = rs.getInt("Spendensumme");			
			ViewProject vp = new ViewProject(titel,icon,name,spendensumme);

			if(status.equals("offen")) {

				synchronized(offen){
					offen.add(vp);
				}
				
			}else {
				
				synchronized(geschlossen){
					geschlossen.add(vp);
				}
				
			}				
		}
			
		con.close();
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("offene_Projekte", offen);
		req.setAttribute("geschlossene_Projekte", geschlossen);
		
		
		req.getRequestDispatcher("/view_main.ftl").forward(req, resp);
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		doGet(req,resp);
		
	}
	
}
