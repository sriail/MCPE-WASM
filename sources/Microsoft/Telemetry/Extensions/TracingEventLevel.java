package Microsoft.Telemetry.Extensions;

public enum TracingEventLevel {
    None(0),
    Critical(1),
    Error(2),
    Informational(3),
    LogAlways(4),
    Verbose(5),
    Warning(6),
    __INVALID_ENUM_VALUE(7);
    
    private final int value;

    private TracingEventLevel(int value2) {
        this.value = value2;
    }

    public int getValue() {
        return this.value;
    }

    public static TracingEventLevel fromValue(int value2) {
        switch (value2) {
            case 0:
                return None;
            case 1:
                return Critical;
            case 2:
                return Error;
            case 3:
                return Informational;
            case 4:
                return LogAlways;
            case 5:
                return Verbose;
            case 6:
                return Warning;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }
}
