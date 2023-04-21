/*
 * This tutorial is in public domain.
 */
package mycompany.geospatial;

// Implementation-neutral
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.util.FactoryException;

// Implementation-dependent
import org.osgeo.proj.Proj;


/**
 * A class offering geospatial services to the "MyCompany" project.
 */
public class Services {
    /**
     * Do not allow instantiation of this class.
     */
    private Services() {
    }

    /**
     * Returns the factory of CRS objects for the given authority.
     * The returned interface is implementation independent.
     *
     * <h4>Alternatives</h4>
     * Some implementations allow to fetch the same factory using
     * {@link java.util.ServiceLoader}.
     *
     * @param  authority  the desired authority, for example "EPSG".
     * @return a factory for creating CRS objects.
     */
    public static CRSAuthorityFactory getAuthorityFactory(String authority) {
        return Proj.getAuthorityFactory(authority);
    }

    /**
     * Returns the coordinate operation between the given pair of CRS.
     *
     * @param  sourceCRS  the source of the coordinate operation.
     * @param  targetCRS  the target of the coordinate operation.
     * @return the coordinate operation between the given CRS.
     * @throws FactoryException if an error occurred while creating the operation.
     */
    public static CoordinateOperation findOperation(CoordinateReferenceSystem sourceCRS,
                                                    CoordinateReferenceSystem targetCRS) throws FactoryException
    {
        return Proj.createCoordinateOperation(sourceCRS, targetCRS, null);
    }
}
