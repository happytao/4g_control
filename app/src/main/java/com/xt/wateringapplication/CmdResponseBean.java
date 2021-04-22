package com.xt.wateringapplication;

/**
 * @author:DIY
 * @date: 2021/4/17
 */
public class CmdResponseBean {

    /**
     * errno : 0
     * error : succ
     * data : {"cmd_uuid":"81572aae-fc34-5deb-8f06-ab45d73cb12b"}
     */

    private Integer errno;
    private String error;
    private DataDTO data;

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        /**
         * cmd_uuid : 81572aae-fc34-5deb-8f06-ab45d73cb12b
         */

        private String cmd_uuid;

        public String getCmd_uuid() {
            return cmd_uuid;
        }

        public void setCmd_uuid(String cmd_uuid) {
            this.cmd_uuid = cmd_uuid;
        }
    }
}
