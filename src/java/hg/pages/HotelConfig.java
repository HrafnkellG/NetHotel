package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.ConCaption;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Panel;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Configures the hotel settings.
 */
/* ========================================================================= */
public class HotelConfig extends HttpServlet
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
            // Must be an admin to view this page.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            
            DBR resHot = Delphi.Inst().HotelGet();
            Hotel hot = (Hotel)resHot.Result();
            
            Page p = new Page(usr.Txt(Loc.HCONFIG));
            Util.CommonElementsOnPages(p, usr, hot, PageID.HOTELCONFIG);
            
            // Include the javascript
            p.AddJSLink("scripts/hotelconf.js");
            
            // The content caption.
            ConCaption cap = new ConCaption(usr.Txt(Loc.HCONFIG));
            p.AddElement(cap);
            
            // 2 panels: left & right
            Panel lpan = new Panel(CSS.CPANELHCLEFT);
            Panel rpan = new Panel(CSS.CPANELHCRIGHT);
            hot.ShowInPanels(lpan, rpan, usr);
            p.AddElement(lpan);
            p.AddElement(rpan);
            
            // Save button for the settings.
            Actions sbutton = new Actions(null);
            sbutton.AddAction(usr.Txt(Loc.SAVE), "javascript:HCSave()");
            p.AddElement(sbutton);
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_config", null);
            p.AddElement(extraDiv);
            
            out.print(p.Render());
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
