package code.inventory;

import java.sql.*;
import java.io.File;

public class Database {
    private Connection connection;
    private final static String initializationStatement = """
            CREATE TABLE IF NOT EXISTS Users (
                username TEXT PRIMARY KEY,
                password TEXT,
                nickname TEXT,
                email TEXT,
                securityQuestion TEXT,
                securityAnswer TEXT
            );

            CREATE TABLE IF NOT EXISTS Players (
                username TEXT PRIMARY KEY,
                level INTEGER,
                xp INTEGER,
                hp INTEGER,
                coins INTEGER
            );

            CREATE TABLE IF NOT EXISTS Cards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                type TEXT,
                character TEXT,
                accuracy INTEGER,
                damagePerSector INTEGER,
                duration INTEGER,
                level INTEGER,
                upgradeCost INTEGER,
                cost INTEGER
            );

            CREATE TABLE IF NOT EXISTS PlayerCards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                card_id INTEGER,
                level INTEGER
            );

            CREATE TABLE IF NOT EXISTS MatchInfos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT,
                result TEXT,
                hostName TEXT,
                rivalName TEXT,
                rivalLevel TEXT,
                aftermath TEXT
            );
            """;
    private static final String dummyDatabaseFiller = """
            -- Insert dummy data into the Users table
            INSERT INTO Users (username, password, nickname, email, securityQuestion, securityAnswer) VALUES
            ('user1', 'pass1', 'nickname1', 'user1@example.com', 'What is your father''s name?', 'Blue'),
            ('user2', 'pass2', 'nickname2', 'user2@example.com', 'What is your father''s name?', 'Fluffy'),
            ('user3', 'pass3', 'nickname3', 'user3@example.com', 'What is your father''s name?', 'Smith'),
            ('user4', 'pass4', 'nickname4', 'user4@example.com', 'What is your favourite color?', 'Greenwood'),
            ('user5', 'pass5', 'nickname5', 'user5@example.com', 'What is your favourite color?', 'Toyota');

            -- Insert dummy data into the Players table
            INSERT INTO Players (username, level, xp, hp, coins) VALUES
            ('user1', 10, 1500, 100, 200),
            ('user2', 5, 700, 80, 50),
            ('user3', 20, 3000, 150, 500),
            ('user4', 15, 2500, 120, 300),
            ('user5', 8, 1100, 90, 150);

            -- Insert dummy data into the Cards table
            INSERT INTO Cards (name, type, character, accuracy, damagePerSector, duration, level, upgradeCost, cost) VALUES
            ('shield', 'spell', 'all', -1, 0, 2, 0, -1, 100),
            ('heal', 'spell', 'all', -1, 0, 0, 0, -1, 100),
            ('powerUp', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('holeReplace', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('holeAmmend', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('reduceRound', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('steal', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('rivalCardDowngrade', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('copyCard', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('hideRivalCards', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('reduceNextCardAcc', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('queenCards', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('nullRival', 'spell', 'all', 0, 0, 0, 0, -1, 100),
            ('rangerShot', 'ordinary', 'ranger', 24, 8, 3, 0, 50, 100),
            ('rangerTrap', 'ordinary', 'ranger', 19, 7, 2, 0, 50, 100),
            ('rangerknife', 'ordinary', 'ranger', 26, 9, 4, 0, 50, 100),
            ('rangerMultishot', 'ordinary', 'ranger', 22, 8, 1, 0, 50, 100),
            ('warriorSlash', 'ordinary', 'warrior', 28, 9, 5, 0, 50, 100),
            ('warriorCharge', 'ordinary', 'warrior', 20, 7, 2, 0, 50, 100),
            ('warriorRevenge', 'ordinary', 'warrior', 25, 8, 3, 0, 50, 100),
            ('warriorSpear', 'ordinary', 'warrior', 27, 9, 4, 0, 50, 100),
            ('sorcererFireball', 'ordinary', 'sorcerer', 23, 8, 1, 0, 50, 100),
            ('sorcererFrost', 'ordinary', 'sorcerer', 18, 7, 5, 0, 50, 100),
            ('sorcererEye', 'ordinary', 'sorcerer', 21, 9, 2, 0, 50, 100),
            ('sorcererBlaze', 'ordinary', 'sorcerer', 17, 8, 3, 0, 50, 100),
            ('rogueStab', 'ordinary', 'rogue', 26, 7, 4, 0, 50, 100),
            ('rogueHardness', 'ordinary', 'rogue', 24, 9, 1, 0, 50, 100),
            ('rogueStrike', 'ordinary', 'rogue', 22, 8, 5, 0, 50, 100),
            ('rogueKnock', 'ordinary', 'rogue', 25, 7, 2, 0, 50, 100);

            -- Insert dummy data into the PlayerCards table
            INSERT INTO PlayerCards (username, card_id, level) VALUES
            ('user1', (SELECT id FROM Cards WHERE name = 'shield'), 1),
            ('user1', (SELECT id FROM Cards WHERE name = 'rangerShot'), 2),
            ('user1', (SELECT id FROM Cards WHERE name = 'heal'), 1),
            ('user1', (SELECT id FROM Cards WHERE name = 'warriorSlash'), 3),
            ('user1', (SELECT id FROM Cards WHERE name = 'powerUp'), 1),
            ('user1', (SELECT id FROM Cards WHERE name = 'holeReplace'), 2),
            ('user1', (SELECT id FROM Cards WHERE name = 'sorcererFireball'), 1),
            ('user1', (SELECT id FROM Cards WHERE name = 'steal'), 2),
            ('user1', (SELECT id FROM Cards WHERE name = 'rogueStab'), 1),
            ('user1', (SELECT id FROM Cards WHERE name = 'hideRivalCards'), 1),
            ('user2', (SELECT id FROM Cards WHERE name = 'rivalCardDowngrade'), 2),
            ('user2', (SELECT id FROM Cards WHERE name = 'warriorCharge'), 1),
            ('user2', (SELECT id FROM Cards WHERE name = 'rogueShadowstrike'), 2),
            ('user2', (SELECT id FROM Cards WHERE name = 'nullRival'), 0),
            ('user2', (SELECT id FROM Cards WHERE name = 'queenCards'), 0),
            ('user2', (SELECT id FROM Cards WHERE name = 'reduceNextCardAcc'), 0),
            ('user3', (SELECT id FROM Cards WHERE name = 'heal'), 3),
            ('user3', (SELECT id FROM Cards WHERE name = 'warriorSlash'), 2),
            ('user3', (SELECT id FROM Cards WHERE name = 'powerUp'), 3),
            ('user3', (SELECT id FROM Cards WHERE name = 'holeReplace'), 2),
            ('user3', (SELECT id FROM Cards WHERE name = 'sorcererFireball'), 3),
            ('user3', (SELECT id FROM Cards WHERE name = 'reduceRound'), 2),
            ('user3', (SELECT id FROM Cards WHERE name = 'rogueStab'), 3),
            ('user3', (SELECT id FROM Cards WHERE name = 'steal'), 3),
            ('user3', (SELECT id FROM Cards WHERE name = 'rangerMultishot'), 2),
            ('user3', (SELECT id FROM Cards WHERE name = 'hideRivalCards'), 3),
            ('user4', (SELECT id FROM Cards WHERE name = 'shield'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'heal'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'powerUp'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'holeReplace'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'holeAmmend'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'reduceRound'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'steal'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rivalCardDowngrade'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'copyCard'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'hideRivalCards'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rangerShot'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'warriorCharge'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'sorcererFrost'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rogueShadowstrike'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rangerTrap'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'warriorSlash'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'sorcererFireball'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rogueStab'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'rangerMultishot'), 2),
            ('user4', (SELECT id FROM Cards WHERE name = 'warriorWhirlwind'), 2),
            ('user5', (SELECT id FROM Cards WHERE name = 'shield'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'heal'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'powerUp'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'holeReplace'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'holeAmmend'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'reduceRound'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'steal'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rivalCardDowngrade'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'copyCard'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'hideRivalCards'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rangerShot'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'warriorCharge'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'sorcererFrost'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rogueShadowstrike'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rangerTrap'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'warriorSlash'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'sorcererFireball'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rogueStab'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'rangerMultishot'), 3),
            ('user5', (SELECT id FROM Cards WHERE name = 'warriorWhirlwind'), 3);

            INSERT INTO MatchInfos (date, result, hostName, rivalName, rivalLevel, aftermath)
            VALUES
            ('2024-07-15', 'Win', 'user1', 'user2', '3', 'Improved team coordination'),
            ('2024-07-22', 'Loss', 'user3', 'user1', '5', 'Need to work on offense'),
            ('2024-08-01', 'Draw', 'user2', 'user4', '2', 'Good learning experience'),
            ('2024-08-10', 'Win', 'user1', 'user5', '6', 'Boosted confidence for upcoming matches'),
            ('2024-08-18', 'Loss', 'user4', 'user1', '4', 'Identified weaknesses in strategy'),
            ('2024-08-25', 'Win', 'user1', 'user3', '5', 'Secured top ranking'),
            ('2024-09-05', 'Draw', 'user5', 'user1', '3', 'Tested new formations effectively'),
            ('2024-09-12', 'Win', 'user1', 'user2', '1', 'Gave newer players more game time'),
            ('2024-09-20', 'Loss', 'user3', 'user1', '6', 'Need to improve defensive tactics'),
            ('2024-09-28', 'Win', 'user1', 'user4', '4', 'Strong performance to end the season');
            """;
    private final static String databasePath = "./database.db";

    public Database() {
        try {
            File dbFile = new File(databasePath);
            if (!dbFile.exists()) {
                System.out.println("Creating new database file...");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
                Statement statement = connection.createStatement();
                System.out.println("Executing initialization statement...");
                statement.executeUpdate(initializationStatement);
                System.out.println("Executing initial data filler...");
                statement.executeUpdate(dummyDatabaseFiller);
                statement.close();
                System.out.println("Database initialized successfully.");
            } else {
                System.out.println("Connecting to existing database...");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
                System.out.println("Connected successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error initializing database:");
            e.printStackTrace();
        }
    }

    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        Connection newConnection;
        try {
            newConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            return newConnection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}