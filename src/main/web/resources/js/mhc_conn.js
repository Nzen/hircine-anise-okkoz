
/* Maudlin Honey Cetyre copyright Nicholas Prado;
	released under Prosperity Public license terms */

var mhcConn = {
	channel : null,
	supported : 'WebSocket' in window,


	init : function()
	{
		if ( ! this.supported )
		{
			alert( 'Try a browser that supports websockets' );
		}
		else
		{
			this.channel = new WebSocket( 'ws://localhost:9998' );
			this.channel.onopen = function coo()
			{
				std.log( 'connected' );
			};
			this.channel.onclose = function coc()
			{
				std.log( 'session over' );
				alert( 'The server disconnected our session' );
			};
			this.channel.onmessage = function com( message )
			{
				mhcConn.applyResponse( message.data );
			};
		}
	},


	sendKey : function( keyCode, pressInsteadOfRelease )
	{
		var keyMessage = {
				msgType : 'KEY',
				code : keyCode,
				pressing : pressInsteadOfRelease
			};
		this.channel.send( JSON.stringify( keyMessage ) );
	},


	applyResponse : function( jsonMessage )
	{
		var response = JSON.parse( jsonMessage );
		if ( response.msgType == 'CANVAS' )
		{
			mhcGui.drawMap( response.value );
		}
		else
		{
			std.log( response.msgType +' is not a response I handle' );
		}
	}
};