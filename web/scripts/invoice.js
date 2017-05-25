/* **********************************************************************
 * 
 * For the Invoice page.
 * When an item is clicked display a dialog so the user can edit it.
 * 
 * CSS IDs of elements used by the script:
 *      invdlgedit   - jQuery dialog, edit invoice item
 *      invpriceitem - Input element for price-per-item
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      
 * Variables created on the server side:
 *      invedittitle - title for the edit-item dialog
 *      invNoItems   - how many items are in the invoice
 *
 * ********************************************************************** */
jQuery(document).ready(InitInvoice);

/* Arrays whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitInvoice() function. */
var _ButtonsINVEdit = {};
var _ButtonsINVEditWithoutDelete = {};

/* Keep track of which invoice item is being edited. */
var _itemID;

/* ----------------------------------------------------------------------
 * 
 * The page has finished loading, initialize.
 *
 * ---------------------------------------------------------------------- */
function InitInvoice() {
    UPInit(); /* From the userpreferences.js file. */
    $('#invdlgedit').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsINVEdit[help  ] = ButtonEditHelp;
    _ButtonsINVEdit[del   ] = ButtonEditDelete;
    _ButtonsINVEdit[cancel] = ButtonEditCancel;
    _ButtonsINVEdit[save  ] = ButtonEditSave;
    
    _ButtonsINVEditWithoutDelete[help  ] = ButtonEditHelp;
    _ButtonsINVEditWithoutDelete[cancel] = ButtonEditCancel;
    _ButtonsINVEditWithoutDelete[save  ] = ButtonEditSave;
}

/* ----------------------------------------------------------------------
 * 
 * Show help for the edit item dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditHelp() {
    alert('TODO');
}

/* ----------------------------------------------------------------------
 * 
 * Delete the item from the invoice.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditDelete() {
    alert('TODO');
}

/* ----------------------------------------------------------------------
 * 
 * Close the edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() {
    $('#invdlgedit').dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Save the changes made to the item.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() {
    var itemAmount = document.getElementById("invpriceitem").value;
    var params  = "inv_itemamount=" + itemAmount
        + "&inv_itemid=" + _itemID;
    
    // Make the ajax call.
    jQuery.ajax({ 
        url: "InvItemUpdate",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            else {
                // Something went wrong, show the error returned.
                alert(data);
            }
        },
        
        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Show dialog where the user can edit an item in the invoice.
 *
 * ---------------------------------------------------------------------- */
function ItemEdit(itemID) {
    _itemID = itemID;
    var params = "inv_itemid=" + itemID;
    
    // Make the ajax call.
    jQuery.ajax({ 
        url: "InvItemEdit",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;   
            }
            $("#invdlgedit").html(data);
            $('#invdlgedit').dialog("option", "title", invedittitle);
            // If there is only 1 item don not allow delete
            if (invNoItems === 1) {
                $('#invdlgedit').dialog("option", "buttons", _ButtonsINVEditWithoutDelete);
            }
            else {
                $('#invdlgedit').dialog("option", "buttons", _ButtonsINVEdit);
            }
            $('#invdlgedit').dialog('open');
        },

        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Mark the invoice as closed.
 *
 * ---------------------------------------------------------------------- */
function CloseInvoice(invID) {
    var params = "invoiceID=" + invID;
    
    // Make the ajax call.
    jQuery.ajax({ 
        url: "InvClose",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;   
            }
            if (data !== 'ok') {
                alert(data);
            }
            else {
                window.location = "Main";
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}