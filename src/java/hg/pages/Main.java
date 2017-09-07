package hg.pages;

import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.JQTabs;
import hg.html5.Page;
import hg.html5.Div;
import hg.html5.JQDatePicker;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class Main extends HttpServlet
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
        // If a user is not logged in, redirect.
        User usr = Util.getUserForSession(request);
        if (usr == null)
            {
            response.sendRedirect(Paths.LOGIN);
            return;
            }
        HttpSession theSession = request.getSession(false);
        Hotel hot = (Hotel)theSession.getAttribute(Sess.HOTEL);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
            {
            Page p = CreateMainPage(usr, hot, theSession);
            out.print(p.Render());
            } 
        finally 
            { 
            out.close();
            }
        }

    private Page CreateMainPage(User usr, Hotel hot, HttpSession sess)
        {
        Page p = new Page(usr.Txt(Loc.APPTITLE));
        Util.CommonElementsOnPages(p, usr, hot, PageID.MAINPAGE);
        p.AddJSLink("scripts/main.js");
        
        // Action buttons
        Actions acts = p.getActions();
        acts.AddAction(usr.Txt(Loc.NEWRES), "Reservation");
        acts.AddAction(usr.Txt(Loc.LISTRES), "ResList");
        acts.AddAction(usr.Txt(Loc.INVOICES), "InvList");
        acts.AddAction("Create DEMO DATA", "javascript:CDD()", "btncdd");
        
        // The tabs
        JQTabs tabs = p.getTabs();
        tabs.AddTab("Graph", "TabGraph");
        tabs.AddTab(usr.Txt(Loc.ARRIVING), "TabArriving");
        tabs.AddTab(usr.Txt(Loc.DEPARTING), "TabDeparting");
        tabs.AddTab(usr.Txt(Loc.CHECKEDIN), "TabCheckedin");
        tabs.AddTab(usr.Txt(Loc.AVAILABLEROOMS), "TabAvailableRooms");
        tabs.AddTab(usr.Txt(Loc.ROOMSTATUS), "TabRoomStatus");
        tabs.AddTab(usr.Txt(Loc.OPENINVOICES), "TabOpenInvoices");
        
        // A variable to keep track of the last selected tab.
        String atab = (String)sess.getAttribute(Sess.ACTIVE_TAB);
        p.AddJSCode("var lastSelectedTab = " + atab + ";\n");
        
        // This holds the current date.
        GregorianCalendar currDate = hot.getDateTime();
        int cdY = currDate.get(GregorianCalendar.YEAR);
        int cdM = currDate.get(GregorianCalendar.MONTH);
        int cdD = currDate.get(GregorianCalendar.DAY_OF_MONTH);
        p.AddJSCode("var currentDateDefinedByHotel = new Date(" + cdY + "," + cdM + "," + cdD + ");\n");
        
        // An extra div for decorative purposes.
        Div extraDiv = new Div("extra_main", null);
        p.AddElement(extraDiv);
        
        return p;
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
