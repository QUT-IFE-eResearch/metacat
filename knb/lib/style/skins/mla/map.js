function round(number,decplaces) {
  var multiplier = '1';
  for (i=0; i < decplaces; i++ ) {
    multiplier = multiplier + '0';
  }
  var rounded = Math.round(number * parseFloat(multiplier)) / parseFloat(multiplier);
  return rounded;

}
var map;
function initMap(mapForm) {
  var styleMapPoint = new OpenLayers.StyleMap( { "default": new OpenLayers.Style( {
                        pointRadius: 2, 
                        fillColor: '#0000FF', 
                        fillOpacity: '1', 
                        strokeColor: '#000', 
                        strokeWidth: '1'
                      } ) } );
  var styleMapPoly = new OpenLayers.StyleMap( { "default": new OpenLayers.Style( {
                        fillOpacity: '0', 
                        strokeColor: '#777', 
                        strokeWidth: '1'
                      } ) } );
  var options = {
    projection: new OpenLayers.Projection("EPSG:900913"),
    displayProjection: new OpenLayers.Projection("EPSG:4326"), 
    units: "m",
    maxResolution: 156543.0339,
    maxExtent: new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34)
  };
  
  map = new OpenLayers.Map('map', options);
  
  var gphy = new OpenLayers.Layer.Google("Google", 
    { type: google.maps.MapTypeId.TERRAIN, 
      sphericalMercator: true,
      minZoomLevel: 3, maxZoomLevel: 15, wrapDateLine:false, 
      maxExtent : new OpenLayers.Bounds(12537508.34,-5537508.34,18037508.34,-937508.34)
    });	

  var demis = new OpenLayers.Layer.TMS("Demis World Map", "http://www2.demis.nl/WMS/wms.asp?wms=WorldMap", {
      getURL : get_wms_url, 
      layers : "Bathymetry,Countries,Topography,Hillshading,Coastlines,Waterbodies,Inundated,Rivers,Streams,Builtup+areas,Railroads,Highways,Roads,Trails,Borders,Cities,Settlements,Airports", 
      visibility : false, 
      type : 'jpeg', format : "image/jpeg",
      opacity : 1, isBaseLayer : true,	deltaX : 0.00, deltaY : 0.00
	});
  
  var metacat_points = new OpenLayers.Layer.Vector("Metacat Doc Points", {
      strategies:[new OpenLayers.Strategy.Fixed()],
      preFeatureInsert: function(feature) { feature.geometry.transform(options.displayProjection, options.projection); },
      protocol: new OpenLayers.Protocol.WFS({
        url: "wfs",
        featurePrefix: 'metacat',
        geometryName: 'the_geom',
        featureType: 'data_points',
        featureNS: "http://knb.ecoinformatics.org/metacat",
        srsName: "EPSG:4326"
      }),
      styleMap: styleMapPoint
	});
  
  var metacat_bounds = new OpenLayers.Layer.Vector("Metacat Doc Bounds", {
      strategies:[new OpenLayers.Strategy.Fixed()],
      preFeatureInsert: function(feature) { feature.geometry.transform(options.displayProjection, options.projection); },
      protocol: new OpenLayers.Protocol.WFS({
        url: "wfs",
        featurePrefix: 'metacat',
        geometryName: 'the_geom',
        featureType: 'data_bounds',
        featureNS: "http://knb.ecoinformatics.org/metacat",
        srsName: "EPSG:4326"
      }),
      styleMap: styleMapPoly
	});

  //Other possible WMS base layers to include
  var ol_wms = new OpenLayers.Layer.WMS( "OpenLayers WMS", "http://labs.metacarta.com/wms/vmap0", {layers: 'basic'} );
  ol_wms.setVisibility(false);
  
  map.addLayers([gphy, demis, ol_wms, metacat_points, metacat_bounds]);
  map.addControl(new OpenLayers.Control.LayerSwitcher());
	if (!map.getCenter()) map.zoomToExtent(new OpenLayers.Bounds(13237508.34,-5537508.34,16037508.34,-937508.34));
  
  map.events.register('click', map, function (e) {
    var tolerance = new OpenLayers.Pixel(3, 3);
    var min_px = new OpenLayers.Pixel( e.xy.x - tolerance.x, e.xy.y + tolerance.y);
    var max_px = new OpenLayers.Pixel( e.xy.x + tolerance.x, e.xy.y - tolerance.y);
    //var mid_px = new OpenLayers.Pixel( e.xy.x , e.xy.y );
    var proj = new OpenLayers.Projection("EPSG:4326");
    var min_ll = map.getLonLatFromPixel(min_px).transform(map.getProjectionObject(), proj);
    var max_ll = map.getLonLatFromPixel(max_px).transform(map.getProjectionObject(), proj);
    //var mid_ll = map.getLonLatFromPixel(mid_px);
    //alert("longitude:&nbsp; " + round(mid_ll.lon,3) + " , latitude:&nbsp; " + round(mid_ll.lat,3) );
    mapForm.xmin.value = min_ll.lon;
    mapForm.ymin.value = min_ll.lat;
    mapForm.xmax.value = max_ll.lon;
    mapForm.ymax.value = max_ll.lat;
    mapForm.submit();
  });
}

function get_wms_url(bounds) {
	// recalculate bounds from Google to WGS
	var proj = new OpenLayers.Projection("EPSG:4326");
	bounds.transform(map.getProjectionObject(), proj);
	
	// this is not necessary for most servers display overlay correctly,
	// but in my case the WMS  has been slightly shifted, so I had to correct this with this delta shift
	bounds.left += this.deltaX;
	bounds.right += this.deltaX;
	bounds.top += this.deltaY;
	bounds.bottom += this.deltaY;
	
	//construct WMS request
	var url = this.url;
	url += "&REQUEST=GetMap";
	url += "&SERVICE=WMS";
	url += "&VERSION=1.1.1";
	url += "&LAYERS=" + this.layers;
	url += "&FORMAT=" + this.format;
	url += "&TRANSPARENT=TRUE";
	url += "&STYLES=&SRS=" + "EPSG:4326";
	url += "&BBOX=" + bounds.toBBOX();
	url += "&WIDTH=" + this.tileSize.w;
	url += "&HEIGHT=" + this.tileSize.h;

	return url;
}