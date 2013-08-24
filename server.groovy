@Path("yurl")
public static class HelloWorldResource { // Must be public

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
