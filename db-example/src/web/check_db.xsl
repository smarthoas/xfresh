<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes" encoding="utf-8"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:text>Проверка состояния базы</xsl:text>
                </title>
            </head>
            <body>
                <table width="100%" border="1">
                    <tr>
                        <th>Описание проверки</th>
                        <th>Результат</th>
                    </tr>
                    <xsl:apply-templates select="page/data/check-db-result"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="check-db-result">
        <tr>
            <td><xsl:value-of select="check-description"/></td>
            <xsl:choose>
                <xsl:when test="@status = 0">
                    <td><xsl:value-of select="description"/></td>
                </xsl:when>
                <xsl:otherwise>
                    <td><b style="color:red"><xsl:value-of select="description"/></b></td>
                </xsl:otherwise>
            </xsl:choose>
        </tr>
    </xsl:template>

</xsl:stylesheet>