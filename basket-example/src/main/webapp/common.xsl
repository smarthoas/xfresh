<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" indent="yes" encoding="windows-1251"/>

    <xsl:key match="/page/items/item" name="item" use="@uid"/>

    <xsl:template match="/">
        <html>
        <head>
            <title><xsl:text>Простая корзина</xsl:text></title>
            <link type="text/css" rel="stylesheet" href="basket.css"/>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <xsl:call-template name="main"/>
            <xsl:call-template name="footer"/>
        </body>
        </html>
    </xsl:template>

    <xsl:template name="header">
        <table width="80%" class="header" align="center">
            <tr>
                <td><img src="basket.jpg"/></td>
                <td></td>
            </tr>
            <tr>
                <td></td>
                <td class="links-row">
                    <font color="white">
                        <a href="basket.xml" class="title-link"><xsl:text>Все товары</xsl:text></a>
                        <xsl:text> | </xsl:text>
                        <a href="content.xml" class="title-link"><xsl:text>Моя корзина</xsl:text></a>
                    </font>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template name="footer">
        <table width="80%">

        </table>
    </xsl:template>

</xsl:stylesheet>