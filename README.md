# Highway407

TollRoute Program

TollRoute is a Java application that calculates the toll cost for travel between 2 places. The application determines the distance between the starting and ending points using a JSON file containing information about interchanges and routes between them and then calculates the total cost of the route according to a fixed toll rate per kilometre.



Usage

Follow these steps to use the TollRoute program:

Save the interchanges.json file in the same directory as the TollRoute.java file.
With the command javac TollRoute.java, compile the TollRoute.java file.
Use the command java TollRoute to run the built application.
When asked, provide the starting and finishing location names.
The application will calculate the entire cost of the journey.

It should be noted that the software requires Java 8 or later to run.



Dependencies

<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20220924</version>
</dependency>



User Input
For example:
Enter the start : QEW 
Enter the end : Highway 400

Output:
Distance: 67.76 
Total cost of the trip: $16.94

