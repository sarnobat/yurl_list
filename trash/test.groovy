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


GraphDatabaseService graphDb = new GraphDatabaseFactory()
		.newEmbeddedDatabase("yurl.db");

System.out.println("2");
ExecutionEngine engine = new ExecutionEngine(graphDb);

System.out.println("3");
ExecutionResult result = engine
		.execute("start n=node(*) where not(has(n.type))  return n;");

System.out.println("4");
Iterator<Node> n_column = result.columnAs("n");
String nodeResult = "";

System.out.println("5");
JSONArray jsonArray = new JSONArray();
Iterable<Node> allNodes = GlobalGraphOperations.at(graphDb).getAllNodes();

for (Node node : IteratorUtil.asIterable(n_column)) {
	// note: we're grabbing the name property from the node,
	// not from the n.name in this case.
	// nodeResult = node + ": " + node.getId();
	System.out.println(node.getId());
}
