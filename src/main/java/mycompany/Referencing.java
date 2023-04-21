/*
 * This tutorial is in public domain.
 */
package mycompany;

import mycompany.geospatial.Services;

// Implementation-neutral
import javax.measure.Unit;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.MathTransform;
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
        System.out.println();
        /*
         * Get the axis units of measurement. The unit types are
         * usually `Unit<Length>` or `Unit<Angle>`, but for this
         * test we do not need to know the exact type.
         */
        final int numDimensions = 2;
        CoordinateSystem cs = targetCRS.getCoordinateSystem();
        assert cs.getDimension() == numDimensions : "This tutorial assumes 2D systems.";
        Unit<?> xUnit = cs.getAxis(0).getUnit();
        Unit<?> yUnit = cs.getAxis(1).getUnit();
        /*
         * Coordinates are given with only 3 significant digits,
         * which implies an inaccuracy of more than 100 metres.
         * With such error, it does not matter if the geodetic
         * reference frame is WGS84, NAD83 or NAD27.
         *
         * Axis order is latitude first, then longitude.
         * Units are degrees.
         */
        double[] coordinates = {
            49.250, -123.100,           // Vancouver
            37.783, -122.417,           // San-Francisco
            45.500,  -73.567,           // Montreal
            40.713,  -74.006,           // New-York
            48.801,    2.351,           // Paris
            35.666,  139.772};          // Tokyo
        /*
         * It would be possible to transform point-by-point,
         * but bulk transforms are much faster.
         */
        int numPoints = coordinates.length / numDimensions;
        MathTransform mt = op.getMathTransform();
        mt.transform(coordinates, 0, coordinates, 0, numPoints);
        for (int i=0; i<numPoints; i++) {
            System.out.printf("City location: %9.0f %s, %8.0f %s%n",
                    coordinates[i*numDimensions  ], xUnit,
                    coordinates[i*numDimensions+1], yUnit);
        }
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
