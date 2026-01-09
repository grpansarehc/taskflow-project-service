package com.taskflow.project_service.enums;

public enum DefaultWorkflowStatus {

    TODO("TODO", "To Do", 1, false),
    IN_PROGRESS("IN_PROGRESS", "In Progress", 2, false),
    DONE("DONE", "Done", 3, true);

    private final String code;
    private final String name;
    private final int order;
    private final boolean isFinal;

    DefaultWorkflowStatus(String code, String name, int order, boolean isFinal) {
        this.code = code;
        this.name = name;
        this.order = order;
        this.isFinal = isFinal;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getOrder() { return order; }
    public boolean isFinal() { return isFinal; }
}
