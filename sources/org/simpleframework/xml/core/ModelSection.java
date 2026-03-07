package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ModelSection implements Section {
    private LabelMap attributes;
    private LabelMap elements;
    private Model model;
    private ModelMap models;

    public ModelSection(Model model2) {
        this.model = model2;
    }

    public String getName() {
        return this.model.getName();
    }

    public String getPrefix() {
        return this.model.getPrefix();
    }

    public String getPath(String name) throws Exception {
        Expression path = this.model.getExpression();
        return path == null ? name : path.getElement(name);
    }

    public String getAttribute(String name) throws Exception {
        Expression path = this.model.getExpression();
        return path == null ? name : path.getAttribute(name);
    }

    public Iterator<String> iterator() {
        List<String> list = new ArrayList<>();
        for (String element : this.model) {
            list.add(element);
        }
        return list.iterator();
    }

    public boolean isSection(String name) throws Exception {
        return getModels().get(name) != null;
    }

    public ModelMap getModels() throws Exception {
        if (this.models == null) {
            this.models = this.model.getModels();
        }
        return this.models;
    }

    public Label getText() throws Exception {
        return this.model.getText();
    }

    public LabelMap getAttributes() throws Exception {
        if (this.attributes == null) {
            this.attributes = this.model.getAttributes();
        }
        return this.attributes;
    }

    public LabelMap getElements() throws Exception {
        if (this.elements == null) {
            this.elements = this.model.getElements();
        }
        return this.elements;
    }

    public Label getElement(String name) throws Exception {
        return getElements().getLabel(name);
    }

    public Section getSection(String name) throws Exception {
        Model model2;
        ModelList list = (ModelList) getModels().get(name);
        if (list == null || (model2 = list.take()) == null) {
            return null;
        }
        return new ModelSection(model2);
    }
}
