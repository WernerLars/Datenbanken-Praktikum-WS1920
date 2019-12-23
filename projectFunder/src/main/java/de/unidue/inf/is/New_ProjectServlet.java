package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;


public final class New_ProjectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ArrayList<String> categories = new ArrayList<String>();
		
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement ps = con.prepareStatement("SELECT name FROM dbp068.Kategorie")) {
			ResultSet res = ps.executeQuery();
			while (res.next()) {
				categories.add(res.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("categories", categories);
		
		req.getRequestDispatcher("new_project.ftl").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	
}