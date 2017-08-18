import java.util.*;
import java.lang.reflect.*;
import java.text.DecimalFormat;


/**
 * Represents a stock in the SafeTrade project
 */
public class Stock
{
    public static DecimalFormat money = new DecimalFormat( "0.00" );

    private String stockSymbol;

    private String companyName;

    private double loPrice, hiPrice, lastPrice;

    private int volume;

    private PriorityQueue<TradeOrder> buyOrders, sellOrders;


    public Stock( String symbol, String name, double price )
    {
        stockSymbol = symbol;
        companyName = name;
        loPrice = price;
        hiPrice = price;
        lastPrice = price;
        volume = 0;

        PriceComparator asc = new PriceComparator();
        buyOrders = new PriorityQueue<TradeOrder>( 10, asc );

        PriceComparator dec = new PriceComparator( false );
        sellOrders = new PriorityQueue<TradeOrder>( 10, dec );

    }


    public String getQuote()
    {
        String str = companyName + "(" + stockSymbol + ")" + "\n" + "Price: "
            + lastPrice + " hi: " + hiPrice + " lo: " + loPrice + " vol: "
            + volume + "\n";
        if ( !sellOrders.isEmpty() )
        {
            str += "Ask: " + buyOrders.peek().getPrice() + " size: "
                + buyOrders.peek().getShares();
        }
        else
        {
            str += "Ask: none";
        }

        if ( !buyOrders.isEmpty() )
        {
            str += " Bid: " + buyOrders.peek().getPrice() + " size: "
                + buyOrders.peek().getShares();
        }
        else
        {
            str += "Bid: none";
        }
        return str;
    }


    public void placeOrder( TradeOrder order )
    {
        // change order.getPrice() to decimal format

        String str = "";
        if ( order.isBuy() )
        {
            buyOrders.add( order );
            str = "New order: Buy" + stockSymbol + " (" + companyName + ")"
                + "\n" + order.getShares() + " shares at ";
        }
        else
        {
            sellOrders.add( order );
            str = "New order: Sell" + stockSymbol + " (" + companyName + ")"
                + "\n" + order.getShares() + " shares at ";
        }

        if ( order.isLimit() )
        {
            str += "$" + order.getPrice();
        }
        else
        {
            str += " market";
        }

        order.getTrader().receiveMessage( str );

        executeOrders();

    }


    protected void executeOrders()
    {
        while ( true )
        {
            TradeOrder buy = buyOrders.peek();
            TradeOrder sell = sellOrders.peek();
            int shares = 0;
            double price = 0.0;
            if ( buy == null || sell == null )
            {
                break;
            }
            if ( buy.isLimit() && sell.isLimit()
                && buy.getPrice() > sell.getPrice() )
            {
                break;
            }
            if ( buy.isLimit() && sell.isLimit()
                && sell.getPrice() >= buy.getPrice() )
            {
                price = sell.getPrice();

            }
            else if ( buy.isLimit() && sell.isMarket() )
            {
                price = buy.getPrice();
            }
            else if ( buy.isMarket() && sell.isLimit() )
            {
                price = sell.getPrice();
            }
            else if ( buy.isMarket() && sell.isMarket() )
            {
                price = lastPrice;
            }

            if ( buy.getShares() <= sell.getShares() )
            {
                shares = sell.getShares();
            }
            else
            {
                shares = buy.getShares();
            }

            volume += shares;

            buy.subtractShares( shares );
            sell.subtractShares( shares );

            if ( buy.getShares() == 0 )
            {
                buyOrders.remove();
            }
            if ( sell.getShares() == 0 )
            {
                sellOrders.remove();
            }

            if ( loPrice > price )
            {
                loPrice = price;
            }
            if ( hiPrice < price )
            {
                hiPrice = price;
            }

            buy.getTrader().receiveMessage( "You bought: " + volume
                + stockSymbol + " at " + price + " amt " + price * volume );
            sell.getTrader().receiveMessage( "You sold: " + volume
                + stockSymbol + " at " + price + " amt " + price * price );
        }

    }


    //
    // The following are for test purposes only
    //

    protected String getStockSymbol()
    {
        return stockSymbol;
    }


    protected String getCompanyName()
    {
        return companyName;
    }


    protected double getLoPrice()
    {
        return loPrice;
    }


    protected double getHiPrice()
    {
        return hiPrice;
    }


    protected double getLastPrice()
    {
        return lastPrice;
    }


    protected int getVolume()
    {
        return volume;
    }


    protected PriorityQueue<TradeOrder> getBuyOrders()
    {
        return buyOrders;
    }


    protected PriorityQueue<TradeOrder> getSellOrders()
    {
        return sellOrders;
    }


    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this Stock.
     */
    public String toString()
    {
        String str = this.getClass().getName() + "[";
        String separator = "";

        Field[] fields = this.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            try
            {
                str += separator + field.getType().getName() + " "
                    + field.getName() + ":" + field.get( this );
            }
            catch ( IllegalAccessException ex )
            {
                System.out.println( ex );
            }

            separator = ", ";
        }

        return str + "]";
    }
}
