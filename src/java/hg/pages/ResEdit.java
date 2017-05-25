package hg.pages;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Res;
import hg.db.ResGuest;
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
 * Show a single reservation and the guests assigned to it.
 */
/* ========================================================================= */
public class ResEdit extends HttpServlet
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
            
            // Verify we  have a reservation number.
            String resID = request.getParameter("res_id");
            if (resID == null) 
                {
                out.print("<p>Missing reservation id.</p>");
                return;
                }
            int iresID = Integer.parseInt(resID);
            
            // Fetch the reservation.
            DBR result = Delphi.Inst().Reservation(iresID);
            if (! result.OK()) 
                {
                out.print("<p>Reservation not found.</p>");
                return;
                }
            Res reservation = (Res)result.Result();
            
            // Create the page.
            Page p = CreateReservationPage(usr, hot, reservation);
            out.print(p.Render());
            }
        finally
            {
            out.close();
            }
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Create the page 'Edit reservation'.
     * @param usr Supplies localized text.
     * @param reservation The reservation to show on the page.
     * @return The page.
     */
    /* --------------------------------------------------------------------- */
    private Page CreateReservationPage(User usr, Hotel hot, Res reservation)
        {
        Page p = new Page(usr.Txt(Loc.EDITRESERVATION));
        Util.CommonElementsOnPages(p, usr, hot, PageID.RESERVATION_EDIT);
        p.AddJSLink("scripts/resedit.js");
        p.AddJSCode("var reservation_id = " + reservation.getDBID() + ";\n");
        
        Actions abtns = p.getActions();
        abtns.AddAction(usr.Txt(Loc.INVOICE), "Invoice?invID=" + reservation.getInvoiceID());
        abtns.AddAction(usr.Txt(Loc.ADDGUEST), "javascript:alert('TO-DO')");
        abtns.AddAction(usr.Txt(Loc.DELETE), "javascript:DeleteReservation()");
        
        // Div for jQuery dialog, edit a guest.
        p.AddDiv("res_edit_guest");
        
        // Localized text.
        p.AddJSCode("var guest_edit_title = '" + usr.Txt(Loc.EDIT) + "';\n");
        
        String[] colHeaders = 
            {
            usr.Txt(Loc.NAME),
            usr.Txt(Loc.IDENTIFIER),
            usr.Txt(Loc.NATIONALITY),
            usr.Txt(Loc.SEX),
            usr.Txt(Loc.ROOMNO)
            };
        Table rtbl = p.AddTable(colHeaders, null);
        TableHead tblHead = rtbl.getHead();
        tblHead.setCellCSS(0, CSS.CCOL_NAME);
        tblHead.setCellCSS(1, CSS.CCOL_ID);
        tblHead.setCellCSS(2, CSS.CCOL_NAT);
        tblHead.setCellCSS(3, CSS.CCOL_SEX);
        tblHead.setCellCSS(4, CSS.CCOL_RNO);
        rtbl.setCaption(usr.Txt(Loc.GUESTSINRES) + " (" + reservation.getCustomerName() + ")");
        
        TableBody tbody = rtbl.getBody();
        ArrayList<ResGuest> guestList = reservation.getGuests();        
        boolean isOdd = true;
        for (ResGuest g : guestList) 
            {
            TableRow r = tbody.AddRow();
            String rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN;
            isOdd = ! isOdd;
            r.AddCSSClass(rowClass);
            r.setEventOnclick("EditGuest(" + g.getID() + ")");
            g.ShowInRow(r, usr);
            }
        
        // An extra div for decorative purposes.
        Div extraDiv = new Div("extra_resedit", null);
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
