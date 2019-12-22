package org.ele.springboot.parallel.web;

public class ParallelContentHolder {
    private String call1Content = null;
    private boolean call1Flag = false;
    private String call2Content = null;
    private boolean call2Flag = false;

    public String getCall1Content() {
        return call1Content;
    }

    public void setCall1Content(String call1Content) {
        this.call1Content = call1Content;
    }

    public boolean isCall1Flag() {
        return call1Flag;
    }

    public void setCall1Flag(boolean call1Flag) {
        this.call1Flag = call1Flag;
    }

    public String getCall2Content() {
        return call2Content;
    }

    public void setCall2Content(String call2Content) {
        this.call2Content = call2Content;
    }

    public boolean isCall2Flag() {
        return call2Flag;
    }

    public void setCall2Flag(boolean call2Flag) {
        this.call2Flag = call2Flag;
    }
}
