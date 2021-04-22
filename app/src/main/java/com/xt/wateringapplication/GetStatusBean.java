package com.xt.wateringapplication;

import java.util.List;

/**
 * @author:DIY
 * @date: 2021/4/16
 */


public class GetStatusBean {


    /**
     * errno : 0
     * data : [{"create_time":"2021-04-15 13:52:56","update_at":"2021-04-16 10:42:27","id":"imei","uuid":"4e9210a2-c625-41a6-8d75-80288685bdf0","current_value":"862167057408407"},{"create_time":"2021-04-15 13:52:56","update_at":"2021-04-16 10:42:27","id":"cimi","uuid":"8cfe958e-37dd-4700-9ee7-7471a482de55","current_value":"460048460800688"},{"create_time":"2021-04-15 13:52:57","update_at":"2021-04-16 10:42:28","id":"iccid","uuid":"e333d41b-0d00-49b8-9960-4d601ea4f7e8","current_value":"89860464191970890688"},{"create_time":"2021-04-15 13:52:57","update_at":"2021-04-16 10:42:28","id":"longitude","uuid":"12ee3125-fd01-413e-9493-936025179658","current_value":103.819},{"create_time":"2021-04-15 13:52:58","update_at":"2021-04-16 10:42:28","id":"latitude","uuid":"68c510ba-405c-4f10-9b31-e8efa01e7ea5","current_value":25.530191},{"create_time":"2021-04-15 13:52:58","update_at":"2021-04-16 13:51:12","id":"csq","uuid":"a1c85136-75b5-4597-9353-25866c0e2994","current_value":22},{"create_time":"2021-04-15 13:52:59","update_at":"2021-04-16 13:22:37","id":"relay","uuid":"089265bf-cf93-4d36-9c25-e90018c67439","current_value":{"0":0}},{"create_time":"2021-04-15 13:52:59","update_at":"2021-04-16 10:42:29","id":"DIs","uuid":"646f0bbf-4599-4416-a799-49ff084c20a9","current_value":{"0":1}}]
     * error : succ
     */

    private Integer errno;
    private List<DataDTO> data;
    private String error;

    public Integer getErrno() {
        return errno;
    }

    public void setErrno(Integer errno) {
        this.errno = errno;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class DataDTO {
        /**
         * create_time : 2021-04-15 13:52:56
         * update_at : 2021-04-16 10:42:27
         * id : imei
         * uuid : 4e9210a2-c625-41a6-8d75-80288685bdf0
         * current_value : 862167057408407
         */

        private String create_time;
        private String update_at;
        private String id;
        private String uuid;
        private Object current_value;

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_at() {
            return update_at;
        }

        public void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public Object getCurrent_value() {
            return current_value;
        }

        public void setCurrent_value(Object current_value) {
            this.current_value = current_value;
        }
    }
}