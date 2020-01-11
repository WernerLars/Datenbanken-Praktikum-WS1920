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
	
	private String sqlGetCreator = "SELECT P.ERSTELLER FROM DBP068.PROJEKT AS P "
			+ "WHERE P.KENNUNG = ?";
	
	private String sqlGetWrite = "SELECT S.KOMMENTAR FROM DBP068.SCHREIBT AS S "
			+ "WHERE S.PROJEKT = ?";
	
	private String sqlDeleteWrite = "DELETE FROM DBP068.SCHREIBT AS S "
			+ "WHERE S.PROJEKT = ?";
	
	private String sqlDeleteCommend = "DELETE FROM DBP068.KOMMENTAR "
			+ "WHERE ID = ?";
	
	private String sqlGetDonations = "SELECT S.SPENDER , S.SPENDENBETRAG "
			+ "FROM DBP068.SPENDEN AS S "
			+ "WHERE S.PROJEKT = ?";
	
	private String sqlUpdateAccount = "UPDATE DBP068.KONTO AS K "
			+ "SET K.GUTHABEN = K.GUTHABEN + ? "
			+ "WHERE K.INHABER = ?";
	
	private String sqlDeleteDonations = "DELETE FROM DBP068.SPENDEN AS S "
			+ "WHERE S.PROJEKT = ?";
	
	private String sqlUpdateVorgaenger = "UPDATE DBP068.PROJEKT AS P "
			+ "SET P.VORGAENGER = NULL "
			+ "WHERE P.VORGAENGER = ?";
	
	private String sqlDeleteProject = "DELETE FROM DBP068.PROJEKT AS P "
			+ "WHERE P.KENNUNG = ?";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		boolean fehler = false;
		String kennung = req.getParameter("kennung");
		String ersteller = " ";

		if(kennung != null) {	
			
			try(Connection con = DBUtil.getExternalConnection();
				 PreparedStatement getCreator = con.prepareStatement(sqlGetCreator)){
				
				getCreator.setString(1, kennung);				
				ResultSet rs = getCreator.executeQuery();
				
				while(rs.next()) {
					ersteller = rs.getString("ERSTELLER");
				}
							
				if(USER_ID.equals(ersteller)) {
					
					try(PreparedStatement getWrite = con.prepareStatement(sqlGetWrite);
						PreparedStatement deleteWrite = con.prepareStatement(sqlDeleteWrite);
						PreparedStatement deleteCommend = con.prepareStatement(sqlDeleteCommend);
						PreparedStatement getDonations = con.prepareStatement(sqlGetDonations);
						PreparedStatement updateAccount = con.prepareStatement(sqlUpdateAccount);
						PreparedStatement deleteDonations = con.prepareStatement(sqlDeleteDonations);
						PreparedStatement updateVorgaenger = con.prepareStatement(sqlUpdateVorgaenger);
						PreparedStatement deleteProject = con.prepareStatement(sqlDeleteProject)){
						
						con.setAutoCommit(false);
						
						getWrite.setString(1, kennung);
						rs = getWrite.executeQuery();
						
						deleteWrite.setString(1, kennung);
						deleteWrite.executeUpdate();
						
						while(rs.next()) {							
							deleteCommend.setString(1, rs.getString("KOMMENTAR"));
							deleteCommend.executeUpdate();						
						}
						
						getDonations.setString(1, kennung);
						rs = getDonations.executeQuery();
						
						while(rs.next()) {							
							String spender = rs.getString("SPENDER");
							BigDecimal betrag = rs.getBigDecimal("SPENDENBETRAG");							
							updateAccount.setBigDecimal(1, betrag);
							updateAccount.setString(2, spender);
							updateAccount.executeUpdate();							
						}
						
						deleteDonations.setString(1, kennung);
						deleteDonations.executeUpdate();
						
						updateVorgaenger.setString(1, kennung);
						updateVorgaenger.executeUpdate();
						
						deleteProject.setString(1, kennung);
						deleteProject.executeUpdate();
						
						con.commit();
						
					}catch(SQLException e) {					
						con.rollback();
						resp.addHeader("fehler", "Datenbankfehler!");
						req.getRequestDispatcher("./view_project?kennung="+kennung).forward(req, resp);					
					}
					
				}else {				
					fehler = true;						
				}
				
			}catch(SQLException e) {
				resp.addHeader("fehler", "Datenbankfehler!");
				req.getRequestDispatcher("./view_project?kennung="+kennung).forward(req, resp);
			}
				
			if(fehler) {				
				resp.addHeader("fehler", "Keine Berechtigung!");
				req.getRequestDispatcher("./view_project?kennung="+kennung).forward(req, resp);				
			}else {
				resp.sendRedirect("./view_main");	
			}			
		}			
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		super.doGet(req, resp);
	}

	
	
}
