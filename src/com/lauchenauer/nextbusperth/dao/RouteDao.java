package com.lauchenauer.nextbusperth.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.SqlUtils;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.lauchenauer.nextbusperth.dao.Route;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ROUTE.
*/
public class RouteDao extends AbstractDao<Route, Long> {

    public static final String TABLENAME = "ROUTE";

    /**
     * Properties of entity Route.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Stop_id = new Property(1, Long.class, "stop_id", false, "STOP_ID");
        public final static Property Number = new Property(2, String.class, "number", false, "NUMBER");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Headsign = new Property(4, String.class, "headsign", false, "HEADSIGN");
    };

    private DaoSession daoSession;

    private Query<Route> stop_RouteListQuery;

    public RouteDao(DaoConfig config) {
        super(config);
    }
    
    public RouteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ROUTE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'STOP_ID' INTEGER," + // 1: stop_id
                "'NUMBER' TEXT," + // 2: number
                "'NAME' TEXT," + // 3: name
                "'HEADSIGN' TEXT);"); // 4: headsign
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_ROUTE_STOP_ID_NUMBER_NAME_HEADSIGN ON ROUTE" +
                " (STOP_ID,NUMBER,NAME,HEADSIGN);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ROUTE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Route entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long stop_id = entity.getStop_id();
        if (stop_id != null) {
            stmt.bindLong(2, stop_id);
        }
 
        String number = entity.getNumber();
        if (number != null) {
            stmt.bindString(3, number);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String headsign = entity.getHeadsign();
        if (headsign != null) {
            stmt.bindString(5, headsign);
        }
    }

    @Override
    protected void attachEntity(Route entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Route readEntity(Cursor cursor, int offset) {
        Route entity = new Route( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // stop_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // number
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // headsign
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Route entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStop_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setNumber(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHeadsign(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Route entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Route entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "routeList" to-many relationship of Stop. */
    public synchronized List<Route> _queryStop_RouteList(Long stop_id) {
        if (stop_RouteListQuery == null) {
            QueryBuilder<Route> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Stop_id.eq(stop_id));
            queryBuilder.orderRaw("NUMBER ASC");
            stop_RouteListQuery = queryBuilder.build();
        } else {
            stop_RouteListQuery.setParameter(0, stop_id);
        }
        return stop_RouteListQuery.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getStopDao().getAllColumns());
            builder.append(" FROM ROUTE T");
            builder.append(" LEFT JOIN STOP T0 ON T.'STOP_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Route loadCurrentDeep(Cursor cursor, boolean lock) {
        Route entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Stop stop = loadCurrentOther(daoSession.getStopDao(), cursor, offset);
        entity.setStop(stop);

        return entity;    
    }

    public Route loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Route> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Route> list = new ArrayList<Route>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Route> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Route> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
