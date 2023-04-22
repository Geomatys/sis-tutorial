/*
 * This tutorial is in public domain.
 */
package mycompany;

// Implementation-neutral
import javax.measure.IncommensurableException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * An application with no direct dependency to the
 * "referencing by coordinates" or "metadata" library.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("The application main class.");
        try {
            Referencing.printCityLocations();
            Referencing.printEnvelopeProjection();
        } catch (FactoryException | TransformException | IncommensurableException e) {
            System.err.println("Coordinate operation failed.");
            e.printStackTrace();
        }
    }
}
