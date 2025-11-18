package GUISystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SubInquiryPage extends JPanel {
    private JTextField inputField;
    private JButton searchButton;
    private JLabel label;

    public SubInquiryPage(String category) {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        label = new JLabel(category + " : ");
        inputField = new JTextField(15);
        searchButton = new JButton("조회");

        add(label);
        add(inputField);
        add(searchButton);
    }

    public String getInputText() {
        return inputField.getText();
    }

    // InquiryPage 쪽에서 리스너를 넘겨줄 거임
    public void setSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }
}
