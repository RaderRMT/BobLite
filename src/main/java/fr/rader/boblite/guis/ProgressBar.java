package fr.rader.boblite.guis;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class ProgressBar {

    private final JFrame frame;

    private JPanel panel;
    private JProgressBar progressBar;
    private JLabel actionLabel;

    public ProgressBar(int progressBarMaxValue) {
        // creating the frame
        frame = new JFrame("Bob");
        // setting a relatively good size
        frame.setSize(350, 100);
        // setting the frame's content
        frame.setContentPane(panel);
        // disabling the closing operation
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // moving the window to the center of the screen
        frame.setLocationRelativeTo(null);
        // disable resizing
        frame.setResizable(false);

        // setting the progress bar's max value
        this.progressBar.setMaximum(progressBarMaxValue);
    }

    public void show() {
        // show the frame to the user
        frame.setVisible(true);
    }

    public void kill() {
        // dispose of the frame
        frame.dispose();
    }

    public void setActionText(String action) {
        // changing the action label text.
        // this is the string above the progress bar
        this.actionLabel.setText(action);
    }

    public void setProgressBarValue(int value) {
        // changing the progress bar's value
        this.progressBar.setValue(value);

        // calculating the percentage.
        // one value has to be casted to a float because
        // both values are integers and the percentage won't
        // increase unless it's at 100%
        int percentage = (int) ((float) value / progressBar.getMaximum() * 100);
        // setting the progress bar's string to the percentage
        this.progressBar.setString(percentage + "%");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        actionLabel = new JLabel();
        actionLabel.setText("");
        panel.add(actionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        panel.add(progressBar, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
