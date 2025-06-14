package com.SimpleMQ.Monitor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class GrafanaContent{
    private String TIMESTAMP;
    private String APP_CODE;
    private String SERVICE_ID;
    private String INTERFACE_MAP;
    private String FUNCTIONAL_MAP;
    private String REQUEST_TYPE;
    private String MSG_UID;
    private String CLIENT_IP;
    private String STATUS_CODE;
    private String ERROR_TYPE;
    private String RESPONSE_TIME;
    private String Unique_Id;
    private String LOG_DETAIL;

    public GrafanaContent(String TIMESTAMP, String APP_CODE,
                          String SERVICE_ID, String INTERFACE_MAP,
                          String FUNCTIONAL_MAP, String REQUEST_TYPE,
                          String MSG_UID, String CLIENT_IP, String STATUS_CODE,
                          String ERROR_TYPE, String RESPONSE_TIME,
                          String unique_Id, String LOG_DETAIL) {
        this.TIMESTAMP = TIMESTAMP;
        this.APP_CODE = APP_CODE;
        this.SERVICE_ID = SERVICE_ID;
        this.INTERFACE_MAP = INTERFACE_MAP;
        this.FUNCTIONAL_MAP = FUNCTIONAL_MAP;
        this.REQUEST_TYPE = REQUEST_TYPE;
        this.MSG_UID = MSG_UID;
        this.CLIENT_IP = CLIENT_IP;
        this.STATUS_CODE = STATUS_CODE;
        this.ERROR_TYPE = ERROR_TYPE;
        this.RESPONSE_TIME = RESPONSE_TIME;
        Unique_Id = unique_Id;
        this.LOG_DETAIL = LOG_DETAIL;
    }

    public String ToString()
    {
        return String.join("|",TIMESTAMP,APP_CODE,
                SERVICE_ID,INTERFACE_MAP,FUNCTIONAL_MAP,REQUEST_TYPE,
                MSG_UID,CLIENT_IP,STATUS_CODE,ERROR_TYPE,ERROR_TYPE,
                RESPONSE_TIME,Unique_Id,LOG_DETAIL
        );
    }


}
