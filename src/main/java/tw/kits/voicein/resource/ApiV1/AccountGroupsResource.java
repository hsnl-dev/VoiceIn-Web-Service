package tw.kits.voicein.resource.ApiV1;

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
import tw.kits.voicein.util.TokenRequired;

/**
 *
 * @author Calvin
 */
@Path("/api/v2")
public class AccountGroupsResource {

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
      
      return Response.ok().build();
    };
    
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
    };
    
    /**
     *
     * @param uuid
     * @return
     */
    @POST
    @Path("/accounts/{uuid}/groups")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TokenRequired
    public Response createAgroup(
        @PathParam("uuid") String uuid
        ) {
      
      return Response.ok().build();
    };
    
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
      
      return Response.ok().build();
    };
}
