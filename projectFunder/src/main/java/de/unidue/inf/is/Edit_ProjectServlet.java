package de.unidue.inf.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
	
	private String sqlPred = "SELECT kennung, titel FROM dbp068.projekt WHERE ersteller=? AND kennung<>?";
	private String sqlCategories = "SELECT id, name FROM dbp068.kategorie";
	private String sqlProjectExists = "SELECT count(*) AS project_exists FROM dbp068.projekt WHERE kennung=?";
	private String sqlUpdate = "UPDATE dbp068.projekt SET titel=?, beschreibung=?, finanzierungslimit=?, vorgaenger=?, kategorie=? WHERE kennung=?";
	
	protected HashMap<String, String> getProject(int id) throws SQLException {
		HashMap<String, String> res = new HashMap<String, String>();

		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement ps = con.prepareStatement(
						"SELECT titel, beschreibung, finanzierungslimit, ersteller, vorgaenger, kategorie FROM dbp068.projekt WHERE kennung=?")) {
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			// Get the selected project details
			res.put("kennung", Integer.toString(id));
			res.put("titel", rs.getString("titel"));
			String d = (rs.getString("beschreibung") == null) ? "" : rs.getString("beschreibung");
			res.put("beschreibung", d);
			res.put("finanzierungslimit", rs.getBigDecimal("finanzierungslimit").toString());
			res.put("ersteller", rs.getString("ersteller"));
			res.put("vorgaenger", Integer.toString(rs.getInt("vorgaenger")));
			res.put("kategorie", Integer.toString(rs.getInt("kategorie")));
		}
		
		return res;
	}
	
	protected void showPage(HttpServletRequest req, HttpServletResponse resp, String errorMsg) throws ServletException, IOException {
		// Check if project id is valid and exists
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
		
		HashMap<String, String> project;
		ArrayList<Map<String, String>> categories = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> preds = new ArrayList<Map<String, String>>();
		
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement psCategories = con.prepareStatement(sqlCategories);
				PreparedStatement psProjects = con.prepareStatement(sqlPred)) {
			
			// Get the selected project details
			project = getProject(pid);
			
			/* Get a list of all categories for the logged in user USER_ID and add them to a
			 * list of maps with the following keys:
			 * "id": Unique id of each category
			 * "name": Name of each category
			 */
			ResultSet rsCategories = psCategories.executeQuery();
			while (rsCategories.next()) {
				HashMap<String, String> c = new HashMap<String, String>();
				
				c.put("id", Integer.toString(rsCategories.getInt("id")));
				c.put("name", rsCategories.getString("name"));
				c.put("checked", project.get("kategorie").equals(c.get("id")) ? "checked" : "");
				
				categories.add(c);
			}
			
			/* Get all projects of the logged in user USER_ID and add them to a list of maps
			 * with the following keys:
			 * "kennung": Unique id of each project
			 * "titel": Title of each project
			 */
			psProjects.setString(1, USER_ID);
			psProjects.setInt(2, pid);
			ResultSet rsProjects = psProjects.executeQuery();
			while (rsProjects.next()) {
				HashMap<String, String> p = new HashMap<String, String>();
				
				p.put("kennung", Integer.toString(rsProjects.getInt("kennung")));
				p.put("titel", rsProjects.getString("titel"));
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
		
		// Get a HashMap of the project with the given id
		HashMap<String, String> project;
		try {
			project = getProject(id);
		} catch (SQLException e) {
			resp.sendError(500, "Datenbankfehler: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		// Check if user has permissions for editing the project
		if (!project.get("ersteller").equals(USER_ID)) {
			errorMsg += "-> Projekte dürfen nur vom Ersteller bearbeitet werden.<br>";
			showPage(req, resp, errorMsg);
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
			double limit_old = Double.valueOf(project.get("finanzierungslimit"));
			if  (limit.doubleValue() < limit_old) {
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
		
		if (!errorMsg.equals("")) {
			showPage(req, resp, errorMsg);
		} else {
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
				// Set title
				ps.setString(1, title);
				// Set description
				if (description == null || description.equals("")) {
					ps.setNull(2, Types.CLOB);
				} else {
					ps.setString(2, description);
				}
				// Set limit
				ps.setBigDecimal(3, limit);
				// Set predecessor
				if (pred == null) {
					ps.setNull(4, Types.SMALLINT);
				} else {
					ps.setInt(4, pred);
				}
				// Set category
				ps.setInt(5, category);
				
				// Set project id in WHERE clausel
				ps.setInt(6, id);
				
				ps.executeUpdate();
				
				resp.sendRedirect("view_project?kennung=" + id);
			} catch (SQLException e) {
				resp.sendError(500, "Datenbankfehler: " + e.getMessage());
				e.printStackTrace();
				return;
			}
		}
	}
}
