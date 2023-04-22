/*
 * This tutorial is in public domain.
 */
package mycompany.geospatial;

import java.net.URL;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;

// Implementation-specific
import org.apache.sis.xml.XML;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;


/**
 * Demonstration project for OGC TestBed 18 task D025 — Reference Frame Transformation Engineering Report.
 * The coordinate transformation applied here is completely dummy.
 */
public class VoyagerToObservatory {
    /**
     * The CRS used in Testbed 18 for temporal coordinates.
     * An industrial application would infer this CRS from the GML file to read.
     * But for this simple demo application, we use hard-coded value for simplicity.
     */
    public static final DefaultTemporalCRS TIME_CRS = DefaultTemporalCRS.castOrCopy(CommonCRS.Temporal.TRUNCATED_JULIAN.crs());

    /**
     * Application entry point.
     *
     * @param  args  command-line arguments (ignored).
     * @throws FileNotFoundException if the XML file is not found.
     * @throws JAXBException if an error occurred while parsing the XML file.
     * @throws TransformException if an error occurred during the transformation of the test position.
     */
    public static void main(String[] args) throws FileNotFoundException, JAXBException, TransformException {
        // Temporary workaround for loading axis directions that were not present in ISO 19111:2007.
        org.apache.sis.internal.referencing.AxisDirections.FORWARD.family();

        print(loadGML("VoyagerToObservatory.xml"), new GeneralDirectPosition(5000, 100, -400, 19883.788));
    }

    /**
     * Loads the GML file for coordinate transformation.
     *
     * @param  filename  path to the file to load, relative to project root directory.
     * @throws FileNotFoundException if the XML file is not found.
     * @throws JAXBException if an error occurred while parsing the XML file.
     */
    private static CoordinateOperation loadGML(final String filename) throws FileNotFoundException, JAXBException {
        final URL xmlFile = VoyagerToObservatory.class.getClassLoader().getResource(filename);
        if (xmlFile == null) {
            throw new FileNotFoundException(filename);
        }
        return (CoordinateOperation) XML.unmarshal(xmlFile);
    }

    /**
     * Prints the coordinate operation in Well Known Text format
     * and the result of transforming the given coordinate tuple.
     *
     * @param  operation  the operation to print.
     * @param  sourcePos  an arbitrary source position to transform.
     * @throws TransformException if an error occurred during the transformation of the given position.
     */
    private static void print(final CoordinateOperation operation, final DirectPosition sourcePos)
            throws TransformException
    {
        System.out.println("Coordinate operation read from GML file:");
        System.out.println(operation);
        System.out.println();
        System.out.println("Implementation of the coordinate operation:");
        System.out.println(operation.getMathTransform());
        System.out.println("Transform a coordinate tuple");
        System.out.println("Source: " + sourcePos);
        System.out.println("Target: " + operation.getMathTransform().transform(sourcePos, null));
    }
}
