package uk.ac.ed.inf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        System.out.println( "Small test for ILPDataObject" );
        var orderStatus = OrderStatus.DELIVERED;
        System.out.println( "ILPDataObject.jar was used" );
    }
}
