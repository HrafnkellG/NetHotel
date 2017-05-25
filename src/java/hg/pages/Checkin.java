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
 * Display all the guests of a reservation which have not been checked in.
 */
/* ========================================================================= */
public class Checkin extends HttpServlet
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
            String sresID = request.getParameter("resID");
            int    iresID = Integer.parseInt(sresID);
            
            Page ciPage = new Page(usr.Txt(Loc.APPTITLE));
            Util.CommonElementsOnPages(ciPage, usr, hot, PageID.CHECKIN);
            ciPage.AddJSLink("scripts/checkin.js");
            
            // Action buttons
            Actions actBtns = ciPage.getActions();
            actBtns.AddAction(usr.Txt(Loc.CHECKINCHOSEN), "javascript:doCheckin()");
            actBtns.AddAction(usr.Txt(Loc.VIEWINVOICE), "javascript:alert('TODO')");
            
            // Create table
            String[] headerNames = new String[5];
            headerNames[0] = CreateMasterCheckbox();
            headerNames[1] = usr.Txt(Loc.NAME);
            headerNames[2] = usr.Txt(Loc.ROOMNO);
            headerNames[3] = usr.Txt(Loc.ARRIVING);
            headerNames[4] = usr.Txt(Loc.DEPARTING);
            Table tbl = ciPage.AddTable(headerNames, null);
            
            TableHead tblHead = tbl.getHead();
            tblHead.setCellCSS(0, CSS.CCOL_CHECKBOX);
            tblHead.setCellCSS(1, CSS.CCOL_NAME);
            tblHead.setCellCSS(2, CSS.CCOL_RNO);
            tblHead.setCellCSS(3, CSS.CCOL_DATE);
            tblHead.setCellCSS(4, CSS.CCOL_DATE);
            
            TableBody tblBody = tbl.getBody();
            
            // Guests in the reservation
            DBR qresult = Delphi.Inst().Reservation(iresID);
            Res reservation = (Res)qresult.Result();
            ArrayList<ResGuest> guests = reservation.getGuests();
            boolean isOdd = true;
            for (ResGuest g : guests) 
                {
                if (! g.IsCheckedIn()) 
                    {
                    TableRow row = tblBody.AddRow();
                    g.ShowInCheckin(row);
                    String gcb = CreateGuestCheckbox(g.getID());
                    row.setCell(0, CSS.CCELL_CHECKBOX, gcb);
                    
                    String oddEven = CSS.CROW_EVEN;
                    if (isOdd) { oddEven = CSS.CROW_ODD; }
                    isOdd = !isOdd;
                    row.AddCSSClass(oddEven);
                    row.AddCSSClass(CSS.CROW_NOTHOVER);
                    }
                }
            
            // An extra div for decorative purposes.
            Div extraDiv = new Div("extra_checkin", null);
            ciPage.AddElement(extraDiv);
            
            out.print(ciPage.Render());
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
     * A kluge, should be part of TableRow...
     * @param id Guest id in the database
     * @return An html input element with type = checkbox
     */
    /* --------------------------------------------------------------------- */
    private String CreateGuestCheckbox(int id)
        {
        String icb = "<input type=\"checkbox\" class=\"cbguest\" onclick=\"GuestCBClicked(this)\" data-guestid=\""
            + id + "\" />";
        return icb;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * A kluge, should be part of TableHead...
     * 
     * @return An html input element with type = checkbox
     */
    /* --------------------------------------------------------------------- */
    private String CreateMasterCheckbox()
        {
        return "<input type=\"checkbox\" id=\"masterCheckBox\" onclick=\"MasterCBClicked(this)\"/>";
        }
    
    }