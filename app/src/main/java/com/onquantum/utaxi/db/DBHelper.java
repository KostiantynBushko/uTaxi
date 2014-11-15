package com.onquantum.utaxi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 10/15/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "uTaxi";
    private static int DATABASE_VERSION = 1;

    private static String COUNTRY_TABLE = "tdb_country";
    private static String CITY_TABLE = "tdb_city";
    private static String STREET_TYPOLOGY_TABLE = "tdb_street_typology";
    private static String STREET_TABLE = "tdb_street";

    private static String CREATE_COUNTRY_TABLE = "CREATE TABLE "+ COUNTRY_TABLE + " ("
            +"country_id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            +"country VARCHAR(45) NOT NULL,"
            +"PRIMARY KEY (country_id),"
            +"UNIQUE INDEX cid_UNIQUE (country_id ASC),"
            +"UNIQUE INDEX country_UNIQUE (country ASC))";

    private static String CREATE_CITY_TABLE = "CREATE TABLE " + CITY_TABLE + " ("
            +"city_id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            +"city VARCHAR(45) NOT NULL,"
            +"country_id INT UNSIGNED NOT NULL,"
            +"PRIMARY KEY (city_id),"
            +"UNIQUE INDEX city_id_UNIQUE (city_id ASC),"
            +"FOREIGN KEY (country_id) REFERENCES " + COUNTRY_TABLE + " (country_id))";

    private static String CREATE_STREET_TYPOLOGY_TABLE = "CREATE TABLE " + STREET_TYPOLOGY_TABLE + " ("
            +"street_typology_id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            +"typology VARCHAR(45) NOT NULL,"
            +"PRIMARY KEY (street_typology_id),"
            +"UNIQUE INDEX typology_UNIQUE (typology ASC),"
            +"UNIQUE INDEX street_typology_id_UNIQUE (street_typology_id ASC))";

    private static String CREATE_STREET_TABLE = "CREATE TABLE " + STREET_TABLE + " ("
            +"street_id INT UNSIGNED NOT NULL AUTO_INCREMENT,"
            +"street VARCHAR(45) NOT NULL,"
            +"city_id INT UNSIGNED NOT NULL,"
            +"street_typology_id INT UNSIGNED NOT NULL,"
            +"PRIMARY KEY (street_id),"
            +"INDEX tdb_city_idx (city_id ASC),"
            +"INDEX tdb_street_typology_idx (street_typology_id ASC),"
            +"UNIQUE INDEX street_id_UNIQUE (street_id ASC),"
            +"CONSTRAINT tdb_city_street"
            +" FOREIGN KEY (city_id)"
            +" REFERENCES" + CITY_TABLE + " (city_id),"
            +"CONSTRAINT tdb_street_typology"
            +" FOREIGN KEY (street_typology_id)"
            +" REFERENCES" + STREET_TYPOLOGY_TABLE + " (street_typology_id))";


    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COUNTRY_TABLE);
        db.execSQL(CREATE_CITY_TABLE);
        db.execSQL(CREATE_STREET_TYPOLOGY_TABLE);
        db.execSQL(CREATE_STREET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
