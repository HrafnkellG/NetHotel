/* ********************************************************************** 
 * Script for the Users page. Handles selection of a user from the table
 * and 2 scripts: edit an existing user and create a new one.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 *      nh.js
 * 
 * CSS IDs of elements used by the script:
 *      useredit    - jQuery dialog, edit user
 *      usernew     - jQuery dialog, define new user
 *      username    - text input
 *      userpass    - text input
 *      useraccess  - drop-down input
 *      userlang    - drop-down input
 *      userstyle   - drop-down input
 * 
 * Variables created on the server side:
 *      del
 *      usertitlenew
 *      usertitleedit
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitUsers);

/* Arrays whose indexes are the text on the dialog-buttons and the
 * values are references to the call-back functions for the buttons.
 * Initalized in the InitUsers() function. */
var _ButtonsUserEdit = {};
var _ButtonsUserNew = {};

var _userIDForEdit;

/* ----------------------------------------------------------------------
 * 
 * The page has loaded, run initializers, etc.
 *
 * ---------------------------------------------------------------------- */
function InitUsers() {
    UPInit(); /* From userpreferences.js */
    jQuery('#useredit').dialog({autoOpen: false, modal: true});
    jQuery('#usernew').dialog({autoOpen: false, modal: true});
    
    // Initialize the arrays which control how many buttons will be on
    // each dialog, what text will be on them and which function will be
    // called when the user clicks on them. These arrays are then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _ButtonsUserEdit[help  ] = ButtonEditHelp;
    _ButtonsUserEdit[del   ] = ButtonEditDelete;
    _ButtonsUserEdit[cancel] = ButtonEditCancel;
    _ButtonsUserEdit[save  ] = ButtonEditSave;
    
    _ButtonsUserNew[help  ] = ButtonNewHelp;
    _ButtonsUserNew[cancel] = ButtonNewCancel;
    _ButtonsUserNew[save  ] = ButtonNewSave;
}

/* ----------------------------------------------------------------------
 * 
 * Define a new user.
 *
 * ---------------------------------------------------------------------- */
function NewUser() {
    jQuery.ajax({
        url: "UserEmpty",
        cache: false,
        
        /* Query succeeded. */
        success: function(data) {
            // The jQuery code returns success even though it could not
            // contact the server, so we check for an empty dataset.
            if (data.length === 0) {
                alert(errNoContact);
                return;
            }
            jQuery('#usernew').html(data);
            jQuery('#usernew').dialog("option", "title", usertitlenew);
            jQuery('#usernew').dialog("option", "buttons", _ButtonsUserNew);
            jQuery('#usernew').dialog('open');
        },
        
        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Edit a defined user.
 *
 * ---------------------------------------------------------------------- */
function UserEdit(userID) {
    _userIDForEdit = userID;
    var params = "user_id=" + userID;
    
    jQuery.ajax({
        url: "UserEdit",
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
            jQuery('#useredit').html(data);
            jQuery('#useredit').dialog("option", "title", usertitleedit);
            jQuery('#useredit').dialog("option", "buttons", _ButtonsUserEdit);
            jQuery('#useredit').dialog('open');
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


/* ----------------------------------------------------------------------
 * 
 * Save the information for a new user.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewSave() {
    // Collect the information in the dialog.
    var name = document.getElementById('username').value;
    var passw = document.getElementById('userpass').value;
    
    var selObj = document.getElementById("useraccess");
    var index = selObj.selectedIndex;
    var selAcc = selObj.options[index].value;
    
    selObj = document.getElementById("userlang");
    index = selObj.selectedIndex;
    var selLang = selObj.options[index].value;
    
    selObj = document.getElementById("userstyle");
    index = selObj.selectedIndex;
    var selStyle = selObj.options[index].value;
    
    // Compose the parameter string.
    var params = 
          "user_name="    + name
        + "&user_pass="   + passw
        + "&user_access=" + selAcc
        + "&user_lang="   + selLang
        + "&user_style="  + selStyle;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "UserSave",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data == 'ok')
                {
                window.location.reload(true);
                }
            // Something went wrong, show the error returned.
            else
                {
                alert(data);
                }
            },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the new dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewCancel() {
    jQuery('#usernew').dialog('close');
}


/* ----------------------------------------------------------------------
 * 
 * Help for the new dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonNewHelp() {
    Help(30);
}

/* ----------------------------------------------------------------------
 * 
 * Save the edited information about a user.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditSave() {
    // Collect the information in the dialog.
    var name = document.getElementById('username').value;
    var passw = document.getElementById('userpass').value;
    
    var selObj = document.getElementById("useraccess");
    var index = selObj.selectedIndex;
    var selAcc = selObj.options[index].value;
    
    selObj = document.getElementById("userlang");
    index = selObj.selectedIndex;
    var selLang = selObj.options[index].value;
    
    selObj = document.getElementById("userstyle");
    index = selObj.selectedIndex;
    var selStyle = selObj.options[index].value;
    
    // Compose the parameter string.
    var params = 
          "user_id="      + _userIDForEdit
        + "&user_name="   + name
        + "&user_pass="   + passw
        + "&user_access=" + selAcc
        + "&user_lang="   + selLang
        + "&user_style="  + selStyle;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "UserUpdate",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data == 'ok')
                {
                window.location.reload(true);
                }
            // Something went wrong, show the error returned.
            else
                {
                alert(data);
                }
            },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Close the edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditCancel() {
    jQuery("#useredit").dialog('close');
}

function ButtonEditDelete() {
    var params = "user_id=" + _userIDForEdit;
    
    // Make the ajax call.
    jQuery.ajax({
        url: "UserDelete",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data == 'ok')
                {
                window.location.reload(true);
                }
            // Something went wrong, show the error returned.
            else
                {
                alert(data);
                }
            },

        error: CallbackError
    });
}

/* ----------------------------------------------------------------------
 * 
 * Help for the edit dialog.
 *
 * ---------------------------------------------------------------------- */
function ButtonEditHelp() {
    Help(40);
}