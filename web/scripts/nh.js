/* **********************************************************************
   *
   * Holds those functions which are most useful to the majority of 
   * web-pages.
   *
   ********************************************************************** */    


/* ----------------------------------------------------------------------
   Opens a new window which displays the help-text for the specified page.
   ---------------------------------------------------------------------- */
function Help(page)
    {
    var chrome = GetChromeHelp();
    
    window.open("Help?help_id=" + page, "help", chrome);
    }

    
/* ----------------------------------------------------------------------
   Get the chrome for a pop-up window which displays the help.
   It will be positioned on the right, it's width is 550 pixels and
   the height is 4/5 of the screen, plus some small padding.
   ---------------------------------------------------------------------- */
function GetChromeHelp() 
    {
    var winW = 550;
    var winH = (screen.availHeight / 5) * 4;
    var winX = (screen.availWidth - winW) - 15;
    var winY = 10;
    
    var chrome = "resizable,scrollbars"
        + ",width=" + winW
        + ",height=" + winH
        + ",top=" + winY
        + ",left=" + winX;
        
    return chrome;
    }
    

/* ---------------------------------------------------------------------- 
   Converts a Date object into a string with this format: yyyymmdd
   NB: The Date object counts the months from 0, thus jan=0 and dec=11
   ---------------------------------------------------------------------- */
function nhDateToString(objDate) {
    var y = objDate.getFullYear();
    var m = objDate.getMonth();
    m++;
    var d = objDate.getDate();
    var sy = y.toString();
    var sm = m.toString(); if (sm.length === 1) { sm = "0" + sm; }
    var sd = d.toString(); if (sd.length === 1) { sd = "0" + sd; }
    
    return sy + sm + sd;
}

/* ---------------------------------------------------------------------- 
   Converts a string into a Date object, the string has this format: yyyymmdd
   ---------------------------------------------------------------------- */
function nhStringToDate(strDate) {
    var theYear  = strDate.substring(0,4);
    var theMonth = strDate.substring(4,6);
    var theDay   = strDate.substring(6,8);
    
    // The Date objects expects the month count to start at zero.
    theMonth--;
    
    return new Date(theYear, theMonth, theDay);
}


/* ---------------------------------------------------------------------- 
   Calculate how many days separate the supplied 2 dates.
   The Date object uses milliseconds as a counter so the magic number
   is 1000x60x60x24 milliseconds in a day.
   ---------------------------------------------------------------------- */
function nhNumOfDays(d1, d2) {
    return Math.round(Math.abs(d1-d2)/86400000);
}
    