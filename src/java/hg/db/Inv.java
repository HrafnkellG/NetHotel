package hg.db;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.html5.Table;
import hg.html5.TableBody;
import hg.html5.TableFoot;
import hg.html5.TableHead;
import hg.html5.TableRow;
import hg.util.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;


/* ========================================================================= */
/**
 * An invoice in the system.
 * 
 * @author hg
 */
/* ========================================================================= */
public class Inv
    {
    int     _id;
    String  _user;
    Date    _created;
    int     _customer;     // Table: Customer.id
    String  _customerName; // Table: Customer.firstname
    boolean _closed;
    
    private ArrayList<InvItem> _items = new ArrayList();

    /* --------------------------------------------------------------------- */
    /**
     * Adds an item to the invoice.
     * @param item The item to add.
     */
    /* --------------------------------------------------------------------- */
    void addItem(InvItem item)
        {
        _items.add(item);
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show the items of this invoice in a table.
     * 
     * @param usr For localized text.
     * @param hot For date and currency formatting.
     * @return The created table with the items of this invoice.
     */
    /* --------------------------------------------------------------------- */
    public Table ShowItems(User usr, Hotel hot) 
        {
        String[] colHeaders = new String[5];
        colHeaders[0] = usr.Txt(Loc.ITEM);
        colHeaders[1] = usr.Txt(Loc.DESCRIPTION);
        colHeaders[2] = usr.Txt(Loc.NUMBER_SHORTENED);
        colHeaders[3] = usr.Txt(Loc.PRICE);
        colHeaders[4] = usr.Txt(Loc.TOTAL);
        Table t = new Table(colHeaders, "invitems");
        t.setCaption(usr.Txt(Loc.ITEMS));
        
        TableHead th = t.getHead();
        th.setCellCSS(0, CSS.CCOL_RNO);
        th.setCellCSS(1, CSS.CCOL_NAME);
        th.setCellCSS(2, CSS.CCOL_NUMBER);
        th.setCellCSS(3, CSS.CCOL_NUMBER);
        th.setCellCSS(4, CSS.CCOL_NUMBER);
        
        String rowClass;
        boolean isOdd = true;
        TableBody tb = t.getBody();
        long sumTotal = 0;
        for (InvItem item : _items) 
            {
            sumTotal += item._total;
            rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            isOdd = !isOdd;
            TableRow row = tb.AddRow();
            row.AddCSSClass(rowClass);
            row.setEventOnclick("ItemEdit("  + item._itemid + ")");
            item.showInRow(row, hot);
            }
        
        // Create the footer for the table. It has 2 rows:
        //   1. VAT tax percentage and VAT amount, but only if VAT is applied.
        //   2. The sum total for the invoice.
        TableFoot tf = t.getFoot();
        if (hot._vat > 0.0) 
            {
            String vatLabel = usr.Txt(Loc.VAT) + " " + hot.getVatAsString() + "%";
            long baseAmount = (long)(sumTotal / ((hot._vat / 100) + 1));
            long vatAmount = sumTotal - baseAmount;
            TableRow vatRow = tf.AddRow();
            vatRow.AddCSSClass("inv_footer");
            vatRow.setCell(3, null, vatLabel);
            vatRow.setCell(4, CSS.CCELL_NUMBER + " invf_vatnum", hot.MoneyToString(vatAmount));
            }
        
        TableRow sumRow = tf.AddRow();
        sumRow.AddCSSClass("inv_footer");
        sumRow.setCell(3, null, usr.Txt(Loc.SUMTOTAL));
        sumRow.setCell(4, CSS.CCELL_NUMBER + " invf_sumnum", hot.MoneyToString(sumTotal));
        
        return t;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Show this invoice in a table-row, this row should have 4 columns:
     *   1. Invoice number
     *   2. Invoice date
     *   3. Customer name
     *   4. Invoice amount
     * 
     * @param row Target
     */
    /* --------------------------------------------------------------------- */
    private void ShowInRow(TableRow row, Hotel hot)
        {
        long invAmount = 0;
        for (InvItem item : _items) 
            {
            invAmount += item._total;
            }
        
        row.setCell(0, CSS.CCELL_NUMBER, Integer.toString(_id));
        row.setCell(1, CSS.CCELL_DATE, Util.DateToIso(getCreated()));
        row.setCell(2, CSS.CCELL_NAME, _customerName);
        row.setCell(3, CSS.CCELL_NUMBER, hot.MoneyToString(invAmount));
        }

    /* --------------------------------------------------------------------- */
    /**
     * Show all invoices which intersect the given period.
     * @param invoices Table target.
     * @param perStart Period start
     * @param perEnd Period end
     * @param hot Hotel config., for currency formatting.
     */
    /* --------------------------------------------------------------------- */
    public static void ShowPeriodInTable(
    Table             invoices, 
    GregorianCalendar perStart, 
    GregorianCalendar perEnd, 
    Hotel             hot)
        {
        DBR res = Delphi.Inst().InvoicesPeriod(perStart, perEnd);
        if (! res.OK()) 
            {
            return;
            }
        ArrayList<Inv> invoicesList = (ArrayList)res.Result();
        if (invoicesList.isEmpty())
            {
            return;
            }
        
        TableBody bod = invoices.getBody();
        String rowClass;
        boolean isOdd = true;
        for (Inv invoice : invoicesList) 
            {
            rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            isOdd = !isOdd;
            TableRow row = bod.AddRow();
            row.AddCSSClass(rowClass);
            if (invoice._closed) { row.AddCSSClass(CSS.CINVCLOSED); }
            row.setEventOnclick("alert('Invoice no. " + invoice._id + "')");
            invoice.ShowInRow(row, hot);
            }
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Show all open invoices in a table.
     * @param invTable Target table.
     */
    /* --------------------------------------------------------------------- */
    public static void ShowOpenInTable(Table invTable, Hotel hot)
        {
        DBR res = Delphi.Inst().InvoicesOpen();
        if (! res.OK()) 
            {
            return;
            }
        ArrayList<Inv> invoicesList = (ArrayList)res.Result();
        if (invoicesList.isEmpty())
            {
            return;
            }
        
        TableBody bod = invTable.getBody();
        String rowClass;
        boolean isOdd = true;
        for (Inv invoice : invoicesList) 
            {
            rowClass = (isOdd) ? CSS.CROW_ODD : CSS.CROW_EVEN ;
            isOdd = !isOdd;
            TableRow row = bod.AddRow();
            row.AddCSSClass(rowClass);
            // invShow() is a javascript function in main.js
            row.setEventOnclick("TabInvoicesRowSelect(" + invoice._id + ")");
            invoice.ShowInRow(row, hot);
            }
        }

    public Date getCreated()
        {
        return _created;
        }

    public int getCustomer()
        {
        return _customer;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * The number of items in the invoice.
     */
    /* --------------------------------------------------------------------- */
    public int getNoItems() 
        {
        return _items.size();
        }

    public boolean isClosed()
        {
        return _closed;
        }

    public String getID()
        {
        return Integer.toString(_id);
        }
    
    }//END-OF class Inv
