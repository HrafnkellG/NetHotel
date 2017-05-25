package hg.db;

import hg.cons.CSS;
import hg.cons.Loc;
import hg.html5.Dialog;
import hg.html5.TableRow;
import hg.util.Util;
import java.util.Date;


/* ========================================================================= */
/**
 * An item in an invoice.
 * 
 * @author hg
 */
/* ========================================================================= */
public class InvItem
    {
    int     _itemid;
    int     _invid;
    String  _roomno;
    int     _roomsize;
    Date    _arrive;
    Date    _depart;
    long    _pricePrNight;
    long    _total;

    /* --------------------------------------------------------------------- */
    /**
     * Show this invoice item in a row. The row has the following columns:
     *   1. Item identifier, a string. This is NOT the database id.
     *   2. Descriptive text of the item.
     *   3. No. of items purchased.
     *   4. Price of 1 item.
     *   5. Total price for the item.
     * 
     * @param row The target.
     * @param hot For date and currency formatting.
     */
    /* --------------------------------------------------------------------- */
    void showInRow(TableRow row, Hotel hot)
        {
        String arr = hot.DateToString(_arrive);
        String dep = hot.DateToString(_depart);
        int noNights = Util.DifferenceInDays(_arrive, _depart);
        
        row.setCell(0, CSS.CCELL_RNO, _roomno);
        row.setCell(1, CSS.CCELL_NAME, arr + " / " + dep);
        row.setCell(2, CSS.CCELL_NUMBER, Integer.toString(noNights));
        row.setCell(3, CSS.CCELL_NUMBER, hot.MoneyToString(_pricePrNight));
        row.setCell(4, CSS.CCELL_NUMBER, hot.MoneyToString(_total));
        }

    /* --------------------------------------------------------------------- */
    /**
     * There is 1 field in the dialog:
     *    1. Price pr. item
     * 
     * @param dlg Target
     * @param usr Localized text
     * @param hot Currency formatting
     */
    /* --------------------------------------------------------------------- */
    public void showInEditDialog(Dialog dlg, User usr, Hotel hot)
        {
        dlg.AddTextInput(
            usr.Txt(Loc.PRICE),               // Label
            hot.MoneyToString(_pricePrNight), // Initial content
            "invpriceitem",                   // CSS id
            0);                               // Max. length
        }

    /* --------------------------------------------------------------------- */
    /**
     * Change the price per item and compute the new total.
     * 
     * @param itemAmount The new price.
     */
    /* --------------------------------------------------------------------- */
    public void setPricePerItem(long itemAmount)
        {
        _pricePrNight = itemAmount;
        int numOfNights = Util.DifferenceInDays(_arrive, _depart);
        _total = itemAmount * numOfNights;
        }
    }
