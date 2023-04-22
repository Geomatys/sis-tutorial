/*
 * This tutorial is in public domain.
 */
package mycompany;

import mycompany.geospatial.Services;

// Implementation-neutral
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.IncommensurableException;
import org.opengis.geometry.Envelope;
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
     * @throws IncommensurableException if a unit of measurement can not be converted.
     */
    static void printCityLocations() throws FactoryException, TransformException, IncommensurableException {
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
        /*
         * Show again the same coordinates, but with a different unit of measurement.
         * For making this test simpler, we assume that both axes use the same units.
         * But a more robust code would use an `UnitConverter` for each axis.
         */
        assert xUnit.equals(yUnit) : "This tutorial assumes same unit for X and Y axes.";
        UnitConverter uc = xUnit.getConverterToAny(Services.mileUnit());
        for (int i=0; i<coordinates.length; i++) {
            coordinates[i] = uc.convert(coordinates[i]);
        }
        System.out.println();
        System.out.println("After unit conversion:");
        for (int i=0; i<numPoints; i++) {
            System.out.printf("City location: %6.0f %s, %5.0f %s%n",
                    coordinates[i*numDimensions  ], Services.mileUnit(),
                    coordinates[i*numDimensions+1], Services.mileUnit());
        }
        /*
         * Bonus: demonstration of some Seshat capabilities for unit operations.
         */
        System.out.println();
        System.out.println("Unit operations:");
        System.out.printf("metres × 1000 = %s%n", Services.metreUnit().multiply(1000));
        System.out.printf("kg × m/s² = %s%n",
                Services.kilogramUnit().multiply(Services.metreUnit())
                        .divide(Services.secondUnit().pow(2)));
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

    /**
     * Prints the result of projecting an envelope.
     *
     * @param  args  ignored.
     * @throws FactoryException   if an error occurred while creating a Coordinate Reference System (CRS).
     * @throws TransformException if an error occurred while transforming coordinates to the target CRS.
     */
    static void printEnvelopeProjection() throws FactoryException, TransformException {
        CRSAuthorityFactory factory = Services.getAuthorityFactory("EPSG");
        GeographicCRS sourceCRS = factory.createGeographicCRS("4326");              // WGS 84
        ProjectedCRS  targetCRS = factory.createProjectedCRS ("5041");              // Polar stereographic.
        Envelope      sourceEnv = Services.envelope(sourceCRS, 84, -20, 88, 50);    // (lower, upper).
        Envelope      targetEnv = Services.transform(sourceEnv, targetCRS);
        Envelope      corners   = transformCorners(sourceEnv, Services.findOperation(sourceCRS, targetCRS));

        System.out.println();
        System.out.println("Comparaison of envelope transformations");
        System.out.printf("Source:  %s%n", sourceEnv);
        System.out.printf("Target:  %s%n", targetEnv);
        System.out.printf("Corners: %s%n", corners);
        System.out.printf("Errors on Y axis when using corners: %6.3f kilometres.%n",
                          (corners.getMinimum(1) - targetEnv.getMinimum(1)) / 1000);
    }

    /**
     * Transforms the 4 corners of a two-dimensional envelope. This is not recommended.
     * This method is provided only for demonstrating that this approach is not sufficient.
     *
     * @param  bbox       the bounding box to transform.
     * @param  operation  the coordinate operation to use for transforming corners.
     * @return the result of transforming the 4 corners of the provided bounding box.
     * @throws TransformException if a coordinate cannot be converted.
     */
    private static Envelope transformCorners(Envelope bbox, CoordinateOperation operation) throws TransformException {
        double[] corners = {
            bbox.getMinimum(0), bbox.getMinimum(1),
            bbox.getMaximum(0), bbox.getMinimum(1),
            bbox.getMaximum(0), bbox.getMaximum(1),
            bbox.getMinimum(0), bbox.getMaximum(1)
        };
        operation.getMathTransform().transform(corners, 0, corners, 0, 4);
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        for (int i=0; i<corners.length;) {
            final double x = corners[i++];
            final double y = corners[i++];
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        return Services.envelope(operation.getTargetCRS(), xmin, ymin, xmax, ymax);
    }
}
