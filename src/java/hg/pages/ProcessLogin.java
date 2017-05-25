
package hg.pages;

import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.User;
import hg.html5.Page;
import hg.util.AppLog;
import hg.util.Country;
import hg.util.Util;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ProcessLogin extends HttpServlet
    {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
//        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try 
//            {
//            out.print("<p>Greetings, the user directory should follow:</p>");
//            String propDir  = System.getProperty("user.dir");
//            out.print("<p>" + propDir + "</p>");
//            out.print("<p>Attempting to use the log:</p>");
//            String appLogResult = AppLog.Instance().Info("Hello world...");
//            
//            if (appLogResult.length() > 0) 
//                {
//                out.print("<p>Log failed, this is the message:</p>");
//                out.print("<p>" + appLogResult + "</p>");
//                return;
//                }
//            out.print("<p>If we get here it worked?</p>");
//            
//            out.print("<p>Now we try to connect to the database using this string:</p>");
//            String con_str  = "jdbc:hsqldb:" + propDir + File.separator + "dbfiles" + File.separator + "nethoteldb";
//            out.print("<p>" + con_str + "</p>");
//            
//            out.print("<p>First we load hsqldb.jdbcDriver.</p>");
//            Class.forName("org.hsqldb.jdbcDriver");
//            out.print("<p>And now we attempt to make the connection.</p>");
//            java.sql.Connection _hsqldbConnection = DriverManager.getConnection(con_str, "sa", "");
//            out.print("<p>Apparently we have a connection, let's try to read from the database:</p>");
//            String query = "select * from user where key = 'open'";
//            Statement readStmt = _hsqldbConnection.createStatement();
//            ResultSet res = readStmt.executeQuery(query);
//            res.next();
//            String theUserStyle = res.getString("style");
//            res.close();
//            out.print("<p>The user style is:</p>");
//            out.print("<p>" + theUserStyle + "</p>");
//            
//            out.print("<p>...</p>");
//            out.print("<p>If this is visible, then no exception was generated.</p>");
//            } 
//        catch (Exception e) 
//            {
//            out.print("<p>Oh woe is us! An exception!</p>");
//            out.print("<p>" +  e.getMessage() + "</p>");
//            }
//        finally 
//            { 
//            out.close();
//            }
//        return;
        // DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG DEBUG
        
        Delphi db = Delphi.Inst();
        
        String loginId  = request.getParameter("id");
        String loginKey = request.getParameter("password");
        if (!Util.StringOK(loginId) || 
            !Util.StringOK(loginKey))
            {
            response.sendRedirect(Paths.LOGIN);
            return;
            }
        
        DBR res = db.UserGet(loginId, loginKey);
        if (! res.OK())
            {
            response.sendRedirect(Paths.LOGIN);
            return;
            }
        User usr = (User)res.Result();

        // Create and store in the session object, various objects for the logged in user.
        HttpSession session = request.getSession();
        session.setAttribute(Sess.USER, usr);

        DBR resHot = db.HotelGet();
        Hotel hot = (Hotel)resHot.Result();
        session.setAttribute(Sess.HOTEL, hot);
        
        Country[] cs = Util.CreateCountryList(usr.getLocale());
        session.setAttribute(Sess.COUNTRIES, cs);
        
        String activeTab = "0";
        session.setAttribute(Sess.ACTIVE_TAB, activeTab);
        
        response.sendRedirect(Paths.MAIN);
        }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        processRequest(request, response);
        }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
        {
        processRequest(request, response);
        }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
        {
        return "Short description";
        }// </editor-fold>

    }
