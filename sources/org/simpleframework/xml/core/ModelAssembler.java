package org.simpleframework.xml.core;

import org.simpleframework.xml.Order;
import org.simpleframework.xml.stream.Format;

class ModelAssembler {
    private final ExpressionBuilder builder;
    private final Detail detail;
    private final Format format;

    public ModelAssembler(ExpressionBuilder builder2, Detail detail2, Support support) throws Exception {
        this.format = support.getFormat();
        this.builder = builder2;
        this.detail = detail2;
    }

    public void assemble(Model model, Order order) throws Exception {
        assembleElements(model, order);
        assembleAttributes(model, order);
    }

    private void assembleElements(Model model, Order order) throws Exception {
        String[] arr$ = order.elements();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Expression path = this.builder.build(arr$[i$]);
            if (path.isAttribute()) {
                throw new PathException("Ordered element '%s' references an attribute in %s", path, this.detail);
            } else {
                registerElements(model, path);
                i$++;
            }
        }
    }

    private void assembleAttributes(Model model, Order order) throws Exception {
        String[] arr$ = order.attributes();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String value = arr$[i$];
            Expression path = this.builder.build(value);
            if (path.isAttribute() || !path.isPath()) {
                if (!path.isPath()) {
                    model.registerAttribute(this.format.getStyle().getAttribute(value));
                } else {
                    registerAttributes(model, path);
                }
                i$++;
            } else {
                throw new PathException("Ordered attribute '%s' references an element in %s", path, this.detail);
            }
        }
    }

    private void registerAttributes(Model model, Expression path) throws Exception {
        String prefix = path.getPrefix();
        String name = path.getFirst();
        int index = path.getIndex();
        if (path.isPath()) {
            Model next = model.register(name, prefix, index);
            Expression child = path.getPath(1);
            if (next == null) {
                throw new PathException("Element '%s' does not exist in %s", name, this.detail);
            } else {
                registerAttributes(next, child);
            }
        } else {
            registerAttribute(model, path);
        }
    }

    private void registerAttribute(Model model, Expression path) throws Exception {
        String name = path.getFirst();
        if (name != null) {
            model.registerAttribute(name);
        }
    }

    private void registerElements(Model model, Expression path) throws Exception {
        String prefix = path.getPrefix();
        String name = path.getFirst();
        int index = path.getIndex();
        if (name != null) {
            Model next = model.register(name, prefix, index);
            Expression child = path.getPath(1);
            if (path.isPath()) {
                registerElements(next, child);
            }
        }
        registerElement(model, path);
    }

    private void registerElement(Model model, Expression path) throws Exception {
        String prefix = path.getPrefix();
        String name = path.getFirst();
        int index = path.getIndex();
        if (index <= 1 || model.lookup(name, index - 1) != null) {
            model.register(name, prefix, index);
        } else {
            throw new PathException("Ordered element '%s' in path '%s' is out of sequence for %s", name, path, this.detail);
        }
    }
}
