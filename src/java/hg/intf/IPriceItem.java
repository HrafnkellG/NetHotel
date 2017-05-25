package hg.intf;

import hg.db.Hotel;
import hg.html5.TableRow;

/* ========================================================================= */
/**
 * An item in a price-list.
 */
/* ========================================================================= */
public interface IPriceItem
    {
    /* --------------------------------------------------------------------- */
    /**
     * Display this item in a row in a table.
     * 
     * @param row The target row.
     */
    /* --------------------------------------------------------------------- */
    void ShowInRow(TableRow row, Hotel hot);
    
    String roomNo();
    long guest1();
    long guest2();
    long guest3();
    long guest4();
    long guest5();
    }
