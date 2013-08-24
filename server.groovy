import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
	public String json() throws JSONException {
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
		
		return jsonArray.toString();
	}
}

HttpServer server = JdkHttpServerFactory.createHttpServer(
		new URI("http://localhost:9099/"), new ResourceConfig(HelloWorldResource.class));
