import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import com.sun.net.httpserver.HttpServer;

@Path("yurl")
public class HelloWorldResource { // Must be public

	@GET
	@Path("uncategorized")
	@Produces("application/json")
	public Response json() throws JSONException {
		GraphDatabaseService graphDb = new GraphDatabaseFactory()
				.newEmbeddedDatabase("yurl.db");

		JSONArray jsonArray = new JSONArray();
		Iterable<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes();
		for (final Node node : allNodes) {
			long id = node.getId();
			String title = "";
			if (node.hasProperty("title")) {
				title = (String) node.getProperty("title");
			}
			String url = "";
			if (node.hasProperty("url")) {
				url = (String) node.getProperty("url");
			}
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("url", url);
			json.put("title", title);
			jsonArray.put(json);
			System.out.println("\"" + title + "\",\"" + url + "\"");
			System.out.println(url);
			System.out.println();
		}

		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.entity(jsonArray.toString()).type("application/json").build();

	}
	
	@GET
	@Path("keys")
	@Produces("application/json")
	public Response keys() throws JSONException {
			JSONArray jsonArray = new JSONArray() {
			{
				put(new JSONObject() {
					{
						put("categoryNode", "87654321");
						put("key", "B");
						put("keyCombo", new JSONArray() {
							{
								put("Shift");
								put("b");
							}
						});
					}
				});
				put(new JSONObject() {
					{
						put("categoryNode", "12345678");
						put("key", "T");
						put("keyCombo", new JSONArray() {
							{
								put("Shift");
								put("t");
							}
						});
					}
				});
			}
		};
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.entity(jsonArray.toString()).type("application/json").build();
	}
	
	
	@GET
	@Path("relate")
	@Produces("application/json")
	public Response relate(@QueryParam("parentID") String parentID, @QueryParam("childID") String childID) throws JSONException {
		System.out.println("relate()");
		println("relate() " + parentID + ", " + childID);
				JSONObject json = new JSONObject() {
					{
						put("status", "SUCCESS");
					}
				};
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.entity(json.toString()).type("application/json").build();
	}
}

HttpServer server = JdkHttpServerFactory.createHttpServer(
		new URI("http://localhost:9099/"), new ResourceConfig(HelloWorldResource.class));
