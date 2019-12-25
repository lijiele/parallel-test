package org.ele.springboot.parallel.web;

public class ParallelContentHolder {
    private volatile String call1Content = null;
    private volatile boolean call1Flag = false;
    private volatile String call2Content = null;
    private volatile boolean call2Flag = false;

    public String getCall1Content() {
        return call1Content;
    }

    public synchronized void setCall1Content(String call1Content) {
        this.call1Content = call1Content;
    }

    public boolean isCall1Flag() {
        return call1Flag;
    }

    public synchronized void setCall1Flag(boolean call1Flag) {
        this.call1Flag = call1Flag;
    }

    public String getCall2Content() {
        return call2Content;
    }

    public synchronized void setCall2Content(String call2Content) {
        this.call2Content = call2Content;
    }

    public boolean isCall2Flag() {
        return call2Flag;
    }

    public synchronized void setCall2Flag(boolean call2Flag) {
        this.call2Flag = call2Flag;
    }
}
