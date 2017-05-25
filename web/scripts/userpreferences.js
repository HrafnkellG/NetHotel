/* **********************************************************************
 * 
 * The user-preferences dialog. 
 * 
 * Scripts referenced:
 *      nh.js
 * 
 * IDs the script needs on the page:
 *      upform
 *      upname
 *      uppass
 *      uplang
 *      upstyle
 *      
 * Variables created on the server side:
 *      cancel
 *      save
 *      help
 *      err
 *      errSave
 *      errNoContact
 *      dtup
 *      
 *  Coded by hg
 * 
 * ********************************************************************** */

var _userPrefsLocalizedBtnText = {};

/* ----------------------------------------------------------------------
 * 
 * Initialize the dialog. Called from the primary script of the page.
 * 
 * ---------------------------------------------------------------------- */
function UPInit() 
    {
    jQuery('#upform').dialog({autoOpen: false, modal: true});
    
    // Initialize the array which controls how many buttons will be on
    // the dialog, what text will be on them and which function will be
    // called when the user clicks on them. This array is then fed to
    // the "dialog()" jQuery function.
    // The variables referenced inside the brackets are created on the server.
    // They hold the localized text for the buttons.
    _userPrefsLocalizedBtnText[help  ] = UPButtonHelp;
    _userPrefsLocalizedBtnText[cancel] = UPButtonCancel;
    _userPrefsLocalizedBtnText[save  ] = UPButtonSave;
    }
    
/* ----------------------------------------------------------------------
 * 
 * Use ajax to get information for the user-preferences form, when the
 * ajax call completes the callback function will open the dialog.
 *
 * ---------------------------------------------------------------------- */
function OpenPreferences() {
    jQuery.ajax({
        url: "Preferences",
        cache: false,
        
        /* The query completed ok. */
        success: function (data, textStatus, XMLHttpRequest) {
            if (data.length == 0) {
                alert(errNoContact);
                return;
            }
            jQuery('#upform').html(data);
            jQuery('#upform').dialog("option", "title", dtup);
            jQuery('#upform').dialog("option", "buttons", _userPrefsLocalizedBtnText);
            jQuery('#upform').dialog('open');
        },
        
        /* The query could not complete. */
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(errNoContact + ' : ' + textStatus + ' : ' + errorThrown);
        }
    });
}
    
/* ----------------------------------------------------------------------
 * 
 * Save the information currently in the dialog-fields.
 * 
 * ---------------------------------------------------------------------- */
function UPButtonSave() 
    {
    var passw = document.getElementById('uppass').value;

    var selObj = document.getElementById("uplang");
    var index = selObj.selectedIndex;
    var selLang = selObj.options[index].value;

    selObj = document.getElementById("upstyle");
    index = selObj.selectedIndex;
    var selStyle = selObj.options[index].value;
    
    var params = 
          "user_pass="   + passw
        + "&user_lang="  + selLang
        + "&user_style=" + selStyle;
    params = encodeURI(params);

    jQuery.ajax(
        {
        url: "PreferencesSave",
        cache: false,
        data: params,
        
        /* Ajax query completed successfully. */
        success: function (data, textStatus, XMLHttpRequest) 
            {
            if (data == 'ok') 
                {
                window.location.reload(true);
                }
            else 
                {
                alert(err + ": " + data);
                }
            },
            
        /* Ajax query did not complete. */
        error: function (XMLHttpRequest, textStatus, errorThrown) 
            {
            alert(err + ": " + errSave);
            }
        });
    }
    
/* ----------------------------------------------------------------------
 * 
 * The user wants to see the help-page for the dialog. 
 *
 * ---------------------------------------------------------------------- */
function UPButtonHelp() 
    {
    Help(20);
    }
    
/* ----------------------------------------------------------------------
 * 
 * The user cancelled, close the dialog.
 *
 * ---------------------------------------------------------------------- */
function UPButtonCancel() 
    {
    jQuery('#upform').dialog('close');
    }    