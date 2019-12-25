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

import java.util.ArrayList;
import java.util.List;

public class View_ProjectServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private String query;

	private List<ViewProject> project = new ArrayList<>();
	
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		query = req.getQueryString();
		
		try {
		
		Connection con = DBUtil.getExternalConnection();
		
		Statement st = con.createStatement();
		
		ResultSet rs = st.executeQuery("SELECT P.STATUS, P.BESCHREIBUNG, P.TITEL ,P.FINANZIERUNGSLIMIT, K.ICON , B.NAME , S.SPENDENSUMME "
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
			
			if(spendensumme == null) {
				spendensumme = new BigDecimal("0");
			}
			

			ViewProject vp = new ViewProject(status,beschreibung,titel,fl,icon,ersteller,spendensumme);
			
			project.add(vp);
		
		}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("project", project);
		req.getRequestDispatcher("/view_project.ftl").forward(req, resp);
			
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	
	

}
