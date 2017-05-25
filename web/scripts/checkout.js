/* **********************************************************************
 * 
 * Script for the Checkout page.
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitCheckout);


function InitCheckout() {
    UPInit(); /* From userpreferences.js */
}


/* ----------------------------------------------------------------------
 * 
 * Master check-box clicked. 
 * Select or de-select all other check-boxes.
 *
 * ---------------------------------------------------------------------- */
function MasterCBClicked(mcb) {
    var checkState = true;
    if (mcb.checked === false) {
        checkState = false;
    }
    /* Use jQuery to iterate through all the guests. */
    var checkboxes = jQuery(".cbguest");
    
    checkboxes.each( 
        function() {
        this.checked = checkState;
        } 
    );
}


/* ----------------------------------------------------------------------
 * 
 * A check-box before a guest was clicked.
 * De-select the master check-box.
 *
 * ---------------------------------------------------------------------- */
function GuestCBClicked(gcb) {
    document.getElementById("masterCheckBox").checked = false;
}



/* ----------------------------------------------------------------------
 * 
 * Check out all selected guests.
 *
 * ---------------------------------------------------------------------- */
function doCheckout() {
    // Collect the ids of the selected guests.
    var guestIDs = "";
    var checkboxes = jQuery(".cbguest");
    checkboxes.each( 
        function() 
            {
            if (this.checked === true) 
                {
                if (guestIDs.length > 0) 
                    {
                    guestIDs += ",";
                    }
                guestIDs += this.dataset.guestid;
                }
            } 
    );//END-OF checkboxes.each
    
    if (guestIDs.length === 0) {
        return;
    }
    
    // We have the ids, make the call to the server to check them out.
    var params = "guests=" + guestIDs;
    jQuery.ajax({
        url: "CheckoutGuests",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data === 'ok')
                {
                // The variable 'invoiceID' is defined on the server.
                window.location = "Invoice?invID=" + invoiceID;
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
 * General handler for ajax errors.
 *
 * ---------------------------------------------------------------------- */
function CallbackError(XMLHttpRequest, textStatus, errorThrown) {
    alert(errTxt + ":    Ajax status = '" + textStatus + "'    error type: " + errorThrown);
}