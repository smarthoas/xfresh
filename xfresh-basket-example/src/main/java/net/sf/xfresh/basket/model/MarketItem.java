package net.sf.xfresh.basket.model;

/**
 * User: Olga Bolshakova
 * Date: 26.06.2007
 * Time: 18:28:01
 */
public class MarketItem {

    private final int uid;

    public MarketItem(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarketItem that = (MarketItem) o;

        if (uid != that.uid) return false;

        return true;
    }

    public int hashCode() {
        return uid;
    }
}
