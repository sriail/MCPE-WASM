package org.simpleframework.xml.core;

import org.simpleframework.xml.Version;

class Structure {
    private final Instantiator factory;
    private final Model model;
    private final boolean primitive;
    private final Label text;
    private final Label version;

    public Structure(Instantiator factory2, Model model2, Label version2, Label text2, boolean primitive2) {
        this.primitive = primitive2;
        this.factory = factory2;
        this.version = version2;
        this.model = model2;
        this.text = text2;
    }

    public Instantiator getInstantiator() {
        return this.factory;
    }

    public Section getSection() {
        return new ModelSection(this.model);
    }

    public boolean isPrimitive() {
        return this.primitive;
    }

    public Version getRevision() {
        if (this.version != null) {
            return (Version) this.version.getContact().getAnnotation(Version.class);
        }
        return null;
    }

    public Label getVersion() {
        return this.version;
    }

    public Label getText() {
        return this.text;
    }
}
