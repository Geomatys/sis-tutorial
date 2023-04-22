package mycompany.geospatial;

import java.io.File;
import java.util.Collection;
import java.awt.image.ImagingOpException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.apache.sis.storage.Resource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.image.Interpolation;
import org.apache.sis.referencing.CRS;

public class ReadGeoTIFF {
    /**
     * Demo entry point.
     *
     * @param  args  ignored.
     * @throws DataStoreException if an error occurred while reading the raster.
     * @throws ImagingOpException unchecked exception thrown if an error occurred while loading a tile.
     * @throws FactoryException   if an error occurred while creating the Coordinate Reference System (CRS).
     * @throws TransformException if an error occurred while transforming coordinates to the target CRS.
     */
    public static void main(String[] args) throws DataStoreException, FactoryException, TransformException {
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
        }
        /*
         * By default, it is possible to continue to use the `GridCoverage` (but not the `Resource`) after
         * the `DataStore` has been closed because data are in memory. Note that it would not be the case
         * if deferred data loading was enabled has shown in "Handle rasters bigger than memory" example.
         *
         * Reproject to "WGS 84 / World Mercator" (EPSG::3395) using bilinear interpolation.
         * This example lets Apache SIS choose the output grid size and resolution.
         * But it is possible to specify those aspects if desired.
         */
        var processor = new GridCoverageProcessor();
        processor.setInterpolation(Interpolation.BILINEAR);
        data = processor.resample(data, CRS.forCode("EPSG::3395"));
        System.out.printf("Information about the image after reprojection:%n%s%n", data.getGridGeometry());
    }
}
