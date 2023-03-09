package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

public class SQLiteViewer extends JFrame {
    DefaultTableModel model = new DefaultTableModel();
    JTable Table=new JTable(model);
    public SQLiteViewer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setTitle("SQLite Viewer");
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        //add an JTextField
        JTextField FileNameTextField=new JTextField();
        FileNameTextField.setName("FileNameTextField");
        FileNameTextField.setBounds(2,10,600,30);
        add(FileNameTextField);

        //add an open file JButton=OpenFileButton
        JButton OpenFileButton=new JButton("Open");
        OpenFileButton.setName("OpenFileButton");
        OpenFileButton.setBounds(610,10,90,30);
        add(OpenFileButton);

        //Create A JComboBox=TablesComboBox
        JComboBox<String> TablesComboBox = new JComboBox<>();
        TablesComboBox.setName("TablesComboBox");
        TablesComboBox.setBounds(2,50,700,30);
        add(TablesComboBox);

        //Create JTextField=QueryTextArea
        JTextField QueryTextArea= new JTextField();
        QueryTextArea.setName("QueryTextArea");
        QueryTextArea.setBounds(2,90,600,90);
        add(QueryTextArea);

        //Action when the open button is pressed
        OpenFileButton.addActionListener(e -> {
                    String url = "jdbc:sqlite:" + FileNameTextField.getText();
                    SQLiteDataSource dataSource = new SQLiteDataSource();
                    dataSource.setUrl(url);
                    //check and make the connection
                    try (Connection con = dataSource.getConnection()) {
                        if (con.isValid(5)) {
                            System.out.println("Connection is valid.");
                            //Create the Java  Statement
                            Statement st=con.createStatement();
                            //Define the SELECT statement to get all the name of tables
                            String query = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%'";
                            //execute the Query
                            ResultSet rs=st.executeQuery(query);
                            // iterate through the java ResultSet
                            ArrayList<String>tables=new ArrayList<>();
                            while(rs.next()){
                                tables.add(rs.getString(1));
                            }
                            TablesComboBox.setModel(new DefaultComboBoxModel<>(tables.toArray(new String[1])));
                            String y= (String) TablesComboBox.getSelectedItem();
                            QueryTextArea.setText("SELECT * FROM "+y+";");
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }





        });

        //Action when JComboBox is changed
        TablesComboBox.addActionListener(e -> {
            String y= (String) TablesComboBox.getSelectedItem();
            QueryTextArea.setText("SELECT * FROM "+y+";");
        });



        this.Table.setName("Table");
        JScrollPane sp=new JScrollPane(this.Table);
        sp.setBounds(2,190,680,300);
        add(sp);







        //Create the Execute JButton
        JButton ExecuteQueryButton=new JButton("Execute");
        ExecuteQueryButton.setName("ExecuteQueryButton");
        ExecuteQueryButton.setBounds(610,95,80,50);
        add(ExecuteQueryButton);
        ExecuteQueryButton.addActionListener(e ->{
            String url = "jdbc:sqlite:" + FileNameTextField.getText();
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(url);
            //check and make the connection
            try (Connection con = dataSource.getConnection()) {
                if (con.isValid(5)) {
                    System.out.println("Connection is valid.");
                    //Create the Java  Statement
                    Statement st=con.createStatement();
                    //Define the SELECT statement to get all the name of tables
                    String query =QueryTextArea.getText();
                    //execute the Query
                    ResultSet rs=st.executeQuery(query);
                    // Add the column names to the model created
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        this.model.addColumn(metaData.getColumnLabel(i));
                    }

                    // Add the data to the model
                    while (rs.next()) {
                        Object[] rowData = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            rowData[i - 1] = rs.getObject(i);
                        }
                        this.model.addRow(rowData);
                    }
                    this.Table=new JTable(model);

                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        });


        setVisible(true);

    }
}
