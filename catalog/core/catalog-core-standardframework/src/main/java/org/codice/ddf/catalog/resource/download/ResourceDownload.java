package org.codice.ddf.catalog.resource.download;

import ddf.catalog.operation.ResourceResponse;

/**
 * Operations related to the download of a metacard resource.
 */
public interface ResourceDownload {
    ResourceResponse start();
}
