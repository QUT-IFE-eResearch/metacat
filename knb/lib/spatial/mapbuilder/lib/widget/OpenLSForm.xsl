<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes"/><xsl:param name="modelId"/><xsl:param name="widgetId"/><xsl:param name="formName">GeocoderForm</xsl:param><xsl:param name="pc">Postalcode: </xsl:param><xsl:param name="street">Street: </xsl:param><xsl:param name="number">Number: </xsl:param><xsl:param name="city">City: </xsl:param><xsl:param name="municipality">Municipality: </xsl:param><xsl:param name="countryCode">Country: </xsl:param><xsl:param name="load">search</xsl:param><xsl:template match="/"><div id="geocoder"><form name="{$formName}" id="{$formName}"><table><tr><td><xsl:value-of select="$pc"/></td><td><input name="pcValue" type="text"/></td></tr><tr><td><xsl:value-of select="$street"/></td><td><input name="streetValue" type="text"/></td></tr><tr><td><xsl:value-of select="$number"/></td><td><input name="numberValue" type="text"/></td></tr><tr><td><xsl:value-of select="$city"/></td><td><input name="cityValue" type="text"/></td></tr><tr><td><xsl:value-of select="$municipality"/></td><td><input name="municipalityValue" type="text"/></td></tr><tr/><tr><td><xsl:value-of select="$countryCode"/></td><td><input name="countryValue" type="text"/></td></tr><tr><td/><td><a href="javascript:config.objects.{$widgetId}.submitForm(config.objects.{$widgetId});"><xsl:value-of select="$load"/></a></td></tr></table></form></div></xsl:template></xsl:stylesheet>
