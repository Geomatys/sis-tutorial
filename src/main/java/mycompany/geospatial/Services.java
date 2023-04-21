/*
 * This tutorial is in public domain.
 */
package mycompany.geospatial;

// Implementation-neutral
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.util.FactoryException;

// Implementation-dependent
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;


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
     * @throws FactoryException if the factory is not available.
     */
    public static CRSAuthorityFactory getAuthorityFactory(String authority) throws FactoryException {
        return CRS.getAuthorityFactory(authority);
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
        return CRS.findOperation(sourceCRS, targetCRS, null);
    }

    public static Unit<Length> metreUnit()    {return Units.METRE;}
    public static Unit<Mass>   kilogramUnit() {return Units.KILOGRAM;}
    public static Unit<Time>   secondUnit()   {return Units.SECOND;}
    public static Unit<Length> mileUnit()     {return Units.STATUTE_MILE;}
}
