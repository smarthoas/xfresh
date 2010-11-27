<?xml version="1.0" encoding="windows-1251"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" indent="yes" encoding="windows-1251"/>

    <xsl:include href="common.xsl"/>

    <xsl:template name="main">
        <xsl:apply-templates select="/page/data/basket" mode="show"/>
    </xsl:template>

    <xsl:template match="/page/data/basket" mode="show">
        <form action="content.xml" method="post">
            <input type="hidden" name="delete"/>
            <table width="80%" align="center" class="general-table">
                <tr>
                    <td colspan="5">
                        <div>
                            <xsl:choose>
                                <xsl:when test="count(content/basket-market-item) &gt; 0">
                                    <h2><center><xsl:text>Спасибо, что выбрали наш магазин!</xsl:text></center></h2>
                                    <p style="padding:10px">
                                        <xsl:text>В вашей корзине находятся следующие товары.</xsl:text>
                                    </p>
                                </xsl:when>
                                <xsl:otherwise>
                                    <p style="padding:10px">
                                        <center><xsl:text>В вашей корзине пока ничего нет.</xsl:text></center>
                                    </p>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                    </td>
                </tr>

                <xsl:if test="count(content/basket-market-item) &gt; 0">
                    <tr class="items-table-title">
                        <td width="60%" class="item-row" colspan="2">
                            <xsl:text>Товар</xsl:text>
                        </td>
                        <td class="item-row">
                            <xsl:text>Кол-во</xsl:text>
                        </td>
                        <td class="item-row">
                            <xsl:text>Цена (руб)</xsl:text>
                        </td>
                        <td class="item-row">
                            <xsl:text>Удалить из корзины</xsl:text>
                        </td>
                    </tr>

                    <xsl:for-each select="content/basket-market-item">
                        <xsl:variable name="market-item" select="key('item', @uid)"/>
 
                        <tr>
                            <td colspan="5" class="item-title">
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
                                <xsl:value-of select="@count"/><br/><br/>
                                <xsl:text>другое?</xsl:text><br/>
                                <input type="text" size="1" name="{concat('count', @uid)}"/>
                                <input type="submit" value="ok" class="ok-button"/>
                            </td>
                            <td class="price">
                                <xsl:value-of select="$market-item/price/text()"/>
                            </td>
                            <td class="chooser">
                                <input type="checkbox" name="{@uid}"/>
                            </td>
                        </tr>
                    </xsl:for-each>

                    <tr>
                        <td colspan="5" class="footer" align="center">
                            <input type="submit" value="Удалить из корзины" class="submit-button"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="5" class="footer" align="right">
                            <xsl:text>Всего товаров:</xsl:text>
                            <xsl:value-of select="@count"/>
                        </td>
                    </tr>
                </xsl:if>
            </table>
        </form>
    </xsl:template>
</xsl:stylesheet>