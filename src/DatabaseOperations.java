import java.sql.*;
import java.util.ArrayList;

public class DatabaseOperations {

    // Initialize global variables for global reference.
    private ResultSet selectResultSet;
    private Connection connection;
    private ResultSet searchResultSet;

    // Default constructor. Establish connection at the time of object declaration.
    public DatabaseOperations(){

        connectToDatabase();

    }


    // Creating the connection to database
    public void connectToDatabase(){

        String connectionUrl = "jdbc:sqlite:procatalog.db";

        try {
            connection = DriverManager.getConnection(connectionUrl);

        } catch (SQLException e) {
            e.printStackTrace();
        }


        // Dummy table creation for database functionality check.
        // Does not duplicate table at each connection establish, NOT EXISTS keyword.
        String createTable = "CREATE TABLE IF NOT EXISTS dummyTable (\n"
                + " id INTEGER PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " surname text NOT NULL\n"
                + ");";

        try {
            Statement createTableStt = connection.createStatement();
            createTableStt.execute(createTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Fetching passed parameter 'tableName' referenced table data to resultSet
    public ResultSet selectFromTable(String tableName){

        String selectQuery = "SELECT * FROM "+ tableName;

        try {
            Statement selectStatement = connection.createStatement();

            selectResultSet = selectStatement.executeQuery(selectQuery);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return selectResultSet;
    }

    // Delete data dependant on id(primary_key) attribute of specific row.
    public void deleteRowFromTable(String tableName, int id){

        String deleteQuery = "DELETE FROM " +tableName+ " WHERE id = " + id;

        try {

            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Deleting choosen column from table, will be added.
    public void deleteColumnFromTable(String tableName, String columnName){

    }


    // Update table's data. (In our case, updates a collection's item's attribute.)
    public void editDataFromTable(String tableName, int id, String columnChoice, String newValue){

        // Ex: UPDATE books SET pageCount = 400 WHERE id = 37; (So the books table's 37th id'ed book's pagecount attribute will be updated to 400.)
        String editDataQuery = "UPDATE " +tableName+ " SET " +columnChoice+ " = " +newValue+ " WHERE id = " +id;

        try {
            PreparedStatement editStatement = connection.prepareStatement(editDataQuery);
            editStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**  This search method was for searching items in a collection, so it is not relevant anymore. Will be deleted at further commits. */
    /*public ResultSet searchTable(String tableName, String attributeName, String searchKey){

        // Searches for the 'alike' versions of searchKey string.
        String searchQuery = "SELECT * FROM "+tableName+" WHERE "+attributeName+" LIKE '%"+searchKey+"%'";

        try {

            Statement searchStt = connection.createStatement();
            searchResultSet = searchStt.executeQuery(searchQuery);

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return searchResultSet;
    }*/


    // Fetches tablename column data from master table of out database which holds database infrastructure information.
    public ResultSet allTables() throws SQLException {
        String allCollectionsQuery = "SELECT name FROM sqlite_master WHERE type='table'";

        Statement selectAllStt = connection.createStatement();

        ResultSet allCollections = selectAllStt.executeQuery(allCollectionsQuery);

        return allCollections;
    }

    /** @param columnNames takes column names one by one from arrList and adds them in arraylist order to the table as columns.
     *  @param tableName takes table name and creates the table with specified name and a default primary key column.
     *  As a result collection will be created with desired columns and name. */
    public void addTable(String tableName , ArrayList<String> columnNames) throws SQLException {
        String createCollectionQ = "CREATE TABLE IF NOT EXISTS " + tableName + " (id integer primary key );";

        Statement createCollectionStt = connection.createStatement();
        createCollectionStt.execute(createCollectionQ);
        int i = 0;
        while(i < columnNames.size()){
            String addColumnQ = "ALTER TABLE "+tableName+" ADD COLUMN "+columnNames.get(i)+" text";
            PreparedStatement ps = connection.prepareStatement(addColumnQ);
            ps.executeUpdate();
            i++;
        }
    }


    /** @param tableName specifies table which is going to be updated with addition of a new column.
     *  @param columnName specifies column name that will be added to the table.
     *  As a result when invoked this method updates the table and adds a new column. */
    public void addColumnTable(String tableName, String columnName) throws SQLException {
        String addColumnQ = "ALTER TABLE "+tableName+" ADD COLUMN "+columnName+" text";

        Statement addColumnStt = connection.createStatement();

        addColumnStt.executeUpdate(addColumnQ);
    }

    // Change specified table's name by passing parameters as 'oldTableName' , 'newTableName'.
    public void editTableName(String oldTableName , String newTableName) throws SQLException {
        String alterTableQuery = "ALTER TABLE " +oldTableName+ " RENAME TO "+ newTableName;

        Statement alterStt = connection.createStatement();

        alterStt.executeUpdate(alterTableQuery);

    }

    // Delete specified table with respect to passed tableName param.
    public void deleteTable(String tableName) throws SQLException {

        String deleteTableQuery = "DROP TABLE IF EXISTS " + tableName;

        Statement deleteStt = connection.createStatement();

        deleteStt.executeUpdate(deleteTableQuery);

    }





}
