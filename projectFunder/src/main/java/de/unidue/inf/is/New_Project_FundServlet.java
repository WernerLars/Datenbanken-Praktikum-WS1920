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


public class New_Project_FundServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private final String USER_ID = "dummy@dummy.com";
	
	private String kennung;
	private String titel;
	private String status;
	private String fehlercode;
	
	private String sqlGetProject = "SELECT P.TITEL,P.STATUS FROM DBP068.PROJEKT AS P "
			+ "WHERE P.KENNUNG = ?";
	
	private String sqlGetCredit = "SELECT K.GUTHABEN FROM DBP068.KONTO AS K "
			+ "WHERE K.INHABER = ?";
	
	private String sqlHasDonated = "SELECT S.SPENDER FROM DBP068.SPENDEN AS S WHERE "
			+ "S.PROJEKT = ? AND S.SPENDER = ? ";
	
	private String sqlInsertDonation = "INSERT INTO DBP068.SPENDEN (spender,projekt,spendenbetrag,sichtbarkeit) "
			+ "VALUES (?,?,?,?)";
		
	private String sqlUpdateAccount = "UPDATE DBP068.KONTO AS K "
			+ "SET K.GUTHABEN = K.GUTHABEN - ? "
			+ "WHERE K.INHABER = ?";
	
	private String sqlGetLimitAndSum = "SELECT P.FINANZIERUNGSLIMIT , SB.SPENDENSUMME "
			+ "FROM DBP068.PROJEKT AS P "
			+ "JOIN (SELECT S.PROJEKT , SUM(S.SPENDENBETRAG) AS SPENDENSUMME "
			+ "FROM DBP068.SPENDEN AS S "
			+ "GROUP BY S.PROJEKT) AS SB "
			+ "ON P.KENNUNG = SB.PROJEKT "
			+ "WHERE P.KENNUNG = ?";
	
	private String sqlUpdateStatus = "UPDATE DBP068.PROJEKT AS P "
			+ "SET P.STATUS = ? "
			+ "WHERE P.KENNUNG = ?";
						
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		kennung = req.getParameter("kennung");
		
		if(fehlercode == null) {
			fehlercode = " ";
		}
		
		if(kennung != null) {
			
			try(Connection con = DBUtil.getExternalConnection();
				PreparedStatement getProject = con.prepareStatement(sqlGetProject)) {
										
				getProject.setString(1, kennung);			
				ResultSet rs = getProject.executeQuery();	
				
				while(rs.next()) {
					titel = rs.getString("TITEL");
					status = rs.getString("STATUS");
				}
							
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			req.setAttribute("titel", titel);
			req.setAttribute("fehler", fehlercode);
			req.getRequestDispatcher("./new_project_fund.ftl").forward(req, resp);
			fehlercode = " ";
		}	
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		BigDecimal guthaben = new BigDecimal("0");
		BigDecimal spendenbetrag = new BigDecimal(req.getParameter("spende"));
		
		String sichtbarkeit = req.getParameter("anonym");
		
		if(sichtbarkeit == null) {
			sichtbarkeit = "oeffentlich";
		}else {
			sichtbarkeit = "privat";
		}	
		
		BigDecimal finanzierungslimit = new BigDecimal("0");
		BigDecimal spendensumme = new BigDecimal("0");
			
		boolean fehler = false;
		
		if(status.equals("offen")) {
			
			if(spendenbetrag.compareTo(BigDecimal.ZERO) > 0 ) {
				
				try(Connection con = DBUtil.getExternalConnection();
					PreparedStatement getCredit = con.prepareStatement(sqlGetCredit);
					PreparedStatement hasDonated = con.prepareStatement(sqlHasDonated)) {
					
					getCredit.setString(1, USER_ID);
					ResultSet rs = getCredit.executeQuery();
					
					while(rs.next()) {
						guthaben = rs.getBigDecimal("GUTHABEN");
					}
					
					if(guthaben.compareTo(spendenbetrag) >= 0) {
								
						hasDonated.setString(1, kennung);
						hasDonated.setString(2, USER_ID);
						rs = hasDonated.executeQuery();
						
						if(rs.next() == false) {
							
							try(PreparedStatement insertDonation = con.prepareStatement(sqlInsertDonation);
								PreparedStatement updateAccount = con.prepareStatement(sqlUpdateAccount);
								PreparedStatement getLimitAndSum = con.prepareStatement(sqlGetLimitAndSum);
								PreparedStatement updateStatus = con.prepareStatement(sqlUpdateStatus)){
								
								con.setAutoCommit(false);
								
								insertDonation.setString(1, USER_ID);
								insertDonation.setString(2, kennung);
								insertDonation.setBigDecimal(3, spendenbetrag);
								insertDonation.setString(4, sichtbarkeit);
								insertDonation.executeUpdate();
								
								updateAccount.setBigDecimal(1, spendenbetrag);
								updateAccount.setString(2, USER_ID);
								updateAccount.executeUpdate();
								
								getLimitAndSum.setString(1, kennung);
								rs = getLimitAndSum.executeQuery();
								
								while(rs.next()) {
									finanzierungslimit = rs.getBigDecimal("FINANZIERUNGSLIMIT");
									spendensumme = rs.getBigDecimal("SPENDENSUMME");
								}
								
								if(spendensumme.compareTo(finanzierungslimit) >= 0) {
									updateStatus.setString(1, "geschlossen");
									updateStatus.setString(2, kennung);
									updateStatus.executeUpdate();							
								}
					
								con.commit();
								
							}catch(SQLException e) {
								con.rollback();
								fehlercode = "Datenbankfehler!";
								resp.sendRedirect("./new_project_fund?kennung="+kennung);
							}
		
						}else {
							fehler = true;
							fehlercode = "Sie haben schon für dieses Projekt gespendet!";
						}
	
					}else {
						fehler = true;
						fehlercode = "Kontoguthaben nicht ausreichend!";					
					}
					
				}catch(SQLException e) {
					fehlercode = "Datenbankfehler!";
					resp.sendRedirect("./new_project_fund?kennung="+kennung);
				}
				
				if(fehler) {				
					resp.sendRedirect("./new_project_fund?kennung="+kennung);					
				}else {				
					resp.sendRedirect("./view_project?kennung="+kennung);					
				}						
				
			}else {		
				fehlercode = "Ungültiger Spendenbetrag!";
				resp.sendRedirect("./new_project_fund?kennung="+kennung);			
			}
		}else {
			fehlercode = "Status des Projektes ist geschlossen!";
			resp.sendRedirect("./new_project_fund?kennung="+kennung);	
		}		
	}
}
