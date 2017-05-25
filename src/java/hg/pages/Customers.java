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
import hg.db.User;
import hg.html5.Actions;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * View, add, edit and delete customers.
 */
/* ========================================================================= */
public class Customers extends HttpServlet
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
        HttpSession theSession = request.getSession(false);
        Hotel hot = (Hotel)theSession.getAttribute(Sess.HOTEL);
            
            Page p = new Page(usr.Txt(Loc.CUSTOMERS));
            Util.CommonElementsOnPages(p, usr, hot, PageID.CUSTOMERS);
            
            // Elements specific to the customers page.
            //
            // The javascript.
            p.AddJSLink("scripts/customers.js");
            
            // The action button: create a new customer.
            Actions custActions = p.getActions();
            custActions.AddAction(usr.Txt(Loc.NEWCUSTOMER), "javascript:NewCustomer()");
            
            CreateCustomersTable(p, usr);
            
            // Localized text.
            p.AddJSCode("var ctitlenew = '"  + usr.Txt(Loc.NEWCUSTOMER)  + "';\n");
            p.AddJSCode("var ctitleedit = '" + usr.Txt(Loc.EDITCUSTOMER) + "';\n");
            
            // Div for dialogs.
            p.AddDiv("customernew");
            p.AddDiv("customeredit");
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_customers", null);
            p.AddElement(extraDiv);
            
            out.print(p.Render());
            }
        finally
            {
            out.close();
            }
        }
    
    /* --------------------------------------------------------------------- */
    /**
     *                      CreateCustomersTable
     */
    /* --------------------------------------------------------------------- */
    private void CreateCustomersTable(Page p, User usr) 
        {
        // Get defined customers.
        DBR res = Delphi.Inst().Customers();
        if (! res.OK()) 
            {
            return;
            }
        ArrayList<Customer> clist = (ArrayList)res.Result();
        if (clist.isEmpty()) 
            {
            return;
            }

        // Column Header Text
        String[] cht = new String[4];
        cht[0] = usr.Txt(Loc.NAME);
        cht[1] = usr.Txt(Loc.ADDRESS);
        cht[2] = usr.Txt(Loc.PRICELIST);
        cht[3] = usr.Txt(Loc.DISCOUNT);

        Table tbl = p.AddTable(cht, CSS.CGRID);
        tbl.setCaption(usr.Txt(Loc.DEFINEDCUSTOMERS));

        // Set column header CSS classes.
        TableHead thead = tbl.getHead();
        thead.setCellCSS(0, CSS.CCOL_NAME);
        thead.setCellCSS(1, CSS.CCOL_NAME);
        thead.setCellCSS(2, CSS.CCOL_NAME);
        thead.setCellCSS(3, CSS.CCOL_NUMBER);

        TableBody tbody = tbl.getBody();
        boolean isOdd = true;
        for (Customer c : clist) 
            {
            TableRow row = tbody.AddRow();
            row.setEventOnclick("CustomerEdit(" + c.getDBIdentifier() + ")");

            if (isOdd) 
                {
                row.AddCSSClass(CSS.CROW_ODD);
                isOdd = false;
                }
            else 
                {
                row.AddCSSClass(CSS.CROW_EVEN);
                isOdd = true;
                }
            c.ShowInRow(row);
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
