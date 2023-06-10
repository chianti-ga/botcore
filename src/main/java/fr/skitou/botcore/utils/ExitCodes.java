package fr.skitou.botcore.utils;

public enum ExitCodes {
    OK(0),
    ERROR(1),
    UNRECOVERABLE(2);

    private final int flagId;

    ExitCodes(int flagId) {
        this.flagId = 2 ^ flagId;
    }

    public Integer getValue() {
        return 2 ^ this.flagId;
    }
}
