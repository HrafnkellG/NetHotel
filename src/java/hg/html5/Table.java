package hg.html5;

import hg.cons.CSS;
import hg.util.Util;


/* ========================================================================= */
/**
 * A table element.
 */
/* ========================================================================= */
public class Table extends Widget
    {
    /** Table caption. */
    private String _caption;
    
    /** The 'thead' section of the table. */
    private TableHead _head;
    
    /** The 'tfoot' section. */
    private TableFoot _foot;
    
    /** The 'tbody' section. */
    private TableBody _body;
    
    private String _divCSS = null;
    
    /* --------------------------------------------------------------------- */
    /**
     * Create a table.
     * @param colNames Gives us the names of the columns and their number. 
     */
    /* --------------------------------------------------------------------- */
    public Table(String[] colNames, String divCSSClass)
        {
        _head = new TableHead(colNames);
        _foot = new TableFoot(colNames.length);
        _body = new TableBody(colNames.length);
        _divCSS = divCSSClass;
        }

    @Override
    public String Render()
        {
        // Opening tags
        StringBuilder sb = new StringBuilder();
        if (_divCSS != null) 
            {
            sb.append("<div class=\"").append(_divCSS).append("\">\n");
            }
        sb.append("<table class=\"");
        sb.append(CSS.CNHTABLE);
        sb.append("\">\n");
        
        // Add caption if defined.
        if (_caption != null)
            {
            sb.append("<caption>");
            sb.append(_caption);
            sb.append("</caption>\n");
            }
        
        // Add in sequence: head, foot, body
        sb.append(_head.Render());
        sb.append(_foot.Render());
        sb.append(_body.Render());
        
        // Closing tags
        sb.append("</table>\n");
        if (_divCSS != null) 
            {
            sb.append("</div>\n");
            }
        
        return sb.toString();
        }
    
    public void setCaption(String text)
        {
        if (Util.StringOK(text))
            {
            _caption = text;
            }
        }

    public TableHead getHead()
        {
        return _head;
        }

    public TableBody getBody()
        {
        return _body;
        }

    public TableFoot getFoot()
        {
        return _foot;
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
