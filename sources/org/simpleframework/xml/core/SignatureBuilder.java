package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class SignatureBuilder {
    private final Constructor factory;
    private final ParameterTable table = new ParameterTable();

    public SignatureBuilder(Constructor factory2) {
        this.factory = factory2;
    }

    public boolean isValid() {
        return this.factory.getParameterTypes().length == this.table.width();
    }

    public void insert(Parameter value, int index) {
        this.table.insert(value, index);
    }

    public List<Signature> build() throws Exception {
        return build(new ParameterTable());
    }

    private List<Signature> build(ParameterTable matrix) throws Exception {
        if (this.table.isEmpty()) {
            return create();
        }
        build(matrix, 0);
        return create(matrix);
    }

    private List<Signature> create() throws Exception {
        List<Signature> list = new ArrayList<>();
        Signature signature = new Signature(this.factory);
        if (isValid()) {
            list.add(signature);
        }
        return list;
    }

    private List<Signature> create(ParameterTable matrix) throws Exception {
        List<Signature> list = new ArrayList<>();
        int height = matrix.height();
        int width = matrix.width();
        for (int i = 0; i < height; i++) {
            Signature signature = new Signature(this.factory);
            for (int j = 0; j < width; j++) {
                Parameter parameter = matrix.get(j, i);
                String path = parameter.getPath();
                if (signature.contains(parameter.getKey())) {
                    throw new ConstructorException("Parameter '%s' is a duplicate in %s", path, this.factory);
                }
                signature.add(parameter);
            }
            list.add(signature);
        }
        return list;
    }

    private void build(ParameterTable matrix, int index) {
        build(matrix, new ParameterList(), index);
    }

    private void build(ParameterTable matrix, ParameterList signature, int index) {
        ParameterList column = this.table.get(index);
        int height = column.size();
        if (this.table.width() - 1 > index) {
            for (int i = 0; i < height; i++) {
                ParameterList extended = new ParameterList(signature);
                if (signature != null) {
                    extended.add((Parameter) column.get(i));
                    build(matrix, extended, index + 1);
                }
            }
            return;
        }
        populate(matrix, signature, index);
    }

    private void populate(ParameterTable matrix, ParameterList signature, int index) {
        ParameterList column = this.table.get(index);
        int width = signature.size();
        int height = column.size();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix.get(j).add((Parameter) signature.get(j));
            }
            matrix.get(index).add((Parameter) column.get(i));
        }
    }

    private static class ParameterTable extends ArrayList<ParameterList> {
        /* access modifiers changed from: private */
        public int height() {
            if (width() > 0) {
                return get(0).size();
            }
            return 0;
        }

        /* access modifiers changed from: private */
        public int width() {
            return size();
        }

        public void insert(Parameter value, int column) {
            ParameterList list = get(column);
            if (list != null) {
                list.add(value);
            }
        }

        public ParameterList get(int column) {
            for (int i = size(); i <= column; i++) {
                add(new ParameterList());
            }
            return (ParameterList) super.get(column);
        }

        public Parameter get(int column, int row) {
            return (Parameter) get(column).get(row);
        }
    }

    private static class ParameterList extends ArrayList<Parameter> {
        public ParameterList() {
        }

        public ParameterList(ParameterList list) {
            super(list);
        }
    }
}
