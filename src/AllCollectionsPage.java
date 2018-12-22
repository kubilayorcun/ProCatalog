import javax.swing.*;
import java.awt.Font;
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
    private JList<String> allCollectionsList;
    private DefaultListModel<String> listModel;
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
        listModel = new DefaultListModel<>();
        populateDefaultListData();
        allCollectionsList = new JList<>(listModel);
        JScrollPane scrollableList = new JScrollPane(allCollectionsList);
        scrollableList.setBounds(200 , 140 , 500, 260);

        // Creating search field for input.
        searchField = new JTextField();
        searchField.setBounds(200 , 60 , 400 , 40);

        // Creating search button for search invocation.
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
        addCollectionButton.addActionListener(this);

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
            // VIEW BUTTON ACTION
        }
        else if(e.getSource().equals(editButton)){
            if(allCollectionsList.getSelectedIndex() != -1) {
                String clickedName = allCollectionsList.getSelectedValue();
                int clickedIndex = allCollectionsList.getSelectedIndex();
                String newName = JOptionPane.showInputDialog(getRootPane(),
                        "Please enter the new name:", "Edit Name", JOptionPane.INFORMATION_MESSAGE);
                try {
                    databaseOperations.editTableName(clickedName, newName);
                    listModel.setElementAt(newName, clickedIndex);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(super.rootPane, "Please select a collection." ,
                        "Error" , JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getSource().equals(deleteButton)){
            if(allCollectionsList.getSelectedIndex() != -1) {
                int clickedIndex = allCollectionsList.getSelectedIndex();
                String clickedCollection = allCollectionsList.getSelectedValue();
                try {
                    databaseOperations.deleteTable(clickedCollection);
                    listModel.remove(clickedIndex);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(super.rootPane, "Please select a collection." ,
                        "Error" , JOptionPane.WARNING_MESSAGE);
            }
        }
        else if(e.getSource().equals(addCollectionButton)){
            JTextField nameField = new JTextField(5);
            JTextField numberField = new JTextField(5);

            JPanel creatorPanel = new JPanel();
            creatorPanel.add(new JLabel("Please enter the name of collection:"));
            creatorPanel.add(nameField);
            creatorPanel.add(new JLabel("Please enter the number of labels you want in collection:"));
            creatorPanel.add(numberField);

            int result = JOptionPane.showConfirmDialog(null, creatorPanel,
                    "Collection Creator", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.CANCEL_OPTION) {
                System.out.println("Exit");
            } else if(nameField.getText().isEmpty() || numberField.getText().isEmpty()){
                JOptionPane.showMessageDialog(super.rootPane, "Please fill all fields." ,
                        "Error" , JOptionPane.WARNING_MESSAGE);
            } else if(result == JOptionPane.OK_OPTION) {
                int labelNumber = Integer.parseInt(numberField.getText());
                ArrayList<String> labelArr = new ArrayList<>();
                JTextField[] labels = new JTextField[labelNumber];

                JPanel labelPanel = new JPanel();
                labelPanel.add(new JLabel("Please enter the name of labels:"));
                for (int i = 0; i < labelNumber; i++){
                    labels[i] = new JTextField(5);
                    labelPanel.add(labels[i]);
                }
                // Note: To be honest, I don't want to add more nested if statements so no more check.
                JOptionPane.showConfirmDialog(null, labelPanel,
                        "Collection Creator", JOptionPane.DEFAULT_OPTION);
                for (int i = 0; i < labelNumber; i++){
                    labelArr.add(labels[i].getText());
                }
                System.out.println(labelArr);
                try {
                    databaseOperations.addTable(nameField.getText(), labelArr);
                    listModel.addElement(nameField.getText());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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