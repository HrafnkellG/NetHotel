package hg.html5;

import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * Create a help page.
 */
/* ========================================================================= */
public class HelpPage extends Widget
    {
    private String _webTitle = "";
    private String _contentTitle = "";
    ArrayList<Section> _sects = new ArrayList();
    ArrayList<String> _cssLinks = new ArrayList();
    
    /* --------------------------------------------------------------------- */
    /**
     * CONSTRUCTOR
     * 
     * @param webpageTitle Title of the web-page.
     * @param contentTitle Title of the help content.
     */
    /* --------------------------------------------------------------------- */
    public HelpPage(String webpageTitle, String contentTitle) 
        {
        if (Util.StringOK(_webTitle)) _webTitle = webpageTitle;
        if (Util.StringOK(contentTitle)) _contentTitle = contentTitle;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Add a section to the help page.
     * @param title Title of the help text.
     * @param content The help content.
     */
    /* --------------------------------------------------------------------- */
    public void AddSection(String title, String content) 
        {
        Section sec = new Section(title, content);
        _sects.add(sec);
        }
    
    public void AddCSSLink(String link) 
        {
        if (Util.StringOK(link)) 
            {
            _cssLinks.add(link);
            }
        }
    
    @Override
    public String Render()
        {
        StringBuilder p = new StringBuilder();
        p.append("<!DOCTYPE html>\n");
        p.append("<html>\n");
        
        p.append("<head>\n");
        p.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" />");
        p.append("<title>").append(_webTitle).append("</title>\n");
        for (String link : _cssLinks) 
            {
            p.append("<link rel=\"stylesheet\" href=\"").append(link).append("\" type=\"text/css\" />\n");
            }
        p.append("</head>\n");
        
        p.append("<body>\n");
        p.append("<div id=\"container\">\n");
        
        // Help page title
        p.append("<div id=\"title\">\n");
        p.append("<span>").append(_contentTitle).append("</span>\n");
        p.append("</div>\n");
        
        // Sections
        for (Widget w : _sects) 
            {
            p.append(w.Render());
            }
        
        p.append("</div>\n");
        p.append("</body>\n");
        
        p.append("</html>\n");
        
        return p.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    /* ========================================================================= */
    /**
     * Private class for the sections of the help-page.
     */
    /* ========================================================================= */
    private class Section extends Widget 
        {
        private String _title = "";
        private String _cont = "";
        
        /* --------------------------------------------------------------------- */
        /**
         * CONSTRUCTOR
         */
        /* --------------------------------------------------------------------- */
        public Section(String title, String content) 
            {
            _title = title;
            _cont = content;
            }
        
        
        @Override
        public String Render()
            {
            StringBuilder sec = new StringBuilder();
            sec.append("<div class=\"section\">\n");
            
            if (Util.StringOK(_title)) 
                {
                sec.append("<span class=\"section_title\">").append(_title).append("</span>\n");
                }
            
            sec.append("<span class=\"section_text\">").append(_cont).append("</span>\n");
            sec.append("</div>\n");
            
            return sec.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
    }
