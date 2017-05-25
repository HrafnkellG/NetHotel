package hg.html5;

import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * A top-level menu-item, it can have zero or more sub-items.
 * 
 * @author hg
 */
/* ========================================================================= */
public class MenuItem extends Widget
    {
    private String _action = "#";
    private String _text;
    
    private ArrayList<SubItem> _subItems = new ArrayList();
    
    /* --------------------------------------------------------------------- */
    /**
     * An item in the top-level of the menu.
     * 
     * @param value Text to display.
     * 
     * @param action URL or javascript.
     */
    /* --------------------------------------------------------------------- */
    MenuItem(String value, String action)
        {
        _text = value;
        if (Util.StringOK(action))
            {
            _action = action;
            }
        }
    
    @Override
    public String Render()
        {
        // Open li element.
        StringBuilder sb = new StringBuilder("<li>\n");
        
        // Action and text.
        sb.append("<a href=\"");
        sb.append(_action);
        sb.append("\">");
        sb.append(_text);
        sb.append("</a>\n");
        
        // Sub-menus.
        if (! _subItems.isEmpty())
            {
            sb.append("<ul>\n");
            for (SubItem msi : _subItems)
                {
                sb.append(msi.Render());
                }
            sb.append("</ul>\n");
            }
        
        // Close li element.
        sb.append("</li>\n");
        
        return sb.toString();
        }

    public void AddSubItem(String value, String action)
        {
        // Must have something to show the user.
        if (! Util.StringOK(value) || ! Util.StringOK(action))
            {
            throw new UnsupportedOperationException("value and action may not be empty.");
            }
        
        SubItem msi = new SubItem(value, action);
        _subItems.add(msi);
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Embedded class for sub-items of top-level items.
     */
    /* --------------------------------------------------------------------- */
    private class SubItem extends Widget
        {
        private String _value;
        private String _action;
        
        SubItem(String value, String action)
            {
            _value = value;
            _action = action;
            }
        
        @Override
        public String Render()
            {
            StringBuilder sb = new StringBuilder("<li><a href=\"");
            sb.append(_action);
            sb.append("\">");
            sb.append(_value);
            sb.append("</a></li>\n");

            return sb.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    
    }
