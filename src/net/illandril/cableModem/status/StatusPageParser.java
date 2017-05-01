package net.illandril.cableModem.status;

import java.time.LocalDateTime;

public abstract class StatusPageParser {
    public abstract StatusPageData parse( LocalDateTime statusPageRequestTime, String statusPageHTML );
}
