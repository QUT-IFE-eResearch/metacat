<?xml version="1.0"?>
<xsl:stylesheet xmlns:mb="http://mapbuilder.sourceforge.net/mapbuilder" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes"/><xsl:param name="listenerFilter"/><xsl:param name="targetFilter"/><xsl:template match="/mb:Logger"><div style="height:300px;overflow:scroll"><table border="1"><tr><td colspan="2">
          Event Log
        </td><td><a href="javascript:window.logger.clearLog();">Clear</a> - 
          <a href="javascript:window.logger.callListeners('refresh');">Refresh</a> - 
          <a href="javascript:window.logger.saveLog();">Save</a></td></tr><tr><th>
          Event
        </th><th>
          Listener
        </th><th>
          Target
        </th></tr><xsl:choose><xsl:when test="string-length($listenerFilter)&gt;0"><xsl:choose><xsl:when test="string-length($targetFilter)&gt;0"><xsl:apply-templates select="event[@listener=$listenerFilter and @target=$targetFilter]"/></xsl:when><xsl:otherwise><xsl:apply-templates select="event[@listener=$listenerFilter]"/></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:apply-templates select="event"/></xsl:otherwise></xsl:choose></table></div></xsl:template><xsl:template match="event"><tr><td><xsl:value-of select="."/></td><td><xsl:value-of select="@listener"/></td><td><xsl:value-of select="@target"/></td></tr></xsl:template></xsl:stylesheet>
