/*
License: LGPL as per: http://www.gnu.org/copyleft/lesser.html
$Id$
*/

// Ensure this object's dependancies are loaded.
mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");
mapbuilder.loadScript(baseDir+"/util/openlayers/OpenLayers.js");

/**
 * When this button is selected, clicks on the MapPane trigger a Metacat query with the 
 * currently set AOI.
 * @constructor
 * @base ButtonBase
 * @authors P. Mark Anderson anderson@nceas.ucsb.edu, Matthew Perry perry@nceas.ucsb.edu
 * @param widgetNode The widget node from the Config XML file.
 * @param model  The model for this widget
 */
function AoiMetacatQuery(widgetNode, model) {
	// Extend ButtonBase
	ButtonBase.apply(this, new Array(widgetNode, model))
	
	// Grab the important values from the config file
	var metacat = widgetNode.selectSingleNode("mb:metacat").firstChild.nodeValue;
	var skin = widgetNode.selectSingleNode("mb:skin").firstChild.nodeValue;
	var tolerance = widgetNode.selectSingleNode("mb:pixel_tolerance").firstChild.nodeValue;

	var method = "popup";

	this.cursor = 'crosshair';

	this.createControl = function(objRef) {
		var Control = 
			OpenLayers.Class(OpenLayers.Control, {
			    /**
			     * Property: type
			     * {OpenLayers.Control.TYPE}
			     */
			    type: OpenLayers.Control.TYPE_TOOL,
			
			    /**
			     * Property: out
			     * {Boolean} Should the control be used for zooming out?
			     */
			    out: false,
			
			    /**
			     * Method: draw
			     */    
			    draw: function() {
			        this.handler = new OpenLayers.Handler.Box( this,
			                            {done: this.zoomBox}, {keyMask: this.keyMask} );
			    },
			
			    /**
			     * Method: zoomBox
				 * Queries metacat and displays the result in a popup window.
			     * Parameters:
			     * position - {<OpenLayers.Bounds>} or {<OpenLayers.Pixel>}
			     */
			    zoomBox: function (position) {

			        if (position instanceof OpenLayers.Bounds) {
		                var minXY = this.map.getLonLatFromPixel(
		                            new OpenLayers.Pixel(position.left, position.bottom));
		                var maxXY = this.map.getLonLatFromPixel(
		                            new OpenLayers.Pixel(position.right, position.top));
		                var bounds = new OpenLayers.Bounds(minXY.lon, minXY.lat,
		                                               maxXY.lon, maxXY.lat);
		                                               
			            //alert("bounds:" + bounds);

			            // Determine the BBOX
						var x1 = bounds.left;
						var y1 = bounds.bottom;
						var x2 = bounds.right;
						var y2 = bounds.top;
			        } else { // it's a pixel
			        	
			        	var minXY = this.map.getLonLatFromPixel(
		                            new OpenLayers.Pixel(position.x, position.y));
			        	var x1 = minXY.lon;
						var y1 = minXY.lat;
						var x2 = minXY.lon;
						var y2 = minXY.lat;
			        }

					if ( x1 == x2 && y1 == y2 ) {
						// BBOX is really a point; give a bit of wiggle room
						// tolerance is number of pixels to buffer, set in config
			        	var extent = this.map.getExtent();
				    	var res = extent.getWidth() / this.map.size.w;
				    	//alert("res:" + res);
						x1 = x1 - tolerance * res;
						y1 = y1 - tolerance * res;
						x2 = x2 + tolerance * res;
						y2 = y2 + tolerance * res;          
					} 
					
					var url = metacat + "?action=spatial_query&xmin=" + x1 + "&xmax=" + x2 + 
					         "&ymin=" + y1 + "&ymax=" + y2 + "&skin=" + skin;
					
					if (method == 'popup') {
					  newwindow = window.open(url,'queryWin',
					             'height=600,width=800,status=yes,toolbar=yes,menubar=no,location=yes,resizable=yes,scrollbars=yes');
					  if (window.focus) {newwindow.focus()}
					}
			        
			    },
			
			    CLASS_NAME: "mbControl.AoiMetacatQuery"
			});
			
		return Control;
	}
}

