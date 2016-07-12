package tw.kits.voicein.bean;

import com.google.i18n.phonenumbers.NumberParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import tw.kits.voicein.model.QRcode;
import tw.kits.voicein.util.Helpers;

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
    @Valid
    @NotNull
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

    public class Customer {

        private String name;
        @NotNull
        private String phoneNumber;
        private String location;
        private String company;
        private String availableStartTime;
        private String availableEndTime;
        private Boolean isEnable;
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
             try {
                return Helpers.transferRawPhoneNumberToNationalFormat(phoneNumber, "");
            } catch (NumberParseException ex) {
                Logger.getLogger(QRcode.class.getName()).log(Level.SEVERE, null, ex);
                return phoneNumber;
            }
        }

        /**
         * @param phoneNumber the phoneNumber to set
         */
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        /**
         * @return the location
         */
        public String getLocation() {
            return location;
        }

        /**
         * @param location the location to set
         */
        public void setLocation(String location) {
            this.location = location;
        }

        /**
         * @return the company
         */
        public String getCompany() {
            return company;
        }

        /**
         * @param company the company to set
         */
        public void setCompany(String company) {
            this.company = company;
        }

        /**
         * @return the availableStartTime
         */
        public String getAvailableStartTime() {
            return availableStartTime;
        }

        /**
         * @param availableStartTime the availableStartTime to set
         */
        public void setAvailableStartTime(String availableStartTime) {
            this.availableStartTime = availableStartTime;
        }

        /**
         * @return the availableEndTime
         */
        public String getAvailableEndTime() {
            return availableEndTime;
        }

        /**
         * @param availableEndTime the availableEndTime to set
         */
        public void setAvailableEndTime(String availableEndTime) {
            this.availableEndTime = availableEndTime;
        }

        /**
         * @return the isEnable
         */
        public Boolean getIsEnable() {
            return isEnable;
        }

        /**
         * @param isEnable the isEnable to set
         */
        public void setIsEnable(Boolean isEnable) {
            this.isEnable = isEnable;
        }

     
    }

}
