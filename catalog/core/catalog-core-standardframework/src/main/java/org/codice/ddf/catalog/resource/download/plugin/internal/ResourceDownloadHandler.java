/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.catalog.resource.download.plugin.internal;

import javax.annotation.Nullable;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;

/**
 * Service interface that needs to be implemented to perform pre or post resource download
 * processing.
 */
public interface ResourceDownloadHandler {
    /**
     * Sets the reference to the next {@link ResourceDownloadHandler} to call.
     *
     * @param nextResourceDownloadHandler reference to the next service to call
     */
    void setNextHandler(@Nullable ResourceDownloadHandler nextResourceDownloadHandler);

    /**
     * Handles the current resource download request. This method is responsible for calling
     * the next {@link ResourceDownloadHandler} once its processing is complete. Not calling
     * the next handler will stop the chain.
     *
     * @param resourceRequest resource request
     * @param metacard        metacard for which the resource is requested
     * @return response for the requested resource
     */
    ResourceResponse handle(ResourceRequest resourceRequest, Metacard metacard);
}
