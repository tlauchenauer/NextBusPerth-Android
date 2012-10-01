package com.lauchenauer.nextbusperth;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

import java.io.File;

/**
 * Generates entities and DAOs for the example project DaoExample.
 * <p/>
 * Run it as a Java application (not Android).
 *
 * @author Markus
 */
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
        routeIndex.addProperty(routeNumber);
        routeIndex.addProperty(routeName);
        routeIndex.addProperty(headsign);
        route.addIndex(routeIndex);

        // StopTime Entity (route_id LONG, departure_time DATETIME)
        Entity stopTime = schema.addEntity("StopTime");
        Property stopTimeRouteId = route.addLongProperty("route_id").getProperty();
        Property departureTime = route.addDateProperty("departure_time").getProperty();
        Index stopTimeIndex = new Index();
        stopTimeIndex.makeUnique();
        stopTimeIndex.addProperty(departureTime);
        stopTime.addIndex(stopTimeIndex);

        // Journey Entity (id LONG, name STRING)
        Entity journey = schema.addEntity("Journey");
        journey.addIdProperty();
        journey.addStringProperty("name");

        // JourneyRoute Entity (journey_name STRING, stop_number STRING, route_number STRING, headsign STRING, selected BOOLEAN, PRIMARY KEY(journey_name, stop_number, route_number, headsign))
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
        stop.addToMany(route, stopId);
        route.addToMany(stopTime, stopTimeRouteId);
        journey.addToMany(journeyRoute, journeyId);
        journeyRoute.addToOne(journey, journeyId);
        journeyRoute.addToOne(route, journeyRouteId);

        new DaoGenerator().generateAll(schema, "./src/", "./test/");
    }

//    private static void addNote(Schema schema) {
//        Entity note = schema.addEntity("Note");
//        note.addIdProperty();
//        note.addStringProperty("text").notNull();
//        note.addStringProperty("comment");
//        note.addDateProperty("date");
//    }
//
//    private static void addCustomerOrder(Schema schema) {
//        Entity customer = schema.addEntity("Customer");
//        customer.addIdProperty();
//        customer.addStringProperty("name").notNull();
//
//        Entity order = schema.addEntity("Order");
//        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
//        order.addIdProperty();
//        Property orderDate = order.addDateProperty("date").getProperty();
//        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
//        order.addToOne(customer, customerId);
//
//        ToMany customerToOrders = customer.addToMany(order, customerId);
//        customerToOrders.setName("orders");
//        customerToOrders.orderAsc(orderDate);
//    }

}
