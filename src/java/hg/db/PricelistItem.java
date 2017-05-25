package hg.db;

import hg.cons.CSS;
import hg.html5.TableRow;
import hg.intf.IPriceItem;

/* ========================================================================= */
/**
 * An item in a price-list.
 */
/* ========================================================================= */
public class PricelistItem implements IPriceItem
    {
    int     _id;
    int     _priceid;
    String  _roomno;
    String  _roomtype;
    int     _roomsize;
    long    _guest1;
    long    _guest2;
    long    _guest3;
    long    _guest4;
    long    _over4;
    long    _extracot;

    @Override
    public void ShowInRow(TableRow row, Hotel hot)
        {
        // Cell 1
        row.setCell(0, CSS.CCELL_RNO, _roomno);
        
        // Cell 2
        row.setCell(1, CSS.CCELL_RTYPE, _roomtype);
        
        // Three parameters needed for the EditCell javascript function:
        // 1. CSS id of the td element. To make it unique we combine the database
        //    id of the price item and the cell number.
        // 2. The database id for the price item.
        // 3. The cell index, starts at 0.
        //
        // Cells 3 and 8 are always editable, 4-7 must be checked.
        
        // Cell 3
        String cssForEditable = CSS.CCELL_NUMBER + " " + CSS.CCELL_EDITABLE;
        String cellCSSID = "C" + Integer.toString(_id) + "2";
        row.setCell(2, cssForEditable, hot.MoneyToString(_guest1));
        row.setCellID(2, cellCSSID);
        row.setCellClickEvent(2, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '2')");
        
        // Cell 4
        if (_roomsize >= 2) 
            {
            cellCSSID = "C" + Integer.toString(_id) + "3";
            row.setCell(3, cssForEditable, hot.MoneyToString(_guest2));
            row.setCellID(3, cellCSSID);
            row.setCellClickEvent(3, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '3')");
            }
        
        // Cell 5
        if (_roomsize >= 3) 
            {
            cellCSSID = "C" + Integer.toString(_id) + "4";
            row.setCell(4, cssForEditable, hot.MoneyToString(_guest3));
            row.setCellID(4, cellCSSID);
            row.setCellClickEvent(4, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '4')");
            }
        
        // Cell 6
        if (_roomsize >= 4) 
            {
            cellCSSID = "C" + Integer.toString(_id) + "5";
            row.setCell(5, cssForEditable, hot.MoneyToString(_guest4));
            row.setCellID(5, cellCSSID);
            row.setCellClickEvent(5, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '5')");
            }
        
        // Cell 7
        if (_roomsize > 4) 
            {
            cellCSSID = "C" + Integer.toString(_id) + "6";
            row.setCell(6, cssForEditable, hot.MoneyToString(_over4));
            row.setCellID(6, cellCSSID);
            row.setCellClickEvent(6, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '6')");
            }
        
        // Cell 8
        cellCSSID = "C" + Integer.toString(_id) + "7";
        row.setCell(7, cssForEditable, hot.MoneyToString(_extracot));
        row.setCellID(7, cellCSSID);
        row.setCellClickEvent(7, "EditCell('" + cellCSSID + "', '" + Integer.toString(_id) + "', '7')");
        
        }

    @Override
    public String roomNo()
        {
        return _roomno;
        }

    @Override
    public long guest1()
        {
        return _guest1;
        }

    @Override
    public long guest2()
        {
        return _guest2;
        }

    @Override
    public long guest3()
        {
        return _guest3;
        }

    @Override
    public long guest4()
        {
        return _guest4;
        }

    @Override
    public long guest5()
        {
        return _over4;
        }
    
    }
