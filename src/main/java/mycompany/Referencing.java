/*
 * This tutorial is in public domain.
 */
package mycompany;

import mycompany.geospatial.Services;

// Implementation-neutral
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Demonstration of referencing services.
 */
class Referencing {
    /**
     * Shows the projected coordinates of some cities.
     *
     * @throws FactoryException if a CRS or coordinate operation can not be created.
     * @throws TransformException if a point can not be transformed.
     */
    static void printCityLocations() throws FactoryException, TransformException {
        CRSAuthorityFactory factory = Services.getAuthorityFactory("EPSG");
        GeographicCRS sourceCRS = factory.createGeographicCRS("4326");
        ProjectedCRS  targetCRS = factory.createProjectedCRS ("3395");
        CoordinateOperation  op = Services.findOperation(sourceCRS, targetCRS);
        /*
         * We should always check if the operation
         * that we got is suitable for our purpose.
         */
        System.out.printf("Coordinate operation: %s%n", op.getName().getCode());
        System.out.printf("Scope: %s%n", op.getScope());
        printGeographicExtent(op.getDomainOfValidity());
    }

    private static void printGeographicExtent(Extent domain) {
        for (GeographicExtent geographic : domain.getGeographicElements()) {
            if (geographic instanceof GeographicBoundingBox bbox) {
                System.out.println("Domain of validity:");
                System.out.printf("- South bound latitude: %6.1f°%n", bbox.getSouthBoundLatitude());
                System.out.printf("- North bound latitude: %6.1f°%n", bbox.getNorthBoundLatitude());
                System.out.printf("- West bound longitude: %6.1f°%n", bbox.getWestBoundLongitude());
                System.out.printf("- East bound longitude: %6.1f°%n", bbox.getEastBoundLongitude());
            }
        }
    }
}
