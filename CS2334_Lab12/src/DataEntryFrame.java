import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
/**
 * Class to create a GUI for an example program to accept form submissions. The program keeps a list of several
 * forms that are being filled out. Each form holds some information about the user, such as their name, email,
 * and signature. The JComboBox at the top of the program is used to swap between forms that are being filled out.
 * Each form can be filled out separately, and information about each form is stored in a FormData object. These
 * objects are stored in a list and selected for editing with the ComboBox.
 *
 * The following is required of your program:
 * (1) Your frame layout should roughly match the layout demonstrated in lab and uploaded to canvas.
 * (2) You should be able to use the text fields to modify a FormData object. Pressing the "Save" button will
 *     attempt to set the values of the currently selected formdata (as corresponding to the currently selected
 *     index from the ComboBox). Use the values in the text fields to set the values of the formdata. Pressing "Reset"
 *     will clear all fields of the formdata. Pressing "New Form" will generate a new formdata for editing.
 * (3) You should be able to export all of your stored forms (i.e. export the datalist object).
 * (4) You should be abel to import a set of stored forms (i.e. import a list of FormData into the datalist object).
 * (5) You should not serialize the Social Security Numbers (see the FormData class).
 *
 * Note: the different forms are represented in the ComboBox by the display names.
 *
 * Follow the TODOs to complete your code.
 *
 * @author Stephen
 * @version 2019-04-24
 */
public class DataEntryFrame extends JFrame
{
	/**
	 * Users may fill out multiple forms at once. Only one form can be displayed at once, however.
	 * As such, users may cycle through this list to edit different forms.
	 */
	private ArrayList<FormData> datalist = new ArrayList<FormData>();
	private JComboBox<String> formSelect = new JComboBox<String>();

	/**
	 * Function used for refreshing the combo box contents. Populates the box with the display names.
	 */
	private DefaultComboBoxModel<String> getComboBoxModel(List<FormData> data)
	{
		ArrayList<String> displayNames = new ArrayList<String>();
		for (FormData form : data)
		{
			displayNames.add(form.getDisplayName());
		}
		String[] comboBoxModel = displayNames.toArray(new String[displayNames.size()]);
	    return new DefaultComboBoxModel<>(comboBoxModel);
	}

	/**
	 * Identifying Information:
	 */
	private JLabel firstNameInfo = new JLabel("First Name:");
	private JTextField firstName = new JTextField(15);
	private JLabel midddleInitialInfo = new JLabel("Middle Initial:");
	private JTextField middleInitial = new JTextField(1);
	private JLabel lastNameInfo = new JLabel("Last Name:");
	private JTextField lastName = new JTextField(15);
	private JLabel displayNameInfo = new JLabel("Display Name:");
	private JTextField displayName = new JTextField(15);
	private JLabel SSNInfo = new JLabel("Social Security Number:");
	private JTextField SSN = new JTextField(15);

	/**
	 * Contact information:
	 */
	private JLabel phoneInfo = new JLabel("Phone Number:");
	private JTextField phone = new JTextField(15);
	private JLabel emailInfo = new JLabel("Email Address:");
	private JTextField email = new JTextField(15);
	private JLabel addressInfo = new JLabel("Street Address:");
	private JTextField address = new JTextField(15);

	/**
	 * User verification:
	 */
	private JLabel signatureInfo = new JLabel("Signature:");
	private SignaturePanel spanel = new SignaturePanel();

	/**
	 * Translate stored form information into visual update...
	 */
	private void setVisuals(FormData data)
	{
		//gets the text stored for each field of the form and puts it into the correct field
		this.firstName.setText(data.getFirstName());
		this.middleInitial.setText(Character.toString(data.getMiddleInitial()));
		this.lastName.setText(data.getLastName());
		this.displayName.setText(data.getDisplayName());
		//this.SSN.setText(data.getSSN());
		this.phone.setText(data.getPhone());
		this.email.setText(data.getEmail());
		this.address.setText(data.getAddress());
		this.spanel.getSignature();

	}

	/**
	 * Error/confirmation message:
	 */
	private JTextField errorField = new JTextField("No Errors");

	@SuppressWarnings("unchecked")
	public DataEntryFrame()
	{
		this.setLayout(new GridLayout(7, 1));

		// Add initial form:
		datalist.add(new FormData());
		datalist.get(0).setValues("fn", 'm', "ln", "dn", "111111111", "1234567890",
				"test@ou.edu", "111 first st", new ArrayList<Point>());
		this.setVisuals(datalist.get(0));

		// Add in the form selector:
		DefaultComboBoxModel<String> comboBoxModel = getComboBoxModel(datalist);
		formSelect.setModel(comboBoxModel);
		formSelect.setSelectedIndex(0);
		formSelect.addActionListener((e) -> {
			int select = formSelect.getSelectedIndex();
			this.setVisuals(datalist.get(select));
		});
		this.add(formSelect);

		//makes panel for all the fillable fields and sets them into it
		JPanel formFill = new JPanel(new GridLayout(8,2));
		formFill.add(firstNameInfo);
		formFill.add(firstName);
		formFill.add(midddleInitialInfo);
		formFill.add(middleInitial);
		formFill.add(lastNameInfo);
		formFill.add(lastName);
		formFill.add(displayNameInfo);
		formFill.add(displayName);
		formFill.add(SSNInfo);
		formFill.add(SSN);
		formFill.add(phoneInfo);
		formFill.add(phone);
		formFill.add(emailInfo);
		formFill.add(email);
		formFill.add(addressInfo);
		formFill.add(address);
		
		
		
		this.add(formFill);

		// Add in the signature panel:
		spanel.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e) {}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				//adds the position of the mouse to the arraylist and paints it onto the signature panel
				spanel.addPoint(e.getPoint());
				spanel.repaint();
			}
			
		});
		this.add(signatureInfo);
		this.add(spanel);

		// Add in the form create, save, and reset panel:
		JPanel formHandling = new JPanel(new GridLayout(1, 3));
		JButton createForm = new JButton("New Form");
		createForm.addActionListener((e) -> {
			FormData newData = new FormData();
			newData.setValues("fn", 'm', "ln", "dn", "111111111", "1234567890",
					"test@ou.edu", "111 first st", new ArrayList<Point>());
			datalist.add(newData);
			int select = datalist.size() - 1;
			DefaultComboBoxModel<String> newComboBoxModel = getComboBoxModel(datalist);
			formSelect.setModel(newComboBoxModel);
			formSelect.setSelectedIndex(select);
			this.setVisuals(datalist.get(select));
		});

		JButton saveForm = new JButton("Save");
		saveForm.addActionListener((e) -> {
			int select = formSelect.getSelectedIndex();

			// use the JTextFields and the signature panel to set the values
			// of the selected FormData object.
			boolean changed = datalist.get(select).setValues(firstName.getText(), middleInitial.getText().toCharArray()[0], lastName.getText(), 
			displayName.getText(), SSN.getText(), phone.getText(), email.getText(), address.getText(), spanel.getSignature());

			this.setVisuals(datalist.get(select));
			DefaultComboBoxModel<String> newComboBoxModel = getComboBoxModel(datalist);
			formSelect.setModel(newComboBoxModel);
			formSelect.setSelectedIndex(select);

			//displays an error message if setting the values failed. Else, displays a success message.
			if(!changed) {
				errorField.setText("Values not set successfully");
			}
			else {
				errorField.setText("Values set successfully");
			}
		});

		JButton resetForm = new JButton("Reset");
		resetForm.addActionListener((e) -> {
			int select = formSelect.getSelectedIndex();
			boolean reset = datalist.get(select).setValues("", ' ', "", "", "", "", "", "", new ArrayList<Point>());
			this.setVisuals(datalist.get(select));
		});

		//adds buttons to panel and the panel to the frame
		formHandling.add(createForm);
		formHandling.add(saveForm);
		formHandling.add(resetForm);
		this.add(formHandling);

		// Add in the error message field:
		this.errorField.setEditable(false);
		//adds the error field to the frame
		this.add(errorField);

		// Add in the import/export panel:
		JButton importButton = new JButton("Import");

		importButton.addActionListener((e) -> {

			//gets the file to import from
			JFileChooser fileSelection = new JFileChooser(System.getProperty("user.dir"));
			fileSelection.showOpenDialog(null);
			fileSelection.setFileSelectionMode(JFileChooser.FILES_ONLY);
			File file = fileSelection.getSelectedFile();
			
			//tries to import data from the file and throws an exception if it fails
			try {
				//creates a file input stream to import data from the file
				FileInputStream fiStream = new FileInputStream(file);
				//creates an object input stream to convert data to an object
				ObjectInputStream oiStream = new ObjectInputStream(fiStream);
				//puts data from the file into the arraylist
				datalist = (ArrayList<FormData>) oiStream.readObject();
				oiStream.close();
			}
			//catches exceptions
			catch (Exception E) {
				System.out.println("Error importing data.");
			}
			
			System.out.println("Data imported successfully.");
			
			
            int select = 0;
			DefaultComboBoxModel<String> newComboBoxModel = getComboBoxModel(datalist);
			formSelect.setModel(newComboBoxModel);
			formSelect.setSelectedIndex(select);
			this.setVisuals(datalist.get(select));
			
		});
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener((e) -> {

			//creates a file chooser to allow the user to select the file to be exported to
			JFileChooser fileSelection = new JFileChooser(System.getProperty("user.dir"));
			fileSelection.showOpenDialog(null);
			fileSelection.setFileSelectionMode(JFileChooser.FILES_ONLY);
			File file = fileSelection.getSelectedFile();
			
			//tries to export the data and throws an exception if it cannot be found
			try {
				//creates a file output stream to write the data to the file
				FileOutputStream foStream = new FileOutputStream(file);
				//creates an object output stream to write the object to the file output stream
				ObjectOutputStream ooStream = new ObjectOutputStream(foStream);
				//writes the forms to the file
				ooStream.writeObject(datalist);
				ooStream.close();
			}
			//catches an IO Exception
			catch(IOException ioE) {
				System.out.println("Error exporting data.");
			}
			
			System.out.println("Data exported successfully.");
			
		});

		//creates panel for import and export buttons and adds it to the frame
		JPanel importExport = new JPanel(new GridLayout(1,2));
		importExport.add(importButton);
		importExport.add(exportButton);
		this.add(importExport);

		// JFrame basics:
		this.setTitle("Example Form Fillout");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 700);
		this.setVisible(true);
	}

	public static void main(String[] args)
	{
		new DataEntryFrame();
	}
}
