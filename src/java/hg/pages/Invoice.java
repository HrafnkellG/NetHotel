package hg.pages;

import hg.cons.Loc;
import hg.cons.PageID;
import hg.cons.Paths;
import hg.cons.Sess;
import hg.db.Customer;
import hg.db.DBR;
import hg.db.Delphi;
import hg.db.Hotel;
import hg.db.Inv;
import hg.db.User;
import hg.html5.Actions;
import hg.html5.ConCaption;
import hg.html5.Div;
import hg.html5.Page;
import hg.html5.Paragraph;
import hg.html5.Table;
import hg.html5.UnorderedList;
import hg.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/* ========================================================================= */
/**
 * Show an invoice.
 * @author hg
 */
/* ========================================================================= */
public class Invoice extends HttpServlet
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
            
            // Get the invoice number.
            String sinvNo = request.getParameter("invID");
            int    iinvNo = Integer.parseInt(sinvNo);
            
            // And the invoice.
            DBR dbResult = Delphi.Inst().Invoice(iinvNo);
            if (! dbResult.OK()) 
                {
                out.print("<p>" + dbResult.ErrorMessage() + "</p>");
                return;
                }
            Inv theInvoice = (Inv)dbResult.Result();
            
            // Start creating the page, first the common elements.
            Page p = new Page(usr.Txt(Loc.APPTITLE));
            Util.CommonElementsOnPages(p, usr, hot, PageID.INVLIST);
            p.AddJSLink("scripts/invoice.js");
            
            // Next 3 elements: title, invoice number and invoice date.
            ConCaption invTitle = new ConCaption(usr.Txt(Loc.INVOICE));
            p.AddElement(invTitle);
            
            String sInvNoLabel = usr.Txt(Loc.INVOICE) + " " + usr.Txt(Loc.NUMBER_SHORTENED) + ": ";
            Div invNo = new Div("invno", new Paragraph(sInvNoLabel + sinvNo));
            p.AddElement(invNo);
            
            String creationLabel = usr.Txt(Loc.DATE) + ": ";
            String creationDate = hot.DateToString(theInvoice.getCreated());
            Div invDate = new Div("invdate", new Paragraph(creationLabel + creationDate));
            p.AddElement(invDate);
            
            // Create 2 info panels, one for the buyer and another for the seller.
            // Each panel has 6 fields (name, 2 address lines, city, post, country).
            // If a field is not defined we put an empty one in its place (to
            // keep both panels of the same height).
            
            // First the buyer.
            DBR resultCustomer = Delphi.Inst().Customer(theInvoice.getCustomer());
            if (resultCustomer.OK()) 
                {
                Customer cust = (Customer)resultCustomer.Result();
                String cname  = cust.getName();
                String caddr1 = cust.getAddress1();
                String caddr2 = cust.getAddress2();
                String ccity  = cust.getCity();
                String cpost  = cust.getPostalcode();
                String ccount = cust.getCountry();
                
                UnorderedList list = new UnorderedList(null, null);
                list.AddListItem(usr.Txt(Loc.BUYER), "invbtitle");
                // Only add fields which are defined.
                int addedFields = 0;
                if (Util.StringOK(cname)) { list.AddListItem(cname, null); ++addedFields; }
                if (Util.StringOK(caddr1)) { list.AddListItem(caddr1, null); ++addedFields; }
                if (Util.StringOK(caddr2)) { list.AddListItem(caddr2, null); ++addedFields; }
                if (Util.StringOK(ccity)) { list.AddListItem(ccity, null); ++addedFields; }
                if (Util.StringOK(cpost)) { list.AddListItem(cpost, null); ++addedFields; }
                if (Util.StringOK(ccount)) { list.AddListItem(ccount, null); ++addedFields; }
                while (addedFields < 6) 
                    {
                    list.AddListItem("&nbsp;", null);
                    addedFields++;
                    }
                
                Div buyer = new Div("invbuyerinfo", list);
                p.AddElement(buyer);
                }
            else 
                {
                // Got an error when fetching the customer, output error string.
                Div error = new Div("invbuyerinfo", new Paragraph("Database error"));
                p.AddElement(error);
                }
            
            // Now the panel for the seller, i.e. the hotel.
            String hname  = hot.getName();
            String haddr1 = hot.getAddr1();
            String haddr2 = hot.getAddr2();
            String hcity  = hot.getCity();
            String hcount = hot.getCountry();
            UnorderedList list = new UnorderedList(null, null);
            list.AddListItem(usr.Txt(Loc.SELLER), "invstitle");
            // Only add fields which are defined.
            int addedFields = 0;
            if (Util.StringOK(hname)) { list.AddListItem(hname, null); ++addedFields; }
            if (Util.StringOK(haddr1)) { list.AddListItem(haddr1, null); ++addedFields; }
            if (Util.StringOK(haddr2)) { list.AddListItem(haddr2, null); ++addedFields; }
            if (Util.StringOK(hcity)) { list.AddListItem(hcity, null); ++addedFields; }
            if (Util.StringOK(hcount)) { list.AddListItem(hcount, null); ++addedFields; }
            while (addedFields < 6) 
                {
                list.AddListItem("&nbsp;", null);
                addedFields++;
                }
            
            Div seller = new Div("invsellerinfo", list);
            p.AddElement(seller);
            
            // 3 buttons: add an item / void invoie / print pdf
            Actions buttons = new Actions("actions");
            // If the invoice is open we add the "Close" and "Void" buttons.
            if (!theInvoice.isClosed()) 
                {
                buttons.AddAction(usr.Txt(Loc.CLOSEINVOICE), "javascript:CloseInvoice(" + theInvoice.getID() + ")");
                buttons.AddAction(usr.Txt(Loc.VOIDINVOICE), "javascript:alert('TODO')");
                }
            buttons.AddAction(usr.Txt(Loc.PRINTINVOICE), "javascript:alert('TODO')");
            p.AddElement(buttons);
            
            // Table with the invoice items.
            Table itemTable = theInvoice.ShowItems(usr, hot);
            p.AddElement(itemTable);
            
            // Div for jQuery, used to create dialog.
            // Plus variables needed by the javascript.
            p.AddJSCode("var invedittitle = '" + usr.Txt(Loc.EDITITEM) + "';");
            p.AddJSCode("var invNoItems = " + theInvoice.getNoItems() + ";");
            p.AddDiv("invdlgedit");
            
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
