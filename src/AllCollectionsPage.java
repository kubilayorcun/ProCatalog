import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AllCollectionsPage extends CustomFrame implements ActionListener {
    private ArrayList<String> allCollectionsArr;
    private JTextField searchField;
    private DataList dataList;
    private DatabaseOperations databaseOperations;
    private DefaultListModel<String> listModel;

    public AllCollectionsPage() throws SQLException {
        super();
        setLayout(null);

        // Creating objects.
        allCollectionsArr = new ArrayList<>();
        dataList = new DataList();
        databaseOperations = new DatabaseOperations();

        // Creating list for all collections.
        listModel = new DefaultListModel<>();
        populateDefaultListData();
        JList<String> allCollectionsList = new JList<>(listModel);
        JScrollPane scrollableList = new JScrollPane(allCollectionsList);
        scrollableList.setBounds(200 , 140 , 500, 260);

        // Creating search field for input.
        searchField = new JTextField();
        searchField.setBounds(200 , 60 , 400 , 40);

        // Creating search button for search invocation.
        JButton searchButton = new JButton("Search");
        searchButton.setBounds(600,60,100,40);
        searchButton.addActionListener(this);

        add(scrollableList);
        add(searchButton);
        add(searchField);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String searchKey = searchField.getText();
        boolean isFound = false;
        if (searchKey.isEmpty()){
            JOptionPane.showMessageDialog(super.rootPane, "Search field is empty." , "Warning" , JOptionPane.WARNING_MESSAGE);
        }
        else{
            try {
                ResultSet allCollectionsResultSet = databaseOperations.allTables();
                allCollectionsArr = dataList.fillCollections(allCollectionsResultSet);
                listModel.clear();
                for (String collection :allCollectionsArr){
                    if (collection.contains(searchKey)){
                        listModel.addElement(collection);
                        isFound = true;
                    }
                }
                if (!isFound){
                    JOptionPane.showMessageDialog(getRootPane() , "There is no matching collection found." , "No Result" , JOptionPane.INFORMATION_MESSAGE);
                    populateDefaultListData();
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }
    }
    public void populateDefaultListData() throws SQLException {
        ResultSet resultSet = databaseOperations.allTables();
        for (String collection : dataList.fillCollections(resultSet)){
            listModel.addElement(collection);
        }
    }

}
