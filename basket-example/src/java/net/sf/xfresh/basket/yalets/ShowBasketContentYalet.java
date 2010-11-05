package net.sf.xfresh.basket.yalets;

import net.sf.xfresh.basket.model.Basket;
import net.sf.xfresh.basket.model.MarketItem;
import net.sf.xfresh.core.InternalRequest;
import net.sf.xfresh.core.InternalResponse;

/**
 * User: Olga Bolshakova
 * Date: 27.06.2007
 * Time: 12:11:27
 */
public class ShowBasketContentYalet extends AbstractBasketYalet {
    public void process(InternalRequest req, InternalResponse res) {
        Basket basket = getBasket(req);
        
        for(MarketItem item : manager.getMarketItems()) {
            int uid = item.getUid();
            String count = req.getParameter(String.valueOf("count" + uid));
            if (count != null && !"".equals(count)) {
                int count1 = Integer.parseInt(count);
                if (count1 > 0) {
                    basket.setItemCount(item, count1);
                    res.add(basket);
                    setCookies(basket, res);
                    return;
                }
            }
        }
        

        boolean isDel = req.getParameter("delete") != null;

        for(MarketItem item : manager.getMarketItems()) {
            int uid = item.getUid();
            if (req.getParameter(String.valueOf(uid)) != null) {
                if (isDel) {
                    basket.removeItem(item);
                } else {
                    basket.addItem(item);
                }
            }
        }
        
        res.add(basket);
        setCookies(basket, res);
    }  
}
