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
package ddf.catalog.resource.download;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ddf.catalog.CatalogFramework;
import ddf.catalog.operation.ResourceRequest;
import ddf.catalog.operation.ResourceResponse;
import ddf.catalog.resource.ResourceNotFoundException;
import ddf.catalog.resource.ResourceNotSupportedException;

@RunWith(MockitoJUnitRunner.class)
public class ResourceDownloadEndpointTest {

    private static final String METACARD_ID = "57a4b894e13a455b8cccb87cec778b58";

    private static final String SOURCE_ID = "ddf.distribution";

    private static final String DOWNLOAD_ID = "download ID";

    @Mock
    private CatalogFramework mockCatalogFramework;

    @Mock
    private ReliableResourceDownloadManager mockDownloadManager;

    @Mock
    private ResourceResponse mockResourceResponse;

    private ObjectMapper objectMapper = JsonFactory.create();

    @Before
    public void setup() {
        when(mockResourceResponse.getPropertyValue("downloadIdentifier")).thenReturn(DOWNLOAD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheOnlyUsingGetWhenCacheDisabled() throws Exception {
        // Setup
        setupMockDownloadManager(false);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID, METACARD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheOnlyUsingGetWhenNullResourceResponse() throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(null, null);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID, METACARD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheOnlyUsingGetWhenCatalogFrameworkThrowsIOException()
            throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(mockResourceResponse, IOException.class);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID, METACARD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheOnlyUsingGetWhenCatalogFrameworkThrowsResourceNotSupportedException()
            throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(mockResourceResponse, ResourceNotSupportedException.class);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID, METACARD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheOnlyUsingGetWhenCatalogFrameworkThrowsResourceNotFoundException()
            throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(mockResourceResponse, ResourceNotFoundException.class);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID, METACARD_ID);
    }

    @Test
    public void downloadToCacheOnlyUsingGet() throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(mockResourceResponse, null);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        Response response = resourceDownloadEndpoint.getDownloadListOrDownloadToCache(SOURCE_ID,
                METACARD_ID);

        ResourceDownloadEndpoint.ResourceDownloadResponse resourceDownloadResponse =
                objectMapper.fromJson((String) response.getEntity(),
                        ResourceDownloadEndpoint.ResourceDownloadResponse.class);

        assertThat(resourceDownloadResponse.getDownloadIdentifier(), is(DOWNLOAD_ID));
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test
    public void downloadToCacheUsingPost() throws Exception {
        // Setup
        setupMockDownloadManager(true);
        setupMockCatalogFramework(mockResourceResponse, null);
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);

        // Perform Test
        Response response = resourceDownloadEndpoint.downloadToCache(SOURCE_ID, METACARD_ID);

        ResourceDownloadEndpoint.ResourceDownloadResponse resourceDownloadResponse =
                objectMapper.fromJson((String) response.getEntity(),
                        ResourceDownloadEndpoint.ResourceDownloadResponse.class);

        assertThat(resourceDownloadResponse.getDownloadIdentifier(), is(DOWNLOAD_ID));
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheUsingPostWithNullSourceId() throws Exception {
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);
        resourceDownloadEndpoint.downloadToCache(null, METACARD_ID);
    }

    @Test(expected = DownloadToCacheOnlyException.class)
    public void downloadToCacheUsingPostWithNullMetacardId() throws Exception {
        ResourceDownloadEndpoint resourceDownloadEndpoint = new ResourceDownloadEndpoint(
                mockCatalogFramework,
                mockDownloadManager,
                objectMapper);
        resourceDownloadEndpoint.downloadToCache(SOURCE_ID, null);
    }

    private void setupMockDownloadManager(boolean isCacheEnabled) {
        when(mockDownloadManager.isCacheEnabled()).thenReturn(isCacheEnabled);
    }

    private void setupMockCatalogFramework(ResourceResponse mockResourceResponse,
            Class<? extends Throwable> exceptionClass) throws Exception {
        when(mockCatalogFramework.getResource(any(ResourceRequest.class),
                eq(SOURCE_ID))).thenReturn(mockResourceResponse);

        if (exceptionClass != null) {
            doThrow(exceptionClass).when(mockCatalogFramework)
                    .getResource(any(ResourceRequest.class), eq(SOURCE_ID));
        }
    }
}