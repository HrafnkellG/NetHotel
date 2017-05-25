package hg.html5;

/* ========================================================================= */
/**
 * Add items to a drop-down menu in a dialog, a select element on the page.
 */
/* ========================================================================= */
public interface IDropDown
    {
    /* --------------------------------------------------------------------- */
    /**
     * Item in the drop-down.
     * @param txt Displayed text.
     * @param value What this selection-item stands for.
     * @param selected True if selected.
     */
    /* --------------------------------------------------------------------- */
    void AddItem(String txt, String value, boolean selected);
    }
