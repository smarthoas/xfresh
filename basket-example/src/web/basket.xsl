<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" indent="yes" encoding="windows-1251"/>

    <xsl:include href="common.xsl"/>

    <xsl:key name="basket-item" match="/page/data/basket/content/basket-market-item" use="@uid"/>

    <xsl:template name="main">
        <xsl:apply-templates select="page/data/collection" mode="show"/>
    </xsl:template>
    
    <xsl:template match="collection" mode="show">
        <form action="content.xml" method="post">
            <!--<input type="hidden" name="_ox"/>-->
            <table width="80%" align="center" class="general-table">
                <tr class="items-table-title">
                    <td width="70%" class="item-row" colspan="2">
                        <xsl:text>Товар</xsl:text>
                    </td>
                    <td class="item-row">
                        <xsl:text>Цена (руб)</xsl:text>
                    </td>
                    <td class="item-row">
                        <xsl:text>Добавить в корзину</xsl:text>
                    </td>
                </tr>
                <xsl:for-each select="market-item">
                    <xsl:variable name="market-item" select="key('item', @uid)"/>
                    <tr>
                        <td colspan="4" class="item-title">
                            <xsl:value-of select="$market-item/name/text()"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <img src="{$market-item/image/@path}"/>
                        </td>
                        <td>
                            <xsl:value-of select="$market-item/description/text()"/>
                        </td>
                        <td class="price">
                            <xsl:value-of select="$market-item/price/text()"/>
                        </td>
                        <td class="chooser">
                            <xsl:choose>
                                <xsl:when test="key('basket-item', @uid)/@count">
                                    <xsl:text>уже в корзине</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <input type="checkbox" name="{@uid}"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:for-each>
                <tr>
                    <td colspan="4" class="footer" align="center">
                        <input type="submit" value="Положить в корзину" class="submit-button"/>
                    </td>
                </tr>
            </table>
        </form>

    </xsl:template>
</xsl:stylesheet>