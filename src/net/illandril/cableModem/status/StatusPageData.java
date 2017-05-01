package net.illandril.cableModem.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StatusPageData {
    private final List<UpstreamData> upstreamData = new ArrayList<>();
    private final List<DownstreamData> downstreamData = new ArrayList<>();
    
    StatusPageData() {}
    
    void add( UpstreamData upstreamDatum ) {
        upstreamData.add( upstreamDatum );
    }
    
    void add( DownstreamData downstreamDatum ) {
        downstreamData.add( downstreamDatum );
    }
    
    public List<UpstreamData> getUpstreamData() {
        return Collections.unmodifiableList( upstreamData );
    }
    
    public List<DownstreamData> getDownstreamData() {
        return Collections.unmodifiableList( downstreamData );
    }
}
