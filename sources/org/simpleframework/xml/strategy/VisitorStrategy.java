package org.simpleframework.xml.strategy;

import java.util.Map;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

public class VisitorStrategy implements Strategy {
    private final Strategy strategy;
    private final Visitor visitor;

    public VisitorStrategy(Visitor visitor2) {
        this(visitor2, new TreeStrategy());
    }

    public VisitorStrategy(Visitor visitor2, Strategy strategy2) {
        this.strategy = strategy2;
        this.visitor = visitor2;
    }

    public Value read(Type type, NodeMap<InputNode> node, Map map) throws Exception {
        if (this.visitor != null) {
            this.visitor.read(type, node);
        }
        return this.strategy.read(type, node, map);
    }

    public boolean write(Type type, Object value, NodeMap<OutputNode> node, Map map) throws Exception {
        boolean result = this.strategy.write(type, value, node, map);
        if (this.visitor != null) {
            this.visitor.write(type, node);
        }
        return result;
    }
}
