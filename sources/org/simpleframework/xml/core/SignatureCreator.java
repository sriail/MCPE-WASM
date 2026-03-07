package org.simpleframework.xml.core;

import com.microsoft.cll.android.EventEnums;
import java.util.List;

class SignatureCreator implements Creator {
    private final List<Parameter> list;
    private final Signature signature;
    private final Class type;

    public SignatureCreator(Signature signature2) {
        this.type = signature2.getType();
        this.list = signature2.getAll();
        this.signature = signature2;
    }

    public Class getType() {
        return this.type;
    }

    public Signature getSignature() {
        return this.signature;
    }

    public Object getInstance() throws Exception {
        return this.signature.create();
    }

    public Object getInstance(Criteria criteria) throws Exception {
        Object[] values = this.list.toArray();
        for (int i = 0; i < this.list.size(); i++) {
            values[i] = getVariable(criteria, i);
        }
        return this.signature.create(values);
    }

    private Object getVariable(Criteria criteria, int index) throws Exception {
        Variable variable = criteria.remove(this.list.get(index).getKey());
        if (variable != null) {
            return variable.getValue();
        }
        return null;
    }

    public double getScore(Criteria criteria) throws Exception {
        Signature match = this.signature.copy();
        for (Object key : criteria) {
            Parameter parameter = match.get(key);
            Variable label = criteria.get(key);
            Contact contact = label.getContact();
            if (parameter != null && !Support.isAssignable(label.getValue().getClass(), parameter.getType())) {
                return -1.0d;
            }
            if (contact.isReadOnly() && parameter == null) {
                return -1.0d;
            }
        }
        return getPercentage(criteria);
    }

    private double getPercentage(Criteria criteria) throws Exception {
        double score = EventEnums.SampleRate_0_percent;
        for (Parameter value : this.list) {
            if (criteria.get(value.getKey()) != null) {
                score += 1.0d;
            } else if (value.isRequired() || value.isPrimitive()) {
                return -1.0d;
            }
        }
        return getAdjustment(score);
    }

    private double getAdjustment(double score) {
        double adjustment = ((double) this.list.size()) / 1000.0d;
        if (score > EventEnums.SampleRate_0_percent) {
            return (score / ((double) this.list.size())) + adjustment;
        }
        return score / ((double) this.list.size());
    }

    public String toString() {
        return this.signature.toString();
    }
}
