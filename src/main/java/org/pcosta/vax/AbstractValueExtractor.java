/**
 * Licensed under MPL / LGPL dual-license.
 *
 * Copyright (c) 2010 Tamas Eppel <Tamas.Eppel@gmail.com>
 *
 * You should have received a copy of the licenses
 * along with this program.
 * If not, see:
 *
 *    <http://www.gnu.org/licenses/>
 *    <http://www.mozilla.org/MPL/MPL-1.1.html>
 */
package org.pcosta.vax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pcosta.vax.impl.DefaultValueKeyGenerator;
import org.pcosta.vax.impl.IterableFields;
import org.pcosta.vax.impl.IterableMethods;
import org.pcosta.vax.impl.ParsingContext;
import org.pcosta.vax.impl.exception.ValidationException;
import org.pcosta.vax.impl.exception.VaxException;

/**
 *
 * @author Tamas.Eppel@gmail.com
 *
 */
public abstract class AbstractValueExtractor<Extracted, Factory extends FrontEndFactory<Extracted>> {

    private Factory frontEndFactory;

    private ValueKeyGenerator valueKeyGenerator = new DefaultValueKeyGenerator();

    @SuppressWarnings("rawtypes")
    private Map<String, ValueAdapter> valueAdapters = Collections.emptyMap();

    public AbstractValueExtractor() {
    }

    public AbstractValueExtractor(final Factory frontEndFactory) {
        super();
        this.frontEndFactory = frontEndFactory;
    }

    /**
     *
     * @param instance
     * @throws {@link IllegalArgumentException}
     * @throws {@link VaxException}
     * @throws {@link AdapterException}
     * @return
     */
    public Extracted marshal(final Object instance) {

        @SuppressWarnings("rawtypes")
        final Map<String, ValueAdapter> adapters = this.getValueAdapterMap();
        final ValueKeyGenerator keyGenerator = this.getValueKeyGenerator();

        final ExtractorFrontEnd<Extracted> frontEnd = getFrontEndFactory().create();
        frontEnd.init();

        final Queue<ParsingContext> parsingContext = new ConcurrentLinkedQueue<ParsingContext>();
        parsingContext.offer(new ParsingContext(instance));

        final Marshaler<Extracted, FrontEndFactory<Extracted>> marshaler
            = new Marshaler<Extracted, FrontEndFactory<Extracted>>(frontEnd, keyGenerator, adapters, parsingContext);

        while (!parsingContext.isEmpty()) {
            final ParsingContext context = parsingContext.poll();
            for (final Field field : new IterableFields(context.getInstance())) {
                marshaler.process(field, context);
            }
            for (final Method method : new IterableMethods(context.getInstance())) {
                marshaler.process(method, context);
            }
        }

        if (!marshaler.getViolations().isEmpty()) {
            final ValidationException e = new ValidationException();
            e.setViolations(marshaler.getViolations());
            throw e;
        }

        return frontEnd.getExtracted();
    }

    public <T> T unmarshal(final Class<T> clazz, final Extracted values) {
        T result = null;
        try {
            result = unmarshal(clazz.newInstance(), values);
        } catch (final InstantiationException e) {
            new VaxException(e);
        } catch (final IllegalAccessException e) {
            new VaxException(e);
        }
        return result;
    }

    public <T> T unmarshal(final T instance, final Extracted values) {
        @SuppressWarnings("rawtypes")
        final Map<String, ValueAdapter> adapters = this.getValueAdapterMap();
        final ValueKeyGenerator keyGenerator = this.getValueKeyGenerator();

        final ExtractorFrontEnd<Extracted> frontEnd = getFrontEndFactory().create();
        frontEnd.init(values);

        final Queue<ParsingContext> parsingContext = new ConcurrentLinkedQueue<ParsingContext>();
        parsingContext.offer(new ParsingContext(instance));

        final Unmarshaler<Extracted, FrontEndFactory<Extracted>> unmarshaler
        = new Unmarshaler<Extracted, FrontEndFactory<Extracted>>(frontEnd, keyGenerator, adapters, parsingContext);

        while (!parsingContext.isEmpty()) {
            final ParsingContext context = parsingContext.poll();
            for (final Field field : new IterableFields(context.getInstance())) {
                unmarshaler.process(field, context);
            }
            for (final Method method : new IterableMethods(context.getInstance())) {
                unmarshaler.process(method, context);
            }
        }

        if (!unmarshaler.getViolations().isEmpty()) {
            final ValidationException e = new ValidationException();
            e.setViolations(unmarshaler.getViolations());
            throw e;
        }

        return instance;
    }

    public synchronized ValueKeyGenerator getValueKeyGenerator() {
        return this.valueKeyGenerator;
    }

    public synchronized void setValueKeyGenerator(final ValueKeyGenerator valueKeyGenerator) {
        this.valueKeyGenerator = valueKeyGenerator;
    }

    @SuppressWarnings("rawtypes")
    private synchronized Map<String, ValueAdapter> getValueAdapterMap() {
        return this.valueAdapters;
    }

    @SuppressWarnings("rawtypes")
    public synchronized Collection<ValueAdapter> getValueAdapters() {
        return this.valueAdapters.values();
    }

    @SuppressWarnings("rawtypes")
    public synchronized void setValueAdapters(final Collection<? extends ValueAdapter> valueAdapters) {
        if (valueAdapters != null) {
            final Map<String, ValueAdapter> result = new HashMap<String, ValueAdapter>(valueAdapters.size());
            for (final ValueAdapter adapter : valueAdapters) {
                result.put(adapter.getClass().getName(), adapter);
            }
            this.valueAdapters = result;
        }
    }

    public synchronized Factory getFrontEndFactory() {
        return frontEndFactory;
    }

    public synchronized void setFrontEndFactory(final Factory frontEndFactory) {
        this.frontEndFactory = frontEndFactory;
    }

}
