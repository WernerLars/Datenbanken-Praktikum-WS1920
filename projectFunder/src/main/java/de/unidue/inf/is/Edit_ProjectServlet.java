package de.unidue.inf.is;

import java.io.IOException;
import java.math.BigDecimal;
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

public class Edit_ProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String USER_ID = "dummy@dummy.com";
	
	private String sqlTarget = "SELECT titel, beschreibung, finanzierungslimit, ersteller, vorgaenger, kategorie FROM dbp068.projekt WHERE kennung=?";
	private String sqlPred = "SELECT kennung, titel FROM dbp068.projekt WHERE ersteller=?";
	private String sqlCategories = "SELECT id, name FROM dbp068.kategorie";
	private String sqlProjectExists = "SELECT count(*) AS project_exists FROM dbp068.projekt WHERE kennung=?";
	private String sqlCreator = "SELECT count(*) AS valid FROM dbp068.projekt WHERE kennung=? AND ersteller=?";
	
	protected void showPage(HttpServletRequest req, HttpServletResponse resp, String errorMsg) throws ServletException, IOException {
		int pid;
		if (req.getParameter("kennung") != null) {
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement ps = con.prepareStatement(sqlProjectExists)) {
				pid = Integer.valueOf(req.getParameter("kennung"));
				
				ps.setInt(1, pid);
				ResultSet rs = ps.executeQuery();
				rs.next();
				if (rs.getInt("project_exists") != 1) {
					resp.sendError(404, "Ungülige Projektkennung angegeben.");
					return;
				}
			} catch (SQLException e) {
				resp.sendError(500, "Datenbankfehler: " + e.getMessage());
				e.printStackTrace();
				return;
			} catch (NumberFormatException e) {
				resp.sendError(404, "Ungülige Projektkennung angegeben.");
				return;
			}
		} else {
			resp.sendError(404, "Keine Projektkennung angegeben.");
			return;
		}
		
		HashMap<String, String> project = new HashMap<String, String>();
		ArrayList<Map<String, String>> categories = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> preds = new ArrayList<Map<String, String>>();
		
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement psTarget = con.prepareStatement(sqlTarget);
				PreparedStatement psCategories = con.prepareStatement(sqlCategories);
				PreparedStatement psProjects = con.prepareStatement(sqlPred)) {
			
			// Get the selected project details
			psTarget.setInt(1, pid);
			ResultSet rs = psTarget.executeQuery();
			rs.next();
			project.put("kennung", Integer.toString(pid));
			project.put("titel", rs.getString("titel"));
			project.put("beschreibung", rs.getString("beschreibung"));
			project.put("finanzierungslimit", rs.getBigDecimal("finanzierungslimit").toString());
			project.put("ersteller", rs.getString("ersteller"));
			project.put("vorgaenger", Integer.toString(rs.getInt("vorgaenger")));
			project.put("kategorie", Integer.toString(rs.getInt("kategorie")));
			
			/* Get a list of all categories for the logged in user USER_ID and add them to a
			 * list of maps with the following keys:
			 * "id": Unique id of each category
			 * "name": Name of each category
			 */
			ResultSet resCategories = psCategories.executeQuery();
			while (resCategories.next()) {
				HashMap<String, String> c = new HashMap<String, String>();
				
				c.put("id", Integer.toString(resCategories.getInt("id")));
				c.put("name", resCategories.getString("name"));
				c.put("checked", project.get("kategorie").equals(c.get("id")) ? "checked" : "");
				
				categories.add(c);
			}
			
			/* Get all projects of the logged in user USER_ID and add them to a list of maps
			 * with the following keys:
			 * "kennung": Unique id of each project
			 * "titel": Title of each project
			 */
			psProjects.setString(1, USER_ID);
			ResultSet resProjects = psProjects.executeQuery();
			while (resProjects.next()) {
				HashMap<String, String> p = new HashMap<String, String>();
				
				p.put("kennung", Integer.toString(resProjects.getInt("kennung")));
				p.put("titel", resProjects.getString("titel"));
				p.put("checked", project.get("vorgaenger").equals(p.get("kennung")) ? "checked" : "");
				
				preds.add(p);
			}
		} catch (SQLException e) {
			resp.sendError(500, "Datenbankfehler: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		req.setAttribute("errorMsg", errorMsg.equals("") ? "" : "Fehler:<br>" + errorMsg);
		req.setAttribute("project", project);
		req.setAttribute("categories", categories);
		req.setAttribute("preds", preds);
		
		req.getRequestDispatcher("edit_project.ftl").forward(req, resp);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		showPage(req, resp, "");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Initialize possible error message
		String errorMsg = "";
		
		// Get the project id
		int id;
		if (req.getParameter("id") != null) {
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement ps = con.prepareStatement(sqlProjectExists)) {
				id = Integer.valueOf(req.getParameter("id"));
				
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				rs.next();
				if (rs.getInt("project_exists") != 1) {
					errorMsg += "-> Ungülige Projektkennung angegeben.";
					showPage(req, resp, errorMsg);
					return;
				}
			} catch (SQLException e) {
				resp.sendError(500, "Datenbankfehler: " + e.getMessage());
				e.printStackTrace();
				return;
			} catch (NumberFormatException e) {
				errorMsg += "-> Ungültige Projektkennung vorhanden.";
				showPage(req, resp, errorMsg);
				return;
			}
		} else {
			errorMsg += "-> Keine Projektkennung vorhanden.";
			showPage(req, resp, errorMsg);
			return;
		}
		
		// Creator is the current logged in user
		String creator = USER_ID;
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement ps = con.prepareStatement(sqlCreator)) {
			ps.setInt(1, id);
			ps.setString(2, creator);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getInt("valid") != 1) {
				errorMsg += "-> Projekte dürfen nur vom Ersteller bearbeitet werden.<br>";
				showPage(req, resp, errorMsg);
				return;
			}
		} catch (SQLException e) {
			resp.sendError(500, "Datenbankfehler: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		// Check if title has 1 - 30 characters
		String title = req.getParameter("title");
		if (title == null || title.length() == 0 || title.length() > 30) {
			errorMsg += "-> Titel muss zwischen 1 und 30 Zeichen lang sein.<br>";
		}
		
		// Description has no constrains
		String description = req.getParameter("description");
		
		// Try to create a BigInteger object from the input and check if the limit is at least greater 100
		BigDecimal limit = null;
		try {
			limit = new BigDecimal(req.getParameter("limit"));
			if  (limit.doubleValue() < 100) {
				errorMsg += "-> Zu geringes Finanzierungslimit angegeben (mindestens 100€).<br>";
			}
		} catch (Exception e) {
			errorMsg += "-> Ungültiges oder kein Finanzierungslimit angegeben.<br>";
		}
		
		// Check if pred can be interpreted as an Integer and or set null if pred = "None"
		Integer pred = null;
		try {
			if (!req.getParameter("pred").equals("None")) {
				pred = Integer.valueOf(req.getParameter("pred"));
			}
		} catch (Exception e) {
			errorMsg += "-> Ungültiger Vorgänger angegeben.<br>";
		}
		
		// Check if category can be interpreted as an Integer
		Integer category = null;
		try {
			category = Integer.valueOf(req.getParameter("category"));
		} catch (Exception e) {
			errorMsg += "-> Keine oder ungültige Kategorie angegeben.<br>";
		}
	}
}
