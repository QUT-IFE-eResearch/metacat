<?xml version="1.0"?>
<xsl:stylesheet xmlns:wmc="http://www.opengis.net/context" xmlns:wms="http://www.opengis.net/wms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.0"><xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/><xsl:param name="width">600</xsl:param><xsl:param name="selectedLayer"/><xsl:param name="styleName">composite</xsl:param><xsl:template match="/WMT_MS_Capabilities"><xsl:param name="wmsVersion" select="@version"/><xsl:param name="wmsTitle" select="Service/Title"/><xsl:param name="wmsOnlineResource" select="Capability/Request/GetMap/DCPType/HTTP/Get/OnlineResource/@xlink:href"/><ViewContext xmlns="http://www.opengis.net/context" version="1.0.0" id="autoGeneratedContext_"><General><Title><xsl:value-of select="$wmsTitle"/></Title><xsl:variable name="minx" select="number(Capability/Layer/LatLonBoundingBox/@minx)"/><xsl:variable name="maxx" select="number(Capability/Layer/LatLonBoundingBox/@maxx)"/><xsl:variable name="miny" select="number(Capability/Layer/LatLonBoundingBox/@miny)"/><xsl:variable name="maxy" select="number(Capability/Layer/LatLonBoundingBox/@maxy)"/><xsl:variable name="dy" select=" $maxy - $miny "/><xsl:variable name="dx" select=" $maxx - $minx "/><xsl:variable name="height" select="round(($dy div $dx)*$width)"/><Window width="{$width}" height="{$height}"/><BoundingBox SRS="EPSG:4326" minx="{$minx}" miny="{$miny}" maxx="{$maxx}" maxy="{$maxy}"/></General><LayerList><xsl:choose><xsl:when test="string-length($selectedLayer)&gt;0"><xsl:apply-templates select="Capability/Layer/Layer[Name=$selectedLayer]"><xsl:with-param name="version" select="$wmsVersion"/><xsl:with-param name="title" select="$wmsTitle"/><xsl:with-param name="url" select="$wmsOnlineResource"/></xsl:apply-templates></xsl:when><xsl:otherwise><xsl:apply-templates select="Capability/Layer/Layer"><xsl:with-param name="version" select="$wmsVersion"/><xsl:with-param name="title" select="$wmsTitle"/><xsl:with-param name="url" select="$wmsOnlineResource"/></xsl:apply-templates></xsl:otherwise></xsl:choose></LayerList></ViewContext></xsl:template><xsl:template match="Layer"><xsl:param name="version"/><xsl:param name="title"/><xsl:param name="url"/><xsl:element name="Layer" namespace="http://www.opengis.net/context"><xsl:attribute name="queryable"><xsl:value-of select="@queryable"/></xsl:attribute><xsl:attribute name="hidden"><xsl:text>0</xsl:text></xsl:attribute><xsl:element name="Server" namespace="http://www.opengis.net/context"><xsl:attribute name="service"><xsl:text>OGC:WMS</xsl:text></xsl:attribute><xsl:attribute name="version"><xsl:value-of select="$version"/></xsl:attribute><xsl:attribute name="title"><xsl:value-of select="$title"/></xsl:attribute><xsl:element name="OnlineResource" namespace="http://www.opengis.net/context"><xsl:attribute name="type"><xsl:text>simple</xsl:text></xsl:attribute><xsl:attribute name="xlink:href"><xsl:value-of select="$url"/></xsl:attribute></xsl:element></xsl:element><Name xmlns="http://www.opengis.net/context"><xsl:value-of select="Name"/></Name><Title xmlns="http://www.opengis.net/context"><xsl:value-of select="Title"/></Title><Abstract xmlns="http://www.opengis.net/context"><xsl:value-of select="Abstract"/></Abstract><FormatList xmlns="http://www.opengis.net/context"><Format current="1">image/png</Format></FormatList><StyleList xmlns="http://www.opengis.net/context"><Style current="1"><xsl:copy-of select="Style[Name=$styleName]/*"/></Style></StyleList><xsl:if test="Dimension"><DimensionList xmlns="http://www.opengis.net/context"><xsl:copy-of select="Dimension"/></DimensionList></xsl:if></xsl:element></xsl:template><xsl:template match="/wms:WMS_Capabilities"><xsl:param name="wmsVersion" select="/wms:WMS_Capabilities/@version"/><xsl:param name="wmsTitle" select="/wms:WMS_Capabilities/wms:Service/wms:Title"/><xsl:param name="wmsOnlineResource" select="/wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:DCPType/wms:HTTP/wms:Get/wms:OnlineResource/@xlink:href"/><xsl:param name="defLayers" select="wms:Capability//wms:Layer[wms:Name=$selectedLayer]"/><xsl:param name="defLayer" select="$defLayers[1]"/><xsl:param name="bbox" select="//wms:BoundingBox"/><xsl:variable name="abstract" select="$defLayer/wms:Abstract"/><xsl:variable name="title" select="$defLayer/wms:Title"/><xsl:variable name="infoLink" select="$defLayer/wms:MetadataURL/wms:OnlineResource/@xlink:href"/><xsl:variable name="logoLink" select="$defLayer/wms:Attribution/wms:LogoURL/wms:OnlineResource/@xlink:href"/><xsl:variable name="providerName" select="wms:Service/wms:Title"/><ViewContext xmlns="http://www.opengis.net/context" version="1.0.0" id="autoGeneratedContext_"><General><Title><xsl:value-of select="$title"/></Title><Abstract><xsl:value-of select="$abstract"/></Abstract><xsl:variable name="minx" select="number($bbox/@minx)"/><xsl:variable name="maxx" select="number($bbox/@maxx)"/><xsl:variable name="miny" select="number($bbox/@miny)"/><xsl:variable name="maxy" select="number($bbox/@maxy)"/><xsl:variable name="srs" select="$bbox/@CRS"/><xsl:choose><xsl:when test="$defLayer/@fixedWidth"><xsl:variable name="fWidth" select="$defLayer/@fixedWidth"/><xsl:variable name="fHeight" select="$defLayer/@fixedHeight"/><Window width="{$fWidth}" height="{$fHeight}"/></xsl:when><xsl:otherwise><xsl:variable name="dy" select=" $maxy - $miny "/><xsl:variable name="dx" select=" $maxx - $minx "/><xsl:variable name="height" select="round(($dy div $dx)*$width)"/><Window width="{$width}" height="{$height}"/></xsl:otherwise></xsl:choose><BoundingBox SRS="{$srs}" minx="{$minx}" miny="{$miny}" maxx="{$maxx}" maxy="{$maxy}"/><DescriptionURL format="text/html"><OnlineResource xlink:href="{$infoLink}" xlink:type="simple"/></DescriptionURL><LogoURL format="text/html"><OnlineResource xlink:href="{$logoLink}" xlink:type="simple"/></LogoURL><ContactInformation><ContactPersonPrimary><ContactOrganization><xsl:value-of select="$providerName"/></ContactOrganization></ContactPersonPrimary></ContactInformation></General><LayerList><xsl:choose><xsl:when test="string-length($selectedLayer)&gt;0"><xsl:apply-templates select="$defLayer"><xsl:with-param name="version" select="$wmsVersion"/><xsl:with-param name="title" select="$wmsTitle"/><xsl:with-param name="url" select="$wmsOnlineResource"/></xsl:apply-templates></xsl:when><xsl:otherwise><xsl:apply-templates select="wms:Capability/wms:Layer/*"><xsl:with-param name="version" select="$wmsVersion"/><xsl:with-param name="title" select="$wmsTitle"/><xsl:with-param name="url" select="$wmsOnlineResource"/></xsl:apply-templates></xsl:otherwise></xsl:choose></LayerList></ViewContext></xsl:template><xsl:template match="wms:Layer"><xsl:param name="version"/><xsl:param name="title"/><xsl:param name="url"/><xsl:element name="Layer" namespace="http://www.opengis.net/context"><xsl:attribute name="queryable"><xsl:value-of select="@queryable"/></xsl:attribute><xsl:attribute name="hidden"><xsl:text>0</xsl:text></xsl:attribute><xsl:element name="Server" namespace="http://www.opengis.net/context"><xsl:attribute name="service"><xsl:text>OGC:WMS</xsl:text></xsl:attribute><xsl:attribute name="version"><xsl:value-of select="$version"/></xsl:attribute><xsl:attribute name="title"><xsl:value-of select="$title"/></xsl:attribute><xsl:element name="OnlineResource" namespace="http://www.opengis.net/context"><xsl:attribute name="type"><xsl:text>simple</xsl:text></xsl:attribute><xsl:attribute name="xlink:href"><xsl:value-of select="$url"/></xsl:attribute></xsl:element></xsl:element><Name xmlns="http://www.opengis.net/context"><xsl:value-of select="wms:Name"/></Name><Title xmlns="http://www.opengis.net/context"><xsl:value-of select="wms:Title"/></Title><Abstract xmlns="http://www.opengis.net/context"><xsl:value-of select="wms:Abstract"/></Abstract><FormatList xmlns="http://www.opengis.net/context"><Format current="1">image/png</Format></FormatList><StyleList xmlns="http://www.opengis.net/context"><Style current="1"><Name><xsl:value-of select="$styleName"/></Name><xsl:if test="wms:Style/wms:LegendURL"><LegendURL><OnlineResource><xsl:attribute name="xlink:href"><xsl:value-of select="wms:Style/wms:LegendURL/wms:OnlineResource/@xlink:href"/></xsl:attribute></OnlineResource></LegendURL></xsl:if></Style></StyleList><xsl:if test="wms:Dimension[@name='time']"><DimensionList xmlns="http://www.opengis.net/context"><Dimension name="time" units="ISO8601"><xsl:value-of select="wms:Dimension[@name='time']"/></Dimension></DimensionList></xsl:if></xsl:element></xsl:template></xsl:stylesheet>
