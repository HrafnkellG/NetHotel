package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Customer;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.Div;
import hg.html5.IDropDown;
import hg.html5.Page;
import hg.html5.Panel;
import hg.html5.Table;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* ========================================================================= */
/**
 * Create a new reservation.
 */
/* ========================================================================= */
public class Reservation extends HttpServlet
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
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            Page p = new Page(usr.Txt(Loc.RESERVATION));
            Util.CommonElementsOnPages(p, usr, hot, PageID.RESERVATION);
            
            // This sequence is mandatory, the 'res' script uses a reference to
            // the 'datepicker-is' script.
            p.AddJSLink("scripts/jq/i18n/datepicker-is.js");
            p.AddJSLink("scripts/res.js");
            
            // The save button
            Actions sbutton = p.getActions();
            sbutton.AddAction(usr.Txt(Loc.SAVE), "javascript:ResSave()");
            
            Panel pan = new Panel(CSS.CPANELRES);
            pan.AddTitle(usr.Txt(Loc.RESERVATION));
            pan.AddJQDPInput(usr.Txt(Loc.ARRIVING), "", "idresarrive");
            pan.AddJQDPInput(usr.Txt(Loc.DEPARTING), "", "idresdepart");
            pan.AddInfoField(usr.Txt(Loc.NOOFNIGHTS), "1", "idresnnights");
            pan.AddInfoField(usr.Txt(Loc.NOOFGUESTS), "0", "idresnguests");
            pan.AddInfoField(usr.Txt(Loc.NOOFROOMS),  "0", "idresnrooms");
            AddAvailableCustomers(pan, usr);
            p.AddElement(pan);
            
            // A list of available rooms for the period.
            GregorianCalendar fromDate = hot.getDateTime();
            GregorianCalendar toDate   = hot.getDateTime();
            toDate.add(GregorianCalendar.DAY_OF_MONTH, 1);
            
            String[] colHeaders = new String[4];
            colHeaders[0] = usr.Txt(Loc.ROOMNO);
            colHeaders[1] = usr.Txt(Loc.SIZE);
            colHeaders[2] = usr.Txt(Loc.TYPE);
            colHeaders[3] = usr.Txt(Loc.FLOOR);
            Table avail = new Table(colHeaders, "superfluous");
            avail.setCaption(usr.Txt(Loc.AVAILABLEROOMS));
            
            Res.showInTableAvailableRooms(avail, fromDate, toDate, hot.AllowOverbooking());
            
            // Before adding the table to the page we must wrap it with a div
            // element so jQuery can manipulate it.
            Div divWrap = new Div("panrestab", avail);
            p.AddElement(divWrap);
            
            // Code to localize the jQuery Datepicker.
            Locale loc = usr.getLocale();
            String lang = loc.getLanguage();
            if (lang.compareTo("en") == 0) 
                {
                // English is the default in jQuery and denoted by an empty string.
                lang = "";
                }
            String jsCode = "var user_lang = '" + lang + "';\n";
            p.AddJSCode(jsCode);
            
            // Code to create 2 javascript objects of type Date. These are start
            // and end for the reservation. When first displayed the page always
            // has the arrive date as today and the departure as tomorrow.
            GregorianCalendar perStart = hot.getDateTime();
            GregorianCalendar perEnd   = hot.getDateTime();
            perEnd.add(GregorianCalendar.DAY_OF_MONTH, 1);
            
            String pstartyear = Integer.toString(perStart.get(GregorianCalendar.YEAR));
            String pstartmonth = Integer.toString(perStart.get(GregorianCalendar.MONTH));
          //if (pstartmonth.length() == 1) { pstartmonth = "0" + pstartmonth; }
            String pstartday = Integer.toString(perStart.get(GregorianCalendar.DAY_OF_MONTH));
          //if (pstartday.length() == 1) { pstartday = "0" + pstartday; }
            
            String pendyear = Integer.toString(perEnd.get(GregorianCalendar.YEAR));
            String pendmonth = Integer.toString(perEnd.get(GregorianCalendar.MONTH));
          //if (pendmonth.length() == 1) { pendmonth = "0" + pendmonth; }
            String pendday = Integer.toString(perEnd.get(GregorianCalendar.DAY_OF_MONTH));
          //if (pendday.length() == 1) { pendday = "0" + pendday; }
            
            p.AddJSCode("var period_start = new Date(" + pstartyear + "," + pstartmonth + "," + pstartday + ");\n");
            p.AddJSCode("var period_end = new Date(" + pendyear + "," + pendmonth + "," + pendday + ");\n");
            
            // Localized text.
            p.AddJSCode("var err_input = '" + usr.Txt(Loc.ERR_INPUT) + "';\n");
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_reservation", null);
            p.AddElement(extraDiv);
            
            out.print(p.Render());
            }
        catch (Exception ex) 
            {
            out.print(ex.getMessage());
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

    /* --------------------------------------------------------------------- */
    /**
     * Add a drop-down to the panel with all available customers.
     * @param pan Target
     * @param usr Localized text.
     */
    /* --------------------------------------------------------------------- */
    private void AddAvailableCustomers(Panel pan, User usr)
        {
        ArrayList<Customer> customersList = null;
        DBR resCustomers = Delphi.Inst().Customers();
        if (resCustomers.OK()) 
            {
            customersList = (ArrayList)resCustomers.Result();
            }
        else 
            {
            customersList = new ArrayList();
            }
        
        IDropDown idd = pan.AddDropDown(usr.Txt(Loc.CUSTOMER), "idrescust");
      //idd.AddItem(usr.Txt(Loc.CASH), "-1", false);
        
        if (! customersList.isEmpty()) 
            {
            for (Customer c : customersList) 
                {
                c.ShowInDropDown(idd);
                }
            }
        }

    }
