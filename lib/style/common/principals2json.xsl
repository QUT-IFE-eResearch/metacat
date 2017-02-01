<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="text" encoding="utf-8" indent="no" />

  <xsl:template match="principals">
   { "principals":
     [ <xsl:apply-templates select="//user" /> ]
   }
  </xsl:template>

  <xsl:template match="user">
       {
           "dn" : "<xsl:value-of select="username" />",
           "name" : "<xsl:value-of select="name" />",
           "org" : "<xsl:value-of select="organization" />",
           "email" : "<xsl:value-of select="email" />"
           }<xsl:if test="following-sibling::*">,</xsl:if>
  </xsl:template>

</xsl:stylesheet>