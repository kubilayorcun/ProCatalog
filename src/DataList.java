import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataList {

    private static ArrayList<String> additionalLabels = new ArrayList<>();

    /**
     * This function will fill every result comes from CollectionAll
     * to an ArrayList.
     * ResultSet example:
     * ResultSet result = statement.executeQuery("SELECT name FROM CollectionAll");
     * This function can also be used for storing all items in the collectionA.
     *
     * @param result the result of the CollectionAll search
     * @return       the ArrayList contains every collection name
     */
    public ArrayList<String> fillCollections(ResultSet result) throws SQLException {
        ArrayList<String> collections = new ArrayList<>();
        while(result.next()) {
            String str = result.getString("name");
            // TODO: Delete before prod
            collections.add(str);
        }
        return(collections);
    }

    /**
     * This function will map every item label and its data to an
     * HashMap.
     * ResultSet example:
     * ResultSet result = statement.executeQuery("SELECT x, y, z FROM CollectionA");
     *
     * @param result the result of the CollectionA search
     * @param arr    the ArrayList that contains every column name in
     *               a collection. Ex: Collection A
     * @return       the HashMap contains every item couple.
     */
    public HashMap<String, String> fillLabels(ResultSet result, ArrayList arr) throws SQLException {
        HashMap<String, String> currCollection = new HashMap<>();
        while(result.next()) {
            for(Object obj : arr) {
                String str2 = result.getString((String) obj);
                // TODO: Delete before prod
                System.out.println(obj + str2);
                currCollection.put((String) obj, str2);
            }
        }
        return(currCollection);
    }

    public static void main(String[] args) {

        // Simulation without DB

        // fillCollections
        ArrayList<String> collections = new ArrayList<>();
        ArrayList<String> collectionA = new ArrayList<>();
        collections.add("CD");
        collections.add("DVD");
        collections.add("BD");
        System.out.println(collections);
        collectionA.add("CD1");
        collectionA.add("CD2");
        collectionA.add("CD3");
        System.out.println(collectionA);

        // fillLabels
        HashMap<String, String> CD1 = new HashMap<>();
        CD1.put("Name", "CD1");
        CD1.put("Date", "1997");
        System.out.println(CD1);

        // TODO: Create a simulation after DB is completed


    }
}