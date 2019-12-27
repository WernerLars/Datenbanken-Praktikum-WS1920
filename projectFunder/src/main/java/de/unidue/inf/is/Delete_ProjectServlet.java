package de.unidue.inf.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.utils.DBUtil;

public class Delete_ProjectServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private final String USER_ID = "dummy@dummy.com";
	
	private String query;
	private String kennung;
	private String ersteller;
	
	private boolean fehler;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		fehler = false;
		query = req.getQueryString();
		

		if(query.substring(0, 8).equals("kennung=")) {	
			
			try {
				
				kennung = query.substring(8);
				
				Connection con = DBUtil.getExternalConnection();
				
				PreparedStatement ps = con.prepareStatement("SELECT P.ERSTELLER FROM DBP068.PROJEKT AS P "
						+ "WHERE P.KENNUNG = ?");
				
				ps.setString(1, kennung);
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					ersteller = rs.getString("ERSTELLER");
				}
			
				
				if(USER_ID.equals(ersteller)) {
					
					ps = con.prepareStatement("SELECT S.KOMMENTAR FROM DBP068.SCHREIBT AS S "
							+ "WHERE S.PROJEKT = ?");
					ps.setString(1, kennung);
					rs = ps.executeQuery();
					
			
					while(rs.next()) {
						
						ps = con.prepareStatement("DELETE FROM DBP068.KOMMENTAR AS K "
								+ "WHERE K.ID = ?");
						ps.setString(1, rs.getString("KOMMENTAR"));
						ps.executeUpdate();
						
					}
					
					
					ps = con.prepareStatement("DELETE FROM DBP068.SCHREIBT AS S "
							+ "WHERE S.PROJEKT = ?");
					ps.setString(1, kennung);
					ps.executeUpdate();
					
	
					ps = con.prepareStatement("SELECT S.SPENDER , S.SPENDENBETRAG "
							+ "FROM DBP068.SPENDEN AS S "
							+ "WHERE S.PROJEKT = ?");
					ps.setString(1, kennung);
					rs = ps.executeQuery();
					while(rs.next()) {
						
						String spender = rs.getString("SPENDER");
						BigDecimal betrag = rs.getBigDecimal("SPENDENBETRAG");
						
						ps = con.prepareStatement("UPDATE DBP068.KONTO AS K "
								+ "SET K.GUTHABEN = K.GUTHABEN + ? "
								+ "WHERE K.INHABER = ?");
						ps.setBigDecimal(1, betrag);
						ps.setString(2, spender);
						ps.executeUpdate();
						
					}
					

					ps = con.prepareStatement("DELETE FROM DBP068.SPENDEN AS S "
							+ "WHERE S.PROJEKT = ?");
					ps.setString(1, kennung);
					ps.executeUpdate();
					
					
					
					ps = con.prepareStatement("DELETE FROM DBP068.PROJEKT AS P "
							+ "WHERE P.KENNUNG = ?");
					ps.setString(1, kennung);
					ps.executeUpdate();
					

					con.close();
							
					
				}else {
					
					fehler = true;
						
				}
				
			}catch(SQLException e) {
				req.setAttribute("fehler", "Datenbankfehler");
				req.getRequestDispatcher("./error_page.ftl").forward(req, resp);
			}
				
			if(fehler) {
				req.setAttribute("fehler", "Keine Berechtigung");
				req.getRequestDispatcher("./error_page.ftl").forward(req, resp);
				
			}else {
				req.getRequestDispatcher("./ok_page.ftl").forward(req, resp);	
			}
			
		}
			
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		super.doGet(req, resp);
	}

	
	
}
