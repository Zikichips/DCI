package dci.model;

public class TraceCall {
    private String caller;
    private String callee;
    private int count;

    public TraceCall(String caller, String callee, int count) {
        this.caller = caller;
        this.callee = callee;
        this.count = count;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
} 