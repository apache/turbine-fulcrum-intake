<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="http://xml.apache.org/xslt/java"
                exclude-result-prefixes="java"
                version="1.0">

    <xsl:param name="TITLE" select="''"/>

    <xsl:template match="role-list">
        <html>
            <body>
                <h1><xsl:value-of select="$TITLE"/></h1>
                <table border="0"> 
                    <tr>
                        <td>Name</td>
                        <td>Description</td>
                    </tr>
                    <xsl:apply-templates select="role" />
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="role">
        <tr>
            <td>
                <xsl:value-of select="@name"/>
            </td>
            <td>
                <xsl:value-of select="@description"/>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
