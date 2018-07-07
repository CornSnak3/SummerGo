package gui;

import core.Board;
import core.MoveHistory;
import core.exception.UnsupportedFileFormatException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import static java.lang.System.exit;

public class GUI extends JFrame {

    private JMenuBar jMenuBar;
    private JMenu jMenuGame;
    private JMenu jMenuHelp;
    private JMenu jMenuAbout;

    // Game Menu
    private JMenuItem jGameNew;
    private JMenuItem jGameSave;
    private JMenuItem jGameLoad;
    private JMenuItem jGameExit;

    private Board board;
    private boolean isGameGoing;

    private JButton[][] jIntersections;

    public GUI() {
        board = null;
        isGameGoing = false;
        initMenu();
        setJMenuBar(jMenuBar);
        initWindow();
        drawBoard();
        pack();
    }

    void initWindow() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Summer Go");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        setVisible(true);
    }

    void initMenu() {
        jMenuBar = new JMenuBar();

        jMenuGame = new JMenu("Game");
        jMenuHelp = new JMenu("Help");
        jMenuAbout = new JMenu("About");

        // Game menu
        jGameNew = new JMenuItem("New Game");
        jGameNew.setAccelerator(KeyStroke.getKeyStroke("control N"));

        jGameNew.addActionListener(actionEvent -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2));

            // Board size
            JLabel boardSizeLabel = new JLabel("Size: ", JLabel.CENTER);
            inputPanel.add(boardSizeLabel);
            JComboBox boardSizeComboBox = new JComboBox();
            boardSizeComboBox.addItem("9x9");
            boardSizeComboBox.addItem("13x13");
            boardSizeComboBox.addItem("19x19");
            inputPanel.add(boardSizeComboBox);

            // Komi
            JLabel komiLabel = new JLabel("Komi: ", JLabel.CENTER);
            inputPanel.add(komiLabel);
            JTextField komiSizeTextField = new JTextField();
            inputPanel.add(komiSizeTextField);

            int boardSizeChoose = JOptionPane.showConfirmDialog(this,
                    inputPanel,
                    "New game",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (boardSizeChoose == 1) {
                int boardSize = Integer.parseInt(((String) boardSizeComboBox.getSelectedItem()).split("x")[0]);
                double komi = Double.parseDouble(komiSizeTextField.getText());
                newGame(boardSize);
            }
        });

        jGameSave = new JMenuItem("Save Game");
        jGameSave.setAccelerator(KeyStroke.getKeyStroke("control S"));
        jGameSave.addActionListener(actionEvent -> {
            if (this.board == null)
                return;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a directory to save game: ");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    this.board.saveGame(file);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "Problem saving to file: </b>" + file.getName() + "</b>",
                            "Saving error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jGameLoad = new JMenuItem("Load Game");
        jGameLoad.setAccelerator(KeyStroke.getKeyStroke("control O"));
        jGameLoad.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose a file containing a game: ");
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    newGame(Board.loadGame(file));
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null,
                            "Can't open file <b>" + file.getName() + "</b>",
                            "Can't open file",
                            JOptionPane.ERROR_MESSAGE);
                } catch (UnsupportedFileFormatException exc) {
                    JOptionPane.showMessageDialog(null,
                            "File <b>" + file.getName() + "</b> has invalid format",
                            "Invalid file",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jGameExit = new JMenuItem("Exit");
        jGameExit.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        jGameExit.addActionListener(actionEvent -> {
            String[] options = { "Yes", "No" };
            int returnValue = JOptionPane.showOptionDialog(this,
                    "<html>Are you sure you want to exit?<br>All unsaved progress will be lost</html>",
                    "Are you sure?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[1]);
            if (returnValue == 0)
                System.exit(0);
        });

        jMenuGame.add(jGameNew);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameSave);
        jMenuGame.add(jGameLoad);
        jMenuGame.addSeparator();
        jMenuGame.add(jGameExit);

        // Help menu
        jMenuHelp.setMnemonic(KeyEvent.VK_F1);
        jMenuHelp.addActionListener(actionEvent -> {
            // TODO
        });

        // About menu
        jMenuAbout.addActionListener(actionEvent -> {
            // TODO
        });

        // Getting it all together
        jMenuBar.add(jMenuGame);
        jMenuBar.add(jMenuHelp);
        jMenuBar.add(jMenuAbout);
    }

    void drawBoard() {
        if (board == null)
            return;
        int boardSize = board.getBoardSize();

        JPanel jBoard = new JPanel(new GridLayout(boardSize, boardSize));
        jIntersections = new JButton[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                jIntersections[x][y] = new JButton(new ImageIcon("sprites/grid.png"));
                jIntersections[x][y].setEnabled(true);
                jIntersections[x][y].setBorder(BorderFactory.createEmptyBorder());
                jIntersections[x][y].setContentAreaFilled(false);
            }
        }
        getContentPane().add(jBoard, BorderLayout.CENTER);
    }

    void newGame(int boardSize) {
        double komi = 3.5;
        if (boardSize == 13) {
            komi += 2;
        } else if (boardSize == 19) {
            komi += 3;
        }

        this.board = new Board(boardSize, komi);
        drawBoard();
        revalidate();
        repaint();
    }

    void newGame(Board board) {
        this.board = board;
    }


}
