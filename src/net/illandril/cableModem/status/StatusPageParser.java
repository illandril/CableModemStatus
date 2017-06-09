package net.illandril.cableModem.status;

import java.time.ZonedDateTime;

public abstract class StatusPageParser {
    public abstract StatusPageData parse( ZonedDateTime statusPageRequestTime, String statusPageHTML );
}
