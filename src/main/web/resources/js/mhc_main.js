
/* Maudlin Honey Cetyre copyright Nicholas Prado;
	released under Prosperity Public license terms */


var std = function() {};
std.log = function( message )
{
	try
	{ console.log( message ); }
	catch ( exception )
	{ return; } // IE reputedly has no console.
}

window.onload = function()
{
	if ( ! mhcGui.supported || ! mhcConn.supported )
	{
		alert( 'Choose a browser that supports both'
				+' websockets and canvas to use mhc.' );
	}
	mhcGui.init();
	mhcInput.init();
	mhcConn.init();
};






























