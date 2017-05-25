package hg.html5;

import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * A 2-level menu, it can have top-level items and each of them can have
 * a drop-down list of sub-items. Those sub-items can not have their own
 * sub-items.
 * 
 * @author hg
 */
/* ========================================================================= */
public class Menu extends Widget
    {
    private ArrayList<MenuItem> _items = new ArrayList();

    @Override
    public String Render()
        {
        // Starts with 2 tags: a div and an ul.
        StringBuilder sb = new StringBuilder();
        
        sb.append("<div id=\"menu\">\n<ul id=\"nav\">\n");
        
        // Add the items.
        for (MenuItem mi : _items)
            {
            sb.append(mi.Render());
            }
        
        // Close the tags.
        sb.append("</ul>\n</div>\n");
        
        return sb.toString();
        }

    /* --------------------------------------------------------------------- */
    /**
     * Add an item to the menu.
     * @param value Display text.
     * @param action Action when selected.
     */
    /* --------------------------------------------------------------------- */
    public MenuItem AddItem(String value, String action)
        {
        // Must have a text to show the user.
        if (!Util.StringOK(value))
            {
            throw new UnsupportedOperationException("Text for menu-item is missing.");
            }
        
        MenuItem mi = new MenuItem(value, action);
        _items.add(mi);
        return mi;
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
