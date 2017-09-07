package hg.ajax.tabs;

import hg.cons.Loc;
import hg.cons.Sess;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.JQDatePicker;
import hg.html5.Table;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/* ========================================================================= */
/**
 * The Graph tab was selected.
 * 
 * @author hg
 */
/* ========================================================================= */
public class TabGraph extends HttpServlet
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try
            {
            User usr = Util.getUserForSession(request);
            if (usr == null) 
                {
                out.print("No user for session.");
                return;
                }
            Delphi db = Delphi.Inst();
            
            JQDatePicker jqdp = new JQDatePicker(usr.Txt(Loc.DATE), "dpinput", "dpgraph");
            out.print(jqdp.Render());
            
            Actions act = new Actions(null);
            act.AddAction("Create a reservation for the selected days", "javascript:alert('success')");
            out.print(act.Render());
            
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            Date toDay = hot.getDate();
            Table tbl = graphTable.makeGraphTable(db, usr, toDay);
            out.print(tbl.Render());
            }
        finally
            {
            out.close();
            }
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
