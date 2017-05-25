package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Hotel;
import hg.db.Inv;
import hg.db.User;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Table;
import hg.html5.TableHead;
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
 * List of invoices for the hotel. User can display just those invoices which
 * are active (open), or all of them for a selected period. The default period
 * is from the current date and the next 30 days.
 */
/* ========================================================================= */
public class InvList extends HttpServlet
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
            Page p = new Page(usr.Txt(Loc.APPTITLE));
            Util.CommonElementsOnPages(p, usr, hot, PageID.INVLIST);
            
//            hg.html5.CheckBox cb = new hg.html5.CheckBox(
//                "singleCheckbox", 
//                "cbOnlyOpen", 
//                "CheckboxClick()", 
//                true,
//                usr.Txt(Loc.ACTIVEINVOICES));
//            p.AddElement(cb);
            
            hg.html5.Period per = new hg.html5.Period(
                "period", 
                "dpfrom", 
                "dpto", 
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
            // Here is where we load the invlist script to make sure it comes 
            // after the localization script for the datepicker.
            p.AddJSLink("scripts/invlist.js");
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
            
            // A table with invoices intersecting the period.
            String[] colHeaders = new String[4];
            colHeaders[0] = usr.Txt(Loc.NUMBER_SHORTENED);
            colHeaders[1] = usr.Txt(Loc.DATE);
            colHeaders[2] = usr.Txt(Loc.CUSTOMER);
            colHeaders[3] = usr.Txt(Loc.AMOUNT);
            Table invoices = new Table(colHeaders, "pantable");
            invoices.setCaption(usr.Txt(Loc.INVOICES));
            // Set CSS for the columns.
            TableHead th = invoices.getHead();
            th.setCellCSS(0, CSS.CCOL_NUMBER);
            th.setCellCSS(1, CSS.CCOL_DATE);
            th.setCellCSS(2, CSS.CCOL_NAME);
            th.setCellCSS(3, CSS.CCOL_NUMBER);
            Inv.ShowPeriodInTable(invoices, perStart, perEnd, hot);
            
            p.AddElement(invoices);
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_invlist", null);
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
