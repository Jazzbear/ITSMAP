package com.leafcastlelabs.demoui.model;

//this is our data objects for holding information about demos
//it is used by the DemoAdaptor when drawing elements for the listview
public class Demo {

    private String name;
    private String description;
    private String intentAction;    //intent action (for starting through implicit intent)
    private int resultCode;

    public Demo(String demoName, String demodescription){

       this(demoName, demodescription, null, 0);
    }

    public Demo(String demoName, String demodescription, String demoAction, int demoResultCode){

        this.name = demoName;
        this.description = demodescription;
        this.intentAction = demoAction;
        this.resultCode = demoResultCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public void setIntentAction(String intentAction) {
        this.intentAction = intentAction;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
