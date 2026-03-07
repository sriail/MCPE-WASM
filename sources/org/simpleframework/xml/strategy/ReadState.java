package org.simpleframework.xml.strategy;

import org.simpleframework.xml.util.WeakCache;

class ReadState extends WeakCache<ReadGraph> {
    private final Contract contract;
    private final Loader loader = new Loader();

    public ReadState(Contract contract2) {
        this.contract = contract2;
    }

    public ReadGraph find(Object map) throws Exception {
        ReadGraph read = (ReadGraph) fetch(map);
        return read != null ? read : create(map);
    }

    private ReadGraph create(Object map) throws Exception {
        ReadGraph read = (ReadGraph) fetch(map);
        if (read != null) {
            return read;
        }
        ReadGraph read2 = new ReadGraph(this.contract, this.loader);
        cache(map, read2);
        return read2;
    }
}
