/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.bean;

import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;

/**
 *
 * @author Henry
 */
public class AccountDialBean {
    @NotNull
    private ObjectId contactId;

    /**
     * @return the contactId
     */
    public ObjectId getContactId() {
        return contactId;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(ObjectId contactId) {
        this.contactId = contactId;
    }
}
