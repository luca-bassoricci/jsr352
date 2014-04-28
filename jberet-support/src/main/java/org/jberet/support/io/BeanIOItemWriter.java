/*
 * Copyright (c) 2014 Red Hat, Inc. and/or its affiliates.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Cheng Fang - Initial API and implementation
 */

package org.jberet.support.io;

import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

/**
 * An implementation of {@code javax.batch.api.chunk.ItemWriter} based on BeanIO. This writer class handles all
 * data formats that are supported by BeanIO, e.g., fixed length file, CSV file, XML, etc. It also supports
 * dynamic BeanIO mapping properties, which are specified in job xml, injected into this class, and can be referenced
 * in BeanIO mapping file. {@link org.jberet.support.io.BeanIOItemWriter} configurations are specified as
 * reader properties in job xml.
 */
@Named
@Dependent
public class BeanIOItemWriter extends BeanIOItemReaderWriterBase implements ItemWriter {
    private StreamFactoryKey mappingFileKey;
    private BeanWriter beanWriter;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        mappingFileKey = new StreamFactoryKey(jobContext, streamMapping);
        final StreamFactory streamFactory = getStreamFactory(streamFactoryLookup, mappingFileKey, mappingProperties);
        final Writer outputWriter = new OutputStreamWriter(getOutputStream(CsvProperties.OVERWRITE));
        beanWriter = streamFactory.createWriter(streamName, outputWriter);
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        for (final Object e : items) {
            beanWriter.write(e);
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }

    @Override
    public void close() throws Exception {
        if (beanWriter != null) {
            beanWriter.close();
            beanWriter = null;
        }
    }
}
