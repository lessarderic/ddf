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
package org.codice.ddf.catalog.resource.download.impl;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.codice.ddf.catalog.resource.download.ResourceDownload;
import org.codice.ddf.catalog.resource.download.plugin.internal.ResourceDownloadHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;

/**
 * Class that chains and keeps track of all the registered {@link ResourceDownloadHandler} that
 * need to be called before a resource download starts.
 */
public class ResourceDownloadHandlerChain {

    private Supplier<ResourceDownload> resourceDownloadSupplier;

    private ResourceDownloadHandler handlersHead;

    public void init() {
        List<ServiceReference<ResourceDownloadHandler>> handlers = getHandlers();
        handlersHead = chainHandlers(handlers);
    }

    public ResourceResponse handle(ResourceRequest resourceRequest, Metacard metacard) {
        return handlersHead.handle(resourceRequest, metacard);
    }

    private List<ServiceReference<ResourceDownloadHandler>> getHandlers() {
        // TODO - Get ResourceDownloadHandler OSGi services
        return Collections.emptyList();
    }

    private ResourceDownloadHandler chainHandlers(
            List<ServiceReference<ResourceDownloadHandler>> handlers) {
        // TODO - Chain handlers to each other and add a last one that creates a new
        // ResourceDownload and delegates to it

        ResourceDownloadHandler lastResourceDownloadHandler = new ResourceDownloadHandler() {
            @Override
            public void setNextHandler(ResourceDownloadHandler nextResourceDownloadHandler) {
                // No-op
            }

            @Override
            public ResourceResponse handle(ResourceRequest resourceRequest, Metacard metacard) {
                ResourceDownload resourceDownload = resourceDownloadSupplier.get();
                return resourceDownload.start();
            }
        };

        ResourceDownloadHandler resourceDownloadHandler =
                getBundleContext().getService(handlers.get(0));
        resourceDownloadHandler.setNextHandler(lastResourceDownloadHandler);

        return resourceDownloadHandler;
    }

    public BundleContext getBundleContext() {
        // TODO
        return null;
    }

    public void setResourceDownloadSupplier(Supplier<ResourceDownload> resourceDownloadSupplier) {
        this.resourceDownloadSupplier = resourceDownloadSupplier;
    }
}
