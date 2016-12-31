package io.github.hexafraction.balance_checker;

import java.awt.*;
import com.google.common.base.Joiner;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.ParameterFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by Andrey Akhmetov on 6/26/2016.
 */
public class MainWindow {
    private JLabel donationsNotif;
    private TableModel dm;
    JFileChooser jfc = new JFileChooser(){
        @Override
        public void approveSelection(){
            if(getDialogType()!=SAVE_DIALOG) {
                super.approveSelection();
                return;
            };
            File f = getSelectedFile();
            if(f.exists() && getDialogType() == SAVE_DIALOG){
                int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                switch(result){
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.NO_OPTION:
                        return;
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.CANCEL_OPTION:
                        cancelSelection();
                        return;
                }
            }
            super.approveSelection();
        }
    };
    boolean outOfDate = false;
    public MainWindow() {
        initComponents();
		clearListButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dm.addresses.clear();
                dm.fireTableDataChanged();
                super.mouseClicked(e);
            }
        });
        loadFromFileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int rs = jfc.showOpenDialog(contentPane);
                if (rs == JFileChooser.APPROVE_OPTION) {
                    File f = jfc.getSelectedFile();
                    try {
                        List<String> lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
                        loadAddresses(lines.toArray(new String[0]));

                    } catch (MalformedInputException e1) {
                        JOptionPane.showMessageDialog(contentPane, "Error reading file. It's probably not a text file, or is corrupt.");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(contentPane, "Error reading file: " + e1.getClass() + ": " + e1.getMessage());
                    }
                }

            }
        });
        pasteAddressesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddressInput dialog = new AddressInput();
                dialog.pack();
                dialog.setModal(true);
                dialog.setVisible(true);
                String[] addrs = dialog.area.getText().split("[\n,\r\\W]");
                loadAddresses(addrs);
            }
        });
        getBalancesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getBalancesButton.setEnabled(false);
                pasteAddressesButton.setEnabled(false);
                loadFromFileButton.setEnabled(false);
                clearListButton.setEnabled(false);
                SwingWorker<Exception, Double> worker = new SwingWorker<Exception, Double>() {
                    int listPtr = 0;

                    @Override
                    protected Exception doInBackground() throws Exception {
                        for (int i = 0; i < dm.addresses.size(); i += 20) {
                            try {
                                doRequest(dm.addresses.subList(i, Math.min(i + 20, dm.addresses.size())));
                            } catch (Exception e) {
                                return e;
                            }
                        }
                        return null;
                    }

                    private void doRequest(List<Address> sublist) throws Exception {
                        String concatenated = Joiner.on(',').join(sublist);
                        //publish(Collections.nCopies(sublist.size(), 125.2).toArray(new Double[0]));

                        String response = RemoteData.readContents("https://btc.blockr.io/api/v1/address/balance/" + concatenated);
                        System.out.println("https://btc.blockr.io/api/v1/address/balance/" + concatenated);
                        System.out.println(response);
                        
                        ArrayList<Double>balances = new ArrayList<Double>();
                        int index = 0;
                        while ((index=response.indexOf("\"balance\":"))>=0)
                        {
                        	response = response.substring(index+10);

                        	balances.add(Double.parseDouble(response.substring(0, response.indexOf(","))));
                        	System.out.println(response.substring(0, response.indexOf(",")));
                        	System.out.println(Double.parseDouble(response.substring(0, response.indexOf(","))));
                        }
                        
                        Double[] pubval = new Double[balances.size()];
                        for (int i = 0; i < balances.size(); i++) {
                            pubval[i] = balances.get(i);
                        }
                        publish(pubval);
                    }

                    @Override
                    protected void process(List<Double> chunks) {
                        for (double d : chunks) {
                            Address addr = dm.addresses.get(listPtr);
                            addr.validBalance = true;
                            addr.balance = d;
                            System.out.println(d);
                            listPtr++;
                        }
                        dm.fireTableDataChanged();
                        super.process(chunks);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get() != null) {
                                JOptionPane.showMessageDialog(contentPane, "Error reading network: " + get().getClass() + ": " + get().getMessage());
                            }
                            System.out.println("No network error");
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        }
                        double total = 0;
                        for (Address a : dm.addresses) {
                            if (a.validBalance) total += a.balance;
                        }
                        ttl.setText("Total: " + total);
                        getBalancesButton.setEnabled(true);
                        pasteAddressesButton.setEnabled(true);
                        loadFromFileButton.setEnabled(true);
                        clearListButton.setEnabled(true);

                        outOfDate = false;
                    }
                };
                worker.execute();
            }
        });
        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(outOfDate){
                    int dialogResult = JOptionPane.showConfirmDialog(null, "The balances are out of date. Continue anyway?", "Balances out of date", JOptionPane.YES_NO_OPTION);
                    if(dialogResult!=JOptionPane.YES_OPTION){
                        return;
                    }
                }

                jfc.showSaveDialog(contentPane);
                List<String> lines = new ArrayList<String>(dm.addresses.size());

                try (PrintWriter pw = new PrintWriter(jfc.getSelectedFile())){
                    for(Address a : dm.addresses){
                        pw.println(a.addr+","+a.balance);
                    }
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(contentPane, "Error writing file: " + e1.getClass() + ": " + e1.getMessage());
                }
            }
        });
    }

    private void loadAddresses(String[] lines) {

        outOfDate = true;
        for (String l : lines) {
            String addr = l.replaceAll(",[\\d.]*$", "");
            addr = addr.replaceAll("[,\\W\\t\n\r]", "");
            if (addr.isEmpty()) continue;
            if (!addr.matches("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$")) {
                JOptionPane.showMessageDialog(contentPane, "Invalid address: " + addr);
                break;
            }
            Address a = new Address(addr);
            if (!dm.addresses.contains(a)) dm.addresses.add(new Address(addr));

        }
        ttl.setText("Amounts out of date. Click 'Get Balances' to get balances and calculate total.");
        dm.fireTableDataChanged();

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BTC balance checker");
        frame.setContentPane(new MainWindow().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {

        dm = new TableModel();
        tbl = new JTable(dm);
        tbl.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(10);
    }

    class TableModel extends AbstractTableModel {
        List<Address> addresses = new ArrayList<>();

        @Override
        public void fireTableDataChanged() {
            super.fireTableDataChanged();

        }

        @Override
        public int getRowCount() {
            return addresses.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return (column == 0) ? "Address" : "Balance";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) return addresses.get(rowIndex).addr;
            else return addresses.get(rowIndex).validBalance ? addresses.get(rowIndex).balance : null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == 0) ? String.class : Double.class;
        }
    }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - William Xu
		createUIComponents();

		contentPane = new JPanel();
		btnsPane = new JPanel();
		pasteAddressesButton = new JButton();
		loadFromFileButton = new JButton();
		clearListButton = new JButton();
		getBalancesButton = new JButton();
		exportButton = new JButton();
		ttl = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();

		//======== contentPane ========
		{

			// JFormDesigner evaluation mark
			contentPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), contentPane.getBorder())); contentPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			contentPane.setLayout(new BorderLayout());

			//======== btnsPane ========
			{
				btnsPane.setLayout(new FlowLayout());

				//---- pasteAddressesButton ----
				pasteAddressesButton.setLabel("Add addresses...");
				pasteAddressesButton.setText("Add addresses...");
				btnsPane.add(pasteAddressesButton);

				//---- loadFromFileButton ----
				loadFromFileButton.setText("Load from file...");
				btnsPane.add(loadFromFileButton);

				//---- clearListButton ----
				clearListButton.setText("Clear list");
				btnsPane.add(clearListButton);

				//---- getBalancesButton ----
				getBalancesButton.setText("Get balances!");
				btnsPane.add(getBalancesButton);

				//---- exportButton ----
				exportButton.setText("Export");
				btnsPane.add(exportButton);
			}
			contentPane.add(btnsPane, BorderLayout.NORTH);

			//---- ttl ----
			ttl.setFont(UIManager.getFont("TextArea.font"));
			ttl.setText("Total: 0");
			contentPane.add(ttl, BorderLayout.SOUTH);

			//======== scrollPane1 ========
			{

				//---- tbl ----
				tbl.setFillsViewportHeight(true);
				scrollPane1.setViewportView(tbl);
			}
			contentPane.add(scrollPane1, BorderLayout.CENTER);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - William Xu
	JPanel contentPane;
	private JPanel btnsPane;
	private JButton pasteAddressesButton;
	private JButton loadFromFileButton;
	private JButton clearListButton;
	private JButton getBalancesButton;
	private JButton exportButton;
	private JLabel ttl;
	private JTable tbl;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
