/*
License: LGPL as per: http://www.gnu.org/copyleft/lesser.html
$Id$
*/

// Ensure this object's dependancies are loaded.
mapbuilder.loadScript(baseDir+"/widget/ButtonBase.js");

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
  var wfs_proxy = widgetNode.selectSingleNode("mb:wfs_proxy").firstChild.nodeValue;
  var wfs_server = widgetNode.selectSingleNode("mb:wfs_server").firstChild.nodeValue;
  var tolerance = widgetNode.selectSingleNode("mb:pixel_tolerance").firstChild.nodeValue;

  /**
   * Queries metacat and displays the result in a popup window.
   * @param objRef      Pointer to this object.
   * @param targetNode  The node for the enclosing HTML tag for this widget.
   */
  this.doAction = function(objRef,targetNode) {
    if (objRef.enabled) {
      var method = "popup";
      var bbox = objRef.targetModel.getParam("aoi");
      if ( bbox!=null) {
        var ul = bbox[0];
        var lr = bbox[1];
        var extent = objRef.targetModel.extent;
        var extbox = extent.model.getBoundingBox();
        var mapwidth_real = extbox[2] - extbox[0];
        var mapwidth_pixel = extent.model.getWindowWidth();
        var res = mapwidth_real / mapwidth_pixel;
      }

      var selectedLayer = objRef.context.getParam("selectedLayer");
       
      if ( selectedLayer != null ) {

        // Determine the BBOX
        var x1 = ul[0];
        var y1 = lr[1];
        var x2 = lr[0];
        var y2 = ul[1];
        if ( x1 == x2 && y1 == y2 ) {
          // BBOX is really a point; give a bit of wiggle room
          // tolerance is number of pixels to buffer, set in config
          x1 = x1 - tolerance * res;
          y1 = y1 - tolerance * res;
          x2 = x2 + tolerance * res;
          y2 = y2 + tolerance * res;          
        } 

        var wfs_url = wfs_server + "?request=GetFeature&typename=" + selectedLayer + 
                  "&BBOX=" + x1 + "," + y1 + "," + x2 + "," + y2;
        var url = wfs_proxy + "?wfsurl=" + escape(wfs_url);

        if (method == 'popup') {
           newwindow = window.open(url,'queryWin','height=600,width=800,status=yes,toolbar=yes,menubar=no,location=yes,resizable=yes,scrollbars=yes');
           if (window.focus) {newwindow.focus()}
        }
      } else {
        alert("Please select a query layer first");
        
      }

    }
  }

  /**
   * Register for mouseUp events.
   * @param objRef  Pointer to this object.
   */
  this.setMouseListener = function(objRef) {
    if (objRef.mouseHandler) {
      objRef.mouseHandler.model.addListener('mouseup',objRef.doAction,objRef);
    }
    objRef.context=objRef.widgetNode.selectSingleNode("mb:context");
    if (objRef.context){
      alert("got context!");
      objRef.context=eval("config.objects."+objRef.context.firstChild.nodeValue);
    } else {
		//alert("no context in AoiMetacatQuery widget.  using mainMap ");
		objRef.context=eval("config.objects.mainMap");
		/*
		var nodes = objRef.widgetNode.childNodes;
    	for (var i=0; i<nodes.length; i++) {
    		alert(nodes.item(i).nodeType + " // " +
    		nodes.item(i).nodeName + " // " +
    		nodes.item(i).nodeValue);
		}
		*/
	}
  }
  this.model.addListener( "loadModel", this.setMouseListener, this );

}

