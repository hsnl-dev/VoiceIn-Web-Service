package tw.kits.voicein.bean;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * {
 * "providerUuid": "ecsx-1030-3454-edxf", "customer":{ "name": "Henry",
 * "phoneNumber": "0975531859" } }
 *
 * @author Henry
 */

public class IconCreateBean {
    @NotNull
    private String providerUuid;
    @Valid @NotNull
    private Customer customer;

    /**
     * @return the providerUuid
     */
    public String getProviderUuid() {
        return providerUuid;
    }

    /**
     * @param providerUuid the providerUuid to set
     */
    public void setProviderUuid(String providerUuid) {
        this.providerUuid = providerUuid;
    }

    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.customer.getName();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        
        this.customer.setName(name);
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return this.customer.getPhoneNumber();
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.customer.setPhoneNumber(phoneNumber);
    }

}

class Customer {
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
