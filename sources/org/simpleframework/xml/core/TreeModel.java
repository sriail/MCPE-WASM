package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TreeModel implements Model {
    private LabelMap attributes;
    private Detail detail;
    private LabelMap elements;
    private Expression expression;
    private int index;
    private Label list;
    private ModelMap models;
    private String name;
    private OrderList order;
    private Policy policy;
    private String prefix;
    private Label text;

    private static class OrderList extends ArrayList<String> {
    }

    public TreeModel(Policy policy2, Detail detail2) {
        this(policy2, detail2, (String) null, (String) null, 1);
    }

    public TreeModel(Policy policy2, Detail detail2, String name2, String prefix2, int index2) {
        this.attributes = new LabelMap(policy2);
        this.elements = new LabelMap(policy2);
        this.models = new ModelMap(detail2);
        this.order = new OrderList();
        this.detail = detail2;
        this.policy = policy2;
        this.prefix = prefix2;
        this.index = index2;
        this.name = name2;
    }

    public Model lookup(Expression path) {
        Model model = lookup(path.getFirst(), path.getIndex());
        if (!path.isPath()) {
            return model;
        }
        Expression path2 = path.getPath(1, 0);
        if (model != null) {
            return model.lookup(path2);
        }
        return model;
    }

    public void registerElement(String name2) throws Exception {
        if (!this.order.contains(name2)) {
            this.order.add(name2);
        }
        this.elements.put(name2, (Object) null);
    }

    public void registerAttribute(String name2) throws Exception {
        this.attributes.put(name2, (Object) null);
    }

    public void registerText(Label label) throws Exception {
        if (this.text != null) {
            throw new TextException("Duplicate text annotation on %s", label);
        } else {
            this.text = label;
        }
    }

    public void registerAttribute(Label label) throws Exception {
        String name2 = label.getName();
        if (this.attributes.get(name2) != null) {
            throw new AttributeException("Duplicate annotation of name '%s' on %s", name2, label);
        } else {
            this.attributes.put(name2, label);
        }
    }

    public void registerElement(Label label) throws Exception {
        String name2 = label.getName();
        if (this.elements.get(name2) != null) {
            throw new ElementException("Duplicate annotation of name '%s' on %s", name2, label);
        }
        if (!this.order.contains(name2)) {
            this.order.add(name2);
        }
        if (label.isTextList()) {
            this.list = label;
        }
        this.elements.put(name2, label);
    }

    public ModelMap getModels() throws Exception {
        return this.models.getModels();
    }

    public LabelMap getAttributes() throws Exception {
        return this.attributes.getLabels();
    }

    public LabelMap getElements() throws Exception {
        return this.elements.getLabels();
    }

    public boolean isModel(String name2) {
        return this.models.containsKey(name2);
    }

    public boolean isElement(String name2) {
        return this.elements.containsKey(name2);
    }

    public boolean isAttribute(String name2) {
        return this.attributes.containsKey(name2);
    }

    public Iterator<String> iterator() {
        List<String> list2 = new ArrayList<>();
        Iterator i$ = this.order.iterator();
        while (i$.hasNext()) {
            list2.add((String) i$.next());
        }
        return list2.iterator();
    }

    public void validate(Class type) throws Exception {
        validateExpressions(type);
        validateAttributes(type);
        validateElements(type);
        validateModels(type);
        validateText(type);
    }

    private void validateText(Class type) throws Exception {
        if (this.text == null) {
            return;
        }
        if (!this.elements.isEmpty()) {
            throw new TextException("Text annotation %s used with elements in %s", this.text, type);
        } else if (isComposite()) {
            throw new TextException("Text annotation %s can not be used with paths in %s", this.text, type);
        }
    }

    private void validateExpressions(Class type) throws Exception {
        Iterator i$ = this.elements.iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null) {
                validateExpression(label);
            }
        }
        Iterator i$2 = this.attributes.iterator();
        while (i$2.hasNext()) {
            Label label2 = i$2.next();
            if (label2 != null) {
                validateExpression(label2);
            }
        }
        if (this.text != null) {
            validateExpression(this.text);
        }
    }

    private void validateExpression(Label label) throws Exception {
        Expression location = label.getExpression();
        if (this.expression != null) {
            String path = this.expression.getPath();
            String expect = location.getPath();
            if (!path.equals(expect)) {
                throw new PathException("Path '%s' does not match '%s' in %s", path, expect, this.detail);
            }
            return;
        }
        this.expression = location;
    }

    private void validateModels(Class type) throws Exception {
        Iterator<ModelList> it = this.models.iterator();
        while (it.hasNext()) {
            int count = 1;
            Iterator i$ = it.next().iterator();
            while (true) {
                if (i$.hasNext()) {
                    Model model = (Model) i$.next();
                    if (model != null) {
                        String name2 = model.getName();
                        int index2 = model.getIndex();
                        int count2 = count + 1;
                        if (index2 != count) {
                            throw new ElementException("Path section '%s[%s]' is out of sequence in %s", name2, Integer.valueOf(index2), type);
                        } else {
                            model.validate(type);
                            count = count2;
                        }
                    }
                }
            }
        }
    }

    private void validateAttributes(Class type) throws Exception {
        for (String name2 : this.attributes.keySet()) {
            if (((Label) this.attributes.get(name2)) == null) {
                throw new AttributeException("Ordered attribute '%s' does not exist in %s", name2, type);
            } else if (this.expression != null) {
                this.expression.getAttribute(name2);
            }
        }
    }

    private void validateElements(Class type) throws Exception {
        for (String name2 : this.elements.keySet()) {
            ModelList list2 = (ModelList) this.models.get(name2);
            Label label = (Label) this.elements.get(name2);
            if (list2 == null && label == null) {
                throw new ElementException("Ordered element '%s' does not exist in %s", name2, type);
            } else if (list2 != null && label != null && !list2.isEmpty()) {
                throw new ElementException("Element '%s' is also a path name in %s", name2, type);
            } else if (this.expression != null) {
                this.expression.getElement(name2);
            }
        }
    }

    public void register(Label label) throws Exception {
        if (label.isAttribute()) {
            registerAttribute(label);
        } else if (label.isText()) {
            registerText(label);
        } else {
            registerElement(label);
        }
    }

    public Model lookup(String name2, int index2) {
        return this.models.lookup(name2, index2);
    }

    public Model register(String name2, String prefix2, int index2) throws Exception {
        Model model = this.models.lookup(name2, index2);
        if (model == null) {
            return create(name2, prefix2, index2);
        }
        return model;
    }

    private Model create(String name2, String prefix2, int index2) throws Exception {
        Model model = new TreeModel(this.policy, this.detail, name2, prefix2, index2);
        if (name2 != null) {
            this.models.register(name2, model);
            this.order.add(name2);
        }
        return model;
    }

    public boolean isComposite() {
        Iterator<ModelList> it = this.models.iterator();
        while (it.hasNext()) {
            Iterator i$ = it.next().iterator();
            while (true) {
                if (i$.hasNext()) {
                    Model model = (Model) i$.next();
                    if (model != null && !model.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        if (this.models.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        if (this.text == null && this.elements.isEmpty() && this.attributes.isEmpty() && !isComposite()) {
            return true;
        }
        return false;
    }

    public Label getText() {
        if (this.list != null) {
            return this.list;
        }
        return this.text;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return String.format("model '%s[%s]'", new Object[]{this.name, Integer.valueOf(this.index)});
    }
}
