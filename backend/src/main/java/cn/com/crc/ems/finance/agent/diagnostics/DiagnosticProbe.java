package cn.com.crc.ems.finance.agent.diagnostics;

/** A bounded, read-only dependency probe used by health and diagnostics endpoints. */
public interface DiagnosticProbe {

    String name();

    DiagnosticComponentStatus check();
}
