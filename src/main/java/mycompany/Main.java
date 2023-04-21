/*
 * This tutorial is in public domain.
 */
package mycompany;

// Implementation-neutral
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
        } catch (FactoryException | TransformException e) {
            System.err.println("Coordinate operation failed.");
            e.printStackTrace();
        }
    }
}
