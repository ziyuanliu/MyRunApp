package com.example.ziyuanliu.myapplication.backend.data;

/**
 * Created by ziyuanliu on 5/17/16.
 */


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

public class ExerciseEntryDataStore {

    private static final Logger mLogger = Logger
            .getLogger(ExerciseEntryDataStore.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    private static Key getKey() {
        return KeyFactory.createKey(ExerciseEntry.EXERCISE_PARENT_ENTITY_NAME,
                ExerciseEntry.EXERCISE_PARENT_KEY_NAME);
    }

    private static void createParentEntity() {
        Entity entity = new Entity(getKey());

        mDatastore.put(entity);
    }

    public static boolean add(ExerciseEntry exercise) {
        if (getContactByName(exercise.mId, null) != null) {
            mLogger.log(Level.INFO, "contact exists");
            return false;
        }

        Key parentKey = getKey();


        Entity entity = new Entity(ExerciseEntry.EXERCISE_ENTITY_NAME, exercise.mId,
                parentKey);

        entity.setProperty(ExerciseEntry.FIELD_ID, exercise.mId);
        entity.setProperty(ExerciseEntry.FIELD_INPUT_TYPE, exercise.mInputType);
        entity.setProperty(ExerciseEntry.FIELD_ACTIVITY_TYPE, exercise.mActivityType);
        entity.setProperty(ExerciseEntry.FIELD_DATE_TIME, exercise.mDateTime);
        entity.setProperty(ExerciseEntry.FIELD_DURATION, exercise.mDuration);
        entity.setProperty(ExerciseEntry.FIELD_DISTANCE, exercise.mDistance);
        entity.setProperty(ExerciseEntry.FIELD_AVG_SPEED, exercise.mAvgSpeed);
        entity.setProperty(ExerciseEntry.FIELD_CALORIE, exercise.mCalorie);
        entity.setProperty(ExerciseEntry.FIELD_CLIMB, exercise.mClimb);
        entity.setProperty(ExerciseEntry.FIELD_HEART_RATE, exercise.mHeartRate);
        entity.setProperty(ExerciseEntry.FIELD_COMMENT, exercise.mComment);
        mDatastore.put(entity);

        return true;
    }


    public static boolean delete(String name) {
        // you can also use name to get key, then use the key to delete the
        // entity from datastore directly
        // because name is also the entity's key

        // query
        Filter filter = new FilterPredicate(ExerciseEntry.FIELD_ID,
                FilterOperator.EQUAL, name);

        Query query = new Query(ExerciseEntry.EXERCISE_ENTITY_NAME);
        query.setFilter(filter);

        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = mDatastore.prepare(query);

        Entity result = pq.asSingleEntity();
        boolean ret = false;
        if (result != null) {
            // delete
            mDatastore.delete(result.getKey());
            ret = true;
        }

        return ret;
    }

    public static ArrayList<ExerciseEntry> list() {
        ArrayList<ExerciseEntry> resultList = new ArrayList<ExerciseEntry>();
        Query query = new Query(ExerciseEntry.EXERCISE_ENTITY_NAME);
        // get every record from datastore, no filter
        query.setFilter(null);
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);

        for (Entity entity : pq.asIterable()) {
            ExerciseEntry contact = getExercistEntryFromEntity(entity);
            if (contact != null) {
                resultList.add(contact);
            }
        }
        return resultList;
    }

    public static ExerciseEntry getContactByName(String name, Transaction txn) {
        Entity result = null;
        try {
            result = mDatastore.get(KeyFactory.createKey(getKey(),
                    ExerciseEntry.EXERCISE_ENTITY_NAME, name));
        } catch (Exception ex) {

        }

        return getExercistEntryFromEntity(result);
    }

    private static ExerciseEntry getExercistEntryFromEntity(Entity entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseEntry(
                (String) entity.getProperty(ExerciseEntry.FIELD_ID),
                (String) entity.getProperty(ExerciseEntry.FIELD_INPUT_TYPE),
                (String) entity.getProperty(ExerciseEntry.FIELD_ACTIVITY_TYPE),
                (String) entity.getProperty(ExerciseEntry.FIELD_DATE_TIME),
                (String) entity.getProperty(ExerciseEntry.FIELD_DURATION),
                (String) entity.getProperty(ExerciseEntry.FIELD_DISTANCE),
                (String) entity.getProperty(ExerciseEntry.FIELD_AVG_SPEED),
                (String) entity.getProperty(ExerciseEntry.FIELD_CALORIE),
                (String) entity.getProperty(ExerciseEntry.FIELD_CLIMB),
                (String) entity.getProperty(ExerciseEntry.FIELD_HEART_RATE),
                (String) entity.getProperty(ExerciseEntry.FIELD_COMMENT)
        );
    }
}
