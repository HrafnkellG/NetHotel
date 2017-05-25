package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Pricelist;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.Div;
import hg.html5.JQAccordion;
import hg.html5.Page;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.intf.IPriceItem;
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
 * Create a page where an admin can create, edit and delete price lists.
 */
/* ========================================================================= */
public class Pricelists extends HttpServlet
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
            // Only available to admins.
            User usr = Util.getAdminForSession(request);
            if (usr == null) 
                {
                response.sendRedirect(Paths.LOGIN);
                return;
                }
            Hotel hot = (Hotel)request.getSession(false).getAttribute(Sess.HOTEL);
            
            Page p = new Page(usr.Txt(Loc.PRICELISTS));
            Util.CommonElementsOnPages(p, usr, hot, PageID.PRICELISTSPAGE);
            p.AddJSLink("scripts/jq/i18n/datepicker-is.js");
            p.AddJSLink("scripts/pricel.js");
            
            // Button for new price list.
            Actions acts = p.getActions();
            acts.AddAction(usr.Txt(Loc.NEWPRICELIST), "javascript:PLNew()");
            
            AddAccordion(p, usr, hot);
            
            // Divs for the jQuery lib, dialogs.
            p.AddDiv("pledit");
            p.AddDiv("plnew");
            
            // JS variables for localized text.
            p.AddJSCode("var pltitlenew='"  + usr.Txt(Loc.NEWPRICELIST)  + "';\n");
            p.AddJSCode("var pltitleedit='" + usr.Txt(Loc.EDITPRICELIST) + "';\n");
            
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
            // and end for the active period.
            GregorianCalendar perStart = hot.getDateTime();
            GregorianCalendar perEnd   = hot.getDateTime();
            perEnd.add(GregorianCalendar.DAY_OF_MONTH, 30);
            
            String pstartyear = Integer.toString(perStart.get(GregorianCalendar.YEAR));
            String pstartmonth = Integer.toString(perStart.get(GregorianCalendar.MONTH));
            if (pstartmonth.length() == 1) { pstartmonth = "0" + pstartmonth; }
            String pstartday = Integer.toString(perStart.get(GregorianCalendar.DAY_OF_MONTH));
            if (pstartday.length() == 1) { pstartday = "0" + pstartday; }
            
            String pendyear = Integer.toString(perEnd.get(GregorianCalendar.YEAR));
            String pendmonth = Integer.toString(perEnd.get(GregorianCalendar.MONTH));
            if (pendmonth.length() == 1) { pendmonth = "0" + pendmonth; }
            String pendday = Integer.toString(perEnd.get(GregorianCalendar.DAY_OF_MONTH));
            if (pendday.length() == 1) { pendday = "0" + pendday; }
            
            p.AddJSCode("var period_start = new Date(" + pstartyear + "," + pstartmonth + "," + pstartday + ");\n");
            p.AddJSCode("var period_end = new Date(" + pendyear + "," + pendmonth + "," + pendday + ");\n");
                
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_pricelist", null);
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

    private void AddAccordion(Page p, User usr, Hotel hot)
        {
        DBR resPrice  = Delphi.Inst().Pricelists();
        if (resPrice.OK()) 
            {
            ArrayList<Pricelist> lists = (ArrayList)resPrice.Result();
            if (! lists.isEmpty()) 
                {
                // Column captions for the tables.
                String[] headerCaptions = new String[8];
                headerCaptions[0] = usr.Txt(Loc.ROOMNO);
                headerCaptions[1] = usr.Txt(Loc.TYPE);
                headerCaptions[2] = "1";
                headerCaptions[3] = "2";
                headerCaptions[4] = "3";
                headerCaptions[5] = "4";
                headerCaptions[6] = ">4";
                headerCaptions[7] = usr.Txt(Loc.EXTRACOT);

                JQAccordion acc = p.getAccordion();
                for (Pricelist price : lists) 
                    {
                    // Buttons to edit and copy
                    Actions plActs = new Actions(CSS.CTABLEACTIONS);
                    plActs.AddAction(usr.Txt(Loc.EDIT), "javascript:PLEdit(" + price.getDBID() + ")");
                    plActs.AddAction(usr.Txt(Loc.COPYTONEW), "javascript:PLEditCTN(" + price.getDBID() + ")");

                    // Table for the price items.
                    Table tbl = new Table(headerCaptions, CSS.CGRID);
                    TableHead thead = tbl.getHead();
                    thead.setCellCSS(0, CSS.CCOL_RNO);
                    thead.setCellCSS(1, CSS.CCOL_RTYPE);
                    thead.setCellCSS(2, CSS.CCOL_NUMBER);
                    thead.setCellCSS(3, CSS.CCOL_NUMBER);
                    thead.setCellCSS(4, CSS.CCOL_NUMBER);
                    thead.setCellCSS(5, CSS.CCOL_NUMBER);
                    thead.setCellCSS(6, CSS.CCOL_NUMBER);
                    thead.setCellCSS(7, CSS.CCOL_NUMBER);

                    ArrayList<IPriceItem> pitems = price.getItems();
                    TableBody tbody = tbl.getBody();
                    boolean isOdd = true;
                    for (IPriceItem item : pitems) 
                        {
                        TableRow row = tbody.AddRow();
                        if (isOdd) 
                            {
                            row.AddCSSClass(CSS.CROW_ODD);
                            }
                        else 
                            {
                            row.AddCSSClass(CSS.CROW_EVEN);
                            }
                        isOdd = !isOdd;
                        row.AddCSSClass(CSS.CROW_NOTHOVER);
                        item.ShowInRow(row, hot);
                        }

                    acc.AddPleat(price.getCaption(), plActs, tbl);
                    }
                }
            }
        }//END-OF-METHOD AddAccordion

    }//END-OF-CLASS Pricelists
