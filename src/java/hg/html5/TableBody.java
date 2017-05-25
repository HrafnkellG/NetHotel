package hg.html5;

import java.util.ArrayList;

/* ========================================================================= */
/**
 * A 'tbody' element in a table.
 * @author hg
 */
/* ========================================================================= */
public class TableBody extends Widget
    {
    private int _numCols;
    private ArrayList<TableRow> _rows = new ArrayList();
    
    /* --------------------------------------------------------------------- */
    /**
     * When created it needs the number of columns (i.e. cells).
     * @param numColumns How many columns are in the table.
     */
    /* --------------------------------------------------------------------- */
    TableBody(int numColumns)
        {
        _numCols = numColumns;
        }

    @Override
    public String Render()
        {
        if (_rows.isEmpty())
            {
            // No rows defined, return an empty body.
            return "<tbody><tr><td></td></tr></tbody>\n";
            }
        
        StringBuilder sb = new StringBuilder("<tbody>\n");
        
        for (TableRow r : _rows)
            {
            sb.append(r.Render());
            }
        
        sb.append("</tbody>\n");
        
        return sb.toString();
        }

    public TableRow AddRow()
        {
        TableRow row = new TableRow(_numCols);
        _rows.add(row);
        return row;
        }

    public int RowCount()
        {
        return _rows.size();
        }

    @Override
    public void TitlePopupText(String titleText)
        {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    }
