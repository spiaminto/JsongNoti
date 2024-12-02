package io.spiaminto.jsongnoti.aop.trace;

public interface LogTrace {
    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e, Object[] params);
}
