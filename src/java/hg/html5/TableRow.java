package hg.html5;

import hg.util.Util;


/* ========================================================================= */
/**
 * A 'tr' element in a table, can reside in 'tbody' or 'tfoot' but not 'thead'.
 */
/* ========================================================================= */
public class TableRow extends Widget
    {
    /** The cells in the row. */
    private Cell[] _cells;
    
    /** CSS class names for the row. */
    private String _cssClass = "";
    
    /** CSS id for the row. */
    private String _cssID;
    
    /** Javascript function call for the 'onclick' event. */
    private String _onclick;
    
    /** A pop-up for the row. */
    private String _titleText = null;
    
    /* --------------------------------------------------------------------- */
    /**
     * Create as many empty cells as there are columns in the table.
     * @param numOfCells Implicitly the number of columns.
     */
    /* --------------------------------------------------------------------- */
    TableRow(int numOfCells)
        {
        if (numOfCells <= 0)
            {
            throw new IllegalArgumentException("numOfCells must be > 0.");
            }
        
        _cells = new Cell[numOfCells];
        
        for (int i=0; i<numOfCells; i++)
            {
            _cells[i] = new Cell();
            }
        }

    @Override
    public String Render()
        {
        // Opening tag for the row.
        StringBuilder sb = new StringBuilder("<tr");
        if (_titleText != null) 
            {
            sb.append(" title=\"").append(_titleText).append("\"");
            }
        if (_cssClass != null)
            {
            sb.append(" class=\"");
            sb.append(_cssClass);
            sb.append("\"");
            }
        if (_cssID != null)
            {
            sb.append(" id=\"");
            sb.append(_cssID);
            sb.append("\"");
            }
        if (_onclick != null)
            {
            sb.append(" onclick=\"");
            sb.append(_onclick);
            sb.append("\"");
            }
        sb.append(">\n");
        
        // Insert the cells.
        for (int i = 0; i < _cells.length; i++)
            {
            Cell c = _cells[i];
            sb.append(c.Render());
            }
        
        // Closing tag for the row.
        sb.append("</tr>\n");
        
        return sb.toString();
        }

    /* --------------------------------------------------------------------- */
    /**
     * Adds a CSS class-name to the row.
     * @param className 
     */
    /* --------------------------------------------------------------------- */
    public void AddCSSClass(String className)
        {
        if (Util.StringOK(className))
            {
            if (_cssClass.length() == 0) 
                {
                _cssClass = className;
                }
            else 
                {
                _cssClass += " " + className;
                }
            }
        }

    public void setCSSID(String id)
        {
        if (Util.StringOK(id))
            {
            _cssID = id;
            }
        }

    public void setEventOnclick(String js)
        {
        if (Util.StringOK(js))
            {
            _onclick = js;
            }
        }

    public void setCell(int cellNum, String cssClass, String value)
        {
        if (cellNum < 0 || cellNum > (_cells.length - 1))
            {
            throw new IllegalArgumentException("cellNum is out of bounds.");
            }
        
        Cell c = _cells[cellNum];
        c.setCSSClass(cssClass);
        c.setValue(value);
        }
    
    public void setCellID(int cellNum, String id) 
        {
        if (cellNum < 0 || cellNum > (_cells.length - 1))
            {
            throw new IllegalArgumentException("cellNum is out of bounds.");
            }
        
        Cell c = _cells[cellNum];
        c.setCSSID(id);
        }

    public void setCellClickEvent(int cellNum, String jsFunctionCall)
        {
        if (cellNum < 0 || cellNum > (_cells.length - 1))
            {
            throw new IllegalArgumentException("cellNum is out of bounds.");
            }
        Cell c = _cells[cellNum];
        c.setClickEvent(jsFunctionCall);
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        if (Util.StringOK(titleText)) 
            {
            _titleText = titleText;
            }
        }
    
    
    /* ********************************************************************* */
    /**
     * An embedded class representing a cell in the row.
     */
    /* ********************************************************************* */
    private class Cell extends Widget
        {
        /** The value of the cell. */
        private String _value = "";
        
        /** The CSS class names, if any. */
        private String _cssClassNames;
        
        private String _cellCSSID;
        
        private String _clickEvent;
        
        /* --------------------------------------------------------------------- */
        /**
         * Set what the cell will display.
         * @param val Text to show.
         */
        /* --------------------------------------------------------------------- */
        public void setValue(String val)
            {
            if (Util.StringOK(val))
                {
                _value = val;
                }
            }
        
        /* --------------------------------------------------------------------- */
        /**
         * Set the cells CSS class name(s).
         * @param cssClassName Class names.
         */
        /* --------------------------------------------------------------------- */
        public void setCSSClass(String cssClassName)
            {
            if (Util.StringOK(cssClassName))
                {
                _cssClassNames = cssClassName;
                }
            }
        
        public void setCSSID(String id) 
            {
            if (Util.StringOK(id)) 
                {
                _cellCSSID = id;
                }
            }
        
        @Override
        public String Render()
            {
            // Open the 'td' element.
            StringBuilder sb = new StringBuilder("<td");
            
            if (_cellCSSID != null) 
                {
                sb.append(" id=\"").append(_cellCSSID).append("\"");
                }
            
            if (_cssClassNames != null)
                {
                sb.append(" class=\"");
                sb.append(_cssClassNames);
                sb.append("\"");
                }
            
            if (_clickEvent != null) 
                {
                sb.append(" onclick=\"").append(_clickEvent).append("\"");
                }
            
            sb.append(">");
            sb.append(_value);
            
            // Close the 'td' element.
            sb.append("</td>\n");
            
            return sb.toString();
            }

        private void setClickEvent(String jsFunctionCall)
            {
            if (Util.StringOK(jsFunctionCall)) 
                {
                _clickEvent = jsFunctionCall;
                }
            }

        @Override
        public void TitlePopupText(String titleText)
            {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        
        }
    
    }//END-OF TableRow