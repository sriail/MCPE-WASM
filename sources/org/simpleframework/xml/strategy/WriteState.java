package org.simpleframework.xml.strategy;

import org.simpleframework.xml.util.WeakCache;

class WriteState extends WeakCache<WriteGraph> {
    private Contract contract;

    public WriteState(Contract contract2) {
        this.contract = contract2;
    }

    public WriteGraph find(Object map) {
        WriteGraph write = (WriteGraph) fetch(map);
        if (write != null) {
            return write;
        }
        WriteGraph write2 = new WriteGraph(this.contract);
        cache(map, write2);
        return write2;
    }
}
