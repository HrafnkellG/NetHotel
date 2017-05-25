package hg.html5;

import hg.util.Util;
import java.util.ArrayList;


/* ========================================================================= */
/**
 * The "ul" tag.
 * @author hg
 */
/* ========================================================================= */
public class UnorderedList extends Widget
    {
    private String _class = null;
    private String _id    = null;
    private ArrayList<ListItem> _items = new ArrayList();
    
    public UnorderedList(String cssClass, String cssID) 
        {
        if (Util.StringOK(cssID)) { _id = cssID; }
        if (Util.StringOK(cssClass)) { _class = cssClass; }
        }
    
    public void AddListItem(String content, String cssClass) 
        {
        ListItem li = new ListItem(content, cssClass);
        _items.add(li);
        }
    
    @Override
    public String Render()
        {
        StringBuilder sb = new StringBuilder("<ul");
        if (_class != null) 
            {
            sb.append(" class=\"").append(_class).append("\"");
            }
        if (_id != null) 
            {
            sb.append(" id=\"").append(_id).append("\"");
            }
        sb.append(">\n");
        
        for (ListItem li : _items) 
            {
            sb.append(li.Render());
            }
        
        sb.append("</ul>\n");
        
        return sb.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Holds the "li" tag.
     */
    /* --------------------------------------------------------------------- */
    private class ListItem extends Widget 
        {
        private String _content = "";
        private String _class = null;
        ListItem(String content, String cssClass) 
            {
            if (Util.StringOK(content)) { _content = content; }
            if (Util.StringOK(cssClass)) { _class = cssClass; }
            }
        
        @Override
        public String Render()
            {
            StringBuilder sb = new StringBuilder("<li");
            if (_class != null) 
                { 
                sb.append(" class=\"").append(_class).append("\""); 
                }
            sb.append(">").append(_content).append("</li>\n");
            return sb.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }//END-OF class ListItem
    
    }//END-OF class UnorderedList
