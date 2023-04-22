/*
 * This tutorial is in public domain.
 */
package mycompany.geospatial;

// Implementation-neutral
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

// Implementation-dependent
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
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

    /**
     * Transforms the given envelope to the specified CRS using the services offered by the library.
     * The result is better than what we get with a naive algorithm transforming the 4 corners.
     *
     * @param  env  the envelope to transform.
     * @param  crs  the desired CRS for the envelope to return.
     * @return the envelope transformed to the specified CRS.
     * @throws TransformException if at least one coordinate tuple can not be transformed.
     */
    public static Envelope transform(Envelope env, CoordinateReferenceSystem crs) throws TransformException {
        return Envelopes.transform(env, crs);
    }

    /**
     * Creates a two-dimensional envelope with the given values.
     *
     * @param  crs   the coordinate reference system of the envelope.
     * @param  xmin  minimal value on the first axis.
     * @param  ymin  minimal value on the second axis
     * @param  xmax  maximal value on the first axis
     * @param  ymax  maximal value on the second axis
     * @return the two-dimensional envelope.
     */
    public static Envelope envelope(CoordinateReferenceSystem crs, double xmin, double ymin, double xmax, double ymax) {
        var env = new GeneralEnvelope(crs);
        env.setRange(0, xmin, xmax);
        env.setRange(1, ymin, ymax);
        return env;
    }

    public static Unit<Length> metreUnit()    {return Units.METRE;}
    public static Unit<Mass>   kilogramUnit() {return Units.KILOGRAM;}
    public static Unit<Time>   secondUnit()   {return Units.SECOND;}
    public static Unit<Length> mileUnit()     {return Units.STATUTE_MILE;}
}
