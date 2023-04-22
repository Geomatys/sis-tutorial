package mycompany.geospatial;

import java.io.File;
import java.util.Collection;
import java.awt.image.ImagingOpException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;

public class ReadGeoTIFF {
    /**
     * Demo entry point.
     *
     * @param  args  ignored.
     * @throws DataStoreException if an error occurred while reading the raster.
     * @throws ImagingOpException unchecked exception thrown if an error occurred while loading a tile.
     */
    public static void main(String[] args) throws DataStoreException {
        GridCoverage data;
        try (DataStore store = DataStores.open(new File("Aéroport.tiff"))) {
            /*
             * This data store is an aggregate because a GeoTIFF file may contain many images.
             * Not all data stores are aggregate, so the following casts do not apply to all.
             * For this example, we know that the file is GeoTIFF and we take the first image.
             */
            Collection<? extends Resource> allImages = ((Aggregate) store).components();
            GridCoverageResource firstImage = (GridCoverageResource) allImages.iterator().next();
            /*
             * Read the resource immediately and fully.
             */
            data = firstImage.read(null, null);
            System.out.printf("Information about the selected image:%n%s%n", data);
            /*
             * Read only a subset of the resource. The Area Of Interest can be specified
             * in any Coordinate Reference System (CRS). The envelope will be transformed
             * automatically to the CRS of the data (the data are not transformed).
             * This example uses Universal Transverse Mercator (UTM) zone 31 North.
             */
            var areaOfInterest = new GeneralEnvelope(CommonCRS.WGS84.universal(49, 2.5));
            areaOfInterest.setRange(0,   46600,  467000);       // Minimal and maximal easting values (metres)
            areaOfInterest.setRange(1, 5427000, 5428000);       // Minimal and maximal northing values (metres).
            data = firstImage.read(new GridGeometry(null, areaOfInterest, GridOrientation.HOMOTHETY), null);
            System.out.printf("Information about the resource subset:%n%s%n",
                              data.getGridGeometry().getExtent());
        }
        /*
         * By default, it is possible to continue to use the `GridCoverage` (but not the `Resource`) after
         * the `DataStore` has been closed because data are in memory. Note that it would not be the case
         * if deferred data loading was enabled has shown in "Handle rasters bigger than memory" example.
         */
    }
}
