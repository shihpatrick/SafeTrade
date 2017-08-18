import java.lang.reflect.*;
import java.util.*;


/**
 * Represents a stock trader.
 */
public class Trader implements Comparable<Trader>
{
    private Brokerage brokerage;

    private String screenName, password;

    private TraderWindow myWindow;

    private Queue<String> mailbox;


    public Trader( Brokerage brkg, String n, String p )
    {
        brokerage = brkg;
        screenName = n;
        password = p;
        myWindow = null;
        mailbox = new LinkedList<String>();

    }


    // Compares this trader to another by comparing their screen names case
    // blind.
    public int compareTo( Trader other )
    {
        return this.screenName.compareToIgnoreCase( other.screenName );
    }


    // Indicates whether some other trader is "equal to" this one, based on
    // comparing their screen names case blind.
    public boolean equals( Object other )
    {
        return this.compareTo( (Trader)other ) == 0;
    }


    // Returns the screen name for this trader.
    public String getName()
    {
        return screenName;
    }


    // Returns the password for this trader.
    public String getPassword()
    {
        return password;
    }


    // Requests a quote for a given stock symbol from the brokerage by calling
    // brokerage's getQuote.
    public void getQuote( String symbol )
    {
        brokerage.getQuote( symbol, this );
    }


    // Returns true if this trader has any messages in its mailbox.
    public boolean hasMessages()
    {
        return !mailbox.isEmpty();
    }


    // Creates a new TraderWindow for this trader and saves a reference to it in
    // myWindow.
    public void openWindow()
    {
        myWindow = new TraderWindow( this );
        while ( !mailbox.isEmpty() )
        {
            myWindow.showMessage( mailbox.remove() );
        }
    }


    // Places a given order with the brokerage by calling brokerage's
    // placeOrder.
    public void placeOrder( TradeOrder order )
    {
        brokerage.placeOrder( order );
    }


    // Logs out this trader.
    public void quit()
    {
        brokerage.logout(this);
        myWindow = null;
    }


    // Adds msg to this trader's mailbox and displays all messages.
    public void receiveMessage( String msg )
    {
        mailbox.add( msg );
        if ( myWindow == null )
        {
            while ( !mailbox.isEmpty() )
            {
                myWindow.showMessage( mailbox.remove() );
            }
        }

    }


    //
    // The following are for test purposes only
    //
    protected Queue<String> mailbox()
    {
        return mailbox;
    }


    /**
     * <p>
     * A generic toString implementation that uses reflection to print names and
     * values of all fields <em>declared in this class</em>. Note that
     * superclass fields are left out of this implementation.
     * </p>
     * 
     * @return a string representation of this Trader.
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
                if ( field.getType().getName().equals( "Brokerage" ) )
                    str += separator + field.getType().getName() + " "
                        + field.getName();
                else
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
