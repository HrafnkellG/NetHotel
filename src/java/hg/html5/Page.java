package hg.html5;

import hg.util.Util;
import java.util.ArrayList;


/* ========================================================================= */
/**
 * Handle a basic web page.
 */
/* ========================================================================= */
public class Page
    {
    private String              _title      = "";
    private String              _caption    = "";
    private ArrayList<String>   _cssLinks   = new ArrayList();
    private ArrayList<String>   _jsLinks    = new ArrayList();
    private ArrayList<String>   _emptyDivs  = new ArrayList();
    private Menu                _menu       = new Menu();
    private StringBuilder       _jsCode     = new StringBuilder();
    private Actions             _actions;
    private ArrayList<Widget>   _widgets    = new ArrayList();
    private JQTabs              _tabs;
    private JQAccordion         _accordion;
    
    /* --------------------------------------------------------------------- */
    /**
     * Constructor.
     * @param title May be null.
     */
    /* --------------------------------------------------------------------- */
    public Page(String title) 
        {
        if (Util.StringOK(title)) 
            {
            _title = title;
            }
        }
    
    public void AddElement(Widget w) 
        {
        _widgets.add(w);
        }
    
    public void AddCSSLink(String link) 
        {
        if (Util.StringOK(link)) 
            {
            _cssLinks.add(link);
            }
        }
    
    /** Link to javascript file. */
    public void AddJSLink(String link) 
        {
        if (Util.StringOK(link)) 
            {
            _jsLinks.add(link);
            }
        }
    
    public void AddJSCode(String jsCode) 
        {
        if (Util.StringOK(jsCode)) 
            {
            _jsCode.append(jsCode);
            }
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Adds a table to the page.
     * @param colNames Names of the column headers.
     * @param divCSS The css ID for the enclosing div.
     */
    /* --------------------------------------------------------------------- */
    public Table AddTable(String[] colNames, String divCSS) 
        {
        Table t = new Table(colNames, divCSS);
        _widgets.add(t);
        return t;
        }
    
    /** Create an empty div element with the specified id. */
    public void AddDiv(String id) 
        {
        if (Util.StringOK(id)) 
            {
            _emptyDivs.add(id);
            }
        }
    
    public Menu getMenu() 
        {
        return _menu;
        }
    
    public void setCaption(String cap) 
        {
        if (Util.StringOK(cap)) 
            {
            _caption = cap;
            }
        }
    
    public JQTabs getTabs() 
        {
        _tabs = new JQTabs();
        return _tabs;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Convert this object into an HTML 5 compliant web page.
     */
    /* --------------------------------------------------------------------- */
    public String Render() 
        {
        StringBuilder sb = new StringBuilder();
        
        // First 3 lines of the page.
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n");
        
        // Head.
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
        
        // Title.
        sb.append("<title>").append(_title).append("</title>\n");
        
        // CSS links.
        for (String cssl : _cssLinks) 
            {
            sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssl).append("\" />\n");
            }
        
        // Javascript code
        if (_jsCode.length() > 0) 
            {
            sb.append("<script type=\"text/javascript\">\n");
            sb.append(_jsCode);
            sb.append("</script>\n");
            }
        
        // Javascript links.
        for (String jsl : _jsLinks) 
            {
            sb.append("<script type=\"text/javascript\" src=\"").append(jsl).append("\"></script>\n");
            }
        
        // Close head.
        sb.append("</head>\n");
        
        // Body start
        sb.append("<body>\n<div id=\"container\">\n");
        
        // Caption
        sb.append("<div id=\"nhtitle\"><pre>").append(_caption).append("</pre></div>\n");
        
        // Menu
        sb.append(_menu.Render());
        
        // Action buttons
        if (_actions != null) 
            {
            sb.append(_actions.Render());
            }
        
        for (Widget w : _widgets) 
            {
            sb.append(w.Render());
            }
        
        // Tabs
        if (_tabs != null) 
            {
            sb.append(_tabs.Render());
            }
        
        // Accordion
        if (_accordion != null) 
            {
            sb.append(_accordion.Render());
            }
        
        // Create empty div elements for the jQUery lib. (dialogs)
        for (String divID : _emptyDivs) 
            {
            sb.append("<div id=\"").append(divID).append("\"></div>\n");
            }
        
        // Body end
        sb.append("</div>\n</body>\n");
        
        // Close page.
        sb.append("</html>\n");
        
        return sb.toString();
        }

    /* --------------------------------------------------------------------- */
    /**
     * Get the 'actions' object, essentially a row of buttons.
     */
    /* --------------------------------------------------------------------- */
    public Actions getActions()
        {
        _actions = new Actions(null);
        return _actions;
        }

    public JQAccordion getAccordion()
        {
        _accordion = new JQAccordion();
        return _accordion;
        }
    }
