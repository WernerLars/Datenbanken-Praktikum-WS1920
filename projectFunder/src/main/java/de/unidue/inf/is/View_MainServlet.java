package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;

public final class View_MainServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
		Connection con = DBUtil.getExternalConnection();
		
		//String s = "SELECT ";
		
		//PreparedStatement ps = con.prepareStatement(s);
		
		
		
		
		
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		req.getRequestDispatcher("/view_main.ftl").forward(req, resp);
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		doGet(req,resp);
		
	}
	
}
