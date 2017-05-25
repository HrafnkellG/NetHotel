package hg.html5;

import hg.cons.CSS;
import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * Tabs under the control of the jQuery library.
 * 
 * @author hg
 */
/* ========================================================================= */
public class JQTabs extends Widget
    {
    private ArrayList<Tab> _tabs = new ArrayList();
    
    @Override
    public String Render()
        {
        // No use rendering an empty list.
        if (_tabs.isEmpty())
            {
            throw new UnsupportedOperationException();
            }
        
        StringBuilder sb = new StringBuilder();
        
        // Open containing div element.
        sb.append("<div id=\"");
        sb.append(CSS.IJQTABS);
        sb.append("\">\n");
        
        // Open the ul element.
        sb.append("<ul>\n");
        
        // Add the tabs.
        for (Tab t : _tabs)
            {
            sb.append(t.Render());
            }
        
        // Close the ul element.
        sb.append("</ul>\n");
        
        // Add a div element which jQuery uses to display the data.
        sb.append("<div></div>\n");
        
        // Close containing div element.
        sb.append("</div>\n");
        
        return sb.toString();
        }
/* --------------------------------------------------------------------- */
    /**
     * Adds a new tab.
     * 
     * @param val Caption for the tab.
     * @param ajax URL for the ajax call which will populate the tab.
     */
    /* --------------------------------------------------------------------- */
    public void AddTab(String val, String ajax)
        {
        if (! Util.StringOK(val) || ! Util.StringOK(ajax))
            {
            throw new IllegalArgumentException();
            }
        
        Tab t = new Tab(val, ajax);
        _tabs.add(t);
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    /**
     * Embedded class representing a single tab.
     */
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    private class Tab extends Widget
        {
        private String _val;
        private String _ajaxCall;
        
        Tab(String value, String ajaxCall)
            {
            _val = value;
            _ajaxCall = ajaxCall;
            }

        @Override
        public String Render()
            {
            StringBuilder sb = new StringBuilder();
            sb.append("<li><a href=\"");
            sb.append(_ajaxCall);
            sb.append("\">");
            sb.append(_val);
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
