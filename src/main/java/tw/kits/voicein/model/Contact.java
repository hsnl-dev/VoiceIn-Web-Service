package tw.kits.voicein.model;

import org.mongodb.morphia.annotations.*;
/**
 *
 * @author Calvin
 */
@Entity("contacts")
public class Contact {
   //Field
   @Id
   private String parentUuid;
   private String phoneNumber;
   
   public Contact() {
       
   } 
}
