package hg.db;

import hg.util.Util;

/* ========================================================================= */
/**
 * (D)ata(B)ase(R)esult object. Holds information about the result of the
 * query to the database.
 */
/* ========================================================================= */
public class DBR
    {
    private Object  _result     = null;
    private boolean _queryOK    = true;
    private String  _failureMsg = "";
    
    /* --------------------------------------------------------------------- */
    /**
     * Mark the query as failed.
     * @param failureMsg Text describing the failure.
     */
    /* --------------------------------------------------------------------- */
    void Failed(String failureMsg) 
        {
        _queryOK = false;
        if (Util.StringOK(failureMsg)) 
            {
            _failureMsg = failureMsg;
            }
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * What the query returned.
     */
    /* --------------------------------------------------------------------- */
    void setResult(Object res) 
        {
        _result = res;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * The result of the query. Cast it to the type of object expected.
     */
    /* --------------------------------------------------------------------- */
    public Object Result() 
        {
        return _result;
        }
    
    /* --------------------------------------------------------------------- */
    /**
     * Returns true if the query completed successfully.
     */
    /* --------------------------------------------------------------------- */
    public boolean OK() 
        {
        return _queryOK;
        }
    
    public String ErrorMessage() 
        {
        return _failureMsg;
        }
    
    }
