/* ********************************************************************** 
 * Script for the Customers page. Creates 2 dialogs: one to create a new
 * customer, and a second one to edit an existing customer.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      customernew     - a div element used by jQuery to create a dialog
 *      customeredit    - a div element used by jQuery to create a dialog
 *      cust_name       - text field in dialog
 *      cust_address1   - text field in dialog
 *      cust_address2   - text field in dialog
 *      cust_postal     - text field in dialog
 *      cust_city       - text field in dialog
 *      cust_country    - text field in dialog
 *      cust_pricelist  - drop-down in dialog
 *      cust_disc       - text field in dialog
 *      
 * Variables created on the server side:
 *      del         - localized text
 *      ctitlenew   - localized text dialog title
 *      ctitleedit  - localized text dialog title
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
$(document).ready(InitCustomers);

/* Arrays whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitCustomers() function. */
var _ButtonsCEdit = {};
var _ButtonsCNew = {};

/* The customer which has been chosen for edit. */
var _currentCustomerEdit;

/* ----------------------------------------------------------------------
 * 
 * Page has loaded, run initializers.
 *
 * ---------------------------------------------------------------------- */
function InitCustomers() {
    UPInit(); /* From userpreferences.js */
    $('#customernew').dialog({autoOpen: false, modal: true});
    $('#customeredit').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsCEdit[help  ] = ButtonEditHelp;
    _ButtonsCEdit[del   ] = ButtonEditDelete;
    _ButtonsCEdit[cancel] = ButtonEditCancel;
    _ButtonsCEdit[save  ] = ButtonEditSave;
    
    _ButtonsCNew[help  ] = ButtonNewHelp;
    _ButtonsCNew[cancel] = ButtonNewCancel;
    _ButtonsCNew[save  ] = ButtonNewSave;
}

/* ----------------------------------------------------------------------
 * 
 * Create a dialog so the user can enter info about a new customer.
 *
 * ---------------------------------------------------------------------- */
function NewCustomer() {
    $.ajax({
        url: "CustomerNew",
        cache: false,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            $('#customernew').html(data);
            $('#customernew').dialog("option", "title", ctitlenew);
            $('#customernew').dialog("option", "buttons", _ButtonsCNew);
            $('#customernew').dialog('open');
        },
        
        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * User has selected a customer for edit.
 *
 * ---------------------------------------------------------------------- */
function CustomerEdit(customerID) {
    _currentCustomerEdit = customerID;
    
    var params = "cust_id=" + customerID;
    
    $.ajax({
        url: "CustomerEdit",
        cache: false,
        data: params,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            $('#customeredit').html(data);
            $('#customeredit').dialog("option", "title", ctitleedit);
            $('#customeredit').dialog("option", "buttons", _ButtonsCEdit);
            $('#customeredit').dialog('open');
        },
        
        error: CallbackError
    });
}


/* ----------------------------------------------------------------------
 * 
 * Save info about a new customer.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewSave() {
    // Gather info from the fields.
    var cname  = document.getElementById('cust_name').value;
    var caddr1 = document.getElementById('cust_address1').value;
    var caddr2 = document.getElementById('cust_address2').value;
    var cpost  = document.getElementById('cust_postal').value;
    var ccity  = document.getElementById('cust_city').value;
    var ccount = document.getElementById('cust_country').value;
    var cdisc  = document.getElementById('cust_disc').value;
    
    var plObj   = document.getElementById('cust_pricelist');
    var plIndex = plObj.selectedIndex;
    var cplist  = plObj.options[plIndex].value;
    
    // Parameters
    var params = 
          "cust_name="       + cname
        + "&cust_address1="  + caddr1
        + "&cust_address2="  + caddr2
        + "&cust_postal="    + cpost
        + "&cust_city="      + ccity
        + "&cust_country="   + ccount
        + "&cust_disc="      + cdisc
        + "&cust_pricelist=" + cplist;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    $.ajax({
        url: "CustomerSaveNew",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewCancel() { 
    $('#customernew').dialog('close');
}

/* ----------------------------------------------------------------------
 * 
 * Save information about the changed customer.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() { 
    // Gather info from the fields.
    var cname  = document.getElementById('cust_name').value;
    var caddr1 = document.getElementById('cust_address1').value;
    var caddr2 = document.getElementById('cust_address2').value;
    var cpost  = document.getElementById('cust_postal').value;
    var ccity  = document.getElementById('cust_city').value;
    var ccount = document.getElementById('cust_country').value;
    var cdisc  = document.getElementById('cust_disc').value;
    
    var plObj   = document.getElementById('cust_pricelist');
    var plIndex = plObj.selectedIndex;
    var cplist  = plObj.options[plIndex].value;
    
    // Parameters
    var params =
          "cust_id="         + _currentCustomerEdit
        + "&cust_name="      + cname
        + "&cust_address1="  + caddr1
        + "&cust_address2="  + caddr2
        + "&cust_postal="    + cpost
        + "&cust_city="      + ccity
        + "&cust_country="   + ccount
        + "&cust_disc="      + cdisc
        + "&cust_pricelist=" + cplist;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    $.ajax({
        url: "CustomerSaveEdit",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() { 
    $('#customeredit').dialog('close');
}


/* ----------------------------------------------------------------------
 * 
 * Delete a customer.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditDelete() { 
    var params = 'cust_id=' + _currentCustomerEdit;
    
    // Make the ajax call.
    $.ajax({
        url: "CustomerDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data) {
            if (data === 'ok') {
                window.location.reload(true);
            }
            // Something went wrong, show the error returned.
            else {
                alert(data);
            }
        },

        error: CallbackError
    });
}

function ButtonEditHelp() { alert('TODO'); }
function ButtonNewHelp() { alert('TODO'); }


/* ----------------------------------------------------------------------
 * 
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}