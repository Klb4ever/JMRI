// EnginesTableFrame.java

 package jmri.jmrit.operations.rollingstock.engines;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import jmri.jmrit.operations.OperationsFrame;
import jmri.jmrit.operations.locations.LocationManagerXml;
import jmri.jmrit.operations.rollingstock.cars.CarManagerXml;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.setup.Setup;

/**
 * Frame for adding and editing the engine roster for operations.
 *
 * @author		Bob Jacobsen   Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2008, 2011
 * @version             $Revision: 1.23 $
 */
public class EnginesTableFrame extends OperationsFrame implements PropertyChangeListener{
	
	static ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.operations.rollingstock.engines.JmritOperationsEnginesBundle");

	EnginesTableModel enginesModel = new EnginesTableModel();
	javax.swing.JTable enginesTable = new javax.swing.JTable(enginesModel);
	JScrollPane enginesPane;
	EngineManager engineManager = EngineManager.instance();
	
	// labels
	JLabel numEngines = new JLabel();
	JLabel textEngines = new JLabel();
	JLabel textSort = new JLabel(rb.getString("SortBy"));
	JLabel textSep1 = new JLabel("          ");
	JLabel textSep2 = new JLabel();
	
	// radio buttons	
    JRadioButton sortByNumber = new JRadioButton(rb.getString("Number"));
    JRadioButton sortByRoad = new JRadioButton(rb.getString("Road"));
    JRadioButton sortByModel = new JRadioButton(rb.getString("Model"));
    JRadioButton sortByConsist = new JRadioButton(rb.getString("Consist"));
    JRadioButton sortByLocation = new JRadioButton(rb.getString("Location"));
    JRadioButton sortByDestination = new JRadioButton(rb.getString("Destination"));
    JRadioButton sortByTrain = new JRadioButton(rb.getString("Train"));
    JRadioButton sortByMoves = new JRadioButton(rb.getString("Moves"));    
    JRadioButton sortByBuilt = new JRadioButton(rb.getString("Built"));
    JRadioButton sortByOwner = new JRadioButton(rb.getString("Owner"));
    JRadioButton sortByValue = new JRadioButton(rb.getString("Value"));
    JRadioButton sortByRfid = new JRadioButton(rb.getString("Rfid"));
    ButtonGroup group = new ButtonGroup();
    
	// major buttons
	JButton addButton = new JButton(rb.getString("Add"));
	JButton findButton = new JButton(rb.getString("Find"));
	JButton saveButton = new JButton(rb.getString("Save"));
	
	JTextField findEngineTextBox = new JTextField(6);

    public EnginesTableFrame() {
        super(ResourceBundle.getBundle("jmri.jmrit.operations.rollingstock.engines.JmritOperationsEnginesBundle").getString("TitleEnginesTable"));
        // general GUI config

        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

    	// Set up the jtable in a Scroll Pane..
    	enginesPane = new JScrollPane(enginesTable);
    	enginesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
       	enginesModel.initTable(enginesTable);
     	
       	// load the number of engines and listen for changes
     	numEngines.setText(Integer.toString(engineManager.getNumEntries()));
    	engineManager.addPropertyChangeListener(this);
    	textEngines.setText(rb.getString("engines"));

    	// Set up the control panel
    	
    	//row 1
    	JPanel cp1 = new JPanel();
    	
    	cp1.add(textSort);
    	cp1.add(sortByNumber);
    	cp1.add(sortByRoad);
    	cp1.add(sortByModel);
    	cp1.add(sortByConsist);
    	cp1.add(sortByLocation);
    	cp1.add(sortByDestination);
    	cp1.add(sortByTrain);
    	cp1.add(sortByMoves);
       	cp1.add(sortByBuilt);
    	cp1.add(sortByOwner);
    	if(Setup.isValueEnabled())
    		cp1.add(sortByValue);
       	if(Setup.isRfidEnabled())
    		cp1.add(sortByRfid);

       	// row 2
    	JPanel cp2 = new JPanel();
		findButton.setToolTipText(rb.getString("findEngine"));
		findEngineTextBox.setToolTipText(rb.getString("findEngine"));
		
    	cp2.add(numEngines);
    	cp2.add(textEngines);
    	cp2.add(textSep1); 	
		cp2.add (addButton);
		cp2.add(saveButton);
		cp2.add (findButton);
		cp2.add (findEngineTextBox);
				
		// place controls in scroll pane
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridBagLayout());
		addItem(controlPanel, cp1, 0, 0 );
		addItem(controlPanel, cp2, 0, 1);
		
	    JScrollPane controlPane = new JScrollPane(controlPanel);
	    // make sure panel doesn't get too short
	    controlPane.setMinimumSize(new Dimension(50,90));
	    controlPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
    	getContentPane().add(enginesPane);
	   	getContentPane().add(controlPane);
	   	
		// setup buttons
		addButtonAction(addButton);
		addButtonAction(findButton);
		addButtonAction(saveButton);
		
	   	sortByNumber.setSelected(true);
		addRadioButtonAction (sortByNumber);
		addRadioButtonAction (sortByRoad);
		addRadioButtonAction (sortByModel);
		addRadioButtonAction (sortByConsist);
		addRadioButtonAction (sortByLocation);
		addRadioButtonAction (sortByDestination);
		addRadioButtonAction (sortByTrain);
		addRadioButtonAction (sortByMoves);
		addRadioButtonAction (sortByBuilt);
		addRadioButtonAction (sortByOwner);
		addRadioButtonAction (sortByValue);
		addRadioButtonAction (sortByRfid);
		
		group.add(sortByNumber);
		group.add(sortByRoad);
		group.add(sortByModel);
		group.add(sortByConsist);
		group.add(sortByLocation);
		group.add(sortByDestination);
		group.add(sortByTrain);
		group.add(sortByMoves);
		group.add(sortByBuilt);
		group.add(sortByOwner);
		group.add(sortByValue);
		group.add(sortByRfid);
    	
 		// build menu
		JMenuBar menuBar = new JMenuBar();
		JMenu toolMenu = new JMenu(rb.getString("Tools"));
		toolMenu.add(new EngineRosterMenu("Roster", EngineRosterMenu.MAINMENU, this));
		toolMenu.add(new NceConsistEngineAction(rb.getString("MenuItemNceSync"), this));
		menuBar.add(toolMenu);
		setJMenuBar(menuBar);
    	addHelpMenu("package.jmri.jmrit.operations.Operations_Locomotives", true);
    	
    	pack();
    	/* all JMRI window position and size are now saved
       	setSize(engineManager.getEnginesFrameSize());
    	setLocation(engineManager.getEnginesFramePosition());
    	*/
    	setVisible(true);
    	
    	// also load the cars
    	CarManagerXml.instance();
    }
    
	public void radioButtonActionPerformed(java.awt.event.ActionEvent ae) {
		log.debug("radio button actived");
		if (ae.getSource() == sortByNumber){
			enginesModel.setSort(enginesModel.SORTBYNUMBER);
		}
		if (ae.getSource() == sortByRoad){
			enginesModel.setSort(enginesModel.SORTBYROAD);
		}
		if (ae.getSource() == sortByModel){
			enginesModel.setSort(enginesModel.SORTBYMODEL);
		}
		if (ae.getSource() == sortByConsist){
			enginesModel.setSort(enginesModel.SORTBYCONSIST);
		}
		if (ae.getSource() == sortByLocation){
			enginesModel.setSort(enginesModel.SORTBYLOCATION);
		}
		if (ae.getSource() == sortByDestination){
			enginesModel.setSort(enginesModel.SORTBYDESTINATION);
		}
		if (ae.getSource() == sortByTrain){
			enginesModel.setSort(enginesModel.SORTBYTRAIN);
		}
		if (ae.getSource() == sortByMoves){
			enginesModel.setSort(enginesModel.SORTBYMOVES);
		}
		if (ae.getSource() == sortByBuilt){
			enginesModel.setSort(enginesModel.SORTBYBUILT);
		}
		if (ae.getSource() == sortByOwner){
			enginesModel.setSort(enginesModel.SORTBYOWNER);
		}
		if (ae.getSource() == sortByValue){
			enginesModel.setSort(enginesModel.SORTBYVALUE);
		}
		if (ae.getSource() == sortByRfid){
			enginesModel.setSort(enginesModel.SORTBYRFID);
		}
	}
	
	public List<String> getSortByList(){
		return enginesModel.getSelectedEngineList();
	}
    
	EngineEditFrame f = null;
	
	// add, save or find button
	public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
//		log.debug("engine button activated");
		if (ae.getSource() == findButton){
			int rowindex = enginesModel.findEngineByRoadNumber(findEngineTextBox.getText());
			if (rowindex < 0){
				JOptionPane.showMessageDialog(this,
						MessageFormat.format(rb.getString("engineWithRoadNumNotFound"),new Object[]{findEngineTextBox.getText()}), rb.getString("engineCouldNotFind"),
						JOptionPane.INFORMATION_MESSAGE);
				return;
				
			}else{
				enginesTable.changeSelection(rowindex, 0, false, false);
			}
			return;
		}
		if (ae.getSource() == addButton){
			if (f != null)
				f.dispose();
			f = new EngineEditFrame();
			f.initComponents();
			f.setTitle(rb.getString("TitleEngineAdd"));
			f.setVisible(true);
		}
		if (ae.getSource() == saveButton){
			/* all JMRI window position and size are now saved
			engineManager.setEnginesFrame(this);
			*/
			engineManager.setEnginesFrameTableColumnWidths(getCurrentTableColumnWidths());
			LocationManagerXml.instance().writeFileIfDirty();	// could have created locations or tracks during import
			EngineManagerXml.instance().writeOperationsFile();
			if (Setup.isCloseWindowOnSaveEnabled())
				dispose();
		}
	}
	
	protected int[] getCurrentTableColumnWidths(){	
		TableColumnModel tcm = enginesTable.getColumnModel();
		int[] widths = new int[tcm.getColumnCount()];
		for (int i=0; i<tcm.getColumnCount(); i++)
			widths[i] = tcm.getColumn(i).getWidth();
		return widths;
	}

    public void dispose() {
    	engineManager.setEnginesFrameTableColumnWidths(getCurrentTableColumnWidths());
    	engineManager.removePropertyChangeListener(this);
    	enginesModel.dispose();
    	if (f != null)
    		f.dispose();
        super.dispose();
    }
    
    public void propertyChange(PropertyChangeEvent e) {
    	if(Control.showProperty && log.isDebugEnabled()) log.debug("Property change " +e.getPropertyName()+ " old: "+e.getOldValue()+ " new: "+e.getNewValue());
    	if (e.getPropertyName().equals(EngineManager.LISTLENGTH_CHANGED_PROPERTY)) {
    		numEngines.setText(Integer.toString(engineManager.getNumEntries()));
    	}
    }
    
	static org.apache.log4j.Logger log = org.apache.log4j.Logger
	.getLogger(EnginesTableFrame.class.getName());
}
