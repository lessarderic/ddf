/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.common.test.restito;

import static org.junit.Assert.fail;
import static com.xebialabs.restito.semantics.Action.bytesContent;
import static com.xebialabs.restito.semantics.Action.contentType;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.withPostBodyContaining;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger;

import com.xebialabs.restito.builder.stub.StubHttp;
import com.xebialabs.restito.builder.verify.VerifyHttp;
import com.xebialabs.restito.server.StubServer;

public class FederatedCswServer {
    private static final XLogger LOGGER =
            new XLogger(LoggerFactory.getLogger(FederatedCswServer.class));

    private final String sourceId;

    private final String httpRoot;

    private final int port;

    private StubServer cswStubServer;

    public FederatedCswServer(String sourceId, String httpRoot, int port) {
        if (httpRoot.endsWith(":")) {
            this.httpRoot = httpRoot.substring(0, httpRoot.length() - 1);
        } else {
            this.httpRoot = httpRoot;
        }

        this.sourceId = sourceId;
        this.port = port;
    }

    public void start() {
        try {
            cswStubServer = new StubServer(port).run();

            setupCapabilityResponse();
            setupQueryResponse();
        } catch (IOException | RuntimeException e) {
            fail("Failed to setup " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public void stop() {
        cswStubServer.stop();
    }

    public void reset() {
        // There is no way in version 0.7 of Restito to clear the calls and reset the
        // StubServer so we need to stop the current one and create a new instance.
        // TODO - Replace this code when the new version of Restito is used.
        stop();
        start();
    }

    public StubHttp whenHttp() {
        return StubHttp.whenHttp(cswStubServer);
    }

    public VerifyHttp verifyHttp() {
        return VerifyHttp.verifyHttp(cswStubServer);
    }

    private void setupCapabilityResponse() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(
                "/csw-get-capabilities-response.xml")) {
            String document = substituteTags(IOUtils.toString(inputStream));

            LOGGER.debug("Capability response: \n{}", document);

            whenHttp().match(get("/services/csw"))
                    .then(ok(), contentType("text/xml"), bytesContent(document.getBytes()));
        }
    }

    private void setupQueryResponse() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/csw-query-response.xml")) {
            String document = substituteTags(IOUtils.toString(inputStream));

            LOGGER.debug("Query response: \n{}", document);

            whenHttp().match(post("/services/csw"), withPostBodyContaining("GetRecords"))
                    .then(ok(), contentType("text/xml"), bytesContent(document.getBytes()));
        }
    }

    private String substituteTags(String document) {
        String resultDocument = substituteSourceId(document);
        resultDocument = substitutePortNumber(resultDocument);
        return substituteHttpRoot(resultDocument);
    }

    private String substitutePortNumber(String document) {
        return document.replaceAll("\\{\\{port\\}\\}", Integer.toString(port));
    }

    private String substituteSourceId(String document) {
        return document.replaceAll("\\{\\{sourceId\\}\\}", sourceId);
    }

    private String substituteHttpRoot(String document) {
        return document.replaceAll("\\{\\{httpRoot\\}\\}", httpRoot);
    }
}
