package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;

public class New_CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String USER_ID = "dummy@dummy.com";

	private String sqlProjectName = "SELECT titel FROM dbp068.projekt WHERE kennung=?";
	private String sqlInsertKommentar = "INSERT INTO dbp068.kommentar (id, text, datum, sichtbarkeit) VALUES (?, ?, ?, ?)";
	private String sqlInsertSchreibt = "INSERT INTO dbp068.schreibt (benutzer, projekt, kommentar) VALUES (?, ?, ?)";
	
	// Get the next free comment id
	protected int getNewCommentID() throws SQLException {
		try (
				Connection con = DBUtil.getExternalConnection();
				PreparedStatement ps = con.prepareStatement("SELECT id FROM dbp068.kommentar ORDER BY id ASC")) {
			ResultSet rs = ps.executeQuery();
			
			// SMALLINT has a size of 15 bit (from -32768 to +32767)
			int i = 1;
			while (rs.next() && i <= 32767) {
				if (rs.getInt("id") != i) return i;
				++i;
			}
			
			// Return i if i less equal than size of SMALLINT, else return -1 (error).
			return (i <= 32767) ? i : -1;
		}
	}
	
	protected void showPage(HttpServletRequest req, HttpServletResponse resp, String errorMsg) throws ServletException, IOException {
		String title = "";
		int pid;
		
		if (req.getParameter("kennung") != null) {
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement ps = con.prepareStatement(sqlProjectName)) {
				pid = Integer.valueOf(req.getParameter("kennung"));
				
				ps.setInt(1, pid);
				ResultSet rs = ps.executeQuery();
				
				if (rs.next()) { title = rs.getString("titel"); } 
				else {
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
			
			req.setAttribute("pid", pid);
			req.setAttribute("errorMsg", errorMsg);
			req.setAttribute("title", title);
			
			req.getRequestDispatcher("new_comment.ftl").forward(req, resp);
		
		} else {
			resp.sendError(404, "Keine Projektkennung angegeben.");
			return;
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		showPage(req, resp, "");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String errorMsg = "";
		
		String comment = req.getParameter("comment");
		if (comment == null || comment.equals("")) {
			errorMsg += "-> Der Kommentartext darf nicht leer sein.<br>";
		}
		
		String anonymous = (req.getParameter("anonymous") == null) ? "oeffentlich" : "privat";
		
		if (errorMsg.equals("")) {
			try (Connection con = DBUtil.getExternalConnection()) {
				// Set auto-commit off
				con.setAutoCommit(false);
				
				try (
						PreparedStatement psProject = con.prepareStatement(sqlProjectName);
						PreparedStatement psInsertKommentar = con.prepareStatement(sqlInsertKommentar);
						PreparedStatement psInsertSchreibt = con.prepareStatement(sqlInsertSchreibt)) {
					// Get new comment id
					int cid = getNewCommentID();
					if (cid == -1) {
						resp.sendError(404, "Kommentar kann nicht erstellt werden.");
						return;
					}
					
					// Get current timestamp
					Date date = new Date();
					Timestamp ts = new Timestamp(date.getTime());
					
					// Get project id and check if it exists
					int pid = Integer.valueOf(req.getParameter("kennung"));
					psProject.setInt(1, pid);
					ResultSet rs = psProject.executeQuery();
					if (!rs.next()) {
						resp.sendError(404, "Ungülige Projektkennung angegeben.");
						return;
					}
					
					// Set values and execute update
					psInsertKommentar.setInt(1, cid);
					psInsertKommentar.setString(2, comment);
					psInsertKommentar.setTimestamp(3, ts);
					psInsertKommentar.setString(4, anonymous);
					psInsertKommentar.executeUpdate();
					
					psInsertSchreibt.setString(1, USER_ID);
					psInsertSchreibt.setInt(2, pid);
					psInsertSchreibt.setInt(3, cid);
					psInsertSchreibt.executeUpdate();

					// Commit changes
					con.commit();
					
					// Redirect to the view_project page
					resp.sendRedirect("view_project?kennung=" + pid);
					
				} catch (SQLException e) {
					// Rollback transaction
					con.rollback();
					
					// Send error message
					resp.sendError(500, "Datenbankfehler: " + e.getMessage());
					e.printStackTrace();
					return;
					
				} catch (NumberFormatException e) {
					// Rollback transaction
					con.rollback();
					
					// Send error message
					resp.sendError(404, "Ungülige Projektkennung angegeben.");
					return;
				}
				
			} catch (SQLException e) {
				resp.sendError(500, "Datenbankfehler: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			
		} else {
			showPage(req, resp, "Fehler:<br>" + errorMsg + "<br>");
		}
	}
}
