package org.simpleframework.xml.core;

import com.microsoft.cll.android.EventEnums;
import java.util.ArrayList;
import java.util.List;

class ClassInstantiator implements Instantiator {
    private final List<Creator> creators;
    private final Detail detail;
    private final Creator primary;
    private final ParameterMap registry;

    public ClassInstantiator(List<Creator> creators2, Creator primary2, ParameterMap registry2, Detail detail2) {
        this.creators = creators2;
        this.registry = registry2;
        this.primary = primary2;
        this.detail = detail2;
    }

    public boolean isDefault() {
        if (this.creators.size() > 1) {
            return false;
        }
        if (this.primary != null) {
            return true;
        }
        return false;
    }

    public Object getInstance() throws Exception {
        return this.primary.getInstance();
    }

    public Object getInstance(Criteria criteria) throws Exception {
        Creator creator = getCreator(criteria);
        if (creator != null) {
            return creator.getInstance(criteria);
        }
        throw new PersistenceException("Constructor not matched for %s", this.detail);
    }

    private Creator getCreator(Criteria criteria) throws Exception {
        Creator result = this.primary;
        double max = EventEnums.SampleRate_0_percent;
        for (Creator instantiator : this.creators) {
            double score = instantiator.getScore(criteria);
            if (score > max) {
                result = instantiator;
                max = score;
            }
        }
        return result;
    }

    public Parameter getParameter(String name) {
        return (Parameter) this.registry.get(name);
    }

    public List<Parameter> getParameters() {
        return this.registry.getAll();
    }

    public List<Creator> getCreators() {
        return new ArrayList(this.creators);
    }

    public String toString() {
        return String.format("creator for %s", new Object[]{this.detail});
    }
}
