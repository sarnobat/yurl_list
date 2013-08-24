import org.neo4j.helpers.collection.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

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
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import com.sun.net.httpserver.HttpServer;

	@Path("yurl")
	public class HelloWorldResource { // Must be public


		@GET
		@Path("keys")
		@Produces("application/json")
		public Response keys() throws JSONException {
			JSONArray jsonArray = new JSONArray() {
				{
					put(new JSONObject() {
						{
							put("categoryNode", "47");
							put("key", "B");
							put("name", "business");
						}
					});
					put(new JSONObject() {
						{
							put("categoryNode", "46");
							put("key", "T");
							put("name", "technology");
						}
					});
					put(new JSONObject() {
						{
							put("categoryNode", "45");
							put("key", ")");
							put("name", "root");
						}
					});
				}
			};
			return Response.ok().header("Access-Control-Allow-Origin", "*")
					.entity(jsonArray.toString()).type("application/json").build();
		}


		@GET
		@Path("uncategorized")
		@Produces("application/json")
		public Response json() throws JSONException {
			System.out.println("1");
			GraphDatabaseService graphDb = new GraphDatabaseFactory()
					.newEmbeddedDatabase("yurl.db");
			ExecutionEngine engine = new ExecutionEngine(graphDb);
			ExecutionResult result = engine.execute("start n=node(*) MATCH n<-[r?:link]-source where not(has(n.type)) and r is null return n;");
			Iterator<Node> n_column = result.columnAs("n");
			String nodeResult = "";
			JSONArray jsonArray = new JSONArray();
			Iterable<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes();

			for (Node node : IteratorUtil.asIterable(n_column)) {
				// note: we're grabbing the name property from the node,
				// not from the n.name in this case.
				// nodeResult = node + ": " + node.getId();
				System.out.println(node.getId());
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
			graphDb.shutdown();
			return Response.ok().header("Access-Control-Allow-Origin", "*")
					.entity(jsonArray.toString()).type("application/json").build();

		}
		@GET
		@Path("relate")
		@Produces("application/json")
		public Response relate(@QueryParam("parentID") String parentID,
				@QueryParam("childID") String childID) throws JSONException {
			System.out.println("relate() " + parentID + ", " + childID);
			System.out.println("1");
			GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("yurl.db");
			System.out.println("2");
			Transaction tx = graphDb.beginTx();
			System.out.println("3");
			graphDb.getNodeById(Long.parseLong(parentID)).createRelationshipTo(
					graphDb.getNodeById(Long.parseLong(childID)), RelTypes.CONTAINS);
			System.out.println("4");
			String outcome = "FAILURE";
			try {
				// Updating operations go here
				tx.success();
				outcome = "SUCCESS";
			} finally {
				tx.finish();
			}
			graphDb.shutdown();
			JSONObject json = new JSONObject();
			json.put("status", outcome);
			return Response.ok().header("Access-Control-Allow-Origin", "*").entity(json.toString())
					.type("application/json").build();
		}

	}

	public enum RelTypes implements RelationshipType {
		CONTAINS
	}

HttpServer server = JdkHttpServerFactory.createHttpServer(
		new URI("http://localhost:9099/"), new ResourceConfig(HelloWorldResource.class));
