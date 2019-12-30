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

public class New_CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String sqlProjectName = "SELECT titel FROM dbp068.projekt WHERE kennung=?";
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String title = "";
		int id;
		
		if (req.getParameter("kennung") != null) {
			try (
					Connection con = DBUtil.getExternalConnection();
					PreparedStatement ps = con.prepareStatement(sqlProjectName)) {
				id = Integer.valueOf(req.getParameter("kennung"));
				
				ps.setInt(1, id);
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
			
			req.setAttribute("id", id);
			req.setAttribute("errorMsg", "");
			req.setAttribute("title", title);
			
			req.getRequestDispatcher("new_comment.ftl").forward(req, resp);
		} else {
			resp.sendError(404, "Keine Projektkennung angegeben.");
			return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
