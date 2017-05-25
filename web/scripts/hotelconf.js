/* ********************************************************************** 
 * Script for the HotleConfig page. Handle the settings for the hotel; name,
 * address, locale etc.
 * 
 * Scripts referenced: 
 *      userpreferences.js
 * 
 * CSS IDs of elements used by the script:
 *      hcname      - Hotel name
 *      hcaddr1     - Address line 1
 *      hcaddr2     - Address line 2
 *      hccity      - City
 *      hccountry   - Country
 *      hcvat       - V.A.T. percentage
 *      hctimezone  - Timezone, select element
 *      hclocale    - Locale, select element
 *      hcoverbook  - Overbooking, checkbox
 * 
 * Variables created on the server side:
 *      ?
 * 
 * ********************************************************************** */

/* Wait for the page to load before running the script. */
jQuery(document).ready(InitHConf);

/* ----------------------------------------------------------------------
 * 
 * The page has loaded, run initializers, etc.
 *
 * ---------------------------------------------------------------------- */
function InitHConf() {
    UPInit(); /* From userpreferences.js */
}

/* ----------------------------------------------------------------------
 * 
 * User clicked on the save button.
 *
 * ---------------------------------------------------------------------- */
function HCSave() {
    /* Gather parameters. */
    var name = document.getElementById('hcname').value;
    var adr1 = document.getElementById('hcaddr1').value;
    var adr2 = document.getElementById('hcaddr2').value;
    var city = document.getElementById('hccity').value;
    var coun = document.getElementById('hccountry').value;
    var vat  = document.getElementById('hcvat').value;
    
    var selTZ = document.getElementById("hctimezone");
    var index = selTZ.selectedIndex;
    var timez = selTZ.options[index].value;
    
    var selLO = document.getElementById("hclocale");
        index = selLO.selectedIndex;
    var locl  = selLO.options[index].value;
    
    var overb = document.getElementById("hcoverbook").checked;
    
    var params = 
          "hc_name="      + name
        + "&hc_addr1="    + adr1
        + "&hc_addr2="    + adr2
        + "&hc_city="     + city
        + "&hc_country="  + coun
        + "&hc_vat="      + vat
        + "&hc_timezone=" + timez
        + "&hc_locale="   + locl
        + "&hc_overbook=" + overb;

    // Make allowance for non-ASCII characters.
    params = encodeURI(params);
    
    // Make the ajax call.
    jQuery.ajax({
        url: "HCSave",
        cache: false,
        data: params,

        /* Callback for success. */
        success: function(data)
            {
            if (data != 'ok')
                {
                // Something went wrong, show the error returned.
                alert(data);
                }
            else {
                // TO-DO: Something less intrusive, a pop-up which fades out?
                alert('OK');
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