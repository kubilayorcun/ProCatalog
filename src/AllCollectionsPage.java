import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private DefaultListModel listModel;
    private JList allCollectionsList;
    private JButton searchButton;
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton addCollectionButton;
    public AllCollectionsPage() throws SQLException {
        super();
        setLayout(null);

        // Creating objects.
        allCollectionsArr = new ArrayList<>();
        dataList = new DataList();
        databaseOperations = new DatabaseOperations();

        // Creating list for all collections.
        listModel = new DefaultListModel();
        populateDefaultListData();
        allCollectionsList = new JList(listModel);
        JScrollPane scrollableList = new JScrollPane(allCollectionsList);
        scrollableList.setBounds(200 , 140 , 500, 260);

        // Creating search field for input.
        searchField = new JTextField();
        searchField.setBounds(200 , 60 , 400 , 40);

        // Creating search button for search invokation.
        searchButton= new JButton("Search");
        searchButton.setBounds(600,60,100,40);
        searchButton.addActionListener(this);

        // Creating info button for showing collection general info. e.g columns and table name.
        viewButton = new JButton("View");
        viewButton.setBounds(240 , 420 , 100,40);
        viewButton.addActionListener(this);

        // Creating edit button for editing collection info. e.g editing collection's name, adding new column, deleting column.
        editButton = new JButton("Edit");
        editButton.setBounds(400 , 420 , 100 , 40);
        editButton.addActionListener(this);

        // Creating delete button for deleting selected collection.
        deleteButton = new JButton("Delete");
        deleteButton.setBounds(560 , 420 , 100 , 40);
        deleteButton.addActionListener(this);

        // Creating add collection button for adding new collection.
        addCollectionButton = new JButton("+ Collection");
        addCollectionButton.setBounds(340 , 500  , 220 , 40);

        // Creating brand name label.
        JLabel projectLabel = new JLabel("P  R  O  C  A  T  A  L  O  G");
        projectLabel.setFont(new Font("Arial Black" , Font.PLAIN , 30));
        projectLabel.setBounds(237 , 5 , 500 , 50);

        add(projectLabel);
        add(viewButton);
        add(editButton);
        add(deleteButton);
        add(addCollectionButton);
        add(scrollableList);
        add(searchButton);
        add(searchField);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(searchButton)){
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
        else if(e.getSource().equals(viewButton)){
            JFrame viewFrame = new JFrame();
            JPanel viewPanel = new JPanel();
            viewFrame.setSize(500,500);
            viewPanel.setLayout(new BorderLayout());

            String tableName = allCollectionsList.getSelectedValue().toString();
            System.out.println(tableName);
            try {
                int columnCount = databaseOperations.tableColumns(tableName).size();
                int rowCount = databaseOperations.tableRowCount(tableName);

                String[] columnNames = new String[columnCount];
                int i = 0;
                for(String s : databaseOperations.tableColumns(tableName)){
                    columnNames[i] = s;
                    i++;
                }

                String[][] data = new String[rowCount][columnCount];
                ResultSet tableData = databaseOperations.selectFromTable(tableName);
                int index = 0;
                while(tableData.next()){

                    for (int j= 0 ; j < columnCount; j++){
                        data[index][j] = tableData.getString(j+1);
                        System.out.println(tableData.getString(j+1));
                    }
                    index++;
                }

                JTable table = new JTable(data , columnNames);




                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton addButton = new JButton("[+] ADD");
                JButton deleteButton = new JButton("[-] DELETE");
                buttonPanel.add(addButton);
                buttonPanel.add(deleteButton);
                viewPanel.add(buttonPanel, BorderLayout.SOUTH);
                viewPanel.add(table.getTableHeader() , BorderLayout.PAGE_START);
                viewPanel.add(table , BorderLayout.CENTER);
                viewFrame.add(viewPanel);
                viewFrame.setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        else if(e.getSource().equals(editButton)){
            // EDIT BUTTON ACTION
        }
        else if (e.getSource().equals(deleteButton)){
            // DELETE BUTTON ACTION
        }
        else if(e.getSource().equals(addCollectionButton)){
            // ADD COLLECTION ACTION
        }

    }
    public void populateDefaultListData() throws SQLException {
        ResultSet resultSet = databaseOperations.allTables();
        for (String collection : dataList.fillCollections(resultSet)){
            listModel.addElement(collection);
        }
    }

}
