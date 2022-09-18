
/* Maudlin Honey Cetyre copyright Nicholas Prado;
	released under Prosperity Public license terms */


var mhcGui = {
	drawOn : null,
	canvasWidth : 0,
	canvasHeight : 0,
	supported : window.CanvasRenderingContext2D,


	init : function()
	{
		var canvasObj = document.getElementById('gameScreen');
		this.drawOn = canvasObj.getContext('2d');
		this.canvasWidth = canvasObj.width;
		this.canvasHeight = canvasObj.height;
	},


	drawBackground : function()
	{
		const backgroundColor = "#443355";
		this.drawOn.beginPath();
		this.drawOn.fillStyle = backgroundColor;
		this.drawOn.fillRect( 0, 0, this.canvasWidth, this.canvasHeight );
	},


	indexToPixel : function( index )
	{
		return index *20; // ASK perhaps different between x and y
	},


	// IMPROVE convert indicies to pixel positions
	drawMap : function( layeredMap )
	{
		this.drawBackground();
		this.drawOn.font = "30px monospace";
		for ( var layerInd = 0; layerInd < layeredMap.length; layerInd++ )
		{
			var layerArr = layeredMap[ layerInd ];
			for ( var rowInd = 0; rowInd < layerArr.length; rowInd++ )
			{
			 	var cell = layerArr[ rowInd ];
				this.drawOn.fillStyle = cell.color;
				this.drawOn.fillText(
					cell.symbol,
					this.indexToPixel( cell.xxPos ),
					this.indexToPixel( cell.yyPos ) +25 );
			}
		}
	}
};






















