package highway407;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TollRoute {

	private static final String FILENAME = "interchanges.json";
	private static final double TOLL_RATE_PER_KM = 0.25; // $0.25/km

	public static void main(String[] args) {
		TollRoute tollCalculator = new TollRoute();
		try (Scanner scan = new Scanner(System.in)) {
			System.out.print("Enter the start : ");
			String startingLocationName = scan.nextLine();

			System.out.print("Enter the end : ");
			String endingLocationName = scan.nextLine();

			double cost;

			if (startingLocationName.isBlank()
					|| endingLocationName.isBlank()) {
				cost = 0.0;
				System.out.println("Invalid input!");
			} else {
				cost = tollCalculator.costToTrip(startingLocationName,
						endingLocationName);
				System.out.println("Total cost of the trip: $"
						+ String.format("%.2f", cost));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static double costToTrip(String nameToStart, String nameToEnd) {
		try {
			String jsonContent = Files.readString(Paths.get(FILENAME));
			JSONObject interchangesJson = new JSONObject(jsonContent);

			JSONObject locations = interchangesJson.getJSONObject("locations");
			int startLocationId = findLocationIdByName(nameToStart, locations);
			int endLocationId = findLocationIdByName(nameToEnd, locations);

			double distance;
			double cost = 0;

			if (endLocationId == -1) {
				distance = 0;
				cost = 0;
				System.out.println("Location does not exits!!!!");
			} else {
				distance = calculateDistance(startLocationId, endLocationId,
						locations, new HashSet<>());
				cost = distance * TOLL_RATE_PER_KM;

				System.out.println("Distance: " + distance);
			}
			return Math.round(cost * 100.0) / 100.0;
		} catch (IOException | JSONException e) {
			System.out.println("Error: " + e.getMessage());
			return 0;
		}
	}

	public static int findLocationIdByName(String name, JSONObject locations)
			throws JSONException {
		int locationId = -1;
		Iterator<String> locationIds = locations.keys();
		while (locationIds.hasNext()) {
			String locationIdString = locationIds.next();
			JSONObject location = locations.getJSONObject(locationIdString);
			if (location.getString("name").equals(name)) {
				locationId = Integer.parseInt(locationIdString);
				break;
			}
		}
		return locationId;

	}

	public static double calculateDistance(int startLocationId,
			int endLocationId, JSONObject locations, HashSet<Object> hashSet)
			throws JSONException {
		JSONObject startLocation = locations
				.getJSONObject(Integer.toString(startLocationId));
		JSONObject endLocation = locations
				.getJSONObject(Integer.toString(endLocationId));
		double distance = 0;

		// Check if there's a direct route between the two locations
		JSONArray startRoutes = startLocation.getJSONArray("routes");
		JSONArray endRoutes = endLocation.getJSONArray("routes");
		for (int i = 0; i < startRoutes.length(); i++) {
			JSONObject startRoute = startRoutes.getJSONObject(i);
			int toId = startRoute.getInt("toId");
			double startDistance = startRoute.getDouble("distance");

			for (int j = 0; j < endRoutes.length(); j++) {

				JSONObject endRoute = endRoutes.getJSONObject(j);
				if ((endRoute.getInt("toId") == 47) || toId == 47) {
					continue;
				}
				if ((endRoute.getInt("toId")) == toId) {
					double endDistance = endRoute.getDouble("distance");
					distance = startDistance + endDistance;
					break;
				}
			}
			if (distance > 0) {
				break;
			}
		}

		// If there's no direct route, calculate the distance through
		// intermediate locations
		if (distance == 0)

		{
			for (int i = 0; i < startRoutes.length(); i++) {
				JSONObject startRoute = startRoutes.getJSONObject(i);
				int intermediateLocationId = startRoute.getInt("toId");
				if (intermediateLocationId == 47) {
					continue;
				}
				double intermediateDistance = startRoute.getDouble("distance");
				if (intermediateLocationId != startLocationId
						&& !hashSet.contains(intermediateLocationId)) {
					hashSet.add(intermediateLocationId);
					double remainingDistance = calculateDistance(
							intermediateLocationId, endLocationId, locations,
							hashSet);
					if (remainingDistance > 0) {
						distance = intermediateDistance + remainingDistance;
						break;
					}
				}
			}
		}

		if (startLocation.equals(endLocation)) {
			distance = 0;
		}

		return Math.round(distance * 100.0) / 100.0;
	}
}