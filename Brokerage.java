import java.lang.reflect.*;
import java.util.*;


/**
 * Represents a brokerage.
 */
public class Brokerage implements Login
{
    private Map<String, Trader> traders;

    private Set<Trader> loggedTraders;

    private StockExchange exchange;


    public Brokerage( StockExchange ex )
    {
        exchange = ex;
        traders = new TreeMap<String, Trader>();
        loggedTraders = new TreeSet<Trader>();
    }


    public int addUser( String name, String password )
    {
        if ( name.length() > 10 || name.length() < 4 )
        {
            return -1;
        }
        if ( password.length() > 10 || password.length() < 2 )
        {
            return -2;
        }
        if ( traders.containsKey( name ) )
        {
            return -3;
        }

        loggedTraders.add( new Trader( this, name, password ) );
        return 0;

    }


    public int login( String name, String password )
    {
        Trader trader = traders.get( name );
        if ( trader == null )
        {
            return -1;
        }
        if ( !trader.getPassword().equals( password ) )
        {
            return -2;
        }
        if ( loggedTraders.contains( name ) )
        {
            return -3;
        }
        if ( !trader.hasMessages() )
        {
            trader.receiveMessage( "Welcome to SafeTrade!" );
            trader.openWindow();
            loggedTraders.add( trader );
        }
        trader.openWindow();
        return 0;

    }


    public void logout( Trader trader )
    {
        loggedTraders.remove( trader );
    }


    public void getQuote( String symbol, Trader trader )
    {
        trader.receiveMessage( exchange.getQuote(symbol) ); 
    }
    
    public void placeOrder(TradeOrder order)
    {
        exchange.placeOrder(order); 
    }


    //
    // The following are for test purposes only
    //
    protected Map<String, Trader> getTraders()
    {
        return traders;
    }


    protected Set<Trader> getLoggedTraders()
    {
        return loggedTraders;
    }


    protected StockExchange getExchange()
    {
        return exchange;
    }


    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this Brokerage.
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


