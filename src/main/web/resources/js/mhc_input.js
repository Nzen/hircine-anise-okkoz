
/* Maudlin Honey Cetyre copyright Nicholas Prado;
	released under Prosperity Public license terms */

var mhcInput =
{
	keyPressed : function( wholeKey )
	{
		wholeKey.preventDefault();
		mhcConn.sendKey( wholeKey.key, true );
	},


	keyReleased : function( wholeKey )
	{
		mhcConn.sendKey( wholeKey.key, false );
	},


	init : function()
	{
		document.addEventListener( 'keydown', this.keyPressed );
		document.addEventListener( 'keyup', this.keyReleased );
	}
};






























