package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;

public class View_ProfileServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private String query;
	private String email;
	private String name;
	private int anzErProjekte;
	private int anzUnProjekte;
	


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
				
				
				con.close();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			
					
			
			req.setAttribute("email", email);
			req.setAttribute("name", name);
			req.setAttribute("anzerprojekte", anzErProjekte);
			req.setAttribute("anzunprojekte", anzUnProjekte);
			req.getRequestDispatcher("./view_profile.ftl").forward(req, resp);
		}
		
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		super.doGet(req, resp);
	}


}
