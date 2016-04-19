/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

/**
 *
 * @author Henry
 */
public class GcmPayloadBean {
    private String to;
    private Message data;
    public GcmPayloadBean(){
        data = new Message();
    }
    /**
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return the data
     */
    public Message getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Message data) {
        this.data = data;
    }

    public class Message{
        private String message;
        public Message(){}
        /**
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
