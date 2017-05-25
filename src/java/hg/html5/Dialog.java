package hg.html5;

import hg.util.Util;
import java.util.ArrayList;

/* ========================================================================= */
/**
 * A form element which will be displayed as a dialog by the jQuery lib.
 */
/* ========================================================================= */
public class Dialog extends Widget
    {
    private ArrayList<Widget> _rows = new ArrayList();
    
    /* --------------------------------------------------------------------- */
    /**
     * Create a text input field.
     * @param lab A label for the field.
     * @param txt Initial content.
     * @param cssID CSS identifier.
     * @param maxLen Maximum no. of chars. allowed, 0 = unlimitied.
     */
    /* --------------------------------------------------------------------- */
    public void AddTextInput(String lab, String txt, String cssID, int maxLen) 
        {
        TextInput ti = new TextInput(lab, txt, cssID, maxLen);
        _rows.add(ti);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Datepicker. A text input field which the jQuery lib will turn into
     * a datepicker widget.
     * 
     * @param lab A label for the field.
     * @param txt Initial content.
     * @param cssID CSS identifier.
     */
    /* --------------------------------------------------------------------- */
    public void AddJQDPInput(String lab, String txt, String cssID) 
        {
        DatepickerInput dp = new DatepickerInput(lab, txt, cssID);
        _rows.add(dp);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Create a checkbox input.
     * @param label Label text for the box.
     * @param cssID CSS identifier.
     * @param isChecked True if the box starts out checked.
     */
    /* --------------------------------------------------------------------- */
    public void AddCheckbox(String label, String cssID, boolean isChecked) 
        {
        Checkbox cb = new Checkbox(label, cssID, isChecked);
        _rows.add(cb);
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * A row with a drop-down menu.
     * @param label A label for the drop-down.
     * @param cssID A CSS identifier.
     * @return The object so items can be added.
     */
    /* --------------------------------------------------------------------- */
    public IDropDown AddDropDown(String label, String cssID) 
        {
        DropDown dd = new DropDown(label, cssID);
        _rows.add(dd);
        return dd;
        }

    @Override
    public String Render()
        {
        StringBuilder dlg = new StringBuilder();
        dlg.append("<form>\n");
        
        for (Widget w : _rows) 
            {
            dlg.append(w.Render());
            }
        
        dlg.append("</form>\n");
        
        return dlg.toString();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    
    private class TextInput extends Widget
        {
        private String _label = "";
        private String _content = "";
        private String _cssid = "";
        private int    _maxLength = 0;
        
        public TextInput(String lab, String txt, String cssID, int maxLen) 
            {
            
            if (Util.StringOK(lab)) { _label = lab; }
            if (Util.StringOK(txt)) { _content = txt; }
            if (Util.StringOK(cssID)) { _cssid = cssID; }
            if (maxLen > 0) { _maxLength = maxLen; }
            }
        
        @Override
        public String Render()
            {
            StringBuilder row = new StringBuilder();
            row.append("<div class=\"form_row\">\n");
            row.append("<span class=\"form_label\">").append(_label).append("</span>\n");
            row.append("<span class=\"form_input\"><input type=\"text\"");
            if (_cssid.length() > 0) 
                { 
                row.append(" id=\"").append(_cssid).append("\""); 
                }
            if (_content.length() > 0) 
                { 
                row.append(" value=\"").append(_content).append("\""); 
                }
            if (_maxLength > 0) 
                { 
                row.append(" maxlength=\"").append(Integer.toString(_maxLength)).append("\""); 
                }
            row.append("></span>\n");
            row.append("</div>\n");
            
            return row.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }//END-OF-CLASS TextInput
    
    
    private class DatepickerInput extends Widget 
        {
        private String _label;
        private String _content = "";
        private String _cssid = "";
        
        public DatepickerInput(String lab, String txt, String cssID) 
            {
            _label = lab;
            if (Util.StringOK(txt)) { _content = txt; }
            if (Util.StringOK(cssID)) { _cssid = cssID; }
            }

        @Override
        public String Render()
            {
            StringBuilder row = new StringBuilder();
            row.append("<div class=\"form_row\">\n");
            row.append("<span class=\"form_label\">").append(_label).append("</span>\n");
            row.append("<span class=\"form_input\"><input class=\"dpinput\"");
            if (_cssid.length() > 0) 
                { 
                row.append(" id=\"").append(_cssid).append("\""); 
                }
            if (_content.length() > 0) 
                { 
                row.append(" value=\"").append(_content).append("\""); 
                }
            row.append(" type=\"text\"></span>\n");
            row.append("</div>\n");
            
            return row.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }//END-OF-CLASS DatepickerInput
    
    
    private class Checkbox extends Widget 
        {
        private String  _label   = "";
        private String  _cssid   = "";
        private boolean _checked = false;
        
        public Checkbox(String label, String cssID, boolean isChecked) 
            {
            if (Util.StringOK(label)) 
                {
                _label = label;
                }
            if (Util.StringOK(cssID)) 
                {
                _cssid = cssID;
                }
            _checked = isChecked;
            }

        @Override
        public String Render()
            {
            StringBuilder row = new StringBuilder();
            row.append("<div class=\"form_row\">\n");
            row.append("<span class=\"form_label\">").append(_label).append("</span>\n");
            row.append("<span class=\"form_input\"><input type=\"checkbox\"");
            if (_cssid.length() > 0) 
                { 
                row.append(" id=\"").append(_cssid).append("\""); 
                }
            if (_checked) 
                { 
                row.append(" checked=\"checked\""); 
                }
            row.append("></span>\n");
            row.append("</div>\n");
            
            return row.toString();
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }//END-OF-CLASS Checkbox
    
    private class DropDown extends Widget implements IDropDown 
        {
        private String _label = "";
        private String _cssid = "";
        private ArrayList<DDOption> _options = new ArrayList();
        
        private DropDown(String label, String cssID)
            {
            if (Util.StringOK(label)) 
                {
                _label = label;
                }
            if (Util.StringOK(cssID)) 
                {
                _cssid = cssID;
                }
            }

        @Override
        public String Render()
            {
            StringBuilder row = new StringBuilder();
            row.append("<div class=\"form_row\">\n");
            row.append("<span class=\"form_label\">").append(_label).append("</span>\n");
            row.append("<span class=\"form_input\">\n<select id=\"").append(_cssid).append("\" class=\"dropdowninput\">\n");
            
            for (DDOption o : _options) 
                {
                row.append("<option value=\"").append(o.val).append("\"");
                if (o.sel) 
                    {
                    row.append(" selected");
                    }
                row.append(">").append(o.txt).append("</option>\n");
                }
            
            row.append("</select>\n</span>\n</div>\n");
            
            return row.toString();
            }

        @Override
        public void AddItem(String txt, String value, boolean selected)
            {
            if (! Util.StringOK(txt)) { txt = ""; }
            if (! Util.StringOK(value)) { value = ""; }
            DDOption opt = new DDOption();
            opt.txt = txt;
            opt.val = value;
            opt.sel = selected;
            _options.add(opt);
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        
        }//END-OF-CLASS DropDown
    
    /** Keep track of info for each option in the drop-down. */
    private class DDOption 
        {
        String txt;
        String val;
        boolean sel;
        }
    
    }//END-OF-CLASS Dialog
