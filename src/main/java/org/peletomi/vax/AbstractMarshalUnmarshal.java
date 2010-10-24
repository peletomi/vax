package org.peletomi.vax;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.peletomi.vax.annotation.Value;
import org.peletomi.vax.impl.ParsingContext;
import org.peletomi.vax.impl.util.BeanUtils;

import com.google.common.collect.ImmutableList;

public abstract class AbstractMarshalUnmarshal<Extracted, Factory extends FrontEndFactory<Extracted>> {

    protected final ExtractorFrontEnd<Extracted> frontEnd;

    protected final ValueKeyGenerator keyGenerator;

    @SuppressWarnings("rawtypes")
    protected final Map<String, ValueAdapter> adapters;

    protected final List<String> violations = new ArrayList<String>();

    protected final Queue<ParsingContext> parsingContext;

    protected ParsingContext currentContext;

    protected AnnotatedElement element;

    public AbstractMarshalUnmarshal(final ExtractorFrontEnd<Extracted> frontEnd, final ValueKeyGenerator keyGenerator,
            @SuppressWarnings("rawtypes") final Map<String, ValueAdapter> adapters, final Queue<ParsingContext> parsingContext) {
        super();
        this.frontEnd = frontEnd;
        this.keyGenerator = keyGenerator;
        this.adapters = adapters;
        this.parsingContext = parsingContext;
    }

    protected abstract void process();

    public void process(final AnnotatedElement element, final ParsingContext currentContext) {
        clear();
        checkNotNull(element);
        checkNotNull(currentContext);
        this.element = element;
        this.currentContext = currentContext;
        process();
        clear();
    }

    public List<String> getViolations() {
        return violations;
    }

    protected void clear() {
        currentContext = null;
        element = null;
    }

    protected String getName() {
        String name;
        if (element instanceof Method) {
            final Method method = (Method)element;
            name = BeanUtils.getName(BeanUtils.getMethodName(method), method);
        } else {
            final Field field = (Field)element;
            name = BeanUtils.getName(field.getName(), field);
        }
        return name;
    }

    protected List<String> validate(final String name, final Object value) {
        List<String> result = Collections.emptyList();
        final boolean required = element.getAnnotation(Value.class).required();
        boolean parentRequired;
        if (currentContext.getParentElement() == null) {
            parentRequired = true;
        } else {
            parentRequired = currentContext.getParentElement().getAnnotation(Value.class).required();
        }
        if (required && parentRequired && (value == null || "".equals(value.toString()))) {
            result = ImmutableList.copyOf(
                      new String[] {String.format("value [%s] required but not set in class [%s]",
                              name, currentContext.getInstance().getClass().getName()) });
        }
        return result;
    }

}
