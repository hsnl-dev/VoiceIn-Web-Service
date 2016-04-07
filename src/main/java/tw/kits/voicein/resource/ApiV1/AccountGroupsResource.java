package tw.kits.voicein.resource.ApiV1;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.mongodb.morphia.Datastore;
import tw.kits.voicein.model.Group;
import tw.kits.voicein.model.User;
import tw.kits.voicein.util.MongoManager;
import tw.kits.voicein.util.TokenRequired;

/**
 *
 * @author Calvin
 */
@Path("/api/v1")
public class AccountGroupsResource {

    // For mongodb.
    MongoManager mongoManager = MongoManager.getInstatnce();
    Datastore dataStoreObject = mongoManager.getDs();

    // Logger.
    static final Logger LOGGER = Logger.getLogger(AccountAvatarsResource.class.getName());
    ConsoleHandler consoleHandler = new ConsoleHandler();

    /**
     *
     * @param uuid
     * @return
     */
    @GET
    @Path("/accounts/{uuid}/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response getGroupList(
            @PathParam("uuid") String uuid
    ) {
        User owner = dataStoreObject.get(User.class, uuid);
        return Response.ok().build();
    }

    /**
     *
     * @param uuid
     * @param action - expected parameters are delete or add.
     * @return
     */
    @PUT
    @Path("/accounts/{uuid}/groups/{groupUuid}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response updateContactInFGroup(
            @PathParam("uuid") String uuid,
            @QueryParam("action") String action
    ) {

        return Response.ok().build();
    }

    /**
     *
     * @param uuid
     * @param group
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createAgroup(@PathParam("uuid") String uuid, @NotNull @Valid Group group) {
        User owner = dataStoreObject.get(User.class, uuid);

        if (owner != null) {
            group.setUser(owner);
            dataStoreObject.save(group);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }

    }

    /**
     *
     * @param uuid
     * @param groupUuid
     * @return
     */
    @DELETE
    @Path("/accounts/{uuid}/groups/{groupUuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response deleteAGroup(
            @PathParam("uuid") String uuid,
            @PathParam("groupUuid") String groupUuid
    ) {
        Group groupToDelete = dataStoreObject.get(Group.class, groupUuid);
        
        if (groupToDelete != null) {
            dataStoreObject.delete(groupToDelete);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
