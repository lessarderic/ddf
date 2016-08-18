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

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import org.codice.ddf.catalog.resource.download.ResourceDownload;

import ddf.catalog.data.Metacard;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;

/**
 * Queue of resources that are being downloaded or queued to be downloaded.
 */
public class ResourceDownloadQueue
        implements org.codice.ddf.catalog.resource.download.ResourceDownloadQueue,
        Supplier<ResourceDownload> {

    private ResourceDownloadHandlerChain handlerChain;

    private Queue<ResourceDownload> downloads = new LinkedList<>();

    public ResourceDownloadQueue(ResourceDownloadHandlerChain handlerChain) {
        this.handlerChain = handlerChain;
        handlerChain.setResourceDownloadSupplier(this);
    }

    @Override
    public ResourceResponse add(ResourceRequest resourceRequest, Metacard metacard) {
        return handlerChain.handle(resourceRequest, metacard);
    }

    @Override
    public ResourceDownload get() {
        ResourceDownload resourceDownload =
                new org.codice.ddf.catalog.resource.download.impl.ResourceDownload();
        downloads.add(resourceDownload);
        return resourceDownload;
    }
}
