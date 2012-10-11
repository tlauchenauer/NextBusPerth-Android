package com.lauchenauer.nextbusperth;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class NextBusDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.lauchenauer.nextbusperth.dao");

        schema.enableKeepSectionsByDefault();

        // Stop Entity (id LONG PRIMARY KEY, stop_number STRING, stop_name STRING)
        Entity stop = schema.addEntity("Stop");
        stop.addIdProperty();
        Property stopNumber = stop.addStringProperty("number").getProperty();
        stop.addStringProperty("name");
        Index stopIndex = new Index();
        stopIndex.makeUnique();
        stopIndex.addProperty(stopNumber);
        stop.addIndex(stopIndex);

        // Route Entity (id LONG PRIMARY KEY, stop_id LONG, route_number STRING, route_name STRING, headsign STRING)
        Entity route = schema.addEntity("Route");
        route.addIdProperty();
        Property stopId = route.addLongProperty("stop_id").getProperty();
        Property routeNumber = route.addStringProperty("number").getProperty();
        Property routeName = route.addStringProperty("name").getProperty();
        Property headsign = route.addStringProperty("headsign").getProperty();
        Index routeIndex = new Index();
        routeIndex.makeUnique();
        routeIndex.addProperty(stopId);
        routeIndex.addProperty(routeNumber);
        routeIndex.addProperty(routeName);
        routeIndex.addProperty(headsign);
        route.addIndex(routeIndex);

        // StopTime Entity (id LONG, route_id LONG, departure_time DATETIME)
        Entity stopTime = schema.addEntity("StopTime");
        stopTime.addIdProperty();
        Property stopTimeRouteId = stopTime.addLongProperty("route_id").getProperty();
        Property departureTime = stopTime.addDateProperty("departure_time").getProperty();
        Index stopTimeIndex = new Index();
        stopTimeIndex.makeUnique();
        stopTimeIndex.addProperty(departureTime);
        stopTimeIndex.addProperty(stopTimeRouteId);
        stopTime.addIndex(stopTimeIndex);

        // Journey Entity (id LONG, name STRING)
        Entity journey = schema.addEntity("Journey");
        journey.addIdProperty();
        journey.addStringProperty("name");
        journey.addStringProperty("stop_number");
        journey.addStringProperty("stop_name");
        journey.addIntProperty("stop_lat");
        journey.addIntProperty("stop_lon");
        journey.addIntProperty("default_for");
        journey.addIntProperty("position");

        // JourneyRoute Entity (id LONG, journey_id LONG, route_id LONG, selected BOOLEAN)
        Entity journeyRoute = schema.addEntity("JourneyRoute");
        journeyRoute.addIdProperty();
        Property journeyId = journeyRoute.addLongProperty("journey_id").getProperty();
        Property journeyRouteId = journeyRoute.addLongProperty("route_id").getProperty();
        journeyRoute.addBooleanProperty("selected");
        Index journeyRouteIndex = new Index();
        journeyRouteIndex.makeUnique();
        journeyRouteIndex.addProperty(journeyId);
        journeyRouteIndex.addProperty(journeyRouteId);
        journeyRoute.addIndex(journeyRouteIndex);

        // Relationships
        route.addToOne(stop, stopId);
        ToMany stopToRoute = stop.addToMany(route, stopId);
        stopToRoute.orderAsc(routeNumber);
        route.addToMany(stopTime, stopTimeRouteId);
        stopTime.addToOne(route, stopTimeRouteId);
        journey.addToMany(journeyRoute, journeyId);
        journeyRoute.addToOne(journey, journeyId);
        journeyRoute.addToOne(route, journeyRouteId);

        new DaoGenerator().generateAll(schema, "./src/", "./test/");
    }
}
