import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class KeystrokeLoggerApp extends JFrame {
    private final JTextArea inputArea;
    private final JTextArea logArea;
    private final JLabel statusLabel;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton clearButton;
    private final JButton saveButton;
    private final KeystrokeLogger logger;
    public KeystrokeLoggerApp() {
        super("Keystroke Logger Prototype (Academic Demonstration)");
        logger = new KeystrokeLogger();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 560);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputArea = new JTextArea(5, 40);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Type demonstration text here (dummy text only)"
                )
        );
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(
                BorderFactory.createTitledBorder("Captured Key Log")
        );
        statusLabel = new JLabel("Status: OFF (not recording)");
        statusLabel.setFont(
                statusLabel.getFont().deriveFont(Font.BOLD)
        );
        statusLabel.setForeground(new Color(150, 0, 0));
        startButton = new JButton("Start Recording");
        stopButton = new JButton("Stop Recording");
        clearButton = new JButton("Clear Log");
        saveButton = new JButton("Save Log");
        stopButton.setEnabled(false);
        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        JPanel controlsPanel =
                new JPanel(new BorderLayout());
        controlsPanel.add(buttonPanel, BorderLayout.WEST);
        controlsPanel.add(statusLabel, BorderLayout.EAST);
        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        inputScroll,
                        logScroll
                );
        splitPane.setResizeWeight(0.35);
        root.add(splitPane, BorderLayout.CENTER);
        root.add(controlsPanel, BorderLayout.SOUTH);
        setContentPane(root);
        wireEvents();
    }
    private void wireEvents() {
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (logger.isRecording()) {
                    char c = e.getKeyChar();
                    if (c >= 32 && c != 127) {
                        logger.recordPrintableKey(c);
                        refreshLogDisplay();
                    }
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (!logger.isRecording()) {
                    return;
                }
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_BACK_SPACE:
                        logger.recordSpecialKey("[BACKSPACE]");
                        refreshLogDisplay();
                        break;
                    case KeyEvent.VK_ENTER:
                        logger.recordSpecialKey("[ENTER]");
                        refreshLogDisplay();
                        break;
                    case KeyEvent.VK_TAB:
                        logger.recordSpecialKey("[TAB]");
                        refreshLogDisplay();
                        e.consume();
                        break;
                    default:
                        break;
                }
            }
        });
        startButton.addActionListener(e -> {
            logger.startRecording();
            statusLabel.setText("Status: ON (recording)");
            statusLabel.setForeground(new Color(0, 120, 0));
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            inputArea.requestFocusInWindow();
        });
        stopButton.addActionListener(e -> {
            logger.stopRecording();
            statusLabel.setText("Status: OFF (not recording)");
            statusLabel.setForeground(new Color(150, 0, 0));
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });
        clearButton.addActionListener(e -> {
            logger.clearLog();
            refreshLogDisplay();
        });
        saveButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Log As");
            chooser.setSelectedFile(
                    new java.io.File("keystroke_log.csv")
            );
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file =
                        chooser.getSelectedFile();
                try {
                    LogFileWriter.writeCsv(
                            file,
                            logger.getEntries()
                    );
                    JOptionPane.showMessageDialog(
                            this,
                            "Log saved successfully to:\n"
                                    + file.getAbsolutePath(),
                            "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to save log:\n"
                                    + ex.getMessage(),
                            "Save Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }
    private void refreshLogDisplay() {
        logArea.setText(logger.getFormattedLog());
        logArea.setCaretPosition(
                logArea.getDocument().getLength()
        );
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KeystrokeLoggerApp app =
                    new KeystrokeLoggerApp();
            app.setVisible(true);
        });
    }
}