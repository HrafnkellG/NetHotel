package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.User;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Period;
import hg.html5.Table;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * A list of all reservations which intersect a given period.
 */
/* ========================================================================= */
public class ResList extends HttpServlet
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
            // If a user is not logged in, redirect.
            User usr = Util.getUserForSession(request);
            if (usr == null)
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            HttpSession theSession = request.getSession(false);
            Hotel hot = (Hotel)theSession.getAttribute(Sess.HOTEL);
            
            Page p = new Page(usr.Txt(Loc.LISTRES));
            Util.CommonElementsOnPages(p, usr, hot, PageID.RESERVATION_LIST);
            
            // Selfcontained panel for the period.
            Period per = new Period(
                CSS.CPERIOD + " perreslist", 
                "idresstart", 
                "idresend", 
                usr.Txt(Loc.PERIODFROM),
                usr.Txt(Loc.TO));
            p.AddElement(per);
            
            // Code to localize the jQuery Datepicker.
            Locale loc = usr.getLocale();
            String lang = loc.getLanguage();
            if (lang.compareTo("en") == 0) 
                {
                // English is the default in jQuery and denoted by an empty string.
                lang = "";
                }
            else 
                {
                // The language is not English so include a link 
                // to the jQuery localization file.
                p.AddJSLink("scripts/jq/i18n/" + Util.jqDatepickerLocalizationFile(lang));
                }
            // Here is where we load the reslist script to make sure it comes 
            // after the localization script for the datepicker.
            p.AddJSLink("scripts/reslist.js");
            String jsCode = "var user_lang = '" + lang + "';\n";
            p.AddJSCode(jsCode);
            
            // Code to create 2 javascript objects of type Date. These are start
            // and end for the reservations. When first displayed the page always
            // has the start date as today and the end 30 days later.
            GregorianCalendar perStart = hot.getDateTime();
            GregorianCalendar perEnd   = hot.getDateTime();
            perEnd.add(GregorianCalendar.DAY_OF_MONTH, 30);
            
            String paramStart = Util.jqParamsForDate(perStart);
            String paramEnd   = Util.jqParamsForDate(perEnd);
            
            p.AddJSCode("var period_start = new Date(" + paramStart + ");\n");
            p.AddJSCode("var period_end = new Date(" + paramEnd + ");\n");
            
            // A table with the reservations intersecting the period.
            String[] colHeaders = new String[5];
            colHeaders[0] = usr.Txt(Loc.ARRIVING);
            colHeaders[1] = usr.Txt(Loc.DEPARTING);
            colHeaders[2] = usr.Txt(Loc.CUSTOMER);
            colHeaders[3] = usr.Txt(Loc.NOOFROOMS);
            colHeaders[4] = usr.Txt(Loc.NOOFGUESTS);
            Table reservations = new Table(colHeaders, "pantable");
            reservations.setCaption(usr.Txt(Loc.RESERVATION));
            Res.showInTableReservations(reservations, perStart, perEnd);
            
            p.AddElement(reservations);
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_reslist", null);
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
