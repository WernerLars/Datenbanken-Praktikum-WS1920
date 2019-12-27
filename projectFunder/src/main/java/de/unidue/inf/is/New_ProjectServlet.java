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


public final class New_ProjectServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String USER_ID = "dummy@dummy.com";

	protected void showPage(HttpServletRequest req, HttpServletResponse resp, String errorMsg) throws ServletException, IOException {
		ArrayList<Map<String, String>> categories = new ArrayList<Map<String, String>>();
		ArrayList<Map<String, String>> projects = new ArrayList<Map<String, String>>();
		
		String sqlCategories = "SELECT id, name FROM dbp068.kategorie";
		String sqlProjects = "SELECT kennung, titel FROM dbp068.projekt WHERE ersteller=?";
		
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement psCategories = con.prepareStatement(sqlCategories);
				PreparedStatement psProjects = con.prepareStatement(sqlProjects)) {
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
				
				projects.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		req.setAttribute("errorMsg", errorMsg);
		req.setAttribute("categories", categories);
		req.setAttribute("projects", projects);
				
		req.getRequestDispatcher("new_project.ftl").forward(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		showPage(req, resp, "");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Initialize possible error message
		String errorMsg = "";
		
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
		
		// Creator is the current logged in user
		String creator = USER_ID;
		
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
		
		// If errorsMsg != "" some errors occur and insertion aborted.
		if (!errorMsg.equals("")) {
			showPage(req, resp, "Fehler:<br>" + errorMsg);
		} else {
			String sqlInsert = "INSERT INTO dbp068.projekt (titel, beschreibung, status, finanzierungslimit, ersteller, vorgaenger, kategorie) VALUES (?, ?, 'offen', ?, ?, ?, ?)";
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
				// Set title
				psInsert.setString(1, title);
				// Set description
				if (description == null || description.equals("")) {
					psInsert.setNull(2, Types.CLOB);
				} else {
					psInsert.setString(2, description);
				}
				// Set limit
				psInsert.setBigDecimal(3, limit);
				// Set creator
				psInsert.setString(4, creator);
				if (pred == null) {
					psInsert.setNull(5, Types.SMALLINT);
				} else {
					psInsert.setInt(5, pred);
				}
				// Set category
				psInsert.setInt(6, category);
				
				// Execute insert statement
				psInsert.executeUpdate();
				
				resp.sendRedirect("view_main");
			} catch (Exception e) {
				showPage(req, resp, "Datenbankfehler:<br>" + e.getMessage() + "<br>");
			}
		}
	}
}