package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;


public final class New_ProjectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String USER_ID = "dummy@dummy.com";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<Map<String, String>> projects = new ArrayList<Map<String, String>>();
		
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement psCategories = con.prepareStatement(
						"SELECT name FROM dbp068.Kategorie");
				PreparedStatement psProjects = con.prepareStatement(
						"SELECT kennung, titel FROM dbp068.projekt WHERE ersteller=?")
			) {
			ResultSet resCategories = psCategories.executeQuery();
			while (resCategories.next()) {
				categories.add(resCategories.getString("name"));
			}
			
			psProjects.setString(1, USER_ID);
			ResultSet resProjects = psProjects.executeQuery();
			while (resProjects.next()) {
				HashMap<String, String> p = new HashMap<String, String>();
				
				p.put("kennung", Integer.toString(resProjects.getInt("kennung")));
				p.put("titel", resProjects.getString("titel"));
				
				projects.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("categories", categories);
		req.setAttribute("projects", projects);
		
		req.getRequestDispatcher("new_project.ftl").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	
}