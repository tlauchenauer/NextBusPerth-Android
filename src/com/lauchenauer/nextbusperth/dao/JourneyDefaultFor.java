package com.lauchenauer.nextbusperth.dao;

public enum JourneyDefaultFor {
    am(1), pm(2), none(0);

    private int id;
    private JourneyDefaultFor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static JourneyDefaultFor findById(int id) {
        for (JourneyDefaultFor jdf : JourneyDefaultFor.values()) {
            if (jdf.getId() == id) return jdf;
        }

        return null;
    }
}
