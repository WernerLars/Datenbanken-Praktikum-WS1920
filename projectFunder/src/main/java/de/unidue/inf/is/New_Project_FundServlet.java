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
	private boolean fehler;
	
	private BigDecimal spendenbetrag;
	private String sichtbarkeit;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		kennung = req.getParameter("kennung");
	

		if(fehlercode == null) {
			fehlercode = " ";
		}
		
		if(kennung != null) {
			
			try {
								
				Connection con = DBUtil.getExternalConnection();

				PreparedStatement ps = con.prepareStatement("SELECT P.TITEL,P.STATUS FROM DBP068.PROJEKT AS P "
						+ "WHERE P.KENNUNG = ?");			
				ps.setString(1, kennung);			
				ResultSet rs = ps.executeQuery();			
				rs.next();
				titel = rs.getString("TITEL");
				status = rs.getString("STATUS");
				
				con.close();
					
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
		
		spendenbetrag = new BigDecimal(req.getParameter("spende"));
		sichtbarkeit = req.getParameter("anonym");
		fehler = false;
		
		if(status.equals("offen")) {
			if(spendenbetrag.compareTo(BigDecimal.ZERO) > 0 ) {
				
				try {
					
					Connection con = DBUtil.getExternalConnection();
					
					PreparedStatement ps = con.prepareStatement("SELECT K.GUTHABEN FROM DBP068.KONTO AS K "
							+ "WHERE K.INHABER = ?");
					ps.setString(1, USER_ID);
					ResultSet rs = ps.executeQuery();
					rs.next();
					BigDecimal guthaben = rs.getBigDecimal("GUTHABEN");
					
					if(guthaben.compareTo(spendenbetrag) >= 0) {
								
						if(sichtbarkeit == null) {
							sichtbarkeit = "oeffentlich";
						}else {
							sichtbarkeit = "privat";
						}	
						
						ps = con.prepareStatement("SELECT S.SPENDER FROM DBP068.SPENDEN AS S WHERE "
								+ "S.PROJEKT = ? AND S.SPENDER = ? ");
						ps.setString(1, kennung);
						ps.setString(2, USER_ID);
						rs = ps.executeQuery();
						if(rs.next() == false) {
							
							ps = con.prepareStatement("INSERT INTO DBP068.SPENDEN (spender,projekt,spendenbetrag,sichtbarkeit) "
									+ "VALUES (?,?,?,?)");
							ps.setString(1, USER_ID);
							ps.setString(2, kennung);
							ps.setBigDecimal(3, spendenbetrag);
							ps.setString(4, sichtbarkeit);
							ps.executeUpdate();
							
							ps = con.prepareStatement("UPDATE DBP068.KONTO AS K "
									+ "SET K.GUTHABEN = K.GUTHABEN - ? "
									+ "WHERE K.INHABER = ?");
							ps.setBigDecimal(1, spendenbetrag);
							ps.setString(2, USER_ID);
							ps.executeUpdate();
							
							ps = con.prepareStatement("SELECT P.FINANZIERUNGSLIMIT , SB.SPENDENSUMME "
									+ "FROM DBP068.PROJEKT AS P "
									+ "JOIN (SELECT S.PROJEKT , SUM(S.SPENDENBETRAG) AS SPENDENSUMME "
									+ "FROM DBP068.SPENDEN AS S "
									+ "GROUP BY S.PROJEKT) AS SB "
									+ "ON P.KENNUNG = SB.PROJEKT "
									+ "WHERE P.KENNUNG = ?");
							ps.setString(1, kennung);
							rs = ps.executeQuery();
							rs.next();
							BigDecimal fl = rs.getBigDecimal("FINANZIERUNGSLIMIT");
							BigDecimal ss = rs.getBigDecimal("SPENDENSUMME");
							
							if(ss.compareTo(fl) >= 0) {
								
								ps = con.prepareStatement("UPDATE DBP068.PROJEKT AS P "
										+ "SET P.STATUS = ? "
										+ "WHERE P.KENNUNG = ?");
								ps.setString(1, "geschlossen");
								ps.setString(2, kennung);
								ps.executeUpdate();							
							}
				
						}else {
							fehler = true;
							fehlercode = "Sie haben schon für dieses Projekt gespendet!";
						}
	
					}else {
						fehler = true;
						fehlercode = "Kontoguthaben nicht ausreichend!";					
					}
					
					con.close();
					
				}catch(SQLException e) {
					e.printStackTrace();
				}
				
				if(fehler == true) {
					
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
