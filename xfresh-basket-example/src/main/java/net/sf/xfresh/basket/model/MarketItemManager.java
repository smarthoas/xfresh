package net.sf.xfresh.basket.model;

import java.util.List;
import java.util.Arrays;

/**
 * User: Olga Bolshakova
 * Date: 26.06.2007
 * Time: 18:33:35
 */
public class MarketItemManager {

    //private final Basket basket = new Basket();

    private static final List<MarketItem> items = Arrays.asList(
            new MarketItem(1),
            new MarketItem(2),
            new MarketItem(3),
            new MarketItem(4),
            new MarketItem(5),
            new MarketItem(6),
            new MarketItem(7)
    );

    //public Basket getBasket() {
    //    return basket;
    //}

    public List<MarketItem> getMarketItems() {
        return items;
    }
    
    public MarketItem getItem(int uid) {
        return items.get(uid-1);
    }
}
