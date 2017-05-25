package hg.html5;

import java.util.ArrayList;

/* ========================================================================= */
/**
 * Implement the jQuery Accordion widget.
 */
/* ========================================================================= */
public class JQAccordion extends Widget
    {
    private ArrayList<Widget> _pleats = new ArrayList();

    /* --------------------------------------------------------------------- */
    /**
     * Add one of the "pleats" of the accordion.
     * 
     * @param title The caption for the pleat.
     * @param acts Action buttons above the table.
     * @param tbl A table holding the actual price list.
     */
    /* --------------------------------------------------------------------- */
    public void AddPleat(String title, Actions acts, Table tbl) 
        {
        Pleat aPleat = new Pleat(title, acts, tbl);
        _pleats.add(aPleat);
        }
    
    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder();
        // Open containing div
        sb.append("<div id=\"accordion\">\n");
        
        for (Widget w : _pleats) 
            {
            sb.append(w.Render());
            }
        
        // Close containing div
        sb.append("</div>\n");
        return sb.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    /**
     * A pleat in the accordion.
     */
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    private class Pleat extends Widget
        {
        private String  _title;
        private Actions _act;
        private Table   _table;
        
        public Pleat(String title, Actions acts, Table tbl)
            {
            _title = title;
            _act = acts;
            _table = tbl;
            }

        @Override
        public String Render()
            {
            StringBuilder sb = new StringBuilder();
            
            // First the title, above the containing div.
            sb.append("<span><pre>").append(_title).append("</pre></span>\n");
            
            // Open the containing div.
            sb.append("<div>\n");
            
            // Add action buttons.
            sb.append(_act.Render());
            
            // Add the price list itself.
            sb.append(_table.Render());
            
            // Close containing div.
            sb.append("</div>\n");
            
            return sb.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    
    }//END-OF-CLASS JQAccordion
