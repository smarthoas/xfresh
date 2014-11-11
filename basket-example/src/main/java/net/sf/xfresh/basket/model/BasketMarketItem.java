package net.sf.xfresh.basket.model;

/**
 * User: Olga Bolshakova
 * Date: 27.06.2007
 * Time: 13:53:46
 */
public class BasketMarketItem extends MarketItem {
    private int count = 1;

    public BasketMarketItem(int uid) {
        super(uid);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
