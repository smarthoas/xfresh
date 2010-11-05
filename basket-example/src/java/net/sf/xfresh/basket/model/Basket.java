package net.sf.xfresh.basket.model;

import java.util.*;

/**
 * User: Olga Bolshakova
 * Date: 26.06.2007
 * Time: 18:26:19
 */
public class Basket {
    private final List<MarketItem> content = new LinkedList<MarketItem>();
    private int count = 0;

    public void addItem(MarketItem item) {
        content.add(new BasketMarketItem(item.getUid()));
        count++;
    }

    public void removeItem(MarketItem item) {
        Iterator<MarketItem> it = content.iterator();
        while (it.hasNext()) {
            if (it.next().getUid() == item.getUid()) {
                it.remove();
            }
        }
    }

    public List<MarketItem> getContent() {
        return content;
    }
    
    public int getCount() {
    	return count;
    }
    
    public boolean contains(MarketItem item) {
        for (MarketItem marketItem : content) {
            if (marketItem.getUid() == item.getUid()) {
                return true;
            }
        }
        return false;
    }

    public int getItemCount(MarketItem item) {
        for (MarketItem marketItem : content) {
            if (marketItem.getUid() == item.getUid()) {
                return ((BasketMarketItem)marketItem).getCount();
            }
        }
        return 0;
    }

    public void setItemCount(MarketItem item, int itemCount) {
        for (MarketItem marketItem : content) {
            if (marketItem.getUid() == item.getUid()) {
            	this.count += itemCount - ((BasketMarketItem)marketItem).getCount();
                ((BasketMarketItem)marketItem).setCount(itemCount);
            }
        }
    }
   
}
