import javax.swing.*;
import java.awt.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class AllCollectionsPage extends CustomFrame implements ActionListener, TableModelListener {
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
    private String  tableName;

    public AllCollectionsPage() throws SQLException, IOException {
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
        allCollectionsList.setSelectedIndex(0);
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

        // Refresh button to load initial collections again manually.


        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBounds(400,107,100,26);
        refreshButton.addActionListener(e -> {
            try {
                populateDefaultListData();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });


        add(refreshButton);
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

            tableName = allCollectionsList.getSelectedValue();
            viewFrame.setTitle(tableName);

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
                    }
                    index++;
                }
                DefaultTableModel defaultTableModel = new DefaultTableModel(data , columnNames);
                JTable table = new JTable(defaultTableModel);
                table.getModel().addTableModelListener(this);

                JPanel buttonPanel = new JPanel(new FlowLayout());
                JButton addButton = new JButton("[+] ADD");
                JButton deleteButton = new JButton("[-] DELETE");
                deleteButton.addActionListener(e12 -> {
                    int option = JOptionPane.showConfirmDialog(null,"Are you sure ?", "Warning" , JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {

                    String id = String.valueOf(table.getModel().getValueAt(table.getSelectedRow(), 0 ));
                    databaseOperations.deleteRowFromTable(tableName , Integer.parseInt(id));
                        ((DefaultTableModel)table.getModel()).removeRow(table.getSelectedRow());
                    }
                });

                addButton.addActionListener(e1 -> {
                    ArrayList<JTextField> textFields = new ArrayList<>();
                    ArrayList<String> newValues = new ArrayList<>();
                    String[] labels;
                    int numPairs;
                    try {
                        numPairs = databaseOperations.tableColumns(tableName).size();
                        labels = new String[numPairs];
                        for (int r = 0 ; r<numPairs; r++){
                            labels[r] = databaseOperations.tableColumns(tableName).get(r);
                        }
                        GridLayout gridLayout = new GridLayout(0,2);
                        JPanel p = new JPanel(gridLayout);
                        for (int k = 0; k < numPairs; k++) {
                            JLabel l = new JLabel(labels[k], JLabel.CENTER);
                            if (!l.getText().equals("id")) {
                                p.add(l);
                                JTextField textField = new JTextField(10);
                                textFields.add(textField);
                                l.setLabelFor(textField);
                                p.add(textField);
                            }
                        }
                        int result = JOptionPane.showConfirmDialog(null , p , "New Item" , JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION){
                            for (JTextField t : textFields){
                                newValues.add(t.getText());
                            }
                            databaseOperations.addRow(tableName , newValues);
                            Vector<String> vector = new Vector<>();
                            ResultSet s = databaseOperations.lastRow(tableName);
                            try {
                                for(int a = 1 ; a <= numPairs; a++){
                                    vector.add(s.getString(a));
                                }
                            } catch (SQLException e2) {
                                e2.printStackTrace();
                            }
                            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                            tableModel.addRow(vector);
                        }

                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                });

                buttonPanel.add(addButton);
                buttonPanel.add(deleteButton);
                viewPanel.add(buttonPanel, BorderLayout.SOUTH);
                viewPanel.add(table.getTableHeader() , BorderLayout.PAGE_START);
                viewPanel.add(table , BorderLayout.CENTER);
                viewFrame.add(viewPanel);
                table.setToolTipText("You can edit the cells by double-clicking and enter to finish.");
                viewFrame.setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        else if(e.getSource().equals(editButton)){
            if(allCollectionsList.getSelectedIndex() != -1) {
                String clickedName = allCollectionsList.getSelectedValue();
                int clickedIndex = allCollectionsList.getSelectedIndex();
                String newName = JOptionPane.showInputDialog(getRootPane(),
                        "Please enter the new name:", "Edit Name", JOptionPane.INFORMATION_MESSAGE);
                if(!newName.equals("")) {
                    try {
                        databaseOperations.editTableName(clickedName, newName);
                        listModel.setElementAt(newName, clickedIndex);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    JOptionPane.showMessageDialog(super.rootPane, "Please give a new name." ,
                            "Error" , JOptionPane.WARNING_MESSAGE);
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
            //noinspection StatementWithEmptyBody
            if (result == JOptionPane.CANCEL_OPTION) {
                // Cancel
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
        listModel.clear();
        ResultSet resultSet = databaseOperations.allTables();
        for (String collection : dataList.fillCollections(resultSet)){
            if (!collection.contains("sqlite")){
                listModel.addElement(collection);
            }

        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        TableModel tempModel = (TableModel) e.getSource();
        int row = e.getFirstRow();
        int column = e.getColumn();

        if (row >= 0 && column >= 1){
            String id = String.valueOf(tempModel.getValueAt(row  , 0));
            String newValue = String.valueOf(tempModel.getValueAt(row,column));
            String columnName = tempModel.getColumnName(column);
            databaseOperations.editRowFromTable(tableName , Integer.parseInt(id),columnName,newValue);
        }else {
            JOptionPane.showMessageDialog(super.rootPane, "You can't change the id." ,
                    "Error" , JOptionPane.WARNING_MESSAGE);
        }
    }
}
